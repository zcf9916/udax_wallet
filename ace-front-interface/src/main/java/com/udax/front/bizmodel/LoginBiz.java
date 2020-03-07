package com.udax.front.bizmodel;

import com.alibaba.fastjson.JSON;
import com.github.wxiaoqi.security.common.entity.admin.Param;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.merchant.Merchant;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.enums.FrontUserStatus;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.SecurityUtil;
import com.github.wxiaoqi.security.common.util.WebUtil;
import com.github.wxiaoqi.security.common.util.jwt.JWTInfo;
import com.udax.front.biz.FrontUserBiz;
import com.udax.front.biz.merchant.MerchantBiz;
import com.udax.front.service.ServiceUtil;
import com.udax.front.tencent.Configure;
import com.udax.front.util.TencentVerify;
import com.udax.front.util.user.JwtTokenUtil;
import com.udax.front.vo.reqvo.LoginModel;
import com.udax.front.vo.rspvo.UserInfoRspVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.github.wxiaoqi.security.common.constant.Constants.BaseParam.TOKEN_PARAM;


/**
 * 登陆相关业务逻辑
 */
public class LoginBiz {

    protected final Logger logger = LogManager.getLogger(this.getClass());

    private FrontUser frontUser;

    private JwtTokenUtil jwtTokenUtil;

    private LoginModel loginModel;

    private FrontUserBiz biz;

    private MerchantBiz merchantBiz;
    public LoginBiz(LoginModel loginModel, FrontUserBiz biz, MerchantBiz merchantBiz,JwtTokenUtil util) {
        this.loginModel = loginModel;
        this.biz = biz;
        this.jwtTokenUtil = util;
        this.merchantBiz = merchantBiz;
    }

    public ObjectRestResponse login(HttpServletRequest  request){
        try{

            ObjectRestResponse response = validSessionCode(request);
            if(!response.isRel()){
                return response;
            }
            response = validateQueryInfo();
            if(!response.isRel()){
                return response;
            }

            updateUserInfo(request);
            //获取token并缓存
            String token = dealToken(request);
            //获取useAgent,或者手机信息用来风险控制
            //dealVisitInfo(request);
            //返回结果
            Map<String, Object> resultMap = InstanceUtil.newHashMap("token", token);

            UserInfoRspVo rspVo = new UserInfoRspVo();
            rspVo.setUserSig(Configure.genTestUserSig(frontUser.getUserName()));
            BeanUtils.copyProperties(frontUser,rspVo);
            BeanUtils.copyProperties(frontUser.getUserInfo(),rspVo);
            rspVo.setHeadPictureUrl(frontUser.getUserInfo().getPortrait());
            if(StringUtils.isBlank(frontUser.getTradePwd())){
                rspVo.setIsSetTradePwd(EnableType.DISABLE.value());
            }else{
                rspVo.setIsSetTradePwd(EnableType.ENABLE.value());
            }
            Merchant merchant = merchantBiz.getMerchantInfoByUserId(frontUser.getId());
            if(merchant != null){
                rspVo.setMchStatus(merchant.getMchStatus());
            }

            resultMap.put("userInfo",rspVo);
            resultMap.put("userId",frontUser.getId());
            return new ObjectRestResponse<Map>().data(resultMap);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            return new ObjectRestResponse().status(ResponseCode.UNKNOW_ERROR);
        }



    }

    //处理访问记录,目前只处理PC端的数据
    private void dealVisitInfo(HttpServletRequest request) {
//        //获取user-Agent,有可能为空
//        String userAgentStr = request.getHeader("User-Agent");
//        if(StringUtils.isNotBlank(userAgentStr)){
//            UserAgent userAgent  = UserAgentUtil.parsePhoneVersion(userAgentStr);
//            TokenUserAgentInfo info = new TokenUserAgentInfo();
//            info.setDeviceInfo(userAgent.getDeviceInfo());
//            info.setOsName(userAgent.getOsName());
//            info.setUaName(userAgent.getUaName());
//            if(userAgent.getType().contains("Mobile")){
//                info.setVisitType(TokenUserAgentInfo.VisitType.MOBILE_BROWSER);
//            }else{
//                info.setVisitType(TokenUserAgentInfo.VisitType.PC_BROWSER);
//            }
//            if(frontUser.getId() < 1){
//                throw new LoginException("");
//            }
//            //记录访问记录
//            CacheUtil.getCache().set(top.ibase4j.core.Constants.CommonType.USERAGENT + frontUser.getId().toString(), info);
//        }else{
//            throw new LoginException("缺少userAgent信息");
//           // CacheUtil.getCache().set(top.ibase4j.core.Constants.CommonType.USERAGENT + frontUser.getId().toString(), new TokenUserAgentInfo());
//        }



    }

