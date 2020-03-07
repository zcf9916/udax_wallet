package com.udax.front.vo.rspvo.fund;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import com.udax.front.annotation.DateTimeFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class FundAccountAssertLogVo {


    private String remark;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long fundId;//基金id

    private String dcCode;//货币代码


    private Integer changeType;//变更类型

    @JsonSerialize(using = ToStringSerializer.class)
    private Long flowPrimary; //流水号

    @JsonSerialize(using = DateToTimeStampSerializer.class)
    private Date updateTime;

}
