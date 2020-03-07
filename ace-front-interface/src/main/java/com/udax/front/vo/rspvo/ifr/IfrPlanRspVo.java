package com.udax.front.vo.rspvo.ifr;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class IfrPlanRspVo {

    @JsonSerialize( using = ToStringSerializer.class)
    private Long id;

    /**
     * 方案名称
     */
    private String name;

    /**
     * 描述信息
     */
    private String desp;

    private Integer status;

}