    private ObjectRestResponse validSessionCode(HttpServletRequest request) {
//        	if("native".equals(request.getHeader("env"))){//env:native ,app自带属性，以区分是app还是pc端
//                String sessionCode = (String) request.getSession().getAttribute(CommonConstants.VERIFICATION_CODE);
//                if (!loginModel.getVeriCode().equalsIgnoreCase(sessionCode)) {
//                    return new ObjectRestResponse().rel(false).status(HttpCode.BAD_REQUEST.value()).msg(Resources.getMessage("SYS_VERIFICATION_CODE"));
//                }
//            }else{//pc登录验证图形验证码是否正确
//            	 String sessionCode = (String) request.getSession().getAttribute(CommonConstants.VERIFICATION_CODE);
//                 if (!loginModel.getVeriCode().equalsIgnoreCase(sessionCode)) {
//                     return new ObjectRestResponse().rel(false).status(HttpCode.BAD_REQUEST.value()).msg(Resources.getMessage("SYS_VERIFICATION_CODE"));
//                 }
//            }



            if(!TencentVerify.verifyTicket(loginModel.getTicket(),loginModel.getRandstr(), WebUtil.getInternetIp())){
                return new ObjectRestResponse().status(ResponseCode.VERIFICATION_CODE_ERROR);
            }
            return new ObjectRestResponse();
    }
    
    //生产token并缓存
    private String dealToken(HttpServletRequest request) throws Exception {
        String token = jwtTokenUtil.generateToken(new JWTInfo(frontUser.getUserName(), frontUser.getId(), frontUser.getUid(),frontUser.getUserInfo().getExchangeId()));

//        //获取后台token相关参数
        Param token_param = (Param) CacheUtil.getCache().get(TOKEN_PARAM);
        int expire = 600;//600秒
        if(token_param != null){
            expire = Integer.parseInt(token_param.getParamValue());
        }
        CacheUtil.getCache().set(token, frontUser);
        //防止多次登陆产生多条token被恶意调用
        CacheUtil.getCache().set(frontUser.getUid(),token);
        CacheUtil.getCache().expire(token, expire);
        return token;
    }


    private void updateUserInfo(HttpServletRequest request) {
        //frontUser.setIsOnline(LoginStatus.ONLINE.value());
        //frontUser.setUpdateTime(new Date());
        frontUser.setLoginErrTimes(0);
        String loginIp = WebUtil.getHost(request);
        if (StringUtils.isNotEmpty(frontUser.getLoginIp()) && !frontUser.getLoginIp().equals(loginIp)) {
            frontUser.setLoginIp(loginIp);// TODO此处如果用户登陆IP与上次登陆不一致，需发送短信验证码验证
        }

    }

    //处理登陆失败
    private ObjectRestResponse handleLoginFailed(){
//        if (frontUser.getLoginErrTimes() >= 5) {
//            return new Result(false,HttpCode.LOCKED,HttpCode.LOCKED.msg());
//           // throw new LoginException(Resources.getMessage(HttpCode.LOCKED.name()));
//        } else {
//            frontUser.setLoginErrTimes(frontUser.getLoginErrTimes() + 1);
//            Parameter parameter = new Parameter();
//            parameter.setService(getService());
//            parameter.setParam(frontUser);
//            parameter.setMethod("update");
//            provider.execute(parameter);
//        }
        return new ObjectRestResponse().status(ResponseCode.ACCOUNT_PWD_ERROR);
    }
    //验证查询结果
    private ObjectRestResponse validateQueryInfo(){
        //通过用户名查询出用户
        frontUser = ServiceUtil.selectUnionUserInfoByUserName(loginModel.getUsername(),biz);
        //FrontUser frontUser = null;
        //如果查不出用户
        if (frontUser == null) {
            return new ObjectRestResponse().status(ResponseCode.ACCOUNT_PWD_ERROR);
        }
        //frontUser = (FrontUser) resultList.get(0);
        if(frontUser.getUserStatus() != FrontUserStatus.ACTIVE.value()) { // 验证用户是否已被注销，注销后提示账户信息不存在
            return new ObjectRestResponse().status(ResponseCode.UNACTIVE.value());
        }
        // 判断登录失败次数
        if (frontUser.getLoginErrTimes() >= 5) {
            return new ObjectRestResponse().status(ResponseCode.LOCKED.value());
        }
        String password = SecurityUtil.encryptPassword(loginModel.getPassword()+frontUser.getSalt());
        if (!password.equals(frontUser.getUserPwd())) {
            return handleLoginFailed();
        }
       // cac
        return new ObjectRestResponse();
    }


}
