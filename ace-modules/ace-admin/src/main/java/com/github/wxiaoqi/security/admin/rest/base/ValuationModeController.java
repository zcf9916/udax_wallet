package com.github.wxiaoqi.security.admin.rest.base;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.admin.biz.base.ValuationModeBiz;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.ValuationMode;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import com.github.wxiaoqi.security.common.util.Query;
import com.github.wxiaoqi.security.common.vo.ValuationModeVo;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/valuationMode")
public class ValuationModeController extends BaseController<ValuationModeBiz, ValuationMode> {


    @RequestMapping(value = "/{id}/mode", method = RequestMethod.GET)
    public ObjectRestResponse<List<ValuationMode>> getValuationMode(@PathVariable Long id) {
        return new ObjectRestResponse().data(baseBiz.getValuationMode(id)).rel(true);
    }


    @RequestMapping(value = "/{id}/mode/add", method = RequestMethod.POST)
    public ObjectRestResponse addElementAuthority(@PathVariable Long id, Long exchId) {
        baseBiz.modifyValuationMode(id, exchId);
        return new ObjectRestResponse().rel(true);
    }


    @RequestMapping(value = "/{id}/mode/remove", method = RequestMethod.POST)
    public ObjectRestResponse removeElementAuthority(@PathVariable Long id, Long exchId) {
        baseBiz.removeValuationMode(id, exchId);
        return new ObjectRestResponse().rel(true);
    }

    @RequestMapping(value = "/{id}/mode/defaultSymbol", method = RequestMethod.POST)
    public ObjectRestResponse updateDefaultSymbol(@PathVariable Long id, Long exchId, String symbol) {
        baseBiz.updateDefaultSymbol(id, exchId, symbol);
        return new ObjectRestResponse().rel(true);
    }


    @RequestMapping(value = "/pageQuery", method = RequestMethod.GET)
    public TableResultResponse<ValuationModeVo> getList(@RequestParam Map<String, Object> params) {
        params.put("dictType", Constants.DirtTypeConstant.FB_SYMBOL);
        Query query = new Query(params);
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<ValuationModeVo> list = baseBiz.selectValuationMode(params);
        return new TableResultResponse<ValuationModeVo>(result.getTotal(), list);
    }
}
