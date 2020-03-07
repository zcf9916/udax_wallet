package com.github.wxiaoqi.security.admin.biz.ud;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.ValuationMode;
import com.github.wxiaoqi.security.common.entity.admin.WhiteExchInfo;
import com.github.wxiaoqi.security.common.entity.ud.HPurchaseLevel;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.admin.ValuationModeMapper;
import com.github.wxiaoqi.security.common.mapper.admin.WhiteExchInfoMapper;
import com.github.wxiaoqi.security.common.mapper.ud.HPurchaseLevelMapper;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import com.github.wxiaoqi.security.common.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class HPurchaseLevelBiz extends BaseBiz<HPurchaseLevelMapper, HPurchaseLevel> {

    @Autowired
    private WhiteExchInfoMapper whiteExchInfoMapper;

    public void insertSelective(HPurchaseLevel entity) {
        if (BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            if (entity.getExchId() == null) {
                throw new UserInvalidException(Resources.getMessage("CONFIG_EXCH_ID"));
            }
        } else {
            entity.setExchId(BaseContextHandler.getExId());
        }
        //获取交易所属性配置中结算币种
        WhiteExchInfo info = whiteExchInfoMapper.selectByPrimaryKey(entity.getExchId());



        if (!StringUtils.isNotBlank(info.getLtCode())) {
            throw new UserInvalidException(Resources.getMessage("CONFIG_PURCHASE_SYMBOL"));
        }
        HPurchaseLevel level = new HPurchaseLevel();
        level.setName(entity.getName());
        level.setExchId(entity.getExchId());
        level.setLevel(entity.getLevel());
        int count = mapper.selectCount(level);
        if (count > 0) {
            throw new UserInvalidException(Resources.getMessage("CONFIG_PURCHASE_LEVEL"));
        }

        EntityUtils.setCreatAndUpdatInfo(entity);
        entity.setSymbol(info.getLtCode());
        entity.setEarliestStartTime(new Date()); //当前时间
        entity.setInterest(entity.getInterest().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        entity.setIsOpen(EnableType.ENABLE.value()); //默认开启
        mapper.insert(entity);
    }


    public void updateSelectiveById(HPurchaseLevel entity) {

        HPurchaseLevel level = mapper.selectByPrimaryKey(entity.getId());
        if (!entity.getLevel().equals(level.getLevel())){
            HPurchaseLevel purchaseLevel = new HPurchaseLevel();
            purchaseLevel.setExchId(entity.getExchId());
            purchaseLevel.setLevel(entity.getLevel());
            int count = mapper.selectCount(purchaseLevel);
            if (count > 0) {
                throw new UserInvalidException(Resources.getMessage("CONFIG_PURCHASE_LEVEL"));
            }
        }
        EntityUtils.setUpdatedInfo(entity);
        entity.setInterest(entity.getInterest().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        mapper.updateByPrimaryKeySelective(entity);
    }


    //根据当前投资量选出最接近的投资等级  (手动结算计算分成时所用，复用前端方法，前端更改，后台需同步更改)
    public HPurchaseLevel getProximalLevel(BigDecimal currentAmount) {
        //过滤出满足条件的
        Optional<HPurchaseLevel> list = mapper.selectAll().stream().filter((l) -> l.getAmountLimit().compareTo(currentAmount) <= 0)
                .max(Comparator.comparing(HPurchaseLevel::getAmountLimit));
        return list.get();
    }
}
