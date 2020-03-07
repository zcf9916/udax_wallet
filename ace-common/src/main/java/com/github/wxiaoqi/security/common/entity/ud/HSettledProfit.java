package com.github.wxiaoqi.security.common.entity.ud;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "h_settled_profit")
@Getter
@Setter
public class HSettledProfit extends BaseEntity {


    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 对应订单号
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     *     结算状态  0.未汇总完数据  1.已汇总完数据待结算  2.已分配用户利润并记录其它用户的分成记录
     */
    private Integer status;

    /**
     * 冻结金额
     */
    @Column(name = "freeze_amount")
    private BigDecimal freezeAmount;

    /**
     * 冻结利润
     */
    @Column(name = "freeze_profit")
    private BigDecimal freezeProfit;

    private String symbol;


    /**
     * 是否进行下一轮的排队了    0 否  1 是
     */
    @Column(name = "if_queue_next_order")
    private Integer ifQueueNextOrder;

    @Column(name = "update_time")
    private Date updateTime;


    @Column(name = "level_id")
    private Long levelId;



    /**
     * 方案名称
     */
    @Column(name = "level_name")
    private String levelName;
}