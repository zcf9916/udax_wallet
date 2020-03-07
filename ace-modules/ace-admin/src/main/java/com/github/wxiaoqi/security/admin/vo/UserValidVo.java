package com.github.wxiaoqi.security.admin.vo;

import com.github.wxiaoqi.security.common.entity.admin.DictData;

import lombok.Data;

@Data
public class UserValidVo {
    public Long id;
    public Integer userValid;
    public DictData dictData;
    /** 1:普通用户审核; 2:商户审核; */
    public Integer userType;
}
