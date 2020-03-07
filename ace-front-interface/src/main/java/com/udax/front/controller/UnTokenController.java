package com.udax.front.controller;

import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.BaseVersion;
import com.github.wxiaoqi.security.common.entity.admin.FrontAdvert;
import com.github.wxiaoqi.security.common.entity.admin.FrontCountry;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.front.FrontUserInfo;
import com.github.wxiaoqi.security.common.entity.front.RedPacketSend;
import com.github.wxiaoqi.security.common.entity.front.UserLoginLog;
import com.github.wxiaoqi.security.common.entity.lock.UserSymbolLockDetail;
import com.github.wxiaoqi.security.common.entity.ud.HCommissionRelation;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.mapper.front.CommissionLogMapper;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.*;
import com.github.wxiaoqi.security.common.util.model.IconModel;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import com.udax.front.biz.*;
import com.udax.front.biz.merchant.MerchantBiz;
import com.udax.front.biz.ud.HCommissionRelationBiz;
import com.udax.front.bizmodel.*;
import com.udax.front.service.ServiceUtil;
import com.udax.front.tencent.TencentSendUtils;
import com.udax.front.util.BizControllerUtil;
import com.udax.front.util.CacheBizUtil;
import com.udax.front.util.user.JwtTokenUtil;
import com.udax.front.vo.FrontUserRegisterVo;
import com.udax.front.vo.reqvo.*;
import com.udax.front.vo.rspvo.FrontCountryVo;
import com.udax.front.vo.rspvo.ud.UDAdRspVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URLClassLoader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

import static com.github.wxiaoqi.security.common.constant.Constants.SMS_LIMIT;

/**
 * @author zhoucf  不需要token的接口
 * @create 2018／2／27
 */
@RestController
@RequestMapping("/wallet/untoken")
@Slf4j
public class UnTokenController extends BaseFrontController<FrontUserBiz,FrontUser> {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserLoginLogBiz logBiz;


    @Autowired
    private CacheBiz cacheBiz;

    @Autowired
    private DcAssertAccountBiz assertAccountBiz;

    @Autowired
    private CommissionLogBiz commissionLogBiz;




    @Autowired
    private MerchantBiz merchantBiz;
    //登陆获取token接口
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse modifyUsers(@RequestBody @Valid LoginModel model) throws Exception {
        LoginBiz loginBiz = new LoginBiz(model,baseBiz,merchantBiz,jwtTokenUtil);
        ObjectRestResponse result = loginBiz.login(request);
        if(result.isRel()){
           Map<String,Object> map = (Map<String,Object>)result.getData();
           Long userId = (Long) map.get("userId");
           map.remove("userId");
          insertLoginlog(userId);
        }
        return result;
    }


