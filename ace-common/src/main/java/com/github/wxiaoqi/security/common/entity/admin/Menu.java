package com.github.wxiaoqi.security.common.entity.admin;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;

import lombok.Getter;
import lombok.Setter;

@Table(name = "base_menu")
@Setter
@Getter
public class Menu extends BaseAdminEntity {

    private String code;

    private String title;

    @Column(name = "parent_id")
    private Long parentId = AdminCommonConstant.ROOT;

    private String href;

    private String icon;

    private String type;

    private String description;

    /**
     * 语言类型
     */
    @Column(name = "language_type")
    private String languageType;

    private String path;

    private String attr1;

    @Transient
    private String languageTitle;
}