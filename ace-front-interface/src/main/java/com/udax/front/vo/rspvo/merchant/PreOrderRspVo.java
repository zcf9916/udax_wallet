package com.udax.front.vo.rspvo.merchant;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.udax.front.vo.reqvo.merchant.OrderBaseModel;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class PreOrderRspVo extends BaseMchRspVo {
    private String mchOrderNo;//商户订单号

    private String prepayId;//预支付交易会话标识

    private String tradeType;//交易类型   详见MchTradeType


    private String symbol;//订单代币


    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal chargeAmount;//订单手续费数量


//    private String body;//商品描述
}