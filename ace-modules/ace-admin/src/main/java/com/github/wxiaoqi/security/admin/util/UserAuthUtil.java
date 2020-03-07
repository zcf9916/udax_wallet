package com.github.wxiaoqi.security.admin.util;


import com.github.wxiaoqi.security.admin.config.UserAuthConfig;
import com.github.wxiaoqi.security.common.exception.auth.UserTokenException;
import com.github.wxiaoqi.security.common.util.jwt.IJWTAdminInfo;
import com.github.wxiaoqi.security.common.util.jwt.JWTHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Created by ace on 2017/9/15.
 */
@Configuration
public class UserAuthUtil {
    @Autowired
    private UserAuthConfig userAuthConfig;

    public IJWTAdminInfo getInfoFromAdminToken(String token) throws Exception {
        try {
            return JWTHelper.getInfoFromAdminToken(token, userAuthConfig.getPubKeyByte());
        }catch (IllegalArgumentException ex) {
            throw new UserTokenException("User token is null or empty!");
        }catch (Exception e ){
            throw new UserTokenException("User token is null or empty!");
        }
    }
}
