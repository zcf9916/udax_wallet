package com.udax.front.vo.rspvo.merchant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
public class MchBgRechargeQueryRspVo implements Serializable {


    /**
     * token地址
     */
    private String userAddress;



    /**
     * 充值金额
     */
    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal rechargeAmount;

    /**
     * 货币编码
     */
    private String symbol;

    @JsonSerialize(using = DateToTimeStampSerializer.class)
    private Date createTime;




    /**
     * 区块链订单ID
     */
    private String blockOrderId;


    private Integer type;

}