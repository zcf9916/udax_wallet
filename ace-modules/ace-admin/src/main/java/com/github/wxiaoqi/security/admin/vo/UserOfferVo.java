package com.github.wxiaoqi.security.admin.vo;

import com.github.wxiaoqi.security.common.entity.front.FrontUser;

import lombok.Data;

@Data
public class UserOfferVo {
    private Long id;
    private FrontUser frontUser;
    private Integer status;
}
