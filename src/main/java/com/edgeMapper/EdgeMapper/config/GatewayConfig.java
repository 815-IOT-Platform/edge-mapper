package com.edgeMapper.EdgeMapper.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created by huqiaoqian on 2020/11/4
 */
@Configuration
@ConfigurationProperties(prefix = "gateway")
@Data
public class GatewayConfig {
    private String host;
    private Integer port;
    private Map<String,String> devices;
}
