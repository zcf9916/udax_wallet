package com.udax.front.biz.merchant;

import com.alibaba.fastjson.JSON;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.entity.merchant.MchNotify;
import com.github.wxiaoqi.security.common.entity.merchant.MchPayToken;
import com.github.wxiaoqi.security.common.entity.merchant.MchRefundDetail;
import com.github.wxiaoqi.security.common.entity.merchant.MchTradeDetail;
import com.github.wxiaoqi.security.common.entity.merchant.Merchant;
import com.github.wxiaoqi.security.common.enums.AccountLogType;
import com.github.wxiaoqi.security.common.enums.AccountSignType;
import com.github.wxiaoqi.security.common.enums.MchResponseCode;
import com.github.wxiaoqi.security.common.enums.merchant.MchNotifyStatus;
import com.github.wxiaoqi.security.common.enums.merchant.MchOrderStatus;
import com.github.wxiaoqi.security.common.enums.merchant.MchRefundAccountType;
import com.github.wxiaoqi.security.common.exception.MchException;
import com.github.wxiaoqi.security.common.mapper.merchant.MchNotifyMapper;
import com.github.wxiaoqi.security.common.mapper.merchant.MchRefundDetailMapper;
import com.github.wxiaoqi.security.common.mapper.merchant.MchTradeDetailMapper;
import com.github.wxiaoqi.security.common.mapper.merchant.MerchantMapper;
import com.github.wxiaoqi.security.common.task.CallbackMsg;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.udax.front.biz.DcAssertAccountBiz;
import com.udax.front.util.SignUtil;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import com.udax.front.vo.reqvo.merchant.MchPayTokenModel;
import com.udax.front.vo.reqvo.merchant.MchRefundModel;
import com.udax.front.vo.rspvo.merchant.RefundNotifyVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static com.github.wxiaoqi.security.common.constant.Constants.MCH_CALLBACK_TASK;
import static com.github.wxiaoqi.security.common.enums.AccountSignType.ACCOUNT_PAY_AVAILABLE;
import static com.github.wxiaoqi.security.common.enums.AccountSignType.MERCHANT_REFUND_SETTLE;

/**
 * 
 *
 * @author Mr.AG
 * @email 463540703@qq.com
 * @date 2017-12-26 19:43:46
 */
@Service
@Slf4j
public class MchRefundDetailBiz extends BaseBiz<MchRefundDetailMapper,MchRefundDetail> {

    @Autowired
    private MchTradeDetailMapper mchTradeDetailMapper;

    @Autowired
    private DcAssertAccountBiz dcAssertAccountBiz;


    @Autowired
    private MchNotifyMapper notifyMapper;
    @Autowired
    private MerchantMapper merchantMapper;

