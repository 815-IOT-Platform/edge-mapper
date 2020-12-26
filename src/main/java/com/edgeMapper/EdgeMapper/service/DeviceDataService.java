package com.edgeMapper.EdgeMapper.service;

import com.edgeMapper.EdgeMapper.model.dto.BodyInfoDto;
import com.edgeMapper.EdgeMapper.model.dto.DeviceDto;

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
}
