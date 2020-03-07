package com.udax.front.biz.ud;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.ud.*;
import com.github.wxiaoqi.security.common.enums.AccountLogType;
import com.github.wxiaoqi.security.common.enums.AccountSignType;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.enums.ud.QueueStatus;
import com.github.wxiaoqi.security.common.enums.ud.SettledProfitStatus;
import com.github.wxiaoqi.security.common.enums.ud.UDOrderDetailStatus;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.mapper.ud.*;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.LocalDateUtil;
import com.github.wxiaoqi.security.common.util.Query;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.udax.front.biz.DcAssertAccountBiz;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import com.udax.front.vo.rspvo.ud.UDQueueListRspVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 *
 * @author Mr.AG
 * @email 463540703@qq.com
 * @date 2017-12-26 19:43:46
 */
@Service
@Slf4j
public class HQueueBiz extends BaseBiz<HQueueMapper, HQueue> {

    @Autowired
    private HPurchaseLevelBiz purchaseLevelBiz;


    @Autowired
    private DcAssertAccountBiz dcAssertAccountBiz;

    @Autowired
    private HUserInfoMapper hUserInfoMapper;

    @Autowired
    private HSettledProfitMapper settledProfitMapper;

    @Autowired
    private HOrderDetailMapper detailMapper;

    @Autowired
    private HCommissionRelationMapper relationMapper;


    public TableResultResponse selectQueueList(Query query){


        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<HQueue> list =  mapper.selectQueueList(query);
        List<UDQueueListRspVo> rspVoList  = InstanceUtil.newArrayList();
        list.stream().forEach((l) -> {
            UDQueueListRspVo rspVo = new UDQueueListRspVo();
            BeanUtils.copyProperties(l,rspVo);
            if(l.getOrderDetail() != null && l.getOrderDetail().getSettleDay() != null){
                HOrderDetail orderDetail = l.getOrderDetail();
                //如果还是结算中
                if(orderDetail.getStatus().equals(UDOrderDetailStatus.INIT.value())){
                    rspVo.setStatus(2);
                }else if(orderDetail.getStatus().equals(UDOrderDetailStatus.COLLECT.value())){
                    rspVo.setStatus(3);
                }else if(orderDetail.getStatus().equals(UDOrderDetailStatus.FINISH.value())){
                    rspVo.setStatus(4);
                }
                rspVo.setProfit(orderDetail.getProfit());
                rspVo.setCharge(orderDetail.getCharge());
                //rspVo.setProfit(orderDetail.getLockAmount().multiply(orderDetail.getInterest()).multiply(new BigDecimal(orderDetail.getCurrentSettleDay())).setScale(8,BigDecimal.ROUND_DOWN));
            }
            rspVoList.add(rspVo);
        });

        TableResultResponse resultResponse = new TableResultResponse(result.getTotal(),rspVoList);

        return  resultResponse;
    }



    //定时任务查询符合进入队列的用户




    private void matchQueue(List<HQueue> queueList,HPurchaseLevel level){
        for(HQueue queue : queueList){
            //查询是否有运行中的订单
            Example example = new Example(HQueue.class);
            example.createCriteria().andEqualTo("userId",queue.getUserId())
                    .andEqualTo("status",UDOrderDetailStatus.INIT.value());
            Integer count = detailMapper.selectCountByExample(example);
            if(count > 0){
                logger.error("队列订单号:" + queue.getOrderNo() + ",在匹配订单中已经存在一个运行中的订单,等待下次匹配");
                continue;
            }
            HQueue updateQueue = new HQueue();
            updateQueue.setId(queue.getId());
            updateQueue.setStatus(QueueStatus.MATCH.value());//状态置为已脱离队列,进入匹配队列
            mapper.updateByPrimaryKeySelective(updateQueue);
            //往运行队列中插入数据
            HOrderDetail hOrderDetail = new HOrderDetail();
            BeanUtils.copyProperties(queue,hOrderDetail);
            hOrderDetail.setPurchaseTime(queue.getCreateTime());//申购时间
            hOrderDetail.setInterest(level.getInterest());//日息
            hOrderDetail.setCreateTime(new Date());//匹配时间
            hOrderDetail.setStatus(UDOrderDetailStatus.INIT.value());//设置为待结算
            hOrderDetail.setSettleDay(level.getRunTime());//运行时间
            hOrderDetail.setCurrentSettleDay(0);//当前运行天数
            detailMapper.insertSelective(hOrderDetail);
            //新增待结算利润表数据
            HSettledProfit insertParam = new HSettledProfit();
            insertParam.setFreezeAmount(hOrderDetail.getLockAmount());
            insertParam.setFreezeProfit(BigDecimal.ZERO);
            insertParam.setOrderNo(hOrderDetail.getOrderNo());
            insertParam.setSymbol(hOrderDetail.getSymbol());
            insertParam.setUserId(hOrderDetail.getUserId());
            insertParam.setUpdateTime(new Date());
            insertParam.setLevelName(hOrderDetail.getLevelName());
            insertParam.setLevelId(hOrderDetail.getLevelId());
            settledProfitMapper.insertSelective(insertParam);
        }
    }



