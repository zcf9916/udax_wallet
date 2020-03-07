package com.github.wxiaoqi.security.admin.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-06-22 15:19
 */
@Data
public class PermissionInfo implements Serializable{
    private String code;
    private String type;
    private String uri;
    private String method;
    private String name;
    private String menu;

}
