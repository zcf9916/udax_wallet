package com.github.wxiaoqi.security.common.entity.ud;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import com.github.wxiaoqi.security.common.constant.Constants;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "h_order_detail")
@Getter
@Setter
public class HOrderDetail extends BaseEntity {

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 方案名称
     */
    @Column(name = "level_name")
    private String levelName;


    /**
     * 订单号
     */
    @Column(name = "order_no")
    private String orderNo;


    /**
     * 申购的方案Id
     */
    @Column(name = "level_id")
    private Long levelId;

    /**
     * 锁定金额
     */
    @Column(name = "lock_amount")
    private BigDecimal lockAmount;



    private String symbol;

    //实际产生的利润
    private BigDecimal profit;

    //手续费
    private BigDecimal charge;

    /**
     * 日息
     */
    @Column(name = "interest")
    private BigDecimal interest;

    /**
     * 订单生成(匹配)时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 申购时间
     */
    @Column(name = "purchase_time")
    private Date purchaseTime;

    /**
     * 结算天数
     */
    @Column(name = "settle_day")
    private Integer settleDay;

    @Column(name = "current_settle_day")
    private Integer currentSettleDay;

    /**
     * 利润结算状态: 0-未结算完成,1-利润已汇总完成  2.利润已结算完成
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 上一轮的订单id
     */
    @Column(name = "last_order_no")
    private String lastOrderNo;

    /**
     * 收益结算时间
     */
    @Transient
    private Date settleTime;
    /**
     * 申购人
     */
    @Transient
    private String userName;
    /**
     * 平台手续费
     */
    @Transient
    private BigDecimal paltformCharge;
   /**
     * 用户收益
     */
    @Transient
    private BigDecimal userProfit;

    @Transient
    private String exchName;//白标名称

    public Date getSettleTime() {

        return this.settleDay != null && this.createTime != null
                ? DateUtils.addDays(createTime,settleDay) : null;
    }

    public void setSettleTime(Date settleTime) {
        this.settleTime = settleTime;
    }



}