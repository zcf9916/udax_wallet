package com.github.wxiaoqi.security.common.entity.admin;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Table(name = "front_help_type")
@Setter
@Getter
public class FrontHelpType extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 交易所ID
     */
    @Column(name = "exchange_id")
    private Long exchangeId;

    /**
     * 帮助类型名称
     */
    @Column(name = "type_name")
    private String typeName;

    /**
     * 是否启用 1:启用，0:弃用
     */
    private Integer enable;

    private String remark;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 语言
     */
    private String languageType;
    /**
     * 交易所名称
     */
    @Transient
    private String exchangeName;

}