    @Transactional(rollbackFor = Exception.class)
    public MchRefundDetail refund(MchRefundModel model,Long mchId,String ip) throws  Exception{
        if(!MchRefundAccountType.isType(model.getRefundAccountType())){
            throw new MchException(MchResponseCode.MERCHANT_REFUND_ACCOUNT_TYPE.name());
        }
        //查询原支付订单
        MchTradeDetail param = new MchTradeDetail();
        if(StringUtils.isNotBlank(model.getTransNo())){
            boolean ifMatch = Pattern.matches("^[0-9]{1,32}$",model.getTransNo());
            if(!ifMatch){
                throw new MchException(MchResponseCode.MERCHANT_TRANS_NO.name());
            }
            param.setWalletOrderNo(model.getTransNo());
        } else if(StringUtils.isNotBlank(model.getMchOrderNo())){
            boolean ifMatch = Pattern.matches("^[a-zA-Z0-9]{1,32}$",model.getMchOrderNo());
            if(!ifMatch){
                throw new MchException(MchResponseCode.MERCHANT_ORDER_NO.name());
            }
            param.setMchId(mchId);
            param.setMchOrderNo(model.getMchOrderNo());
        }
       //锁定这个订单
        MchTradeDetail mchTradeDetail = mchTradeDetailMapper.selectForUpdate(param);
        //原支付订单是否存在
        if(mchTradeDetail == null || mchTradeDetail.getStatus().intValue() == MchOrderStatus.UNPAY.value()){
            throw new MchException(MchResponseCode.MERCHANT_TRANS_NO.name());
        }
        MchRefundDetail countParam = new MchRefundDetail();
        countParam.setMchId(mchTradeDetail.getMchId());
        countParam.setMchOrderNo(model.getRefundMchOrderNo());
        Integer count = mapper.selectCount(countParam);
        if( count > 0 ){
            throw new MchException(MchResponseCode.MERCHANT_REFUND_ORDER_NO_DUP.name());
        }


        //可退款总数量
        BigDecimal refundTotalAmount = mchTradeDetail.getAmount().subtract(mchTradeDetail.getChargeAmount()).subtract(mchTradeDetail.getRefundAmount());
        if(mchTradeDetail.getStatus().equals(MchOrderStatus.REFUND_ALL.value()) || refundTotalAmount.compareTo(model.getRefundAmount()) <= 0){
            throw new MchException(MchResponseCode.MERCHANT_ORDER_REFUND_BALANCE.name());
        }
        AccountSignType signType = null;
        if(model.getRefundAccountType().intValue() == MchRefundAccountType.REFUND_SOURCE_AVAILABLE.value().intValue()){
            signType = ACCOUNT_PAY_AVAILABLE;
        } else if(model.getRefundAccountType().intValue() == MchRefundAccountType.REFUND_SOURCE_WAITCONFIRM.value().intValue()){
            signType = MERCHANT_REFUND_SETTLE;
        } else {
            throw new MchException(MchResponseCode.MERCHANT_REFUND_ACCOUNT_TYPE.name());
        }


        String refundTransNo = String.valueOf(IdGenerator.nextId());

        AccountAssertLogVo payVo = new AccountAssertLogVo();
        payVo.setUserId(mchTradeDetail.getMchUserId());
        payVo.setSymbol(mchTradeDetail.getSymbol());
        payVo.setAmount(model.getRefundAmount());
        payVo.setChargeSymbol(mchTradeDetail.getSymbol());
        payVo.setType(AccountLogType.MERCHANT_REFUND);//商户退款
        payVo.setTransNo(refundTransNo);//退款流水号
        payVo.setRemark(AccountLogType.MERCHANT_REFUND.name());
        //扣减可用字段
        dcAssertAccountBiz.signUpdateAssert(payVo,signType);
        AccountAssertLogVo receive = new AccountAssertLogVo();
        receive.setUserId(mchTradeDetail.getUserId());
        receive.setSymbol(mchTradeDetail.getSymbol());
        receive.setAmount(model.getRefundAmount());
        receive.setChargeSymbol(mchTradeDetail.getSymbol());
        receive.setTransNo(refundTransNo);//退款流水号
        receive.setRemark(AccountLogType.RECEIVE_REFUND.name());
        receive.setType(AccountLogType.RECEIVE_REFUND);//接收退款
        //增加可用字段
        dcAssertAccountBiz.signUpdateAssert(receive,AccountSignType.ACCOUNT_RECHARGE);




        //生成退款记录
        MchRefundDetail mchRefundDetail = new MchRefundDetail();
        mchRefundDetail.setTotalAmount(mchTradeDetail.getAmount());//原订单总金额
        mchRefundDetail.setAmount(model.getRefundAmount());//退款数量
        mchRefundDetail.setCreateTime(new Date());//订单创建时间
        mchRefundDetail.setMchId(mchTradeDetail.getMchId());//商户id
        mchRefundDetail.setMchNo(mchTradeDetail.getMchNo());//商户号
        mchRefundDetail.setIp(ip);//ip
        mchRefundDetail.setRefundAccountType(model.getRefundAccountType());
        mchRefundDetail.setMchUserId(mchTradeDetail.getMchUserId());//商户对应用户id
        mchRefundDetail.setWalletOrderNo(refundTransNo);//订单号
        mchRefundDetail.setMchOrderNo(model.getRefundMchOrderNo());//商户退款订单号
        mchRefundDetail.setOriMchOrderNo(mchTradeDetail.getMchOrderNo());//商户原订单号
        mchRefundDetail.setOriWalletOrderNo(mchTradeDetail.getWalletOrderNo());//原流水号
        mchRefundDetail.setNotifyUrl(model.getRefundNotifyUrl());//通知地址
        mchRefundDetail.setRefundRemark(model.getRefundRemark());//退款备注
        mchRefundDetail.setSymbol(mchTradeDetail.getSymbol());//代币
//        mchRefundDetail.setTradeType(mchTradeDetail.getTradeType());//交易方式
        mchRefundDetail.setUserId(mchTradeDetail.getUserId());//退款用户id
        mchRefundDetail.setNonceStr(model.getNonceStr());//随机字符串
        mapper.insertSelective(mchRefundDetail);

        //更新退款金额
        MchTradeDetail updateParam = new MchTradeDetail();
        updateParam.setRefundAmount(mchTradeDetail.getRefundAmount().add(model.getRefundAmount()));
        updateParam.setId(mchTradeDetail.getId());
        if(updateParam.getRefundAmount().compareTo(mchTradeDetail.getAmount().subtract(mchTradeDetail.getChargeAmount())) >= 0){
            updateParam.setStatus(MchOrderStatus.REFUND_ALL.value());
        } else {
            updateParam.setStatus(MchOrderStatus.REFUND_PART.value());
        }

        mchTradeDetailMapper.updateByPrimaryKeySelective(updateParam);



        Merchant merchant = merchantMapper.selectByPrimaryKey(mchTradeDetail.getMchId());

        // 推送给商户到账成功通知
        RefundNotifyVo vo = new RefundNotifyVo();
//        vo.setAmount(mchRefundDetail.getAmount().stripTrailingZeros().toPlainString());//時間数量
//        vo.setSymbol(mchRefundDetail.getSymbol());
        List<MchPayTokenModel> modelList= new ArrayList<MchPayTokenModel>();
    	for (MchPayToken mchPayToken : mchTradeDetail.getTokenList()) {
    		MchPayTokenModel payModel = new MchPayTokenModel();
    		payModel.setAmount(mchPayToken.getAmount());
    		payModel.setSymbol(mchPayToken.getSymbol());
    		modelList.add(payModel);
		}
    	vo.setTokenList(modelList);
        vo.setMchNo(String.valueOf(merchant.getMchNo()));//商户号
        vo.setNotifyId(String.valueOf(IdGenerator.nextId()));
        vo.setTime(mchRefundDetail.getCreateTime());
        vo.setTransNo(mchRefundDetail.getWalletOrderNo());//退款流水号


        vo.setOriMchOrderNo(mchRefundDetail.getOriMchOrderNo());
        vo.setRefundAccountType(mchRefundDetail.getRefundAccountType());
        vo.setOriTransNo(mchRefundDetail.getOriWalletOrderNo());
        vo.setNonceStr(mchRefundDetail.getNonceStr());
        vo.setMchOrderNo(mchRefundDetail.getMchOrderNo());//商户退款订单号
        String sign=SignUtil.sign(vo, merchant.getSecretKey());//用商户秘钥加密
        vo.setSign(sign);

        MchNotify mchNotify = new MchNotify();
        mchNotify.setType(MchNotifyStatus.REFUND_SUCCESS_NOTICE.value());
        mchNotify.setMchId(merchant.getId());
        mchNotify.setNotifyId(Long.parseLong(vo.getNotifyId()));
        mchNotify.setOrderNo(vo.getTransNo());
        mchNotify.setCreateTime(new Date());
        mchNotify.setCount(0);
        mchNotify.setNotifyStr(JSON.toJSONString(vo));//签名字符串
        mchNotify.setCallbackUrl(mchRefundDetail.getNotifyUrl());
        notifyMapper.insertSelective(mchNotify);

        CallbackMsg msg = new CallbackMsg();
        BeanUtils.copyProperties(mchNotify,msg);
        CacheUtil.getCache().setSAdd(MCH_CALLBACK_TASK,msg);


        return  mchRefundDetail;
    }

}