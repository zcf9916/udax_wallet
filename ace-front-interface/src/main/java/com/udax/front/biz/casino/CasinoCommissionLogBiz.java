package com.udax.front.biz.casino;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.casino.*;
import com.github.wxiaoqi.security.common.entity.front.*;
import com.github.wxiaoqi.security.common.enums.AccountLogType;
import com.github.wxiaoqi.security.common.enums.AccountSignType;
import com.github.wxiaoqi.security.common.enums.CommissionConfigType;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.casino.CasinoCommissionLogMapper;
import com.github.wxiaoqi.security.common.mapper.casino.CasinoParamMapper;
import com.github.wxiaoqi.security.common.mapper.casino.CasinoRoleMapper;
import com.github.wxiaoqi.security.common.mapper.casino.CasinoUserInfoMapper;
import com.github.wxiaoqi.security.common.mapper.front.CommissionLogMapper;
import com.github.wxiaoqi.security.common.mapper.front.FrontTransferDetailMapper;
import com.github.wxiaoqi.security.common.mapper.front.TransferOrderMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import com.udax.front.biz.CacheBiz;
import com.udax.front.biz.DcAssertAccountBiz;
import com.udax.front.biz.FrontUserBiz;
import com.udax.front.event.TransferCoinEvent;
import com.udax.front.event.TransferOrderEvent;
import com.udax.front.util.CacheBizUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class CasinoCommissionLogBiz extends BaseBiz<CasinoCommissionLogMapper, CasinoCommissionLog> {



//
//    @Autowired
//    private CommissionLogBiz cmsLogBiz;//订单生产的分成明细

    @Autowired
    private CasinoRoleMapper casinoRoleMapper;

    @Autowired
    private CasinoUserInfoMapper casinoUserInfoMapper;
    @Autowired
    private CacheBiz cacheBiz;

    @Autowired
    private CasinoParamMapper paramMapper;
    @Autowired
    private CasinoUserInfoBiz frontUserBiz;

    //返佣
    public BigDecimal getRebate(Long userId){
        return mapper.getUserTotalCms(userId,CasinoCommissionLog.CmsType.REBATE.value());
    }
   //业绩分红
    public BigDecimal getCMs(Long userId){
        return mapper.getUserTotalCms(userId,CasinoCommissionLog.CmsType.CMS.value());
    }

//    @Getter
//    @Setter
//    public static class SettleConfig{
//       private BigDecimal cmsRate;//分成比例
//
//       private Integer roleType;//角色类型
//
//       private Long userId;
//
//       private String userName;
//    }
//
//
//
//
//    //更新上级用户的有效用户数
//    public void updateUserInfo(FrontUser user,LinkedList<FrontUser> parentList){
//        //成为拿到直推或者间接推荐收益的会员的推荐人数
//        CasinoParam param = new CasinoParam();
//        param.setCasinoKey("rebate_condition");
//        param.setExchId(user.getCasinoUserInfo().getExchangeId());
//        CasinoParam casinoParam = paramMapper.selectOne(param);
//        int count = Integer.valueOf(casinoParam.getCasinoValue());
//        CasinoUserInfo userInfo = new CasinoUserInfo();
//        userInfo.setIsValid(EnableType.ENABLE.value());
//        userInfo.setId(user.getCasinoUserInfo().getId());
//        //如果直推用户大于
//        if(userInfo.getDirectChild() >= count && userInfo.getStatus().equals(EnableType.DISABLE.value())){
//
//        }
//        //frontUserBiz.updateSelectiveById();
//
//    }
//
//
//    /**
//     * 业绩分红
//     * @param userId
//     * @param totalAmount 业绩
//     * @param type   开工类型
//     * @throws Exception
//     */
//    @Transactional( rollbackFor = Exception.class)
//    public void bonus(Long userId,BigDecimal totalAmount,int type) throws Exception{
//        log.info("开始处理赌场用户:"  + userId + ",业绩"+totalAmount+"的分成事件");
//        FrontUser user = frontUserBiz.selectCasinoUnionUserInfoById(userId);
//        if(user == null || !user.getCasinoUserInfo().getStatus().equals(EnableType.DISABLE.value())){
//            log.info("赌场用户:"  + userId + ",邀请人数未达到条件");
//            return;
//        }
//        List<CasinoMethodConfig> configList = getCmsConfigList(user.getCasinoUserInfo().getExchangeId(),cacheBiz);
//        //校验配置
//        validMethodConfig(configList,user.getCasinoUserInfo().getExchangeId());
//        //获取分成的用户Id
//        LinkedList<FrontUser> userList = getSuperiorUserList(user.getCasinoUserInfo().getUserId());//所有上级用户,包括自身
//        //角色配置
//        List<CasinoRole> roleList = getCasinoRoleConfig(user.getCasinoUserInfo().getExchangeId());
//        LinkedList<SettleConfig> settleConfig = getMethodSettleConfig(configList,userList,type,roleList);
//
//
//        //生成分成明细记录
//        BigDecimal tempCms = BigDecimal.ZERO;
//        Long orderNo = IdGenerator.nextId();
//        //如果是对赌
//        if(type == CasinoMethodConfig.CasinoMethodConfigType.ON_GAMBLING.value()){
//            CasinoParam param = new CasinoParam();
//            param.setCasinoKey("activate_member");
//            param.setExchId(user.getCasinoUserInfo().getExchangeId());
//            CasinoParam casinoParam = paramMapper.selectOne(param);
//            totalAmount = new BigDecimal(casinoParam.getCasinoValue());
//            if(totalAmount.compareTo(BigDecimal.ZERO) <= 0){
//                log.info("所有会员需要缴纳一万元等值的币（USDT）进行激活精英会员,根据此配置分配邀请返佣,数据为0");
//                return;
//            }
//        }
//
//        for(SettleConfig u : settleConfig){
//            CasinoCommissionLog cmsLog = new CasinoCommissionLog();
//            cmsLog.setCmsType(CasinoCommissionLog.CmsType.REBATE.value());
//            cmsLog.setCmsRate(u.getCmsRate());//分成比例
//            cmsLog.setRoleType(u.getRoleType());//角色类型
//            cmsLog.setCreateTime(new Date());
//            cmsLog.setExchangeId(user.getCasinoUserInfo().getExchangeId());
//            cmsLog.setOrderNo(orderNo);
//            cmsLog.setOrderTime(new Date().getTime()/1000);//时间戳
//            cmsLog.setDirectUserId(user.getId());
//            cmsLog.setDirectName(user.getUserName());
//            cmsLog.setTotalAmount(totalAmount);
//            cmsLog.setAmount(cmsLog.getTotalAmount().multiply(u.getCmsRate()).setScale(8,BigDecimal.ROUND_DOWN));//分得的数量
//            //如果是白标配置
//            if(CasinoMethodConfig.RoleType.WHITE.value().equals(u.getRoleType())){
//                cmsLog.setReceiveUserName("");
//                cmsLog.setAmount(totalAmount.subtract(tempCms));
//            } else{
//                cmsLog.setReceiveUserId(u.getUserId());
//                cmsLog.setReceiveUserName(u.getUserName());
//                tempCms = tempCms.add(cmsLog.getAmount());
//            }
//            if(cmsLog.getAmount().compareTo(BigDecimal.ZERO) <= 0){
//                continue;
//            }
//            mapper.insertSelective(cmsLog);
//        };
//    }
//
//
//
//    //升级成精英会员
//    @Transactional( rollbackFor = Exception.class)
//    public void upgradeToCream(Long userId) throws Exception{
//        log.info("开始处理赌场用户:"  + userId + ",邀请返利的分成事件");
//        FrontUser user = frontUserBiz.selectCasinoUnionUserInfoById(userId);
//        if(user == null || !user.getCasinoUserInfo().getStatus().equals(EnableType.DISABLE.value())){
//            log.info("赌场用户:"  + userId + ",邀请人数未达到条件");
//            return;
//        }
//        List<CasinoRebateConfig> configList = getRebateConfigList(user.getCasinoUserInfo().getExchangeId(),cacheBiz);
//        validRebateConfig(configList,user.getCasinoUserInfo().getExchangeId());
//        //获取分成的用户Id
//        LinkedList<FrontUser> userList = getSuperiorUserList(user.getCasinoUserInfo().getParentId());//所有上级用户
//        updateUserInfo(user,userList);
//        //角色配置
//        List<CasinoRole> roleList = getCasinoRoleConfig(user.getCasinoUserInfo().getExchangeId());
//
//        LinkedList<SettleConfig> settleConfig = getRebateSettleConfig(configList,userList,roleList);
//        //生成分成明细记录
//        BigDecimal tempCms = BigDecimal.ZERO;
//        Long orderNo = IdGenerator.nextId();
//        //成为精英会员的会费
//        CasinoParam param = new CasinoParam();
//        param.setCasinoKey("activate_member");
//        param.setExchId(user.getCasinoUserInfo().getExchangeId());
//        CasinoParam casinoParam = paramMapper.selectOne(param);
//        BigDecimal totalAmount = new BigDecimal(casinoParam.getCasinoValue());
//        if(totalAmount.compareTo(BigDecimal.ZERO) <= 0){
//            log.info("所有会员需要缴纳一万元等值的币（USDT）进行激活精英会员,根据此配置分配邀请返佣,数据为0");
//            return;
//        }
//        for(SettleConfig u : settleConfig){
//            CasinoCommissionLog cmsLog = new CasinoCommissionLog();
//            cmsLog.setCmsType(CasinoCommissionLog.CmsType.REBATE.value());
//            cmsLog.setCmsRate(u.getCmsRate());//分成比例
//            cmsLog.setRoleType(u.getRoleType());//角色类型
//            cmsLog.setCreateTime(new Date());
//            cmsLog.setExchangeId(user.getCasinoUserInfo().getExchangeId());
//            cmsLog.setOrderNo(orderNo);
//            cmsLog.setOrderTime(new Date().getTime()/1000);//时间戳
//            cmsLog.setDirectUserId(user.getId());
//            cmsLog.setDirectName(user.getUserName());
//            cmsLog.setTotalAmount(totalAmount);
//            cmsLog.setAmount(cmsLog.getTotalAmount().multiply(u.getCmsRate()).setScale(8,BigDecimal.ROUND_DOWN));//分得的数量
//            //如果是白标配置
//            if(CasinoMethodConfig.RoleType.WHITE.value().equals(u.getRoleType())){
//                cmsLog.setReceiveUserName("");
//                cmsLog.setAmount(totalAmount.subtract(tempCms));
//            } else{
//                cmsLog.setReceiveUserId(u.getUserId());
//                cmsLog.setReceiveUserName(u.getUserName());
//                tempCms = tempCms.add(cmsLog.getAmount());
//            }
//            if(cmsLog.getAmount().compareTo(BigDecimal.ZERO) <= 0){
//                continue;
//            }
//            mapper.insertSelective(cmsLog);
//            count ++;
//        };
//    }
//    //获取角色配置
//    private List<CasinoRole> getCasinoRoleConfig(Long exchId){
//        CasinoRole param = new CasinoRole();
//        param.setExchId(exchId);
//        return casinoRoleMapper.select(param);
//    }
//
//
//    //获取要邀請返佣结算的配置
//    private LinkedList<SettleConfig> getRebateSettleConfig(List<CasinoRebateConfig> cmsConfigList, List<FrontUser> userList,List<CasinoRole> roleList) {
//
//        //需要结算的用户
//        int settleUserSize = userList.stream().limit(cmsConfigList.size() - 1).collect(Collectors.toList()).size();
//        //用户配置
//        List<CasinoRebateConfig> userConfigList = cmsConfigList.stream().filter((l) -> l.getType() < CasinoRebateConfig.CasinoRebateConfigType.WHITE_LABEL_DIVISION.value()).collect(Collectors.toList());
//        final AtomicInteger count = new AtomicInteger();
//        //用户分配的配置
//        LinkedList<SettleConfig> settleConfig = new LinkedList(userConfigList.stream().limit(userList == null ? 0 : userList.size()).collect(Collectors.toList()).stream().sorted(new Comparator<CasinoRebateConfig>() {
//            @Override
//            public int compare(CasinoRebateConfig o1, CasinoRebateConfig o2) {
//                if(o1.getType() > o2.getType()){
//                    return 1;
//                } else {
//                    return -1;
//                }
//            }
//        }).collect(Collectors.toList()).stream().limit(settleUserSize).map((o) ->{
//                SettleConfig config = new SettleConfig();
//                config.setUserName(userList.get(count.get()).getUserName());
//                config.setUserId(userList.get(count.getAndIncrement()).getId());
//                config.setRoleType(o.getRoleType());
//                config.setCmsRate(o.getCmsRate());
//                return o;
//        }).collect(Collectors.toList()));
//
//
//        //副总李经理
//        List<FrontUser> gmUserList = userList.stream().filter((l) -> l.getCasinoUserInfo().getType().equals(CasinoUserInfo.RoleType.GM.value())
//                && l.getCasinoUserInfo().getStatus().equals(EnableType.ENABLE.value())
//        ).collect(Collectors.toList());
//        CasinoRole gmrole = roleList.stream().filter((l) -> l.getType().equals(CasinoRole.CasinoRoleType.DEPUTY_GENERAL_MANAGER.value())).findFirst().get();
//
//
//        gmUserList.stream().forEach((s) -> {
//            SettleConfig sConfig = new SettleConfig();
//            sConfig.setCmsRate(gmrole.getCmsRate());
//            sConfig.setRoleType(CasinoMethodConfig.RoleType.GM.value());//
//            sConfig.setUserId(s.getId());
//            sConfig.setUserName(s.getUserName());
//            settleConfig.add(sConfig);
//        });
//
//
//        //副总李经理
//        List<FrontUser> vpUserList = userList.stream().filter((l) -> l.getCasinoUserInfo().getType().equals(CasinoUserInfo.RoleType.VP.value())
//                && l.getCasinoUserInfo().getStatus().equals(EnableType.ENABLE.value())
//        ).collect(Collectors.toList());
//        CasinoRole vprole = roleList.stream().filter((l) -> l.getType().equals(CasinoRole.CasinoRoleType.VICE_PRESIDENT.value())).findFirst().get();
//
//        vpUserList.stream().forEach((s) -> {
//            SettleConfig sConfig = new SettleConfig();
//            sConfig.setCmsRate(vprole.getCmsRate());
//            sConfig.setRoleType(CasinoMethodConfig.RoleType.VP.value());//
//            sConfig.setUserId(s.getId());
//            sConfig.setUserName(s.getUserName());
//            settleConfig.add(sConfig);
//        });
//
//
//        BigDecimal remainRate = BigDecimal.ZERO;
//        if (!settleConfig.isEmpty()) {
//            //分出去的比例
//            remainRate = settleConfig.stream().map(SettleConfig::getCmsRate).reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
//        }
//
//        //加上白标配置
//        //剩下的都是白标的
//        SettleConfig exchConfig = new SettleConfig();
//        exchConfig.setCmsRate(BigDecimal.ONE.subtract(remainRate));
//        exchConfig.setRoleType(CasinoMethodConfig.RoleType.WHITE.value());//白标
//        settleConfig.add(exchConfig);
//        return settleConfig;
//    }
//
//
//
//    //获取要邀請返佣结算的配置
//    private LinkedList<SettleConfig> getMethodSettleConfig(List<CasinoMethodConfig> cmsConfigList, LinkedList<FrontUser> userList,int type,List<CasinoRole> roleList) {
//        //副总经理和副总裁的配置
//        CasinoRole vprole = roleList.stream().filter((l) -> l.getType().equals(CasinoRole.CasinoRoleType.VICE_PRESIDENT.value())).findFirst().get();
//        CasinoRole gmrole = roleList.stream().filter((l) -> l.getType().equals(CasinoRole.CasinoRoleType.DEPUTY_GENERAL_MANAGER.value())).findFirst().get();
//
//        //对赌
//        //需要结算的用户
//        List<FrontUser> settleUser = userList.stream().limit(3).collect(Collectors.toList());//会员自身,直推人和简介推荐人
//        //副总李经理
//        List<FrontUser> gmUserList = userList.stream().filter((l) -> l.getCasinoUserInfo().getType().equals(CasinoUserInfo.RoleType.GM.value())
//                && l.getCasinoUserInfo().getStatus().equals(EnableType.ENABLE.value())
//        ).collect(Collectors.toList());
//        //副总裁
//        List<FrontUser> vpUserList = userList.stream().filter((l) -> l.getCasinoUserInfo().getType().equals(CasinoUserInfo.RoleType.VP.value())
//                && l.getCasinoUserInfo().getStatus().equals(EnableType.ENABLE.value())
//        ).collect(Collectors.toList());
//
//        CasinoMethodConfig config = cmsConfigList.stream().filter((l) -> l.getType().equals(CasinoMethodConfig.CasinoMethodConfigType.ON_GAMBLING.value())).findFirst().orElse(null);
//        //结算配置
//        LinkedList<SettleConfig> list = InstanceUtil.newLinkedList();
//        if(StringUtil.listIsNotBlank(settleUser)){
//            //如果是对赌  收益自负
//            if(type == CasinoMethodConfig.CasinoMethodConfigType.ON_GAMBLING.value()) {
//                SettleConfig sConfig = new SettleConfig();
//                sConfig.setCmsRate(config.getUserCmsRate());
//                sConfig.setRoleType(CasinoMethodConfig.RoleType.SELF.value());//用户自身
//                sConfig.setUserId(settleUser.get(0).getId());
//                sConfig.setUserName(settleUser.get(0).getUserName());
//                list.add(sConfig);
//            }
//
//            if(settleUser.size() > 1){
//                SettleConfig dConfig = new SettleConfig();
//                dConfig.setCmsRate(config.getDirectUserRate().multiply(config.getPlatformCmsRate()));
//                dConfig.setRoleType(CasinoMethodConfig.RoleType.DIRECT.value());//直推用户
//                dConfig.setUserId(settleUser.get(1).getId());
//                dConfig.setUserName(settleUser.get(1).getUserName());
//                list.add(dConfig);
//            }
//            if(settleUser.size() > 2){
//                SettleConfig iConfig = new SettleConfig();
//                iConfig.setCmsRate(config.getIndirectUserRate().multiply(config.getPlatformCmsRate()));
//                iConfig.setRoleType(CasinoMethodConfig.RoleType.INDIRECT.value());//间接推荐人
//                iConfig.setUserId(settleUser.get(2).getId());
//                iConfig.setUserName(settleUser.get(2).getUserName());
//                list.add(iConfig);
//            }
//        };
//        //副总经理
//        if(StringUtil.listIsNotBlank(gmUserList)){
//            gmUserList.stream().forEach((s) ->{
//                SettleConfig sConfig = new SettleConfig();
//                sConfig.setCmsRate(gmrole.getCmsRate());
//                sConfig.setRoleType(CasinoMethodConfig.RoleType.GM.value());//
//                sConfig.setUserId(s.getId());
//                sConfig.setUserName(s.getUserName());
//                list.add(sConfig);
//            });
//        };
//        //副总裁
//        if(StringUtil.listIsNotBlank(vpUserList)){
//            vpUserList.stream().forEach((s) ->{
//                SettleConfig sConfig = new SettleConfig();
//                sConfig.setCmsRate(vprole.getCmsRate());
//                sConfig.setRoleType(CasinoMethodConfig.RoleType.VP.value());//
//                sConfig.setUserId(s.getId());
//                sConfig.setUserName(s.getUserName());
//                list.add(sConfig);
//            });
//        };
//
//        //剩下的都是白标的
//        SettleConfig exchConfig = new SettleConfig();
//        exchConfig.setCmsRate(BigDecimal.ONE.subtract(list.stream().map(SettleConfig::getCmsRate).reduce(BigDecimal.ZERO,(a,b) -> a.add(b))));
//        exchConfig.setRoleType(CasinoMethodConfig.RoleType.WHITE.value());//白标
//        list.add(exchConfig);
//
//        return list;
//    }
//
//
//
//
//
//
//    /**
//     * 获取利润分成配置
//     */
//    private  List<CasinoMethodConfig> getCmsConfigList(Long exchId, CacheBiz biz) {
////        List<CmsConfig> list = null;
////        list = (List<CmsConfig>) CacheUtil.getCache().get(Constants.CacheServiceType.CMS_CONFIG_BIZ + exchId);
////        if (StringUtil.listIsBlank(list)) {
////            biz.cacheReturnCmsConfig();
////            list = (List<CmsConfig>) CacheUtil.getCache().get(Constants.CacheServiceType.CMS_CONFIG_BIZ + exchId);
////            if (list == null || list.size() == 0) {
////                logger.error("获取分成配置失败,交易所id:" + exchId);
////            }
////        }
//
//        return list == null ? new ArrayList<>() : list;
//    }
//    /**
//     * 获取返佣分成配置
//     */
//    public  List<CasinoRebateConfig> getRebateConfigList(Long exchId, CacheBiz biz) {
////        List<CmsConfig> list = null;
////        list = (List<CmsConfig>) CacheUtil.getCache().get(Constants.CacheServiceType.CMS_CONFIG_BIZ + exchId);
////        if (StringUtil.listIsBlank(list)) {
////            biz.cacheReturnCmsConfig();
////            list = (List<CmsConfig>) CacheUtil.getCache().get(Constants.CacheServiceType.CMS_CONFIG_BIZ + exchId);
////            if (list == null || list.size() == 0) {
////                logger.error("获取分成配置失败,交易所id:" + exchId);
////            }
////        }
//
//        return list == null ? new ArrayList<>() : list;
//    }
//
//    //校验配置
//    private void validRebateConfig(List<CasinoRebateConfig> configList,Long exchId) throws Exception {
//        //用户和白标加起来不等于100%
//        if(configList.stream().map(CasinoRebateConfig::getCmsRate).reduce(BigDecimal.ZERO,(a,b) -> a.add(b)).compareTo(BigDecimal.ONE) != 0){
//            throw new UserInvalidException("用户和白标加起来不等于100%,exchId"  + exchId);
//        }
//    }
//    //校验业绩配置
//    private void validMethodConfig(List<CasinoMethodConfig> configList,Long exchId) throws Exception {
//        //用户和白标加起来不等于100%
//        if(configList.stream().map(CasinoMethodConfig::getCmsRate).reduce(BigDecimal.ZERO,(a,b) -> a.add(b)).compareTo(BigDecimal.ONE) != 0){
//            throw new UserInvalidException("用户和白标加起来不等于100%,exchId"  + exchId);
//        }
//    }
//
//
//    //校验配置
//    private void validConfig(List<CmsConfig> cmsConfigList,Long exchId) throws Exception {
//        List<CmsConfig> otherConfig = cmsConfigList.stream().filter( (l) -> l.getType() >= CommissionConfigType.RECOMMENDED_USER_SHARE.value()).collect(Collectors.toList());
//        //用户和白标加起来不等于100%
//        if(otherConfig.stream().map(CmsConfig::getCmsRate).reduce(BigDecimal.ZERO,(a,b) -> a.add(b)).compareTo(BigDecimal.ONE) != 0){
//            throw new UserInvalidException("用户和白标加起来不等于100%,exchId"  + exchId);
//        }
//        CmsConfig userConfig = otherConfig.stream().filter((l) -> l.getType().equals(CommissionConfigType.RECOMMENDED_USER_SHARE.value())).findFirst().orElse(null);
//        CmsConfig exchConfig = otherConfig.stream().filter((l) -> l.getType().equals(CommissionConfigType.EXCH_PROPORTION.value())).findFirst().orElse(null);
//        //用户和白标加起来不等于100%
//        if(userConfig == null || exchConfig == null){
//            throw new UserInvalidException("用户和白标配置为空,exchId"  + exchId);
//        }
//        //按type排序,把各个层级的比例乘以用户的比例
//        List<CmsConfig> userConfigList = cmsConfigList.stream().filter( (l) -> l.getType() < CommissionConfigType.RECOMMENDED_USER_SHARE.value())
//                .sorted(new Comparator<CmsConfig>() {
//                    @Override
//                    public int compare(CmsConfig o1, CmsConfig o2) {
//                        if(o1.getType() > o2.getType()){
//                            return 1;
//                        } else {
//                            return -1;
//                        }
//                    }
//                }).collect(Collectors.toList());
//
//        //三级用户加起来不等于100%
//        if(userConfigList.stream().map(CmsConfig::getCmsRate).reduce(BigDecimal.ZERO,(a,b) -> a.add(b)).compareTo(BigDecimal.ONE) != 0){
//            throw new UserInvalidException("三级用户加起来不等于100%,exchId"  + exchId);
//        }
//    }
//
//    //获取用户配置
//    private List<CmsConfig> getUserConfig(List<CmsConfig> cmsConfigList) throws Exception {
//        CmsConfig userConfig = cmsConfigList.stream().filter((l) -> l.getType().equals(CommissionConfigType.RECOMMENDED_USER_SHARE.value())).findFirst().orElse(null);
//        //按type排序,把各个层级的比例乘以用户的比例
//        List<CmsConfig> userConfigList = cmsConfigList.stream().filter((l) -> l.getType() < CommissionConfigType.RECOMMENDED_USER_SHARE.value())
//                .map((o) -> {
//                    o.setCmsRate(o.getCmsRate().multiply(userConfig.getCmsRate()));
//                    return o;
//                })
//                .sorted(new Comparator<CmsConfig>() {
//                    @Override
//                    public int compare(CmsConfig o1, CmsConfig o2) {
//                        if (o1.getType() > o2.getType()) {
//                            return 1;
//                        } else {
//                            return -1;
//                        }
//                    }
//                }).collect(Collectors.toList());
//        return userConfigList;
//    }
//    //获取用户的上级用户列表
//
//    /**
//     *
//     * @param userId 用户id
//     * @param level 往上查几级
//     * @return
//     */
//    private LinkedList<FrontUser> getSuperiorUserList (Long userId){
//        LinkedList<FrontUser> userIdList = new LinkedList<>();
//        while (userId.intValue() != 0 && userId != null){
//            FrontUser user = frontUserBiz.selectCasinoUnionUserInfoById(userId);
//            if(user == null){
//                log.info("Id为"  + userId + "的用户不存在");
//                break;
//            }
//            userIdList.add(user);
//            userId = user.getCasinoUserInfo().getParentId();
//        }
//        return userIdList;
//    }
//


}
