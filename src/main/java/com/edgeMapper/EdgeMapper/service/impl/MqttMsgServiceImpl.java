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
                    this.handleHeartBeats(data.substring(10));
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

    @Override
    public void reconncetToBleGateway() {
        String topic = "sys/8cd4950007da/cloud";
        BleDto bleDto=new BleDto();
        BleGatewayDto bleGatewayDto=new BleGatewayDto();
        BleGatewayContentDto bleGatewayContentDto = new BleGatewayContentDto();
        BleGatewayDataDto bleGatewayDataDto = new BleGatewayDataDto();
        bleGatewayDto.setType("Down");
        bleGatewayContentDto.setType("ConnectDeviceLong");
        bleGatewayDataDto.setData("");
        bleGatewayDataDto.setMac("EF3AEDFA337C");
        bleGatewayContentDto.setData(bleGatewayDataDto);
        bleGatewayDto.setContent(bleGatewayContentDto);
        bleDto.setComType(bleGatewayDto);
        String msgBody = JSON.toJSONString(bleDto);
        MqttMessage msg = new MqttMessage(msgBody.getBytes());
        log.info("向网关发起重连{}", msgBody);
        try {
            mqttClient.publish(topic,msg);
        } catch (Exception e) {
            Log.error("重连指令发送异常");
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
        deviceDto.setPropertyType("deviceInfo");
        deviceDto.setProperties(properties);
        log.info("发送手环电量数据{}",deviceDto);
        deviceDataService.processMsg(deviceDto);
    }

    private void handleHeartBeats(String data){
        log.info("valid data is {}",data);
        int heartBeats=16*(data.charAt(0)-'0')+data.charAt(1)-'0';
        int walkCounts=0,miles=0,calolis=0,speed=0;
        String w=reverse(data.substring(2,10));
        String m=reverse(data.substring(10,18));
        String c=reverse(data.substring(18,26));
        for(int i=0;i<3;i++){
            for(int j=0;j<8;j++){
                switch (i){
                    case 0:walkCounts+=walkCounts*16+w.charAt(j)-'0';
                        break;
                    case 1:miles+=miles*16+m.charAt(j)-'0';
                        break;
                    case 2:calolis+=calolis*16+c.charAt(j)-'0';
                        break;
                    default:break;
                }
            }
        }
        speed=16*(data.charAt(26)-'0')+data.charAt(27)-'0';
        DeviceDto deviceDto = new DeviceDto();
        Map<String,String> properties = new HashMap<>();
        properties.put("heartBeats",String.valueOf(heartBeats));
        properties.put("walkCounts",String.valueOf(walkCounts));
        properties.put("miles",String.valueOf(miles));
        properties.put("calories",String.valueOf(calolis));
        properties.put("speed",String.valueOf(speed));
        deviceDto.setDeviceName("ble-watch");
        deviceDto.setPropertyType("realTime");
        deviceDto.setProperties(properties);
        log.info("发送手环实时数据（心率、步数、里程、热量、步速）{}",deviceDto);
        deviceDataService.processMsg(deviceDto);
    }
    private String reverse(String s){
        String ans="";
        ans=s.substring(6,8)+s.substring(4,6)+s.substring(2,4)+s.substring(0,2);
        return ans;
    }
    private void handleVersion(String data){
        int cur=0;
        int deviceLow=16*(data.charAt(cur++)-'0')+data.charAt(cur++)-'0';
        int deviceHigh=16*(data.charAt(cur++)-'0')+data.charAt(cur++)-'0';
        int bluetoothVersion=16*(data.charAt(cur++)-'0')+data.charAt(cur++)-'0';
        int deviceVersion=16*(data.charAt(cur++)-'0')+data.charAt(cur++)-'0';
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
