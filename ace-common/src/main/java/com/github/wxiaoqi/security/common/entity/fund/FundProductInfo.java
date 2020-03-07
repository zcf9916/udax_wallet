package com.github.wxiaoqi.security.common.entity.fund;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "fund_product_info")
@Getter
@Setter
public class FundProductInfo extends BaseEntity {


    //白标ID
    private Long exchangeId;

    /**
     * 基金产品id
     */
    @Column(name = "fund_id")
    @JsonSerialize(using= ToStringSerializer.class)
    private Long fundId;

    /**
     * 产品名称
     */
    @Column(name = "fund_name")
    private String fundName;

    /**
     * 策略
     */
    @Column(name = "strategy_id")
    private Long strategyId;


    /**
     * 策略
     */
    @Transient
    private FundStrategy strategy;

    /**
     * 认购费率
     */
    @Column(name = "subscripe_rate")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal subscripeRate;

//    /**
////     * 1.现货;2.期货;3.期权
////     */
////    @Column(name = "invest_target")
////    private Integer investTarget;

    /**
     * 代币代码
     */
    @Column(name = "dc_code")
    private String dcCode;

    /**
     * 预期规模
     */
    @Column(name = "expect_scale")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal expectScale;

    /**
     * 预期收益,百分比
     */
    @Column(name = "expect_profit")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal expectProfit;

    /**
     * 最小申购数量
     */
    @Column(name = "min_buy_num")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal minBuyNum;

    /**
     * 实际规模
     */
    @Column(name = "actual_scale")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal actualScale;

    /**
     * 投资者分成比例
     */
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal proportion;

    /**
     * 1.可以超额认购; 2.不允许超额认购
     */
    @Column(name = "over_range")
    private Integer overRange;

    /**
     * 1.已发布;2.募集中;3.申购结束;4.已启动;5.清盘中;6.已清盘
     */
    private Integer status;

    /**
     * 管理团队信息id
     */
    @Column(name = "manager_id")
    private Long managerId;


    /**
     * 管理团队信息id
     */
    @Transient
    private FundManageInfo manageInfo;

    /**
     * 发布时间
     */
    @Column(name = "publish_time")
    private Date publishTime;

    /**
     * 申购开始时间
     */
    @Column(name = "buy_starttime")
    private Date buyStarttime;

    /**
     * 申购结束时间
     */
    @Column(name = "buy_endtime")
    private Date buyEndtime;

    /**
     * 封闭开始时间
     */
    @Column(name = "cycle_starttime")
    private Date cycleStarttime;

    /**
     * 封闭结束时间
     */
    @Column(name = "cycle_endtime")
    private Date cycleEndtime;

    /**
     * 运行开始时间
     */
    @Column(name = "run_starttime")
    private Date runStarttime;

    /**
     * 运行结束时间
     */
    @Column(name = "run_endtime")
    private Date runEndtime;

    /**
     * 清盘时间
     */
    @Column(name = "clear_time")
    private Date clearTime;

    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 0.禁用;1启用
     */
    private Integer enable;

    /**
     * 策略类型名称  后台产品列表展示
     */
    @Transient
    private String strategyType;
    /**
     * 实际收益率
     */
    @Transient
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal currProfilt;
    /**
     * 到期实际盈亏
     */
    @Transient
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal currProfiltAmount;

    /**
     * 投资人数
     */
    @Transient
    private Long totalInvestors;

    /**
     * 申购订单总数
     */
    @Transient
    private Long totalOrder;

    public void setCurrProfiltAmount(BigDecimal currProfiltAmount) {
        this.currProfiltAmount = currProfiltAmount;
    }

    public BigDecimal getCurrProfiltAmount() {
        return this.actualScale != null && this.currProfilt != null
                ? this.actualScale.multiply(this.currProfilt).setScale(8, BigDecimal.ROUND_UP)
                : null;
    }

}