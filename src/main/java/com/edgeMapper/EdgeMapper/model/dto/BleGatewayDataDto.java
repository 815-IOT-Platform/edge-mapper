package com.edgeMapper.EdgeMapper.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by huqiaoqian on 2020/12/20
 */
@Data
public class BleGatewayDataDto implements Serializable {
    private static final long serialVersionUID = -7978364844335600003L;

    private String response;

    private String mac;

    private String data;
}
