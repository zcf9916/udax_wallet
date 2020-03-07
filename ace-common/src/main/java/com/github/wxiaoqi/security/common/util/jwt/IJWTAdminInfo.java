package com.github.wxiaoqi.security.common.util.jwt;

/**
 * Created by ace on 2017/9/10.
 */
public interface IJWTAdminInfo {
    /**
     * 获取用户名
     * @return
     */
    String getUsername();

    /**
     * 获取用户ID
     * @return
     */
    Long getId();

    /**
     * 获取名称
     * @return
     */
    String getName();

    /**
     *  交易所id
     */
    Long getExchangeId();
}
