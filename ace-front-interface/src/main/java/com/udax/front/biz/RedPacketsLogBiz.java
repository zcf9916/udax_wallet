package com.udax.front.biz;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.BasicSymbol;
import com.github.wxiaoqi.security.common.entity.front.RedPacketLog;
import com.github.wxiaoqi.security.common.entity.front.RedPacketSend;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.mapper.front.RedPacketLogMapper;
import com.github.wxiaoqi.security.common.mapper.front.RedPacketSendMapper;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import com.udax.front.util.CacheBizUtil;
import com.udax.front.vo.rspvo.RedPacketQueryRspVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Random;

/**
 * 
 *
 * @author Mr.AG
 * @email 463540703@qq.com
 * @date 2017-12-26 19:43:46
 */
@Service
@Slf4j
public class RedPacketsLogBiz extends BaseBiz<RedPacketLogMapper,RedPacketLog> {

    @Autowired
    private RedPacketSendMapper sendMapper;

    @Autowired
    private DcAssertAccountBiz assertAccountBiz;

    @Autowired
    private CacheBiz cacheBiz;

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public ObjectRestResponse openRedPacket(String orderNo) throws Exception {
         RedPacketSend param = new RedPacketSend();
         param.setOrderNo(orderNo);
         param.setStatus(RedPacketOrderStatus.INIT.value());
         int count = 0;
         while (true){
             count ++;
             logger.info("拆红包,订单号" + orderNo + "用户ID:" + BaseContextHandler.getUserID() + "第" + count + "次循环");
             RedPacketSend send = sendMapper.selectOne(param);
             if( send == null ||  send.getStatus() != RedPacketOrderStatus.INIT.value() || send.getCurrentNum() == send.getNum()){
                 return new ObjectRestResponse(false);
             }
             //currentNum 相当于乐观锁
             Example example = new Example(RedPacketSend.class);
             example.createCriteria().andEqualTo("id", send.getId())
                     .andEqualTo("currentNum",send.getCurrentNum())
                     .andEqualTo("status",RedPacketOrderStatus.INIT.value());
             BigDecimal money = send.getType() == RedPacketType.COMMON.value() ?
                     send.getTotalAmount().divide(new BigDecimal(send.getNum()),8,RoundingMode.HALF_UP) : getRandomMoney(send);
             RedPacketSend updateParam = new RedPacketSend();
             updateParam.setCurrentNum(send.getCurrentNum() + 1);
             updateParam.setCurrentAmout(send.getCurrentAmout().add(money));
             if(updateParam.getCurrentNum() == send.getNum()){
                 updateParam.setStatus(RedPacketOrderStatus.ALLDONE.value());//已抢完
             }
             int result = sendMapper.updateByExampleSelective(updateParam,example);
             if( result > 0){
                 RedPacketLog log = new RedPacketLog();
                 log.setAmount(money);
                 log.setCreateTime(new Date());
                 log.setOrderNo(send.getOrderNo());
                 log.setReceiveUserId(BaseContextHandler.getUserID());
                 log.setSymbol(send.getSymbol());
                 log.setTotalAmount(send.getTotalAmount());
                 log.setType(send.getType());
                 log.setStatus(RedPacketLogOrderStatus.SETTLE.value());
                 log.setUserId(send.getUserId());
                 //插入抢红包记录
                 mapper.insert(log);

                 //扣减可用余额
                 AccountAssertLogVo payVo = new AccountAssertLogVo();
                 payVo.setUserId(log.getReceiveUserId());
                 payVo.setSymbol(send.getSymbol());
                 payVo.setAmount(money);
                 payVo.setChargeSymbol(send.getSymbol());
                 payVo.setChargeAmount(BigDecimal.ZERO);
                 payVo.setType(AccountLogType.SNATCH_REDPACKETS);
                 payVo.setTransNo(send.getOrderNo());//转账流水号
                 payVo.setRemark(AccountLogType.SNATCH_REDPACKETS.name());
                 assertAccountBiz.signUpdateAssert(payVo,AccountSignType.ACCOUNT_RECHARGE);//扣减可用

                 send.setCurrentAmout(updateParam.getCurrentAmout());
                 send.setCurrentNum(updateParam.getCurrentNum());
                 if(updateParam.getCurrentNum() == send.getNum()){
                     send.setStatus(RedPacketOrderStatus.ALLDONE.value());//已抢完
                 }
                 //更新缓存
                 RedPacketQueryRspVo.RedPacketLogRspVO logVo = new RedPacketQueryRspVo.RedPacketLogRspVO();
                 BeanUtils.copyProperties(log,logVo);
                 logVo.setUserName(BaseContextHandler.getUsername());
                 CacheUtil.getCache().set(Constants.REDPACKETS.RPORDER + send.getOrderNo(),send);
                 CacheUtil.getCache().leftPush(Constants.REDPACKETS.RPLOG + send.getOrderNo(),logVo);//新增红包记录
                 CacheUtil.getCache().leftPush(Constants.REDPACKETS.RP_USERID + send.getOrderNo(),log.getReceiveUserId());//新增红包列表用户id
                 return new ObjectRestResponse().data(log);
             }
         }


         // return new ;
    }
    private  BigDecimal getRandomMoney(RedPacketSend send) {
        //根据位数随机红包
        BasicSymbol symbol = CacheBizUtil.getBasicSymbol(send.getSymbol(),cacheBiz);
        Integer  decimalPlaces = symbol.getDecimalPlaces();
        if(decimalPlaces == null || decimalPlaces.intValue() < 1 || decimalPlaces.intValue() > 8){
            decimalPlaces = 8;
        }
        // remainSize 剩余的红包数量
        // remainMoney 剩余的钱
        BigDecimal remainMoney = send.getTotalAmount().subtract(send.getCurrentAmout());
        Integer remainPackage = send.getNum() - send.getCurrentNum();
        if (remainPackage == 1) {
            return remainMoney.setScale(decimalPlaces,RoundingMode.HALF_DOWN);
        }
        Random r = new Random();
        double min = Math.pow(10,-decimalPlaces); //
        double max = remainMoney.doubleValue() / remainPackage * 2;
        if (max > 6) {
            max = 6;
        }
        double money = r.nextDouble() * max;
        money = money <= min ? min : money;
        return BigDecimal.valueOf(money).setScale(decimalPlaces,RoundingMode.HALF_DOWN);
    }


}