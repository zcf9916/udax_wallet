package com.github.wxiaoqi.security.common.entity.ifr;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "ifr_pay_order")
@Getter
@Setter
public class IfrPayOrder {
    @Id
    private Long id;

    @Column(name = "order_no")
    private String orderNo;

    /**
     * 活动id
     */
    @Column(name = "campaign_id")
    private String campaignId;

    /**
     * 邀请人id
     */
    @Column(name = "referral_id")
    private String referralId;

    /**
     * 邮箱
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 国家
     */
    private String country;
    /**
     * 币种符号
     */
    private String currency;

    /**
     * 充值数量
     */
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal units;

    /**
     * 换算的USD法币价值,需要加上点差
     */
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal amount;

    /**
     * 持股周期(对应字典表)
     */
    private Integer period;

    /**
     * 支付状态0:待支付,1:支付成功,2:支付失败
     */
    @Column(name = "pay_status")
    private Integer payStatus;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;


    @Transient
    private FrontUser frontUser;

}