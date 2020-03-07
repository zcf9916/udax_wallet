package com.github.wxiaoqi.security.admin.biz.casino;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.casino.CasinoRebateConfig;
import com.github.wxiaoqi.security.common.entity.front.CmsConfig;
import com.github.wxiaoqi.security.common.entity.front.DcAssetAccountLog;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.casino.CasinoRebateConfigMapper;
import com.github.wxiaoqi.security.common.mapper.front.DcAssetAccountLogMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CasinoRebateConfigBiz extends BaseBiz<CasinoRebateConfigMapper, CasinoRebateConfig> {

    @Override
    public void insertSelective(CasinoRebateConfig entity) {
        if (BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            //如果是ADMIN 必须选择交易所
            if (entity.getExchId()==null){
                throw new UserInvalidException(Resources.getMessage("CONFIG_EXCH_ID"));
            }
        }else {
            entity.setExchId(BaseContextHandler.getExId());
        }
        CasinoRebateConfig config = new CasinoRebateConfig();
        config.setExchId(entity.getExchId());
        config.setType(entity.getType());
        if (mapper.selectOne(config) !=null){
            throw new UserInvalidException(Resources.getMessage("CASINO_REBATE_CONFIG"));
        }
        entity.setCmsRate(entity.getCmsRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        verifyScale(entity);
        entity.setCreateTime(new Date());
        super.insertSelective(entity);
    }

    @Override
    public void updateSelectiveById(CasinoRebateConfig entity) {
        entity.setCmsRate(entity.getCmsRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        verifyScale(entity);
        super.updateSelectiveById(entity);
    }

    public void verifyScale(CasinoRebateConfig entity) {
        BigDecimal totalBigDecimal = BigDecimal.ZERO;
        //校验用户分成比例不能超过100%
        Example example = new Example(CmsConfig.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("exchId", entity.getExchId());
        List<CasinoRebateConfig> list = this.selectByExample(example);
        if (StringUtil.listIsBlank(list)) {
            return;
        }
        //新增
        if (entity.getId() == null) {
            entity.setCmsRate(entity.getCmsRate());
            list.add(entity);
        }
        for (CasinoRebateConfig cmsConfig : list) {
            //更新
            if (entity.getId() != null) {
                //主键相同时,add最新的比例
                if (cmsConfig.getId().equals(entity.getId())) {
                    totalBigDecimal = totalBigDecimal.add(entity.getCmsRate());
                } else {
                    totalBigDecimal = totalBigDecimal.add(cmsConfig.getCmsRate());
                }
            } else {
                //新增时汇总分配比例
                totalBigDecimal = totalBigDecimal.add(cmsConfig.getCmsRate());
            }
        }
        if (totalBigDecimal.compareTo(new BigDecimal("1")) == 1) {
            throw new UserInvalidException(Resources.getMessage("REBATE_CONFIG_ERROR"));
        }

    }

    public void  cacheReturn(){
        CasinoRebateConfig rebateConfig = new CasinoRebateConfig();
        rebateConfig.setEnable(EnableType.ENABLE.value());
        List<CasinoRebateConfig> list = mapper.select(rebateConfig);
        if (StringUtil.listIsBlank(list)){
            return;
        }
        Map<Long, List<CasinoRebateConfig>> longListMap = list.stream().collect(Collectors.groupingBy(CasinoRebateConfig::getExchId));
        longListMap.forEach((aLong, casinoRebateConfigs) -> {
            CacheUtil.getCache().set(Constants.CacheServiceType.CASINO_REBATE_CONFIG+aLong, (ArrayList)casinoRebateConfigs);
        });

    }
}
