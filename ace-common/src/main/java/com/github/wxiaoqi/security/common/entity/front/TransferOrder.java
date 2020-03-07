package com.github.wxiaoqi.security.common.entity.front;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Table(name = "transfer_order")
@Getter
@Setter
public class TransferOrder extends BaseEntity {

    /**
     * 转账用户Id
     */
    @Column(name = "user_id")
    private Long userId;



    /**
     * 收款用户名
     */
    @Column(name = "user_name")
    private String userName;

    @Transient
    private FrontUser currentUser;

    /**
     * 订单号
     */
    @Column(name = "order_no")
    private String orderNo;


    /**
     * 收款用户Id
     */
    @Column(name = "receive_user_id")
    private Long receiveUserId;

    @Transient
    private FrontUser receiveUser;

    /**
     * 收款用户名
     */
    @Column(name = "receive_user_name")
    private String receiveUserName;

    /**
     * 代币
     */
    @Column(name = "symbol")
    private String symbol;

    /**
     * 代币数量
     */
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal amount;


    /**
     *  手续费数量
     */
    @Column(name = "charge_amount")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal chargeAmount;



    /**
     *  实际到账数量
     */
    @Column(name = "arrival_amount")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal arrivalAmount;

    /**
     * 订单状态 0:待转账  1:转账成功
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 记录生成时间
     */
    @Column(name = "create_time")
    private Date createTime;
    /**
     * 记录生成时间
     */
    @Column(name = "expire_time")
    private Date expireTime;


    /**
     * 订单完成时间
     */
    @Column(name = "update_time")
    private Date updateTime;


    private Integer type;

    /**
     * 转账备注
     */
    private String remark;

    /**
     * 0:未生成结算记录   1:已生成结算记录
     */
    @Column(name = "settle_status")
    private Integer settleStatus;

}