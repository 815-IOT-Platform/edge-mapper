package com.edgeMapper.EdgeMapper.config;//package com.edgeMapper.EdgeMapper.config;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import tk.mybatis.spring.annotation.MapperScan;
//
//import javax.sql.DataSource;
//
///**
// * Created by huqiaoqian on 2020/11/24
// */
//@Configuration
//@MapperScan(basePackages = "com.hqq.ruleservice.mappers")
//public class DataSourceConfig {
//
//    @Primary
//    @Bean(name = "DataSource")
//    @ConfigurationProperties(prefix="spring.datasource")
//    public DataSource DataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//}
