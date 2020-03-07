package com.udax.front.biz.ud;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.ud.*;
import com.github.wxiaoqi.security.common.enums.EmailTemplateType;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.enums.SendMsgType;
import com.github.wxiaoqi.security.common.enums.ud.QueueStatus;
import com.github.wxiaoqi.security.common.enums.ud.SettledProfitStatus;
import com.github.wxiaoqi.security.common.enums.ud.UDOrderDetailStatus;
import com.github.wxiaoqi.security.common.mapper.front.FrontUserMapper;
import com.github.wxiaoqi.security.common.mapper.ud.*;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.LocalDateUtil;
import com.github.wxiaoqi.security.common.util.SendUtil;
import com.udax.front.service.ServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

/**
 * 
 *
 * @author Mr.AG
 * @email 463540703@qq.com
 * @date 2017-12-26 19:43:46
 */
@Service
@Slf4j
public class HOrderDetailBiz extends BaseBiz<HOrderDetailMapper, HOrderDetail> {


    @Autowired
    private HDailyProfitDetailMapper dailyProfitDetailMapper;

    @Autowired
    private HSettledProfitMapper settledProfitMapper;

    @Autowired
    private HUserInfoMapper userInfoMapper;

    @Autowired
    private HQueueMapper hQueueMapper;


    @Autowired
    private HQueueBiz hQueueBiz;

    @Autowired
    private HPurchaseLevelBiz purchaseLevelBiz;

    @Autowired
    private FrontUserMapper frontUserMapper;

