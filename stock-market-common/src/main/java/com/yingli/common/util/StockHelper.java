package com.yingli.common.util;

import com.yingli.common.constant.CommConsts;
import com.yingli.framework.exception.MessageException;

import java.util.regex.Pattern;

/**
 * 股票辅助类
 */
public class StockHelper {

    /**
     * 校验是否是两市A股
     * @param code
     * @return
     */
    public static boolean checkAStock(String code) {
        //若股票代码为空，则抛出异常
        if (code == null) { throw new MessageException("股票代码为必须参数，不允许为空！");}
        //1.验证股票是否为六位数字
        if (!Pattern.matches("\\d{6}", code)) {
            return false;
        }
        //2.若为00xxxx，则为深圳A股
        if (Pattern.matches("00\\d{4}", code)) {
            return true;
        }
        //3.若为200xxx，则为深圳B股
        if (Pattern.matches("200\\d{3}", code)) {
            return false;
        }
        //4.若为300xxx，则为深圳创业板
        if (Pattern.matches("300\\d{3}", code)) {
            return true;
        }
        //5.若为60xxxx，则为上证A股
        if (Pattern.matches("60\\d{4}", code)) {
            return true;
        }
        //6.若为900xxx，则为上证B股
        if (Pattern.matches("900\\d{3}", code)) {
            return false;
        }
        return false;
    }

    /**
     * 检查是否有效股票
     * @param code
     * @param market 市场代码，0->深圳，1->上海
     */
    public static boolean checkEffectStock(String code, short market) {
        //若股票代码为空，则抛出异常
        if (code == null) { throw new MessageException("股票代码为必须参数，不允许为空！");}
        //1.验证股票是否为六位数字
        if (!Pattern.matches("\\d{6}", code)) {
            return false;
        }
        //2.若为00xxxx，则为深圳A股
        if (Pattern.matches("00\\d{4}", code) && CommConsts.MARKET_SZ == market) {
            return true;
        }
        //3.若为200xxx，则为深圳B股
        if (Pattern.matches("200\\d{3}", code) && CommConsts.MARKET_SZ == market) {
            return false;
        }
        //4.若为300xxx，则为深圳创业板
        if (Pattern.matches("300\\d{3}", code) && CommConsts.MARKET_SZ == market) {
            return true;
        }
        //5.若为60xxxx，则为上证A股
        if (Pattern.matches("60\\d{4}", code) && CommConsts.MARKET_SH == market) {
            return true;
        }
        //6.若为900xxx，则为上证B股
        if (Pattern.matches("900\\d{3}", code) && CommConsts.MARKET_SH == market) {
            return false;
        }
        return false;
    }

    /**
     * 通过股票代码获取股票类型
     * @param stockCode         股票代码
     * @return                  股票类型（1：上证, 2：深证）
     */
    public static Integer getStockCategory(String stockCode) {
        //若股票代码为空，则抛出异常
        if (stockCode == null) { throw new MessageException("股票代码为必须参数，不允许为空！");}
        //1.验证股票是否为六位数字
        if (!Pattern.matches("\\d{6}", stockCode)) {
            return -1;//无效股票
        }
        //2.若为00xxxx，则为深圳A股
        if (Pattern.matches("00\\d{4}", stockCode)) {
            return CommConsts.STOCK_CATAGORY_SZ;
        }
        //3.若为200xxx，则为深圳B股
        if (Pattern.matches("200\\d{3}", stockCode)) {
            return CommConsts.STOCK_CATAGORY_SZ;
        }
        //4.若为300xxx，则为深圳创业板
        if (Pattern.matches("300\\d{3}", stockCode)) {
            return CommConsts.STOCK_CATAGORY_SZ;
        }
        //5.若为60xxxx，则为上证A股
        if (Pattern.matches("60\\d{4}", stockCode)) {
            return CommConsts.STOCK_CATAGORY_SH;
        }
        //6.若为900xxx，则为上证B股
        if (Pattern.matches("900\\d{3}", stockCode)) {
            return CommConsts.STOCK_CATAGORY_SH;
        }
        return -1;//无效股票
    }

    /**
     *
     * 市场代码，0->深圳，1->上海
     * @param stockCode
     * @return
     */
    public static byte getStockMarket(String stockCode) {
        if (getStockCategory(stockCode) == CommConsts.STOCK_CATAGORY_SZ) {
            return 0;
        }
        if (getStockCategory(stockCode) == CommConsts.STOCK_CATAGORY_SH) {
            return 1;
        }
        return -1;
    }

    /**
     * 校验是否为沪深B股
     * @param stockCode
     * @return
     */
    public static boolean checkBStock(String stockCode) {
        //若为200xxx，则为深圳B股
        if (Pattern.matches("200\\d{3}", stockCode)) {
            return true;
        }
        //若为900xxx，则为上证B股
        if (Pattern.matches("900\\d{3}", stockCode)) {
            return true;
        }
        return false;
    }
}
