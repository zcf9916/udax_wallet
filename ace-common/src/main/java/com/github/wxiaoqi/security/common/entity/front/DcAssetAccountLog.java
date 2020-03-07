package com.github.wxiaoqi.security.common.entity.front;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "dc_asset_account_log")
@Getter
@Setter
public class DcAssetAccountLog extends BaseEntity {


    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 变动币种
     */
    private String symbol;

    /**
     * 0:收入  1:支出
     */
    private Integer direction;

    /**
     * 1.转账给平台其他用户 2.收到转账  3.转入基金账户 4.从基金账户转出
5.提现  6.转换币 7.转换币手续费 8. 充值 9.用户给商户支付款项
10.商户收到用户支付的款项 
     */
    private Integer type;

    /**
     * 实际收入/支出数量
     */
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal amount;

    /**
     * 交易涉及到的手续费‌‌
     */
    @Column(name = "charge_amount")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal chargeAmount;

    /**
     * 交易涉及到的手续费‌‌币种
     */
    @Column(name = "charge_symbol")
    private String chargeSymbol;

    /**
     * 流水生成时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 变动前总资产
     */
    @Column(name = "pre_total")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal preTotal;

    /**
     * 变动前可用资产
     */
    @Column(name = "pre_available")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal preAvailable;

    /**
     * 变动前冻结资产
     */
    @Column(name = "pre_freeze")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal preFreeze;

    /**
     * 变动前待结算资产
     */
    @Column(name = "pre_waitconfirm")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal preWaitconfirm;

    /**
     * 变动后总资产
     */
    @Column(name = "after_total")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal afterTotal;

    /**
     * 变动后可用资产
     */
    @Column(name = "after_available")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal afterAvailable;

    /**
     * 变动后冻结资产
     */
    @Column(name = "after_freeze")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal afterFreeze;

    /**
     * 变动后待结算资产
     */
    @Column(name = "after_waitconfirm")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal afterWaitconfirm;


    @Column(name = "trans_no")
    @JsonSerialize(using = ToStringSerializer.class)
    private String transNo;


    @Column(name = "usdt_rate")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal usdtRate;


    @Column(name = "usdt_amount")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal usdtAmount;


    private String remark;

    @Transient
    private String userName;

    @Transient
    private String mobile;

}