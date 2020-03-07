package com.udax.front.interceptor;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.LocalUtil;
import com.udax.front.biz.CacheBiz;
import com.udax.front.util.CacheBizUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Locale;

public class LocaleInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private CacheBiz cacheBiz;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String language = request.getHeader("locale");
        Locale locale = Locale.US;
        if (StringUtils.isNotBlank(language)) {
            if (language.equals("zh")) {
                locale = Locale.SIMPLIFIED_CHINESE;
            } else  if (language.equals("zh_tw")) {
                locale = Locale.TAIWAN;
            } else if (language.equals("ko")) {
                locale = Locale.KOREA;
            } else if (language.equals("en")) {
                locale = Locale.US;
            } else if (language.equals("ja")) {
                locale = Locale.JAPANESE;
            }else if (language.equals("th")) {
                locale = LocalUtil.THAI;
            }else if (language.equals("in")) {
                locale = LocalUtil.INDONESIA;
            }else if (language.equals("ms")) {
                locale = LocalUtil.MALAY;
            }
        }
        if (locale == null) {
            locale = Locale.US;
        }
        BaseContextHandler.setLanguage(language);
        LocaleContextHolder.setLocale(locale);





        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }


}
