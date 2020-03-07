package com.github.wxiaoqi.security.admin.rest.front;

import com.github.wxiaoqi.security.admin.biz.front.WhiteExchInfoBiz;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.WhiteExchInfo;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("whiteExchInfo")
public class WhiteExchInfoController extends BaseController<WhiteExchInfoBiz, WhiteExchInfo> {
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ObjectRestResponse<WhiteExchInfo> add(@RequestBody WhiteExchInfo entity) {
        if (entity.getId() == null) {
            baseBiz.insertSelective(entity);
        } else {
            baseBiz.updateSelectiveById(entity);
        }
        return new ObjectRestResponse<WhiteExchInfo>();
    }


    @RequestMapping(value = "/pageAll", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<WhiteExchInfo> list(@RequestParam Map<String, Object> params) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            params.put("exchangeId", BaseContextHandler.getExId());
        }
        return super.queryListByCustomPage(params);
    }
}
