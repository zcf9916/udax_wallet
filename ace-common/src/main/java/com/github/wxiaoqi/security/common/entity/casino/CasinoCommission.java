package com.github.wxiaoqi.security.common.entity.casino;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "casino_commission")
@Data
public class CasinoCommission extends BaseEntity {


    /**
     * 订单号 自动生成
     */
    @Column(name = "order_no")
    @JsonSerialize(using= ToStringSerializer.class)
    private Long orderNo;

    /**
     * 产生此订单用户
     */
    @Column(name = "userId")
    private Long userId;

    /**
     * 用户名称
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 开工模式:1:邀请返佣 2:与平台按比例分成 3:会员与客户对赌 
     */
    @Column(name = "method_type")
    private Integer methodType;

    /**
     * 总业绩
     */
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    /**
     * 会员分红 会员订单分红
     */
    @Column(name = "member_amount")
    private BigDecimal memberAmount;

    /**
     * 平台分红
     */
    @Column(name = "platform_amount")
    private BigDecimal platformAmount;

    /**
     * 直推分红
     */
    @Column(name = "straight_amout")
    private BigDecimal straightAmout;

    /**
     * 间接
     */
    @Column(name = "indirect_amout")
    private BigDecimal indirectAmout;

    /**
     * 总裁
     */
    @Column(name = "president_amount")
    private BigDecimal presidentAmount;

    /**
     * 经理
     */
    @Column(name = "manager_amount")
    private BigDecimal managerAmount;

    /**
     * 订单时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 订单时间时间戳
     */
    @Column(name = "order_time")
    private Long orderTime;

}