    //首页展示内容
    @RequestMapping(value = "/ad", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse ad() throws Exception{
        List<FrontAdvert> noticeList =  CacheBizUtil.getFrontAdvert(BaseContextHandler.getAppExId(),ClientType.APP.value(),BaseContextHandler.getLanguage(),cacheBiz);
        List<UDAdRspVo> adList = BizControllerUtil.transferEntityToListVo(UDAdRspVo.class,noticeList);
        return new ObjectRestResponse().data(adList);
    }



    //获取icon接口
    @RequestMapping(value = "/getRegisterType", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse getRegisterInfo() throws Exception {
//        Long exchId = CacheBizUtil.getExchId(exchInfo,cacheBiz);
//        if(exchId == null || exchId.intValue() < 1){
//            return new ObjectRestResponse().status(ResponseCode.EXINFO_LENGTH);
//        }
        Integer registerType =  CacheBizUtil.getExchRegisterType(BaseContextHandler.getAppExId(),cacheBiz);
        return new ObjectRestResponse().data(registerType);
    }



    @PostMapping("chkAccount")
    @ApiOperation(value = "检查账号是否存在,以及对应的手机邮箱验证状态是否开启", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object chkAccount(@RequestBody @Valid CkAccountModel model) {
        // 通过登录名查询出用户
        FrontUser frontUser = baseBiz.selectUnionUserInfoByUserName(model.getAccount());
        if ( frontUser != null) {
            return new ObjectRestResponse<>().data( EnableType.ENABLE.value());

        }
        return new ObjectRestResponse<>().data(EnableType.DISABLE.value());

    }


    //获取icon接口
    @RequestMapping(value = "/icon", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse icon() throws Exception {
//        Long exchId = CacheBizUtil.getExchId(exchInfo,cacheBiz);
//        if(exchId == null || exchId.intValue() < 1){
//            return new ObjectRestResponse().status(ResponseCode.EXINFO_LENGTH);
//        }
        List<IconModel> list = CacheBizUtil.getBasicSymbolIcon(cacheBiz,BaseContextHandler.getAppExId());
        return new ObjectRestResponse().data(list);
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
    public ObjectRestResponse modifyUsers(@RequestBody @Valid RegisterModel registerModel){
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
        FrontUser user = baseBiz.selectByUsername(registerModel.getUsername());
        if (user != null) {
            return new ObjectRestResponse().status(ResponseCode.USER_EXIST);
        }

        baseBiz.register(vo);

//        // 新增用户
//        Parameter parameter = new Parameter(getService(), "register").setParam(frontUser);
//        frontUser = (FrontUser) provider.execute(parameter).getResult();
//        if (frontUser != null && frontUser.getId() != null) {
//            List<String> symbolList = (List<String>) CacheUtil.getCache()
//                    .get(top.ibase4j.core.Constants.CACHE_NAMESPACE + "digitalSymbol:" + "allSymbol");
//            if (symbolList != null && symbolList.size() > 0) {
//                List<SpotAccountAssert> accountList = new ArrayList<SpotAccountAssert>();
//                // Set<String> ketSet = symbolMap.keySet();
//                for (String symbol : symbolList) {
//                    SpotAccountAssert accountAssert = new SpotAccountAssert();
//                    accountAssert.setUserId(frontUser.getId());
//                    accountAssert.setDcCode(symbol);
//                    accountAssert.setTotolAmount(BigDecimal.ZERO);
//                    accountAssert.setAvailableAmount(BigDecimal.ZERO);
//                    accountAssert.setFreezeAmount(BigDecimal.ZERO);
//                    accountAssert.setOutFreeze(BigDecimal.ZERO);
//                    accountList.add(accountAssert);
//                }
//                parameter.setService("spotAccountAssertService");
//                parameter.setMethod("updateBatch");
//                parameter.setParam(accountList);
//                provider.execute(parameter);
//            }
//            // 验证码失效
//            request.getSession().removeAttribute(Constants.VERIFICATION_CODE);
//            return setSuccessModelMap(modelMap);
//        }
        TencentSendUtils.importAccount(vo.getUserName());
        TencentSendUtils.setUserInfo(vo.getUserName());
        return new ObjectRestResponse();
    }


    @PostMapping("/sendsms")
    public ObjectRestResponse sendMsg(@RequestBody @Valid SmsModel smsModel) {
        Long ttl = CacheUtil.getCache().ttl(SMS_LIMIT + smsModel.getUserName()+":" + smsModel.getSendMsgType());
        if(ttl > 0){
            throw new BusinessException(ResponseCode.SMS_LIMIT.name());
        }
        // Map<String, Object> params = WebUtil.getParameter(request);
//        //校验白标信息
//        Long exchId = CacheBizUtil.getExchId(smsModel.getExInfo(),cacheBiz);
//        if(exchId == null || exchId.intValue() < 1){
//            return new ObjectRestResponse().status(ResponseCode.EXINFO_LENGTH);
//        }
        String sendType = smsModel.getSendType();
        SendSmsBiz smsBiz;
        ValidCodeType type = ValidCodeType.transferFromValue(Integer.parseInt(sendType));
        //如果是注册  需要验证locationcode
        if(smsModel.getSendMsgType().equals(SendMsgType.USER_REG.value())
                && type == ValidCodeType.PHONE_CODE){
            boolean flag = ServiceUtil.valiCountryCode(smsModel.getLocationCode(),cacheBiz);
            if(!flag){
                return new ObjectRestResponse().status(ResponseCode.COUNTRYCODE_VALID);
            }
        }
        switch (type) {
            case EMAIL_CODE:
                smsBiz = new EmailSmsBiz();
                break;
            case PHONE_CODE:
                smsBiz = new PhoneSmsBiz();
                break;
            default:
                return new ObjectRestResponse().status(ResponseCode.SMS_SEND_TYPE_ERROR);
        }
        ObjectRestResponse response = smsBiz.sendValidCode(smsModel,baseBiz,BaseContextHandler.getAppExId());
        //60秒有效期
        CacheUtil.getCache().set(SMS_LIMIT + smsModel.getUserName()+":" + smsModel.getSendMsgType(),true,60);
        return response;
    }


    //检查手机号/帐号/邮箱是否存在
    @PostMapping("/accountInfo")
    public ObjectRestResponse accountInfo(@RequestBody @Valid CkAccountModel model) {
        // 通过登录名查询出用户
       FrontUser user = ServiceUtil.selectUnionUserInfoByUserName(model.getAccount(),baseBiz);
        Map<String,Object> rspMap = InstanceUtil.newHashMap();
        if (user != null && user.getUserInfo() != null) {
            rspMap.put("exists", 1);
            rspMap.put("openPhoneValid", user.getUserInfo().getIsValidPhone());
            if(user.getUserInfo().getIsValidPhone().equals(1)){
                rspMap.put("phone", user.getMobile());
            }
            rspMap.put("openEmailValid", user.getUserInfo().getIsValidEmail());
            if(user.getUserInfo().getIsValidEmail().equals(1)){
                rspMap.put("email", user.getEmail());
            }
        } else {
            rspMap.put("exists", 0);
        }

        return new ObjectRestResponse().data(rspMap);

    }

    //移动端版本控制 参数:1表示android ;2:表示ios
    @PostMapping("/versionInfo")
    public ObjectRestResponse accountInfo(@RequestParam  String deviceType) {
        if(!VersionType.isType(deviceType)){
            return new ObjectRestResponse().status(ResponseCode.VERSION_TYPE_ERROR);
        }
        BaseVersion baseVersion = CacheBizUtil.getBaseVersion(cacheBiz,deviceType,BaseContextHandler.getAppExId());

        return new ObjectRestResponse().data(baseVersion);

    }

    //重置密码
    @PostMapping("/changePwd")
    public ObjectRestResponse changePwd(@RequestBody @Valid ChangePwdModel model) {
        // 加上收取验证码的逻辑
        // 通过登录名查询出用户
        FrontUser user = ServiceUtil.selectUnionUserInfoByUserName(model.getUsername(),baseBiz);
        if (user == null) {
            return new ObjectRestResponse().status(ResponseCode.USER_NOT_EXIST);
        }

        // 调用验证码公共方法校验验证码
        CommonBiz.commonVerifyMethod(user, SendMsgType.FORGOT_PASSWORD.value(),
                model.getMobileCode(), model.getEmailCode());
        user.setUserPwd(SecurityUtil.encryptPassword(model.getPassword() + user.getSalt()));
        baseBiz.updatePwd(user);
        CommonBiz.clearVerifyMethod(user);
        return new ObjectRestResponse();
    }

    //获取国家编码列表
    @PostMapping("/countrys")
    public Object getcountrys() throws InstantiationException, IllegalAccessException {

//        Map<String, Object> queryParams = new HashMap<String, Object>();
//        queryParams.put("status:equal", EnableType.ENABLE.value()); //可用状态
//        queryParams.put("orderByInfo","sort"); //排序字段
//        Query query = new Query(queryParams);
        List<FrontCountry> list = CacheBizUtil.getFrontCountry(cacheBiz);
        List<FrontCountryVo> volist = BizControllerUtil.transferEntityToListVo(FrontCountryVo.class,list);
        return new ObjectRestResponse().data(volist);
    }
//
//    //增加资产
//    @PostMapping("/addAssert")
//    public void addAssert(@RequestBody AddAssertModel model) throws Exception {
//        FrontUser user =  ServiceUtil.getUniFrontUserByName(model.getUserName(),baseBiz);
//        AccountAssertLogVo payVo = new AccountAssertLogVo();
//        payVo.setUserId(user.getId());
//        payVo.setSymbol(model.getDcCode());
//        payVo.setAmount(model.getAmount());
//        payVo.setChargeSymbol(model.getDcCode());
//        payVo.setType(AccountLogType.RECHARGE);
//        if("freeze".equals(model.getType())){
//            payVo.setType(AccountLogType.UD_FREEZE);
//            assertAccountBiz.signUpdateAssert(payVo,AccountSignType.ACCOUNT_WITHDRAW_FREEZE);
//            return;
//        }
//        if("unfreeze".equals(model.getType())){
//            payVo.setType(AccountLogType.UD_UNFREEZE);
//            assertAccountBiz.signUpdateAssert(payVo,AccountSignType.ACCOUNT_WITHDRAW_DEDUTION);
//            return;
//        }
//        assertAccountBiz.signUpdateAssert(payVo,AccountSignType.ACCOUNT_RECHARGE);
//    }

//
//    @PostMapping("/getDecryptInfo")
//    public String decryptBASE64(@RequestBody  String info){
//        info = SecurityUtil.decryptDes(info, "11223344".getBytes());
//        return info;
//    }
//
    @PostMapping("/getEncryptInfo")
    public String encryptBASE64(@RequestParam String info){

        info =  SecurityUtil.encryptBASE64(SecurityUtil.encryptDes(info, "12345678".getBytes()).getBytes());
        return info;
    }



    @PostMapping("/test")
    public ObjectRestResponse test(){
        System.out.println(System.getProperty("java.class.path"));
        System.out.println(this.getClass().getName());
        ClassLoader cl= this.getClass().getClassLoader();
        System.out.println("zcf:" + cl);
        while (cl.getParent() != null){
            System.out.println("zcf:" + cl.getParent());
            cl = cl.getParent();
        }

      //  URLClassLoader cl = new URLClassLoader()

        return new ObjectRestResponse();
    }



    @Autowired
    private FrontUserInfoBiz frontUserInfobiz;

    @Autowired
    private HCommissionRelationBiz commissionRelationBiz;

    @PostMapping("/addData")
    public ObjectRestResponse addData(){
        List<FrontUserInfo> userlist = frontUserInfobiz.selectListAll();
        userlist = userlist.stream().filter((f) -> f.getParentId() > 0).collect(Collectors.toList());
        userlist.forEach((l) ->{
            recycle(l,l.getParentId());
        });
        return new ObjectRestResponse<>();
    }
    private void recycle(FrontUserInfo l, Long userId){
        FrontUserInfo param = new FrontUserInfo();
        param.setUserId(userId);
        //选择父类
        FrontUserInfo parentInfo = frontUserInfobiz.selectOne(param);
        if(parentInfo == null){
            log.error("userId:" + userId);
            return;
        }
        HCommissionRelation relation = new HCommissionRelation();
        relation.setUserId(l.getUserId());
        relation.setReceiveUserId(parentInfo.getUserId());
        relation.setLevel(l.getLevel());
        relation.setReceiveLevel(parentInfo.getLevel());
        if(commissionRelationBiz.selectCount(relation) == 0){
            commissionRelationBiz.insert(relation);
        }
        if(parentInfo.getParentId() > 0){
            recycle(l,parentInfo.getParentId());
        }
    }

    public static void main(String[] args) {
        LongAdder add = new LongAdder();
        add.increment();
        add.increment();
    }

}
