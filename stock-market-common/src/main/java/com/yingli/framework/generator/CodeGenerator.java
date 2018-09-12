package com.yingli.framework.generator;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.io.File;

/**
 * 根据MySql生成相应的实体和Service方法
 */
public class CodeGenerator {
    public static void main(String[] args){
        String [] models = {"api", "entity", "mapper", "service"};
        for (String model : models) {
            generator(model);
        }
    }

    public static void generator(String model) {
        //1.获取项目所在的路径
        File file = new File("");
        String path = file.getAbsolutePath();
        AutoGenerator mpg = new AutoGenerator();
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        //gc.setOutputDir(path);
        gc.setFileOverride(true);
        gc.setActiveRecord(true);
        gc.setEnableCache(false);
        // XML ResultMap
        gc.setBaseResultMap(true);
        // XML columList
        gc.setBaseColumnList(true);
        gc.setAuthor("chh2683@163.com");
        //不打开文件目录
        gc.setOpen(false);

        // 自定义文件命名，注意 %s 会自动填充表实体属性！
        gc.setMapperName("%sDao");
        gc.setXmlName("%sDao");
        gc.setServiceName("I%sService");
        gc.setServiceImplName("%sServiceImpl");
        //gc.setControllerName("%sAction");
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("yingli");
        dsc.setUrl("jdbc:mysql://10.10.1.101:3306/gubao?characterEncoding=utf8");
        mpg.setDataSource(dsc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();

        // 此处可以修改为您的表前缀
        strategy.setTablePrefix(new String[] {"a_", "s_", "u_"});
        strategy.setNaming(NamingStrategy.underline_to_camel);// 表名生成策略
        // 需要生成的表
        strategy.setInclude(new String[]{"s_stock_info"});
        mpg.setStrategy(strategy);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.yingli");
        //pc.setController("server.controller");
        pc.setEntity("entity");
        pc.setMapper("dao");
        pc.setXml("dao.mapper");
        pc.setService("service");
        pc.setServiceImpl("service.impl");
        //pc.setModuleName("finances-center-platform-data");
        mpg.setPackageInfo(pc);

        TemplateConfig tc = new TemplateConfig();
        if ("api".equals(model)) {
            gc.setOutputDir(path + "/stock-market-service/" + "src/main/java");
            tc.setController(null);
            tc.setEntity(null);
            tc.setMapper(null);
            tc.setXml(null);
            tc.setServiceImpl(null);
        } else if ("entity".equals(model)) {
            gc.setOutputDir(path + "/stock-market-service/" + "src/main/java");
            tc.setController(null);
            tc.setMapper(null);
            tc.setXml(null);
            tc.setService(null);
            tc.setServiceImpl(null);
        } else if ("mapper".equals(model)) {
            gc.setOutputDir(path + "/stock-market-service/" + "src/main/java");
            tc.setController(null);
            tc.setEntity(null);
            tc.setService(null);
            tc.setServiceImpl(null);
        } else if ("service".equals(model)) {
            gc.setOutputDir(path + "/stock-market-service/" + "src/main/java");
            tc.setController(null);
            tc.setEntity(null);
            tc.setMapper(null);
            tc.setXml(null);
            tc.setService(null);
        }
        mpg.setTemplate(tc);
        mpg.execute();
        System.out.println("finish!");
    }
}
