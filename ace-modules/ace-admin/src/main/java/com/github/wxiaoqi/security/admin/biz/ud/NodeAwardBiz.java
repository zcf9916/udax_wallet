package com.github.wxiaoqi.security.admin.biz.ud;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.ValuationMode;
import com.github.wxiaoqi.security.common.entity.admin.WhiteExchInfo;
import com.github.wxiaoqi.security.common.entity.ud.HNodeAward;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.admin.ValuationModeMapper;
import com.github.wxiaoqi.security.common.mapper.admin.WhiteExchInfoMapper;
import com.github.wxiaoqi.security.common.mapper.ud.HNodeAwardMapper;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import com.github.wxiaoqi.security.common.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class NodeAwardBiz extends BaseBiz<HNodeAwardMapper, HNodeAward> {

    @Autowired
    private WhiteExchInfoMapper whiteExchInfoMapper;

    public void insertSelective(HNodeAward entity) {

        if (BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            if (entity.getExchId()== null){
                throw new UserInvalidException(Resources.getMessage("CONFIG_EXCH_ID"));
            }
        }else {
            entity.setExchId(BaseContextHandler.getExId());
        }

        //获取交易所属性配置中结算币种
        WhiteExchInfo info = whiteExchInfoMapper.selectByPrimaryKey(entity.getExchId());
        if (!StringUtils.isNotBlank(info.getLtCode())) {
            throw new UserInvalidException(Resources.getMessage("CONFIG_PURCHASE_SYMBOL"));
        }
        HNodeAward nodeAward = new HNodeAward();
        nodeAward.setRemark(entity.getRemark());
        nodeAward.setExchId(entity.getExchId());
        int count = mapper.selectCount(nodeAward);
        if (count > 0) {
            throw new UserInvalidException(Resources.getMessage("CONFIG_NODE_AWARD"));
        }
        entity.setBaseProfitRate(entity.getBaseProfitRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        entity.setRate(entity.getRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        entity.setGlobalRate(entity.getGlobalRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        entity.setSymbol(info.getLtCode());
        EntityUtils.setCreatAndUpdatInfo(entity);
        mapper.insertSelective(entity);
    }

    public void updateSelectiveById(HNodeAward entity) {
        entity.setBaseProfitRate(entity.getBaseProfitRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        entity.setRate(entity.getRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        entity.setGlobalRate(entity.getGlobalRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        EntityUtils.setUpdatedInfo(entity);
        mapper.updateByPrimaryKeySelective(entity);
    }

    public List<HNodeAward> selectListAll() {
        return mapper.selectListAll();
    }

}
