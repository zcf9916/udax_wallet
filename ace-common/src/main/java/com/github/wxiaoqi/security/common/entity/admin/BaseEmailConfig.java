package com.github.wxiaoqi.security.common.entity.admin;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import lombok.Data;

@Table(name = "base_email_config")
@Data
public class BaseEmailConfig extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;

    /**
     * SMTP服务器
     */
    @Column(name = "smtp_host")
    private String smtpHost;

    /**
     * SMTP服务器端口
     */
    @Column(name = "smtp_port")
    private String smtpPort;

    /**
     * 名称
     */
    @Column(name = "sender_name")
    private String senderName;

    /**
     * 发邮件邮箱账号
     */
    @Column(name = "sender_account")
    private String senderAccount;

    /**
     * 发邮件邮箱密码
     */
    @Column(name = "sender_password")
    private String senderPassword;

    /**
     * 白标(交易所)Id
     */
    @Column(name = "white_exch_id")
    private Long whiteExchId;

    @Column(name = "remark_")
    private String remark;

    @Transient
    private  String exchName;

}