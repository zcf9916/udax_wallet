package com.udax.front.biz.merchant;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.github.wxiaoqi.security.common.entity.merchant.Merchant;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.enums.merchant.MchOrderSettleStatus;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.exception.MchException;
import com.github.wxiaoqi.security.common.mapper.merchant.MerchantMapper;
import com.udax.front.util.SignUtil;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import com.udax.front.vo.reqvo.merchant.MchPayTokenModel;
import com.udax.front.vo.rspvo.merchant.PayNotifyVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.entity.merchant.MchNotify;
import com.github.wxiaoqi.security.common.entity.merchant.MchPayToken;
import com.github.wxiaoqi.security.common.entity.merchant.MchTradeDetail;
import com.github.wxiaoqi.security.common.enums.merchant.MchNotifyStatus;
import com.github.wxiaoqi.security.common.enums.merchant.MchOrderStatus;
import com.github.wxiaoqi.security.common.mapper.merchant.MchNotifyMapper;
import com.github.wxiaoqi.security.common.mapper.merchant.MchPayTokenMapper;
import com.github.wxiaoqi.security.common.mapper.merchant.MchTradeDetailMapper;
import com.github.wxiaoqi.security.common.util.LocalDateUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.udax.front.biz.DcAssertAccountBiz;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 *
 * @author Mr.AG
 * @email 463540703@qq.com
 * @date 2017-12-26 19:43:46
 */
@Service
@Slf4j
public class MchTradeDetailBiz extends BaseBiz<MchTradeDetailMapper,MchTradeDetail> {

    @Autowired
    private DcAssertAccountBiz dcAssertAccountBiz;

    @Autowired
    private MchNotifyMapper notifyMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private MerchantBiz merchantBiz;
    
    @Autowired
    private MchPayTokenMapper mchPayTokenMapper;

