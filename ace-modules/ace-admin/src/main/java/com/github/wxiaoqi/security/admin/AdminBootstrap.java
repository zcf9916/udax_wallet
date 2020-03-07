package com.github.wxiaoqi.security.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.github.wxiaoqi.security.common.EnableCommonConfig;
import com.spring4all.swagger.EnableSwagger2Doc;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-05-25 12:44
 */

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@EnableCommonConfig
@MapperScan("com.github.wxiaoqi.security.common.mapper")
@EnableSwagger2Doc
public class AdminBootstrap  extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(AdminBootstrap.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AdminBootstrap.class);
    }
}
