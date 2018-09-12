package com.yingli.stock.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yingli.framework.entity.ResultBean;
import com.yingli.service.IStockInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/market/1.0")
@Api(description = "行情相关API", produces = MediaType.APPLICATION_JSON_VALUE)
public class StockMarketController {

    @Autowired
    private IStockInfoService stockInfoService;

    @RequestMapping(value = "/queryStockMarket", method = RequestMethod.POST)
    @ApiOperation(value = "查询股票行情", notes = "参数为股票代码", httpMethod = "POST")
    public ResultBean<?> queryStockMarket(@ApiParam(name = "股票代码", value = "{stockCode:['000001','000002']}") @RequestBody JSONObject obj) {
        JSONArray jsonArray = obj.getJSONArray("stockCode");
        List<Object> stockCodeList = new ArrayList<>();
        if (jsonArray != null) {
            jsonArray.stream().parallel().forEach((Object stockCode) -> stockCodeList.add(stockCode));
        }
        List<JSONObject> list = stockInfoService.batchGetStockMarket(stockCodeList);
        return ResultBean.ok(list);
    }
}
