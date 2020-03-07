package com.github.wxiaoqi.security.common.entity.admin;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import lombok.Data;

@Table(name = "base_email_template")
@Data
public class BaseEmailTemplate extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 邮件标题
     */
    @Column(name = "email_title")
    private String emailTitle;

    /**
     * 模板名称
     */
    @Column(name = "template_name")
    private String templateName;

    /**
     * 白标(交易所)Id
     */
    @Column(name = "white_exch_id")
    private Long whiteExchId;

    @Column(name = "remark_")
    private String remark;

    /**
     * 模板内容
     */
    private String template;

    @Transient
    private String exchName;


}