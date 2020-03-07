package com.github.wxiaoqi.security.admin.biz.front;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.FrontCountry;
import com.github.wxiaoqi.security.common.entity.front.CmsConfig;
import com.github.wxiaoqi.security.common.enums.CommissionConfigType;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.front.CmsConfigMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
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
public class CmsConfigBiz extends BaseBiz<CmsConfigMapper, CmsConfig> {

    @Override
    public void insertSelective(CmsConfig entity) {
        CmsConfig config = new CmsConfig();
        config.setExchId(entity.getExchId());
        config.setType(entity.getType());
        CmsConfig dbConfig = mapper.selectOne(config);
        entity.setCmsRate(entity.getCmsRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        if (dbConfig != null) {
            throw new UserInvalidException(Resources.getMessage("CMS_CONFIG_ERROR"));
        }

        if (CommissionConfigType.RECOMMENDED_USER_SHARE.value().equals(entity.getType())) {
            //类型为4 生成白标分成数据
            config.setType(CommissionConfigType.EXCH_PROPORTION.value());
            config.setCmsRate(new BigDecimal("1").subtract(entity.getCmsRate()));
            config.setCreateTime(new Date());
            config.setEnable(EnableType.ENABLE.value());
            super.insertSelective(config);
        } else {
            verifyScale(entity.getExchId(), entity);
        }
        entity.setCreateTime(new Date());
        super.insertSelective(entity);
        cacheReturn();
    }


    @Override
    public void updateSelectiveById(CmsConfig entity) {
        //同步更新白标分成比例
        entity.setCmsRate(entity.getCmsRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        if (CommissionConfigType.RECOMMENDED_USER_SHARE.value().equals(entity.getType())) {
            CmsConfig config = new CmsConfig();
            config.setExchId(entity.getExchId());
            config.setType(CommissionConfigType.EXCH_PROPORTION.value());
            CmsConfig dbConfig = mapper.selectOne(config);
            dbConfig.setCmsRate(new BigDecimal("1").subtract(entity.getCmsRate()));
            super.updateSelectiveById(dbConfig);
        } else {
            verifyScale(entity.getExchId(), entity);
        }
        super.updateSelectiveById(entity);
        cacheReturn();
    }

    public void verifyScale(Long exchId, CmsConfig entity) {
        BigDecimal totalBigDecimal = BigDecimal.ZERO;
        //校验用户分成比例不能超过100%
        Example example = new Example(CmsConfig.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("exchId", exchId)
                .andNotEqualTo("parentId", AdminCommonConstant.ROOT);
        List<CmsConfig> list = this.selectByExample(example);
        if (StringUtil.listIsBlank(list)) {
            return;
        }
        //新增
        if (entity.getId() == null) {
            entity.setCmsRate(entity.getCmsRate());
            list.add(entity);
        }
        for (CmsConfig cmsConfig : list) {
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
            throw new UserInvalidException(Resources.getMessage("CMS_CONFIG_USER_ERROR"));
        }


    }

    public void deleteById(Object id) {
        CmsConfig dbConfig = mapper.selectByPrimaryKey(id);
        //删除下级分成
        CmsConfig cmsConfig = new CmsConfig();
        if (CommissionConfigType.EXCH_PROPORTION.value().equals(dbConfig.getType())) {
            //判断当前删除的是白标分成 者根据交易所删除
            cmsConfig.setExchId(dbConfig.getExchId());
        } else {
            //其他者同时删除下级分成
            cmsConfig.setParentId((Long) id);
        }
        mapper.deleteByPrimaryKey(id);
        mapper.delete(cmsConfig);
        cacheReturn();
    }

    public void  cacheReturn(){
        CmsConfig config = new CmsConfig();
        config.setEnable(EnableType.ENABLE.value());
        List<CmsConfig> configList = mapper.select(config);
        Map<Long, List<CmsConfig>> configListMap = configList.stream().collect(Collectors.groupingBy(CmsConfig::getExchId));
        configListMap.forEach((exchId, cmsConfigs) -> {
            CacheUtil.getCache().set(Constants.CacheServiceType.CMS_CONFIG_BIZ+exchId, (ArrayList<CmsConfig>) cmsConfigs);
        });

    }

}