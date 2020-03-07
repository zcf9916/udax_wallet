package com.github.wxiaoqi.security.common.entity.casino;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.wxiaoqi.security.common.base.BaseEntity;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
@Data
@Table(name = "casino_commission_log")
public class CasinoCommissionLog extends BaseEntity {

    public static enum CmsType{
        REBATE(1),//推荐返佣
        CMS(2);//业绩分成
        private Integer value;
        public Integer value(){return this.value;}
        CmsType(Integer value){
            this.value = value;
        }
    }


    /**
     * 订单号 自动生成
     */
    @Column(name = "order_no")
    @JsonSerialize(using= ToStringSerializer.class)
    private Long orderNo;

    /**
     * 业绩的直推用户ID
     */
    @Column(name = "direct_user_id")
    private Long directUserId;

    /**
     * 直推用户名
     */
    @Column(name = "direct_name")
    private String directName;

    /**
     * 结算方式  0 与平台按比例分成 1 会员与客户对赌
     */
    @Column(name = "settle_type")
    private Integer settleType;

    /**
     * 分到的业绩
     */
    private BigDecimal amount;

    /**
     * 总业绩
     */
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    /**
     * 分成类型  1 推荐返佣  2 业绩分成
     */
    @Column(name = "cms_type")
    private Integer cmsType;

    /**
     *   角色类型 * 1 直推用户 2间接推荐人 3白标分成 4 副总经理 5副总裁 6 会员自己
     */
    @Column(name = "role_type")
    private Integer roleType;


    /**
     * 分成比例
     */
    @Column(name = "cms_rate")
    private BigDecimal cmsRate;

    /**
     * 收取分成的用户id
     */
    @Column(name = "receive_user_id")
    private Long receiveUserId;

    /**
     * 收取分成的用户名
     */
    @Column(name = "receive_user_name")
    private String receiveUserName;

    /**
     * 白标ID
     */
    @Column(name = "exchange_id")
    private Long exchangeId;

    /**
     * 佣金的货币单位
     */
    private String currency;

    /**
     * 结算状态: 0:未结算.1:已结算.
     */
    @Column(name = "settle_status")
    private Integer settleStatus;

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


    @Transient
    private FrontUser frontUser;


}