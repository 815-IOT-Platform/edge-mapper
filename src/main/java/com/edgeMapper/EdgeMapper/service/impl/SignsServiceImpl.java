package com.edgeMapper.EdgeMapper.service.impl;

import com.edgeMapper.EdgeMapper.dao.tdengine.SignsMapper;
import com.edgeMapper.EdgeMapper.model.domain.Signs;
import com.edgeMapper.EdgeMapper.service.SignsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by huqiaoqian on 2020/12/9
 */
@Service
@Transactional(transactionManager = "tdengineTransactionManager")
public class SignsServiceImpl implements SignsService {
    @Autowired
    private SignsMapper signsMapper;
    @Override
    public boolean init() {
        signsMapper.createDB();
        signsMapper.createTable();

        return true;

    }

    @Override
    public List<Signs> query(Long limit, Long offset) {
        return signsMapper.select(limit, offset);
    }

    @Override
    public int save(int temperature) {
        Signs signs=new Signs();
        signs.setTemperature(temperature);
        return signsMapper.insert(signs);

    }

    @Override
    public int save(List<Signs> signsList) {
        return signsMapper.batchInsert(signsList);
    }
}
