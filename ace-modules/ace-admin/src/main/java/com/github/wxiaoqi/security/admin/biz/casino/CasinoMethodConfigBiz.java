package com.github.wxiaoqi.security.admin.biz.casino;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.casino.CasinoMethodConfig;
import com.github.wxiaoqi.security.common.entity.casino.CasinoRebateConfig;
import com.github.wxiaoqi.security.common.entity.front.CmsConfig;
import com.github.wxiaoqi.security.common.entity.front.DcAssetAccountLog;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.casino.CasinoMethodConfigMapper;
import com.github.wxiaoqi.security.common.mapper.front.DcAssetAccountLogMapper;
import com.github.wxiaoqi.security.common.util.StringUtil;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Service
public class CasinoMethodConfigBiz extends BaseBiz<CasinoMethodConfigMapper, CasinoMethodConfig> {


    @Override
    public void insertSelective(CasinoMethodConfig entity) {
        if (BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            //如果是ADMIN 必须选择交易所
            if (entity.getExchId()==null){
                throw new UserInvalidException(Resources.getMessage("CONFIG_EXCH_ID"));
            }
        }else {
            entity.setExchId(BaseContextHandler.getExId());
        }
        CasinoMethodConfig config = new CasinoMethodConfig();
        config.setExchId(entity.getExchId());
        config.setType(entity.getType());
        if (mapper.selectOne(config) !=null){
            throw new UserInvalidException(Resources.getMessage("CASINO_METHOD_CONFIG_ERROR"));
        }
        BigDecimal bigDecimal =BigDecimal.ZERO;
        if (CasinoMethodConfig.CasinoMethodConfigType.PROPORTIONATE.value().equals(entity.getType())){
            entity.setUserCmsRate(entity.getUserCmsRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            entity.setPlatformCmsRate(entity.getPlatformCmsRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            entity.setFixedValue(bigDecimal);
            if (entity.getUserCmsRate().add(entity.getPlatformCmsRate()).compareTo(new BigDecimal("1")) == 1) {
                throw new UserInvalidException(Resources.getMessage("CASINO_METHOD_CONFIG"));
            }
            if (entity.getUserCmsRate().add(entity.getPlatformCmsRate()).compareTo(new BigDecimal("1")) == -1) {
                throw new UserInvalidException(Resources.getMessage("CASINO_METHOD_CONFIG"));
            }
        }else if (CasinoMethodConfig.CasinoMethodConfigType.ON_GAMBLING.value().equals(entity.getType())){
            entity.setUserCmsRate(bigDecimal);
            entity.setPlatformCmsRate(bigDecimal);

        }
        entity.setDirectUserRate(entity.getDirectUserRate().divide(BigDecimal.valueOf(100),4,RoundingMode.HALF_UP));
        entity.setIndirectUserRate(entity.getIndirectUserRate().divide(BigDecimal.valueOf(100),4,RoundingMode.HALF_UP));
        if (entity.getPlatformCmsRate().compareTo(new BigDecimal("1")) == 1) {
            throw new UserInvalidException(Resources.getMessage("CASINO_METHOD_CONFIG"));
        }
        entity.setCreateTime(new Date());
        super.insertSelective(entity);
    }

    @Override
    public void updateSelectiveById(CasinoMethodConfig entity) {
        BigDecimal bigDecimal =BigDecimal.ZERO;
        if (CasinoMethodConfig.CasinoMethodConfigType.PROPORTIONATE.value().equals(entity.getType())){
            entity.setUserCmsRate(entity.getUserCmsRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            entity.setPlatformCmsRate(entity.getPlatformCmsRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            entity.setFixedValue(bigDecimal);

            if (entity.getUserCmsRate().add(entity.getPlatformCmsRate()).compareTo(new BigDecimal("1")) == 1) {
                throw new UserInvalidException(Resources.getMessage("CASINO_METHOD_CONFIG"));
            }
            if (entity.getUserCmsRate().add(entity.getPlatformCmsRate()).compareTo(new BigDecimal("1")) == -1) {
                throw new UserInvalidException(Resources.getMessage("CASINO_METHOD_CONFIG"));
            }

        }else if (CasinoMethodConfig.CasinoMethodConfigType.ON_GAMBLING.value().equals(entity.getType())){
            entity.setUserCmsRate(bigDecimal);
            entity.setPlatformCmsRate(bigDecimal);
            if (entity.getPlatformCmsRate().compareTo(new BigDecimal("1")) == 1) {
                throw new UserInvalidException(Resources.getMessage("CASINO_METHOD_CONFIG"));
            }
        }
        entity.setDirectUserRate(entity.getDirectUserRate().divide(BigDecimal.valueOf(100),4,RoundingMode.HALF_UP));
        entity.setIndirectUserRate(entity.getIndirectUserRate().divide(BigDecimal.valueOf(100),4,RoundingMode.HALF_UP));
        if (entity.getPlatformCmsRate().compareTo(new BigDecimal("1")) == 1) {
            throw new UserInvalidException(Resources.getMessage("CASINO_METHOD_CONFIG"));
        }
        super.updateSelectiveById(entity);
    }


}