    @Autowired
    private HParamBiz paramBiz;

//
//    public void calcDailyProfit(){
//
//
//        //查询是否有未到期的订单
//        Example example = new Example(HOrderDetail.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqua3lTo("ifExpire", EnableType.DISABLE.value());//未到期订单
//        example.setOrderByClause(" id");//正序查询
//        int total =   mapper.selectCountByExample(example);//计算总量
//        int totalPageCount  =  new BigDecimal(total).divide( new BigDecimal(BATCHUPDATE_LIMIT),BigDecimal.ROUND_CEILING).intValue();//需要分页的数量
//
//        int beginCount = 1;
//        Long lastId = 0L;//上一批次的最后一个Id,下次分页查询要从上一次的最后一个开始查询
//        while( beginCount <= totalPageCount){
//            if(lastId >  0){
//                criteria.andGreaterThan("id",lastId);
//            }
//            Page<Object> result = PageHelper.startPage(0, BATCHUPDATE_LIMIT);
//            List<HOrderDetail> orderDetailList = mapper.selectByExample(example);
//            //这个批次的最大Id
//            if(StringUtil.listIsNotBlank(orderDetailList)){
//                orderDetailList.stream().forEach((l) ->{
//                    try{
//                        settleOrderDetail(l);
//                     }catch (Exception e){
//                        logger.error("计算订单每日收益以及生成分成数据时候出错,订单号:" + l.getOrderNo(),e);
//                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                    }
//                });
//                lastId = orderDetailList.get(orderDetailList.size() - 1).getId();
//            }
//            beginCount ++;
//        }
//    }
    @Transactional(rollbackFor = Exception.class)
    public HUserInfo settleOrderDetail(HOrderDetail orderDetail){
        try{
            logger.info("开始计算订单每日收益以及生成分成数据,订单号:" + orderDetail.getOrderNo());
            //新增每日更新的利润
            if(orderDetail.getCurrentSettleDay() >= orderDetail.getSettleDay()){
                return null;
            }
            //更新订单信息
            HOrderDetail updateParam =  new HOrderDetail();
            updateParam.setId(orderDetail.getId());
            updateParam.setCurrentSettleDay(orderDetail.getCurrentSettleDay() + 1);
            if(updateParam.getCurrentSettleDay().intValue() == orderDetail.getSettleDay().intValue()){
                logger.info("最后一天修改订单为到期状态,订单号:" + orderDetail.getOrderNo());
                updateParam.setStatus(UDOrderDetailStatus.COLLECT.value());//订单状态置为到期
            }
            //更新订单中已经结算的时间  以及是否到期的状态
            mapper.updateByPrimaryKeySelective(updateParam);
            //计算每日收益
            BigDecimal profit = orderDetail.getLockAmount().multiply(orderDetail.getInterest()).setScale(8,BigDecimal.ROUND_DOWN);

            boolean realTimeSettle = getRealTimeSettle(orderDetail.getUserId());
            addDailyData(orderDetail,updateParam.getCurrentSettleDay(),profit);
            updateSettleProfit(orderDetail,updateParam.getCurrentSettleDay(), profit,realTimeSettle);

            //如果倒数第二天的时候  用户没有继续打钱进来排队,通知用户,并且非实时结算的才需要通知用户
            if(updateParam.getCurrentSettleDay().intValue() == orderDetail.getSettleDay().intValue() - 1 && !realTimeSettle){
                notifyUser(orderDetail);
            }
            //如果最后一天的时候  用户没有继续打钱进来排队,锁定用户
            if(updateParam.getCurrentSettleDay().intValue() == orderDetail.getSettleDay().intValue()){
                HUserInfo userInfo = lockUser(orderDetail,realTimeSettle);
                return userInfo;
            }
        }catch (Exception e){
            logger.error("计算订单每日收益以及生成分成数据时候出错,订单号:" + orderDetail.getOrderNo(),e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return null;
    }

    private boolean getRealTimeSettle(Long userId){
        HUserInfo userInfoParam = new HUserInfo();
        userInfoParam.setUserId(userId);
        HUserInfo hUserInfo =  userInfoMapper.selectOne(userInfoParam);
        HParam realTimeSettle = ServiceUtil.getUdParamByKey("REALTIME_SETTLE",paramBiz,hUserInfo.getExchangeId());
        if(realTimeSettle != null && realTimeSettle.getUdValue().equals("true")){
            return true;
        }
        return false;
    }


    //新增每日收益数据
    private void addDailyData(HOrderDetail orderDetail,Integer currentSettleDay,BigDecimal profit){
        //新增每日更新的利润
        HDailyProfitDetail hDailyProfitDetail = new HDailyProfitDetail();
        BeanUtils.copyProperties(orderDetail,hDailyProfitDetail);
        hDailyProfitDetail.setId(null);
        hDailyProfitDetail.setCreateTime(new Date());
        //计算日息
        hDailyProfitDetail.setProfit(profit);
        //实际结算的订单对应哪一天的数据
        LocalDate localDate =  LocalDateUtil.date2LocalDate(orderDetail.getCreateTime()).plusDays(currentSettleDay);
        hDailyProfitDetail.setSettleTime(LocalDateUtil.localDate2Date(localDate));
        dailyProfitDetailMapper.insertSelective(hDailyProfitDetail);
    }


    //更新待结算利润表
    private void updateSettleProfit(HOrderDetail orderDetail,Integer currentSettleDay,BigDecimal profit, boolean realTimeSettle){
        //更新或插入待分配利润表
        HSettledProfit selectParam = new HSettledProfit();
        selectParam.setOrderNo(orderDetail.getOrderNo());
        HSettledProfit settledProfit = settledProfitMapper.selectOne(selectParam);
        if(settledProfit == null){
            logger.error("待结算利润表数据为空,订单号:" + orderDetail.getOrderNo());
            return;
        }
        //汇总数据到待结算表中
        HSettledProfit updateParams = new HSettledProfit();
        updateParams.setId(settledProfit.getId());
        updateParams.setFreezeProfit(profit.multiply(new BigDecimal(currentSettleDay)));
        if(currentSettleDay == orderDetail.getSettleDay().intValue()){
            updateParams.setStatus(SettledProfitStatus.WAIT_SETTLE.value());
            //是否配置实时返还利润
            if(realTimeSettle){
                updateParams.setIfQueueNextOrder(EnableType.ENABLE.value());//设置成下一轮的钱已经进来了,可以被结算定时任务扫描到
            }
        }
        settledProfitMapper.updateByPrimaryKeySelective(updateParams);

        //更新订单的收益
        HOrderDetail updateOrderDetail = new HOrderDetail();
        updateOrderDetail.setId(orderDetail.getId());
        updateOrderDetail.setProfit(updateParams.getFreezeProfit());
        mapper.updateByPrimaryKeySelective(updateOrderDetail);
    }

    //通知用户
    private void notifyUser(HOrderDetail orderDetail) {
        HQueue queryQueue = new HQueue();
        queryQueue.setStatus(QueueStatus.WAIT_MATCH.value());//等待匹配的状态
        queryQueue.setUserId(orderDetail.getUserId());//用户id
        HQueue queue = hQueueMapper.selectOne(queryQueue);
        //如果不存在已经参与排队的,通知用户
        if(queue == null){
            Map<String, Object> param = InstanceUtil.newHashMap("userId", orderDetail.getUserId());
            FrontUser user = frontUserMapper.selectUnionUserInfo(param);
            //发送短信或邮件通知  让用户充钱
            SendUtil.sendSmsOrEmail(SendMsgType.UD_COMMUNITY_CHARGE_NOTICE.value(), EmailTemplateType.UD_COMMUNITY_CHARGE_NOTICE.value(), user, null, user.getUserName(), orderDetail.getLevelName());
        }
    }
    //锁定用户
    private HUserInfo lockUser(HOrderDetail orderDetail,boolean realTimeSettle) throws Exception{
        HQueue queryQueue = new HQueue();
        queryQueue.setStatus(QueueStatus.WAIT_MATCH.value());//等待匹配的状态
        queryQueue.setUserId(orderDetail.getUserId());//用户id
        HQueue queue = hQueueMapper.selectOne(queryQueue);
        //如果存在已经参与排队的,不执行
        if(queue != null) {
            return null;
        }
        HUserInfo userInfoParam = new HUserInfo();
        userInfoParam.setUserId(orderDetail.getUserId());
        HUserInfo hUserInfo =  userInfoMapper.selectOne(userInfoParam);
        if(hUserInfo == null) {
                return null;
        }
        //不是实时结算的不需要锁定用户
        if(!realTimeSettle){
            //先锁定用户
            HUserInfo updateUserParam = new HUserInfo();
            updateUserParam.setId(hUserInfo.getId());
            updateUserParam.setStatus(EnableType.DISABLE.value());
            userInfoMapper.updateByPrimaryKeySelective(updateUserParam);
        }

        //如果开启了自动复投,返回用户信息
        if(hUserInfo.getAutoRepeat().equals(EnableType.ENABLE.value())){
            return hUserInfo;
        }
        return null;

    }
    @Transactional(rollbackFor = Exception.class)
    public void autoRepeat( HUserInfo hUserInfo,HOrderDetail orderDetail){
        try{
            //选择最接近的方案


            HPurchaseLevel levelParam = new HPurchaseLevel();
            levelParam.setLevel(hUserInfo.getUserLevel());
            levelParam.setExchId(hUserInfo.getExchangeId());
            HPurchaseLevel purchaseLevel = purchaseLevelBiz.selectOne(levelParam);
            hQueueBiz.queueForJob(purchaseLevel,hUserInfo.getUserId());

        }catch (Exception e){
            logger.error("用户自动复投失败,订单号:" + orderDetail.getOrderNo(),e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

}