package com.edgeMapper.EdgeMapper.service;

import com.edgeMapper.EdgeMapper.model.dto.DeviceDataDto;
import com.google.gson.JsonObject;

import java.sql.SQLException;

/**
 * Created by huqiaoqian on 2020/9/23
 */
public interface MqttMsgService {
    public void updateDeviceTwin(String deviceName, JsonObject data);

    public void launchOrder(String order);

    public void transferBleGatewayData(String data) throws SQLException, ClassNotFoundException;


    public void reconncetToBleGateway();
}
