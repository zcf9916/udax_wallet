package com.github.wxiaoqi.security.common.entity.admin;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import lombok.Data;

import java.util.Date;
import javax.persistence.*;
@Data
@Table(name = "cfg_description_template")
public class CfgDescriptionTemplate extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "language_type")
    private String languageType;



    @Column(name = "upd_name")
    private String updName;

    /**
     * 描述信息
     */
    private String remark;

    /**
     * 通用的提币提示信息模板
     */
    @Column(name = "withdraw_desp")
    private String withdrawDesp;

    /**
     * 通用的充币描述信息模板
     */
    @Column(name = "recharge_desp")
    private String rechargeDesp;

    /**
     * 用户与用户之间的转账描述信息
     */
    @Column(name = "transfer_desp")
    private String transferDesp;

}