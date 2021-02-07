package com.edgeMapper.EdgeMapper.service.impl;

import com.alibaba.fastjson.JSON;
import com.edgeMapper.EdgeMapper.config.Constants;
import com.edgeMapper.EdgeMapper.model.dto.*;
import com.edgeMapper.EdgeMapper.service.DeviceDataService;
import com.edgeMapper.EdgeMapper.service.MqttMsgService;
import com.edgeMapper.EdgeMapper.service.TDEngineService;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.drools.javaparser.utils.Log;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by huqiaoqian on 2020/9/23
 */
@Slf4j
@Service
public class MqttMsgServiceImpl implements MqttMsgService {

    @Autowired
    private MqttClient mqttClient;

    @Autowired
    private DeviceDataService deviceDataService;

    @Autowired
    private TDEngineService tdEngineService;


    @Override
    public void updateDeviceTwin(String deviceName, JsonObject data){
        String topic = Constants.DeviceETPrefix + deviceName + Constants.TwinETUpdateSuffix;
        JsonObject rawMsg = new JsonObject();
        JsonObject twins = new JsonObject();
        Set<String> keySet = data.keySet();
        try{
            for (String key : keySet) {
                JsonObject twin = new JsonObject();
                JsonObject twinValue = new JsonObject();
                JsonObject typeMetadata = new JsonObject();
                twinValue.add("value", data.get(key));
                typeMetadata.addProperty("type", "Updated");
                twin.add("actual", twinValue);
                twin.add("metadata", typeMetadata);
                twins.add(key, twin);
            }
        } catch (Exception e) {
            log.error("json转换异常");
        }
        rawMsg.add("twin",twins);
        log.info("topic is {}, rawMsg is {}",topic,rawMsg);
        MqttMessage msg = new MqttMessage(rawMsg.toString().getBytes());
        try {
            mqttClient.publish(topic,msg);
        } catch (MqttException e) {
            log.error("mqtt消息发送失败");
        }

    }

    @Override
    public void launchOrder(String order) {
        String topic = "sys/8cd4950007da/cloud";
        BleDto bleDto = new BleDto();
        BleGatewayDto bleGatewayDto = new BleGatewayDto();
        BleGatewayContentDto bleGatewayContentDto = new BleGatewayContentDto();
        BleGatewayDataDto bleGatewayDataDto = new BleGatewayDataDto();
        bleGatewayDto.setType("Down");
        bleGatewayContentDto.setType("Passthrough");
        bleGatewayDataDto.setData(order);
        bleGatewayDataDto.setMac("EF3AEDFA337C");
        bleGatewayContentDto.setData(bleGatewayDataDto);
        bleGatewayDto.setContent(bleGatewayContentDto);
        bleDto.setComType(bleGatewayDto);
        String msgBody = JSON.toJSONString(bleDto);
        MqttMessage msg = new MqttMessage(msgBody.getBytes());
        log.info("向网关发送指令{}", msgBody);
        try {
            mqttClient.publish(topic,msg);
        } catch (Exception e) {
            Log.error("指令发送异常");
        }
    }



    @Override
    public void transferBleGatewayData(String data) throws SQLException, ClassNotFoundException {
        if (data.substring(0,2).equals("68")) {
            //有效数据包
            String cmd = data.substring(2,4);
            switch (cmd) {
                case "83"://手环电量
                    this.handleBleWatchPower(data.substring(8,10));
                    break;
                case "86"://心率、步数、里程、热量、步速
                    this.handleHeartBeats(data.substring(10,38));
                    break;
                case "03":
                    break;
                case "87"://版本号
                    this.handleVersion(data.substring(8,16));
                    break;
                case "84"://设置计步参数回应
                    break;
                case "85"://读取功能是否开启
                    this.handleFunctionStatus(data.substring(8,14));
                    break;
                default:
                    break;
            }
        }
    }

    private void handleBleWatchPower(String data) throws SQLException, ClassNotFoundException {
        int power = 0;
        power=Integer.parseInt(data,16);
        DeviceDto deviceDto = new DeviceDto();
        Map<String,String> properties = new HashMap<>();
        properties.put("power",String.valueOf(power));
        deviceDto.setDeviceName("ble-watch");
        deviceDto.setPropertyType("realTime");
        deviceDto.setProperties(properties);
        log.info("发送手环电量数据{}",deviceDto);
        /**DeviceDataDto deviceDataDto = new DeviceDataDto();
        List<SingleDataDto> dataDtos = new LinkedList<>();
        SingleDataDto dataDto = new SingleDataDto();
        deviceDataDto.setDeviceName("ble-watch");
        dataDto.setName("power");
        dataDto.setValue(String.valueOf(power));
        dataDtos.add(dataDto);
        deviceDataDto.setDataDtos(dataDtos);
        this.launchData(deviceDataDto);*/
        deviceDataService.processMsg(deviceDto);
        tdEngineService.insertBleWatchPower(power);
    }

