package com.udax.front.biz.ud;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.ud.*;
import com.github.wxiaoqi.security.common.enums.AccountLogType;
import com.github.wxiaoqi.security.common.enums.AccountSignType;
import com.github.wxiaoqi.security.common.enums.ud.SettledProfitStatus;
import com.github.wxiaoqi.security.common.enums.ud.UDOrderDetailStatus;
import com.github.wxiaoqi.security.common.enums.ud.UdCommissionType;
import com.github.wxiaoqi.security.common.mapper.ud.*;
import com.github.wxiaoqi.security.common.util.DataSortUtil;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.udax.front.biz.DcAssertAccountBiz;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import com.udax.front.service.ServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Comparator;
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
public class HSettledProfitBiz extends BaseBiz<HSettledProfitMapper, HSettledProfit> {

    @Autowired
    private HBonusConfigMapper configMapper;

    @Autowired
    private HCommissionRelationMapper commissionRelationMapper;



    @Autowired
    private HCommissionDetailMapper commissionDetailMapper;


    @Autowired
    private DcAssertAccountBiz dcAssertAccountBiz;

    @Autowired
    private HUserInfoMapper hUserInfoMapper;


    @Autowired
    private HOrderDetailMapper orderDetailMapper;

    @Autowired
    private HParamMapper paramMapper;

    @Autowired
    private HNodeAwardMapper nodeAwardMapper;


    @Autowired
    private HPurchaseLevelBiz purchaseLevelBiz;

    //获取用户的所有利润
    public BigDecimal getAllProfit(){
        Long userId = BaseContextHandler.getUserID();
        BigDecimal returnResult = mapper.getAllProfit(userId);
        return returnResult == null ? BigDecimal.ZERO : returnResult;
    }



