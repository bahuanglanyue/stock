package com.yingli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass=true, exposeProxy=true)
public class GatewayStart {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayStart.class);

    public static void main(String[] args) {
        SpringApplication.run(GatewayStart.class);
        LOGGER.info("行情API服务启动成功");
    }
}
