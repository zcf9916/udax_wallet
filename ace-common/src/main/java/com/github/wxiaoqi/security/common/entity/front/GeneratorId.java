package com.github.wxiaoqi.security.common.entity.front;

import javax.persistence.Column;
import javax.persistence.Table;

import com.github.wxiaoqi.security.common.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Table(name = "generator_id")
@Getter
@Setter
public class GeneratorId extends BaseEntity {
    /**
     * 自增序列KEY值
     */
    private String k;

    /**
     * 自增序列VALUE值
     */
    private String v;

    /**
     * 自增的步长
     */
    @Column(name = "incre_len")
    private Integer increLen;

    /**
     * 乐观锁版本
     */
    @Column(name = "VERSION")
    private Integer version;

    /**
     * 级别描述
     */
    @Column(name = "remark_")
    private String remark;


}