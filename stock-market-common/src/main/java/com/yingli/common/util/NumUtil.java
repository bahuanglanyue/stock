package com.yingli.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;

public class NumUtil {

    /**
     * 返回double数值整数部分
     * @param num
     * @return
     */
    public static int transDoubleToInt(double num) {
        DecimalFormat df = new DecimalFormat("0");
        return Integer.valueOf(df.format(num));
    }

    /**
     * 提供指定数值的（精确）小数位四舍五入处理。
     *
     * @param value 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static Double round(Double value, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        value = getDoubleValue(value);
        BigDecimal b = new BigDecimal(Double.toString(value));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Null to 0.0
     *
     * @param src
     * @return
     */
    public static Double getDoubleValue(Double src) {
        return src == null ? 0.0 : src;
    }

    public static int transDoubleToInt(String num) {
        return transDoubleToInt(Double.valueOf(num));
    }

    /**
     * 提供精确的减法运算。
     *
     * @param value1 被减数
     * @param value2 减数
     * @return 两个参数的差
     */
    public static double sub(Double value1, Double value2) {
        value1 = getDoubleValue(value1);
        value2 = getDoubleValue(value2);
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        return b1.subtract(b2).doubleValue();
    }

    public static byte[] double2Bytes(Double d) {
        long value = Double.doubleToRawLongBits(d);
        byte[] byteRet = new byte[8];
        for (int i = 0; i < 8; i++) {
            byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
        }
        return byteRet;
    }

    /**
     * 提供精确的乘法运算。
     * @param value1 被乘数
     * @param value2 乘数
     * @return 两个参数的积
     */
    public static Double mul(Double value1, Double value2) {
        value1 =getDoubleValue(value1);
        value2 =getDoubleValue(value2);
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        return b1.multiply(b2).doubleValue();
    }

    /*public static void sys(String[] args) {
        Double num = 22200.033322;
        System.out.println(transDoubleToInt(num));
    }*/

    /**
     * 多个double类型参数加法运算
     * @param vals
     * @return
     */
    public static Double add(Double...vals) {
        BigDecimal total = new BigDecimal(0);
        for (int i = 0; i < vals.length; i++) {
            BigDecimal bd = new BigDecimal(Double.toString(getDoubleValue(vals[i])));
            total = total.add(bd);
        }
        return total.doubleValue();
    }

    public static void main(String[] args) {
        java.sql.Date date = new java.sql.Date(1521542043000l);
        Timestamp timestamp = new Timestamp(1522813564000l);
        System.out.println(DateUtil.formatDate(timestamp, "yyyy-MM-dd HH:mm:ss"));
    }
}
