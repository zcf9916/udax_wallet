package com.github.wxiaoqi.security.admin.vo;

import lombok.Data;

@Data
public class FrontFreezeVo {
    public Long userId;
    public String[] freezeTypeStr;
}
