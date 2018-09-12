package com.yingli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAutoConfiguration
@EnableAspectJAutoProxy(proxyTargetClass=true, exposeProxy=true)
@EnableTransactionManagement
public class StockMarketServerStart extends SpringBootServletInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockMarketServerStart.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(StockMarketServerStart.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(StockMarketServerStart.class);
        LOGGER.info("行情后台服务启动成功");
    }
}