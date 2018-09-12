package com.yingli.stock.server;

import java.util.List;

public class HqServerConfig {
    private List<String> hqIp;

    private List<Integer> hqPort;

    public List<String> getHqIp() {
        return hqIp;
    }

    public void setHqIp(List<String> hqIp) {
        this.hqIp = hqIp;
    }

    public List<Integer> getHqPort() {
        return hqPort;
    }

    public void setHqPort(List<Integer> hqPort) {
        this.hqPort = hqPort;
    }
}