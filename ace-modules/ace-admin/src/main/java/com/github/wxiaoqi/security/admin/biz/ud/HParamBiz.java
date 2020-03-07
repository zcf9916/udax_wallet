package com.github.wxiaoqi.security.admin.biz.ud;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.entity.ud.HParam;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.ud.HParamMapper;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ud 智能社区参数配置
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HParamBiz extends BaseBiz<HParamMapper, HParam> {

    public void insertSelective(HParam entity) {
        HParam param = new HParam();
        param.setUdKey(entity.getUdKey());
        param.setExchId(entity.getExchId());
        int count = mapper.selectCount(param);
        if (count >0){
            throw new UserInvalidException(Resources.getMessage("CONFIG_H_PRAAM"));
        }
        EntityUtils.setCreatAndUpdatInfo(entity);
        mapper.insertSelective(entity);
    }


    public void updateSelectiveById(HParam entity) {
        EntityUtils.setUpdatedInfo(entity);
        mapper.updateByPrimaryKeySelective(entity);

    }

}
