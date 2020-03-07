package com.github.wxiaoqi.security.admin.rest.casino;

import com.github.wxiaoqi.security.admin.biz.casino.CasinoCommissionLogBiz;
import com.github.wxiaoqi.security.admin.biz.casino.CasinoUserInfoBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.casino.CasinoUserInfo;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 推荐返利配置表
 */
@RestController
@RequestMapping("/casinoUserInfo")
public class CasinoUserInfoController extends BaseController<CasinoUserInfoBiz, CasinoUserInfo> {

    @Autowired
    private CasinoCommissionLogBiz casinoCommissionLogBiz;

    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public TableResultResponse<CasinoUserInfo> list(@RequestParam Map<String, Object> param) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            param.put("exchangeId", BaseContextHandler.getExId());
        }
        return super.queryListByCustomPage(param);
    }

    @RequestMapping(value = "/{id}/updateUserType", method = RequestMethod.PUT)
    public ObjectRestResponse<CasinoUserInfo> updateUserType(@PathVariable Long id) {
        if (id == null) {
            throw new UserInvalidException(Resources.getMessage("CASINO_USER_INFO"));
        }
        CasinoUserInfo info = baseBiz.selectById(id);
        casinoCommissionLogBiz.upgradeToCream(info.getUserId(),info);

        //普通会员升级为精英会员
//        if (CasinoUserInfo.RoleType.NORMAL.value().equals(info.getSettleType())) {
//            info.setSettleType(CasinoUserInfo.RoleType.CREAM.value());
//            baseBiz.updateSelectiveById(info);
//            //精英会员降级为普通会员
//        } else if (CasinoUserInfo.RoleType.CREAM.value().equals(info.getSettleType())) {
//            info.setSettleType(CasinoUserInfo.RoleType.NORMAL.value());
//            baseBiz.updateSelectiveById(info);
//        }
        return new ObjectRestResponse<CasinoUserInfo>();
    }


    @RequestMapping(value = "/{id}/updateVicePresident", method = RequestMethod.PUT)
    public ObjectRestResponse<CasinoUserInfo> updateVicePresident(@PathVariable Long id) {
        if (id == null) {
            throw new UserInvalidException(Resources.getMessage("CASINO_USER_INFO"));
        }
        CasinoUserInfo info = baseBiz.selectById(id);
        //普通会员升级副总裁
        if (CasinoUserInfo.CasinoUserInfoType.NORMAL.value().equals(info.getType())
                || CasinoUserInfo.CasinoUserInfoType.CREAM.value().equals(info.getType())) {
            info.setType(CasinoUserInfo.CasinoUserInfoType.VP.value());
            baseBiz.updateSelectiveById(info);
            //降级
        }
        return new ObjectRestResponse<CasinoUserInfo>();
    }

}
