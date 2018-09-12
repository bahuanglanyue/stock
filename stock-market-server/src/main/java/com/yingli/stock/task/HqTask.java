package com.yingli.stock.task;

import com.yingli.stock.server.StockHaltResumeService;
import com.yingli.stock.server.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 行情定时任务
 */
@Component("hqTask")
public class HqTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(HqTask.class);

    @Autowired
    private StockService stockService;

    @Autowired
    private StockHaltResumeService stockHaltResumeService;

    /**
     * 凌晨预设昨日收盘价
     */
    public void initPreClose() {
        LOGGER.info("*************************************开始执行预设昨日收盘价*************************************");
        long start = System.currentTimeMillis();
        stockService.initPreClose();
        long end = System.currentTimeMillis();
        LOGGER.info("*************************************执行预设昨日收盘价结束, 耗时{}秒*************************************\n", (end - start) / 1000);
    }

    /**
     * 初始化缓存及DB中股票
     */
    public void initCacheDbStock() {
        LOGGER.info("*************************************开始执行初始化缓存及DB中股票*************************************");
        long start = System.currentTimeMillis();
        stockService.initCacheDbStock();
        long end = System.currentTimeMillis();
        LOGGER.info("*************************************执行初始化缓存及DB中股票结束, 耗时{}秒*************************************\n", (end - start) / 1000);
    }

    /**
     * 实时行情刷新
     */
    public void doFlushStockHq() {
        LOGGER.info("*************************************开始执行行情刷新*************************************");
        long start = System.currentTimeMillis();
        stockService.flushStockHq();
        long end = System.currentTimeMillis();
        LOGGER.info("*************************************执行行情刷新结束, 耗时{}秒*************************************\n", (end - start) / 1000);
    }

    /**
     * 除权除息
     */
    public void executeDoStockXrDr() {
        LOGGER.info("*************************************除权除息数据扫描执行开始*************************************");
        long start = System.currentTimeMillis();
        stockService.doStockXrDr();
        long end = System.currentTimeMillis();
        LOGGER.info("*************************************除权除息数据扫描执行结束, 耗时{}秒*************************************\n", (end - start) / 1000);
    }

    /**
     * 停复牌股票扫描
     */
    public void doFlushHaltResumeStock() {
        LOGGER.info("*************************************停复牌数据扫描执行开始*************************************");
        long start = System.currentTimeMillis();
        int sum = stockHaltResumeService.doFlushHaltResumeStock();
        long end = System.currentTimeMillis();
        LOGGER.info("*************************************停复牌数据扫描执行结束, 扫描到{}只股票停复牌信息， 耗时{}秒*************************************\n", sum, (end - start) / 1000);
    }
}
