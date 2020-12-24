package com.edgeMapper.EdgeMapper.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by huqiaoqian on 2020/11/5
 */
@Data
public class SingleDataDto implements Serializable {
    private static final long serialVersionUID = 220557497566394356L;
    private String name;

    private String value;
}
