package com.github.wxiaoqi.security.admin.biz.front;

import com.github.wxiaoqi.security.admin.biz.front.BasicSymbolBiz;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.entity.admin.SymbolExch;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.mapper.admin.SymbolExchMapper;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import com.github.wxiaoqi.security.common.vo.SymbolCurrencyCharge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class SymbolExchBiz extends BaseBiz<SymbolExchMapper, SymbolExch> {


    @Autowired
    private BasicSymbolBiz basicSymbolBiz;

    /**
     * 根据交易所id获取中间表集合
     *
     * @param id
     * @return
     */
    public List<SymbolExch> getSymbolExchBiz(Long id) {
        Example example = new Example(SymbolExch.class);
        example.createCriteria().andEqualTo("exchId", id);
        return this.selectByExample(example);
    }


    public List<SymbolExch> getLockSymbolList(Long exchId){
        return mapper.getLockSymbolList(exchId, EnableType.ENABLE.value());
    }
    /**
     * 币种授权交易所（add）
     *
     * @param id
     * @param exchId
     */
    public void modifySymbolExch(Long id, Long exchId) {
        SymbolExch symbolExch = new SymbolExch();
        symbolExch.setExchId(exchId);
        symbolExch.setSymbolId(id);
        SymbolExch exch = this.selectOne(symbolExch);
        if (exch == null) {
            EntityUtils.setCreatAndUpdatInfo(symbolExch);
            this.insertSelective(symbolExch);
        }
        basicSymbolBiz.cacheReturn();
    }

    /**
     * 删除授权
     *
     * @param id
     * @param exchId
     */
    public void removeSymbolExch(Long id, Long exchId) {
        SymbolExch symbolExch = new SymbolExch();
        //如果id为-1时 则根据交易所id 删除所有关联中间表
        if (id.equals(AdminCommonConstant.ROOT)) {
            symbolExch.setExchId(exchId);
            this.delete(symbolExch);
        } else {
            //或者删除单个
            symbolExch.setExchId(exchId);
            symbolExch.setSymbolId(id);
            this.delete(symbolExch);
        }
        basicSymbolBiz.cacheReturn();
    }

    public List<SymbolCurrencyCharge> getSymbolCurrencyCharge(Map<String, Object> params) {
        return mapper.selectSymbolAndChange(params);
    }
}