    @Transactional(rollbackFor = Exception.class)
    public void settleProfit(HSettledProfit profit){
        try{
            logger.error(" 开始计算订单分成,订单号:" + profit.getOrderNo());
            HUserInfo userParam = new HUserInfo();
            userParam.setUserId(profit.getUserId());
            userParam = hUserInfoMapper.selectOne(userParam);
            if( userParam == null)
                return;


            //返回冻结的数量和金额
            AccountAssertLogVo payVo = new AccountAssertLogVo();
            payVo.setUserId(profit.getUserId());
            payVo.setSymbol(profit.getSymbol());
            payVo.setAmount(profit.getFreezeAmount());
            payVo.setChargeSymbol(profit.getSymbol());
            payVo.setType(AccountLogType.UD_UNFREEZE);
            payVo.setTransNo(profit.getOrderNo());//流水号
            payVo.setRemark(AccountLogType.UD_UNFREEZE.name());//返还冻结的金额
            dcAssertAccountBiz.signUpdateAssert(payVo,AccountSignType.ACCOUNT_WITHDRAW_DEDUTION);
            AccountAssertLogVo receive = new AccountAssertLogVo();
            receive.setUserId(profit.getUserId());
            receive.setSymbol(profit.getSymbol());
            receive.setAmount(profit.getFreezeProfit());
            receive.setChargeSymbol(profit.getSymbol());
            receive.setTransNo(profit.getOrderNo());//流水号
            receive.setType(AccountLogType.UD_PROFIT);
            receive.setRemark(AccountLogType.UD_PROFIT.name());//返还理论
            dcAssertAccountBiz.signUpdateAssert(receive,AccountSignType.ACCOUNT_RECHARGE);




            BigDecimal platCharge = platCharge(profit,userParam.getExchangeId());//计算平台手续费收益

            userProfit(profit,userParam.getExchangeId());//处理可以分到分成的用户

            nodeProfit(profit,userParam.getExchangeId());//节点奖分配

            //更新状态
            HSettledProfit updateParam = new HSettledProfit();
            updateParam.setId(profit.getId());
            updateParam.setStatus(SettledProfitStatus.SETTLED.value());
            mapper.updateByPrimaryKeySelective(updateParam);


            //更新订单状态
            Example example = new Example(HOrderDetail.class);
            example.createCriteria().andEqualTo("orderNo",profit.getOrderNo());
            HOrderDetail updateOrder = new HOrderDetail();
            updateOrder.setStatus(UDOrderDetailStatus.FINISH.value());
            updateOrder.setCharge(platCharge);
            updateOrder.setProfit(profit.getFreezeProfit());
            orderDetailMapper.updateByExampleSelective(updateOrder,example);

        }catch ( Exception e){
            logger.error("计算订单分成时候出错,订单号:" + profit.getOrderNo(),e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

    }

    private  void userProfit(HSettledProfit profit,Long exchId) throws Exception{
        //生成对应的分成记录
        HCommissionRelation relation = new HCommissionRelation();
        relation.setUserId(profit.getUserId());
        List<HCommissionRelation> relationsList = commissionRelationMapper.select(relation);
        HUserInfo queryParam = new HUserInfo();
        queryParam.setUserId(profit.getUserId());
        HUserInfo userInfo = hUserInfoMapper.selectOne(queryParam);
        if(StringUtil.listIsBlank(relationsList)) {
            return;
        }
        HBonusConfig configParam = new HBonusConfig();
        configParam.setExchId(exchId);
        List<HBonusConfig> configList = configMapper.select(configParam);
        if(StringUtil.listIsBlank(configList)){
            return;
        }
        Map<Integer,HBonusConfig> rateMap = configList.stream().collect(Collectors.toMap(HBonusConfig::getLevel, HBonusConfig->HBonusConfig));
        for(HCommissionRelation r : relationsList){
            queryParam.setUserId(r.getReceiveUserId());
            HUserInfo receiveUserInfo = hUserInfoMapper.selectOne(queryParam);

            //分润的基数
            BigDecimal orderProfit = profit.getFreezeProfit();
//                HPurchaseLevel receivelevel = purchaseLevelBiz.getProximalLevel(userInfo);
//                HPurchaseLevel currentLevel = purchaseLevelBiz.selectById(profit.getLevelId());

            //取两个用户之间的等级的最小者
            if(userInfo.getUserLevel().intValue() > receiveUserInfo.getUserLevel().intValue()){
                HPurchaseLevel levelParam = new HPurchaseLevel();
                levelParam.setLevel(receiveUserInfo.getUserLevel().intValue() < 1 ? 1 : receiveUserInfo.getUserLevel().intValue());
                levelParam.setExchId(receiveUserInfo.getExchangeId());
                HPurchaseLevel receivelevel = purchaseLevelBiz.selectOne(levelParam);

                logger.info("level:" + receiveUserInfo.getUserLevel() + ";exchId" + receiveUserInfo.getExchangeId());

                //投资额乘以日息乘以运行时间
                orderProfit = receivelevel.getAmount().multiply(receivelevel.getInterest()).multiply(new BigDecimal(receivelevel.getRunTime()));
            }
            int levelDiff = r.getLevel() -  r.getReceiveLevel();
            //直推用户数量 大于代数差,可以收取他的手续费
            if(receiveUserInfo.getDirectChild() < levelDiff) {
                continue;
            }
            HBonusConfig config = rateMap.get(levelDiff);//查询能分成的利润费
            if (config == null) {
                continue;
            }
            //其他人应该分配的利润
            BigDecimal otherProfit = orderProfit.multiply(config.getProfitRate()).setScale(8, BigDecimal.ROUND_DOWN);
            HCommissionDetail detail = new HCommissionDetail();
            detail.setUserId(r.getUserId());
            detail.setReceiveUserId(r.getReceiveUserId());
            detail.setOrderNo(profit.getOrderNo());
            detail.setOrderProfit(orderProfit);
            detail.setProfitRate(config.getProfitRate());
            detail.setProfit(otherProfit);
            detail.setBonusConfigId(config.getId());
            detail.setCreateTime(new Date());
            detail.setSymbol(profit.getSymbol());
            detail.setType(UdCommissionType.USER_CMS.value());//分润类型
            detail.setAmount(profit.getFreezeAmount());//冻结金额
            detail.setLevelId(profit.getLevelId());//等级Id
            detail.setLevelName(profit.getLevelName());
            //插入分成数据
            commissionDetailMapper.insertSelective(detail);


            //用户资产增加分成数据
            AccountAssertLogVo receive = new AccountAssertLogVo();
            receive.setUserId(r.getReceiveUserId());
            receive.setSymbol(profit.getSymbol());
            receive.setAmount(otherProfit);
            receive.setChargeSymbol(profit.getSymbol());
            receive.setTransNo(profit.getOrderNo());//流水号
            receive.setType(AccountLogType.UD_CMS);
            receive.setRemark(AccountLogType.UD_CMS.name());//返还利润
            dcAssertAccountBiz.signUpdateAssert(receive, AccountSignType.ACCOUNT_RECHARGE);
        }

    }

    //处理平台的手续费收益
    private BigDecimal platCharge(HSettledProfit profit,Long exchId) throws Exception{
        //平台设置的手续费比例
        HParam param = new HParam();
        param.setUdKey("PROFIT_RATE");
        param.setExchId(exchId);
        HParam hParam = paramMapper.selectOne(param);
        BigDecimal profitRate = BigDecimal.ZERO;
        //手续费如果不在0-1之间  统一设置成0
        if( hParam != null){
            profitRate= new BigDecimal(hParam.getUdValue());
            //分成比例不能大于100%
            if(profitRate.compareTo(BigDecimal.ONE) > 0 || profitRate.compareTo(BigDecimal.ZERO) < 0){
                profitRate = BigDecimal.ZERO;
            }
        }

        //平台手续费
        BigDecimal charge = profit.getFreezeProfit().multiply(profitRate).setScale(8,BigDecimal.ROUND_UP);
        if(profitRate.compareTo(BigDecimal.ZERO) > 0){
            //扣减用户相应的手续费
            AccountAssertLogVo chargeVo = new AccountAssertLogVo();
            chargeVo.setUserId(profit.getUserId());
            chargeVo.setSymbol(profit.getSymbol());
            chargeVo.setAmount(charge);
            chargeVo.setChargeSymbol(profit.getSymbol());
            chargeVo.setTransNo(profit.getOrderNo());//流水号
            chargeVo.setType(AccountLogType.UD_CHARGE);
            chargeVo.setRemark(AccountLogType.UD_CHARGE.name());//扣除手续费
            dcAssertAccountBiz.signUpdateAssert(chargeVo,AccountSignType.ACCOUNT_PAY_AVAILABLE);
        }
            //生成平台的手续费收益数据
            HCommissionDetail detail  = new HCommissionDetail();
            detail.setUserId(profit.getUserId());
            detail.setReceiveUserId(0L);
            detail.setOrderNo(profit.getOrderNo());
            detail.setOrderProfit(profit.getFreezeProfit());
            detail.setProfitRate(profitRate);
            detail.setProfit(charge);
            detail.setBonusConfigId(0L);
            detail.setSymbol(profit.getSymbol());
            detail.setCreateTime(new Date());
            detail.setType(UdCommissionType.PLAT_CMS.value());//分润类型,平台
            detail.setAmount(profit.getFreezeAmount());//冻结金额
            detail.setLevelId(profit.getLevelId());//等级Id
            detail.setLevelName(profit.getLevelName());
            //插入分成数据
            commissionDetailMapper.insertSelective(detail);
            return charge;
    }

    //节点奖分配
    private  void nodeProfit(HSettledProfit profit,Long exchId) throws Exception{

        HUserInfo userInfo = new HUserInfo();
        userInfo.setUserId(profit.getUserId());
        //查询当前用户的所有上级用户
        List<HUserInfo> receiveUserList = hUserInfoMapper.selectAllChildOfTopUser(profit.getUserId());
        
        HNodeAward nodeParam = new HNodeAward();
        nodeParam.setExchId(exchId);
        List<HNodeAward> nodeAwardList = nodeAwardMapper.select(nodeParam).stream().sorted(Comparator.comparing(HNodeAward::getChildInvest))
                .collect(Collectors.toList());

        //以下为节点奖相关代码
        List<HCommissionDetail> insertList = InstanceUtil.newArrayList();//分成数据集合,批量插入
        if(StringUtil.listIsNotBlank(receiveUserList)){
            for(HUserInfo hUserInfo : receiveUserList){
                singleUserNodeProfit(profit,nodeAwardList,hUserInfo,insertList);//计算节点奖
            }
        }

        if(StringUtil.listIsNotBlank(insertList)){
            commissionDetailMapper.insertList(insertList);//分成数据集合,批量插入
        }

        //以下为全球奖相关代码
        HNodeAward topNodeAward = null; //超级节点奖对象
        int count = 0;
        for(HNodeAward nodeAward:nodeAwardList){
            if(nodeAward.getGlobalRate().compareTo(BigDecimal.ZERO) > 0){//全球奖利润率不为零,为超级节点奖,可以分成全球利润
                topNodeAward = nodeAward;
                count ++;
            }
        }
        if(count > 1){ //超级节点奖利润率只能为唯一一条数据
            logger.error("节点奖励配置数据错误,超级节点奖利润率只能为唯一一条数据!");
            return;
        }
        globalNodeProfit(profit,topNodeAward,exchId);//计算全球奖

    }

    //单个用户的节点奖计算
    private void singleUserNodeProfit(HSettledProfit profit,List<HNodeAward> nodeAwardList,HUserInfo topUser,List<HCommissionDetail> insertList) throws Exception{
        BigDecimal topUserProfit = BigDecimal.ZERO;//上级用户若达到节点奖条件,应该分配的利润
        BigDecimal profitRate = BigDecimal.ZERO; //对应的利润分配比例

        for(HNodeAward nodeAward:nodeAwardList){
            if(topUser.getAddNodeAmount().compareTo(BigDecimal.ZERO) != 0 && topUser.getChildInvest().add(topUser.getAddNodeAmount()).compareTo(nodeAward.getChildInvest()) >= 0){ //额外增加的投资额不为零，只判断投资额是否满足条件
                profitRate = nodeAward.getBaseProfitRate().multiply(nodeAward.getRate()).setScale(8, BigDecimal.ROUND_DOWN);
                topUserProfit = profit.getFreezeProfit().multiply(profitRate).setScale(8, BigDecimal.ROUND_DOWN);
            }else if(topUser.getAddNodeAmount().compareTo(BigDecimal.ZERO) == 0 && topUser.getAllChild() >= nodeAward.getChildNum() && topUser.getChildInvest().compareTo(nodeAward.getChildInvest()) >= 0){//额外增加的投资额为零，需判断投资额和有效用户数量
                profitRate = nodeAward.getBaseProfitRate().multiply(nodeAward.getRate()).setScale(8, BigDecimal.ROUND_DOWN);
                topUserProfit = profit.getFreezeProfit().multiply(profitRate).setScale(8, BigDecimal.ROUND_DOWN);
            }
        }

        if(StringUtil.listIsNotBlank(insertList)){
            if(insertList.get(insertList.size()-1).getProfitRate().compareTo(profitRate) >= 0){//一个层级的节点奖只能分同一个人,上一代的层级必须大于下一代
                return;
            }
        }

        if(topUserProfit.compareTo( BigDecimal.ZERO) > 0){
            //能分到利润,则写入分成明细表
            HCommissionDetail detail = new HCommissionDetail();
            detail.setUserId(profit.getUserId());
            detail.setReceiveUserId(topUser.getUserId());
            detail.setOrderNo(profit.getOrderNo());
            detail.setOrderProfit(profit.getFreezeProfit());
            detail.setProfitRate(profitRate);
            detail.setProfit(topUserProfit);
            detail.setSymbol(profit.getSymbol());
            detail.setBonusConfigId(0l);
            detail.setCreateTime(new Date());
            detail.setType(UdCommissionType.NODE_AWARD.value());//分润类型
            detail.setAmount(profit.getFreezeAmount());//冻结金额
            detail.setLevelId(profit.getLevelId());//等级Id
            detail.setLevelName(profit.getLevelName());
            //插入分成数据
            // commissionDetailMapper.insertSelective(detail);
            insertList.add(detail);

            //用户资产增加分成数据
            AccountAssertLogVo receive = new AccountAssertLogVo();
            receive.setUserId(topUser.getUserId());
            receive.setSymbol(profit.getSymbol());
            receive.setAmount(topUserProfit);
            receive.setChargeSymbol(profit.getSymbol());
            receive.setTransNo(profit.getOrderNo());//流水号
            receive.setType(AccountLogType.NODE_AWARD);
            receive.setRemark(AccountLogType.NODE_AWARD.name());//返还利润
            dcAssertAccountBiz.signUpdateAssert(receive, AccountSignType.ACCOUNT_RECHARGE);
        }
    }

    //所有满足条件的用户全球奖金计算
    private void globalNodeProfit(HSettledProfit profit,HNodeAward topNodeAward,Long exchId) throws Exception{

        if(topNodeAward !=null) {
            //查询所有的超级节点用户
            Map<String,Object> paramMap = InstanceUtil.newHashMap();
            paramMap.put("investAmount",topNodeAward.getChildInvest());
            paramMap.put("exchId",exchId);
            List<HUserInfo> topUserList = hUserInfoMapper.selectGlobalUser(paramMap);//查询满足条件的全球用户

            if(StringUtil.listIsBlank(topUserList)){
                return;
            }

            List<HCommissionDetail> insertList = InstanceUtil.newArrayList();//分成数据集合,批量插入

            BigDecimal globalProfitRate = topNodeAward.getBaseProfitRate().multiply(topNodeAward.getGlobalRate()).setScale(8, BigDecimal.ROUND_DOWN);//超级节点用户应该分配的利润
            BigDecimal globalUserProfit = profit.getFreezeProfit().multiply(globalProfitRate).setScale(8, BigDecimal.ROUND_DOWN); //超级节点用户对应的利润分配比例

            for(HUserInfo globalUser:topUserList){
                if(globalUser.getAddNodeAmount().compareTo(BigDecimal.ZERO) != 0 && globalUser.getChildInvest().add(globalUser.getAddNodeAmount()).compareTo(topNodeAward.getChildInvest()) >= 0){
                    //能分到利润,则写入分成明细表
                    HCommissionDetail detail = getHCommissionDetail(profit,globalUser,globalProfitRate,globalUserProfit);
                    //插入分成数据
                    insertList.add(detail);

                    //用户资产增加分成数据
                    AccountAssertLogVo receive = new AccountAssertLogVo();
                    receive.setUserId(globalUser.getUserId());
                    receive.setSymbol(profit.getSymbol());
                    receive.setAmount(globalUserProfit);
                    receive.setChargeSymbol(profit.getSymbol());
                    receive.setTransNo(profit.getOrderNo());//流水号
                    receive.setType(AccountLogType.GLOBAL_AWARD);
                    receive.setRemark(AccountLogType.GLOBAL_AWARD.name());//返还利润
                    dcAssertAccountBiz.signUpdateAssert(receive, AccountSignType.ACCOUNT_RECHARGE);
                }else if(globalUser.getAddNodeAmount().compareTo(BigDecimal.ZERO) == 0 && globalUser.getAllChild() >= topNodeAward.getChildNum() && globalUser.getChildInvest().compareTo(topNodeAward.getChildInvest()) >= 0 && globalUserProfit.compareTo( BigDecimal.ZERO) > 0){
                    //能分到利润,则写入分成明细表
                    HCommissionDetail detail = getHCommissionDetail(profit,globalUser,globalProfitRate,globalUserProfit);
                    //插入分成数据
                    insertList.add(detail);

                    //用户资产增加分成数据
                    AccountAssertLogVo receive = new AccountAssertLogVo();
                    receive.setUserId(globalUser.getUserId());
                    receive.setSymbol(profit.getSymbol());
                    receive.setAmount(globalUserProfit);
                    receive.setChargeSymbol(profit.getSymbol());
                    receive.setTransNo(profit.getOrderNo());//流水号
                    receive.setType(AccountLogType.GLOBAL_AWARD);
                    receive.setRemark(AccountLogType.GLOBAL_AWARD.name());//返还利润
                    dcAssertAccountBiz.signUpdateAssert(receive, AccountSignType.ACCOUNT_RECHARGE);
                }
            }
            if(StringUtil.listIsNotBlank(insertList)){
                commissionDetailMapper.insertList(insertList);//分成数据集合,批量插入
            }
        }
    }

    private HCommissionDetail getHCommissionDetail(HSettledProfit profit,HUserInfo user,BigDecimal profitRate,BigDecimal userProfit){
        //能分到利润,则写入分成明细表
        HCommissionDetail detail = new HCommissionDetail();
        detail.setUserId(profit.getUserId());
        detail.setReceiveUserId(user.getUserId());
        detail.setOrderNo(profit.getOrderNo());
        detail.setOrderProfit(profit.getFreezeProfit());
        detail.setProfitRate(profitRate);
        detail.setSymbol(profit.getSymbol());
        detail.setProfit(userProfit);
        detail.setBonusConfigId(0l);
        detail.setCreateTime(new Date());
        detail.setType(UdCommissionType.GLOBAL_AWARD.value());//分润类型
        detail.setAmount(profit.getFreezeAmount());//冻结金额
        detail.setLevelId(profit.getLevelId());//等级Id
        detail.setLevelName(profit.getLevelName());
        return detail;
    }
}