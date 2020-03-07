package com.github.wxiaoqi.security.admin.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-06-21 8:12
 */
@Data
public class UserInfo implements Serializable{
    public String id;
    public String username;
    public String password;
    public String name;
    public Long exchangeId;
    private String description;
}
