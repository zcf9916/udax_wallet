package com.github.wxiaoqi.security.admin.runner;

import com.github.wxiaoqi.security.admin.biz.base.*;
import com.github.wxiaoqi.security.admin.biz.casino.CasinoParamBiz;
import com.github.wxiaoqi.security.admin.biz.casino.CasinoRebateConfigBiz;
import com.github.wxiaoqi.security.admin.biz.front.*;
import com.github.wxiaoqi.security.admin.config.UserAuthConfig;
import com.github.wxiaoqi.security.admin.util.DBLog;
import com.github.wxiaoqi.security.admin.util.SpringBeanUtil;
import com.github.wxiaoqi.security.common.configuration.KeyConfiguration;
import com.github.wxiaoqi.security.common.entity.casino.CasinoParam;
import com.github.wxiaoqi.security.common.entity.front.CmsConfig;
import com.github.wxiaoqi.security.common.util.jwt.RsaKeyHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author ace
 * @create 2017/12/17.
 */
@Configuration
public class AuthServerRunner implements CommandLineRunner {

    protected final Logger logger = LogManager.getLogger();

    @Autowired
    private UserAuthConfig userAuthConfig;

    @Autowired
    private RedisTemplate<Serializable, Serializable> redisTemplate;
    private static final String REDIS_USER_PRI_KEY = "AG:AUTH:JWT:PRI";//jwt私钥
    private static final String REDIS_USER_PUB_KEY = "AG:AUTH:JWT:PUB";//jwt公钥
    private static final String REDIS_SERVICE_PRI_KEY = "AG:AUTH:CLIENT:PRI";//客户端私钥
    private static final String REDIS_SERVICE_PUB_KEY = "AG:AUTH:CLIENT:PUB";//客户端公钥

    @Autowired
    private KeyConfiguration keyConfiguration;

    @Override
    public void run(String... args) throws Exception {

        //TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));//设置默认时区为中国标准时间

        //如果redis存在上述key,初始化keyConfiguration
        if (redisTemplate.hasKey(REDIS_USER_PRI_KEY) && redisTemplate.hasKey(REDIS_USER_PUB_KEY) && redisTemplate.hasKey(REDIS_SERVICE_PRI_KEY) && redisTemplate.hasKey(REDIS_SERVICE_PUB_KEY)) {
            keyConfiguration.setUserPriKey(RsaKeyHelper.toBytes(redisTemplate.opsForValue().get(REDIS_USER_PRI_KEY).toString()));
            keyConfiguration.setUserPubKey(RsaKeyHelper.toBytes(redisTemplate.opsForValue().get(REDIS_USER_PUB_KEY).toString()));
        } else {
            //如果redis不存在上述key,从配置文件中读取userSecret
            Map<String, byte[]> keyMap = RsaKeyHelper.generateKey(keyConfiguration.getUserSecret());
            keyConfiguration.setUserPriKey(keyMap.get("pri"));
            keyConfiguration.setUserPubKey(keyMap.get("pub"));
            redisTemplate.opsForValue().set(REDIS_USER_PRI_KEY, RsaKeyHelper.toHexString(keyMap.get("pri")));
            redisTemplate.opsForValue().set(REDIS_USER_PUB_KEY, RsaKeyHelper.toHexString(keyMap.get("pub")));
        }
        //设置用户相关公钥
        this.userAuthConfig.setPubKeyByte(keyConfiguration.getUserPubKey());
        //启动操作日志线程
        DBLog.getInstance().start();

      //初始化帮助类型
        SpringBeanUtil.getBean(FrontHelpTypeBiz.class).cacheReturn();
        //初始化帮助内容
        SpringBeanUtil.getBean(FrontHelpContentBiz.class).cacheReturn();
        //初始化邮件配置
        SpringBeanUtil.getBean(EmailSendBiz.class).cacheReturn();
        //初始化邮件模板
        SpringBeanUtil.getBean(EmailTemplateBiz.class).cacheReturn();
        //初始化邮件审核人员
        SpringBeanUtil.getBean(EmailAuditorBiz.class).cacheReturn();
        //初始化短信配置
        SpringBeanUtil.getBean(SmsConfigBiz.class).cacheReturn();
        //初始化基础货币
        SpringBeanUtil.getBean(BasicSymbolBiz.class).cacheReturn();
        //数字货币转换配置
        SpringBeanUtil.getBean(CfgCurrencyTransferBiz.class).cacheReturn();
        //菜单
        SpringBeanUtil.getBean(MenuBiz.class).cacheReturn();
        //资源
        SpringBeanUtil.getBean(ElementBiz.class).cacheReturn();
        //货币配置表
        SpringBeanUtil.getBean(CfgDcRechargeWithdrawBiz.class).cacheReturn();
        //字典
        SpringBeanUtil.getBean(DictDataBiz.class).cacheReturn();
        //国家
        SpringBeanUtil.getBean(FrontCountryBiz.class).cacheReturn();
        //配置信息
        SpringBeanUtil.getBean(CfgCurrencyChargeBiz.class).cacheReturn();
        //系统参数
        SpringBeanUtil.getBean(ParamBiz.class).cacheReturn();
        //交易所
        SpringBeanUtil.getBean(WhiteExchInfoBiz.class).cacheReturn();
        //广告
        SpringBeanUtil.getBean(FrontAdvertBiz.class).cacheReturn();
        //公告
        SpringBeanUtil.getBean(FrontNoticeBiz.class).cacheReturn();

        //版本控制
        SpringBeanUtil.getBean(BaseVersionBiz.class).cacheReturn();

        //币种图标
        SpringBeanUtil.getBean(BasicSymbolImageBiz.class).cacheReturn();

        //代币描述信息模板表
        SpringBeanUtil.getBean(CfgDescriptionTemplateBiz.class).cacheReturn();

        //交易对中间表
        SpringBeanUtil.getBean(TransferExchBiz.class).cacheReturn();
        //计价方式
        SpringBeanUtil.getBean(ValuationModeBiz.class).cacheReturn();

        //代币描述信息缓存
        SpringBeanUtil.getBean(CfgSymbolDescriptionBiz.class).cacheReturn();

        SpringBeanUtil.getBean(CmsConfigBiz.class).cacheReturn();

        SpringBeanUtil.getBean(CasinoParamBiz.class).cacheReturn();

        SpringBeanUtil.getBean(CasinoRebateConfigBiz.class).cacheReturn();

        logger.info("=================================");
        logger.info("系统 Udax_wallet_admin 启动完成");
        logger.info("=================================");

    }
}
