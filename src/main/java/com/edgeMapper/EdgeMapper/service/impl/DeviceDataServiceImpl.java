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

import java.io.UnsupportedEncodingException;
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
    public void getBleWatchPower() { //获取电量
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
    public void getHeartBeats() { //获取心率、步数等
        String order="68060100006F16";
        mqttMsgService.launchOrder(order);
    }

    @Override
    public void getVersion() { //4.7
        String order="680700006F16";
        mqttMsgService.launchOrder(order);
    }

    @Override
    public void getParameters(){
    }

    @Override
    public void setParameters(Integer is12Hour, Integer isMetric, Integer heartBeatInterval, Integer Fatigue, Integer heartBeatInspection, Integer timeFormat, Integer Language){
        String order="6802";
    }

    @Override
    public void setWalkParameters(Integer height, Integer weight, Integer sex, Integer age){ //参数传十进制的
        String order="68040400";
        String heightHex = String.format("%02X",height);
        log.info("heightHex:"+heightHex);
        String weightHex = String.format("%02X",weight);
        String sexHex = String.format("%02X",sex);
        String ageHex = String.format("%02X",age);
        order = order + heightHex + weightHex + sexHex + ageHex;
        String crc = crc(order);
        order = order + crc +"16";
        mqttMsgService.launchOrder(order);
    }

    public String getBodyInfo(BodyInfoDto bodyInfoDto){
        //todo 转化成16进制
        return "B23C001C";//身高2+体重2+性别2（男：0，女：1）+年龄4:178cm+60kg+男+28岁
    }

    @Override
    public void getFunctionStatus (String type){
        String order="68050200";
        order+="01";//读取
        order+=type;//类型
        order+=crc(order);
        order+="16";
        mqttMsgService.launchOrder(order);
        return;
    }

    @Override
    public void setFunctionStatus (String type, String status){
        String order="68050300";
        order+="00";//设置
        order+=type;
        order+=status;
        order+=crc(order);
        order+="16";
        mqttMsgService.launchOrder(order);
        return;
    }

    @Override
    public void setWeather(Map map) throws UnsupportedEncodingException {
        String order = "680F";
        int data_length = 0;
        String area = (String) map.get("地点");
        int area_length = area.getBytes("UTF-8").length; //中文对应 UTF-8编码的长度
        String weatherContent = (String)map.get("天气内容");
        int weatherContent_length = area.getBytes("UTF-8").length;
        data_length = area_length + weatherContent_length + 12;     // 数据部分总长度: 中文部分 + 其他部分
        order += String.format("%02X",data_length);                 // 数据长度
        order += "00";
        order += converse(String.format("%08X",(int)map.get("时间")));
        order += converse(new String(((String) map.get("地点")).getBytes("UTF-8")));
        order += String.format("%02X",(int)map.get("天气"));
        order += converse(new String(((String) map.get("天气内容")).getBytes("UTF-8")));
        order += converse(String.format("%06X",(int)map.get("温度")));
        order += String.format("%02X",(int)map.get("紫外线强度"));
        order += String.format("%02X",(int)map.get("风力"));
        order += String.format("%02X",(int)map.get("风向"));
        order += String.format("%02X",(int)map.get("空气质量"));
        order+=crc(order);
        order+="16";
        mqttMsgService.launchOrder(order);
        return;
    }

    @Override
    public void setReminder(Map map) {  //一次设置一个时间点
        String order = "";
        if ((int)map.get("提醒类型") != 6) {    //常规提醒
            order = "680907000100";
            order += (String)map.get("提醒类型");
            order += (String)map.get("提醒总数");
            order += (String)map.get("提醒时间");
            order += (String)map.get("星期重复");
            order += crc(order);
            order += "16";
        }
    }

    @Override
    public void readReminder(String data) {

    }

    @Override
    public void deleteReminder(String data) {

    }

    @Override
    public String crc (String data){ //计算校验码
        int i=0;
        int sum=0;
        while(i<data.length()){
            if(data.charAt(i)>='0' && data.charAt(i)<='9'){
                sum+= 16*(data.charAt(i)-'0');
            } else{
                sum = sum + 16*(10 + data.charAt(i) - 'A');
            }
            i++;
            if(data.charAt(i)>='0' && data.charAt(i)<='9'){
                sum+= data.charAt(i)-'0';
            } else{
                sum = sum + 10 + data.charAt(i) - 'A';
            }
            i++;
        }
        log.info("sum:"+sum);
        log.info("sumHex:"+String.format("%02X",sum));
        String a = String.format("%02X",sum);
        log.info("sumHex2:"+ a.substring(a.length()-2));
        return a.substring(a.length()-2);
    }

    @Override
    public String converse(String data) { //转化为低字节在前
        String ans = "";
        int index = data.length();
        int time = index / 2;
        while (time != 0) {
            ans += data.substring(index-2,index);
            index -= 2;
            time --;
        }
        return ans;
    }
}
