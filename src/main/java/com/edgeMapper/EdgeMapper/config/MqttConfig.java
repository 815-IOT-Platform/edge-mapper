package com.edgeMapper.EdgeMapper.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by huqiaoqian on 2020/9/23
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mqtt")
public class MqttConfig {
    private int mode;

    private String server;

    private String internalServer;

    private String clientId;

    private boolean cleanSession;

    private int connectionTimeout;

    private int keepAliveInterval;
}
