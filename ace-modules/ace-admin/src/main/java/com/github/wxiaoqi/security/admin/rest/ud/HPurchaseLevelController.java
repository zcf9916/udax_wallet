package com.github.wxiaoqi.security.admin.rest.ud;

import com.github.wxiaoqi.security.admin.biz.ud.HPurchaseLevelBiz;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.ud.HOrderDetail;
import com.github.wxiaoqi.security.common.entity.ud.HPurchaseLevel;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@RequestMapping("/purchaseLevel")
@RestController
public class HPurchaseLevelController extends BaseController<HPurchaseLevelBiz, HPurchaseLevel> {

    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public TableResultResponse<HPurchaseLevel> list(@RequestParam Map<String, Object> params) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            params.put("exchId", BaseContextHandler.getExId());
        }
        return super.queryListByCustomPage(params);
    }

    @RequestMapping(value = "/getAll",method = RequestMethod.GET)
    public List<HPurchaseLevel> all(@RequestParam Long exchangeId){
        Example example = new Example(HPurchaseLevel.class);
        example.createCriteria().andEqualTo("exchId",exchangeId);
        return baseBiz.selectByExample(example);
    }
}
