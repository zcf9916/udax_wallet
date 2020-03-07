package com.github.wxiaoqi.security.admin.biz.casino;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.casino.CasinoParam;
import com.github.wxiaoqi.security.common.entity.front.DcAssetAccountLog;
import com.github.wxiaoqi.security.common.mapper.casino.CasinoParamMapper;
import com.github.wxiaoqi.security.common.mapper.front.DcAssetAccountLogMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CasinoParamBiz extends BaseBiz<CasinoParamMapper, CasinoParam> {

    public void  cacheReturn(){
        List<CasinoParam> list = mapper.selectAll();
        if (StringUtil.listIsBlank(list)){
            return;
        }
        list.forEach(casinoParam -> {
            CacheUtil.getCache().set(Constants.CACHE_NAMESPACE+casinoParam.getCasinoKey()+casinoParam.getExchId(), casinoParam);
        });
    }

    @Override
    public void insertSelective(CasinoParam entity) {
        super.insertSelective(entity);
        cacheReturn();
    }

    @Override
    public void updateSelectiveById(CasinoParam entity) {
        super.updateSelectiveById(entity);
        cacheReturn();
    }

    @Override
    public void deleteById(Object id) {
        super.deleteById(id);
        cacheReturn();
    }
}
