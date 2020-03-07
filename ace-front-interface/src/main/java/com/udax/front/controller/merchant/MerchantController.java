package com.udax.front.controller.merchant;

import com.github.wxiaoqi.security.common.configuration.KeyConfiguration;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.merchant.MchNotify;
import com.github.wxiaoqi.security.common.entity.merchant.MchPayToken;
import com.github.wxiaoqi.security.common.entity.merchant.MchTradeDetail;
import com.github.wxiaoqi.security.common.entity.merchant.Merchant;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.enums.merchant.MchNotifyStatus;
import com.github.wxiaoqi.security.common.enums.merchant.MchStatus;
import com.github.wxiaoqi.security.common.exception.MchException;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.task.CallbackMsg;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.LocalDateUtil;
import com.github.wxiaoqi.security.common.util.SendUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.udax.front.biz.FrontUserBiz;
import com.udax.front.biz.merchant.MchNotifyBiz;
import com.udax.front.biz.merchant.MchTradeDetailBiz;
import com.udax.front.biz.merchant.MerchantBiz;
import com.udax.front.controller.BaseFrontController;
import com.udax.front.service.ServiceUtil;
import com.udax.front.util.SignUtil;
import com.udax.front.vo.reqvo.merchant.MchAuthModel;
import com.udax.front.vo.reqvo.merchant.MchCheckPreIdModel;
import com.udax.front.vo.reqvo.merchant.MchPayOrderModel;
import com.udax.front.vo.reqvo.merchant.MchPayTokenModel;
import com.udax.front.vo.rspvo.merchant.CheckPreIdRspVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.github.wxiaoqi.security.common.constant.Constants.MCH_CALLBACK_TASK;

/**
 * @author zhoucf
 * @create 2018／3/13 商户订单相关接口
 */
@RestController
@RequestMapping("/wallet/merchant/")
public class MerchantController extends BaseFrontController<MerchantBiz,Merchant> {


    @Autowired
    private MchTradeDetailBiz mchTradeDetailBiz;

    @Autowired
    private FrontUserBiz frontUserBiz;

    @Autowired
    private KeyConfiguration config;

    @Autowired
    private MchNotifyBiz mchNoticeBiz;





    //商户认证接口
    @PostMapping("/auth")
    public Object authUserInfo(@RequestBody @Valid MchAuthModel merchantAuthModel) {
        // 通过登录名查询出用户
        FrontUser user = ServiceUtil.selectUnionUserInfoById(BaseContextHandler.getUserID(),frontUserBiz);
        if (user == null || user.getUserInfo() == null) {
            return new ObjectRestResponse().status(ResponseCode.USER_NOT_EXIST);
        }
        //用户状态是否正常
        if(user.getUserStatus().intValue() != FrontUserStatus.ACTIVE.value()){
            return new ObjectRestResponse().status(ResponseCode.UNACTIVE);
        }
        //用户是否通过实名认证
        if(user.getUserInfo().getIsValid() != ValidType.AUTH.value()){
            return new ObjectRestResponse().status(ResponseCode.UNAUTH);
        }
        //查询是否已经认证过了
        Merchant merchantParam = new Merchant();
        merchantParam.setUserId(Long.valueOf(BaseContextHandler.getUserID()));
        Merchant merchant = baseBiz.selectOne(merchantParam);

        //重复认证,提交認證狀態不能重複認證
        if(merchant != null && merchant.getMchStatus().intValue() >  MchStatus.NOAUTH.value().intValue()){
            throw new MchException(MchResponseCode.MERCHANT_AUTH_DUPLICATE.name());
        }
        //状态改为提交审核
        if(merchant != null){
            merchantParam.setId(merchant.getId());//如果是第二次提交,存在ID
        }
        merchantParam.setMchStatus(MchStatus.SUBMIT.value());
        merchantParam.setCreateTime(new Date());
        merchantParam.setMchLicenseZm(merchantAuthModel.getLicenseZm());//商户营业执照正面
        merchantParam.setMchLicenseFm(merchantAuthModel.getLicenseFm());//商户营业执照反面
//        merchantParam.setSalt(RandomStringUtils.randomAlphabetic(20));//盐值
//        merchantParam.setMchManagerPwd(SecurityUtil.encryptPassword(merchantAuthModel.getLoginPwd()+ merchantParam.getSalt()));//商户后台登录密码
        merchantParam.setMchName(merchantAuthModel.getMchName());//商户名

        baseBiz.authMerchant(merchantParam);

        //邮件通知管理人员审核
        SendUtil.sendEmailNotice(EmailTemplateType.MERCHANT_REVIEW.value(), user.getUserInfo().getExchangeId(), user.getUserName());

        return new ObjectRestResponse();
    }



