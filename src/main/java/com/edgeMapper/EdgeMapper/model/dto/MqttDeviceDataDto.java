package com.edgeMapper.EdgeMapper.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MqttDeviceDataDto {
    String deviceName;
    Map<String, String> properties;
    String tdSql;
}
