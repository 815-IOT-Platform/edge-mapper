package com.edgeMapper.EdgeMapper.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by huqiaoqian on 2020/12/21
 */
@Data
public class BodyInfoDto implements Serializable {

    private static final long serialVersionUID = 4989795543209808944L;

    @ApiModelProperty("身高")
    private Integer height;

    @ApiModelProperty("体重")
    private Integer weight;

    @ApiModelProperty("性别")
    private Integer sex;

    @ApiModelProperty("年龄")
    private Integer age;
}
