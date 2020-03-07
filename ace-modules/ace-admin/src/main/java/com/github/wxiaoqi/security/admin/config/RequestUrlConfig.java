package com.github.wxiaoqi.security.admin.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.naming.ldap.PagedResultsControl;

@Configuration
@Data
public class RequestUrlConfig {
    @Value("${requestUrl.config}")
    public String requestUrl;

    @Value("${blockchain.pushaddress}")
    public String blockchainUrl;

}
