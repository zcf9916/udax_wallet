package com.github.wxiaoqi.security.common.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.entity.admin.CfgChargeTemplate;
import lombok.Data;

import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * 币种权限分配vo
 */
@Data
public class SymbolCurrencyCharge {

    private Long id;

    private String symbol;

    private String protocolType;

    private Integer isShow;

    @Transient
    private CfgChargeTemplate tradeCharge;

    @Transient
    private CfgChargeTemplate dcWithdrawCharge;

    @Transient
    private CfgChargeTemplate ltWithdrawCharge;

    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal spread;
}
