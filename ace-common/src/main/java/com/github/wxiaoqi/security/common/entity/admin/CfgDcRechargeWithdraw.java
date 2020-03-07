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

@Getter@Setter
@Table(name = "cfg_dc_recharge_withdraw")
public class CfgDcRechargeWithdraw extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 货币代码
     */
    private String symbol;

    /**
     * 1.正常;0.停用;
     */
    private Integer status;

    /**
     * 充币状态 0.可用;1.停用
     */
    @Column(name = "recharge_status")
    private Integer rechargeStatus;

    /**
     * 提币状态 0.可用;1.停用
     */
    @Column(name = "withdraw_status")
    private Integer withdrawStatus;

    /**
     * 最小提币数量
     */
    @Column(name = "min_withdraw_amount")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal minWithdrawAmount;

    /**
     * 最大提币数量
     */
    @Column(name = "max_withdraw_amount")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal maxWithdrawAmount;

    /**
     * 用户与用户之间转账最小数量限制
     */
    @Column(name = "min_transfer_amount")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal minTransferAmount;

    /**
     * 最小充币数量
     */
    @Column(name = "min_recharge_amount")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal minRechargeAmount;
    /**
     * 当日最大提币数量
     */
    @Column(name = "max_withdraw_day")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal maxWithdrawDay;

    private String remark;

    private Long exchId;

    private Integer isShow;

    private Integer systemConfig;

    private String protocolType;

    @Transient
    private WhiteExchInfo info;

    @Override
    public String toString() {
        return "CfgDcRechargeWithdraw{" +
                "symbol='" + symbol + '\'' +
                ", status=" + status +
                ", rechargeStatus=" + rechargeStatus +
                ", withdrawStatus=" + withdrawStatus +
                ", minWithdrawAmount=" + minWithdrawAmount +
                ", maxWithdrawAmount=" + maxWithdrawAmount +
                ", minTransferAmount=" + minTransferAmount +
                ", minRechargeAmount=" + minRechargeAmount +
                ", maxWithdrawDay=" + maxWithdrawDay +
                ", remark='" + remark + '\'' +
                ", exchId=" + exchId +
                ", isShow=" + isShow +
                ", systemConfig=" + systemConfig +
                ", protocolType='" + protocolType + '\'' +
                ", info=" + info +
                '}';
    }
}