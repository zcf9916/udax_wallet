package com.github.wxiaoqi.security.admin.biz.front;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.FrontHelpContent;
import com.github.wxiaoqi.security.common.entity.admin.FrontHelpType;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.admin.FrontHelpContentMapper;
import com.github.wxiaoqi.security.common.mapper.admin.FrontHelpTypeMapper;
import com.github.wxiaoqi.security.common.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class FrontHelpTypeBiz extends BaseBiz<FrontHelpTypeMapper, FrontHelpType> {

    protected Logger logger = LogManager.getLogger();

    @Autowired
    private FrontHelpContentMapper frontHelpContentMapper;

    public List<FrontHelpType> cacheReturn() {
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.FrontHelpType);
        List<FrontHelpType> helpTypeList = null;
        try {
            // 初始化时将把对象设置进缓存
            Example example = new Example(FrontHelpType.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("enable", EnableType.ENABLE.value());
            // helpContentList = frontHelpContentMapper.queryFrontHelpForTypeName();
            helpTypeList = mapper.selectByExample(example);
            // 根据sort属性升序排序
            DataSortUtil.sortAsc(helpTypeList, "sort");
            if (!StringUtil.listIsBlank(helpTypeList)) {
                // 获取所有对应语言的list
                Map<String, ArrayList<FrontHelpType>> allTypeMap = InstanceUtil.newHashMap();
                for (FrontHelpType frontHelpType : helpTypeList) {
                    if (allTypeMap.containsKey(frontHelpType.getExchangeId() + ":" + frontHelpType.getLanguageType())) {
                        ArrayList<FrontHelpType> helpTypes = allTypeMap.get(frontHelpType.getExchangeId() + ":" + frontHelpType.getLanguageType());
                        helpTypes.add(frontHelpType);
                    } else {
                        ArrayList<FrontHelpType> list = new ArrayList<>();
                        list.add(frontHelpType);
                        allTypeMap.put(frontHelpType.getExchangeId() + ":" + frontHelpType.getLanguageType(), list);
                    }

                }
                // 将map按交易平台ID缓存
                allTypeMap.forEach((k, v) -> {
                    CacheUtil.getCache().set(Constants.CacheServiceType.FrontHelpType + k, v);
                });
            }
        } catch (Exception e) {
            logger.error("加载帮助类型缓存时异常", e);
        }
        return helpTypeList;
    }

    @Override
    public void insertSelective(FrontHelpType entity) {
        EntityUtils.setCreatAndUpdatInfo(entity);
        mapper.insertSelective(entity);
        cacheReturn();
    }

    @Override
    public void updateSelectiveById(FrontHelpType entity) {
        EntityUtils.setUpdatedInfo(entity);
        mapper.updateByPrimaryKeySelective(entity);
        cacheReturn();
    }

    @Override
    public void deleteById(Object id) {
        // 初始化时将把对象设置进缓存
        Example example = new Example(FrontHelpContent.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("typeId", id);
        // helpContentList = frontHelpContentMapper.queryFrontHelpForTypeName();
        List<FrontHelpContent> contents = frontHelpContentMapper.selectByExample(example);
        if (StringUtil.listIsNotBlank(contents)) {
            throw new UserInvalidException(Resources.getMessage("BASE_HELP_ERRER"));
        }
        mapper.deleteByPrimaryKey(id);
        cacheReturn();
    }

}
