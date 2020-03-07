package com.github.wxiaoqi.security.common.entity.front;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.base.BaseEntity;
import com.github.wxiaoqi.security.common.entity.admin.DictData;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Table(name = "front_withdraw")
public class FrontWithdraw extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 提现流水号
     */
    @Column(name = "trans_no")
    private String transNo;

    /**
     * token地址
     */
    @Column(name = "user_address")
    private String userAddress;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 提现金额
     */
    @Column(name = "trade_amount")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal tradeAmount;

    /**
     * 区块链转账事物id
     */
    @Column(name = "transaction_id")
    private String transactionId;

    /**
     * 货币编码
     */
    private String symbol;

    /**
     * 基础代币编码
     */
    @Column(name = "basic_symbol")
    private String basicSymbol;

    /**
     * 提现手续费
     */
    @Column(name = "charge_amount")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal chargeAmount;
    /**
     *  是否自动提币
     *  1 :自动提币
     * 0: 手动审核
     */
    @Column(name = "auto_withdraw")
    private Integer autoWithdraw;

    /**
     * 到账金额
     */
    @Column(name = "arrival_amoumt")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal arrivalAmoumt;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 提现状态1:待审核,2:已审核待转账，3:已提现
     */
    private Integer status;

    /**
     * 1.普通提现  2.商户提现
     */
    private Integer type;

    /**
     * 商户订单号
     */
    @Column(name = "mch_order_no")
    private String mchOrderNo;

    //随机字符串
    @Column(name = "nonce_str")
    private String nonceStr;


    /**
     * 区块链汇聚手续费币种
     */
    @Column(name = "fee_symbol")
    private String feeSymbol;

    /**
     * 区块链汇聚手续费
     */
    @Column(name = "withdraw_fee")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal withdrawFee;

    private Integer confirmations;

    @Transient
    private FrontUserInfo userInfo;

    @Transient
    private FrontUser frontUser;

    @Transient
    private DictData dictData;

    private String protocolType;

    private String proxyCode;
}