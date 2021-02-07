package com.edgeMapper.EdgeMapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * Created by huqiaoqian on 2020/9/23
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class EdgeMapperApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdgeMapperApplication.class,args);
    }

}
