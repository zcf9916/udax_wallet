package com.github.wxiaoqi.security.common.vo;

import com.github.wxiaoqi.security.common.enums.AccountLogType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountAssertLogVo{
    private Long userId;

    private String symbol;

    private BigDecimal amount;

    private BigDecimal chargeAmount = BigDecimal.ZERO;

    private String chargeSymbol;

    private String transNo;

    private String remark;

    private AccountLogType type;
}
