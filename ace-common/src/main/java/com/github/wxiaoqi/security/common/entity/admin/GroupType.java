package com.github.wxiaoqi.security.common.entity.admin;

import javax.persistence.Table;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;

import lombok.Getter;
import lombok.Setter;

@Table(name = "base_group_type")
@Getter
@Setter
public class GroupType extends BaseAdminEntity {

    private String code;

    private String name;

    private String description;
}