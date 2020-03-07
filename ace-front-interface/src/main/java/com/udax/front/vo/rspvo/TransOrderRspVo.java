package com.udax.front.vo.rspvo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransOrderRspVo {

    private String receiveUserName;
    private String symbol;
    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal amount;
    private String orderNo;
    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal arrivalAmount;//实际到账数量

    private String realName;//真实姓名

    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal chargeAmount;
}