package com.github.wxiaoqi.security.common.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * @author ace
 * @create 2017/12/17.
 */
@Configuration
@Data
public class SftpConfiguration {
    @Value("${sftp.host}")
    private String host;


    @Value("${sftp.port}")
    private Integer port;

    @Value("${sftp.timeout}")
    private Integer timeout;

    @Value("${sftp.aliveMax}")
    private Integer aliveMax;


    @Value("${sftp.user.name}")
    private String userName;

    @Value("${sftp.user.password}")
    private String password;


    @Value("${sftp.nginx.path}")
    private String nginxPath;

}
