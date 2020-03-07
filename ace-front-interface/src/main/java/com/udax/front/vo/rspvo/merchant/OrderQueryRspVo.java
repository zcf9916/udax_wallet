package com.udax.front.vo.rspvo.merchant;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class OrderQueryRspVo extends  BaseMchRspVo {


    private String mchOrderNo;//商户订单号
    @JsonSerialize(using = ToStringSerializer.class)
    private Long transNo;//钱包流水号

    private String tradeType;//交易类型   详见MchTradeType


    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal chargeAmount;//手续费数量

    private Integer status;

    @JsonSerialize(using = DateToTimeStampSerializer.class)
    private Date orderTime;//生成订单时间
}