package com.github.wxiaoqi.security.common.entity.admin;

import javax.persistence.Column;
import javax.persistence.Table;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;

import lombok.Getter;
import lombok.Setter;

@Table(name = "base_dept")
@Setter
@Getter
public class Dept extends BaseAdminEntity {

    /**
     * 父部门id
     */
    private Long pid;

    /**
     * 父级ids
     */
    private String pids;

    /**
     * 简称
     */
    @Column(name = "simple_name")
    private String simpleName;

    /**
     * 全称
     */
    @Column(name = "full_name")
    private String fullName;

    /**
     * 描述
     */
    private String description;

    /**
     * 版本（乐观锁保留字段）
     */
    private Integer version;

    /**
     * 排序
     */
    private Integer sort;

}