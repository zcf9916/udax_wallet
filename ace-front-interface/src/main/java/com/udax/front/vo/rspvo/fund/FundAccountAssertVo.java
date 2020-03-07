package com.udax.front.vo.rspvo.fund;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FundAccountAssertVo {


    private String dcCode; //币种代码

    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal totalAmount; //总数量

    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal availableAmount; //可用数量

    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal freezeAmount;//交易冻结

    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal bbAvailableAmount;//币币账户可用数量

//    private String assertValue;//资产净值
//
//    private String profit;//浮动盈亏





}
