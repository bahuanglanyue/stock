package com.yingli.service;

import com.alibaba.fastjson.JSONObject;
import com.yingli.entity.StockInfo;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chh2683@163.com
 * @since 2018-07-23
 */
public interface IStockInfoService extends IService<StockInfo> {

    /**
     * 批量查询股票行情
     * @param stockCodeList
     * @return
     */
    List <JSONObject> batchGetStockMarket(List<Object> stockCodeList);

    /**
     * 更新停复牌数据
     * @param stockInfoList
     */
    void batchUpdateStockHaltResume(List<StockInfo> stockInfoList);
}
