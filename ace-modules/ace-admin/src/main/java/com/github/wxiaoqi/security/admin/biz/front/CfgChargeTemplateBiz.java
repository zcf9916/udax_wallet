package com.github.wxiaoqi.security.admin.biz.front;

import java.util.List;

import com.github.wxiaoqi.security.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.entity.admin.CfgChargeTemplate;
import com.github.wxiaoqi.security.common.entity.admin.CfgCurrencyCharge;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.admin.CfgChargeTemplateMapper;
import com.github.wxiaoqi.security.common.mapper.admin.CfgCurrencyChargeMapper;
import com.github.wxiaoqi.security.common.util.EntityUtils;

@Service
@Transactional(rollbackFor = Exception.class)
public class CfgChargeTemplateBiz extends BaseBiz<CfgChargeTemplateMapper, CfgChargeTemplate> {
    @Autowired
    private CfgCurrencyChargeMapper cfgCurrencyChargeMapper;

    @Autowired
    private CfgCurrencyChargeBiz cfgCurrencyChargeBiz;

    @Autowired
    private TransferExchBiz transferExchBiz;

    @Autowired
    private CfgCurrencyTransferBiz cfgCurrencyTransferBiz;

    @Override
    public void updateSelectiveById(CfgChargeTemplate entity) {
        if (entity.getStatus().equals(EnableType.DISABLE.value())) {
            List<CfgCurrencyCharge> charges = cfgCurrencyChargeMapper.selectByChargeTemplate(entity.getId());
            if (!StringUtil.listIsBlank(charges)) {
                throw new UserInvalidException(Resources.getMessage("CONFIG_CHARGE_TEMPLATE_UPDATE"));
            }
        }
        EntityUtils.setUpdatedInfo(entity);
        mapper.updateByPrimaryKeySelective(entity);
        cfgCurrencyChargeBiz.cacheReturn();
        transferExchBiz.cacheReturn();
        cfgCurrencyTransferBiz.cacheReturn();
    }

    @Override
    public void deleteById(Object id) {
        List<CfgCurrencyCharge> charges = cfgCurrencyChargeMapper.selectByChargeTemplate((Long) id);
        if (charges.size() > 0) {
            throw new UserInvalidException(Resources.getMessage("CONFIG_CHARGE_TEMPLATE_DELETE"));
        }
        mapper.deleteByPrimaryKey(id);
    }

}
