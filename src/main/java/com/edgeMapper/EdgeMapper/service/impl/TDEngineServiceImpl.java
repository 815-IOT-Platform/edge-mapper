package com.edgeMapper.EdgeMapper.service.impl;

import com.alibaba.fastjson.JSON;
import com.edgeMapper.EdgeMapper.model.dto.DeviceDataDto;
import com.edgeMapper.EdgeMapper.model.dto.MqttDeviceDataDto;
import com.edgeMapper.EdgeMapper.service.TDEngineService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

@Slf4j
@Service
public class TDEngineServiceImpl implements TDEngineService {
    @Lazy
    @Autowired
    MqttClient mqttClient;
    @Override
    public void testTD() throws ClassNotFoundException, SQLException {
        Class.forName("com.taosdata.jdbc.TSDBDriver");
        String jdbcUrl = "jdbc:TAOS://edge-pi:6030/testdb";
        Connection conn = DriverManager.getConnection(jdbcUrl);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("use testdb");
        ResultSet resultSet = stmt.executeQuery("select * from tb");
        Timestamp ts = null;
        Integer temp = null;
        int i=1;
        while(resultSet.next()){
            ts = resultSet.getTimestamp("ts");
            temp = resultSet.getInt("temp");
            System.out.print("第" + i + "行，" + "时间戳为：" + ts + "；数字为：" + temp + "\n");
        }
        resultSet.close();
        stmt.close();
        conn.close();
        return;
    }

    @Override
    public void insertBleWatchPower (Integer power) throws ClassNotFoundException, SQLException {
        Class.forName("com.taosdata.jdbc.TSDBDriver");
        String jdbcUrl = "jdbc:TAOS://edge-pi:6030/testdb";
        Connection conn = DriverManager.getConnection(jdbcUrl);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("use testdb");
        String query = "insert into ble_watch_power values(now, " + String.valueOf(power) + ")";
        stmt.executeQuery(query);
        MqttDeviceDataDto mqttDeviceDataDto = new MqttDeviceDataDto();
        mqttDeviceDataDto.setDeviceName("ble_watch_power"); //设置设备名
        mqttDeviceDataDto.setTdSql(query); //设置TDEngine的SQL语句、
        Map<String,String> hashMap = new HashMap<>();
        hashMap.put("powerTest", String.valueOf(power));
        mqttDeviceDataDto.setProperties(hashMap);
        this.launchData(mqttDeviceDataDto);
        stmt.close();
        conn.close();
        return;
    }

    @Override
    public void insertWalkData (Integer walkCounts) throws ClassNotFoundException, SQLException {
        Class.forName("com.taosdata.jdbc.TSDBDriver");
        String jdbcUrl = "jdbc:TAOS://edge-pi:6030/testdb";
        Connection conn = DriverManager.getConnection(jdbcUrl);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("use testdb");
        String query = "insert into walk_counts values(now, " + String.valueOf(walkCounts) + ")";
        stmt.executeQuery(query);
        stmt.close();
        conn.close();
        return;
    }

    @Override
    public void launchData(MqttDeviceDataDto mqttDeviceDataDto) {
        String topic = "sys/liupeihan/cloud";
        String msgBody = JSON.toJSONString(mqttDeviceDataDto);
        MqttMessage msg = new MqttMessage(msgBody.getBytes());
        log.info("向mqtt发出指令待云端接收",msgBody);
        try{
            mqttClient.publish(topic, msg);
        } catch (Exception e) {
            log.error("指令发送异常");
        }
    }
}
