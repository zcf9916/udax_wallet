package com.github.wxiaoqi.security.admin.biz.front;


import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.BaseVersion;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.admin.BaseVersionMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class BaseVersionBiz extends BaseBiz<BaseVersionMapper, BaseVersion> {


    public void insertSelective(BaseVersion entity) {
        BaseVersion version = new BaseVersion();
        version.setExchId(entity.getExchId());
        version.setVersionChannel(entity.getVersionChannel());
        List<BaseVersion> list = mapper.select(version);
        if (StringUtil.listIsNotBlank(list)) {
            throw new UserInvalidException(Resources.getMessage("CONFIG_BASE_VERSION"));
        }
        EntityUtils.setCreatAndUpdatInfo(entity);
        mapper.insertSelective(entity);
        cacheReturn();
    }

    public void updateSelectiveById(BaseVersion entity) {
        EntityUtils.setUpdatedInfo(entity);
        mapper.updateByPrimaryKeySelective(entity);
        cacheReturn();
    }

    public void deleteById(Object id) {
        mapper.deleteByPrimaryKey(id);
        cacheReturn();
    }


    public List<BaseVersion> cacheReturn() {
        try {
            CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.BASE_VERSION);
            List<BaseVersion> list = mapper.selectAll();
            //根据版本渠道缓存  android or ios
            list.forEach(baseVersion -> {
                CacheUtil.getCache().set(Constants.CacheServiceType.BASE_VERSION + baseVersion.getVersionChannel() + ":" + baseVersion.getExchId(), baseVersion);
            });
            logger.info(" ==版本渠道表:BaseVersion 缓存完成,缓存条数：{}", list.size());
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
