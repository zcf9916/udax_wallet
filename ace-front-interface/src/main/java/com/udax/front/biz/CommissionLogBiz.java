package com.udax.front.biz;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.front.*;
import com.github.wxiaoqi.security.common.enums.AccountLogType;
import com.github.wxiaoqi.security.common.enums.AccountSignType;
import com.github.wxiaoqi.security.common.enums.CommissionConfigType;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.front.CommissionLogMapper;
import com.github.wxiaoqi.security.common.mapper.front.FrontTransferDetailMapper;
import com.github.wxiaoqi.security.common.mapper.front.TransferOrderMapper;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import com.udax.front.event.TransferCoinEvent;
import com.udax.front.event.TransferOrderEvent;
import com.udax.front.util.CacheBizUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class CommissionLogBiz extends BaseBiz<CommissionLogMapper, CommissionLog> {


    @Autowired
    private DcAssertAccountBiz dcAssertAccountBiz;
//
//    @Autowired
//    private CommissionLogBiz cmsLogBiz;//订单生产的分成明细


    @Autowired
    private TransferOrderMapper transferOrderMapper;//转账

    @Autowired
    private CacheBiz cacheBiz;

    @Autowired
    private FrontTransferDetailMapper transferDetailMapper;//转币

    @Autowired
    private FrontUserBiz frontUserBiz;
    /**
     * 获取用户总收益
     * @return
     */
    public BigDecimal getUserTotalCms(@Param("userId") Long userId){
        return mapper.getUserTotalCms(userId);
    }


    private List<CmsConfig> getCmsConfig(Long exchId) throws Exception {
        List<CmsConfig> cmsConfigList = CacheBizUtil.getCmsConfigList(exchId,cacheBiz);
        if(StringUtil.listIsBlank(cmsConfigList) || cmsConfigList.size() != 5){
            throw new Exception("分成配置信息有误,exchId"  + exchId);
        }
        return  cmsConfigList;
    }
    //校验配置
    private void validConfig(List<CmsConfig> cmsConfigList,Long exchId) throws Exception {
        List<CmsConfig> otherConfig = cmsConfigList.stream().filter( (l) -> l.getType() >= CommissionConfigType.RECOMMENDED_USER_SHARE.value()).collect(Collectors.toList());
        //用户和白标加起来不等于100%
        if(otherConfig.stream().map(CmsConfig::getCmsRate).reduce(BigDecimal.ZERO,(a,b) -> a.add(b)).compareTo(BigDecimal.ONE) != 0){
            throw new UserInvalidException("用户和白标加起来不等于100%,exchId"  + exchId);
        }
        CmsConfig userConfig = otherConfig.stream().filter((l) -> l.getType().equals(CommissionConfigType.RECOMMENDED_USER_SHARE.value())).findFirst().orElse(null);
        CmsConfig exchConfig = otherConfig.stream().filter((l) -> l.getType().equals(CommissionConfigType.EXCH_PROPORTION.value())).findFirst().orElse(null);
        //用户和白标加起来不等于100%
        if(userConfig == null || exchConfig == null){
            throw new UserInvalidException("用户和白标配置为空,exchId"  + exchId);
        }
        //按type排序,把各个层级的比例乘以用户的比例
        List<CmsConfig> userConfigList = cmsConfigList.stream().filter( (l) -> l.getType() < CommissionConfigType.RECOMMENDED_USER_SHARE.value())
                .sorted(new Comparator<CmsConfig>() {
                    @Override
                    public int compare(CmsConfig o1, CmsConfig o2) {
                        if(o1.getType() > o2.getType()){
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                }).collect(Collectors.toList());

        //三级用户加起来不等于100%
        if(userConfigList.stream().map(CmsConfig::getCmsRate).reduce(BigDecimal.ZERO,(a,b) -> a.add(b)).compareTo(BigDecimal.ONE) != 0){
            throw new UserInvalidException("三级用户加起来不等于100%,exchId"  + exchId);
        }
    }

    //获取用户配置
    private List<CmsConfig> getUserConfig(List<CmsConfig> cmsConfigList) throws Exception {
        CmsConfig userConfig = cmsConfigList.stream().filter((l) -> l.getType().equals(CommissionConfigType.RECOMMENDED_USER_SHARE.value())).findFirst().orElse(null);
        //按type排序,把各个层级的比例乘以用户的比例
        List<CmsConfig> userConfigList = cmsConfigList.stream().filter((l) -> l.getType() < CommissionConfigType.RECOMMENDED_USER_SHARE.value())
                .map((o) -> {
                    o.setCmsRate(o.getCmsRate().multiply(userConfig.getCmsRate()));
                    return o;
                })
                .sorted(new Comparator<CmsConfig>() {
                    @Override
                    public int compare(CmsConfig o1, CmsConfig o2) {
                        if (o1.getType() > o2.getType()) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                }).collect(Collectors.toList());
        return userConfigList;
    }
    //获取用户的上级用户列表

    /**
     *
     * @param userId 用户id
     * @param level 往上查几级
     * @return
     */
    private LinkedList<FrontUser> getSuperiorUserList (Long userId, int level){
        LinkedList<FrontUser> userIdList = new LinkedList<>();
        FrontUser user = null;
        int tempLevel = 1;
        while (userId.intValue() != 0 && userId != null && tempLevel <= level){
            user = CacheBizUtil.getFrontUserCache(userId,frontUserBiz);
            if(user == null){
                log.info("Id为"  + userId + "的用户不存在");
                break;
            }
            userIdList.add(user);
            userId = user.getUserInfo().getParentId();
            tempLevel ++;
        }
        return userIdList;
    }
    //获取要结算的配置
    private  List<CmsConfig>  getSettleConfig (List<CmsConfig> cmsConfigList,List<FrontUser> userList) throws Exception {
        //白标配置
        CmsConfig exchConfig = cmsConfigList.stream().filter((l) -> l.getType().equals(CommissionConfigType.EXCH_PROPORTION.value())).findFirst().orElse(null);
        //用户总配置
        CmsConfig userConfig = cmsConfigList.stream().filter((l) -> l.getType().equals(CommissionConfigType.RECOMMENDED_USER_SHARE.value())).findFirst().orElse(null);
        List<CmsConfig> userConfigList = getUserConfig(cmsConfigList);//用户配置
        //当前需要算的层级,根据用户父类的层级来决定
        List<CmsConfig> settleConfig = userConfigList.stream().limit(userList == null ? 0 : userList.size()).collect(Collectors.toList());
        BigDecimal remainRate = userConfig.getCmsRate();
        if (!StringUtil.listIsBlank(settleConfig)){
            //用户的比例 - 分出去的比例 = 剩下的比例
            remainRate = remainRate.subtract(settleConfig.stream().map(CmsConfig::getCmsRate).reduce(BigDecimal.ZERO,(a,b) -> a.add(b)));
            CmsConfig firstConfig = settleConfig.get(0);
            firstConfig.setCmsRate(firstConfig.getCmsRate().add(remainRate));
            remainRate = settleConfig.stream().map(CmsConfig::getCmsRate).reduce(BigDecimal.ZERO,(a,b) -> a.add(b));
        }else{
            remainRate = BigDecimal.ZERO;
        }

        exchConfig.setCmsRate(BigDecimal.ONE.subtract(remainRate));
        //加上白标配置
        settleConfig.add(exchConfig);
        return settleConfig;
    }
    @Transactional( rollbackFor = Exception.class)
    public void insertCmsLog(ApplicationEvent event) throws Exception {
          if(event instanceof TransferCoinEvent){
              handleTransferCoinEvent((TransferCoinEvent) event);
          } else if( event instanceof TransferOrderEvent){
              handleTransfer((TransferOrderEvent) event);
          }
    }

    //转币事件
    private  void handleTransferCoinEvent(TransferCoinEvent event) throws Exception{

        FrontTransferDetail order = event.getDetail();
        if(order == null || order.getChargeAmount().compareTo(BigDecimal.ZERO) <= 0){
            log.info("转币订单号:"  + order.getOrderNo() + "的手续费为0");
        }
        log.info("开始处理转币订单号:"  + order.getOrderNo() + "的分成事件");
        Map<String,BigDecimal> quotationBean = cacheBiz.getQuotation();
        List<CmsConfig> cmsConfigList = getCmsConfig(event.getExchId());
        validConfig(cmsConfigList,event.getExchId());
        FrontUser user = CacheBizUtil.getFrontUserCache(order.getUserId(),frontUserBiz);
        List<FrontUser> userList = getSuperiorUserList(user.getUserInfo().getParentId(),cmsConfigList.size() - 2);
        List<CmsConfig> settleConfig = getSettleConfig(cmsConfigList,userList);
        //生成分成明细记录
        int count = 0;
        BigDecimal tempCms = BigDecimal.ZERO;
        for(CmsConfig u : settleConfig){
            CommissionLog cmsLog = new CommissionLog();
            cmsLog.setCmsRate(u.getCmsRate());//分成比例
            cmsLog.setCreateTime(new Date());
            cmsLog.setExchangeId(event.getExchId());
            cmsLog.setOrderNo(order.getOrderNo());
            cmsLog.setOrderTime(order.getCreateTime().getTime()/1000);//时间戳
            cmsLog.setOrderType(CommissionLog.CmsType.TRANSFER_COIN.value());
            cmsLog.setType(u.getType());
            cmsLog.setRate(quotationBean.get(order.getChargeCurrencyCode() + "/" + Constants.QUOTATION_DCCODE) == null ? BigDecimal.ZERO :
                    quotationBean.get(order.getChargeCurrencyCode() + "/" + Constants.QUOTATION_DCCODE));//获取手续费币种和结算币种的转换比例
            cmsLog.setTradeuserId(user.getId());
            cmsLog.setTradeuserName(user.getUserName());
            cmsLog.setSettleSymbol(Constants.QUOTATION_DCCODE);//结算币种
            cmsLog.setSymbol(order.getChargeCurrencyCode());
            cmsLog.setTotalCms(order.getChargeAmount());
            cmsLog.setAmount(order.getChargeAmount().multiply(u.getCmsRate()).setScale(8,BigDecimal.ROUND_DOWN));
            cmsLog.setSettleAmount(cmsLog.getAmount().multiply(cmsLog.getRate()).setScale(8,BigDecimal.ROUND_DOWN));
            if(u.getType().equals(CommissionConfigType.EXCH_PROPORTION.value())){
                //cmsLog.setReceiveUserId(event.getExchId());
                cmsLog.setReceiveUserName("");
                cmsLog.setSettleAmount(cmsLog.getTotalCms().multiply(cmsLog.getRate()).subtract(tempCms));
            } else{
                cmsLog.setReceiveUserId(userList.get(count).getId());
                cmsLog.setReceiveUserName(userList.get(count).getUserName());
                tempCms = tempCms.add(cmsLog.getSettleAmount());
            }
            if(cmsLog.getSettleAmount().compareTo(BigDecimal.ZERO) <= 0){
              continue;
            }
            mapper.insertSelective(cmsLog);
            count ++;
        };
        //更新结算状态
        FrontTransferDetail param = new FrontTransferDetail();
        param.setId(order.getId());
        param.setSettleStatus(EnableType.ENABLE.value());
        transferDetailMapper.updateByPrimaryKeySelective(param);

    }

    //转账事件
    private  void handleTransfer(TransferOrderEvent event) throws Exception {
        TransferOrder order = event.getOrder();
        if(order == null || order.getChargeAmount().compareTo(BigDecimal.ZERO) <= 0){
            log.info("转币订单号:"  + order.getOrderNo() + "的手续费为0");
        }
        log.info("开始处理转币订单号:"  + order.getOrderNo() + "的分成事件");
        Map<String,BigDecimal> quotationBean = cacheBiz.getQuotation();
        List<CmsConfig> cmsConfigList = getCmsConfig(event.getExchId());
        validConfig(cmsConfigList,event.getExchId());
        FrontUser user = CacheBizUtil.getFrontUserCache(order.getUserId(),frontUserBiz);
        List<FrontUser> userList = getSuperiorUserList(user.getUserInfo().getParentId(),cmsConfigList.size() - 2);
        List<CmsConfig> settleConfig = getSettleConfig(cmsConfigList,userList);
        //生成分成明细记录
        int count = 0;
        BigDecimal tempCms = BigDecimal.ZERO;
        for(CmsConfig u : settleConfig){
            CommissionLog cmsLog = new CommissionLog();
            cmsLog.setCmsRate(u.getCmsRate());//分成比例
            cmsLog.setCreateTime(new Date());
            cmsLog.setExchangeId(event.getExchId());
            cmsLog.setOrderNo(order.getOrderNo());
            cmsLog.setOrderTime(order.getCreateTime().getTime()/1000);//时间戳
            cmsLog.setOrderType(CommissionLog.CmsType.TRANSFER_COIN.value());
            cmsLog.setType(u.getType());
            cmsLog.setRate(quotationBean.get(order.getSymbol() + "/" + Constants.QUOTATION_DCCODE) == null ? BigDecimal.ZERO :
                    quotationBean.get(order.getSymbol() + "/" + Constants.QUOTATION_DCCODE));//获取手续费币种和结算币种的转换比例
            cmsLog.setTradeuserId(user.getId());
            cmsLog.setTradeuserName(user.getUserName());
            cmsLog.setSettleSymbol(Constants.QUOTATION_DCCODE);//结算币种
            cmsLog.setSymbol(order.getSymbol());
            cmsLog.setTotalCms(order.getChargeAmount());
            cmsLog.setAmount(order.getChargeAmount().multiply(u.getCmsRate()).setScale(8,BigDecimal.ROUND_DOWN));
            cmsLog.setSettleAmount(cmsLog.getAmount().multiply(cmsLog.getRate()).setScale(8,BigDecimal.ROUND_DOWN));
            if(u.getType().equals(CommissionConfigType.EXCH_PROPORTION.value())){
                //cmsLog.setReceiveUserId(event.getExchId());
                cmsLog.setReceiveUserName("");
                cmsLog.setSettleAmount(cmsLog.getTotalCms().multiply(cmsLog.getRate()).subtract(tempCms));
            } else{
                cmsLog.setReceiveUserId(userList.get(count).getId());
                cmsLog.setReceiveUserName(userList.get(count).getUserName());
                tempCms = tempCms.add(cmsLog.getSettleAmount());
            }
            if(cmsLog.getSettleAmount().compareTo(BigDecimal.ZERO) <= 0){
                continue;
            }
            mapper.insertSelective(cmsLog);
            count ++;
        };
        //更新结算状态
        TransferOrder param = new TransferOrder();
        param.setId(order.getId());
        param.setSettleStatus(EnableType.ENABLE.value());
        transferOrderMapper.updateByPrimaryKeySelective(param);
    }


    //每日结算分成
    public void settleDailyCms(){
        LocalDate localDate =   LocalDate.now().minusDays(1);
        //查询的开始时间
        long beginTimeStamp = localDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli()/1000;
        //查询的结束时间
        long endTimeStamp = localDate.plusDays(1).atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli()/1000 - 1;
        logger.info("分成开始时间:" + beginTimeStamp + "结束时间:" + endTimeStamp);
        //查询未结算的订单
        List<Map<String,Object>> list = mapper.selectSettleData(beginTimeStamp,endTimeStamp,EnableType.DISABLE.value(),CommissionConfigType.EXCH_PROPORTION.value());
        logger.info("分成数据:" + list.size());
        if(StringUtil.listIsNotBlank(list)){
            for(Map<String,Object> ob : list){
                BigDecimal amount = (BigDecimal) ob.get("amount");
                Long userId = (Long) ob.get("userId");
                String symbol = (String) ob.get("symbol");
                try{
                    logger.info("用户开始分成,用户Id:" + userId + ",数量:" + amount.stripTrailingZeros().toPlainString());
                    settleUserCms(symbol,userId,amount,beginTimeStamp,endTimeStamp);
                }catch (Exception e){
                    logger.error("用户id:" + userId + ",代币：" + symbol + "结算分成失败" + e.getMessage(),e);
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                }
            }
        }
    }

    public static void main(String[] args) {
      //  LocalDate localDate =   LocalDate.now().minusDays(2);

        LocalDate localDate=LocalDate.of(2019,11,20);

        //查询的开始时间
        long beginTimeStamp = localDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli()/1000;
        //查询的结束时间
        long endTimeStamp = localDate.plusDays(1).atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli()/1000 - 1;
        System.out.println(beginTimeStamp);
        System.out.println(endTimeStamp);
    }
    /**
     * 结算用户分成
     * @param symbol
     * @param userId
     * @param amount
     * @param beginTimeStamp
     * @param endTimeStamp
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    protected void settleUserCms(String symbol ,Long userId,BigDecimal amount,Long beginTimeStamp,Long endTimeStamp) throws Exception{
        AccountAssertLogVo payVo = new AccountAssertLogVo();
        payVo.setUserId(userId);
        payVo.setSymbol(symbol);
        payVo.setAmount(amount);
        payVo.setChargeSymbol(symbol);
        payVo.setType(AccountLogType.CMS_ADD_ASSERT);
        payVo.setTransNo(String.valueOf(IdGenerator.nextId()));//时间戳 + 用户id作为流水号
        payVo.setRemark(AccountLogType.CMS_ADD_ASSERT.name());
        dcAssertAccountBiz.signUpdateAssert(payVo,AccountSignType.ACCOUNT_RECHARGE);

        //更新改用户当天的记录的状态为已结算
        Example example = new Example(CommissionLog.class);
        example.createCriteria().andEqualTo("settleStatus",EnableType.DISABLE.value())
                .andEqualTo("receiveUserId",userId)
                .andEqualTo("settleSymbol",symbol)
                .andBetween("orderTime",beginTimeStamp,endTimeStamp);
        CommissionLog cms = new CommissionLog();
        cms.setSettleStatus(EnableType.ENABLE.value());
        mapper.updateByExampleSelective(cms,example);
    }

}
