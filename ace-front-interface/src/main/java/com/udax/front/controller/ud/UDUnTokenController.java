package com.udax.front.controller.ud;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.front.UserLoginLog;
import com.github.wxiaoqi.security.common.entity.ud.HOrderDetail;
import com.github.wxiaoqi.security.common.entity.ud.HParam;
import com.github.wxiaoqi.security.common.entity.ud.HUserInfo;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.enums.ud.UDOrderDetailStatus;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.WebUtil;
import com.udax.front.biz.*;
import com.udax.front.biz.ud.HOrderDetailBiz;
import com.udax.front.biz.ud.HParamBiz;
import com.udax.front.biz.ud.HUserInfoBiz;
import com.udax.front.bizmodel.*;
import com.udax.front.bizmodel.ud.UdEmailSmsBiz;
import com.udax.front.bizmodel.ud.UdPhoneSmsBiz;
import com.udax.front.bizmodel.ud.UdSendSmsBiz;
import com.udax.front.controller.BaseFrontController;
import com.udax.front.service.ServiceUtil;
import com.udax.front.task.jobs.UDCalcDailyProfitJob;
import com.udax.front.util.CacheBizUtil;
import com.udax.front.util.user.JwtTokenUtil;
import com.udax.front.vo.FrontUserRegisterVo;
import com.udax.front.vo.reqvo.*;
import com.udax.front.vo.reqvo.ud.UDRegisterModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static com.github.wxiaoqi.security.common.constant.Constants.BATCHUPDATE_LIMIT;
import static com.github.wxiaoqi.security.common.constant.Constants.SMS_LIMIT;

/**
 * @author zhoucf  不需要token的接口
 * @create 2018／2／27
 */
@RestController
@RequestMapping("/wallet/ud/untoken")
public class UDUnTokenController extends BaseFrontController<FrontUserBiz,FrontUser> {
    @Autowired
    private HUserInfoBiz userInfoBiz;

    @Autowired
    private UserLoginLogBiz logBiz;

    @Autowired
    private CacheBiz cacheBiz;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private HOrderDetailBiz orderDetailBiz;


    @Autowired
    private HParamBiz paramBiz;



    //登陆获取token接口
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse modifyUsers(@RequestBody @Valid LoginModel model) throws Exception {
        UDLoginBiz loginBiz = new UDLoginBiz(model,userInfoBiz,jwtTokenUtil);
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
//        //校验白标信息
//        Long exchId = CacheBizUtil.getExchId(registerModel.getExInfo(),cacheBiz);
//        if(exchId == null || exchId.intValue() < 1){
//            return new ObjectRestResponse().status(ResponseCode.EXINFO_LENGTH);
//        }

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




//
//
//    @PostMapping("/sendsms")
//    public ObjectRestResponse sendMsg(@RequestBody @Valid SmsModel smsModel) {
//        Long ttl = CacheUtil.getCache().ttl(SMS_LIMIT + smsModel.getUserName()+":" + smsModel.getSendMsgType());
//        if(ttl > 0){
//            throw new BusinessException(ResponseCode.SMS_LIMIT.name());
//        }
//        // Map<String, Object> params = WebUtil.getParameter(request);
//        //校验白标信息
//        Long exchId = CacheBizUtil.getExchId(smsModel.getExInfo(),cacheBiz);
//        if(exchId == null || exchId.intValue() < 1){
//            return new ObjectRestResponse().status(ResponseCode.EXINFO_LENGTH);
//        }
//        String sendType = smsModel.getSendType();
//        UdSendSmsBiz smsBiz;
//        ValidCodeType type = ValidCodeType.transferFromValue(Integer.parseInt(sendType));
//        //如果是注册  需要验证locationcode
//        if(smsModel.getSendMsgType().equals(SendMsgType.USER_REG.value())
//                && type == ValidCodeType.PHONE_CODE){
//            boolean flag = ServiceUtil.valiCountryCode(smsModel.getLocationCode(),cacheBiz);
//            if(!flag){
//                return new ObjectRestResponse().status(ResponseCode.COUNTRYCODE_VALID);
//            }
//        }
//        switch (type) {
//            case EMAIL_CODE:
//                smsBiz = new UdEmailSmsBiz();
//                break;
//            case PHONE_CODE:
//                smsBiz = new UdPhoneSmsBiz();
//                break;
//            default:
//                return new ObjectRestResponse().status(ResponseCode.SMS_SEND_TYPE_ERROR);
//        }
//        ObjectRestResponse response = smsBiz.sendValidCode(smsModel,userInfoBiz,exchId);
//        //60秒有效期
//        CacheUtil.getCache().set(SMS_LIMIT + smsModel.getUserName()+":" + smsModel.getSendMsgType(),true,60);
//        return response;
//    }
//
//    @PostMapping("/test")
//    public void test(){
//        //查询是否有未到期的订单
//        Example example = new Example(HOrderDetail.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("status", UDOrderDetailStatus.INIT.value());//未到期订单
//        example.setOrderByClause(" id");//正序查询
//        int total =   orderDetailBiz.selectCountByExample(example);//计算总量
//        int totalPageCount  =  new BigDecimal(total).divide( new BigDecimal(BATCHUPDATE_LIMIT),BigDecimal.ROUND_CEILING).intValue();//需要分页的数量
//        int beginCount = 1;
//        Long lastId = 0L;//上一批次的最后一个Id,下次分页查询要从上一次的最后一个开始查询
//        while( beginCount <= totalPageCount){
//            if(lastId >  0){
//                criteria.andGreaterThan("id",lastId);
//            }
//            Page<Object> result = PageHelper.startPage(beginCount, BATCHUPDATE_LIMIT);
//            List<HOrderDetail> orderDetailList = orderDetailBiz.selectByExample(example);
//            //这个批次的最大Id
//            if(StringUtil.listIsNotBlank(orderDetailList)){
//                orderDetailList.stream().forEach((l) ->{
//                    HUserInfo userInfo = orderDetailBiz.settleOrderDetail(l);
//                    if(userInfo != null){
//                        orderDetailBiz.autoRepeat(userInfo,l);
//                    }
//                });
//                lastId = orderDetailList.get(orderDetailList.size() - 1).getId();
//            }
//            beginCount ++;
//        }
//
//
//
//        HParam hparam = ServiceUtil.getUdParamByKey("EXPIRE",paramBiz);
//        Long expire = Long.valueOf(hparam.getUdValue());//解锁有效期
//        //查询是否有未到期的订单
//        Example userExample = new Example(HUserInfo.class);
//        Example.Criteria userCriteria = example.createCriteria();
//        userCriteria.andEqualTo("status", EnableType.ENABLE.value());//查询用户状态为激活的
//        List<HUserInfo> userList = userInfoBiz.selectByExample(userExample);
//        //这个批次的最大Id
//        if(StringUtil.listIsNotBlank(userList)){
//            userList.stream().forEach((l) ->{
//                userInfoBiz.lockUser(l,expire);
//            });
//        }
//
//    }


}
