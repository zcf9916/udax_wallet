package com.github.wxiaoqi.security.admin.biz.base;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.Param;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.mapper.admin.ParamMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.EntityUtils;

@Service
@Transactional(rollbackFor = Exception.class)
public class ParamBiz extends BaseBiz<ParamMapper, Param> {




    public void insertSelective(Param entity) {
        EntityUtils.setCreatAndUpdatInfo(entity);
        mapper.insertSelective(entity);
        cacheReturn();
    }

    public void updateSelectiveById(Param entity) {
        EntityUtils.setUpdatedInfo(entity);
        mapper.updateByPrimaryKeySelective(entity);
        cacheReturn();
    }

    public void deleteById(Object id) {
        mapper.deleteByPrimaryKey(id);
        cacheReturn();
    }

    public List<Param> cacheReturn() {
        List<Param> list = null;
        CacheUtil.getCache().deleteAllKeys(Constants.BaseParam.SYSPARAM);
        try {
            Param param = new Param();
            param.setStatus(EnableType.ENABLE.value());
            list = mapper.select(param);
            list.forEach((p -> {
                CacheUtil.getCache().set(Constants.BaseParam.SYSPARAM +p.getParamKey(), p);
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
