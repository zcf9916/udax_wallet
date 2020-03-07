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
import com.github.wxiaoqi.security.common.entity.admin.BaseEmailTemplate;
import com.github.wxiaoqi.security.common.mapper.admin.BaseEmailTemplateMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.EntityUtils;

@Service
@Transactional(rollbackFor = Exception.class)
public class EmailTemplateBiz extends BaseBiz<BaseEmailTemplateMapper, BaseEmailTemplate> {

    protected Logger logger = LogManager.getLogger();
    @Autowired
    private BaseEmailTemplateMapper emailTemplateMapper;

    public List<BaseEmailTemplate> cacheReturn() {
        List<BaseEmailTemplate> emailTemplateList = null;
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.EmailTemplate);
        try {
            // 初始化时将把对象设置进缓存
            emailTemplateList = emailTemplateMapper.selectAll();
            // 将list放入缓存
            CacheUtil.getCache().set(Constants.CacheServiceType.EmailTemplate + ":list", (ArrayList<BaseEmailTemplate>) emailTemplateList);
            logger.info("==邮件模板表:BaseEmailTemplate缓存完成,缓存条数：{}", emailTemplateList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emailTemplateList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertSelective(BaseEmailTemplate entity) {
        EntityUtils.setCreatAndUpdatInfo(entity);
        mapper.insertSelective(entity);
        cacheReturn();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSelectiveById(BaseEmailTemplate entity) {
        EntityUtils.setUpdatedInfo(entity);
        mapper.updateByPrimaryKeySelective(entity);
        cacheReturn();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Object id) {
        mapper.deleteByPrimaryKey(id);
        cacheReturn();
    }


}
