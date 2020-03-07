package com.github.wxiaoqi.security.admin.interceptor;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.github.wxiaoqi.security.common.util.LocalUtil;

public class LocaleInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(LocaleInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String language = request.getHeader("locale");
        Locale locale = locale = Locale.US;
        if (StringUtils.isNotBlank(language)) {
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
            }
        }
        if (locale == null) {
            locale = Locale.SIMPLIFIED_CHINESE;
        }
        LocaleContextHolder.setLocale(locale);
        return super.preHandle(request, response, handler);
    }
}
