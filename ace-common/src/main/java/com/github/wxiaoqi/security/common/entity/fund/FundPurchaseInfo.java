package com.github.wxiaoqi.security.common.entity.fund;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.base.BaseEntity;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "fund_purchase_info")
@Getter
@Setter
public class FundPurchaseInfo extends BaseEntity {


    //白标ID
    private Long exchangeId;

    /**
     * 申购单号
     */
    @Column(name = "order_no")
    private Long orderNo;

    /**
     * 基金产品Id
     */
    @Column(name = "fund_id")
    private Long fundId;

    /**
     * 基金名称
     */
    @Column(name = "fund_name")
    private String fundName;

    /**
     * 用户Id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 代币代码
     */
    @Column(name = "dc_code")
    private String dcCode;

    /**
     * 申购数量
     */
    @Column(name = "order_volume")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal orderVolume;

    /**
     * 申购手续费
     */
    @Column(name = "order_chrge")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal orderChrge;

    /**
     * 返还的总数量
     */
    @Column(name = "return_volume")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal returnVolume;

    /**
     * 收益数量(可能为负数)
     */
    @Column(name = "profilt_volume")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal profiltVolume;

    /**
     * 收益率
     */
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal yield;

//    /**
//     * 申购时净值
//     */
//    @Column(name = "order_one_worth")
//    private BigDecimal orderOneWorth;
//
//    /**
//     * 当前净值
//     */
//    @Column(name = "curr_one_worh")
//    private BigDecimal currOneWorh;

    /**
     * 1.已认购; 2.运行中; 3.结算中; 4.已结算
     */
    private Integer status;

    @Column(name = "order_time")
    private Date orderTime;

    /**
     * 结算时间
     */
    @Column(name = "settle_time")
    private Date settleTime;

    @Column(name = "update_time")
    private Date updateTime;

    @Transient
    private FrontUser frontUser;

}