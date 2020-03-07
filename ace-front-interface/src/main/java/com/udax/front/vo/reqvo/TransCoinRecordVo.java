package com.udax.front.vo.reqvo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@SuppressWarnings("serial")
@Getter
@Setter
public class TransCoinRecordVo implements Serializable{

    /**
     * 订单号
     */
    private String orderNo;


    /**
     * 转币用户持有代币/接收用户接收币种
     */
    private String srcSymbol;

    /**
     * 转出币的数量
     */
    @JsonSerialize( using = BigDecimalCoinSerializer.class)
    private BigDecimal srcAmount;


    /**
     * 转币用户待转代币/接收用户持有币种
     */
    private String dstSymbol;

    /**
     * 转换得到目标币的数量
     */
    @JsonSerialize( using = BigDecimalCoinSerializer.class)
    private BigDecimal dstAmount;

    /**
     * 收取的手续费币种
     */
    private String chargeCurrencyCode;

    /**
     * 收取的手续费金额
     */
    @JsonSerialize( using = BigDecimalCoinSerializer.class)
    private BigDecimal chargeAmount;

    /**
     * 交易价格
     */
    @JsonSerialize( using = BigDecimalCoinSerializer.class)
    private BigDecimal transPrice;


    /**
     * 成交时间
     */
    @JsonSerialize( using = DateToTimeStampSerializer.class)
    private Date updateTime;


    private String remark;

}
