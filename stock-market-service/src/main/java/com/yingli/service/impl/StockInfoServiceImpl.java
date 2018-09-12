package com.yingli.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yingli.common.constant.CacheConsts;
import com.yingli.entity.StockInfo;
import com.yingli.dao.StockInfoDao;
import com.yingli.framework.redis.RedisCache;
import com.yingli.service.IStockInfoService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author chh2683@163.com
 * @since 2018-07-23
 */
@Service
public class StockInfoServiceImpl extends ServiceImpl<StockInfoDao, StockInfo> implements IStockInfoService {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 批量查询股票行情
     * @param stockCodeList
     * @return
     */
    @Override
    public List<JSONObject> batchGetStockMarket(List<Object> stockCodeList) {
        return redisCache.multGetMap(CacheConsts.REDIS_STOCK_MARKET_KEY, stockCodeList);
    }

    /**
     * 更新停复牌数据
     *
     * @param stockInfoList
     */
    @Override
    public void batchUpdateStockHaltResume(List<StockInfo> stockInfoList) {
        if (CollectionUtils.isNotEmpty(stockInfoList)) {
            List<Object[]> batckArgs = new ArrayList<>();
            for (StockInfo stockInfo : stockInfoList) {
                Object[] args = new Object[]{stockInfo.getTradingHaltTime(), stockInfo.getResumptionTime(), stockInfo.getHaltStatus(), stockInfo.getStockCode()};
                batckArgs.add(args);
            }
            jdbcTemplate.batchUpdate("update s_stock_info set trading_halt_time=?, resumption_time=?, halt_status=? where stock_code=?", batckArgs);
        }
    }
}
