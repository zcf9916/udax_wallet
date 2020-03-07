package com.udax.front;


import com.github.wxiaoqi.security.common.EnableCommonConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Created by Ace on 2017/6/2.
 */
@SpringBootApplication
//@EnableDiscoveryClient
@MapperScan("com.github.wxiaoqi.security.common.mapper")
@EnableCommonConfig
public class FrontInterfaceBootstrapTomcat extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(FrontInterfaceBootstrapTomcat.class, args);
    }


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(FrontInterfaceBootstrapTomcat.class);
    }
}
