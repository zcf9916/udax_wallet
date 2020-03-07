package com.udax.front.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
@Data
public class IfrConfiguration {
    @Value("${ifr.service_version}")
    private String serviceVersion;//生成用户token秘钥


    @Value("${ifr.partner_code}")
    private String partnerCode;//数据库mac签名

    @Value("${ifr.backend_url}")
    private String backendUrl;//钱包项目商户相关接口用的签名key

    @Value("${ifr.redirect_url}")
    private String redirectUrl;// 钱包地址与区块链平台对称加密秘钥

    @Value("${ifr.merchantKey}")
    private String merchantKey;//钱包平台加密秘钥


}
