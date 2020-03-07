package com.github.wxiaoqi.security.common.entity.front;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "red_packet_log")
@Getter
@Setter
public class RedPacketLog extends BaseEntity {

    /**
     * 发红包用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 订单号
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 收红包用户id
     */
    @Column(name = "receive_user_id")
    private Long receiveUserId;

    /**
     * 红包类型 0. 普通红包  1. 随机红包
     */
    private Integer type;

    /**
     * 抢到的红包金额
     */
    private BigDecimal amount;

    /**
     * 总金额
     */
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    /**
     * 金额结算状态: 0-待入账,1-已入账
     */
    private Integer status;

    /**
     * 记录生成时间
     */
    @Column(name = "create_time")
    private Date createTime;
    private String symbol;
}