package com.github.wxiaoqi.security.common.entity.admin;

import javax.persistence.Column;
import javax.persistence.Table;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;

import lombok.Getter;
import lombok.Setter;

@Table(name = "base_element")
@Getter
@Setter
public class Element extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;

    private String code;

    private String type;

    private String name;

    private String uri;

    @Column(name = "menu_id")
    private String menuId;

    @Column(name = "parent_id")
    private Long parentId;

    private String path;

    private String method;

    private String description;

    @Column(name = "language_type")
    private String languageType;


}