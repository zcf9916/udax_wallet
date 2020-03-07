package com.github.wxiaoqi.security.admin.biz.base;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.BaseEmailAuditor;
import com.github.wxiaoqi.security.common.mapper.admin.BaseEmailAuditorMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import com.github.wxiaoqi.security.common.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class EmailAuditorBiz extends BaseBiz<BaseEmailAuditorMapper, BaseEmailAuditor> {

    public HashMap<String, List<BaseEmailAuditor>> cacheReturn() {
        List<BaseEmailAuditor> emailAuditorList = null;
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.EmailAuditor);
        HashMap<String, List<BaseEmailAuditor>> emailMap = new HashMap<String, List<BaseEmailAuditor>>();
        try {
            // 初始化时将把对象设置进缓存
            emailAuditorList = mapper.selectAll();
            if(StringUtil.listIsNotBlank(emailAuditorList)){
                for (BaseEmailAuditor emailAuditor : emailAuditorList) {
                    if (emailMap.containsKey(emailAuditor.getAuditorRole())) {
                        List<BaseEmailAuditor> emailList = emailMap.get(emailAuditor.getAuditorRole());
                        emailList.add(emailAuditor);
                    } else {
                        List<BaseEmailAuditor> emailList = new ArrayList<BaseEmailAuditor>();
                        emailList.add(emailAuditor);
                        emailMap.put(emailAuditor.getAuditorRole(), emailList);
                    }
                }
            }
            CacheUtil.getCache().set(Constants.CacheServiceType.EmailAuditor + ":map", emailMap);
            logger.info("==邮件通知审核人员表:BaseEmailAuditor缓存完成,缓存条数：{}", emailAuditorList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emailMap;
    }

    @Override
    @Transactional
    public void insertSelective(BaseEmailAuditor entity) {
        EntityUtils.setCreatAndUpdatInfo(entity);
        mapper.insertSelective(entity);
        cacheReturn();
    }

    @Override
    @Transactional
    public void updateSelectiveById(BaseEmailAuditor entity) {
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
