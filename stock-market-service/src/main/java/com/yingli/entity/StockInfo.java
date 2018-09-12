package com.yingli.entity;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author chh2683@163.com
 * @since 2018-07-23
 */
@TableName("s_stock_info")
public class StockInfo extends Model<StockInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键【id】
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 股票代码 【stock_code】
     */
    @TableField("stock_code")
    private String stockCode;
    /**
     * 股票名称【stock_name】
     */
    @TableField("stock_name")
    private String stockName;
    /**
     * 股票拼音【stock_pingying】
     */
    @TableField("stock_pingying")
    private String stockPingying;
    /**
     * 股票所属行业【stock_industry】
     */
    @TableField("stock_industry")
    private String stockIndustry;
    /**
     * 上证|深证 股票【stock_category】1：上证 2：深证
     */
    @TableField("stock_category")
    private Integer stockCategory;
    /**
     * 股票是否启用【is_stock_enable】
     */
    @TableField("is_stock_enable")
    private Integer isStockEnable;
    /**
     * 停牌时间【trading_halt_time】
     */
    @TableField("trading_halt_time")
    private Date tradingHaltTime;
    /**
     * 复牌时间【resumption_time】
     */
    @TableField("resumption_time")
    private Date resumptionTime;
    /**
     * 停牌复牌状态【halt_status】
     */
    @TableField("halt_status")
    private Integer haltStatus;
    /**
     * 除权除息公告日【announcement_day】
     */
    @TableField("announcement_day")
    private Date announcementDay;
    /**
     * 股权登记日【record_date】
     */
    @TableField("record_date")
    private Date recordDate;
    /**
     * 除权除息日【ex_dividend_day】
     */
    @TableField("ex_dividend_day")
    private Date exDividendDay;
    /**
     * 除权增股倍数【ex rights_stock_times】
     */
    @TableField("ex_rights_stock_times")
    private Double exRightsStockTimes;
    /**
     * 除息金额【without_dividend】
     */
    @TableField("without_dividend")
    private Double withoutDividend;
    /**
     * 除权除息说明信息【description】
     */
    private String description;
    /**
     * 平台股票最大买入金额【max_buying_amount】
     */
    @TableField("max_buying_amount")
    private Double maxBuyingAmount;
    /**
     * 平台股票最大买入金额是否跟随系统参数【is_follow_param】
     */
    @TableField("is_follow_param")
    private Integer isFollowParam;
    /**
     * 备注【remark】
     */
    private String remark;
    /**
     * 创建时间【created_time】
     */
    @TableField("created_time")
    private Date createdTime;
    /**
     * 更新时间【updated_time】
     */
    @TableField("updated_time")
    private Date updatedTime;
    @TableField("deleted_at")
    private Date deletedAt;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getStockPingying() {
        return stockPingying;
    }

    public void setStockPingying(String stockPingying) {
        this.stockPingying = stockPingying;
    }

    public String getStockIndustry() {
        return stockIndustry;
    }

    public void setStockIndustry(String stockIndustry) {
        this.stockIndustry = stockIndustry;
    }

    public Integer getStockCategory() {
        return stockCategory;
    }

    public void setStockCategory(Integer stockCategory) {
        this.stockCategory = stockCategory;
    }

    public Integer getIsStockEnable() {
        return isStockEnable;
    }

    public void setIsStockEnable(Integer isStockEnable) {
        this.isStockEnable = isStockEnable;
    }

    public Date getTradingHaltTime() {
        return tradingHaltTime;
    }

    public void setTradingHaltTime(Date tradingHaltTime) {
        this.tradingHaltTime = tradingHaltTime;
    }

    public Date getResumptionTime() {
        return resumptionTime;
    }

    public void setResumptionTime(Date resumptionTime) {
        this.resumptionTime = resumptionTime;
    }

    public Integer getHaltStatus() {
        return haltStatus;
    }

    public void setHaltStatus(Integer haltStatus) {
        this.haltStatus = haltStatus;
    }

    public Date getAnnouncementDay() {
        return announcementDay;
    }

    public void setAnnouncementDay(Date announcementDay) {
        this.announcementDay = announcementDay;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public Date getExDividendDay() {
        return exDividendDay;
    }

    public void setExDividendDay(Date exDividendDay) {
        this.exDividendDay = exDividendDay;
    }

    public Double getExRightsStockTimes() {
        return exRightsStockTimes;
    }

    public void setExRightsStockTimes(Double exRightsStockTimes) {
        this.exRightsStockTimes = exRightsStockTimes;
    }

    public Double getWithoutDividend() {
        return withoutDividend;
    }

    public void setWithoutDividend(Double withoutDividend) {
        this.withoutDividend = withoutDividend;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getMaxBuyingAmount() {
        return maxBuyingAmount;
    }

    public void setMaxBuyingAmount(Double maxBuyingAmount) {
        this.maxBuyingAmount = maxBuyingAmount;
    }

    public Integer getIsFollowParam() {
        return isFollowParam;
    }

    public void setIsFollowParam(Integer isFollowParam) {
        this.isFollowParam = isFollowParam;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "StockInfo{" +
        "id=" + id +
        ", stockCode=" + stockCode +
        ", stockName=" + stockName +
        ", stockPingying=" + stockPingying +
        ", stockIndustry=" + stockIndustry +
        ", stockCategory=" + stockCategory +
        ", isStockEnable=" + isStockEnable +
        ", tradingHaltTime=" + tradingHaltTime +
        ", resumptionTime=" + resumptionTime +
        ", haltStatus=" + haltStatus +
        ", announcementDay=" + announcementDay +
        ", recordDate=" + recordDate +
        ", exDividendDay=" + exDividendDay +
        ", exRightsStockTimes=" + exRightsStockTimes +
        ", withoutDividend=" + withoutDividend +
        ", description=" + description +
        ", maxBuyingAmount=" + maxBuyingAmount +
        ", isFollowParam=" + isFollowParam +
        ", remark=" + remark +
        ", createdTime=" + createdTime +
        ", updatedTime=" + updatedTime +
        ", deletedAt=" + deletedAt +
        "}";
    }
}
