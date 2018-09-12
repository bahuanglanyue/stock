package com.yingli.stock.vo;

import java.io.Serializable;

/**
 * 股票信息
 */
public class StockVo implements Serializable {

    private String code;//证券代码
    private String name;//证券名称
    private Double price;//现价
    private Double open; //开盘价
    private Double preClose;//昨日收盘价
    private Double riseLimit;//涨停价
    private Double fallLimit;//跌停价
    private Boolean haltStatus;//是否停牌
    private String updateTime;
    private Long unix;//时间戳
    private Long b1_v;//买一量
    private Double b1_p;//买一价
    private Long b2_v;//买二量
    private Double b2_p;//买二价
    private Long b3_v;//买三量
    private Double b3_p;//买三价
    private Long b4_v;//买四量
    private Double b4_p;//买四价
    private Long b5_v;//买五量
    private Double b5_p;//买五价
    private Long a1_v;//卖一量
    private Double a1_p;//卖一价
    private Long a2_v;//卖二量
    private Double a2_p;//卖二价
    private Long a3_v;//卖三量
    private Double a3_p;//卖三价
    private Long a4_v;//卖四量
    private Double a4_p;//卖四价
    private Long a5_v;//卖五量
    private Double a5_p;//卖五价*/
    private String pingyin;//拼音全程
    private String pingyin_simple;//拼音简称

    private Integer market;//市场代码，0->深圳，1->上海

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockVo stockVo = (StockVo) o;
        return code != null ? code.equals(stockVo.code) : stockVo.code == null;
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getPreClose() {
        return preClose;
    }

    public void setPreClose(Double preClose) {
        this.preClose = preClose;
    }

    public Double getRiseLimit() {
        return riseLimit;
    }

    public void setRiseLimit(Double riseLimit) {
        this.riseLimit = riseLimit;
    }

    public Double getFallLimit() {
        return fallLimit;
    }

    public void setFallLimit(Double fallLimit) {
        this.fallLimit = fallLimit;
    }

    public Boolean isHaltStatus() {
        return haltStatus;
    }

    public void setHaltStatus(Boolean haltStatus) {
        this.haltStatus = haltStatus;
    }

    public Long getUnix() {
        return unix;
    }

    public void setUnix(Long unix) {
        this.unix = unix;
    }

    public Integer getMarket() {
        return market;
    }

    public void setMarket(Integer market) {
        this.market = market;
    }

    public Long getB1_v() {
        return b1_v;
    }

    public void setB1_v(Long b1_v) {
        this.b1_v = b1_v;
    }

    public Double getB1_p() {
        return b1_p;
    }

    public void setB1_p(Double b1_p) {
        this.b1_p = b1_p;
    }

    public Long getB2_v() {
        return b2_v;
    }

    public void setB2_v(Long b2_v) {
        this.b2_v = b2_v;
    }

    public Double getB2_p() {
        return b2_p;
    }

    public void setB2_p(Double b2_p) {
        this.b2_p = b2_p;
    }

    public Long getB3_v() {
        return b3_v;
    }

    public void setB3_v(Long b3_v) {
        this.b3_v = b3_v;
    }

    public Double getB3_p() {
        return b3_p;
    }

    public void setB3_p(Double b3_p) {
        this.b3_p = b3_p;
    }

    public Long getB4_v() {
        return b4_v;
    }

    public void setB4_v(Long b4_v) {
        this.b4_v = b4_v;
    }

    public Double getB4_p() {
        return b4_p;
    }

    public void setB4_p(Double b4_p) {
        this.b4_p = b4_p;
    }

    public Long getB5_v() {
        return b5_v;
    }

    public void setB5_v(Long b5_v) {
        this.b5_v = b5_v;
    }

    public Double getB5_p() {
        return b5_p;
    }

    public void setB5_p(Double b5_p) {
        this.b5_p = b5_p;
    }

    public Long getA1_v() {
        return a1_v;
    }

    public void setA1_v(Long a1_v) {
        this.a1_v = a1_v;
    }

    public Double getA1_p() {
        return a1_p;
    }

    public void setA1_p(Double a1_p) {
        this.a1_p = a1_p;
    }

    public Long getA2_v() {
        return a2_v;
    }

    public void setA2_v(Long a2_v) {
        this.a2_v = a2_v;
    }

    public Double getA2_p() {
        return a2_p;
    }

    public void setA2_p(Double a2_p) {
        this.a2_p = a2_p;
    }

    public Long getA3_v() {
        return a3_v;
    }

    public void setA3_v(Long a3_v) {
        this.a3_v = a3_v;
    }

    public Double getA3_p() {
        return a3_p;
    }

    public void setA3_p(Double a3_p) {
        this.a3_p = a3_p;
    }

    public Long getA4_v() {
        return a4_v;
    }

    public void setA4_v(Long a4_v) {
        this.a4_v = a4_v;
    }

    public Double getA4_p() {
        return a4_p;
    }

    public void setA4_p(Double a4_p) {
        this.a4_p = a4_p;
    }

    public Long getA5_v() {
        return a5_v;
    }

    public void setA5_v(Long a5_v) {
        this.a5_v = a5_v;
    }

    public Double getA5_p() {
        return a5_p;
    }

    public void setA5_p(Double a5_p) {
        this.a5_p = a5_p;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getPingyin() {
        return pingyin;
    }

    public void setPingyin(String pingyin) {
        this.pingyin = pingyin;
    }

    public String getPingyin_simple() {
        return pingyin_simple;
    }

    public void setPingyin_simple(String pingyin_simple) {
        this.pingyin_simple = pingyin_simple;
    }
}
