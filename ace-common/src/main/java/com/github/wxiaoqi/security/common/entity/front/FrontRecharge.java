package com.github.wxiaoqi.security.common.entity.front;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Table(name = "front_recharge")
public class FrontRecharge extends BaseEntity {

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
     * 充值金额
     */
    @Column(name = "recharge_amount")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal rechargeAmount;

    /**
     * 货币编码
     */
    private String symbol;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 充值状态0:充值成功,1:充值失败
     */
    private Integer status;

    @Column(name = "order_id")
    @JsonSerialize(using= ToStringSerializer.class)
    private Long orderId;

    /**
     * 区块链订单ID
     */
    @Column(name = "block_order_id")
    private String blockOrderId;

    /**
     * 区块链汇聚手续费币种
     */
    @Column(name = "fee_symbol")
    private String feeSymbol;

    private String protocolType;

    /**
     * 区块链汇聚手续费
     */
    @Column(name = "recharge_fee")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal rechargeFee;

    //1putong chognzhi   2 shanghu chongzhi 
    private Integer type;

    private String proxyCode;

    @Transient
    private FrontUser frontUser;
}