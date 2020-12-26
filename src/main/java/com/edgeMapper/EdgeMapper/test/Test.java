package com.edgeMapper.EdgeMapper.test;

import com.edgeMapper.EdgeMapper.model.dto.BodyInfoDto;
import com.edgeMapper.EdgeMapper.model.dto.DeviceDto;
import com.edgeMapper.EdgeMapper.service.DeviceDataService;
import com.edgeMapper.EdgeMapper.service.impl.DeviceDataServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Test {
    public static void main(String[] args){
        DeviceDataServiceImpl deviceDataServiceImpl = new DeviceDataServiceImpl();
        deviceDataServiceImpl.setWalkParameters(160,80,0,22);
    }
}
