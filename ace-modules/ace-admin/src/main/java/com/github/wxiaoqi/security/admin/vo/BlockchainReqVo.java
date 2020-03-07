package com.github.wxiaoqi.security.admin.vo;

import lombok.Data;

@Data
public class BlockchainReqVo {
    private int page;
    private int limit;
    private String symbol;
    private String transNo;
    private Long step;
    private String business;
    private Integer status;
    private String proxyCode;
    private Integer autoWithdraw;
}
