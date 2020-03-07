package com.github.wxiaoqi.security.common.entity.admin;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;

@Table(name = "transfer_exch")
@Data
public class TransferExch extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;

    private Long exchId;

    /**
     * 货币转换配置表id
     */
    private Long transferId;
    /**
     * 交易所配置的交易手续费
     */
    private Long chargeId;

    @Transient
    private CfgChargeTemplate cfgChargeTemplate;

    @Transient
    private String srcSymbol;

    @Transient
    private String dstSymbol;

    private Integer isOpen; //1 开启(默认) 0 禁用


    /**
     * 原货币单次最小转币量
     */
    @Column(name = "min_trans_amount")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal minTransAmount;

    /**
     * 原货币单次最大转币量
     */
    @Column(name = "max_trans_amount")
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal maxTransAmount;

}