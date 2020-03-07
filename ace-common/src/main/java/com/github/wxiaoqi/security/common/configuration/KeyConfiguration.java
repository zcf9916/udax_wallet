package com.github.wxiaoqi.security.common.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
    备注信息: 类中添加配置文件参数或者字段时,
              在前端项目中的application_**.yml 文件中添加的配置信息务必在 后端ADMIN 项目中添加同样字段
              因为此类在Common项目中.前后端都用引用
 */
@Configuration
@Data
public class KeyConfiguration {
    @Value("${jwt.rsa-secret}")
    private String userSecret;//生成用户token秘钥


    @Value("${client.signKey}")
    private String signKey;//数据库mac签名

    @Value("${client.merchantKey}")
    private String merchantKey;//钱包项目商户相关接口用的签名key

    @Value("${client.tokenKey}")
    private String tokenKey;// 钱包地址与区块链平台对称加密秘钥
    
    @Value("${client.walletKey}")
    private String walletKey;//钱包平台加密秘钥
    
    @Value("${blockchain.ip}")
    private String blockchainIp; //区块链平台IP



    @Value("${jwt.rsa-secret}")
    private String serviceSecret;
    private byte[] userPubKey;
    private byte[] userPriKey;
//    private byte[] servicePriKey;
//    private byte[] servicePubKey;

    @Value("${ifTestEnv.value:#{false}}")
    private Boolean  ifTestEnv; //是否测试环境
}
