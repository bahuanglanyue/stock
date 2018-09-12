package com.yingli.stock.server;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.sun.jna.Native;
import com.sun.jna.ptr.ShortByReference;
import com.yingli.common.constant.CommConsts;
import com.yingli.common.util.*;
import com.yingli.config.SolrHelper;
import com.yingli.entity.StockInfo;
import com.yingli.framework.redis.RedisCache;
import com.yingli.service.IStockInfoService;
import com.yingli.stock.vo.StockSolr;
import com.yingli.stock.vo.StockVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * 股票行情服务
 */
@Component("stockService")
public class StockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockService.class);

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private HqServerConfig hqServerConfig;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private IStockInfoService stockInfoService;

    @Autowired
    private SolrHelper solrHelper;

    private static final String ECODING = "GBK";
    private static final String REDIS_STOCK_MARKET_KEY = "stockmarket";//redis行情key
    private static final String REDIS_STOCK_ORDER_KEY = "stockorder";//redis行情排序key
    private static final String TRADING_HALT_STOCK = "tradingHaltStock";//redis停牌股票
    private static final String TIME_FORMATE = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final int STOCK_UPDATE_TIME_SECOND = 3;//股票行情距离上次更新时间相差超出几秒，则再更新

    private static TdxHqLibrary tdxHqLibrary = null;
    private byte[] Result = new byte[65535];
    private byte[] ErrInfo = new byte[256];
    private static Map<String, Double> stockPriceMap = new ConcurrentHashMap<String, Double>();//缓存股票价格

    public StockService() {
        if (tdxHqLibrary == null) {
            tdxHqLibrary = (TdxHqLibrary) Native.loadLibrary("/dll/TradeX.dll", TdxHqLibrary.class);
        }
    }

    /**
     * 凌晨预设昨日收盘价
     */
    public void initPreClose() {
        Map<String, String> stockMap = redisCache.mget(REDIS_STOCK_MARKET_KEY, String.class);
        int n = 0;
        for (Map.Entry<String, String> entry : stockMap.entrySet()) {
            String stockCode = entry.getKey();
            JSONObject cacheStockJson = JSONObject.parseObject(entry.getValue());
            Double price = cacheStockJson.getDouble("price");
            if (NumUtil.sub(price, 0d) > 0) {
                Double preClose = price;
                cacheStockJson.put("preClose", preClose);
                Double riseLimit = null;
                Double fallLimit = null;
                String name = cacheStockJson.getString("name");
                if (name.indexOf("ST") != -1) {//ST股
                    riseLimit = NumUtil.mul(preClose, 1.05);
                    fallLimit = NumUtil.mul(preClose, 0.95);
                } else {
                    riseLimit = NumUtil.mul(preClose, 1.1);
                    fallLimit = NumUtil.mul(preClose, 0.9);
                }
                riseLimit = NumUtil.round(riseLimit, 2);
                cacheStockJson.put("riseLimit", riseLimit);//涨停价
                fallLimit = NumUtil.round(fallLimit, 2);
                cacheStockJson.put("fallLimit", fallLimit);//跌停价
                redisCache.addMap(REDIS_STOCK_MARKET_KEY, stockCode, cacheStockJson.toJSONString());
                n++;
            }
        }
        LOGGER.info("预设昨日收盘价，更新股票数->" + n);
    }


    /**
     * 连接行情服务器
     * @return
     */
    boolean connectHq() {
        boolean isConnected = false;
        for (int i = 0; i < hqServerConfig.getHqIp().size(); i++) {
            if (!NetUtil.isHostConnectable(hqServerConfig.getHqIp().get(i), hqServerConfig.getHqPort().get(i), 500)) {//检测连接
                continue;
            }
            isConnected = tdxHqLibrary.TdxHq_Connect(hqServerConfig.getHqIp().get(i), hqServerConfig.getHqPort().get(i), Result, ErrInfo);
            if (isConnected) {
                break;
            }
        }
        return isConnected;
    }

    /**
     * @return 接口中有效股票
     */
    private List<StockVo> getAllStockList() {
        ShortByReference getCount = new ShortByReference();
        /** 深圳股票 */
        List<StockVo> szStockInfoSet = new ArrayList<>();
        if (!tdxHqLibrary.TdxHq_GetSecurityList((byte)0, (short) 350, getCount, Result, ErrInfo)) {//350-1349 股票数：991
            LOGGER.error("获取深圳股票信息异常， 起始索引：{}，{}", 0, Native.toString(ErrInfo, ECODING));
        }
        String szTimeHq1 = Native.toString(Result, "GBK");
        resolvZq(szTimeHq1, szStockInfoSet, CommConsts.MARKET_SZ);

        if (!tdxHqLibrary.TdxHq_GetSecurityList((byte)0, (short) 1350, getCount, Result, ErrInfo)) {//1350-2349  股票数：361
            LOGGER.error("获取深圳股票信息异常， 起始索引：{}，{}", 1350, Native.toString(ErrInfo, ECODING));
        }
        String szTimeHq2 = Native.toString(Result, "GBK");
        resolvZq(szTimeHq2, szStockInfoSet, CommConsts.MARKET_SZ);

        if (!tdxHqLibrary.TdxHq_GetSecurityList((byte)0, (short) 6350, getCount, Result, ErrInfo)) {//6350-7349 股票数：759
            LOGGER.error("获取深圳股票信息异常， 起始索引：{}，{}", 6350, Native.toString(ErrInfo, ECODING));
        }
        String szTimeHq3 = Native.toString(Result, "GBK");
        resolvZq(szTimeHq3, szStockInfoSet, CommConsts.MARKET_SZ);

        if (!tdxHqLibrary.TdxHq_GetSecurityList((byte)0, (short) 7350, getCount, Result, ErrInfo)) {//6350-7349 股票数：
            LOGGER.error("获取深圳股票信息异常， 起始索引：{}，{}", 7350, Native.toString(ErrInfo, ECODING));
        }
        String szTimeHq4 = Native.toString(Result, "GBK");
        resolvZq(szTimeHq4, szStockInfoSet, CommConsts.MARKET_SZ);
        LOGGER.info("已获取深圳股票总数：{}", szStockInfoSet.size());

        /** 上海股票 */
        List<StockVo> shStockInfoSet = new ArrayList<>();
        if (!tdxHqLibrary.TdxHq_GetSecurityList((byte)1, (short) 0, getCount, Result, ErrInfo)) {//0-1000 股票数：237
            LOGGER.error("获取上海股票信息异常， 起始索引：{}，{}", 0, Native.toString(ErrInfo, ECODING));
        }
        String shTimeHq1 = Native.toString(Result, "GBK");
        resolvZq(shTimeHq1, shStockInfoSet, CommConsts.MARKET_SH);

        if (!tdxHqLibrary.TdxHq_GetSecurityList((byte)1, (short) 12000, getCount, Result, ErrInfo)) {//12000-12999 股票数：102
            LOGGER.error("获取上海股票信息异常， 起始索引：{}，{}", 12000, Native.toString(ErrInfo, ECODING));
        }
        String shTimeHq2 = Native.toString(Result, "GBK");
        resolvZq(shTimeHq2, shStockInfoSet, CommConsts.MARKET_SH);

        if (!tdxHqLibrary.TdxHq_GetSecurityList((byte)1, (short) 13000, getCount, Result, ErrInfo)) {//13000-13999 股票数：1000
            LOGGER.error("获取上海股票信息异常， 起始索引：{}，{}", 13000, Native.toString(ErrInfo, ECODING));
        }
        String shTimeHq3 = Native.toString(Result, "GBK");
        resolvZq(shTimeHq3, shStockInfoSet, CommConsts.MARKET_SH);

        if (!tdxHqLibrary.TdxHq_GetSecurityList((byte)1, (short) 14000, getCount, Result, ErrInfo)) {//14000-14999 股票数：330
            LOGGER.error("获取上海股票信息异常， 起始索引：{}，{}", 14000, Native.toString(ErrInfo, ECODING));
        }
        String shTimeHq4 = Native.toString(Result, "GBK");
        resolvZq(shTimeHq4, shStockInfoSet, CommConsts.MARKET_SH);

        if (!tdxHqLibrary.TdxHq_GetSecurityList((byte)1, (short) 15000, getCount, Result, ErrInfo)) {//15000-15999 股票数：9
            LOGGER.error("获取上海股票信息异常， 起始索引：{}，{}", 15000, Native.toString(ErrInfo, ECODING));
        }
        String shTimeHq5 = Native.toString(Result, "GBK");
        resolvZq(shTimeHq5, shStockInfoSet, CommConsts.MARKET_SH);
        LOGGER.info("已获取上海股票总数：{}", shStockInfoSet.size());

        List<StockVo> stockList = new ArrayList<>();
        stockList.addAll(szStockInfoSet);
        stockList.addAll(shStockInfoSet);
        return stockList;
    }

    /**
     * 解析股票并加入list
     * @param curTimeHq
     * @param list
     */
    private void resolvZq(String curTimeHq, List<StockVo> list, short market) {
        StringTokenizer st = new StringTokenizer(curTimeHq, "\n");
        st.nextToken();
        while (st.hasMoreTokens()) {
            String[] colList = st.nextToken().split("\t");
            String code = colList[0];
            String name = colList[2];
            String preCloseStr = colList[5].trim();
            if (StockHelper.checkEffectStock(code, market)) {
                StockVo stockVo = new StockVo();
                stockVo.setCode(code);
                stockVo.setName(name);
                stockVo.setMarket((int)market);
                if (StringUtils.isNotBlank(preCloseStr)) {
                    Double preClose = Double.valueOf(preCloseStr);
                    BigDecimal bd = new BigDecimal(preClose);
                    preClose = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    stockVo.setPreClose(preClose);
                }
                list.add(stockVo);
            }
        }
    }

    /**
     * 从db中获取当前时间
     * @return
     */
    private Timestamp getSysTimeFromDb() {
        Timestamp timestamp = jdbcTemplate.queryForObject("select sysdate()", new Object[]{}, Timestamp.class);
        return timestamp;
    }

    /**
     * 检查当前时间是否是交易日
     * @return
     */
    private boolean checkIsTradeDate(Timestamp now) {
        boolean isTradeDate = true;
        String holiday = DateUtil.formatDate(now, "yyyy-MM-dd");
        int holidayCount = jdbcTemplate.queryForObject("select count(*) from s_holiday_maintain where holiday=?", new Object[]{holiday}, Integer.class);
        if (holidayCount > 0) {//非节假日
            isTradeDate = false;
        }
        return isTradeDate;
    }

    /**
     * 初始化缓存及DB中股票
     */
    public void initCacheDbStock () {
        LOGGER.info("dll股票数据扫描开始------------------->start");
        long startTime = System.currentTimeMillis();
        if (!connectHq()) {
            LOGGER.error("连接异常，{}", Native.toString(ErrInfo, ECODING));
            return;
        }
        List<StockVo> stockList = getAllStockList();
        if (stockList == null || stockList.isEmpty()) {
            return;
        }
        Map<String, String> codeMap = new HashMap<>();
        Timestamp now = getSysTimeFromDb();
        if (now == null) {
            return;
        }
        if (!checkIsTradeDate(now)) {
            LOGGER.info("今天是非交易日，不执行行情数据初始化！");
            return;
        }
        for (int i = 0; i < stockList.size(); i++) {
            String updateTime = DateUtil.formatDate(now, TIME_FORMATE);
            long unix = now.getTime();
            StockVo hqStockVo = stockList.get(i);
            String cacheStockInfoStr = redisCache.getMapField(REDIS_STOCK_MARKET_KEY, hqStockVo.getCode(), String.class);
            if (cacheStockInfoStr == null) {//缓存没数据，直接初始化
                hqStockVo.setUpdateTime(updateTime);
                hqStockVo.setUnix(unix);
                hqStockVo.setMarket(null);
                hqStockVo.setPingyin(PinyinUtil.getPingYin(clearStockName(hqStockVo.getName())));
                hqStockVo.setPingyin_simple(PinyinUtil.getPinyinJC(clearStockName(hqStockVo.getName())));
                redisCache.addMap(REDIS_STOCK_MARKET_KEY, hqStockVo.getCode(), JSONObject.toJSONString(hqStockVo));
                redisCache.zaddEle(REDIS_STOCK_ORDER_KEY, hqStockVo.getCode(), Double.valueOf(now.getTime()));
            } else {
                JSONObject cacheStockJson = JSONObject.parseObject(cacheStockInfoStr);
                if (now.getTime() - cacheStockJson.getLong("unix") > STOCK_UPDATE_TIME_SECOND * 1000) {//如果更新时间大于3秒,更新market及order
                    cacheStockJson.put("name", hqStockVo.getName());
                    cacheStockJson.put("updateTime", updateTime);
                    cacheStockJson.put("unix", unix);
                    cacheStockJson.put("pingyin", PinyinUtil.getPingYin(clearStockName(hqStockVo.getName())));
                    cacheStockJson.put("pingyin_simple", PinyinUtil.getPinyinJC(clearStockName(hqStockVo.getName())));
                    redisCache.addMap(REDIS_STOCK_MARKET_KEY, hqStockVo.getCode(), cacheStockJson.toJSONString());
                    redisCache.zaddEle(REDIS_STOCK_ORDER_KEY, hqStockVo.getCode(), Double.valueOf(unix));
                } else {
                    LOGGER.debug("股票初始化距离上次更新时间太短，不用更新，code={}", hqStockVo.getCode());
                }
            }
            codeMap.put(hqStockVo.getCode(), hqStockVo.getName());
        }
        long endTime = System.currentTimeMillis();
        LOGGER.info("dll股票数据扫描结束,耗时：{}秒-------------------end<", (endTime - startTime) / 1000);

        LOGGER.info("mysql股票表数据扫描开始------------------->start");
        startTime = System.currentTimeMillis();
        scanDbStock(codeMap);
        endTime = System.currentTimeMillis();
        LOGGER.info("mysql股票数据扫描结束,耗时：{}秒-------------------<end", (endTime - startTime) / 1000);
    }

    /**
     * 扫描mysql表中股票信息并处理
     * @param codeMap
     */
    void scanDbStock(Map<String, String> codeMap) {
        try {
            List<Object[]> updateArgsList = new ArrayList<>();//待更新股票
            List<StockInfo> stockList = stockInfoService.selectList(new EntityWrapper<>());
            if (CollectionUtils.isNotEmpty(stockList)) {
                for (StockInfo stockInfo : stockList) {
                    String stock_code = stockInfo.getStockCode();
                    String stock_name = stockInfo.getStockName();
                    if (codeMap.containsKey(stock_code)) {//如果已经存在此股票
                        //检查股票名称是否变更
                        String newStockName = clearStockName(codeMap.get(stock_code));
                        if (!stock_name.equals(newStockName)) {
                            String pingyinJC = PinyinUtil.getPinyinJC(newStockName);
                            int isEnable = checkEnableStock(stock_code, codeMap.get(stock_code));
                            Object[] arg = new Object[]{newStockName, pingyinJC, isEnable, stock_code};
                            updateArgsList.add(arg);
                        }
                        codeMap.remove(stock_code);//移出已存在的股票，剩余即为待新增的股票
                    }
                }
            }

            /** 新增 */
            double max_buying_amount = 1000000;
            int is_follow_param = 1;
            List<Object[]> addArgsList = new ArrayList<>();
            for (Map.Entry<String, String> entry : codeMap.entrySet()) {
                String stockCode = entry.getKey();//证券代码
                String stockName = clearStockName(entry.getValue());
                String pingyinJC = PinyinUtil.getPinyinJC(stockName);//股票拼音
                String hy = null;//股票所属行业
                Integer category = StockHelper.getStockCategory(entry.getKey());//股票 1：上证 2：深证
                Integer isEnable = checkEnableStock(entry.getKey(), entry.getValue());//股票是否启用
                Object[] arg = new Object[]{stockCode, stockName, pingyinJC, hy, category, isEnable, max_buying_amount, is_follow_param};
                addArgsList.add(arg);
            }
            jdbcTemplate.batchUpdate("insert into s_stock_info (stock_code, stock_name, stock_pingying, stock_industry, stock_category, is_stock_enable, max_buying_amount, is_follow_param, created_time, updated_time )" +
                    " values(?, ?, ?, ?, ?, ?, ?, ?, sysdate(), sysdate())", addArgsList);
            LOGGER.info("新增股票: {}", JSONObject.toJSONString(addArgsList));

            /** 更新 */
            jdbcTemplate.batchUpdate("update s_stock_info set stock_name = ?, stock_pingying = ?, is_stock_enable = ?, updated_time = sysdate() where stock_code = ?", updateArgsList);
            LOGGER.info("更新股票: {}", JSONObject.toJSONString(updateArgsList));

        } catch (Exception e) {
            LOGGER.error("查询表s_stock_info异常: {}", e);
        }
    }

    /**
     * 根据股票名称返回股票是否启用 0:不启用，1：启用
     * @param stockCode
     * @param stockName
     * @return
     */
    private int checkEnableStock(String stockCode, String stockName) {
        if (StockHelper.checkBStock(stockCode)) {//B股暂禁止买卖
            return 0;
        }
        if (stockName.indexOf("ST") != -1) {
            return 0;
        }
        if (stockName.indexOf("N") != -1) {
            return 0;
        }
        if (stockName.trim().startsWith("*")) {
            return 0;
        }
        return 1;
    }

    /**
     * 去除股票名称中特殊字符
     * @param stockName
     * @return
     */
    private String clearStockName(String stockName) {
        return stockName.replaceAll(" ", "").replaceAll("\\*", "").replaceAll("Ａ", "A")
                .replaceAll("Ｂ", "B");
    }

    /**
     * 刷新行情
     */
    public void flushStockHq() {
        final int factors = 80;
        //从缓存中获取股票
        Set<ZSetOperations.TypedTuple<Object>> zSet = redisCache.getZSet(REDIS_STOCK_ORDER_KEY);
        Timestamp today = getSysTimeFromDb();
        if (today == null) {
            return;
        }
        if (!checkIsTradeDate(today)) {
            LOGGER.info("今天是非交易日，不执行行情数据刷新！");
            return;
        }
        Date now = new Date(today.getTime());
        String currentDate = DateUtil.formatDate(now);
        String currentTime = DateUtil.formatDate(now, "yyyy-MM-dd HH:mm:ss");
        Date start = DateUtil.getDateTime(currentDate.concat(" ").concat("09:00:00"), "yyyy-MM-dd HH:mm:ss");
        Date end = DateUtil.getDateTime(currentDate.concat(" ").concat("09:27:00"), "yyyy-MM-dd HH:mm:ss");
        if ((now.after(start) && now.before(end))) {
            LOGGER.info("9:00-9:27之间不刷新行情");
            return;
        }

        List<String> updateStockList = new ArrayList<>();
        for (ZSetOperations.TypedTuple tt : zSet) {
            String stockCode = (String)tt.getValue();
            long stockUpdateTime = new Double(tt.getScore()).longValue();
            if (today.getTime() - stockUpdateTime > STOCK_UPDATE_TIME_SECOND * 1000) {
                //待更新
                updateStockList.add(stockCode);
            } else {//无需更新(redis默认取出时从小到大，一旦有一个没超时，则后面股票都不用超时)
                break;
            }
        }
        Set<String> setList = ConcurrentHashMap.<String> newKeySet();//记录实际更新的股票
        Map<String, String> hqMap = new ConcurrentHashMap<>();
        List<Object[]> orderList = Collections.synchronizedList(new ArrayList<Object[]>());

        if (!connectHq()) {
            LOGGER.error("连接异常，{}", Native.toString(ErrInfo, ECODING));
            DingTalkUtil.sendMsgToDingTalk("行情服务", "行情服务连接异常->".concat(Native.toString(ErrInfo, ECODING)));
            return;
        }
        //循环获取五档行情次数
        int splitTime = (updateStockList.size() + factors - 1) / factors;
        final int num = 6;
        /** 采用并发方式获取五档行情，提高效率 */
        int groupNum = (splitTime + num - 1)/num;//线程数
        CountDownLatch counter = new CountDownLatch(groupNum);
        Map<String, String> tradingHaltStock = redisCache.mget(TRADING_HALT_STOCK, String.class);
        for (int g = 0; g < groupNum; g++) {
            final int g_cp = g;
            ThreadPoolHelper.execute(new Thread(() -> {
                try {
                    int splitStart = g_cp * num;
                    int splitEnd = (g_cp + 1) * num - 1;
                    if (splitEnd >= splitTime) {
                        splitEnd = splitTime - 1;
                    }
                    Timestamp nowTime = getSysTimeFromDb();
                    for (int i = splitStart; i <= splitEnd; i++) {
                        int start_index = i * factors;
                        int end_index = (i + 1) * factors - 1;
                        if (end_index >= updateStockList.size()) {
                            end_index = updateStockList.size() - 1;
                        }
                        byte[] marketArr = new byte[end_index - start_index + 1];
                        String[] zqdmArr = new String[end_index - start_index + 1];
                        List<String> zqdmList = Arrays.asList(zqdmArr);
                        for (int j = 0; j <= end_index - start_index; j++) {
                            String stockCode = updateStockList.get(start_index + j);
                            marketArr[j] = StockHelper.getStockMarket(stockCode);
                            zqdmArr[j] = stockCode;
                        }
                        ShortByReference count = new ShortByReference();
                        count.setValue(((short)marketArr.length));
                        if (!tdxHqLibrary.TdxHq_GetSecurityQuotes(marketArr, zqdmArr, count, Result, ErrInfo)) {
                            if (connectHq()) {//重连一次
                                if (!tdxHqLibrary.TdxHq_GetSecurityQuotes(marketArr, zqdmArr, count, Result, ErrInfo)) {
                                    LOGGER.error("获取五档行情异常", Native.toString(ErrInfo, ECODING));
                                    continue;
                                }
                            } else {
                                LOGGER.error("获取五档行情异常", Native.toString(ErrInfo, ECODING));
                                continue;
                            }
                        }
                        String wdhq = Native.toString(Result, ECODING);
                        StringTokenizer st = new StringTokenizer(wdhq, "\n");
                        st.nextToken();
                        while (st.hasMoreTokens()) {
                            String[] colList = st.nextToken().split("\t");
                            JSONObject json = new JSONObject(true);
                            String stockCode = colList[1].trim();
                            json.put("code", stockCode);
                            if (!zqdmList.contains(stockCode)) {
                                continue;
                            }
                            String cacheStock = redisCache.getMapField(REDIS_STOCK_MARKET_KEY, stockCode, String.class);
                            if (StringUtils.isBlank(cacheStock)) {
                                continue;
                            }
                            String name = JSONObject.parseObject(cacheStock).getString("name");
                            json.put("name", name);//股票名称
                            Double price = Double.valueOf(colList[3].trim());
                            BigDecimal bd = new BigDecimal(price);
                            price = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            if (NumUtil.sub(price, 0d) > 0) {//当前次扫描价格不为0
                                if (stockPriceMap.containsKey(stockCode)) {
                                    if (NumUtil.sub(stockPriceMap.get(stockCode), 0d) == 0) {//上次扫描时的价格为0
                                        LOGGER.info("股票:{},初始价格:{},时间:{}", new Object[]{stockCode, price, currentTime});
                                    }
                                }
                            }
                            stockPriceMap.put(stockCode, price);
                            json.put("price", price);//现价
                            String preCloseStr = colList[4].trim();
                            Double preClose = 0d;
                            if (StringUtils.isNotBlank(preCloseStr)) {
                                preClose = Double.valueOf(preCloseStr);
                                bd = new BigDecimal(preClose);
                                preClose = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            }
                            json.put("preClose", preClose);//昨日收盘价
                            Double open = Double.valueOf(colList[5].trim());
                            bd = new BigDecimal(open);
                            open = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            json.put("open", open);//开盘价
                            if (preClose == 0) {//过滤掉异常情况
                                LOGGER.debug("异常股票(昨日收盘价为0)--->" + stockCode);
                                continue;
                            }
                            if (tradingHaltStock.containsKey(stockCode)) {
                                String haltStock = tradingHaltStock.get(stockCode);
                                JSONObject haltStockJson = JSONObject.parseObject(haltStock);
                                int haltStatus = haltStockJson.getInteger("haltStatus");
                                if (haltStatus == 1) {
                                    json.put("haltStatus", true);//停牌
                                } else {
                                    json.put("haltStatus", false);//未停牌
                                }
                            } else {
                                json.put("haltStatus", false);//未停牌
                            }
                            Double riseLimit = null;
                            Double fallLimit = null;
                            if (name.indexOf("ST") != -1) {//ST股
                                riseLimit = NumUtil.mul(preClose, 1.05);
                                fallLimit = NumUtil.mul(preClose, 0.95);
                            } else {
                                riseLimit = NumUtil.mul(preClose, 1.1);
                                fallLimit = NumUtil.mul(preClose, 0.9);
                            }
                            riseLimit = NumUtil.round(riseLimit, 2);
                            json.put("riseLimit", riseLimit);//涨停价
                            fallLimit = NumUtil.round(fallLimit, 2);
                            json.put("fallLimit", fallLimit);//跌停价
                            Double b1_p = Double.valueOf(colList[17].trim());
                            bd = new BigDecimal(b1_p);//买一价
                            b1_p = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            json.put("b1_p", b1_p);
                            try {
                                Double a1_p = Double.valueOf(colList[18].trim());
                                bd = new BigDecimal(a1_p);
                                a1_p = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                json.put("a1_p", a1_p);//卖一价
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println(colList);
                            }

                            Long b1_v = Long.valueOf(colList[19].trim());
                            json.put("b1_v", b1_v);//买一量
                            Long a1_v = Long.valueOf(colList[20].trim());
                            json.put("a1_v", a1_v);//卖一量

                            Double b2_p = Double.valueOf(colList[21].trim());
                            bd = new BigDecimal(b2_p);
                            b2_p = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            json.put("b2_p", b2_p);//买二价
                            Double a2_p = Double.valueOf(colList[22].trim());
                            bd = new BigDecimal(a2_p);
                            a2_p = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            json.put("a2_p", a2_p);//卖二价
                            Long b2_v = Long.valueOf(colList[23].trim());
                            json.put("b2_v", b2_v);//买二量
                            Long a2_v = Long.valueOf(colList[24].trim());
                            json.put("a2_v", a2_v);//卖二量

                            Double b3_p = Double.valueOf(colList[25].trim());
                            bd = new BigDecimal(b3_p);
                            b3_p = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            json.put("b3_p", b3_p);//买三价
                            Double a3_p = Double.valueOf(colList[26].trim());
                            bd = new BigDecimal(a3_p);
                            a3_p = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            json.put("a3_p", a3_p);//卖三价
                            Long b3_v = Long.valueOf(colList[27].trim());
                            json.put("b3_v", b3_v);//买三量
                            Long a3_v = Long.valueOf(colList[28].trim());
                            json.put("a3_v", a3_v);//卖三量

                            Double b4_p = Double.valueOf(colList[29].trim());
                            bd = new BigDecimal(b4_p);
                            b4_p = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            json.put("b4_p", b4_p);//买四价
                            Double a4_p = Double.valueOf(colList[30].trim());
                            bd = new BigDecimal(a4_p);
                            a4_p = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            json.put("a4_p", a4_p);//卖四价
                            Long b4_v = Long.valueOf(colList[31].trim());
                            json.put("b4_v", b4_v);//买四价
                            Long a4_v = Long.valueOf(colList[32].trim());
                            json.put("a4_v", a4_v);//卖四量

                            Double b5_p = Double.valueOf(colList[33].trim());
                            bd = new BigDecimal(b5_p);
                            b5_p = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            json.put("b5_p", b5_p);//买五价
                            Double a5_p = Double.valueOf(colList[34].trim());
                            bd = new BigDecimal(a5_p);
                            a5_p = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            json.put("a5_p", a5_p);//卖五价
                            Long b5_v = Long.valueOf(colList[35].trim());
                            json.put("b5_v", b5_v);//买五量
                            Long a5_v = Long.valueOf(colList[36].trim());
                            json.put("a5_v", a5_v);//卖五量
                            //Date curTime = new java.util.Date();
                            json.put("unix", nowTime.getTime());//时间戳
                            String updateTime = DateUtil.formatDate(nowTime, TIME_FORMATE);
                            json.put("updateTime", updateTime);
                            json.put("pingyin", PinyinUtil.getPingYin(name));
                            json.put("pingyin_simple", PinyinUtil.getPinyinJC(clearStockName(name)));
                            //redisCache.addMap(REDIS_STOCK_MARKET_KEY, stockCode, json.toJSONString());
                            hqMap.put(stockCode, json.toJSONString());
                            //redisCache.zaddEle(REDIS_STOCK_ORDER_KEY, stockCode, Double.valueOf(today.getTime()));
                            orderList.add(new Object[]{stockCode, Double.valueOf(today.getTime())});
                            setList.add(stockCode);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("执行异常", e);
                }
                counter.countDown();
            }));
        }
        try {
            counter.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        redisCache.batchAddMap(REDIS_STOCK_MARKET_KEY, hqMap);
        redisCache.batchZaddEle(REDIS_STOCK_ORDER_KEY, orderList);

        try {
            tdxHqLibrary.TdxHq_Disconnect();
        } catch (Exception e) {
            //e.printStackTrace();
        }

        if (solrHelper.isSolrEnable()) {//启用solr
            ThreadPoolHelper.execute(new Thread(() -> {
                List<StockSolr> stockSolrList = new ArrayList<>();
                try {
                    hqMap.forEach((k, v) -> {
                        StockSolr stockSolr = new StockSolr();
                        stockSolr.setId(UUIDUtils.getRandomUUID());
                        stockSolr.setCreateTime(now);
                        StockVo stockVo = JSONObject.parseObject(v, StockVo.class);
                        BeanUtils.copyProperties(stockVo, stockSolr);
                        stockSolrList.add(stockSolr);
                    });
                    solrHelper.addStock(stockSolrList);
                } catch (Exception e) {
                    LOGGER.error("solr存储行情异常", e);
                }
            }));
        }

        LOGGER.info("需更新股票数：" + updateStockList.size() + "，实际更新股票数：" + setList.size());
    }

    /**
     * 执行除权除息数据扫描
     */
    public void doStockXrDr() {
        List<String> stockList = jdbcTemplate.queryForList("select stock_code from s_stock_info", null, String.class);

        if (!connectHq()) {
            LOGGER.error("连接异常，{}", Native.toString(ErrInfo, ECODING));
            return;
        }
        if (stockList == null || stockList.isEmpty()) {
            return;
        }

        try {
            Timestamp timestamp = getSysTimeFromDb();
            for (int i = 0; i < stockList.size(); i++) {
                String stock_code = stockList.get(i);
                if (!tdxHqLibrary.TdxHq_GetXDXRInfo(StockHelper.getStockMarket(stock_code), stock_code, Result, ErrInfo)) {
                    if (connectHq()) {
                        if (!tdxHqLibrary.TdxHq_GetXDXRInfo(StockHelper.getStockMarket(stock_code), stock_code, Result, ErrInfo)) {
                            LOGGER.error("获取除权除息异常，股票：{}，异常信息：{}", stock_code, Native.toString(ErrInfo, ECODING));
                            continue;
                        }
                    }
                }
                String cqcx = Native.toString(Result, ECODING);
                if (StringUtils.isBlank(cqcx)) {
                    return;
                }
                String newDate = null;//日期即为除权除息日
                Double newSxj = null;//送现金即每10股送息
                Double newSgs = null;//送股数即每10股送股数
                StringTokenizer st = new StringTokenizer(cqcx, "\n");
                st.nextToken();
                while (st.hasMoreTokens()) {
                    String[] colList = st.nextToken().split("\t");
                    //String code = colList[1].trim();//证券代码
                    String date = colList[2].trim();//日期即为除权除息日
                    int baoliu = Integer.valueOf(colList[3]);//保留列
                    Double sxj = Double.valueOf(colList[4].trim());//送现金
                    //Double pgj = Double.valueOf(colList[5].trim());//配股价
                    Double sgs = Double.valueOf(colList[6].trim());//送股数
                    if (1 == baoliu && (sgs > 0 || sxj > 0)) {//1为除权除息类型
                        if (StringUtils.isBlank(newDate)) {
                            newDate = date;
                            newSxj = sxj;
                            newSgs = sgs;
                        } else {
                            if (date.compareTo(newDate) > 0) {
                                newDate = date;
                                newSxj = sxj;
                                newSgs = sgs;
                            }
                        }
                    }
                }

                if (StringUtils.isNotBlank(newDate)) {
                    newDate = DateUtil.getFormatDateStr(newDate);
                    Date today = new Date(timestamp.getTime());
                    if (newDate.compareTo(DateUtil.formatDate(today, "yyyy-MM-dd")) >= 0) {
                        LOGGER.info("当前股票：{}，当前日期之后存在除权除息信息（除权除息日:{}，送股数（每10股）：{}，送现金（每10股）:{}）", new Object[]{stock_code, newDate, newSgs, newSxj});
                        Double ex_rights_stock_times = NumUtil.round(newSgs/10, 7);//除权增股倍数
                        Double without_dividend = NumUtil.round(newSxj/10, 7);//除息金额
                        java.sql.Date ex_dividend_day = new java.sql.Date(DateUtil.getDate(newDate).getTime());
                        Date lastChargeDate = getLastChargeTime(DateUtil.getDate(newDate));//除权登记日即除权除息日上一个交易日
                        Object[] arg = new Object[]{new java.sql.Date(lastChargeDate.getTime()), ex_dividend_day, ex_rights_stock_times, without_dividend, new java.sql.Timestamp(today.getTime()), stock_code};
                        jdbcTemplate.update("update s_stock_info set record_date=?, ex_dividend_day=?,ex_rights_stock_times=?,without_dividend=?,updated_time=? where stock_code=?", arg);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("除权除息扫描异常", e);
        }
    }

    /**
     * 获取上一个交易日
     * @param date
     * @return
     */
    public Date getLastChargeTime(Date date) {
        Date lastDate = date;
        String sql = "select count(*) from s_holiday_maintain where holiday=?";
        while (true) {
            lastDate = DateUtil.addDaysToDate(lastDate, -1);
            String holiday = DateUtil.formatDate(lastDate, "yyyy-MM-dd");
            int holidayCount = jdbcTemplate.queryForObject(sql, new Object[]{holiday}, Integer.class);
            if (holidayCount == 0) {//非节假日
                return lastDate;
            }
        }
    }

    /**
     * 删除B股
     */
    public void delStock() {
        jdbcTemplate.update("DELETE from s_stock_info where stock_code like '20%' or stock_code like '90%'");
        Set<ZSetOperations.TypedTuple<Object>> zSet = redisCache.getZSet(REDIS_STOCK_ORDER_KEY);
        int i = 0;
        for (ZSetOperations.TypedTuple tt : zSet) {
            String stockCode = (String)tt.getValue();
            if (StockHelper.checkBStock(stockCode)) {
                redisCache.delZSetField(REDIS_STOCK_ORDER_KEY, stockCode);
                redisCache.delMapField(REDIS_STOCK_MARKET_KEY, stockCode);
                i++;
            }
        }
        System.out.println("已删除(i=)" + i);
    }

}
