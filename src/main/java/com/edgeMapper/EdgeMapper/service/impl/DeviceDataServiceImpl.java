package com.edgeMapper.EdgeMapper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.edgeMapper.EdgeMapper.config.GatewayConfig;
import com.edgeMapper.EdgeMapper.model.dto.BodyInfoDto;
import com.edgeMapper.EdgeMapper.model.dto.DeviceDataDto;
import com.edgeMapper.EdgeMapper.model.dto.DeviceDto;
import com.edgeMapper.EdgeMapper.model.dto.SingleDataDto;
import com.edgeMapper.EdgeMapper.service.DeviceDataService;
import com.edgeMapper.EdgeMapper.service.MqttMsgService;
import com.edgeMapper.EdgeMapper.util.ByteUtil;
import com.edgeMapper.EdgeMapper.util.GatewayUtil;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by huqiaoqian on 2020/9/23
 */
@Slf4j
@Service
public class DeviceDataServiceImpl implements DeviceDataService {

//    @Autowired
//    private MqttMsgService mqttService;

    @Autowired
    private DefaultMQProducer producer;

    @Autowired
    private GatewayConfig gatewayConfig;

    @Autowired
    private MqttMsgService mqttMsgService;

    @Override
    public void processMsg(DeviceDto deviceDto) {
        try{
            JsonObject data = new JsonObject();
            for(Map.Entry<String ,String> entry:deviceDto.getProperties().entrySet()){
                data.addProperty(entry.getKey(),entry.getValue());
            }
            Message msg = new Message("device-data", JSONObject.toJSONString(deviceDto).getBytes());
            producer.send(msg);
//            mqttService.updateDeviceTwin(deviceDto.getDeviceName(), data);
        }
        catch (Exception e){
            log.error("推送mq实时数据异常",e);
        }
    }

    @Override
    public void processMsg(byte[] bytes) {
        byte response = bytes[0];
        log.info("收到消息={}", ByteUtil.bytesToHexString(bytes));
        switch (response) {
            case 0x70:
                String clusterId = GatewayUtil.byte2HexStr(Arrays.copyOfRange(bytes, 5, 7));
                log.info("clusterId is {}",clusterId);
                boolean isAlarm=false;
                JsonObject data = new JsonObject();
                DeviceDataDto deviceDataDto=new DeviceDataDto();
                List<SingleDataDto> dataDtos=new ArrayList<>();
                switch (clusterId) {
                    case "0004":
                        for (int i = 0; i < Integer.parseInt(String.valueOf(bytes[7])); i++) {
                            if (GatewayUtil.byte2HexStr(Arrays.copyOfRange(bytes, 8 + i * 5, 10 + i * 5)).equals("0000")) {
                                if (bytes[10 + i * 5] == 0x21) {
                                    SingleDataDto dataDto = new SingleDataDto();
                                    int illumination = GatewayUtil.dataBytesToInt(Arrays.copyOfRange(bytes, 11 + i * 5, 13 + i * 5));
                                    if (illumination >= 500) {
                                        isAlarm = true;
                                    }
                                    data.addProperty("illumination", String.valueOf(illumination));
                                    dataDto.setName("illumination");
                                    dataDto.setValue(String.valueOf(illumination));
                                    dataDtos.add(dataDto);
                                }
                            }
                        }
                        if (gatewayConfig.getDevices().containsKey("0004")) {
                            String deviceName = gatewayConfig.getDevices().get("0004");
                            deviceDataDto.setDeviceName(deviceName);
                            deviceDataDto.setDataDtos(dataDtos);
                            log.info("设备数据为{}",deviceDataDto);
                            try {
                                Message msg = new Message("device-data", JSONObject.toJSONString(deviceDataDto).getBytes());
                                producer.send(msg);
                            } catch (Exception e) {
                                log.error("推送mq实时数据异常",e);
                            }
                            //todo:光感度过高，上报给云端
//                            if (isAlarm) {
//                                mqttService.updateDeviceTwin(deviceName, data);
//                            }
                        } else {
                            log.error("云端不存在此设备，或是设备名不匹配");
                        }
                        break;
                }
            case 0x01:
                log.info("全部设备信息={}",bytes);
                break;
            default:
                log.info("消息类型无匹配");
                break;
        }
    }

    @Override
    public void receiveData(byte[] data) {
        DeviceDto deviceDto=new DeviceDto();
    }

    @Override
    public void getBleWatchPower() {
        String order = "680300006B16";
        mqttMsgService.launchOrder(order);
    }

    @Override
    public void setWalkCounts(BodyInfoDto bodyInfoDto) {
        String bodyInfo=getBodyInfo(bodyInfoDto);
        String order="68040400"+bodyInfo+"7A16";
        mqttMsgService.launchOrder(order);
    }

    @Override
    public void getHeartBeats() {
        String order="68060100006F16";
        mqttMsgService.launchOrder(order);
    }

    public void updateBleWatchPower() {

    }

    public String getBodyInfo(BodyInfoDto bodyInfoDto){
        //todo 转化成16进制
        return "B23C001C";//身高2+体重2+性别2（男：0，女：1）+年龄4:178cm+60kg+男+28岁
    }
}
