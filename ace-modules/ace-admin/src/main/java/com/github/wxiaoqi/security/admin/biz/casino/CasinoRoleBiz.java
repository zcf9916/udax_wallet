package com.github.wxiaoqi.security.admin.biz.casino;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.casino.CasinoRole;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.casino.CasinoRoleMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CasinoRoleBiz extends BaseBiz<CasinoRoleMapper, CasinoRole> {


    @Override
    public void insertSelective(CasinoRole entity) {
        if (BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            //如果是ADMIN 必须选择交易所
            if (entity.getExchId()==null){
                throw new UserInvalidException(Resources.getMessage("CONFIG_EXCH_ID"));
            }
        }else {
            entity.setExchId(BaseContextHandler.getExId());
        }
        CasinoRole role = new CasinoRole();
        role.setExchId(entity.getExchId());
        role.setType(entity.getType());
        CasinoRole casinoRole = mapper.selectOne(role);
        if (casinoRole !=null){
            throw new UserInvalidException(Resources.getMessage("CASINO_ROLE_ERROR"));
        }
        entity.setCmsRate(entity.getCmsRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        super.insertSelective(entity);
    }

    @Override
    public void updateSelectiveById(CasinoRole entity) {
        entity.setCmsRate(entity.getCmsRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        super.updateSelectiveById(entity);
    }
}
