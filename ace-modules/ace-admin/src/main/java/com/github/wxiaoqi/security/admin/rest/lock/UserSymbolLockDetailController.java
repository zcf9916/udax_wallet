package com.github.wxiaoqi.security.admin.rest.lock;

import com.github.wxiaoqi.security.admin.biz.lock.UserSymbolLockDetailBiz;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.lock.UserSymbolLockDetail;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("userSymbolLockDetail")
public class UserSymbolLockDetailController extends BaseController<UserSymbolLockDetailBiz, UserSymbolLockDetail> {

    @RequestMapping(value = "/pageAll", method = RequestMethod.GET)
    public TableResultResponse<UserSymbolLockDetail> listAll(@RequestParam Map<String, Object> params) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            params.put("exchangeId", BaseContextHandler.getExId());
        }
        return super.queryListByCustomPage(params);
    }

    /**
     * 后台手动释放锁定资产
      * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/manualFreedAssets",method = RequestMethod.PUT)
    public ObjectRestResponse<UserSymbolLockDetail> manualFreedAssets(@PathVariable Long id){
        baseBiz.manualFreedAssets(id);
        return new ObjectRestResponse<UserSymbolLockDetail>();
    }
}
