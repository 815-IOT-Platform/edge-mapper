package com.edgeMapper.EdgeMapper.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by huqiaoqian on 2020/9/23
 */
@Data
public class DeviceDto implements Serializable {
    private String deviceName;
    private Map<String,String> properties;
}
