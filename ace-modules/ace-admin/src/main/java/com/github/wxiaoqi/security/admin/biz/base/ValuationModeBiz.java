package com.github.wxiaoqi.security.admin.biz.base;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.ValuationMode;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.admin.ValuationModeMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.vo.ValuationModeVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ValuationModeBiz extends BaseBiz<ValuationModeMapper, ValuationMode> {


    public List<ValuationMode> getValuationMode(Long id) {
        Example example = new Example(ValuationMode.class);
        example.createCriteria().andEqualTo("exchId", id);
        return this.selectByExample(example);
    }

    public void modifyValuationMode(Long id, Long exchId) {
        ValuationMode mode = new ValuationMode();
        mode.setExchId(exchId);
        mode.setDictDataId(id);
        ValuationMode mode2 = this.selectOne(mode);
        if (null == mode2) {
            mapper.insertSelective(mode);
        }
        cacheReturn();
    }

    public void removeValuationMode(Long id, Long exchId) {

        ValuationMode mode = new ValuationMode();
        //如果id为-1时 则根据交易所id 删除所有关联中间表
        if (id.equals(AdminCommonConstant.ROOT)) {
            mode.setExchId(exchId);
            mapper.delete(mode);
        } else {
            //或者删除单个
            mode.setExchId(exchId);
            mode.setDictDataId(id);
            mapper.delete(mode);
        }
        cacheReturn();
    }

    public void cacheReturn() {
        try {
            //清除缓存
            CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.VALUATION_MODE);
            Map<String, ArrayList<ValuationModeVo>> allMap = InstanceUtil.newHashMap();
            List<ValuationModeVo> list = mapper.cacheReturn();
            if (StringUtil.listIsNotBlank(list)) {
                list.forEach(vo -> {
                    if (allMap.get(vo.getExchId() + ":" + vo.getLanguageType()) == null) {
                        ArrayList<ValuationModeVo> arrayList = new ArrayList<>();
                        arrayList.add(vo);
                        allMap.put(vo.getExchId()+":"+ vo.getLanguageType(), arrayList);
                    } else {
                        ArrayList<ValuationModeVo> cfgCurrencyTransfers = allMap.get(vo.getExchId() + ":" + vo.getLanguageType());
                        cfgCurrencyTransfers.add(vo);
                    }
                });
            }
            allMap.forEach((k, v) -> {
                CacheUtil.getCache().set(Constants.CacheServiceType.VALUATION_MODE + k, v);
            });
            Map<String, ArrayList<ValuationModeVo>> allMap2 = InstanceUtil.newHashMap();
            list.forEach(vo -> {
               if (allMap2.get(vo.getExchId()) ==null){
                   ArrayList<ValuationModeVo> arrayList = new ArrayList<>();
                   arrayList.add(vo);
                   allMap2.put(vo.getExchId()+"",arrayList);
               }else {
                   ArrayList<ValuationModeVo> vos = allMap2.get(vo.getExchId());
                   vos.add(vo);
               }
            });

            allMap2.forEach((k, v) -> {
                CacheUtil.getCache().set(Constants.CacheServiceType.VALUATION + k, v);
            });
            logger.info("== 计价方式表:ValuationModeVo  缓存完成,缓存条数：{}", list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateDefaultSymbol(Long dictDataId, Long exchId, String symbol ) {
        //删除原有的默认值
        ValuationMode valuationMode = new ValuationMode();
        valuationMode.setExchId(exchId);
        List<ValuationMode> valuationModeList = mapper.select(valuationMode);
        if (!StringUtil.listIsNotBlank(valuationModeList)){
            throw new UserInvalidException(Resources.getMessage("CONFIG_VALUATION_MODE"));
        }
        valuationModeList.forEach(m -> {
            if (!StringUtils.isEmpty(m.getDefaultSymbol())){
                m.setDefaultSymbol(null);
                mapper.updateByPrimaryKey(m);
            }
        });
        //更新最新的默认计价方式
        Example example = new Example(ValuationMode.class);
        example.createCriteria().andEqualTo("exchId", exchId)
                .andEqualTo("dictDataId",dictDataId);
        List<ValuationMode> list = mapper.selectByExample(example);
        if (!StringUtil.listIsNotBlank(list)){
            throw new UserInvalidException(Resources.getMessage("CONFIG_VALUATION_MODE"));
        }
        ValuationMode mode = list.get(0);
        mode.setDefaultSymbol(symbol);
        mapper.updateByPrimaryKey(mode);
        cacheReturn();
    }

    public List<ValuationModeVo> selectValuationMode(Map<String, Object> params) {
        return mapper.selectValuationMode(params);
    }
}
