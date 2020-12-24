package com.edgeMapper.EdgeMapper.mqtt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.edgeMapper.EdgeMapper.model.dto.BleGatewayDto;
import com.edgeMapper.EdgeMapper.service.MqttMsgService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by huqiaoqian on 2020/9/23
 */
@Slf4j
@Component
public class EdgeMqttCallback implements MqttCallback {
    @Autowired
    private EdgeMqttClient mqttClient;

    @Autowired
    private MqttMsgService mqttMsgService;

    @Override
    public void connectionLost(Throwable throwable) {
        log.error("connectionLost");
        while (true) {
            try {
                Thread.sleep(1000);
                mqttClient.init();
                break;
            } catch (Exception e) {
                continue;
            }
        }
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        log.info("topic={},message={}",s,mqttMessage);
        JSONObject jsonObject = JSON.parseObject(mqttMessage.toString());
        BleGatewayDto bleGatewayDto = JSON.parseObject(jsonObject.get("comType").toString(), BleGatewayDto.class);
        log.info("bleGatewayDto={}",bleGatewayDto);
        if (bleGatewayDto.getContent() != null
                && bleGatewayDto.getContent().getType().equals("Passthrough")
                && bleGatewayDto.getContent().getData() != null
                && bleGatewayDto.getContent().getData().getResponse().equals("Reply")
        ) {
            mqttMsgService.transferBleGatewayData(bleGatewayDto.getContent().getData().getData());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
