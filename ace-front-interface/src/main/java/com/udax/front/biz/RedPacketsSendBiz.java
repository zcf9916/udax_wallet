package com.udax.front.biz;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.front.RedPacketLog;
import com.github.wxiaoqi.security.common.entity.front.RedPacketSend;
import com.github.wxiaoqi.security.common.entity.ud.HPurchaseLevel;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.mapper.front.RedPacketLogMapper;
import com.github.wxiaoqi.security.common.mapper.front.RedPacketSendMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import com.udax.front.vo.reqvo.SendRedPacketsModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

/**
 * 
 *
 * @author Mr.AG
 * @email 463540703@qq.com
 * @date 2017-12-26 19:43:46
 */
@Service
@Slf4j
public class RedPacketsSendBiz extends BaseBiz<RedPacketSendMapper,RedPacketSend> {

    @Autowired
    private DcAssertAccountBiz dcAssertAccountBiz;

    @Autowired
    private FrontUserBiz frontUserBiz;

    //红包到期返回
    @Transactional(rollbackFor = Exception.class)
    public void  refund(RedPacketSend send) {
        try{
            BigDecimal remain = send.getTotalAmount().subtract(send.getCurrentAmout());
            //退还红包
            AccountAssertLogVo payVo = new AccountAssertLogVo();
            payVo.setUserId(send.getUserId());
            payVo.setSymbol(send.getSymbol());
            payVo.setAmount(remain);
            payVo.setChargeSymbol(send.getSymbol());
            payVo.setChargeAmount(BigDecimal.ZERO);
            payVo.setType(AccountLogType.REDPACKETS_RETURN);
            payVo.setTransNo(send.getOrderNo());//转账流水号
            payVo.setRemark(AccountLogType.REDPACKETS_RETURN.name());
            dcAssertAccountBiz.signUpdateAssert(payVo,AccountSignType.ACCOUNT_RECHARGE);//扣减可用

            //更新状态
            RedPacketSend updateParam = new RedPacketSend();
            updateParam.setId(send.getId());
            updateParam.setStatus(RedPacketOrderStatus.RETURN.value());
            updateParam.setReturnAmount(remain);
            mapper.updateByPrimaryKeySelective(updateParam);

            send.setStatus(RedPacketOrderStatus.RETURN.value());
            send.setReturnAmount(remain);
            CacheUtil.getCache().set(Constants.REDPACKETS.RPORDER + send.getOrderNo(),send);

        }catch (Exception e){
            logger.error("红包退款失败:订单号:" + send.getOrderNo(),e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

    }


    @Transactional(rollbackFor = Exception.class)
    public  RedPacketSend  sendRedPackets(SendRedPacketsModel model) throws Exception{
        Integer sendType = model.getSendType();//发送类型  0 个人红包   1 群红包
        Integer type = model.getType();  //  0. 普通红包  1. 随机红包
        //需要的余额
        BigDecimal totalAmount = BigDecimal.ZERO;
        RedPacketSend send = new RedPacketSend();
        send.setCreateTime(new Date());
        send.setOrderNo(String.valueOf(IdGenerator.nextId()));
        send.setSendType(model.getSendType());
        send.setType(model.getType());
        send.setSymbol(model.getSymbol());
        send.setRemark(model.getRemark());
        send.setUserId(BaseContextHandler.getUserID());
        //单人红包不能发随机红包
        if(sendType.intValue() == RedPacketSendType.SINGLE.value()){
            totalAmount = model.getAmount();
            send.setType(RedPacketType.COMMON.value());
            send.setNum(1);
            send.setCurrentNum(0);
            send.setReceiveUserId(model.getReceiveUserId());
        } else if(sendType.intValue() == RedPacketSendType.MULTI.value()){
            send.setNum(model.getNumber());
            send.setCurrentNum(0);
            send.setGroupId(model.getGroupID());
            //如果是普通红包,总金额等于 个数 * 金额
            if(type == RedPacketType.COMMON.value()){
                totalAmount = model.getAmount().multiply(new BigDecimal(model.getNumber()));
            } else if( type == RedPacketType.RANDOM.value()){
                totalAmount = model.getAmount();
            }
        }
        send.setTotalAmount(totalAmount);

        //扣减可用余额
        AccountAssertLogVo payVo = new AccountAssertLogVo();
        payVo.setUserId(send.getUserId());
        payVo.setSymbol(send.getSymbol());
        payVo.setAmount(send.getTotalAmount());
        payVo.setChargeSymbol(send.getSymbol());
        payVo.setChargeAmount(BigDecimal.ZERO);
        payVo.setType(AccountLogType.SEND_REDPACKETS);
        payVo.setTransNo(send.getOrderNo());//转账流水号
        payVo.setRemark(AccountLogType.SEND_REDPACKETS.name());
        dcAssertAccountBiz.signUpdateAssert(payVo,AccountSignType.ACCOUNT_PAY_AVAILABLE);//扣减可用

        //插入数据,订单号和剩余可以抢的数量

        //CacheUtil.getCache().set(Constants.REDPACKETS.RPLOG + send.getOrderNo(),new ArrayList<>());
        mapper.insertSelective(send);
        CacheUtil.getCache().set(Constants.REDPACKETS.RPORDER + send.getOrderNo(),send);
        return send;

    }

}