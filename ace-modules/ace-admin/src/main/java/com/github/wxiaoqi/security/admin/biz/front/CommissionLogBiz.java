package com.github.wxiaoqi.security.admin.biz.front;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.front.*;
import com.github.wxiaoqi.security.common.enums.AccountLogType;
import com.github.wxiaoqi.security.common.enums.AccountSignType;
import com.github.wxiaoqi.security.common.enums.CommissionConfigType;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.front.CommissionLogMapper;
import com.github.wxiaoqi.security.common.mapper.front.FrontTransferDetailMapper;
import com.github.wxiaoqi.security.common.mapper.front.FrontUserMapper;
import com.github.wxiaoqi.security.common.mapper.front.TransferOrderMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.LocalDateUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class CommissionLogBiz extends BaseBiz<CommissionLogMapper, CommissionLog> {


    @Autowired
    private TransferOrderMapper transferOrderMapper;//转账
    @Autowired
    private FrontUserMapper frontUserMapper;
    @Autowired
    private CmsConfigBiz cmsConfigBiz;
    @Autowired
    private FrontTransferDetailMapper frontTransferDetailMapper;
    @Autowired
    private AssetAccountBiz assetAccountBiz;


    public void generateFrontTransferDetail(String orderId) {
        FrontTransferDetail detail = new FrontTransferDetail();
        detail.setOrderNo(orderId);
        FrontTransferDetail orderDetail = frontTransferDetailMapper.selectForUpdate(detail);
        if (orderDetail == null || orderDetail.getChargeAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserInvalidException(Resources.getMessage("TRANSFER_ORDER_ERROR"));
        }
        FrontUser user = getUserExchId(orderDetail.getUserId());
        List<CmsConfig> configList = getCmsConfig(user.getUserInfo().getExchangeId());
        if (StringUtil.listIsBlank(configList)) {
            throw new UserInvalidException(Resources.getMessage("NO_CONFIG_COMMISSION"));
        }
        if (getQuotation().get(orderDetail.getChargeCurrencyCode() + "/" + Constants.QUOTATION_DCCODE) == null) {
            throw new UserInvalidException(Resources.getMessage("QUOTATION_ERROR"));
        }
        validConfig(configList, user.getUserInfo().getExchangeId());
        List<FrontUser> userList = getSuperiorUserList(user.getUserInfo().getParentId(), configList.size() - 2);//configList.size()-2需要往上分多少层
        List<CmsConfig> settleConfig = getSettleConfig(configList, userList);
        int count = 0;
        BigDecimal tempCms = BigDecimal.ZERO;
        for (CmsConfig u : settleConfig) {
            CommissionLog cmsLog = new CommissionLog();
            cmsLog.setCmsRate(u.getCmsRate());//分成比例
            cmsLog.setCreateTime(new Date());
            cmsLog.setExchangeId(user.getUserInfo().getExchangeId());
            cmsLog.setOrderNo(orderDetail.getOrderNo());
            logger.error("tjg:" + LocalDateUtil.date2LocalDateTime(orderDetail.getCreateTime()).toString());
            System.out.println(TimeZone.getDefault());
            logger.error("tjg1:" + orderDetail.getCreateTime().getTime());

            cmsLog.setOrderTime(orderDetail.getCreateTime().getTime() / 1000);//时间戳
            cmsLog.setOrderType(CommissionLog.CmsType.TRANSFER_COIN.value());
            cmsLog.setType(u.getType());
            cmsLog.setRate(getQuotation().get(orderDetail.getChargeCurrencyCode() + "/" + Constants.QUOTATION_DCCODE) == null ? BigDecimal.ZERO :
                    getQuotation().get(orderDetail.getChargeCurrencyCode() + "/" + Constants.QUOTATION_DCCODE));//获取手续费币种和结算币种的转换比例
            cmsLog.setTradeuserId(user.getId());
            cmsLog.setTradeuserName(user.getUserName());
            cmsLog.setSettleSymbol(Constants.QUOTATION_DCCODE);//结算币种
            cmsLog.setSymbol(orderDetail.getChargeCurrencyCode());
            cmsLog.setTotalCms(orderDetail.getChargeAmount());
            cmsLog.setAmount(orderDetail.getChargeAmount().multiply(u.getCmsRate()).setScale(8, BigDecimal.ROUND_DOWN));
            cmsLog.setSettleAmount(cmsLog.getAmount().multiply(cmsLog.getRate()).setScale(8, BigDecimal.ROUND_DOWN));
            if (u.getType().equals(CommissionConfigType.EXCH_PROPORTION.value())) {
                //cmsLog.setReceiveUserId(event.getExchId());
                cmsLog.setReceiveUserName("");
                cmsLog.setSettleAmount(cmsLog.getTotalCms().multiply(cmsLog.getRate()).subtract(tempCms));
            } else {
                cmsLog.setReceiveUserId(userList.get(count).getId());
                cmsLog.setReceiveUserName(userList.get(count).getUserName());
                tempCms = tempCms.add(cmsLog.getSettleAmount());
            }
            if (cmsLog.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            mapper.insertSelective(cmsLog);
            count++;
        }
        //更新结算状态
        FrontTransferDetail param = new FrontTransferDetail();
        param.setId(orderDetail.getId());
        param.setSettleStatus(EnableType.ENABLE.value());
        frontTransferDetailMapper.updateByPrimaryKeySelective(param);
    }

    public void generateTransferOrder(String orderId) {
        TransferOrder transfer = new TransferOrder();
        transfer.setOrderNo(orderId);
        TransferOrder order = transferOrderMapper.selectForUpdate(transfer);
        if (order == null || order.getChargeAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserInvalidException(Resources.getMessage("TRANSFER_ORDER_ERROR"));
        }
        FrontUser user = getUserExchId(order.getUserId());
        List<CmsConfig> configList = getCmsConfig(user.getUserInfo().getExchangeId());
        if (StringUtil.listIsBlank(configList)) {
            throw new UserInvalidException(Resources.getMessage("CMSCONFIG_ERROR"));
        }
        if (getQuotation().get(order.getSymbol() + "/" + Constants.QUOTATION_DCCODE) == null) {
            throw new UserInvalidException(Resources.getMessage("QUOTATION_ERROR"));
        }
        validConfig(configList, user.getUserInfo().getExchangeId());
        List<FrontUser> userList = getSuperiorUserList(user.getUserInfo().getParentId(), configList.size() - 2);
        List<CmsConfig> settleConfig = getSettleConfig(configList, userList);
        BigDecimal tempCms = BigDecimal.ZERO;
        int count = 0;
        for (CmsConfig u : settleConfig) {
            CommissionLog cmsLog = new CommissionLog();
            cmsLog.setCmsRate(u.getCmsRate());//分成比例
            cmsLog.setCreateTime(new Date());
            cmsLog.setExchangeId(user.getUserInfo().getExchangeId());
            cmsLog.setOrderNo(order.getOrderNo());
            cmsLog.setOrderTime(order.getCreateTime().getTime() / 1000);//时间戳
            cmsLog.setOrderType(CommissionLog.CmsType.TRANSFER.value());
            cmsLog.setType(u.getType());
            cmsLog.setRate(getQuotation().get(order.getSymbol() + "/" + Constants.QUOTATION_DCCODE) == null ? BigDecimal.ZERO :
                    getQuotation().get(order.getSymbol() + "/" + Constants.QUOTATION_DCCODE));//获取手续费币种和结算币种的转换比例
            cmsLog.setTradeuserId(user.getId());
            cmsLog.setTradeuserName(user.getUserName());
            cmsLog.setSettleSymbol(Constants.QUOTATION_DCCODE);//结算币种
            cmsLog.setSymbol(order.getSymbol());
            cmsLog.setTotalCms(order.getChargeAmount());
            cmsLog.setAmount(order.getChargeAmount().multiply(u.getCmsRate()).setScale(8, BigDecimal.ROUND_DOWN));
            cmsLog.setSettleAmount(cmsLog.getAmount().multiply(cmsLog.getRate()).setScale(8, BigDecimal.ROUND_DOWN));
            if (u.getType().equals(CommissionConfigType.EXCH_PROPORTION.value())) {
                //cmsLog.setReceiveUserId(event.getExchId());
                cmsLog.setReceiveUserName("");
                cmsLog.setSettleAmount(cmsLog.getTotalCms().multiply(cmsLog.getRate()).subtract(tempCms));
            } else {
                cmsLog.setReceiveUserId(userList.get(count).getId());
                cmsLog.setReceiveUserName(userList.get(count).getUserName());
                tempCms = tempCms.add(cmsLog.getSettleAmount());
            }
            if (cmsLog.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            mapper.insertSelective(cmsLog);
            count++;
        }
        //更新结算状态
        TransferOrder param = new TransferOrder();
        param.setId(order.getId());
        param.setSettleStatus(EnableType.ENABLE.value());
        transferOrderMapper.updateByPrimaryKeySelective(param);

    }


    //    //校验配置
    private void validConfig(List<CmsConfig> cmsConfigList, Long exchId) {
        List<CmsConfig> otherConfig = cmsConfigList.stream().filter((l) -> l.getType() >= CommissionConfigType.RECOMMENDED_USER_SHARE.value()).collect(Collectors.toList());
        //用户和白标加起来不等于100%
        if (otherConfig.stream().map(CmsConfig::getCmsRate).reduce(BigDecimal.ZERO, (a, b) -> a.add(b)).compareTo(BigDecimal.ONE) != 0) {
            logger.error("用户和白标加起来不等于100%,exchId" + exchId);
            throw new UserInvalidException("NO_CONFIG");
        }
        CmsConfig userConfig = otherConfig.stream().filter((l) -> l.getType().equals(CommissionConfigType.RECOMMENDED_USER_SHARE.value())).findFirst().orElse(null);
        CmsConfig exchConfig = otherConfig.stream().filter((l) -> l.getType().equals(CommissionConfigType.EXCH_PROPORTION.value())).findFirst().orElse(null);
        //用户和白标加起来不等于100%
        if (userConfig == null || exchConfig == null) {
            logger.error("用户和白标配置为空" + exchId);
            throw new UserInvalidException("NO_CONFIG");
        }
        //按type排序,把各个层级的比例乘以用户的比例
        List<CmsConfig> userConfigList = cmsConfigList.stream().filter((l) -> l.getType() < CommissionConfigType.RECOMMENDED_USER_SHARE.value())
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

        //三级用户加起来不等于100%
        if (userConfigList.stream().map(CmsConfig::getCmsRate).reduce(BigDecimal.ZERO, (a, b) -> a.add(b)).compareTo(BigDecimal.ONE) != 0) {
            logger.error("三级用户加起来不等于100%,exchId");
            throw new UserInvalidException("NO_CONFIG");
        }
    }

    //
//    //获取用户配置
    private List<CmsConfig> getUserConfig(List<CmsConfig> cmsConfigList) {
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


    //获取要结算的配置
    private List<CmsConfig> getSettleConfig(List<CmsConfig> cmsConfigList, List<FrontUser> userList) {
        //白标配置
        CmsConfig exchConfig = cmsConfigList.stream().filter((l) -> l.getType().equals(CommissionConfigType.EXCH_PROPORTION.value())).findFirst().orElse(null);
        //用户总配置
        CmsConfig userConfig = cmsConfigList.stream().filter((l) -> l.getType().equals(CommissionConfigType.RECOMMENDED_USER_SHARE.value())).findFirst().orElse(null);
        List<CmsConfig> userConfigList = getUserConfig(cmsConfigList);//用户配置
        //当前需要算的层级,根据用户父类的层级来决定
        List<CmsConfig> settleConfig = userConfigList.stream().limit(userList == null ? 0 : userList.size()).collect(Collectors.toList());
        BigDecimal remainRate = userConfig.getCmsRate();
        if (!StringUtil.listIsBlank(settleConfig)) {
            //用户的比例 - 分出去的比例 = 剩下的比例
            remainRate = remainRate.subtract(settleConfig.stream().map(CmsConfig::getCmsRate).reduce(BigDecimal.ZERO, (a, b) -> a.add(b)));
            CmsConfig firstConfig = settleConfig.get(0);
            firstConfig.setCmsRate(firstConfig.getCmsRate().add(remainRate));
            remainRate = settleConfig.stream().map(CmsConfig::getCmsRate).reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
        }
        {
            remainRate = BigDecimal.ZERO;
        }

        exchConfig.setCmsRate(BigDecimal.ONE.subtract(remainRate));
        //加上白标配置
        settleConfig.add(exchConfig);
        return settleConfig;
    }

    /***
     * 获取配置信息
     * @param exchId
     * @return
     * @throws Exception
     */
    private List<CmsConfig> getCmsConfig(Long exchId) {
        List<CmsConfig> list = null;
        list = (List<CmsConfig>) CacheUtil.getCache().get(Constants.CacheServiceType.CMS_CONFIG_BIZ + exchId);
        if (StringUtil.listIsBlank(list)) {
            cmsConfigBiz.cacheReturn();
            list = (List<CmsConfig>) CacheUtil.getCache().get(Constants.CacheServiceType.CMS_CONFIG_BIZ + exchId);
        }
        return list == null ? new ArrayList<>() : list;
    }

    /**
     * 获取订单用户交易所
     *
     * @return
     */
    private FrontUser getUserExchId(Long userId) {
        HashMap<String, Object> map = InstanceUtil.newHashMap();
        map.put("userId", userId);
        FrontUser userInfo = frontUserMapper.selectUnionUserInfo(map);
        if (userInfo == null || userInfo.getUserInfo() == null) {
            logger.error("获取用户信息有误,用户id:" + userId);
            throw new UserInvalidException(Resources.getMessage("FRONT_USER_ERROR"));
        }
        return userInfo;
    }

    /**
     * @param userId 用户id
     * @param level  往上查几级
     * @return
     */
    private LinkedList<FrontUser> getSuperiorUserList(Long userId, int level) {
        LinkedList<FrontUser> userIdList = new LinkedList<>();
        FrontUser user = null;
        int tempLevel = 1;
        while (userId != null && userId != 0L && tempLevel <= level) {
            HashMap<String, Object> map = InstanceUtil.newHashMap();
            map.put("userId", userId);
            user = frontUserMapper.selectUnionUserInfo(map);
            if (user == null) {
                log.info("Id为" + userId + "的用户不存在");
                break;
            }
            userIdList.add(user);
            userId = user.getUserInfo().getParentId();
            tempLevel++;
        }
        return userIdList;
    }


    //单条结算
    public void settlement(Long id) throws Exception {
        if (id == null) {
            throw new UserInvalidException(Resources.getMessage("SETTLEMENT_PARAM"));
        }
        CommissionLog commissionLog = new CommissionLog();
        commissionLog.setId(id);
        CommissionLog log = mapper.selectForUpdate(commissionLog);
        if (EnableType.ENABLE.value().equals(commissionLog.getSettleStatus())) {
            throw new UserInvalidException(Resources.getMessage("SETTLEMENT"));
        }
        AccountAssertLogVo payVo = new AccountAssertLogVo();
        payVo.setUserId(log.getReceiveUserId());
        payVo.setSymbol(log.getSettleSymbol());
        payVo.setAmount(log.getSettleAmount());
        payVo.setChargeSymbol(log.getSettleSymbol());
        payVo.setType(AccountLogType.CMS_ADD_ASSERT);
        payVo.setRemark(AccountLogType.CMS_ADD_ASSERT.name());
        assetAccountBiz.dcSignUpdateAssert(payVo, AccountSignType.ACCOUNT_RECHARGE);
        Example example = new Example(CommissionLog.class);
        example.createCriteria().andEqualTo("settleStatus", EnableType.DISABLE.value())
                .andEqualTo("receiveUserId", log.getReceiveUserId())
                .andEqualTo("symbol", log.getSymbol());
        CommissionLog cms = new CommissionLog();

        cms.setSettleStatus(EnableType.ENABLE.value());
        int returnCount = mapper.updateByExampleSelective(cms, example);
        if (returnCount == 0) {
            logger.error("资产更新成功,更新分成明细失败");
        }

    }

    //批量结算
    public Integer batchSettlement(LocalDate beginDateTime, LocalDate endDateTime) {
        long beginTimeStamp = beginDateTime.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli() / 1000;
        long endTimeStamp = endDateTime.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli() / 1000;
        List<Map<String, Object>> list = mapper.selectSettleData(beginTimeStamp, endTimeStamp, EnableType.DISABLE.value(), CommissionConfigType.EXCH_PROPORTION.value());

        if (StringUtil.listIsNotBlank(list)) {
            for (Map<String, Object> ob : list) {
                BigDecimal amount = (BigDecimal) ob.get("amount");
                Long userId = (Long) ob.get("userId");
                String symbol = (String) ob.get("symbol");
                try {
                    logger.info("用户开始分成,用户Id:" + userId + ",数量:" + amount.stripTrailingZeros().toPlainString());
                    settleUserCms(symbol, userId, amount, beginTimeStamp, endTimeStamp);
                } catch (Exception e) {
                    logger.error("用户id:" + userId + ",代币：" + symbol + "结算分成失败" + e.getMessage(), e);
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                }
            }
        }
        return list.size();
    }

    //获取行情价
    public Map<String, BigDecimal> getQuotation() {
        Map<String, BigDecimal> map = (Map<String, BigDecimal>) CacheUtil.getCache().hgetAll(Constants.CommonType.QUOTATION);
        if (map == null) {
            map = InstanceUtil.newHashMap();
        }
        map.put("USDT/USDT", BigDecimal.ONE);
        return map;
    }

    /**
     * 结算用户分成
     *
     * @param symbol
     * @param userId
     * @param amount
     * @param beginTimeStamp
     * @param endTimeStamp
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    protected void settleUserCms(String symbol, Long userId, BigDecimal amount, Long beginTimeStamp, Long endTimeStamp) throws Exception {
        AccountAssertLogVo payVo = new AccountAssertLogVo();
        payVo.setUserId(userId);
        payVo.setSymbol(symbol);
        payVo.setAmount(amount);
        payVo.setChargeSymbol(symbol);
        payVo.setType(AccountLogType.CMS_ADD_ASSERT);
        payVo.setTransNo(String.valueOf(IdGenerator.nextId()));//时间戳 + 用户id作为流水号
        payVo.setRemark(AccountLogType.CMS_ADD_ASSERT.name());
        assetAccountBiz.dcSignUpdateAssert(payVo, AccountSignType.ACCOUNT_RECHARGE);
        //更新改用户当天的记录的状态为已结算
        Example example = new Example(CommissionLog.class);
        example.createCriteria().andEqualTo("settleStatus", EnableType.DISABLE.value())
                .andEqualTo("receiveUserId", userId)
                .andEqualTo("settleSymbol", symbol)
                .andBetween("orderTime", beginTimeStamp, endTimeStamp);
        CommissionLog cms = new CommissionLog();
        cms.setSettleStatus(EnableType.ENABLE.value());
        mapper.updateByExampleSelective(cms, example);
    }

    public static void main(String[] args) {
        LocalDate date = LocalDate.of(2019,11,11);
        System.out.println(LocalDateUtil.localDate2Date(date).getTime());
        System.out.println();
    }
}
