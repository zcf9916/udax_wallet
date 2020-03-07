package com.github.wxiaoqi.security.admin.biz.front;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.BaseSmsConfig;
import com.github.wxiaoqi.security.common.mapper.admin.BaseSmsConfigMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import com.github.wxiaoqi.security.common.util.StringUtil;

@Service
@Transactional(rollbackFor = Exception.class)
public class SmsConfigBiz extends BaseBiz<BaseSmsConfigMapper, BaseSmsConfig> {

    protected Logger logger = LogManager.getLogger();
    @Autowired
    private BaseSmsConfigMapper smsConfigMapper;

    public List<BaseSmsConfig> cacheReturn() {
        List<BaseSmsConfig> smsConfigList = null;
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.SmsConfig);
        try {
            // 初始化时将把对象设置进缓存
            smsConfigList = smsConfigMapper.selectAll();
            if (StringUtil.listIsNotBlank(smsConfigList)) {
                for (BaseSmsConfig baseSmsConfig : smsConfigList) {
                    CacheUtil.getCache().set(Constants.CacheServiceType.SmsConfig + baseSmsConfig.getWhiteExchId(), baseSmsConfig);
                }
            }
            logger.info("==短信配置表:BaseSmsConfig缓存完成,缓存条数：{}", smsConfigList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return smsConfigList;
    }

    @Override
    @Transactional
    public void insertSelective(BaseSmsConfig entity) {
        EntityUtils.setCreatAndUpdatInfo(entity);
        mapper.insertSelective(entity);
        cacheReturn();
    }

    @Override
    @Transactional
    public void updateSelectiveById(BaseSmsConfig entity) {
        EntityUtils.setUpdatedInfo(entity);
        mapper.updateByPrimaryKeySelective(entity);
        cacheReturn();
    }

    @Override
    @Transactional
    public void deleteById(Object id) {
        mapper.deleteByPrimaryKey(id);
        cacheReturn();
    }

}
