package com.github.wxiaoqi.security.admin.vo.reqvo;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
public class WithdrawReqVo {
    @NonNull
    private String symbol;

    private List<String> transNoList =new ArrayList<>();
    @NonNull
    private String sinkAddress;

    private String sinkKey;

    private String proxyCode;
}