    private void handleHeartBeats(String data) throws SQLException, ClassNotFoundException {
        int cur=0;
        int heartBeats=Integer.parseInt(data.substring(0,2),16);
        int walkCounts=256*256*256*Integer.parseInt(data.substring(8,10),16)+256*256*Integer.parseInt(data.substring(6,8),16)+
                256*Integer.parseInt(data.substring(4,6),16)+Integer.parseInt(data.substring(2,4),16);
        int miles=256*256*256*Integer.parseInt(data.substring(16,18),16)+256*256*Integer.parseInt(data.substring(14,16),16)+
                256*Integer.parseInt(data.substring(12,14),16)+Integer.parseInt(data.substring(10,12),16);
        int calolis=256*256*256*Integer.parseInt(data.substring(24,26),16)+256*256*Integer.parseInt(data.substring(22,24),16)+
                256*Integer.parseInt(data.substring(20,22),16)+Integer.parseInt(data.substring(18,20),16);
        int speed=Integer.parseInt(data.substring(26,28),16);
        DeviceDto deviceDto = new DeviceDto();
        Map<String,String> properties = new HashMap<>();
        properties.put("heartBeats",String.valueOf(heartBeats));
        properties.put("walkCounts",String.valueOf(walkCounts));
        properties.put("miles",String.valueOf(miles));
        properties.put("calolis",String.valueOf(calolis));
        properties.put("speed",String.valueOf(speed));
        deviceDto.setDeviceName("ble-watch");
        deviceDto.setPropertyType("realTime");
        deviceDto.setProperties(properties);
        log.info("发送手环实时数据（心率、步数、里程、热量、步速）{}",deviceDto);
        tdEngineService.insertWalkData(walkCounts);
        deviceDataService.processMsg(deviceDto);
    }

    private void handleVersion(String data){
        int cur=0;
        int deviceLow=Integer.parseInt(data.substring(0,2),16);
        int deviceHigh=Integer.parseInt(data.substring(2,4),16);
        int bluetoothVersion=Integer.parseInt(data.substring(4,6),16);
        int deviceVersion=Integer.parseInt(data.substring(6,8),16);
        DeviceDto deviceDto = new DeviceDto();
        Map<String,String> properties = new HashMap<>();
        properties.put("deviceLow",String.valueOf(deviceLow));
        properties.put("deviceHigh",String.valueOf(deviceHigh));
        properties.put("bluetoothVersion",String.valueOf(bluetoothVersion));
        properties.put("deviceVersion",String.valueOf(deviceVersion));
        deviceDto.setDeviceName("ble-watch");
        deviceDto.setPropertyType("deviceInfo");
        deviceDto.setProperties(properties);
        log.info("发送手环设备标识、蓝牙版本、设备版本{}",deviceDto);
        deviceDataService.processMsg(deviceDto);
    }

    private void handleFunctionStatus(String data){
        String readOrSet = "read";
        String type="";
        String status;
        log.info("data前两位:"+data.substring(0,2));
        if(data.substring(0,2).equals("01")){
            readOrSet = "read";
        } else {
            return;
        }
        switch (data.substring(2,4)){
            case "01":
                type="防丢提醒";
                break;
            case "02":
                type="短信提醒";
                break;
            case "03":
                type="来电提醒";
                break;
            case "05":
                type="蓝牙自动关广播";
                break;
            case "06":
                type="抬手亮屏";
                break;
            case "07":
                type="翻腕切屏";
                break;
            case "09":
                type="微信提醒";
                break;
            case "0a":
                type="QQ提醒";
                break;
            case "0b":
                type="facebook";
                break;
            case "0c":
                type="skype";
                break;
            case "0d":
                type="twitter";
                break;
            case "0e":
                type="whatsAPP";
                break;
            case "0f":
                type="line";
                break;
        }
        log.info("data后两位:"+data.substring(4,6));
        if(data.substring(4,6).equals("01")){
            status="TurnOn";
        } else {
            status="TurnOff";
        }
        DeviceDto deviceDto = new DeviceDto();
        Map<String,String> properties = new HashMap<>();
        properties.put("readOrSet",String.valueOf(readOrSet));
        properties.put("type",String.valueOf(type));
        properties.put("status",String.valueOf(status));
        deviceDto.setDeviceName("ble-watch");
        deviceDto.setPropertyType("deviceInfo");
        deviceDto.setProperties(properties);
        log.info("发送手环设备功能状态{}",deviceDto);
        deviceDataService.processMsg(deviceDto);
    }
}
