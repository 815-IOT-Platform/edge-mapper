package com.edgeMapper.EdgeMapper.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by huqiaoqian on 2020/12/20
 */
@Data
public class BleGatewayContentDto implements Serializable {
    private static final long serialVersionUID = 1310368529813730723L;

    private String type;

    private BleGatewayDataDto data;
}
