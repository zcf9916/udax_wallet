package com.github.wxiaoqi.security.common.entity.fund;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Table(name = "fund_strategy")
@Getter
@Setter
public class FundStrategy extends BaseEntity {

    /**
     * 交易所ID
     */
    @Column(name = "exchange_id")
    private Long exchangeId;

    /**
     * 策略类型
     */
    @Column(name = "strategy_type")
    private String strategyType;

    /**
     * 策略描述
     */
    @Column(name = "strategy_info")
    private String strategyInfo;

    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 0.禁用;1启用
     */
    private Integer enable;


}