    //检查预支付标识
    @PostMapping("/checkPreId")
    public Object payOrder(@RequestBody @Valid MchCheckPreIdModel model) throws Exception{
//        // 通过登录名查询出用户
//        ObjectRestResponse response = validMchWithOutSign(Long.valueOf(model.getMchNo()),baseBiz);
//        if(!response.isRel()) return  response;
//        Merchant merchant = (Merchant) response.getData();
        //查询标示表
        MchNotify param = new MchNotify();
        try{
            param.setNotifyId(Long.valueOf(model.getPrepayId()));
        } catch (Exception e){
            throw new MchException(MchResponseCode.MERCHANT_PREID_ERROR.name());
        }

        MchNotify notice =  mchNoticeBiz.selectOne(param);
        ObjectRestResponse response = new ObjectRestResponse();
        if(notice == null || notice.getType().intValue() != MchNotifyStatus.PREORDER_NOTICE.value().intValue() || StringUtils.isBlank(notice.getOrderNo())){
            throw new MchException(MchResponseCode.MERCHANT_PREID_ERROR.name());
        }
        MchTradeDetail mchparam = new MchTradeDetail();
        mchparam.setWalletOrderNo(notice.getOrderNo());
        MchTradeDetail mchTradeDetail = mchTradeDetailBiz.selectByOrderNo(mchparam);
        //预支付标识错误
        if(mchTradeDetail == null){
            throw new MchException(MchResponseCode.MERCHANT_PREID_ERROR.name());
        }
//        //判断商户号是否一致
//        if(!mchTradeDetail.getMchNo().toString().equals(model.getMchNo())){
//            response.status(ResponseCode.MERCHANT_PREID_ERROR);
//            return  response;
//        }
        //判断过期时间
        if(LocalDateTime.now().isAfter(LocalDateUtil.date2LocalDateTime(mchTradeDetail.getExpireTime()))){
            return new ObjectRestResponse().status(ResponseCode.TRANSFER_DEALED);
        }
        Merchant merchant = baseBiz.selectById(mchTradeDetail.getMchId());

        //返回订单的相关信息并签名
        CheckPreIdRspVo rspVo = new CheckPreIdRspVo();
        rspVo.setPrepayId(model.getPrepayId());//预支付id
//        rspVo.setAmount(mchTradeDetail.getAmount());//代币数量
//        rspVo.setSymbol(mchTradeDetail.getSymbol());//代币
        List<MchPayToken> tokenList=mchTradeDetail.getTokenList(); //设置多币种支付信息
        List<MchPayTokenModel> modelList= new ArrayList<MchPayTokenModel>();
    	for (MchPayToken mchPayToken : tokenList) {
    		MchPayTokenModel payModel = new MchPayTokenModel();
    		payModel.setAmount(mchPayToken.getAmount());
    		payModel.setSymbol(mchPayToken.getSymbol());
    		modelList.add(payModel);
		}
        rspVo.setTokenList(modelList);
        rspVo.setMerchantName(merchant.getMchName());//商户名
        rspVo.setBody(mchTradeDetail.getBody());//商品描述
        String  secrectSign = SignUtil.sign(rspVo,config.getMerchantKey());
        rspVo.setSign(secrectSign);//签名

        return  new ObjectRestResponse<>().data(rspVo);
    }


    //用户支付订单
    @PostMapping("/payOrder")
    public Object payOrder(@RequestBody @Valid MchPayOrderModel model) throws Exception{
        // 通过登录名查询出用户
//        ObjectRestResponse response = validMchWithOutSign(Long.valueOf(model.getMchNo()),baseBiz);
//        if(!response.isRel()) return  response;
        boolean flag = ServiceUtil.judgePayPassword(model.getPassword(),BaseContextHandler.getUserID(),frontUserBiz);
        if(!flag){
            return new ObjectRestResponse<>().status(ResponseCode.TRADE_PWD_ERROR);
        }
        MchNotify notify =  mchTradeDetailBiz.payOrder(model.getPrepayId(),BaseContextHandler.getUserID());
        if(notify != null){
            //加入通知回调队列
            CallbackMsg msg = new CallbackMsg();
            BeanUtils.copyProperties(notify,msg);
            CacheUtil.getCache().setSAdd(MCH_CALLBACK_TASK,msg);
        }
        return new ObjectRestResponse<>();
//        response = mchTradeDetailBiz.payOrder(model.getPrepayId(),model.getMchId(),BaseContextHandler.getUserID());
//        if(!response.isRel()){
//            return  response;
//        }
//        MchTradeDetail mchTradeDetail = (MchTradeDetail) response.getData();


    }

}

