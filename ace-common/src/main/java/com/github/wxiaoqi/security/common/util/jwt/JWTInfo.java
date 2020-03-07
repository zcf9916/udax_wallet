package com.github.wxiaoqi.security.common.util.jwt;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by ace on 2017/9/10.
 */
@Data
public class JWTInfo implements Serializable,IJWTInfo {
    private String username;
    private Long id;
    private String uid;

    private Long exchangeId;//交易所Id

    public JWTInfo(String username, Long id, String uid,Long exchangeId) {
        this.username = username;
        this.id = id;
        this.uid = uid;
        this.exchangeId = exchangeId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JWTInfo jwtInfo = (JWTInfo) o;

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
