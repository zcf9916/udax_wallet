package com.github.wxiaoqi.security.common.entity.ud;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import com.github.wxiaoqi.security.common.constant.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "h_purchase_level")
@Getter
@Setter
public class HPurchaseLevel extends BaseAdminEntity {

    /**
     * 申购名称
     */
    private String name;

    /**
     * 申购金额
     */
    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal amount;

    /**
     * 触发匹配数量人数
     */
    @Column(name = "trigger_num")
    private Integer triggerNum;

    /**
     * 匹配人数
     */
    @Column(name = "match_num")
    private Integer matchNum;

    /**
     * 已结算总投入金额限制(低于这个数量的不可玩)
     */
    @Column(name = "amount_limit")
    @JsonSerialize(using = BigDecimalCoinSerializer.class)

    private BigDecimal amountLimit;

    /**
     * 日息
     */
    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal interest;

    /**
     * 是否开放 0:关闭,1:开放
     */
    @Column(name = "is_open")
    private Integer isOpen;

    /**
     * 方案运行天数
     */
    @Column(name = "run_time")
    private Integer runTime;

    /**
     * 客户排单最长等待期限(天)
     */
    @Column(name = "wait_time")
    private Integer waitTime;

    /**
     * 下一轮的最早开始时间
     */
    @Column(name = "earliest_start_time")
    private Date earliestStartTime;

    //描述
    private String desp;

    @Transient
    private String exchName;

    private Long exchId;

    //币种
    private String symbol;
    /**
     * 对应字典UD_LEVEL 申购等级
     */

    private Integer level;
}