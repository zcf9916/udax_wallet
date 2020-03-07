package com.udax.front;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.wxiaoqi.security.common.EnableCommonConfig;

/**
 * Created by Ace on 2017/6/2.
 */
@SpringBootApplication
//@EnableDiscoveryClient
@MapperScan("com.github.wxiaoqi.security.common.mapper")
@EnableCommonConfig
public class FrontInterfaceBootstrap{
    public static void main(String[] args) {
        SpringApplication.run(FrontInterfaceBootstrap.class, args);
    }
}
