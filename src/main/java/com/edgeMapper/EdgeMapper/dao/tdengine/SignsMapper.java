package com.edgeMapper.EdgeMapper.dao.tdengine;

import com.edgeMapper.EdgeMapper.model.domain.Signs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by huqiaoqian on 2020/12/9
 */
public interface SignsMapper {
    int insert(Signs signs);

    int batchInsert(List<Signs> weatherList);

    List<Signs> select(@Param("limit") Long limit, @Param("offset") Long offset);

    void createDB();

    void createTable();

}
