package com.github.wxiaoqi.security.admin.biz.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.BaseEmailConfig;
import com.github.wxiaoqi.security.common.mapper.admin.BaseEmailConfigMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.EntityUtils;

@Service
@Transactional(rollbackFor = Exception.class)
public class EmailSendBiz extends BaseBiz<BaseEmailConfigMapper, BaseEmailConfig> {
    protected Logger logger = LogManager.getLogger();
    @Autowired
    private BaseEmailConfigMapper emailConfigmapper;

    public List<BaseEmailConfig> cacheReturn() {
        List<BaseEmailConfig> emailConfigList = null;
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.EmailConfig);
        try {
            // 初始化时将把对象设置进缓存
            emailConfigList = emailConfigmapper.selectAll();
            // 将list放入缓存
            CacheUtil.getCache().set(Constants.CacheServiceType.EmailConfig + ":list", (ArrayList<BaseEmailConfig>) emailConfigList);
            logger.info("==邮件配置表:BaseEmailConfig缓存完成,缓存条数：{}", emailConfigList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emailConfigList;
    }

    @Override
    @Transactional
    public void insertSelective(BaseEmailConfig entity) {
        EntityUtils.setCreatAndUpdatInfo(entity);
        mapper.insertSelective(entity);
        cacheReturn();
    }

    @Override
    @Transactional
    public void updateSelectiveById(BaseEmailConfig entity) {
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
