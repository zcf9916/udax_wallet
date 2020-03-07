package com.github.wxiaoqi.security.common.entity.ud;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "h_daily_profit_detail")
@Getter
@Setter
public class HDailyProfitDetail extends BaseEntity {

    @Column(name = "user_id")
    private Long userId;

    /**
     * 方案名称
     */
    @Column(name = "level_name")
    private String levelName;

    @Column(name = "level_id")
    private Long levelId;

    /**
     * 收益
     */
    private BigDecimal profit;

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "create_time")
    private Date createTime;

    //对应的结算日期(与订单号构成唯一索引  防止重复计算)
    @Column(name = "settle_time")
    private Date settleTime;


    private String symbol;

}