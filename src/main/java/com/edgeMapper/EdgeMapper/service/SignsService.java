package com.edgeMapper.EdgeMapper.service;


import com.edgeMapper.EdgeMapper.model.domain.Signs;

import java.util.List;

/**
 * Created by huqiaoqian on 2020/12/9
 */
public interface SignsService {

    public boolean init();

    public List<Signs> query(Long limit, Long offset);

    public int save(int temperature);

    public int save(List<Signs> weatherList);
}
