package com.github.wxiaoqi.security.admin.biz.ud;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.ud.HBonusConfig;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.ud.HBonusConfigMapper;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Transactional(rollbackFor = Exception.class)
public class HBonusConfigBiz extends BaseBiz<HBonusConfigMapper, HBonusConfig> {

    public void insertSelective(HBonusConfig entity) {
        if (BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            if (entity.getExchId()== null){
                throw new UserInvalidException(Resources.getMessage("CONFIG_EXCH_ID"));
            }
        }else {
            entity.setExchId(BaseContextHandler.getExId());
        }

        HBonusConfig config = new HBonusConfig();
        config.setLevel(entity.getLevel());
        config.setExchId(entity.getExchId());
        int count = mapper.selectCount(config);
        if (count > 0){
            throw new UserInvalidException(Resources.getMessage("CONFIG_BONUS_CONFIG"));
        }
        entity.setProfitRate(entity.getProfitRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        EntityUtils.setCreatAndUpdatInfo(entity);
        mapper.insertSelective(entity);
    }

    public void updateSelectiveById(HBonusConfig entity) {
        EntityUtils.setUpdatedInfo(entity);
        entity.setProfitRate(entity.getProfitRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        mapper.updateByPrimaryKeySelective(entity);
    }
}
