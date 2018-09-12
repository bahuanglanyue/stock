package com.yingli.stock.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yingli.common.util.DateUtil;
import com.yingli.common.util.HttpUtil;
import com.yingli.common.util.StockHelper;
import com.yingli.entity.StockInfo;
import com.yingli.framework.redis.RedisCache;
import com.yingli.service.IStockInfoService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 停复牌 service
 */
@Component("stockHaltResumeService")
public class StockHaltResumeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockHaltResumeService.class);

    private static final String TRADING_HALT_STOCK = "tradingHaltStock";//redis停牌股票

    private static final String TIME_FORMATE_MIS = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String TIME_FORMATE_MM_ = "yyyy-MM-dd HH:mm";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private IStockInfoService stockInfoService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 停复牌数据扫描
     */
    public Integer doFlushHaltResumeStock() {
        int num = 0;
        try {
            StringBuilder url = new StringBuilder();
            url.append("http://datainterface.eastmoney.com/EM_DataCenter/JS.aspx?type=FD&sty=SRB&st=0&sr=-1&p=1&ps=4000&js=var%20gvHOTBmE=");
            url.append(URLEncoder.encode("{pages:(pc),data:[(x)]}", "UTF-8"));
            url.append("&mkt=1&fd=");
            Date now = jdbcTemplate.queryForObject("select NOW()", null, java.util.Date.class);
            url.append(DateUtil.formatDate(now, null));
            String ret = HttpUtil.doGet(url.toString());
            List<StockInfo> stockInfoList = new ArrayList<>();
            if (StringUtils.isNotBlank(ret)) {
                String resData = ret.substring(ret.indexOf("gvHOTBmE=") + 9);
                JSONObject resJson = JSONObject.parseObject(resData);
                JSONArray dataArr = resJson.getJSONArray("data");
                for (int i = 0; i < dataArr.size(); i++) {
                    String stockStr = dataArr.getString(i);
                    if (StringUtils.isNotBlank(stockStr)) {
                        StockInfo stockInfo = new StockInfo();
                        String[] strArr = stockStr.split(",");
                        String stockCode = strArr[0].trim();//股票代码
                        if (!StockHelper.checkAStock(stockCode)) {//非两市A股
                            continue;
                        }
                        String tradingHaltTime = strArr[2].trim();//停牌时间（yyyy-MM-dd HH:mm）
                        String tradingHaltEndTime = strArr[3].trim();//停牌截止时间（yyyy-MM-dd HH:mm）
                        String resumptionTime = "";//预计复牌时间（yyyy-MM-dd）
                        if (strArr.length > 8) {
                            resumptionTime = strArr[8].trim();
                        }
                        stockInfo.setStockCode(stockCode);
                        Date tradingHaltTimeDate = DateUtil.getDateTime(tradingHaltTime, "yyyy-MM-dd HH:mm");
                        Date resumptionTimeDate = null;//复牌时间
                        stockInfo.setTradingHaltTime(tradingHaltTimeDate);//停牌时间
                        if (StringUtils.isNotBlank(resumptionTime)) {
                            resumptionTimeDate = DateUtil.getDateTime(resumptionTime + " 09:30", "yyyy-MM-dd HH:mm");
                            stockInfo.setResumptionTime(resumptionTimeDate);//预计复牌时间
                        }
                        Date tradingHaltEndTimeDate = null;
                        if (StringUtils.isNotBlank(tradingHaltEndTime)) {
                            tradingHaltEndTimeDate = DateUtil.getDateTime(tradingHaltEndTime + " ", "yyyy-MM-dd HH:mm");
                        }
                        int haltStatus;//停复牌状态
                        if (resumptionTimeDate != null && now.after(resumptionTimeDate)) {//已过复牌时间
                            haltStatus = 0;//未停牌
                        } else {
                            if (now.after(tradingHaltTimeDate)) {
                                if (StringUtils.isBlank(tradingHaltEndTime)) {
                                    haltStatus = 1;//已停牌
                                }  else {
                                    if (tradingHaltEndTimeDate != null) {
                                        if (now.before(tradingHaltEndTimeDate)) {
                                            haltStatus = 1;//已停牌
                                        } else {
                                            haltStatus = 0;//未停牌
                                        }
                                    } else {
                                        haltStatus = 1;//已停牌
                                    }
                                }
                            } else {//未过停牌时间
                                haltStatus = 0;//未停牌
                            }
                        }
                        stockInfo.setHaltStatus(haltStatus);
                        stockInfo.setUpdatedTime(now);

                        boolean needUpdate = false;
                        String tradingHaltStock = redisCache.getMapField(TRADING_HALT_STOCK, stockInfo.getStockCode(), String.class);
                        if (StringUtils.isNotBlank(tradingHaltStock)) {
                            JSONObject tradingHaltStockJSON = JSONObject.parseObject(tradingHaltStock);
                            String tradingHaltTimeCache = tradingHaltStockJSON.getString("tradingHaltTime");
                            String resumptionTimeCache = tradingHaltStockJSON.getString("resumptionTime");
                            int haltStatusCache = tradingHaltStockJSON.getIntValue("haltStatus");
                            if (!StringUtils.equals(tradingHaltTimeCache, tradingHaltTime)
                                    || !StringUtils.equals(resumptionTimeCache, resumptionTime)
                                    || !(haltStatusCache == haltStatus)) {
                                needUpdate = true;
                            }
                        } else {
                            needUpdate = true;
                        }

                        if (needUpdate) {
                            stockInfoList.add(stockInfo);
                            JSONObject json = new JSONObject(true);
                            json.put("stockCode", stockCode);
                            json.put("tradingHaltTime", tradingHaltTime);
                            json.put("resumptionTime", resumptionTime);
                            json.put("haltStatus", haltStatus);
                            String updateTime = DateUtil.formatDate(now, TIME_FORMATE_MIS);
                            json.put("updateTime", updateTime);
                            redisCache.addMap(TRADING_HALT_STOCK, stockInfo.getStockCode(), json.toJSONString());
                            LOGGER.info("更新停复牌股票(stockCode={},tradingHaltTime={},resumptionTime={},haltStatus={})",
                                    new Object[]{stockCode, tradingHaltTime, resumptionTime, haltStatus});
                            num++;
                        }
                    }
                }
                stockInfoService.batchUpdateStockHaltResume(stockInfoList);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return num;
    }
}
