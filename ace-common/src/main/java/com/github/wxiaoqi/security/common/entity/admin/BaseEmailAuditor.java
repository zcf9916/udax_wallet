package com.github.wxiaoqi.security.common.entity.admin;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import lombok.Data;

@Table(name = "base_email_auditor")
@Data
public class BaseEmailAuditor extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 审核人员名字
     */
    @Column(name = "auditor_name")
    private String auditorName;

    /**
     * 审核人员角色(如提币审核管理员、用户审核管理员等)
     */
    @Column(name = "auditor_role")
    private String auditorRole;

    /**
     * 审核人员邮箱账号
     */
    @Column(name = "email_account")
    private String emailAccount;

    /**
     * 发送标题
     */
    @Column(name = "email_title")
    private String emailTitle;

    @Column(name = "remark_")
    private String remark;
    /**
     * 发送内容
     */
    @Column(name = "email_content")
    private String emailContent;
    /**
     * 发送内容
     */
    @Column(name = "white_exch_id")
    private Long whiteExchId;

    @Transient
    private String exchName;//白标名称
}