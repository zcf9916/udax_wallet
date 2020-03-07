package com.github.wxiaoqi.security.admin.rest.ud;


import com.github.wxiaoqi.security.admin.biz.ud.UserInfoBiz;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.ud.HUserInfo;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ud 社区用户管理
 */
@RestController
@RequestMapping("userInfo")
public class UserInfoController extends BaseController<UserInfoBiz, HUserInfo> {

    @RequestMapping(value = "/page",method = RequestMethod.GET)
    public TableResultResponse<HUserInfo> list(@RequestParam Map<String, Object> params){
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            params.put("exchId", BaseContextHandler.getExId());
        }
        return super.queryListByCustomPage(params);
    }

    @RequestMapping(value = "/{id}/node",method = RequestMethod.PUT)
    public ObjectRestResponse<HUserInfo> updateNode(@RequestBody HUserInfo entity){
        baseBiz.updateNodeByUserId(entity);
        return new ObjectRestResponse<HUserInfo>();
    }

    @RequestMapping(value = "/{id}/unlock",method = RequestMethod.PUT)
    public ObjectRestResponse<HUserInfo> unlockUserInfoStatus(@PathVariable Long id){

        /**
         *
         */
        baseBiz.unlockUserInfoStatus(id);
        return new ObjectRestResponse<HUserInfo>();
    }
}
