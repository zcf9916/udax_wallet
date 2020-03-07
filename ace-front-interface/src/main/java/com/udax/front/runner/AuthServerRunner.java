package com.udax.front.runner;

import com.github.wxiaoqi.security.common.configuration.KeyConfiguration;
import com.github.wxiaoqi.security.common.entity.merchant.MchNotify;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.task.CallbackMsg;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.jwt.RsaKeyHelper;
import com.udax.front.biz.merchant.MchNotifyBiz;
import com.udax.front.task.callback.CallbackQueue;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static com.github.wxiaoqi.security.common.constant.Constants.MCH_CALLBACK_TASK;

/**
 * @author ace
 * @create 2017/12/17.
 */
@Configuration
@Slf4j
public class AuthServerRunner implements CommandLineRunner {

    private static final String REDIS_USER_PRI_KEY = "AG:AUTH:JWT:PRI";
    private static final String REDIS_USER_PUB_KEY = "AG:AUTH:JWT:PUB";
    protected final Logger logger = LogManager.getLogger();
    @Autowired
    private RedisTemplate<Serializable, Serializable> redisTemplate;
    //  private static final String REDIS_SERVICE_PRI_KEY = "AG:AUTH:CLIENT:PRI";
    //   private static final String REDIS_SERVICE_PUB_KEY = "AG:AUTH:CLIENT:PUB";
    @Autowired
    private KeyConfiguration keyConfiguration;

    @Autowired
    private MchNotifyBiz mchNotifyBiz;

    @Autowired
    private CallbackQueue queue;


    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor executor;

    @Override
    public void run(String... args) throws Exception {

        //&&redisTemplate.hasKey(REDIS_SERVICE_PRI_KEY)&&redisTemplate.hasKey(REDIS_SERVICE_PUB_KEY)

        if (redisTemplate.hasKey(REDIS_USER_PRI_KEY) && redisTemplate.hasKey(REDIS_USER_PUB_KEY)) {
            keyConfiguration.setUserPriKey(RsaKeyHelper.toBytes(redisTemplate.opsForValue().get(REDIS_USER_PRI_KEY).toString()));
            keyConfiguration.setUserPubKey(RsaKeyHelper.toBytes(redisTemplate.opsForValue().get(REDIS_USER_PUB_KEY).toString()));
            // keyConfiguration.setServicePriKey(RsaKeyHelper.toBytes(redisTemplate.opsForValue().get(REDIS_SERVICE_PRI_KEY).toString()));
            //  keyConfiguration.setServicePubKey(RsaKeyHelper.toBytes(redisTemplate.opsForValue().get(REDIS_SERVICE_PUB_KEY).toString()));
        } else {
            Map<String, byte[]> keyMap = RsaKeyHelper.generateKey(keyConfiguration.getUserSecret());
            keyConfiguration.setUserPriKey(keyMap.get("pri"));
            keyConfiguration.setUserPubKey(keyMap.get("pub"));
            redisTemplate.opsForValue().set(REDIS_USER_PRI_KEY, RsaKeyHelper.toHexString(keyMap.get("pri")));
            redisTemplate.opsForValue().set(REDIS_USER_PUB_KEY, RsaKeyHelper.toHexString(keyMap.get("pub")));
            //  keyMap = RsaKeyHelper.generateKey(keyConfiguration.getServiceSecret());
            //  keyConfiguration.setServicePriKey(keyMap.get("pri"));
            //   keyConfiguration.setServicePubKey(keyMap.get("pub"));
            //   redisTemplate.opsForValue().set(REDIS_SERVICE_PRI_KEY, RsaKeyHelper.toHexString(keyMap.get("pri")));
            //   redisTemplate.opsForValue().set(REDIS_SERVICE_PUB_KEY, RsaKeyHelper.toHexString(keyMap.get("pub")));

        }


        //1.预下单标示  2.下单成功通知标示  3.充值成功通知标示  4.提现成功通知标示 5.退款成功通知标示

        CacheUtil.getCache().del(MCH_CALLBACK_TASK);
        log.info("加载回调成功的商户消息begin");
        Example example = new Example(MchNotify.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andGreaterThan("type", 1);//要通知的
        criteria.andEqualTo("status", EnableType.DISABLE.value());//要通知的
        criteria.andLessThan("count", 6);//通知次数小于6的
        example.setOrderByClause("id asc");
        List<MchNotify> list = mchNotifyBiz.selectByExample(example);

        list.forEach((f) -> {
            //分批插入set里
            CallbackMsg msg = new CallbackMsg();
            BeanUtils.copyProperties(f, msg);
            CacheUtil.getCache().setSAdd(MCH_CALLBACK_TASK, msg);
        });


        log.info("加载回调成功的商户消息end,数据长度:" + list.size());
        //queue.excuteCallback();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                queue.excuteCallback();
            }
        });

        logger.info("=================================");
        logger.info("系统Ace-front-interface启动完成!!!");
        logger.info("=================================");
    }

}
