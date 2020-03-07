package com.github.wxiaoqi.security.common.entity.ud;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import com.github.wxiaoqi.security.common.constant.Constants;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "h_commission_detail")
@Getter
@Setter
public class HCommissionDetail extends BaseEntity {

    @Column(name = "user_id")
    private Long userId;


    @Column(name = "receive_user_id")
    private Long receiveUserId;


    @Column(name = "order_no")
    private String orderNo;

    /**
     * 用户订单总利润
     */
    @Column(name = "order_profit")
    private BigDecimal orderProfit;

    /**
     * 对应的收益配置表的id
     */
    @Column(name = "bonus_config_id")
    private Long bonusConfigId;

    /**
     * 对应的利润分配比例
     */
    @Column(name = "profit_rate")
    private BigDecimal profitRate;

    /**
     * 实际可以分配的利润
     */
    private BigDecimal profit;


    /**
     * 方案名称
     */
    @Column(name = "level_name")
    private String levelName;

    /**
     * 申购等级
     */
    @Column(name = "level_id")
    private Long levelId;

    /**
     * 申购总量
     */
    private BigDecimal amount;

    //0 用户利润分成   1 平台利润分成
    private Integer type;

    @Column(name = "create_time")
    private Date createTime;

    /**
     * 申购人
     */
    @Transient
    private String userName;
   /**
     * 收益用户
     */
    @Transient
    private String receiveUserName;

    /**
     * 收益币种
     */
    private String symbol;

    /**
     * 申购总量
     */
    @Transient
    private BigDecimal totalAmount;

    /**
     * 总利润
     */
    @Transient
    private BigDecimal totalOrderProfit;

    /**
     * 申购手续费
     */
    @Transient
    private BigDecimal purchaseFee;
    /**
     * 平台总收益
     */
    @Transient
    private BigDecimal platformTotalProfit = BigDecimal.ZERO;
    /**
     * 用户总收益
     */
    @Transient
    private BigDecimal userTotalProfit = BigDecimal.ZERO;


    @Transient
    private String exchName;//白标名称

}