    @Transactional(rollbackFor = Exception.class)
    public void timeMatchQueue(){
        //查询申购方案
        List<HPurchaseLevel> levelList = purchaseLevelBiz.selectListAll();
        if(StringUtil.listIsBlank(levelList)){
            logger.error("执行UD排队队列状态检查,有效的用户进入有效队列订单定时任务,方案数据为空");
            return;
        }

        levelList.stream().forEach((l) ->{
            //查询是否达到最长等待时间
            int maxWaitTime = l.getWaitTime();//最大排队等待时间
            LocalDateTime limitTime =LocalDateTime.now().minusDays(maxWaitTime);//在这个时间点之前的都可以排队了
            Example example  = new Example(HQueue.class);
            example.createCriteria().andEqualTo("levelId", l.getId())
                    .andEqualTo("status", EnableType.ENABLE.value())
                    .andLessThan("createTime", LocalDateUtil.localDateTime2Date(limitTime));
            List<HQueue> matchQueueList = mapper.selectByExample(example);//所有达到时间限制的都查出来
            if(StringUtil.listIsNotBlank(matchQueueList)){
                logger.info("方案" + l.getName() + ",达到时间限制的时间的用户人数:" + matchQueueList.size() == null ? 0 : matchQueueList.size());
                matchQueue(matchQueueList,l);
            }


        });
    }



    @Transactional(rollbackFor = Exception.class)
    public void numMatchQueue(){
        //查询申购方案
        List<HPurchaseLevel> levelList = purchaseLevelBiz.selectListAll();
        if(StringUtil.listIsBlank(levelList)){
            logger.error("执行UD排队队列状态检查,达到有效时间的用户进入有效队列订单定时任务,方案数据为空");
            return;
        }

        levelList.stream().forEach((l) ->{
            Long days = LocalDate.now().toEpochDay() - LocalDateUtil.date2LocalDate(l.getEarliestStartTime()).toEpochDay();
            //是否能开始新的一轮
            if (days >= 0) {
                //查询是否达到匹配人数了
                Example example = new Example(HQueue.class);
                example.createCriteria().andEqualTo("levelId", l.getId())
                        .andEqualTo("status", EnableType.ENABLE.value());
                int num = mapper.selectCountByExample(example);//在队列中的位置
                logger.info("方案" + l.getName() + ",最低要求人数" + l.getTriggerNum() + "当前排队人数:" + num);
                if (num >= l.getTriggerNum()) {
                    Map<String, Object> paramMap = InstanceUtil.newHashMap();
                    paramMap.put("levelId", l.getId());
                    paramMap.put("status", EnableType.ENABLE.value());
                    paramMap.put("limit", l.getMatchNum());
                    List<HQueue> matchQueueList = mapper.getMatchList(paramMap);
                    matchQueue(matchQueueList,l);

                    //更新下下一轮的最早开始时间
                    Date newTime = LocalDateUtil.localDate2Date(LocalDate.now().plusDays(l.getRunTime()));
                    HPurchaseLevel updateLevel = new HPurchaseLevel();
                    updateLevel.setId(l.getId());
                    updateLevel.setEarliestStartTime(newTime);
                    purchaseLevelBiz.updateSelectiveByIdWithoutSetParam(updateLevel);
                }
            }
        });

    }

