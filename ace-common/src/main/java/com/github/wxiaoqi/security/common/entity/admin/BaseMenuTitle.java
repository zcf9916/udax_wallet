package com.github.wxiaoqi.security.common.entity.admin;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
@Data
@Table(name = "base_menu_title")
public class BaseMenuTitle {
    @Id
    private Long id;

    /**
     * 名称
     */
    private String title;

    /**
     * 语言
     */
    @Column(name = "language_type")
    private String languageType;

    /**
     * 菜单id
     */
    @Column(name = "menu_id")
    private Long menuId;

}