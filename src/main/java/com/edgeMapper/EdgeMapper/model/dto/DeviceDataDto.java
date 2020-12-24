package com.edgeMapper.EdgeMapper.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by huqiaoqian on 2020/9/23
 */
@Data
public class DeviceDataDto implements Serializable {
    private static final long serialVersionUID = 1579338473805266487L;
    private String deviceName;

    private List<SingleDataDto> dataDtos;
}
