package com.github.wxiaoqi.security.common.entity.merchant;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.base.BaseEntity;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Table(name = "mch_trade_detail")
public  class MchTradeDetail extends BaseEntity {
    /**
     * 1.钱包订单号
     */
    @Column(name = "wallet_order_no")
    private String walletOrderNo;

    /**
     * 商户订单号
     */
    @Column(name = "mch_order_no")
    private String mchOrderNo;

    /**
     * 商户号
     */
    @Column(name = "mch_no")
    private Long mchNo;

    /**
     * 商户id
     */
    @Column(name = "mch_id")
    private Long mchId;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Long userId;


    /**
     * 商户对应用户ID
     */
    @Column(name = "mch_user_id")
    private Long  mchUserId;


    /**
     * 代币代码
     */
    private String symbol;

    /**
     * 订单代币数量
     */
    private BigDecimal amount;
    
    /**
     * 组合支付代币集合
     */
    @Transient
    private List<MchPayToken> tokenList;


    /**
     * 订单已退款数量
     */
    private BigDecimal refundAmount;

    /**
     * 手续费代币代码
     */
    @Column(name = "charge_symbol")
    private String chargeSymbol;


    /**
     * 手续费
     */
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    @Column(name = "charge_amount")
    private BigDecimal chargeAmount;
    /**
     * 商品描述
     */
    private String body;

    private String ip;

    @Column(name = "actual_ip")
    private String actualIp;

    @Column(name = "trade_type")
    private String tradeType;

    @Column(name = "notify_url")
    private String notifyUrl;

    /**
     * //1.待付款;2.已付款;3. 已付款,部分退款中;4.已付款,全额退款
     */
    private Integer status;

    /**
     * 生成订单时间
     */
    @Column(name = "order_time")
    private Date orderTime;

    /**
     * 订单失效时间
     */
    @Column(name = "expire_time")
    private Date expireTime;

    /**
     * 订单更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;


    //随机字符串
    @Column(name = "nonce_str")
    private String nonceStr;

    /**
     * 交易用户信息
     */
    @Transient
    private FrontUser frontUser;

    /**
     * 商家用户信息
     */
    @Transient
    private FrontUser mchFrontUser;

    //结算状态  0 未结算  1已结算
    @Column(name = "settle_status")
    private Integer settleStatus;

    //结算时间
    @Column(name = "settle_time")
    private Date settleTime;
}

