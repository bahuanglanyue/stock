package com.yingli.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yingli.framework.entity.ResultBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

    private static final int SOCKET_TIME_OUT = 8000;//数据包间隔超时时间
    private static final int CONN_TIME_OUT = 5000;//连接建立超时时间
    private static final int CONN_REQ_TIME_OUT = 2000;//从连接池获取连接的超时时间

    public static String doGet(String requestUrl) throws Exception {
        return doGet(requestUrl, SOCKET_TIME_OUT, CONN_TIME_OUT, CONN_REQ_TIME_OUT);
    }

    public static String doGet(String requestUrl, int socketTimeOut, int connTimeOut, int connReqTimeOut) throws Exception {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeOut)
                .setConnectTimeout(connTimeOut)
                .setConnectionRequestTimeout(connReqTimeOut)
                //.setStaleConnectionCheckEnabled(true)
                .build();
        String str = null;
        CloseableHttpClient httpClient = null;
        HttpGet httpGet = null;
        try {
            httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
            httpGet = new HttpGet(requestUrl);
            HttpResponse response = httpClient.execute(httpGet);
            str = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            LOGGER.error("Http请求失败(" + requestUrl + ")", e);
        } finally {
            try {
                if (httpClient != null) {
                    httpGet.releaseConnection();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public static String doPost(String requestUrl, String reqBody) throws Exception {
        return doPost(requestUrl, reqBody, SOCKET_TIME_OUT, CONN_TIME_OUT, CONN_REQ_TIME_OUT);
    }

    public static String doPost(String requestUrl, String reqBody, Map<String, String> headerMap) throws Exception {
        return doPost(requestUrl, reqBody, SOCKET_TIME_OUT, CONN_TIME_OUT, CONN_REQ_TIME_OUT, headerMap);
    }

    public static String doPost(String requestUrl, String reqBody, int socketTimeOut, int connTimeOut, int connReqTimeOut)
        throws Exception {
        if (StringUtils.isBlank(requestUrl)) {
            return null;
        }
        String str = null;
        CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        try {
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(socketTimeOut)
                    .setConnectTimeout(connTimeOut)
                    .setConnectionRequestTimeout(connReqTimeOut)
                    //.setStaleConnectionCheckEnabled(true)
                    .build();
            httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
            httpPost = new HttpPost(requestUrl);
            httpPost.addHeader("Content-type","application/json; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setEntity(new StringEntity(reqBody, Charset.forName("UTF-8")));
            HttpResponse response = httpClient.execute(httpPost);
            str = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            LOGGER.error("Http请求失败(" + requestUrl + ")", e);
        } finally {
            try {
                if (httpClient != null) {
                    httpPost.releaseConnection();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public static String doPost(String requestUrl, String reqBody, int socketTimeOut, int connTimeOut, int connReqTimeOut, Map<String, String> headerMap)
            throws Exception {
        if (StringUtils.isBlank(requestUrl)) {
            return null;
        }
        String str = null;
        CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        try {
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(socketTimeOut)
                    .setConnectTimeout(connTimeOut)
                    .setConnectionRequestTimeout(connReqTimeOut)
                    //.setStaleConnectionCheckEnabled(true)
                    .build();
            httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
            httpPost = new HttpPost(requestUrl);
            httpPost.addHeader("Content-type","application/json; charset=utf-8");
            if (headerMap != null && !headerMap.isEmpty()) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }
            httpPost.setHeader("Accept", "application/json");
            httpPost.setEntity(new StringEntity(reqBody, Charset.forName("UTF-8")));
            HttpResponse response = httpClient.execute(httpPost);
            str = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            LOGGER.error("Http请求失败(" + requestUrl + ")", e);
        } finally {
            try {
                if (httpClient != null) {
                    httpPost.releaseConnection();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public static String doPost(String requestUrl, List<NameValuePair> nvpList, Map<String, String> headerMap) {
        return doPost(requestUrl, nvpList, SOCKET_TIME_OUT, CONN_TIME_OUT, CONN_REQ_TIME_OUT, headerMap);
    }

    public static String doPost(String requestUrl, List<NameValuePair> nvpList, int socketTimeOut, int connTimeOut, int connReqTimeOut, Map<String, String> headerMap) {
        String str = null;
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeOut)
                .setConnectTimeout(connTimeOut)
                .setConnectionRequestTimeout(connReqTimeOut)
                //.setStaleConnectionCheckEnabled(true)
                .build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(requestUrl);
            if (headerMap != null && !headerMap.isEmpty()) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvpList, "utf-8"));
            HttpResponse response = httpClient.execute(httpPost);
            str = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (httpClient != null) {
                    httpPost.releaseConnection();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return str;
        }
    }

    public static List<NameValuePair> object2NamePair(Object o) {
        List<NameValuePair> formParams = new ArrayList<NameValuePair>();
        Field[] fs = o.getClass().getDeclaredFields();
        for (Field field : fs) {
            field.setAccessible(true);//设置些属性是可以访问的
            Object val = null;//得到此属性的值
            try {
                val = field.get(o);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            String name = field.getName();//属性名称
            NameValuePair nameValuePair = new BasicNameValuePair(name, val.toString());
            formParams.add(nameValuePair);
        }
        return formParams;
    }

    public static void main(String[] args) throws Exception {
        int i = 0;
        while (true) {
            JSONArray jsonArray = new JSONArray();
            String ret = HttpUtil.doPost("http://127.0.96.255:9090/services/callback/api/1.0/entrustCallback", jsonArray.toJSONString());
            if (StringUtils.isNotBlank(ret) && ResultBean.SUCCESS == JSONObject.parseObject(ret).getInteger("code")) {
                System.out.println("-----------------1"+ret);
            } else{
                System.out.println("-----------------2");
            }
            i++;
            System.out.println("i=" + i);
        }

        /*try {
            for (int i = 0; i < 5; i++) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int j = 0; j < 2000; j++) {
                                doPost("http://10.10.1.101:9090/services/callback/api/1.0/entrustCallback", "[{\"accountAsset\":25814.43,\"accountAvailableRemain\":162.19,\"accountProfit\":2445.7999999999997,\"accountRemain\":487.72,\"parentFinanceId\":6666}]");
                                System.out.println(j);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    /*public static void sys(String[] args) {
        /*List<NameValuePair> formParams = new ArrayList<NameValuePair>();
        NameValuePair nameValuePair1 = new BasicNameValuePair("custId", "999");
        NameValuePair nameValuePair2 = new BasicNameValuePair("id", "999");
        NameValuePair nameValuePair3 = new BasicNameValuePair("postFinanceCautionMoney", "999");
        formParams.add(nameValuePair1);
        formParams.add(nameValuePair2);
        formParams.add(nameValuePair3);
        String str = doPost("http://10.10.1.101:8000/finances-center/trade/api/1.0/postFinanceCautionMoney", formParams);
        System.out.println(str);

        try {
            doPost("http://127.0.0.1:8080/callback/api/1.0/entrustCallback", "[{\"bargainAveragePrice\": 3.29}]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

}
