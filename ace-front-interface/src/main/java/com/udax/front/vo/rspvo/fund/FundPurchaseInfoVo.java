package com.udax.front.vo.rspvo.fund;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class FundPurchaseInfoVo {

    private String dcCode;

    private BigDecimal profiltVolume;//收益数量

    private String  fundName;//基金名称

    private BigDecimal yield;//实际收益率

    private BigDecimal orderVolume;//申购占用数量


    private BigDecimal orderChrge;//申购手续费

    private String rate;//费率

    private Integer status;//认购状态

    @JsonSerialize(using = DateToTimeStampSerializer.class)
    private Date orderTime; //申购时间

}