    /**
     * 预下单,返回预下单标示
     * @param mchTradeDetail
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Long preOrder(MchTradeDetail mchTradeDetail){
        MchTradeDetail mchparam = new MchTradeDetail();
        mchparam.setMchId(mchTradeDetail.getMchId());
        mchparam.setMchOrderNo(mchTradeDetail.getMchOrderNo());
        MchTradeDetail record = mapper.selectForUpdate(mchparam);
        if( record != null){
            MchNotify notifyParam = new MchNotify();
            notifyParam.setOrderNo(record.getWalletOrderNo());
            MchNotify notify = notifyMapper.selectOne(notifyParam);
            if( notify != null){
                return notify.getNotifyId();
            }
            throw new MchException(MchResponseCode.MERCHANT_PRE_ORDER_NO_DUP.name());
        }

        MchNotify mchNotify = new MchNotify();
        mchNotify.setMchId(mchTradeDetail.getMchId());
        mchNotify.setCreateTime(new Date());
        mchNotify.setNotifyId(IdGenerator.nextId());
        mchNotify.setOrderNo(mchTradeDetail.getWalletOrderNo());
        mchNotify.setType(MchNotifyStatus.PREORDER_NOTICE.value());
        mchNotify.setCount(0);
        mchNotify.setStatus(EnableType.ENABLE.value());
        mchNotify.setCallbackUrl("");
        notifyMapper.insertSelective(mchNotify);
        mapper.insertSelective(mchTradeDetail);
        if(StringUtil.listIsNotBlank(mchTradeDetail.getTokenList())) { //关联写入多币种支付关联表
        	List<MchPayToken> tokenList=mchTradeDetail.getTokenList();
        	for (MchPayToken mchPayToken : tokenList) {
        		mchPayToken.setDetailId(mchTradeDetail.getId());
			}
        	mchPayTokenMapper.insertList(tokenList);
        }
        return mchNotify.getNotifyId();
    }


    //支付订单
    @Transactional(rollbackFor = Exception.class)
    public MchNotify payOrder(String prepayId,Long userId) throws  Exception{

        //查询标示表
        MchNotify param = new MchNotify();
        param.setNotifyId(Long.valueOf(prepayId));
        MchNotify notice =  notifyMapper.selectOne(param);
        if(notice == null || notice.getType().intValue() != MchNotifyStatus.PREORDER_NOTICE.value().intValue() || StringUtils.isBlank(notice.getOrderNo())){
            throw new MchException(MchResponseCode.MERCHANT_PREID_ERROR.name());
        }
        MchTradeDetail mchparam = new MchTradeDetail();
        mchparam.setWalletOrderNo(notice.getOrderNo());
        MchTradeDetail mchTradeDetail = mapper.selectForUpdate(mchparam);
        //预支付标识错误
        if(mchTradeDetail == null){
            throw new MchException(MchResponseCode.MERCHANT_PREID_ERROR.name());
        }
//        //判断商户号是否一致
//        if(!mchTradeDetail.getMchNo().toString().equals(mchId)){
//            return new ObjectRestResponse().status(ResponseCode.MERCHANT_PREID_ERROR);
//        }
        //判断过期时间和订单状态
        if(LocalDateTime.now().isAfter(LocalDateUtil.date2LocalDateTime(mchTradeDetail.getExpireTime()))
                || mchTradeDetail.getStatus().intValue() != MchOrderStatus.UNPAY.value().intValue()){
            throw new BusinessException(ResponseCode.TRANSFER_DEALED.name());
        }

        //扣除手续费后  商户账户实际到账数量
//        BigDecimal amount = mchTradeDetail.getAmount().subtract(mchTradeDetail.getChargeAmount()); //多币种支付时先暂时不处理手续费问题

        //商户结算的实际延迟时间
        Integer settleTime  = merchantBiz.getMchSettleTime(mchTradeDetail.getMchUserId());
        boolean ifSettleNow = settleTime <= 0 ? true : false;
        for (MchPayToken token : mchTradeDetail.getTokenList()) {
        	AccountAssertLogVo receiveVo = new AccountAssertLogVo();
            receiveVo.setUserId(userId);
            receiveVo.setSymbol(token.getSymbol());
            receiveVo.setAmount(token.getAmount());
            receiveVo.setChargeSymbol(mchTradeDetail.getChargeSymbol());
            receiveVo.setType(AccountLogType.PAY_MERCHANT);//支付
            receiveVo.setChargeAmount(mchTradeDetail.getChargeAmount());
            receiveVo.setTransNo(mchparam.getWalletOrderNo());//支付流水号
            receiveVo.setRemark(AccountLogType.PAY_MERCHANT.name());
            //消费用户扣减资产
            dcAssertAccountBiz.signUpdateAssert(receiveVo,AccountSignType.ACCOUNT_PAY_AVAILABLE);
          //增加商户待确认资产
            AccountAssertLogVo vo = new AccountAssertLogVo();
            vo.setUserId(mchTradeDetail.getMchUserId());
            vo.setSymbol(token.getSymbol());
            vo.setAmount(token.getAmount());
            vo.setChargeSymbol(mchTradeDetail.getChargeSymbol());
            vo.setType( ifSettleNow ? AccountLogType.MERCHANT_SETTLE : AccountLogType.MERCHANT_ADD_ASSERT);
            //目前实时结算
            vo.setTransNo(mchparam.getWalletOrderNo());//支付流水号
            vo.setRemark(ifSettleNow ? AccountLogType.MERCHANT_SETTLE.name() : AccountLogType.MERCHANT_ADD_ASSERT.name());
            dcAssertAccountBiz.signUpdateAssert(vo,ifSettleNow ? AccountSignType.ACCOUNT_RECHARGE : AccountSignType.MERCHANT_ADD_ASSERT );
		}



        MchTradeDetail updateParam = new MchTradeDetail();
        updateParam.setId(mchTradeDetail.getId());
        updateParam.setStatus(MchOrderStatus.PAY_SUCCESS.value());//设置状态为成功
        updateParam.setUserId(userId);
        updateParam.setUpdateTime(new Date());
        if( ifSettleNow ){
          updateParam.setSettleStatus(MchOrderSettleStatus.SETTLE.value());
          updateParam.setSettleTime(updateParam.getUpdateTime());
        } else {
          updateParam.setSettleStatus(MchOrderSettleStatus.UNSETTLE.value());
          //设置实际应该结算的时间
          updateParam.setSettleTime(LocalDateUtil.localDate2Date(LocalDateUtil.date2LocalDate(updateParam.getUpdateTime()).plusDays(settleTime)));
        }

        mapper.updateByPrimaryKeySelective(updateParam);

        Merchant merchant = merchantMapper.selectByPrimaryKey(mchTradeDetail.getMchId());
        MchNotify mchNotify = null;
        if (merchant != null) { // 推送给商户到账成功通知
            PayNotifyVo payVo = new PayNotifyVo();
//            payVo.setAmount(mchTradeDetail.getAmount().stripTrailingZeros().toPlainString());//支付数量
//            payVo.setSymbol(mchTradeDetail.getSymbol());
            List<MchPayTokenModel> modelList= new ArrayList<MchPayTokenModel>();
        	for (MchPayToken mchPayToken : mchTradeDetail.getTokenList()) {
        		MchPayTokenModel payModel = new MchPayTokenModel();
        		payModel.setAmount(mchPayToken.getAmount());
        		payModel.setSymbol(mchPayToken.getSymbol());
        		modelList.add(payModel);
    		}
            payVo.setTokenList(modelList);
            payVo.setMchNo(String.valueOf(merchant.getMchNo()));//商户号
            payVo.setNotifyId(String.valueOf(IdGenerator.nextId()));
            payVo.setTime(mchTradeDetail.getUpdateTime());
            payVo.setTransNo(mchTradeDetail.getWalletOrderNo());//流水号

            payVo.setNonceStr(mchTradeDetail.getNonceStr());
            payVo.setMchOrderNo(mchTradeDetail.getMchOrderNo());
            payVo.setTradeType(mchTradeDetail.getTradeType());
            payVo.setChargeAmount(mchTradeDetail.getChargeAmount());
            String sign=SignUtil.sign(payVo, merchant.getSecretKey());
            payVo.setSign(sign);



            mchNotify = new MchNotify();
            mchNotify.setType(MchNotifyStatus.PAY_SUCCESS_NOTICE.value());
            mchNotify.setMchId(merchant.getId());
            mchNotify.setNotifyId(Long.parseLong(payVo.getNotifyId()));
            mchNotify.setOrderNo(payVo.getTransNo());
            mchNotify.setCreateTime(new Date());
            mchNotify.setCount(0);
            mchNotify.setCallbackUrl(mchTradeDetail.getNotifyUrl());
            mchNotify.setNotifyStr(JSON.toJSONString(payVo));//签名字符串
            notifyMapper.insertSelective(mchNotify);
        }

        return mchNotify;

    }


	public MchTradeDetail selectByOrderNo(MchTradeDetail mchparam) {
		return mapper.selectByOrderNo(mchparam);
	}



}