package com.edgeMapper.EdgeMapper.dao.mysql;


import com.edgeMapper.EdgeMapper.model.domain.Rule;

import java.util.List;

/**
 * Created by huqiaoqian on 2020/11/24
 */

public interface RuleMapper  {

    public int insert1(Rule rule);

    public List<Rule> selectAll();
}