    //排队
    @Transactional(rollbackFor = Exception.class)
    public void queue(String id) throws Exception{
        Long userId = BaseContextHandler.getUserID();
        //查询申购方案
        HPurchaseLevel level = purchaseLevelBiz.selectById(id);
        if(level == null || userId == null){
           throw new BusinessException(ResponseCode.PARAM_ERROR.name());
        }
        if(!EnableType.ENABLE.value().equals(level.getIsOpen())){
            throw new BusinessException(ResponseCode.PLAN_CLOSE.name());
        }
        //查询用户信息
        HUserInfo queryParam = new HUserInfo();
        queryParam.setUserId(userId);
        HUserInfo hUserInfo =  hUserInfoMapper.selectOne(queryParam);
        //用户是否被锁定
        if(hUserInfo.getStatus().equals(EnableType.DISABLE.value())){
            throw new BusinessException(ResponseCode.LOCKED.name());
        }
        //实际投资额度是否达到
        if(hUserInfo.getTotalAmount().add(hUserInfo.getAddAmount()).compareTo(level.getAmountLimit()) < 0 ){
            throw new BusinessException(ResponseCode.INVESTMENT_LIMIT.name());
        }


        Map<Long,Long> idList = purchaseLevelBiz.getLevelListByCurrentLevel(hUserInfo).stream().collect(Collectors.toMap(HPurchaseLevel::getId, HPurchaseLevel::getId));;
        if(!idList.containsKey(Long.valueOf(id))){
            throw new BusinessException(ResponseCode.PLAN_NOT_MATCH.name());
        }

//        //选择能投资的方案列表
//        HPurchaseLevel proximalLevel = purchaseLevelBiz.getLevelListByCurrentLevel(hUserInfo);
//        if(proximalLevel.getId().longValue() != level.getId().longValue()){
//            throw new BusinessException(ResponseCode.PLAN_NOT_MATCH.name());
//        }


        //查询用户的所有关联上级节点
        HCommissionRelation queryRelation = new HCommissionRelation();
        queryRelation.setUserId(userId);
        List<HCommissionRelation> list = relationMapper.select(queryRelation);

        //如果用户不是有效用户,把状态置为有效用户,并循环变更上级用户的有效用户
        if(hUserInfo.getIsValid().intValue() == EnableType.DISABLE.value()){
            queryParam.setIsValid(EnableType.ENABLE.value());
            //更新上级用户的有效用户数量
            if(hUserInfo.getParentId().longValue() > 0){
                //所有上级用户Id
                List<Long> ids = list.stream() .map(HCommissionRelation::getReceiveUserId).collect(Collectors.toList());
                hUserInfoMapper.increTotalChild(ids);//有效用户+1
                hUserInfoMapper.increDirectChild(hUserInfo.getParentId());//直推用户+1

            }
        }
        //变更上级用户的下属节点总投资额
        if(hUserInfo.getParentId().longValue() > 0) {
            //本身节点和所有的父节点的子类投资总额增加
            //所有上级用户Id
            List<Long> ids = list.stream() .map(HCommissionRelation::getReceiveUserId).collect(Collectors.toList());
            Map<String, Object> map = InstanceUtil.newHashMap();
            map.put("list", ids);
            map.put("invest", level.getAmount());
            hUserInfoMapper.increChildInvest(map);
        }

        queryParam.setId(hUserInfo.getId());
        queryParam.setTotalAmount(hUserInfo.getTotalAmount().add(level.getAmount()));//更新实际投资金额
        queryParam.setUserLevel(level.getLevel());
        hUserInfoMapper.updateByPrimaryKeySelective(queryParam);





        //冻结资产
        freezeAssert(level.getAmount(),hUserInfo.getUserId(),level.getSymbol());
        enterQueue(level,hUserInfo.getUserId());

    }

    //定时任务  自动申购
    @Transactional(rollbackFor = Exception.class)
    public void queueForJob(HPurchaseLevel level,Long userId) throws Exception {
        //查询申购方案
        if(level == null || userId == null){
            return;
        }
        if(!EnableType.ENABLE.value().equals(level.getIsOpen())){
            return;
        }
        //查询用户信息
        HUserInfo queryParam = new HUserInfo();
        queryParam.setUserId(userId);
        HUserInfo hUserInfo =  hUserInfoMapper.selectOne(queryParam);

        //查询用户的所有关联上级节点
        HCommissionRelation queryRelation = new HCommissionRelation();
        queryRelation.setUserId(userId);
        List<HCommissionRelation> list = relationMapper.select(queryRelation);
        //变更上级用户的下属节点总投资额
        if(hUserInfo.getParentId() > 0) {
            //本身节点和所有的父节点的子类投资总额增加
            //所有上级用户Id
            List<Long> ids = list.stream() .map(HCommissionRelation::getReceiveUserId).collect(Collectors.toList());
            Map<String, Object> map = InstanceUtil.newHashMap();
            map.put("list", ids);
            map.put("invest", level.getAmount());
            hUserInfoMapper.increChildInvest(map);
        }

        queryParam.setId(hUserInfo.getId());
        queryParam.setTotalAmount(hUserInfo.getTotalAmount().add(level.getAmount()));//更新实际投资金额
        hUserInfoMapper.updateByPrimaryKeySelective(queryParam);




        //冻结资产
        freezeAssert(level.getAmount(),hUserInfo.getUserId(),level.getSymbol());
        enterQueue(level,hUserInfo.getUserId());


        //如果复投成功  解锁用户
        HUserInfo updateUserParam = new HUserInfo();
        updateUserParam.setId(hUserInfo.getId());
        updateUserParam.setStatus(EnableType.ENABLE.value());
        hUserInfoMapper.updateByPrimaryKeySelective(updateUserParam);

    }

