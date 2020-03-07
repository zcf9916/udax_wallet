package com.github.wxiaoqi.security.common.entity.admin;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Table(name = "front_help_content")
@Setter
@Getter
public class FrontHelpContent extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 交易所ID
     */
    @Column(name = "exchange_id")
    private Long exchangeId;

    /**
     * 帮助类型id
     */
    @Column(name = "type_id")
    private Long typeId;

    /**
     * 帮助标题
     */
    @Column(name = "help_title")
    private String helpTitle;
    /**
     * 帮助内容
     */
    @Column(name = "help_content")
    private String helpContent;

    /**
     * 是否启用 1:启用，0:弃用
     */
    private Integer enable;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 0:pc 1:APP
     */
    @Column(name = "client_type")
    private Integer clientType;

    private String remark;

    @Transient
    private String typeName;
    /**
     *  语言
     */
    private String languageType;

    @Transient
    private String exchangeName;
}