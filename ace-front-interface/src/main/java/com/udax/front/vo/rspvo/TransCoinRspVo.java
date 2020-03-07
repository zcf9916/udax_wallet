package com.udax.front.vo.rspvo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.math.BigDecimal;

@Getter
@Setter
public class TransCoinRspVo {

    /**
     * 转币用户持有代币/接收用户接收币种
     */
    private String srcSymbol;

    /**
     * 转出币的数量
     */
    @JsonSerialize(using=BigDecimalCoinSerializer.class)
    private BigDecimal srcAmount;
    @JsonSerialize(using=BigDecimalCoinSerializer.class)
    private BigDecimal lastPrices;//最新价
    private String orderNo;//订单号

    /**
     * 转币用户待转代币/接收用户持有币种
     */
    private String dstSymbol;

    /**
     * 转换得到目标币的数量
     */
    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal dstAmount;

    private String chargeSymbol;

    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal chargeAmount;

}