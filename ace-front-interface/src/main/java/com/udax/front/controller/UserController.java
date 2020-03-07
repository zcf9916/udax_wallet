package com.udax.front.controller;

import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.common.SendMsg;
import com.github.wxiaoqi.security.common.entity.front.*;
import com.github.wxiaoqi.security.common.entity.merchant.MchRefundDetail;
import com.github.wxiaoqi.security.common.entity.merchant.MchTradeDetail;
import com.github.wxiaoqi.security.common.entity.merchant.Merchant;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.enums.merchant.MchStatus;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.support.Assert;
import com.github.wxiaoqi.security.common.util.*;
import com.udax.front.biz.*;
import com.udax.front.biz.merchant.MchRefundDetailBiz;
import com.udax.front.biz.merchant.MchTradeDetailBiz;
import com.udax.front.biz.merchant.MerchantBiz;
import com.udax.front.bizmodel.CommonBiz;
import com.udax.front.service.ServiceUtil;
import com.udax.front.tencent.Configure;
import com.udax.front.util.BizControllerUtil;
import com.udax.front.util.CacheBizUtil;
import com.udax.front.vo.reqvo.*;
import com.udax.front.vo.rspvo.GetAssertLogRspVo;
import com.udax.front.vo.rspvo.OwnMemberVo;
import com.udax.front.vo.rspvo.UserInfoRspVo;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhoucf
 * @create 2018／2／27
 */
@RestController
@RequestMapping("/wallet/user")
public class UserController extends BaseFrontController<FrontUserBiz,FrontUser> {

    @Autowired
    private UserLoginLogBiz loginLogBiz;

    @Autowired
    private FrontUserInfoBiz frontUserInfoBiz;


    @Autowired
    private FrontUserBiz frontUserBiz;

    @Autowired
    private DcAssertAccountLogBiz logBiz;

    @Autowired
    private MerchantBiz merchantBiz;

    @Autowired
    private FrontTransferDetailBiz transferCoinBiz;//转币

    @Autowired
    private TransferOrderBiz transferBiz;//转账

    @Autowired
    private MchTradeDetailBiz mchTradeDetailBiz;//用户支付

    @Autowired
    private MchRefundDetailBiz mchRefundDetailBiz;//商户退款


    @Autowired
    private FrontRechargeBiz rechargeBiz;//充值

    @Autowired
    private FrontWithdrawBiz withdrawBiz;//提现

    @Autowired
    private RedPacketsLogBiz redPacketsLogBiz;

    @Autowired
    private RedPacketsSendBiz redPacketsSendBiz;

