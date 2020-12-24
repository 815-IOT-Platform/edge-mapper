package com.edgeMapper.EdgeMapper.service.impl;

import com.alibaba.fastjson.JSON;
import com.edgeMapper.EdgeMapper.config.Constants;
import com.edgeMapper.EdgeMapper.model.dto.*;
import com.edgeMapper.EdgeMapper.service.DeviceDataService;
import com.edgeMapper.EdgeMapper.service.MqttMsgService;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.drools.javaparser.utils.Log;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    public void transferBleGatewayData(String data) {
        if (data.substring(0,2).equals("68")) {
            //有效数据包
            String cmd = data.substring(2,4);
            switch (cmd) {
                case "83"://手环电量
                    this.handleBleWatchPower(data.substring(8,10));
                    break;
                case "86"://心率、步数、里程、热量、步速
                    this.handleHeartBeats(data.substring(8,36));
                case "03":
                    break;
                default:
                    break;
            }
        }
    }

    private void handleBleWatchPower(String data) {
        int power = 0;
        for (int i=0;i<data.length();i++) {
            power+=(power*16+(data.charAt(i)-'0'));
        }
        DeviceDto deviceDto = new DeviceDto();
        Map<String,String> properties = new HashMap<>();
        properties.put("power",String.valueOf(power));
        deviceDto.setDeviceName("ble-watch");
        deviceDto.setProperties(properties);
        log.info("发送手环电量数据{}",deviceDto);
        deviceDataService.processMsg(deviceDto);
    }
    private void handleHeartBeats(String data){
        int cur=0;
        int heartBeats=16*(data.charAt(cur++)-'0')+data.charAt(cur++)-'0';
        int speed=(data.charAt(cur++)-'0')*16+data.charAt(cur)-'0';
        int walkCounts=0,miles=0,calolis=0;
        for(int i=0;i<3;i++){
            for(int j=0;j<8;j++){
            switch (i){
                case 0:walkCounts+=walkCounts*16+data.charAt(cur++)-'0';
                break;
                case 1:calolis+=calolis*16+data.charAt(cur++)-'0';
                break;
                case 2:miles+=miles*16+data.charAt(cur++)-'0';
                break;
                default:break;
            }
            }
        }
        DeviceDto deviceDto = new DeviceDto();
        Map<String,String> properties = new HashMap<>();
        properties.put("heartBeats",String.valueOf(heartBeats));
        properties.put("walkCounts",String.valueOf(walkCounts));
        properties.put("miles",String.valueOf(miles));
        properties.put("calolis",String.valueOf(calolis));
        properties.put("speed",String.valueOf(speed));
        deviceDto.setDeviceName("ble-watch");
        deviceDto.setProperties(properties);
        log.info("发送手环实时数据（心率、步数、里程、热量、步速）{}",deviceDto);
        deviceDataService.processMsg(deviceDto);
    }
}
