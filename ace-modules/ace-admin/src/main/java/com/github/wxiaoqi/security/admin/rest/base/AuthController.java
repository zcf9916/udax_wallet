package com.github.wxiaoqi.security.admin.rest.base;

import com.github.wxiaoqi.security.admin.service.PermissionService;
import com.github.wxiaoqi.security.admin.util.JwtTokenUtil;
import com.github.wxiaoqi.security.admin.vo.UserInfo;
import com.github.wxiaoqi.security.admin.vo.reqvo.LoginVo;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.Param;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.LocalUtil;
import com.github.wxiaoqi.security.common.util.jwt.JWTAdminInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Locale;

import static com.github.wxiaoqi.security.common.constant.Constants.BaseParam.TOKEN_ADMIN;

@RestController
@RequestMapping("jwt")
@Slf4j
public class AuthController {
    @Value("${jwt.token-header}")
    private String tokenHeader;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PermissionService permissionService;

    @RequestMapping(value = "token", method = RequestMethod.POST)
    public ObjectRestResponse<String> createAuthenticationToken(
            @RequestBody @Valid LoginVo loginVo) throws Exception {
        UserInfo info = permissionService.validate(loginVo.getUserName(), loginVo.getPassword());
        String language = loginVo.getLanguage();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(language)) {
            Locale locale = new Locale(language);
            if (language.equals("zh")) {
                locale = Locale.SIMPLIFIED_CHINESE;
            } else if (language.equals("ko")) {
                locale = Locale.KOREA;
            } else if (language.equals("en")) {
                locale = Locale.US;
            } else if (language.equals("ja")) {
                locale = Locale.JAPANESE;
            } else if (language.equals("th")) {
                locale = LocalUtil.THAI;
            } else if (language.equals("in")) {
                locale = LocalUtil.INDONESIA;
            } else if (language.equals("ms")) {
                locale = LocalUtil.MALAY;
            } else {
                locale = Locale.US;
            }
            LocaleContextHolder.setLocale(locale);
        }
        if (!StringUtils.isEmpty(info.getId())) {
            String token = jwtTokenUtil.generateToken(new JWTAdminInfo(info.getUsername(), Long.parseLong(info.getId()), info.getName(), info.getExchangeId()));
            //获取后台token相关参数
            Param token_param = (Param) CacheUtil.getCache().get(TOKEN_ADMIN);
            int expire = 600;//600秒
            if (token_param != null) {
                expire = Integer.parseInt(token_param.getParamValue());
            }
            //缓存用户信息
            CacheUtil.getCache().set(Constants.CacheServiceType.ADMIN_USER + token, info);
            //缓存token
            CacheUtil.getCache().set(Constants.CommonType.TOKEN + info.getId(), token);
            CacheUtil.getCache().expire(Constants.CacheServiceType.ADMIN_USER + token, expire);
            return new ObjectRestResponse<String>().rel(true).data(token);
        }
        return new ObjectRestResponse().status(40001).msg(Resources.getMessage("BASE_PASSWORD_WRONG"));
    }

}
