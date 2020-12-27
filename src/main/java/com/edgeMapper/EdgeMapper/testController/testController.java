package com.edgeMapper.EdgeMapper.testController;

import com.edgeMapper.EdgeMapper.dao.mysql.RuleMapper;
import com.edgeMapper.EdgeMapper.model.domain.Rule;
import com.edgeMapper.EdgeMapper.model.domain.Signs;
import com.edgeMapper.EdgeMapper.model.dto.DeviceDto;
import com.edgeMapper.EdgeMapper.service.DeviceDataService;
import com.edgeMapper.EdgeMapper.service.SignsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huqiaoqian on 2020/9/23
 */
@Api
@RestController
@RequestMapping("mock")
public class testController {

    @Autowired
    DeviceDataService deviceDataService;

    @Autowired
    private SignsService signsService;

    @Autowired
    private RuleMapper ruleMapper;

    @ApiOperation(value = "给mapper传送数据")
    @PostMapping("data")
    public void mockData(@RequestBody DeviceDto deviceDto){
        deviceDataService.processMsg(deviceDto);
    }

    /**
     * create database and table
     * @return
     */
    @GetMapping("/init")
    @ApiOperation("init")
    public boolean init(){
        return signsService.init();
    }

    /**
     * Pagination Query
     * @param limit
     * @param offset
     * @return
     */
    @ApiOperation("query")
    @GetMapping("/{limit}/{offset}")
    public List<Signs> querySigns(@PathVariable Long limit, @PathVariable Long offset){
        return signsService.query(limit, offset);
    }

    /**
     * upload single signs info
     * @param temperature
     * @return
     */
    @GetMapping("/save/{temperature}")
    @ApiOperation("save")
    public int saveSigns(@PathVariable int temperature){

        return signsService.save(temperature);
    }

    /**
     * upload multi signs info
     * @param signsList
     * @return
     */
    @PostMapping("/batch")
    public int batchSaveSigns(@RequestBody List<Signs> signsList){

        return signsService.save(signsList);
    }

    @PostMapping("/save")
    @ApiOperation("save")
    public int save(@RequestBody Rule rule){

        return ruleMapper.insert1(rule);
    }

    @PostMapping("/test")
    @ApiOperation("test")
    public int test(@RequestParam String type){
        deviceDataService.getFunctionStatus(type);
        return 0;
    }

    @PostMapping("/setweather")
    public void setWeather() throws UnsupportedEncodingException {
        Map map = new HashMap<>();
        map.put("时间",0);
        map.put("地点","北京海淀区");
        map.put("天气",0);
        map.put("天气内容","天冷注意保暖");
        map.put("最低温度",0);
        map.put("最高温度",0);
        map.put("当前温度",0);
        map.put("紫外线强度",0);
        map.put("风力",0);
        map.put("风向",0);
        map.put("空气质量",99);
        deviceDataService.setWeather(map);
    }

    @PostMapping("/setReminder")
    public void setReminder() {
        Map map = new HashMap();
        map.put("提醒类型",1);
        map.put("提醒时间总数",1);
        map.put("提醒时间","");
        map.put("星期重复","");
        map.put("提醒名称","大郎，该吃药了");
        deviceDataService.setReminder(map);
    }
}
