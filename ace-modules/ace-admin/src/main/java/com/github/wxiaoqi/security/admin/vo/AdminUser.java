package com.github.wxiaoqi.security.admin.vo;

import lombok.Data;

import java.util.List;

/**
 * Created by ace on 2017/8/22.
 */
@Data
public class AdminUser {
    public String id;
    public String username;
    public String name;
    private String description;
    private String image;
    //顶级白标id
    private Long exchId;
    private List<PermissionInfo> menus;
    private List<PermissionInfo> elements;
}
