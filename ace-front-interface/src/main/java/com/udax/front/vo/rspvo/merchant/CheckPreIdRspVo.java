package com.udax.front.vo.rspvo.merchant;

import java.io.Serializable;
import java.util.List;

import com.udax.front.vo.reqvo.merchant.MchPayTokenModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckPreIdRspVo implements Serializable {


    private String merchantName;//商户名

    private String prepayId;//预支付交易会话标识


//    @JsonSerialize(using = BigDecimalCoinSerializer.class)
//    private BigDecimal amount;//交易类型   详见MchTradeType
//
//    private String symbol;//代币
    
    private List<MchPayTokenModel> tokenList;

    private String sign;//签名信息

    private String body;//商品描述

}