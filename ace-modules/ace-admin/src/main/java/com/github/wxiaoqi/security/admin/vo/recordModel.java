package com.github.wxiaoqi.security.admin.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class recordModel {
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal amount;
    private String business;
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal confirm;
    @JsonSerialize(using= BigDecimalCoinSerializer.class)
    private BigDecimal fee;
    private String fromAddress;
    private String symbol;
    private String toAddress;
    private String txid;
    private Date updateTime;
    private String transNo;
    private String proxyCode;

}
