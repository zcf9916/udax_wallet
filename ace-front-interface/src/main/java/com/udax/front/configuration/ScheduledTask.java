
package com.udax.front.configuration;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.BasicSymbol;
import com.github.wxiaoqi.security.common.entity.front.DcAssetAccount;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.HttpUtils;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.udax.front.util.BizControllerUtil;
import com.udax.front.util.CacheBizUtil;
import com.udax.front.vo.rspvo.AccountListModel;
import com.udax.front.vo.rspvo.QuotationBean;
import com.udax.front.vo.rspvo.UdaxQuotationBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

/**
 * 适合单机用的定时任务   ,不适合集群
 */
@Configuration
@Slf4j
@EnableScheduling
public class ScheduledTask {

    @Autowired
    private Environment env;

    //每隔10秒获取一次行情
    @Scheduled(fixedRate = 10000000)
    public void getQuotation() {
        try{
            String url = env.getProperty("udax.quotation");//udax行情接口
            String returnJson = HttpUtils.postJson(url,"");
            UdaxQuotationBean jsonBean = JSON.parseObject(returnJson,UdaxQuotationBean.class);
            if(jsonBean == null){
                return;

            }
            Map<String,JSONObject> quotationBeanMap = (Map<String,JSONObject>) jsonBean.getData().get("quotation");
            if(quotationBeanMap == null){
                return ;
            }
            quotationBeanMap.forEach((k,v) ->{
                if(!k.contains("/"+Constants.QUOTATION_DCCODE)){
                    return;
                }
                log.info("写入" + k + "行情,价格" + v.getBigDecimal("lastPrice"));
                CacheUtil.getCache().hset(Constants.CommonType.QUOTATION,k,v.getBigDecimal("lastPrice"));
            });
        }catch (Exception e){
            log.error("定时获取行情失败");
        }


    }

}