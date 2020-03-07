package com.github.wxiaoqi.security.admin.vo.reqvo;

import lombok.Data;
import lombok.NonNull;


/**
 * 导入汇聚地址请求vo
 */

@Data
public class WalletPublicConfVo {
    //由区块链定义method 来区分 add or update or delete
    @NonNull
    private String method;
    //币种
    @NonNull
    private String symbol;
    //区块链汇聚地址
    @NonNull
    private String rechargeAddress;
    //区块确认数
    @NonNull
    private Integer confirmations;
    //最小充币数量
    @NonNull
    private Float rechargeMinBalance;
    //状态
    @NonNull
    private Integer status;
    //平台
    private Integer platform = 2;

    //白标标识
    private String proxyCode;

    //汇聚阀值
    private Float rechargeSumBalance;

    private String protocolType;
}

