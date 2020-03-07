package com.github.wxiaoqi.security.common.util.jwt;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by ace on 2017/9/10.
 */
@Data
public class JWTAdminInfo implements Serializable,IJWTAdminInfo {
    private String username;
    private Long id;
    private String name;
    public Long exchangeId;

    public JWTAdminInfo(String username, Long id, String name,Long exchangeId) {
        this.username = username;
        this.id = id;
        this.name = name;
        this.exchangeId=exchangeId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JWTAdminInfo jwtInfo = (JWTAdminInfo) o;

        if (username != null ? !username.equals(jwtInfo.username) : jwtInfo.username != null) {
            return false;
        }
        return id != null ? id.equals(jwtInfo.id) : jwtInfo.id == null;

    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
