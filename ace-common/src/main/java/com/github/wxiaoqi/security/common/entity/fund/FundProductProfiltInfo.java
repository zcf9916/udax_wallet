package com.github.wxiaoqi.security.common.entity.fund;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "fund_product_profilt_info")
@Getter
@Setter
public class FundProductProfiltInfo extends BaseEntity {


    //白标ID
    private Long exchangeId;

    /**
     * 基金产品Id
     */
    @Column(name = "fund_id")
    private Long fundId;

    /**
     * 交易队代码
     */
    private String symbol;

    /**
     * 实际收益率
     */
    @Column(name = "curr_profilt")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal currProfilt;

//    /**
//     * 当前净值
//     */
//    @Column(name = "curr_one_worth")
//    private BigDecimal currOneWorth;

    /**
     * 年化收益率
     */
    @Column(name = "year_profilt")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal yearProfilt;

    @Column(name = "update_time")
    private Date updateTime;

}