    private void freezeAssert(BigDecimal amount,Long userId,String symbol) throws Exception{
        //冻结资产
        AccountAssertLogVo vo = new AccountAssertLogVo();
        vo.setUserId(userId);
        vo.setSymbol(symbol);
        vo.setAmount(amount);
        vo.setChargeSymbol(symbol);
        vo.setType(AccountLogType.UD_FREEZE);
        vo.setTransNo(String.valueOf(IdGenerator.nextId()));//提现流水号
        vo.setRemark(AccountLogType.UD_FREEZE.name());
        dcAssertAccountBiz.signUpdateAssert(vo, AccountSignType.ACCOUNT_WITHDRAW_FREEZE);

    }

    private void enterQueue( HPurchaseLevel level,Long userId){

        HQueue lastQueue = null;
        //倒序查询上一个匹配的订单
        //查询上一个订单的状态
        PageHelper.startPage(1, 1);
        Example example = new Example(HQueue.class);
        example.createCriteria().andEqualTo("userId",userId)
                .andNotEqualTo("status",QueueStatus.INVALID.value());
        //查询非失效的订单
        example.setOrderByClause("id desc");
        List<HQueue> orderDetail = mapper.selectByExample(example);
        if(StringUtil.listIsNotBlank(orderDetail)){
            //锁定上一个排队的队列数据
            lastQueue = mapper.selectForUpdate(orderDetail.get(0).getOrderNo());
        }

        //上一个订单还是启用的
        if(lastQueue != null && lastQueue.getStatus().intValue() == QueueStatus.WAIT_MATCH.value()){
            throw new BusinessException(ResponseCode.DUP_QUEUE.name());
        }

        //生成新的队列数据
        HQueue hQueue = new HQueue();
        hQueue.setOrderNo(String.valueOf(IdGenerator.nextId()));
        hQueue.setCreateTime(new Date());
        hQueue.setLastOrderNo(lastQueue == null ? "" : lastQueue.getOrderNo());
        hQueue.setLevelId(level.getId());
        hQueue.setLevelName(level.getName());
        hQueue.setLockAmount(level.getAmount());
        hQueue.setSymbol(level.getSymbol());
        hQueue.setUserId(userId);
        hQueue.setStatus(QueueStatus.WAIT_MATCH.value());//默认等待匹配


        if( lastQueue != null && StringUtils.isNotBlank(lastQueue.getOrderNo())){
            //更新上一个待结算数据表中是否有下一轮的排队数据为"是"
            HSettledProfit querySettled = new HSettledProfit();
            querySettled.setOrderNo(lastQueue.getOrderNo());
            HSettledProfit settledProfit = settledProfitMapper.selectOne(querySettled);
            if(settledProfit == null){
                logger.error("上一轮的待结算数据为空,订单号:" + lastQueue.getOrderNo());
                throw new BusinessException(ResponseCode.UNKNOW_ERROR.name());
            }
            logger.info("更新订单号:" + settledProfit.getOrderNo() + "状态");
            //更新状态不是eanble的订单,如果已经更新过了就不会更新
            Example settleProfitExample = new Example(HSettledProfit.class);
            settleProfitExample.createCriteria().andEqualTo("id",settledProfit.getId())
                    .andNotEqualTo("ifQueueNextOrder",EnableType.ENABLE.value());
            HSettledProfit updateParam = new HSettledProfit();
            //updateParam.setId(settledProfit.getId());
            updateParam.setIfQueueNextOrder(EnableType.ENABLE.value());
            //settledProfitMapper.updateByPrimaryKeySelective(updateParam);
            settledProfitMapper.updateByExampleSelective(updateParam,settleProfitExample);
        }
        mapper.insertSelective(hQueue);
    }

}