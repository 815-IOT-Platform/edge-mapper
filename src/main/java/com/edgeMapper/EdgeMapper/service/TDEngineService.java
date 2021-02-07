package com.edgeMapper.EdgeMapper.service;

import com.edgeMapper.EdgeMapper.model.dto.MqttDeviceDataDto;

import java.sql.SQLException;

public interface TDEngineService {

    public void testTD() throws ClassNotFoundException, SQLException;

    public void insertBleWatchPower (Integer power) throws ClassNotFoundException, SQLException;

    public void insertWalkData (Integer walkCounts) throws ClassNotFoundException, SQLException;

    public void launchData (MqttDeviceDataDto mqttDeviceDataDto);
}
