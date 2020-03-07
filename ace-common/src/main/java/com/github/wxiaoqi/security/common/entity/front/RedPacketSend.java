package com.github.wxiaoqi.security.common.entity.front;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "red_packet_send")
@Getter
@Setter
public class RedPacketSend extends BaseEntity {

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 订单号
     */
    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "group_id")
    private String groupId;
    /**
     * 红包类型 0. 普通红包  1. 随机红包
     */
    private Integer type;

    /**
     * 0 个人红包   1 群红包
     */
    @Column(name = "send_type")
    private Integer sendType;

    /**
     * 红包个数
     */
    private Integer num;

    /**
     * 红包已抢个数
     */
    @Column(name = "current_num")
    private Integer currentNum;

    /**
     * 红包已抢金额
     */
    @Column(name = "current_amout")
    private BigDecimal currentAmout;

    /**
     * 总金额
     */
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    /**
     * 返还总金额
     */
    @Column(name = "return_amount")
    private BigDecimal returnAmount;

    /**
     * 利润结算状态: 0-已生成,1-已抢完  2.全部退还  3.部分退还
     */
    private Integer status;

    /**
     * 记录生成时间
     */
    @Column(name = "create_time")
    private Date createTime;


    /**
     * 个人红包收款用户Id
     */
    @Column(name = "receive_user_id")
    private Long receiveUserId;

    private String remark;

    private String symbol;

}