package com.github.wxiaoqi.security.common.configuration;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.github.wxiaoqi.security.common.support.cache",
        "com.github.wxiaoqi.security.common.configuration",
        "com.github.wxiaoqi.security.common.util"})
public class AutoConfiguration {
    public AutoConfiguration() {
    }

}