    @PostMapping("getOwnMember")
    @ApiOperation(value = "获取自己所有直推的会员", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getOwnMember(@RequestBody @Valid PageInfo model) throws Exception {
        Map<String,Object> param = BizControllerUtil.modelToMap(model);
        param.put("parentId",BaseContextHandler.getUserID());
        Query query = new Query(param);
        TableResultResponse resultResponse = queryByCustomPage(query,frontUserInfoBiz);
        List<FrontUserInfo> frontUserInfoList = resultResponse.getData().getRows();
        List<OwnMemberVo> rspVolist = new ArrayList<>();
        frontUserInfoList.forEach((f) ->{
            OwnMemberVo vo = new OwnMemberVo();
            BeanUtils.copyProperties(f,vo);
            BeanUtils.copyProperties(f.getFrontUser(),vo);
            rspVolist.add(vo);
        });
        resultResponse.getData().setRows(rspVolist);

        return resultResponse;

    }

    //身份认证
    @PostMapping("/authuser")
    public Object authUserInfo(@RequestBody @Valid UserAuthModel userAuthModel) {
        // 通过登录名查询出用户
        FrontUser user = ServiceUtil.selectUnionUserInfoByUserName(BaseContextHandler.getUsername(),baseBiz);
        if (user == null || user.getUserInfo() == null) {
            return new ObjectRestResponse().status(ResponseCode.USER_NOT_EXIST);
        }
        if(!user.getUserInfo().getIsValid().equals(ValidType.NO_AUTH.value())){
            return  new ObjectRestResponse().status(ResponseCode.USER_AUTH_DUPLICATE);
        }


        FrontUserInfo userInfo = new FrontUserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setIdCardImgZm(userAuthModel.getIdCardImgZm());
        userInfo.setIdCardImgFm(userAuthModel.getIdCardImgFm());
       // userInfo.setLocationCountry(userAuthModel.getCountryName());
        userInfo.setIdCard(userAuthModel.getIdCard());
        userInfo.setFirstName(userAuthModel.getFirstName());
        userInfo.setRealName(userAuthModel.getRealName());
        userInfo.setIsValid(ValidType.SUBMIT.value());// 待认证
        baseBiz.authUserInfo(userInfo);

        //邮件通知管理人员审核
        SendUtil.sendEmailNotice(EmailTemplateType.USER_AUDIT_REMIND.value(),user.getUserInfo().getExchangeId(),user.getUserName());

        return new ObjectRestResponse();
    }




    //登出注销token接口
    @RequestMapping(value = "/loginout", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse loginout()  {
        String token = BaseContextHandler.getToken();
        if (StringUtils.isNotBlank(token)) {
            CacheUtil.getCache().del(token);
        }
        return new ObjectRestResponse();
    }

    //登陆日志
    @PostMapping("/loginlogs")
    public Object loginlogs(@RequestBody @Valid LoginLogModel model) {

        // 通过登录名查询出用户
        Long userId = BaseContextHandler.getUserID();
        FrontUser user = baseBiz.selectById(userId);
        if (user == null) {
            return new ObjectRestResponse().status(ResponseCode.USER_NOT_EXIST);
        }

        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("userId:equal", user.getId()); // 用户Id
        queryParams.put("page", model.getPage()); // 当前页
        queryParams.put("limit", model.getLimit()); // 每页大小
        queryParams.put("orderByInfo","id"); // 用户Id
        Query query = new Query(queryParams);
        return pageQuery(query,UserLoginLog.class,loginLogBiz);
    }




    //修改登录密码
    @PostMapping("/editpwd")
    public Object editpwd( @RequestBody @Valid EditPwdModel model) {
        // 加上收取验证码的逻辑
        // 通过登录名查询出用户
        FrontUser user = ServiceUtil.selectUnionUserInfoByUserName(BaseContextHandler.getUsername(),baseBiz);
        if (user == null) {
            return new ObjectRestResponse().status(ResponseCode.USER_NOT_EXIST);
        }

//        // 调用验证码公共方法校验验证码
//        CommonBiz.commonVerifyMethod(user, SendMsgType.CHANGE_PWD.value(),
//                model.getMobileCode(), model.getEmailCode());
        //比对密码
        if (SecurityUtil.encryptPassword(model.getOldPassword() + user.getSalt()).equals(user.getUserPwd())) {
            user.setUserPwd(SecurityUtil.encryptPassword(model.getPassword() + user.getSalt()));
            //user.setIsWithdraw(CanWithdrawType.NOWITHDRAW.value());// 修改信息后不允许提币
            //user.setUpdateTime(new Date());
            //user.setUserPwd(model.getPassword());
            baseBiz.updatePwd(user);
            return new ObjectRestResponse();
        }
        return new ObjectRestResponse().status(ResponseCode.OLD_PWD_ERROR);
    }

    //绑定邮箱/手机/
    @PostMapping("/bindPhone")
    public ObjectRestResponse bindPhone(@RequestBody @Valid SetValidModel model, HttpServletRequest request, ModelMap modelMap) {
        //通过传入的手机号判断
        Assert.mobile(model.getPhone());
        Assert.notNull(model.getCountryCode(), "COUNTRYCODE");
        FrontUser frontUserParam = new FrontUser();
        frontUserParam.setMobile(model.getPhone());
        List<FrontUser> userList = baseBiz.selectList(frontUserParam);
        if (StringUtil.listIsNotBlank(userList)) {
            return new ObjectRestResponse().status(ResponseCode.MOBILE_BINDED);
        }
        //通过id,从数据库获取更换手机号之前的用户信息
        FrontUser user = ServiceUtil.selectUnionUserInfoByUserName(BaseContextHandler.getUsername(),baseBiz);
        if (user == null) {
            return new ObjectRestResponse().status(ResponseCode.USER_NOT_EXIST);
        }
        if(VerificationType.OPEN.value()==user.getUserInfo().getIsValidEmail()) {
            SendMsg emailMsg = CacheBizUtil.getCacheSmsMsg(user.getEmail().toLowerCase());
            Assert.notNull(emailMsg, "MSGINFO");
            //验证发送类型
            Assert.equals(emailMsg.getMsgType().toString(), ValidCodeType.EMAIL_CODE.value().toString(),"SYS_VERIFICATION_CODE");
            //验证验证码类型
            Assert.equals(emailMsg.getBizType(), SendMsgType.CHANGE_INFO.value(), "SYS_VERIFICATION_CODE");
            Assert.equals(model.getEmailCode(), emailMsg.getSmsCode(), "SYS_VERIFICATION_CODE");
        }


        //获取该手机号发送的短信信息
        SendMsg sendMsg = CacheBizUtil.getCacheSmsMsg(model.getPhone());
        Assert.notNull(sendMsg,"MSGINFO");
        //发送类型和业务类型要对的上
        boolean bizType = sendMsg.getMsgType().equals(ValidCodeType.PHONE_CODE.value());
        boolean sendMsgType = sendMsg.getBizType().equals(SendMsgType.CHANGE_INFO.value());
        boolean msgCodeEqual = sendMsg.getSmsCode().equals(model.getMobileCode());//验证码是否相等
        if (bizType && sendMsgType && msgCodeEqual) {
            Map<String,Object> param = InstanceUtil.newHashMap("userId",user.getId());
            param.put("account",model.getPhone());
            param.put("countrycode",model.getCountryCode());
            baseBiz.bindAccount(param,AccountType.MOBILE);
            return new ObjectRestResponse();
        }else {
            return new ObjectRestResponse().status(ResponseCode.VERIFICATION_CODE_ERROR);
        }
    }

    //绑定邮箱
    @PostMapping("/bindEmail")
    public ObjectRestResponse bindEmail(@RequestBody @Valid SetValidModel model, HttpServletRequest request, ModelMap modelMap) {
        Assert.email(model.getEmail());
        FrontUser frontUserParam = new FrontUser();
        frontUserParam.setEmail(model.getEmail());
        List<FrontUser> userList = baseBiz.selectList(frontUserParam);
        if (StringUtil.listIsNotBlank(userList)) {
            return new ObjectRestResponse().status(ResponseCode.EMAIL_BINDED);
        }

        //通过id,从数据库获取更换手机号之前的用户信息
        FrontUser user = ServiceUtil.selectUnionUserInfoByUserName(BaseContextHandler.getUsername(),baseBiz);
        if (user == null) {
            return new ObjectRestResponse().status(ResponseCode.USER_NOT_EXIST);
        }
        //邮箱只能绑定  不能修改
        if(StringUtils.isNotBlank(user.getEmail())){
            return new ObjectRestResponse().status(ResponseCode.MODIFY_EMAIL_NOT_SUPPORT);
        }

        if(VerificationType.OPEN.value()==user.getUserInfo().getIsValidPhone()) {
            //验证手机
            SendMsg mobileMsg = CacheBizUtil.getCacheSmsMsg(user.getMobile());
            Assert.notNull(mobileMsg, "MSGINFO");
            //验证发送类型
            Assert.equals(mobileMsg.getMsgType().toString(), ValidCodeType.PHONE_CODE.value().toString(), "SYS_VERIFICATION_CODE");
            //验证验证码类型
            Assert.equals(mobileMsg.getBizType(), SendMsgType.CHANGE_INFO.value(), "SYS_VERIFICATION_CODE");
            Assert.equals(model.getMobileCode(), mobileMsg.getSmsCode(), "SYS_VERIFICATION_CODE");

        }


        // 获取该手机号发送的短信信息
        SendMsg sendMsg = CacheBizUtil.getCacheSmsMsg(model.getEmail());
        Assert.notNull(sendMsg,"MSGINFO");
        // 发送类型和业务类型要对的上
        boolean msgType = sendMsg.getMsgType().equals(ValidCodeType.EMAIL_CODE.value());
        boolean sendMsgType = sendMsg.getBizType().equals(SendMsgType.CHANGE_INFO.value());
        boolean msgCodeEqual = sendMsg.getSmsCode().equals(model.getEmailCode());// 验证码是否相等
        if (msgType && sendMsgType && msgCodeEqual) {
            Map<String,Object> param = InstanceUtil.newHashMap("userId",user.getId());
            param.put("account",model.getEmail());
            baseBiz.bindAccount(param,AccountType.EMAIL);
            return new ObjectRestResponse();
        } else {
            return new ObjectRestResponse().status(ResponseCode.VERIFICATION_CODE_ERROR);
        }

    }



/**
     *
     * @return
     */
    @PostMapping("userAuthInfo")
    @ApiOperation(value = "用戶认证页面用到的信息", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object userAuthInfo() {
        FrontUser user = ServiceUtil.selectUnionUserInfoByUserName(BaseContextHandler.getUsername(),baseBiz);
        if (user == null || user.getUserInfo() == null) {
            return new ObjectRestResponse().status(ResponseCode.USER_NOT_EXIST);
        }
        FrontUserInfo userInfo = user.getUserInfo();
        Map<String,Object> rspParam = InstanceUtil.newHashMap();
        rspParam.put("status",userInfo.getIsValid());
        rspParam.put("phone",StringUtil.midleReplaceStar(user.getMobile()));
        rspParam.put("email",StringUtil.midleReplaceStar(user.getEmail()));
        rspParam.put("idcard",StringUtil.midleReplaceStar(userInfo.getIdCard()));
        rspParam.put("firstName",userInfo.getFirstName());
        rspParam.put("secondName",userInfo.getRealName());

        return new ObjectRestResponse<>().data(rspParam);
    }


    /**
     *
     * @return
     */
    @PostMapping("mchAuthInfo")
    @ApiOperation(value = "商户认证页面用到的信息", produces = MediaType.APPLICATION_JSON_VALUE)
        public Object mchAuthInfo() {
            FrontUser user = baseBiz.selectById(BaseContextHandler.getUserID());
        if (user == null) {
            return new ObjectRestResponse().status(ResponseCode.USER_NOT_EXIST);
        }
        Merchant merchant = merchantBiz.getMerchantInfoByUserId(user.getId());
        Map<String,Object> rspParam = InstanceUtil.newHashMap();
        rspParam.put("status",MchStatus.NOAUTH.value());
        if(merchant != null){
            rspParam.put("status",merchant.getMchStatus());
            rspParam.put("mchName",merchant.getMchName());
            rspParam.put("licenseZm",merchant.getMchLicenseZm());
            rspParam.put("licenseFm",merchant.getMchLicenseFm());
        }
        return new ObjectRestResponse<>().data(rspParam);
    }




    /**
     *
     * @return
     */
    @PostMapping("userInfo")
    @ApiOperation(value = "用戶信息", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object userInfo() {
        FrontUser user = ServiceUtil.selectUnionUserInfoByUserName(BaseContextHandler.getUsername(),baseBiz);
        if (user == null) {
            return new ObjectRestResponse().status(ResponseCode.USER_NOT_EXIST);
        }
        UserInfoRspVo rspVo = new UserInfoRspVo();
        rspVo.setUserSig(Configure.genTestUserSig(user.getUserName()));
        BeanUtils.copyProperties(user,rspVo);
        BeanUtils.copyProperties(user.getUserInfo(),rspVo);
        rspVo.setHeadPictureUrl(user.getUserInfo().getPortrait());
        if(StringUtils.isBlank(user.getTradePwd())){
            rspVo.setIsSetTradePwd(EnableType.DISABLE.value());
        }else{
            rspVo.setIsSetTradePwd(EnableType.ENABLE.value());
        }
        Merchant merchant = merchantBiz.getMerchantInfoByUserId(user.getId());
        if(merchant != null){
            rspVo.setMchStatus(merchant.getMchStatus());
        }
        return new ObjectRestResponse<>().data(rspVo);
    }


    @PostMapping("setValid")
    @ApiOperation(value = "设置是否开启邮箱验证、手机验证", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object setValid( @RequestBody ChangeValidModel model) {
        if(!VerificationType.isType(model.getValidType())){
            return new ObjectRestResponse().status(ResponseCode.PARAM_ERROR);
        }
        if(!ValidCodeType.isType(model.getSendType())){
            return new ObjectRestResponse().status(ResponseCode.PARAM_ERROR);
        }

        FrontUser user = ServiceUtil.selectUnionUserInfoByUserName(BaseContextHandler.getUsername(),baseBiz);
        if (user == null) {
            return new ObjectRestResponse().status(ResponseCode.USER_NOT_EXIST);
        }
        //如果两个都是关闭状态,异常
        if (VerificationType.CLOSE.value() == user.getUserInfo().getIsValidPhone()
                && VerificationType.CLOSE.value() == user.getUserInfo().getIsValidEmail()) {
            return new ObjectRestResponse<>().status(ResponseCode.ONE_MUST_OPENED);
        }

        if (VerificationType.CLOSE.value() == model.getValidType()) {
            //如果要关闭手机验证
            if (ValidCodeType.PHONE_CODE.value().equals(model.getSendType())) {
                //如果邮箱已经关闭,不能关闭手机
                if(user.getUserInfo().getIsValidEmail().equals(VerificationType.CLOSE.value())){
                    return new ObjectRestResponse<>().status(ResponseCode.ONE_MUST_OPENED);
                }
            }
            //如果要关闭手机验证
            if (ValidCodeType.EMAIL_CODE.value().equals(model.getSendType())) {
                //如果手机已经关闭,不能关闭邮箱
                if(user.getUserInfo().getIsValidPhone().equals(VerificationType.CLOSE.value())){
                    return new ObjectRestResponse<>().status(ResponseCode.ONE_MUST_OPENED);
                }
            }
        }

        FrontUserInfo frontUserInfo = new FrontUserInfo();
        frontUserInfo.setUserId(user.getUserInfo().getUserId());
        frontUserInfo = frontUserInfoBiz.selectOne(frontUserInfo);



        if (VerificationType.CLOSE.value() == model.getValidType()) {// 判断是开启还是关闭验证 1-开启 0-关闭
            // 调用验证码公共方法校验验证码
            CommonBiz.commonVerifyMethod(user, SendMsgType.CLOSE_VERIFICATION.value(),
                    model.getMobileCode(), model.getEmailCode());
        }

        FrontUserInfo updateParam = new FrontUserInfo();
        updateParam.setId(frontUserInfo.getId());
        if (ValidCodeType.PHONE_CODE.value().equals(model.getSendType())) {
            updateParam.setIsValidPhone(model.getValidType());
        } else {
            updateParam.setIsValidEmail(model.getValidType());
        }
        frontUserInfoBiz.updateSelectiveById(updateParam);


        if (VerificationType.CLOSE.value() == model.getValidType()) {
            // 调用验证码公共方法校验验证码
            CommonBiz.clearVerifyMethod(user);
        }



        return new ObjectRestResponse<>();
    }



    // 添加或修改修改交易密码
    @PostMapping("/editTradePwd")
    public Object editTradePwd(@RequestBody @Valid EditTradePwdModel model) {
        Assert.length(model.getPassword(),6,6, "TRADE_PASSWORD");
        //通过id,从数据库获取更换手机号之前的用户信息
        FrontUser user = ServiceUtil.selectUnionUserInfoByUserName(BaseContextHandler.getUsername(),baseBiz);
        if (user == null) {
            return new ObjectRestResponse().status(ResponseCode.USER_NOT_EXIST);
        }

        // 调用验证码公共方法校验验证码
        CommonBiz.commonVerifyMethod(user, SendMsgType.CHANGE_PAY_PASSWORD.value(),
                model.getMobileCode(), model.getEmailCode());
        FrontUser paramUser = new FrontUser();
        paramUser.setId(user.getId());
        paramUser.setTradePwd(SecurityUtil.encryptPassword(model.getPassword() + user.getSalt()));
        baseBiz.updateTradePwd(paramUser);
        CommonBiz.clearVerifyMethod(user);
        return new ObjectRestResponse();
    }

    //获取用户资产统计
    @PostMapping("/getAssertLog")
    public Object getAssertLog(@RequestBody @Valid GetAssertLogModel model){
        if( model.getBillType() != null && !BillType.isType(model.getBillType())){
            return new ObjectRestResponse().status(ResponseCode.BILLTYPE_ERROR);
        }
        //设置结束时间+1
        if(StringUtils.isNotBlank(model.getEndDate())){
            LocalDateTime localDateTime  = LocalDateTime.parse(model.getEndDate());
            model.setEndDate(localDateTime.plusDays(1).toString(DateUtil.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS));
        }



        //获取支出和收入
        List<Map<String,Object>> countList = logBiz.getAssertCount(model);
        BigDecimal incomeAmount = BigDecimal.ZERO;
        BigDecimal payAmount = BigDecimal.ZERO;
        if(StringUtil.listIsNotBlank(countList)){
            for(Map<String,Object> map:countList){
                if(map != null){
                    Integer direction = (Integer) map.get("direction");
                    if(direction.equals(DirectionType.PAY.value())){
                        payAmount = (BigDecimal) map.get("amount");
                    }
                    if(direction.equals(DirectionType.INCOME.value())){
                        incomeAmount = (BigDecimal) map.get("amount");
                    }
                }
            };
        }




        GetAssertLogRspVo rspVo = new GetAssertLogRspVo();
        rspVo.setIncomeAmount(incomeAmount);
        rspVo.setPayAmount(payAmount);

        //账单类型国际化
        List<GetAssertLogRspVo.BillVo> billList = new ArrayList<>();
        for(BillType type : BillType.values()) {
            GetAssertLogRspVo.BillVo billVo = rspVo.newBillVo();
            billVo.setName(BillType.getName(type));
            billVo.setBillType(type.value());
            billList.add(billVo);
        }
        rspVo.setTypeList(billList);



        //获取分页流水数据
        Map<String,Object> param = InstanceUtil.newHashMap();
        param.put("userId", BaseContextHandler.getUserID());
        param.put("page", model.getPage());
        param.put("limit", model.getLimit());
        param.put("type:in",AccountLogType.getBillTypeList(model.getBillType()));
        param.put("symbol", model.getSymbol());
        param.put("createTime:>=",model.getBeginDate());
        param.put("createTime:<=",model.getEndDate());

        Query query = new Query(param);
        query.setOrderByInfo("create_time desc");

        TableResultResponse pageResult = pageQuery(query, DcAssetAccountLog.class,logBiz);
        if(pageResult !=null && pageResult.getData() != null && StringUtil.listIsNotBlank(pageResult.getData().getRows())){
            List<DcAssetAccountLog> list = pageResult.getData().getRows();
            List<GetAssertLogRspVo.DataVo> rspVoList = new ArrayList<>(list.size());
            list.forEach((f) ->{
                GetAssertLogRspVo.DataVo dataVo = rspVo.newDataVo();
                String directionSymbol = DirectionType.getDirectionSymbol(f.getDirection());
                dataVo.setId(f.getId());//流水Id
                dataVo.setAmount(directionSymbol + f.getAmount().stripTrailingZeros().toPlainString() + " " + f.getSymbol());
                dataVo.setBillType(AccountLogType.getBillType(f.getType()).value());
                dataVo.setCreateTime(f.getCreateTime());
                if(StringUtils.isNotBlank(f.getRemark())){
                    dataVo.setRemark(AccountLogType.getName(f.getRemark()));
                }
                rspVoList.add(dataVo);
            });
            //pageResult =  new TableResultResponse(pageResult.getData().getTotal(),rspVoList);
            rspVo.setDataVoList(rspVoList);
            rspVo.setTotal(pageResult.getData().getTotal());
        }
        return new ObjectRestResponse<>().data(rspVo);
    }


    //流水详情
    @PostMapping("/assertLogDetail")
    public Object assertLogDetail(@RequestParam Long id){
        if( id == null || id.longValue() < 1){
            return  new ObjectRestResponse<>().status(ResponseCode.PARAM_ERROR);
        }
        DcAssetAccountLog logParam = new DcAssetAccountLog();
        logParam.setId(id);
        logParam.setUserId(BaseContextHandler.getUserID());
        DcAssetAccountLog log = logBiz.selectOne(logParam);
        if(log == null){
            return  new ObjectRestResponse<>().status(ResponseCode.PARAM_ERROR);
        }

        Map<String,Object> rspMap = InstanceUtil.newHashMap();
        List<String> headList = InstanceUtil.newLinkedList();
        Map<String,Object> bodyList = InstanceUtil.newLinkedHashMap();
        String headRemark = AccountLogType.getName(log.getRemark());//操作类型 + 对象
        String directionSymbol = DirectionType.getDirectionSymbol(log.getDirection());
        String amount = directionSymbol + log.getAmount().stripTrailingZeros().toPlainString() + " " + log.getSymbol(); //方向 + 具体金额 + 代币

        String transNo = log.getTransNo();//平台流水号
        Long time = log.getCreateTime().getTime();//时间戳
        String balance = log.getAfterTotal().stripTrailingZeros().toPlainString() + " " +  log.getSymbol();//余额

        //共有部分
        headList.add(headRemark);
        headList.add(amount);

        bodyList.put(Resources.getMessage("TRANSNO"),transNo);
        bodyList.put(Resources.getMessage("BALANCE"),balance);
        bodyList.put("time",time);


        rspMap.put("head",headList);
        rspMap.put("body",bodyList);
        //转账相关
        if(log.getType().intValue() == AccountLogType.TRANSFER.value().intValue()
                || log.getType().intValue() == AccountLogType.RECEIVE_TRANSFER.value().intValue()
                || log.getType().intValue() == AccountLogType.RP_TRANSFER.value().intValue()
                || log.getType().intValue() == AccountLogType.RP_RECEIVE_TRANSFER.value().intValue()
                ){
            //转账需要加上对手方
            TransferOrder queryParam = new TransferOrder();
            queryParam.setOrderNo(log.getTransNo());
            TransferOrder transferOrder = transferBiz.selectOne(queryParam);
            if(transferOrder == null){
                return new ObjectRestResponse<>();
            }
            Long userId = log.getType().intValue() == AccountLogType.TRANSFER.value().intValue() || log.getType().intValue() == AccountLogType.RP_TRANSFER.value().intValue()
                    ? transferOrder.getReceiveUserId() : transferOrder.getUserId();
            //查询用户名
            FrontUser user =  CacheBizUtil.getFrontUserCache(userId,frontUserBiz);
            if(user != null && user.getUserInfo() != null){
                bodyList.put(Resources.getMessage("TOUSERNAME"),user.getUserName());
                if(StringUtils.isNotBlank(user.getUserInfo().getRealName())){
                    String desp = log.getType().intValue() == AccountLogType.TRANSFER.value().intValue() ? Resources.getMessage("TRANSFERTO") : Resources.getMessage("TRANSFERFROM");
                    headRemark += " "+desp +" *" + user.getUserInfo().getRealName();//加上转账对象
                    headList.remove(0);
                    headList.add(0,headRemark);
                }
            }

        } else if( log.getType().intValue() == AccountLogType.SEND_REDPACKETS.value().intValue() || log.getType().intValue() == AccountLogType.SNATCH_REDPACKETS.value().intValue()) {
            Long userId = null;
            //发红包  查发红包表
            if(log.getType().intValue() == AccountLogType.SEND_REDPACKETS.value() ){
                RedPacketLog queryParam = new RedPacketLog();
                queryParam.setOrderNo(log.getTransNo());
                queryParam.setUserId(BaseContextHandler.getUserID());
                List<RedPacketLog> transferOrder = redPacketsLogBiz.selectList(queryParam);
                //如果爲空或者是群紅包
                if(StringUtil.listIsBlank(transferOrder)){
                    return new ObjectRestResponse<>().data(rspMap);
                }
                //群红包
                if( transferOrder.size() > 1){
                    return new ObjectRestResponse<>().data(rspMap);
                }
                //一对一红包的对手方
                userId = transferOrder.get(0).getReceiveUserId();
            }else {
                //抢红包
                RedPacketLog queryParam = new RedPacketLog();
                queryParam.setOrderNo(log.getTransNo());
                queryParam.setReceiveUserId(BaseContextHandler.getUserID());
                RedPacketLog transferOrder = redPacketsLogBiz.selectOne(queryParam);
                if(transferOrder == null){
                    return new ObjectRestResponse<>().data(rspMap);
                }
                //发红包人的id
                userId =  transferOrder.getUserId();
            }

            if(userId == null){
                return new ObjectRestResponse<>();
            }
            //查询用户名
            FrontUser user =  CacheBizUtil.getFrontUserCache(userId,frontUserBiz);
            if(user != null && user.getUserInfo() != null){
                bodyList.put(Resources.getMessage("TOUSERNAME"),user.getUserName());
                if(StringUtils.isNotBlank(user.getUserInfo().getRealName())){
                    String desp = log.getType().intValue() == AccountLogType.TRANSFER.value().intValue() ? Resources.getMessage("TRANSFERTO") : Resources.getMessage("TRANSFERFROM");
                    headRemark += " "+desp +" *" + user.getUserInfo().getRealName();//加上转账对象
                    headList.remove(0);
                    headList.add(0,headRemark);
                }
            }
        }
        else if(log.getType().intValue() == AccountLogType.WITHDRAW.value().intValue()){
            //提现
            FrontWithdraw queryParam = new FrontWithdraw();
            queryParam.setTransNo(log.getTransNo());
            FrontWithdraw frontWithdraw = withdrawBiz.selectOne(queryParam);
            if(frontWithdraw == null){
                return new ObjectRestResponse<>().data(rspMap);
            }
            bodyList.put(Resources.getMessage("WITHDRAWADD"),frontWithdraw.getUserAddress());
            bodyList.put(Resources.getMessage("BLOCKID"),frontWithdraw.getTransactionId());
        } else if(log.getType().intValue() == AccountLogType.RECHARGE.value().intValue()){
            //充值
            FrontRecharge queryParam = new FrontRecharge();
            queryParam.setOrderId(Long.valueOf(log.getTransNo()));
            FrontRecharge recharge = rechargeBiz.selectOne(queryParam);
            if(recharge == null){
                return new ObjectRestResponse<>().data(rspMap);
            }
            bodyList.put(Resources.getMessage("RECHARGEADD"),recharge.getUserAddress());
            bodyList.put(Resources.getMessage("BLOCKID"),recharge.getBlockOrderId());
        } else if(log.getType().intValue() == AccountLogType.TRANS_COIN_PAY.value().intValue() ||
                log.getType().intValue() == AccountLogType.TRANS_COIN_INCOME.value().intValue()){
            //转币
            FrontTransferDetail queryParam = new FrontTransferDetail();
            queryParam.setOrderNo(log.getTransNo());
            FrontTransferDetail transferDetail = transferCoinBiz.selectOne(queryParam);
            if(transferDetail == null){
                return new ObjectRestResponse<>().data(rspMap);
            }
            //如果是支出的代币符号和转币记录的中的兑换前币种一样或者收入的代币符号等于兑换后币种
            if(     (log.getSymbol().equals(transferDetail.getSrcSymbol()) && log.getDirection().intValue() == DirectionType.PAY.value().intValue())
                    ||
                    (log.getSymbol().equals(transferDetail.getDstSymbol()) && log.getDirection().intValue() == DirectionType.INCOME.value().intValue()) ) {
                bodyList.put(Resources.getMessage("SRCSYMBOL"),transferDetail.getSrcSymbol());//兑换前币种
                bodyList.put(Resources.getMessage("DSTSYMBOL"),transferDetail.getDstSymbol());//兑换后币种
            } else {
                bodyList.put(Resources.getMessage("SRCSYMBOL"),transferDetail.getDstSymbol());//兑换前币种
                bodyList.put(Resources.getMessage("DSTSYMBOL"),transferDetail.getSrcSymbol());//兑换后币种
            }



        } else if(log.getType().intValue() == AccountLogType.PAY_MERCHANT.value().intValue() ||
                log.getType().intValue() == AccountLogType.MERCHANT_SETTLE.value().intValue()){
            //支付给商户，商户得到付款
            MchTradeDetail queryParam = new MchTradeDetail();
            queryParam.setWalletOrderNo(log.getTransNo());
            MchTradeDetail mchTradeDetail = mchTradeDetailBiz.selectOne(queryParam);
            if(mchTradeDetail == null){
                return new ObjectRestResponse<>().data(rspMap);
            }
            Merchant merchant = merchantBiz.selectById(mchTradeDetail.getMchId());
            bodyList.put(Resources.getMessage("MCHNO"),merchant.getMchName());
            bodyList.put(Resources.getMessage("MCHORDERNO"),mchTradeDetail.getMchOrderNo());
            //付款方
            if(log.getType().intValue() == AccountLogType.MERCHANT_SETTLE.value().intValue()){
                FrontUser user =  CacheBizUtil.getFrontUserCache(mchTradeDetail.getUserId(),frontUserBiz);
                if(user != null && user.getUserInfo() != null && StringUtils.isNotBlank(user.getUserInfo().getRealName())){
                    bodyList.put(Resources.getMessage("PAYUSER"),"*" + user.getUserInfo().getRealName());
                }

            }

        }else if(log.getType().intValue() == AccountLogType.MERCHANT_REFUND.value().intValue() ||
                log.getType().intValue() == AccountLogType.RECEIVE_REFUND.value().intValue()){
            //商户退款，用户得到付款
            MchRefundDetail queryParam = new MchRefundDetail();
            queryParam.setWalletOrderNo(log.getTransNo());
            MchRefundDetail mchRefundDetail = mchRefundDetailBiz.selectOne(queryParam);
            if(mchRefundDetail == null){
                return new ObjectRestResponse<>().data(rspMap);
            }
            Merchant merchant = merchantBiz.selectById(mchRefundDetail.getMchId());
            bodyList.put(Resources.getMessage("MCHNO"),merchant.getMchName());
            bodyList.put(Resources.getMessage("MCHORDERNO"),mchRefundDetail.getMchOrderNo());
            //退款收款方
            if(log.getType().intValue() == AccountLogType.MERCHANT_REFUND.value().intValue()){
                FrontUser user =  CacheBizUtil.getFrontUserCache(mchRefundDetail.getUserId(),frontUserBiz);
                if(user != null && user.getUserInfo() != null && StringUtils.isNotBlank(user.getUserInfo().getRealName())){
                    bodyList.put(Resources.getMessage("RECIEVEUSER"),"*" + user.getUserInfo().getRealName());
                }

            }

        }




        return new ObjectRestResponse<>().data(rspMap);
    }
}
