package com.github.wxiaoqi.security.admin.vo.reqvo;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

/**
 * 区块链手续费vo
 */
@Data
public class WalletFeeConfVo {
    @NonNull
    private String method; // add or update or delete

    @NonNull
    private String symbol; //币种

    @NonNull
    private String address; //地址
    private String privateKey; //秘钥

    @NonNull
    private Float minAmount; //最小金额


    private Integer platform = 2; //平台

    @NonNull
    private String proxyCode; //标识

    private String parameter;//参数配置
}
