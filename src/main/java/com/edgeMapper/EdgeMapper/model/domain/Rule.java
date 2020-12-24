package com.edgeMapper.EdgeMapper.model.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by huqiaoqian on 2020/11/18
 */
@Data
@Table(name = "rule")
public class Rule implements Serializable {
    private static final long serialVersionUID = 7583763575114594106L;
    @Id
    @Column(name = "id")
    private Long ruleId;

    /**
     * 规则名称
     */
    @Column(name = "name")
    private String name;

    @Column(name = "user_id")
    private Long userId;

    /**
     * 规则内容
     */
    @Column(name = "content")
    private String content;

    /**
     * 0：激活；1：关闭
     */
    @Column(name = "status")
    private Integer status;//是否激活，0：激活；1：关闭
}
