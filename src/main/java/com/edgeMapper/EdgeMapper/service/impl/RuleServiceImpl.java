package com.edgeMapper.EdgeMapper.service.impl;

import com.edgeMapper.EdgeMapper.dao.mysql.RuleMapper;
import com.edgeMapper.EdgeMapper.model.domain.Rule;
import com.edgeMapper.EdgeMapper.service.RuleService;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.FileWriter;
import java.util.List;
import java.util.Random;

/**
 * Created by huqiaoqian on 2020/12/10
 */
@Service
@Transactional(transactionManager = "mysqlTransactionManager")
public class RuleServiceImpl implements RuleService {
    @Resource
    private KieSession kieSession;

    @Resource
    private RuleMapper ruleMapper;
    /**
     * 决策表 先翻译成drl 后存储
     */
    @Override
    public  void excel1(Long userId) {
//        //把excel翻译成drl文件
        SpreadsheetCompiler compiler = new SpreadsheetCompiler();
        String path="table/"+String.valueOf(userId).substring(0,6)+".xlsx";
        String worksheetName="rule-"+String.valueOf(userId).substring(0,6)+"table";
        String drl = compiler.compile(ResourceFactory.newClassPathResource(path, "UTF-8"), worksheetName);
        System.out.println(drl);
        try {
            //规则持久化
            Rule rule=new Rule();
            rule.setContent(drl);
            rule.setRuleId(new Random().nextLong());//记得改成分布式id
            rule.setUserId(userId);
            rule.setStatus(0);//默认激活
            ruleMapper.insert1(rule);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void process(byte[] bytes) {
        List<Rule> rules = ruleMapper.selectAll();
        try {
            for (Rule rule : rules) {
                String fileName = "src/main/resources/rules/" + rule.getName() + ".drl";
                FileWriter writer = new FileWriter(fileName);
                writer.write("");//清空原文件内容
                writer.write(rule.getContent());
                writer.flush();
                writer.close();
            }
            //todo 根据字节流解析出DeviceDataDto，然后执行规则
//            PersonForExcel person = new PersonForExcel();
//            person.setName("Tony");
//            person.setAge(13);
//            kieSession.insert(person);
            int ruleFiredCount = kieSession.fireAllRules();
            System.out.println("触发了" + ruleFiredCount + "条规则");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
