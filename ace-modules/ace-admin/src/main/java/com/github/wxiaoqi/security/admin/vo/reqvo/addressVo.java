package com.github.wxiaoqi.security.admin.vo.reqvo;

import lombok.Data;

@Data
public class addressVo {
    private String address;
    private String privkey;
    private String password;
    private Integer autoWithdraw;
}
