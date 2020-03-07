package com.github.wxiaoqi.security.admin.biz.front;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.CfgDescriptionTemplate;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.admin.CfgDescriptionTemplateMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import com.github.wxiaoqi.security.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class CfgDescriptionTemplateBiz extends BaseBiz<CfgDescriptionTemplateMapper, CfgDescriptionTemplate> {

    @Autowired
    private CfgSymbolDescriptionBiz cfgSymbolDescriptionBiz;

    @Autowired
    private CfgCurrencyChargeBiz cfgCurrencyChargeBiz;

    public void insertSelective(CfgDescriptionTemplate entity) {
        CfgDescriptionTemplate template = new CfgDescriptionTemplate();
        template.setLanguageType(entity.getLanguageType());
        List<CfgDescriptionTemplate> templates = mapper.select(template);
        if (StringUtil.listIsNotBlank(templates)){
            throw new UserInvalidException(Resources.getMessage("CONFIG_DESCRIPTION_TEMPLATE"));
        }
        EntityUtils.setCreatAndUpdatInfo(entity);
        mapper.insertSelective(entity);
        cacheReturn();
        cfgSymbolDescriptionBiz.cacheReturn();
        cfgCurrencyChargeBiz.cacheReturn();
    }

    public void updateSelectiveById(CfgDescriptionTemplate entity) {
        EntityUtils.setUpdatedInfo(entity);
        mapper.updateByPrimaryKeySelective(entity);
        cacheReturn();
        cfgSymbolDescriptionBiz.cacheReturn();
        cfgCurrencyChargeBiz.cacheReturn();
    }

    public void deleteById(Object id) {
        mapper.deleteByPrimaryKey(id);
        cacheReturn();
    }

    public List<CfgDescriptionTemplate> cacheReturn() {
        try {
            CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.CFG_DESCRIPTION_TEMPLATE);
            List<CfgDescriptionTemplate> templates = mapper.selectAll();
            if (!StringUtil.listIsBlank(templates)) {
                templates.forEach(cfgDescriptionTemplate -> {
                    CacheUtil.getCache().set(Constants.CacheServiceType.CFG_DESCRIPTION_TEMPLATE + cfgDescriptionTemplate.getLanguageType(), cfgDescriptionTemplate);
                });
                logger.info("==代币描述信息模板表:cfgDescriptionTemplate  缓存完成,缓存条数：{}", templates.size());
                return templates;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
