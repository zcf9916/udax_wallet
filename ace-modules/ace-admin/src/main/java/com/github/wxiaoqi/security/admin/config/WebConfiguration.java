package com.github.wxiaoqi.security.admin.config;


import java.util.ArrayList;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.github.wxiaoqi.security.admin.interceptor.LocaleInterceptor;
import com.github.wxiaoqi.security.admin.interceptor.UserAuthRestInterceptor;
import com.github.wxiaoqi.security.common.handler.GlobalExceptionHandler;

/**
 *
 * @author ace
 * @date 2017/9/8
 */
@Configuration("admimWebConfig")
@Primary
public class WebConfiguration implements WebMvcConfigurer {
    @Bean
    GlobalExceptionHandler getGlobalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getLocaleInterceptor());
        registry.addInterceptor(getUserAuthRestInterceptor()).excludePathPatterns(getExcludePathPatterns());

    }



    @Bean
    UserAuthRestInterceptor getUserAuthRestInterceptor() {
        return new UserAuthRestInterceptor();
    }


    @Bean
    LocaleInterceptor getLocaleInterceptor() {
        return new LocaleInterceptor();
    }



    /**
     * 不需要用户和服务认证判断的路径
     * @return
     */
    private ArrayList<String> getExcludePathPatterns() {
        ArrayList<String> list = new ArrayList<>();
        String[] urls = {
                //多个请求必须以逗号结尾
                "/jwt/token","/common/language","/admin/upload/imageFtp",
        };
        Collections.addAll(list, urls);
        return list;
    }

}
