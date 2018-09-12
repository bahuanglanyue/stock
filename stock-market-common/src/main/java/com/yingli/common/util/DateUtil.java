package com.yingli.common.util;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    /**
     * 返回yyyy-mm-dd HH:MM:ss 组合时间字符串
     * @param date 日期 20171025
     * @param time 时间 93339 or 103028
     * @return
     */
    public static String getFormatConnDateStr(String date, String time) {
        StringBuilder sb = new StringBuilder();
        sb.append(getFormatDateStr(date));
        sb.append(" ");
        sb.append(getFormatTimeStr(time));
        return sb.toString();
    }

    /**
     * 返回 yyyy-mm-dd 日期
     * @param date 日期 20171025
     * @return
     */
    public static String getFormatDateStr(String date) {
        StringBuilder sb = new StringBuilder();
        sb.append(date.substring(0, 4)).append("-").append(date.substring(4, 6)).append("-").append(date.substring(6));
        return sb.toString();
    }

    /**
     * 返回 HH:MM:ss格式时间
     * @param time 93339 or 103028
     * @return
     */
    public static String getFormatTimeStr(String time) {
        if (StringUtils.isBlank(time)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6 - time.length(); i++) {
            time = "0" + time;
        }
        sb.append(time.substring(0, 2)).append(":").append(time.substring(2, 4)).append(":").append(time.substring(4));
        return sb.toString();
    }

    /**
     * 格式化日期
     *
     * @param value
     *            date
     * @param pattern
     *            default"yyyy-MM-dd"
     * @return date string
     */
    public static String formatDate(java.util.Date value, String pattern) {
        String pat = "yyyy-MM-dd";
        if (pattern != null) {
            pat = pattern;
        }
        String result = "";
        if (value != null) {
            SimpleDateFormat htmlDf = new SimpleDateFormat(pat);
            result = htmlDf.format(value).toString();
        }
        return result;
    }

    /**
     * 格式化日期yyyy-MM-dd
     *
     * @param dt
     * @return String
     */
    public static String formatDate(Date dt) {
        return formatDate(dt, "yyyy-MM-dd");

    }

    /**
     * 时间首位补零 9:46:55返回09:46:55， 10:05:42返回10:05:42
     * @param time
     * @return
     */
    public static String getTimeAddZero(String time) {
        if (StringUtils.isBlank(time)) {
            return time;
        }
        if (time.length() == 7) {
            return new StringBuilder("0").append(time).toString();
        }
        return time;
    }

    /**
     * @param date
     * @return Date yyyy-MM-dd HH:mm:ss
     * @throws Exception
     */
    public static Date getDateTime(String date) {
        return getDateTime(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date getDateTime(String dateTime, String pattern) {
        if (org.springframework.util.StringUtils.isEmpty(pattern)) {
            pattern = "yyyy-MM-dd";
        }
        if (org.springframework.util.StringUtils.isEmpty(dateTime)) {
            return null;
        }
        DateFormat myDateFormat = new SimpleDateFormat(pattern);
        try {
            return myDateFormat.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param date
     * @return Date
     * @throws Exception
     */
    public static Date getDate(String date) {
        if (org.springframework.util.StringUtils.isEmpty(date)) {
            return null;
        }
        DateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (date.length() == 16) {
            myDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
        if (date.length() == 19) {
            myDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        if (date.length() == 13) {
            myDateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
        }
        try {
            return myDateFormat.parse(date);
        } catch (ParseException e) {
           e.printStackTrace();
            return null;
        }
    }

    /**
     * 给指定日期加上天数
     * @param days
     * @return Date
     */
    public static Date addDaysToDate(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return c.getTime();
    }

    public static String resolveSpecTimeStr(String time) {
        if (time.indexOf(":") != -1) {
            return getTimeAddZero(time);
        } else {
            return getFormatTimeStr(time);
        }
    }

    public static void main(String[] args) {
        /*System.out.println(getFormatConnDateStr("20171025", "103028"));
        System.out.println(getTimeAddZero("9:05:42"));*/
        System.out.println(getDate("2016-09-13"));

        /*String stockCode;
        String stockName;
        int stockQuantity;
        int soldQuantity;
        Double accountMarketValue = 0d;//证券账户市值
        Double accountProfit = 0d;//浮动盈亏
        String str = "  95    深圳A   300377   赢时胜    16300    16300           15.369  15.410  251183.000     668.300      0.27 ";
        String arr[] = str.split("         +");
        String firstData = arr[0];
        String secondData = arr[1];
        String[] firstDataArr = firstData.split("   +");
        String stockCodeStr = firstDataArr[2];
        if (firstDataArr.length == 5) {
            stockCode = stockCodeStr.substring(0, 6);
            stockName = stockCodeStr.substring(6).trim();
            stockQuantity = NumUtil.transDoubleToInt(firstDataArr[3].trim());
            soldQuantity = NumUtil.transDoubleToInt(firstDataArr[4].trim());
        } else {
            stockCode = firstDataArr[2].trim();
            stockName = firstDataArr[3].trim();
            stockQuantity = NumUtil.transDoubleToInt(firstDataArr[4].trim());
            soldQuantity = NumUtil.transDoubleToInt(firstDataArr[5].trim());
        }
        String secondDataArr[] = secondData.split("\\s+");
        List<String> dataList = new ArrayList<>();
        for (String dataEle : secondDataArr) {
            if (StringUtils.isNotBlank(dataEle)) {
                dataList.add(dataEle);
            }
        }
        String accountMarketValueStr = dataList.get(2);
        accountMarketValue = NumUtil.add(accountMarketValue, Double.valueOf(accountMarketValueStr));
        String accountProfitStr = dataList.get(3);
        accountProfit = NumUtil.add(accountProfit, Double.valueOf(accountProfitStr));

        Map<String, Object> map = new HashMap<>();
        map.put("stockCode", stockCode);
        map.put("stockName", stockName);
        map.put("stockQuantity", stockQuantity);
        map.put("soldQuantity", soldQuantity);
        map.put("accountMarketValue", accountMarketValue);
        map.put("accountProfit", accountProfit);
        System.out.println(JSONObject.toJSONString(map));*/

        /*char[] chars = str.toCharArray();
        String tempCol = null;
        int continuSpaceNum = 0;
        String stockCodeStr = "";
        String stockNameStr = "";
        String stockQuantityStr = "";
        String soldQuantityStr = "";
        String accountMarketValueStr = "";
        String accountProfitStr = "";
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (Character.isSpaceChar(c)) {
                continuSpaceNum++;
            } else {
                continuSpaceNum = 0;
                if ()
            }
        }*/
    }
}
