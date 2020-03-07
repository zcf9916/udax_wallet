package com.udax.front.vo.rspvo.merchant;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RefundRspVo extends BaseMchRspVo {

    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal totalAmount;//原订单总金额

    private String transNo;//钱包流水号

    private String refundTransNo;//退款流水号

    private String mchOrderNO;//商户订单号

    private String mchRefundOrderNo;//商户退款订单号

}