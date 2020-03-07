package com.github.wxiaoqi.security.admin.rest.lock;

import com.github.wxiaoqi.security.admin.biz.lock.UserSymbolLockBiz;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.lock.UserSymbolLock;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("userSymbolLock")
public class UserSymbolLockController extends BaseController<UserSymbolLockBiz, UserSymbolLock> {


    @RequestMapping(value = "/pageAll", method = RequestMethod.GET)
    public TableResultResponse<UserSymbolLock> listAll(@RequestParam Map<String, Object> params) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            params.put("exchangeId", BaseContextHandler.getExId());
        }
        return super.queryListByCustomPage(params);
    }
}
