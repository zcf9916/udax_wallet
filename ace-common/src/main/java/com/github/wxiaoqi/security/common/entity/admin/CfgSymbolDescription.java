package com.github.wxiaoqi.security.common.entity.admin;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import lombok.Data;

import java.util.Date;
import javax.persistence.*;
@Data
@Table(name = "cfg_symbol_description")
public class CfgSymbolDescription extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 代币
     */
    private String symbol;


    /**
     * 交易所id
     */
    @Column(name = "exchange_id")
    private Long exchangeId;

    /**
     * 描述信息
     */
    private String remark;

    /**
     * 币种提币描述信息
     */
    @Column(name = "withdraw_desp")
    private String withdrawDesp;

    /**
     * 币种充值描述
     */
    @Column(name = "recharge_desp")
    private String rechargeDesp;

    /**
     * 用户与用户之间的转账描述信息
     */
    @Column(name = "transfer_desp")
    private String transferDesp;

    @Column(name = "language_type")
    private String languageType;


    private String protocolType;

    private Integer isShow;

    @Transient
    private WhiteExchInfo exchInfo;
}