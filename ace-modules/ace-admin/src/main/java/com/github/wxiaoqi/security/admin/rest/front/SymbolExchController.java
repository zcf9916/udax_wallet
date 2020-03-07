package com.github.wxiaoqi.security.admin.rest.front;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.admin.biz.front.SymbolExchBiz;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.SymbolExch;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import com.github.wxiaoqi.security.common.util.Query;
import com.github.wxiaoqi.security.common.vo.SymbolCurrencyCharge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/symbolExch")
public class SymbolExchController extends BaseController<SymbolExchBiz, SymbolExch> {

    @Autowired
    private SymbolExchBiz symbolExchBiz;

    @RequestMapping(value = "/symbolCurrencyCharge", method = RequestMethod.GET)
    public TableResultResponse<SymbolCurrencyCharge> getSymbolCurrencyCharge(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<SymbolCurrencyCharge> charge = symbolExchBiz.getSymbolCurrencyCharge(params);
        return new TableResultResponse<SymbolCurrencyCharge>(result.getTotal(), charge);
    }

    @RequestMapping(value = "/{id}/symbol", method = RequestMethod.GET)
    public ObjectRestResponse<List<SymbolExch>> getSymbolExch(@PathVariable Long id) {
        return new ObjectRestResponse().data(symbolExchBiz.getSymbolExchBiz(id)).rel(true);
    }

    @RequestMapping(value = "/{id}/symbol/add", method = RequestMethod.POST)
    public ObjectRestResponse addElementAuthority(@PathVariable Long id, Long exchId) {
        symbolExchBiz.modifySymbolExch(id, exchId);
        return new ObjectRestResponse().rel(true);
    }


    @RequestMapping(value = "/{id}/symbol/remove", method = RequestMethod.POST)
    public ObjectRestResponse removeElementAuthority(@PathVariable Long id, Long exchId) {
        symbolExchBiz.removeSymbolExch(id, exchId);
        return new ObjectRestResponse().rel(true);
    }


    /**
     * 查询币种充值锁定信息
     *
     * @param id
     * @param exchId
     * @return
     */

    @RequestMapping(value = "/{symbolId}/symbolExch", method = RequestMethod.GET)
    public ObjectRestResponse<SymbolExch> getSymbolExchInfo(@PathVariable Long symbolId, Long exchId) {
        ObjectRestResponse<SymbolExch> entityObjectRestResponse = new ObjectRestResponse<SymbolExch>();
        SymbolExch exch = new SymbolExch();
        exch.setExchId(exchId);
        exch.setSymbolId(symbolId);
        entityObjectRestResponse.data(baseBiz.selectOne(exch));
        return entityObjectRestResponse;
    }

    @RequestMapping(value = "/lockAll", method = RequestMethod.GET)
    public List<SymbolExch> getLockAll() {
        return baseBiz.getLockSymbolList(BaseContextHandler.getExId());
    }


}
