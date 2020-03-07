package com.github.wxiaoqi.security.common.entity.admin;

import javax.persistence.Column;
import javax.persistence.Table;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;

import lombok.Data;

@Table(name = "base_sms_config")
@Data
public class BaseSmsConfig extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 白标(交易所)Id
     */
    @Column(name = "white_exch_id")
    private Long whiteExchId;

    /**
     * 国家编号(0086)
     */
    @Column(name = "country_code")
    private String countryCode;

    /**
     * 短信平台地址
     */
    @Column(name = "sms_plat_url")
    private String smsPlatUrl;

    /**
     * 短信平台帐号
     */
    @Column(name = "sms_plat_account")
    private String smsPlatAccount;

    /**
     * 短信平台密码
     */
    @Column(name = "sms_plat_password")
    private String smsPlatPassword;

    /**
     * 发送短信签名
     */
    @Column(name = "sender_signature")
    private String senderSignature;

    /**
     * 备注
     */
    @Column(name = "remark_")
    private String remark;


}