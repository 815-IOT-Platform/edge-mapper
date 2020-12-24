package com.edgeMapper.EdgeMapper.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by huqiaoqian on 2020/12/20
 */
@Data
public class BleDto implements Serializable {
    private static final long serialVersionUID = -2426212373753757068L;

    private BleGatewayDto comType;
}
