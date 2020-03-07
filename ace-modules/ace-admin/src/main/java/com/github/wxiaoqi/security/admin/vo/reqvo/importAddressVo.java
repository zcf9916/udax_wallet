package com.github.wxiaoqi.security.admin.vo.reqvo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class importAddressVo {
    private String symbol;
    private List<addressVo> addressList=new ArrayList<>();
    private String address;
    private String privkey;
    private String proxyCode;
    private Integer autoWithdraw;
    private String password;
    private String protocolType;
    /**
     * 添加 or 删除
     */
    private String method;
}
