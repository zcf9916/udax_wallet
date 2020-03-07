package com.udax.front.configuration;


import com.github.wxiaoqi.security.common.handler.GlobalExceptionHandler;
import com.udax.front.interceptor.LocaleInterceptor;
import com.udax.front.interceptor.UserAuthRestInterceptor;
import com.udax.front.interceptor.WhiteTagInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Collections;

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
        //registry.addInterceptor(getServiceAuthRestInterceptor()).addPathPatterns("/service/**");LocaleInterceptor
        registry.addInterceptor(getLocaleInterceptor());
        registry.addInterceptor(getWhiteTageInterceptor()).excludePathPatterns(getLocaleExcludePathPatterns());
        registry.addInterceptor(getUserAuthRestInterceptor()).excludePathPatterns(getExcludePathPatterns());
    }

    /**
     * 不需要用户和服务认证判断的路径
     * @return
     */
    private ArrayList<String> getExcludePathPatterns() {
        ArrayList<String> list = new ArrayList<>();
        String[] urls = {
                "/wallet/untoken/**","/merchant/**","/wallet/blockchain/**","/wallet/ud/untoken/**","/wallet/ifr/backendNotify","/wallet/casino/untoken/**",
                "/wallet/tencent/**"

        };
        Collections.addAll(list, urls);
        return list;
    }

    /**
     * 不需要用户和服务认证判断的路径
     * @return
     */
    private ArrayList<String> getLocaleExcludePathPatterns() {
        ArrayList<String> list = new ArrayList<>();
        String[] urls = { "/wallet/tencent/**","/wallet/blockchain/**","/merchant/**","/wallet/ifr/backendNotify"};
        Collections.addAll(list, urls);
        return list;
    }

    @Bean
    WhiteTagInterceptor getWhiteTageInterceptor() {
        return new WhiteTagInterceptor();
    }
    @Bean
    UserAuthRestInterceptor getUserAuthRestInterceptor() {
        return new UserAuthRestInterceptor();
    }

    @Bean
    LocaleInterceptor getLocaleInterceptor() {
        return new LocaleInterceptor();
    }

}
