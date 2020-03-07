package com.github.wxiaoqi.security.common.entity.admin;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.base.BaseAdminEntity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Table(name = "cfg_currency_charge")
public class CfgCurrencyCharge extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 对应用户级别
     *
     */
    @Column(name = "real_user_level")
    private Integer realUserLevel;

    /**
     * 1.数字货币; 2.法定货币;
     */
    @Column(name = "currency_type")
    private Integer currencyType;

    /**
     * 货币代码
     */
    @Column(name = "symbol")
    private String symbol;


    /**
     * 用户之间转账手续费id
     */
    @Transient
    private CfgChargeTemplate tradeCharge;


    /**
     * 提币手续费id
     */
    @Transient
    private CfgChargeTemplate dcWithdrawCharge;


    /**
     * 出金手续费id
     */
    @Transient
    private CfgChargeTemplate ltWithdrawCharge;


    private String remark;


    private String protocolType;

    private Integer isShow;
    /**
     * 货币主键
     */
    @Transient
    private BasicSymbol basicSymbol;

    /**
     * 点差
     */
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal spread;

    /**
     * 交易所id
     */
    private Long exchId;
}