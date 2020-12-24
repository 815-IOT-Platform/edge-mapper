package com.edgeMapper.EdgeMapper.consumer;//package com.edgeMapper.EdgeMapper.consumer;
//
//import com.edgeMapper.EdgeMapper.model.dto.BodyInfoDto;
//import com.edgeMapper.EdgeMapper.model.mq.MqMessage;
//import com.edgeMapper.EdgeMapper.service.DeviceDataService;
//import com.edgeMapper.EdgeMapper.util.JacksonUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//
///**
// * Created by huqiaoqian on 2020/10/28
// */
//@Slf4j
//@Service
//public class TopicConsumer {
//    @Autowired
//    private DeviceDataService deviceDataService;
//    public void handlerSendMqMsg(String body, String topicName, String tags, String keys){
//        log.info("handlerSendMqMsg:body={},topicName={},tags={},keys={}",body,topicName,tags,keys);
//        MqMessage.checkMessage(body, keys, topicName);
//        BodyInfoDto bodyInfoDto;
//        try {
//            bodyInfoDto = JacksonUtil.parseJson(body, BodyInfoDto.class);
//        } catch (IOException e) {
//            log.error("发送短信MQ出现异常={}", e.getMessage(), e);
//            throw new IllegalArgumentException("JSON转换异常", e);
//        }
//        if(bodyInfoDto==null){
//            log.error("消息体为空");
//        }
//        deviceDataService.setWalkCounts(bodyInfoDto);
//    }
//}
