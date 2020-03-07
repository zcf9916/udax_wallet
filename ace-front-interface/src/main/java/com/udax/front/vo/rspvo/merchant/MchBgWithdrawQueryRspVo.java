package com.udax.front.vo.rspvo.merchant;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class MchBgWithdrawQueryRspVo implements Serializable {

    /**
     * 提现流水号
     */
    private String transNo;

    /**
     * token地址
     */
    private String userAddress;



    /**
     * 提现金额
     */
    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal tradeAmount;

    /**
     * 区块链转账事物id
     */
    private String transactionId;

    /**
     * 货币编码
     */
    private String symbol;

    /**
     * 到账金额
     */
    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal arrivalAmoumt;

    @JsonSerialize(using = DateToTimeStampSerializer.class)
    private Date createTime;


    /**
     * 提现状态1:待审核,2:已审核待转账，3:已提现
     */
    private Integer status;

    /**
     * 1.普通提现  2.商户提现
     */
    private Integer type;

    /**
     * 商户订单号
     */
    private String mchOrderNo;


}