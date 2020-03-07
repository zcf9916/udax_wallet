package com.github.wxiaoqi.security.admin.biz.front;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.wxiaoqi.security.common.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.FrontHelpContent;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.mapper.admin.FrontHelpContentMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.DataSortUtil;
import com.github.wxiaoqi.security.common.util.EntityUtils;

import tk.mybatis.mapper.entity.Example;

@Service
@Transactional(rollbackFor = Exception.class)
public class FrontHelpContentBiz extends BaseBiz<FrontHelpContentMapper, FrontHelpContent> {

    protected Logger logger = LogManager.getLogger();

    public List<FrontHelpContent> cacheReturn() {
        List<FrontHelpContent> helpContentList = null;
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.FrontHelpContent);
        try {
            // 初始化时将把对象设置进缓存
            Example example = new Example(FrontHelpContent.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("enable", EnableType.ENABLE.value());
            //helpContentList = frontHelpContentMapper.queryFrontHelpForTypeName();
            helpContentList = mapper.selectByExample(example);
            //根据sort属性升序排序
            DataSortUtil.sortAsc(helpContentList,"sort");
            HashMap<String, ArrayList<FrontHelpContent>> helpContentMap = new HashMap<String, ArrayList<FrontHelpContent>>();
            if (!StringUtil.listIsBlank(helpContentList)) {
                for (FrontHelpContent frontHelpContent : helpContentList) {
                    if (helpContentMap.containsKey(frontHelpContent.getTypeId()+":"+frontHelpContent.getLanguageType()+":"+frontHelpContent.getClientType())) {
                        List<FrontHelpContent> contentList = helpContentMap.get(frontHelpContent.getTypeId()+":"+frontHelpContent.getLanguageType()+":"+frontHelpContent.getClientType());
                        contentList.add(frontHelpContent);
                    } else {
                    	ArrayList<FrontHelpContent> contentList  = new ArrayList<FrontHelpContent>();
                        contentList.add(frontHelpContent);
                        helpContentMap.put(frontHelpContent.getTypeId()+":"+frontHelpContent.getLanguageType()+":"+frontHelpContent.getClientType(), contentList);
                    }
                    CacheUtil.getCache().set(Constants.CacheServiceType.FrontHelpContent+frontHelpContent.getId()+":"+frontHelpContent.getLanguageType()+":"+frontHelpContent.getClientType(), frontHelpContent);
                }
            }
            helpContentMap.forEach((k,v)->{
            	CacheUtil.getCache().set(Constants.CacheServiceType.FrontHelpContent+ k+Constants.CacheServiceType.LIST, v);
            });
            logger.info("==帮助内容表:frontHelpContent缓存完成,缓存条数：{}", helpContentList.size());
        } catch (Exception e) {
        	logger.error("加载帮助类型缓存时异常",e);
        }
        return helpContentList;
    }

    @Override
    @Transactional
    public void insertSelective(FrontHelpContent entity) {
        EntityUtils.setCreatAndUpdatInfo(entity);
        mapper.insertSelective(entity);
        cacheReturn();
    }

    @Override
    @Transactional
    public void updateSelectiveById(FrontHelpContent entity) {
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