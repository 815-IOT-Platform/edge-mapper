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
}
