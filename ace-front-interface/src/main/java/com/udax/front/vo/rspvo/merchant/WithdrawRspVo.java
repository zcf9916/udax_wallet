package com.udax.front.vo.rspvo.merchant;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class WithdrawRspVo extends BaseMchRspVo {


    private String address;//提现地址

    private String transNo;//提现流水号


    private String mchOrderNo;//商户订单号


    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal chargeAmount;//手续费数量

   // private String

}