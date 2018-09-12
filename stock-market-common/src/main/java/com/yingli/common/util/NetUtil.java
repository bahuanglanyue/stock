package com.yingli.common.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NetUtil {

    /**
     * 获取本地IP
     * @return
     */
    public static String getLocalAddress() {
        String hostAddress = "未知";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hostAddress;
    }

    /**
     * 判断ip是否可以连接
     * @param host ip
     * @param timeOut 超时时间
     * @return
     */
    public static boolean isHostReachable(String host, Integer timeOut) {
        try {
            return InetAddress.getByName(host).isReachable(timeOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断ip、端口是否可连接
     * @param host
     * @param port
     * @return
     */
    public static boolean isHostConnectable(String host, int port, int timeOut) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port), timeOut);
        } catch (IOException e) {
            //e.printStackTrace();
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        boolean flag = isHostConnectable("222.73.49.44", 7709, 1000);
        long endTime = System.currentTimeMillis();
        System.out.println("flag="+ flag + ",time=" + (endTime - startTime));
    }
}
