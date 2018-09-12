package com.yingli.common.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 钉钉服务工具类
 */
public class DingTalkUtil {

    private static final String DINGTALK_SEND_URL = "https://oapi.dingtalk.com/robot/send?access_token=";
    private static final String DEFAULT_ACCESS_TOKEN = "c8fcb82aeaabad44058ce08165759f86b38c9dc7b4e23acc169602d2c145cca4";

    /**
     * 向钉钉发送消息
     * @param accessToken
     * @param msg
     */
    public static void sendMsgToDingTalk(String serverName, String accessToken, String msg) {
        ThreadPoolHelper.execute(() -> {
            try {
                String ip = NetUtil.getLocalAddress();
                HttpClient httpclient = HttpClients.createDefault();
                String url;
                if (StringUtils.isBlank(accessToken)) {
                    url = DINGTALK_SEND_URL + DEFAULT_ACCESS_TOKEN;
                } else {
                    url = DINGTALK_SEND_URL + accessToken;
                }
                HttpPost httppost = new HttpPost(url);
                httppost.addHeader("Content-Type", "application/json; charset=utf-8");
                JSONObject msgJson = new JSONObject();
                msgJson.put("msgtype", "text");
                JSONObject textJson = new JSONObject(true);
                msgJson.put("text", textJson);
                textJson.put("content", serverName.concat("消息（ip=").concat(ip).concat("）\n").concat(msg));
                System.out.println(msgJson.toJSONString());
                StringEntity se = new StringEntity(msgJson.toJSONString(), "utf-8");
                httppost.setEntity(se);
                httpclient.execute(httppost);
            } catch (Exception exception) {
            }
        });
    }

    public static void sendMsgToDingTalk(String serverName, String msg) {
        sendMsgToDingTalk(serverName, null, msg);
    }

    /**
     * 发送消息至远程服务器
     * @param remoteUrl
     * @param msg
     */
    public static void sendMsgToDingRemote(final String remoteUrl, String msg) {
        final JSONObject param = new JSONObject();
        param.put("ip", NetUtil.getLocalAddress());
        param.put("msg", msg);
        try {
            ThreadPoolHelper.execute(() -> {
                try {
                    String ret = HttpUtil.doPost(remoteUrl, param.toJSONString());
                    System.out.println(ret);
                } catch (Exception exception) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 向钉钉发送异常信息
     * @param e
     */
    public static void sendMsgToDingTalk(String serverName, Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        String errorPrintStackTrace = sw.getBuffer().toString();
        sendMsgToDingTalk(serverName, errorPrintStackTrace);
    }

    public static void main(String[] args) {
        sendMsgToDingTalk("交易端", "test");
    }
}
