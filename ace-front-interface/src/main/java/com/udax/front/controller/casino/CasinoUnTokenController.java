package com.udax.front.controller.casino;

import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.front.UserLoginLog;
import com.github.wxiaoqi.security.common.enums.LoginType;
import com.github.wxiaoqi.security.common.enums.RegType;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.WebUtil;
import com.udax.front.biz.CacheBiz;
import com.udax.front.biz.FrontUserBiz;
import com.udax.front.biz.UserLoginLogBiz;
import com.udax.front.biz.casino.CasinoUserInfoBiz;
import com.udax.front.biz.ud.HOrderDetailBiz;
import com.udax.front.biz.ud.HParamBiz;
import com.udax.front.biz.ud.HUserInfoBiz;
import com.udax.front.bizmodel.*;
import com.udax.front.controller.BaseFrontController;
import com.udax.front.service.ServiceUtil;
import com.udax.front.util.user.JwtTokenUtil;
import com.udax.front.vo.FrontUserRegisterVo;
import com.udax.front.vo.reqvo.LoginModel;
import com.udax.front.vo.reqvo.RegisterModel;
import com.udax.front.vo.reqvo.ud.UDRegisterModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * @author zhoucf  不需要token的接口
 * @create 2018／2／27
 */
@RestController
@RequestMapping("/wallet/casino/untoken")
public class CasinoUnTokenController extends BaseFrontController<FrontUserBiz,FrontUser> {
    @Autowired
    private CasinoUserInfoBiz userInfoBiz;

    @Autowired
    private UserLoginLogBiz logBiz;

    @Autowired
    private CacheBiz cacheBiz;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;




    //登陆获取token接口
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse modifyUsers(@RequestBody @Valid LoginModel model) throws Exception {
        CasinoLoginBiz loginBiz = new CasinoLoginBiz(model,userInfoBiz,jwtTokenUtil);
        ObjectRestResponse result = loginBiz.login(request);
        if(result.isRel()){
            Map<String,Object> map = (Map<String,Object>)result.getData();
            Long userId = (Long) map.get("userId");
            map.remove("userId");
            insertLoginlog(userId);
        }
        return result;
    }


    //记录登录日志
    private void insertLoginlog(Long userId) {
        UserLoginLog userLoginLog = new UserLoginLog();
        userLoginLog.setUserId(userId);
        userLoginLog.setLoginIp(WebUtil.getHost(request));
        if("android".equals(request.getHeader("device"))){
            userLoginLog.setLoginType(LoginType.ANDROID.value());
        }else if("ios".equals(request.getHeader("device"))){
            userLoginLog.setLoginType(LoginType.IOS.value());
        }else {
            userLoginLog.setLoginType(LoginType.WEB.value());
        }
        userLoginLog.setLoginStatus(0);
        logBiz.insertSelective(userLoginLog);
    }



    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse modifyUsers(@RequestBody @Valid UDRegisterModel model){
        RegisterModel registerModel = new RegisterModel();
        BeanUtils.copyProperties(model,registerModel);
        if(!RegType.isType(registerModel.getRegType())){
            return new ObjectRestResponse().status(ResponseCode.REGTYPE_NOTNULL);
        }

        AbstractRegister register;
        if(RegType.PHONE.value().equals(registerModel.getRegType())){
            boolean flag = ServiceUtil.valiCountryCode(registerModel.getCountryCode(),cacheBiz);
            if(!flag){
                return new ObjectRestResponse().status(ResponseCode.COUNTRYCODE_VALID);
            }
            register = new PhoneRegister(registerModel);
        }else{
            register = new EmailRegister(registerModel);// 邮箱注册
        }

        // 验证参数
        register.validateParam();
        FrontUserRegisterVo vo = register.setParam(request);
        vo.setExId(BaseContextHandler.getAppExId());
        // 设置用户归属的交易所和平台
        // 验证用户是否已经存在
        FrontUser user = ServiceUtil.selectUnionUserInfoByUserName(registerModel.getUsername(),baseBiz);
        if (user != null) {
            return new ObjectRestResponse().status(ResponseCode.USER_EXIST);
        }
        userInfoBiz.registerForUd(vo);
        return new ObjectRestResponse();
    }




}
