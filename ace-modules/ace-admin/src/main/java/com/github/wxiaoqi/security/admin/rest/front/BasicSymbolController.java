package com.github.wxiaoqi.security.admin.rest.front;

import com.github.wxiaoqi.security.admin.biz.front.BasicSymbolBiz;
import com.github.wxiaoqi.security.admin.biz.front.CfgCurrencyChargeBiz;
import com.github.wxiaoqi.security.common.entity.admin.BasicSymbol;
import com.github.wxiaoqi.security.common.entity.admin.CfgCurrencyCharge;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("basicSymbol")
public class BasicSymbolController extends BaseController<BasicSymbolBiz, BasicSymbol> {


    @Autowired
    private CfgCurrencyChargeBiz cfgCurrencyChargeBiz;


    @RequestMapping(value = "/pageAll", method = RequestMethod.GET)
    public TableResultResponse<BasicSymbol> pageAll(@RequestParam Map<String, Object> params) {
        return super.queryListByCustomPage(params);
    }


    @RequestMapping(value = "/service", method = RequestMethod.POST)
    public ObjectRestResponse<CfgCurrencyCharge> addCurrencyCharge(@RequestBody CfgCurrencyCharge entity) {
        if (entity.getId() == null) {
            cfgCurrencyChargeBiz.insert(entity);
        } else {
            cfgCurrencyChargeBiz.updateCharge(entity);
        }
        return new ObjectRestResponse<CfgCurrencyCharge>();
    }


    @RequestMapping(value = "/service/{id}", method = RequestMethod.GET)
    public ObjectRestResponse<CfgCurrencyCharge> getCurrencyCharge(@PathVariable Long id, Long exchId) {
        ObjectRestResponse<CfgCurrencyCharge> entityObjectRestResponse = new ObjectRestResponse<CfgCurrencyCharge>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("symbolId", id);
        map.put("exchId", exchId);
        CfgCurrencyCharge charge = cfgCurrencyChargeBiz.selectChargeByIdOrExchId(map);
        if (charge == null) {
            entityObjectRestResponse.rel(false);
        }
        entityObjectRestResponse.data(charge);
        return entityObjectRestResponse;
    }

    /**
     * 查询可用于报价币种集合
     *
     * @return
     */
    @RequestMapping(value = "/quoteAll", method = RequestMethod.GET)
    public List<BasicSymbol> quoteAll() {
        Example example = new Example(BasicSymbol.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isQuote", EnableType.ENABLE.value());
        return baseBiz.selectByExample(example);
    }

    /**
     * 查询不可用于报价币种
     *
     * @return
     */
    @RequestMapping(value = "/AllList", method = RequestMethod.GET)
    public List<BasicSymbol> quoteAllList() {
        Example example = new Example(BasicSymbol.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isQuote", EnableType.DISABLE.value());
        return baseBiz.selectByExample(example);
    }

    /**
     * 根据当前登录角色查询报价货币源
     */
    @RequestMapping(value = "/userQuote", method = RequestMethod.GET)
    public Map<String, Object> getUserQuote() {
        return baseBiz.getUserQuote();
    }

    /**
     * 根据交易所获取币种
     *
     * @return
     */
    @RequestMapping(value = "exchAll", method = RequestMethod.GET)
    public List<BasicSymbol> exchAll() {
        return baseBiz.getExchBasicSymbol();
    }

    /**
     * 重复的币种集合
     * @return
     */
    @RequestMapping(value = "repeatSymbolList", method = RequestMethod.GET)
    public List<BasicSymbol> repeatSymbolList() {
        return baseBiz.repeatSymbolList();
    }


}
