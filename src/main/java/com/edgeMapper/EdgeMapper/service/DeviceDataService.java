package com.edgeMapper.EdgeMapper.service;

import com.edgeMapper.EdgeMapper.model.dto.BodyInfoDto;
import com.edgeMapper.EdgeMapper.model.dto.DeviceDto;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by huqiaoqian on 2020/9/23
 */
public interface DeviceDataService {
    public void processMsg(DeviceDto deviceDto);

    public void processMsg(byte[] bytes);

    public void receiveData(byte[] data);

    public void getBleWatchPower();

    public void setWalkCounts(BodyInfoDto bodyInfoDto);

    public void getHeartBeats();

    public void getParameters(); //4.2.1

    public void setParameters(Integer is12Hour, Integer isMetric, Integer heartBeatInterval, Integer Fatigue, Integer heartBeatInspection, Integer timeFormat, Integer Language); //4.2.2

    public void getVersion(); //4.7获取软硬件编码

    public void setWalkParameters(Integer height, Integer weight, Integer sex, Integer age); //4.4设置计步参数

    public String crc (String data);//计算crc校验码，通用

    public String converse (String data);//对于多字节的数据，转化为低字节在前

    public void getFunctionStatus (String type); //4.5获取功能开关状态，参数直接就是两位十六进制的字符串

    public void setFunctionStatus (String type, String status); //4.5改变功能开关状态，参数直接就是两位十六进制的字符串，status为1是开

    public void setWeather (Map map) throws UnsupportedEncodingException;//4.15 给设备发送天气消息

    public void setReminder (Map map);   //4.9 设置自定义提醒

    public void readReminder (String data);  //4.9 读取自定义提醒

    public void deleteReminder (String data);    //4.9 删除自定义提醒
}
