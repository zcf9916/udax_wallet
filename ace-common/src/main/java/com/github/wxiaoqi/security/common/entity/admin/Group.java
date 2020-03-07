package com.github.wxiaoqi.security.common.entity.admin;

import javax.persistence.Column;
import javax.persistence.Table;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;

import lombok.Getter;
import lombok.Setter;

@Table(name = "base_group")
@Getter
@Setter
public class Group extends BaseAdminEntity {

    private String code;

    private String name;

    @Column(name = "parent_id")
    private Long parentId;

    private String path;

    private String type;

    @Column(name = "group_type")
    private Integer groupType = AdminCommonConstant.DEFAULT_GROUP_TYPE;

    private String description;

    @Column
    private String broker; //经纪商名称
}