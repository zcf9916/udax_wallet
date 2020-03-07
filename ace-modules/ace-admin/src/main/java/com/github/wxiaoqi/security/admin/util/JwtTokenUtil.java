package com.github.wxiaoqi.security.admin.util;

import com.github.wxiaoqi.security.common.configuration.KeyConfiguration;
import com.github.wxiaoqi.security.common.util.jwt.IJWTAdminInfo;
import com.github.wxiaoqi.security.common.util.jwt.JWTAdminInfo;
import com.github.wxiaoqi.security.common.util.jwt.JWTHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by ace on 2017/9/10.
 */
@Component
public class JwtTokenUtil {

    @Value("${jwt.expire}")
    private int expire;
    @Autowired
    private KeyConfiguration keyConfiguration;

    public String generateToken(IJWTAdminInfo jwtInfo) throws Exception {
        return JWTHelper.generateToken(jwtInfo, keyConfiguration.getUserPriKey(),expire);
    }
    public JWTAdminInfo getInfoFromToken(String token) throws Exception {
        return JWTHelper.getInfoFromAdminToken(token, keyConfiguration.getUserPubKey());
    }


}
