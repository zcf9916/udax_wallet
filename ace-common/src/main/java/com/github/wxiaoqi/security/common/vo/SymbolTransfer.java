package com.github.wxiaoqi.security.common.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.entity.admin.CfgChargeTemplate;
import lombok.Data;

import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * 交易对权限分配vo
 */
@Data
public class SymbolTransfer {

    private Long id;
    private String srcSymbol;
    private String dstSymbol;
    private Long transferId;
    private Integer isOpen;//是否允许交易

    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal maxTransAmount;

    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal minTransAmount;

    @Transient
    private CfgChargeTemplate charge;
}
