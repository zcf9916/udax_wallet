package com.udax.front.vo.rspvo.merchant;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class QueryWithdrawRspVo extends BaseMchRspVo {



    private Integer status;//状态


    private String tranNo;//流水号

    private String mchOrderNO;//

    private String address;//提现地址

    @JsonSerialize(using = BigDecimalCoinSerializer.class)
    private BigDecimal chargeAmount;//手续费数量

}