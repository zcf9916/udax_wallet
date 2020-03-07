package com.github.wxiaoqi.security.admin.biz.casino;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.casino.*;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.casino.*;
import com.github.wxiaoqi.security.common.mapper.front.FrontUserMapper;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class CasinoCommissionLogBiz extends BaseBiz<CasinoCommissionLogMapper, CasinoCommissionLog> {

    @Autowired
    private FrontUserMapper frontUserMapper;

    @Autowired
    private CasinoRoleMapper casinoRoleMapper;

    @Autowired
    private CasinoParamMapper paramMapper;

    @Autowired
    private CasinoUserInfoBiz casinoUserInfoBiz;

    @Autowired
    private CasinoMethodConfigMapper casinoMethodConfigMapper;

    @Autowired
    private CasinoRebateConfigMapper casinoRebateConfigMapper;


    @Autowired
    private CasinoCommissionMapper casinoCommissionMapper;

    public void addCommission(CasinoCommission entity) {
        log.info("开始处理赌场用户:" + entity.getUserId() + ",业绩" + entity.getTotalAmount() + "的分成事件");
        Map<String, Object> param = InstanceUtil.newHashMap("userId", entity.getUserId());
        FrontUser user = frontUserMapper.selectCasinoUionUserInfo(param);
        if (user == null) {
            log.info("用户信息有误");
            return;
        }
        if (entity.getId() == null) {
            entity.setCreateTime(new Date());
            entity.setUserName(user.getUserName());
            entity.setOrderTime(new Date().getTime() / 1000);//时间戳
        }
        entity.setOrderNo(IdGenerator.nextId());
        entity.setPresidentAmount(BigDecimal.ZERO);
        entity.setManagerAmount(BigDecimal.ZERO);
        entity.setIndirectAmout(BigDecimal.ZERO);
        entity.setStraightAmout(BigDecimal.ZERO);
        entity.setMemberAmount(BigDecimal.ZERO);
        List<CasinoMethodConfig> configList = getCmsConfigList(user.getCasinoUserInfo().getExchangeId());
        LinkedList<FrontUser> linkedList = getRebateValid(user.getCasinoUserInfo().getParentId());
        BigDecimal platform = entity.getTotalAmount();
        //会员与客户对赌
        if (CasinoMethodConfig.CasinoMethodConfigType.ON_GAMBLING.value().equals(entity.getMethodType())) {
            CasinoMethodConfig methodConfig = getMethodConfig(configList, CasinoMethodConfig.CasinoMethodConfigType.ON_GAMBLING.value());
            entity.setPlatformAmount(entity.getTotalAmount());
            entity = performanceShare(entity, user, linkedList, methodConfig);

        } else if (CasinoMethodConfig.CasinoMethodConfigType.PROPORTIONATE.value().equals(entity.getMethodType())) {
            CasinoMethodConfig methodConfig = getMethodConfig(configList, CasinoMethodConfig.CasinoMethodConfigType.PROPORTIONATE.value());
            //写入会员
            CasinoCommissionLog cmsLog = new CasinoCommissionLog();
            cmsLog.setCmsRate(methodConfig.getUserCmsRate());//分成比例
            cmsLog.setRoleType(CasinoMethodConfig.RoleType.SELF.value());//平台
            cmsLog.setCmsType(CasinoCommissionLog.CmsType.CMS.value());
            cmsLog.setAmount(entity.getTotalAmount().multiply(methodConfig.getUserCmsRate()));
            cmsLog.setOrderNo(entity.getOrderNo());
            cmsLog.setTotalAmount(entity.getTotalAmount());
            addLog(cmsLog, user, user);
            entity.setMemberAmount(cmsLog.getAmount());
            //获取平台利润
            platform = entity.getTotalAmount().multiply(methodConfig.getPlatformCmsRate());
            entity.setPlatformAmount(platform);
            entity = performanceShare(entity, user, linkedList, methodConfig);
        }
        entity = handlePresidentAndManager(entity.getTotalAmount(), user.getCasinoUserInfo().getParentId(), entity, entity.getTotalAmount(), user, CasinoCommissionLog.CmsType.CMS.value());
        System.out.println("平台"+entity.getPlatformAmount());
        CasinoCommissionLog cmsLog = new CasinoCommissionLog();
        cmsLog.setCmsType(CasinoCommissionLog.CmsType.CMS.value());
        cmsLog.setCmsRate(new BigDecimal("1"));//分成比例
        cmsLog.setRoleType(CasinoMethodConfig.RoleType.WHITE.value());//角色类型
        cmsLog.setAmount(entity.getPlatformAmount());
        cmsLog.setOrderNo(entity.getOrderNo());
        cmsLog.setTotalAmount(entity.getTotalAmount());
        addLog(cmsLog, user, user);
        if (entity.getId() == null) {
            casinoCommissionMapper.insert(entity);
        } else {
            casinoCommissionMapper.updateByPrimaryKey(entity);
        }

    }


    //升级成精英会员
    @Transactional(rollbackFor = Exception.class)
    public void upgradeToCream(Long userId, CasinoUserInfo info) {
        log.info("开始处理赌场用户:" + userId + ",邀请返利的分成事件");
        Map<String, Object> param = InstanceUtil.newHashMap("userId", userId);
        FrontUser user = frontUserMapper.selectCasinoUionUserInfo(param);
        if (user == null) {
            log.info("用户信息有误");
            return;
        }
        CasinoUserInfo userCasinoUserInfo = user.getCasinoUserInfo();
        userCasinoUserInfo.setType(CasinoUserInfo.CasinoUserInfoType.CREAM.value());
        casinoUserInfoBiz.updateById(userCasinoUserInfo);
        //获取需要分成的用户集合
        LinkedList<FrontUser> userList = getRebateValid(user.getCasinoUserInfo().getParentId());
        CasinoCommission commission = new CasinoCommission();
        commission.setCreateTime(new Date());
        commission.setUserId(userId);
        commission.setUserName(user.getUserName());
        commission.setOrderTime(new Date().getTime() / 1000);//时间戳
        CasinoParam casino = getParam(Constants.CasinoParam.ACTIVATE_MEMBER, user.getCasinoUserInfo().getExchangeId());
        //成为精英会员的会费
        BigDecimal totalAmount = new BigDecimal(casino.getCasinoValue());
        commission.setTotalAmount(totalAmount);
        commission.setOrderNo(IdGenerator.nextId());
        commission.setPresidentAmount(BigDecimal.ZERO);
        commission.setManagerAmount(BigDecimal.ZERO);
        commission.setIndirectAmout(BigDecimal.ZERO);
        commission.setStraightAmout(BigDecimal.ZERO);
        commission.setMethodType(CasinoMethodConfig.RoleType.WHITE.value());
        //获取邀请返佣信息
        if (!StringUtil.listIsBlank(userList)) {
            List<CasinoRebateConfig> rebateConfigList = getRebateConfigList(user.getCasinoUserInfo().getExchangeId());
            for (FrontUser frontUser : userList) {
                CasinoCommissionLog cmsLog = new CasinoCommissionLog();
                cmsLog.setOrderNo(commission.getOrderNo());
                cmsLog.setCmsType(CasinoCommissionLog.CmsType.REBATE.value());
                if (user.getCasinoUserInfo().getParentId().equals(frontUser.getId())) {
                    //直推
                    CasinoRebateConfig rebateConfig = getRebateConfig(rebateConfigList, CasinoRebateConfig.CasinoRebateConfigType.Direct_push_user.value());
                    cmsLog.setCmsRate(rebateConfig.getCmsRate());//分成比例
                    cmsLog.setRoleType(rebateConfig.getType());//角色类型
                    cmsLog.setAmount(totalAmount.multiply(rebateConfig.getCmsRate()));
                    commission.setStraightAmout(cmsLog.getAmount());
                } else {
                    CasinoRebateConfig rebateConfig = getRebateConfig(rebateConfigList, CasinoRebateConfig.CasinoRebateConfigType.INDIRECT_RECOMMENDER.value());
                    cmsLog.setCmsRate(rebateConfig.getCmsRate());//分成比例
                    cmsLog.setRoleType(rebateConfig.getType());//角色类型
                    cmsLog.setAmount(totalAmount.multiply(rebateConfig.getCmsRate()));
                    commission.setIndirectAmout(cmsLog.getAmount());
                }
                cmsLog.setTotalAmount(totalAmount);
                addLog(cmsLog, user, frontUser);
            }
        }
        commission = handlePresidentAndManager(commission.getTotalAmount(), user.getCasinoUserInfo().getParentId(), commission, commission.getTotalAmount(), user, CasinoCommissionLog.CmsType.REBATE.value());
        CasinoCommissionLog cmsLog = new CasinoCommissionLog();
        cmsLog.setCmsType(CasinoCommissionLog.CmsType.REBATE.value());
        cmsLog.setCmsRate(new BigDecimal("1"));//分成比例
        cmsLog.setRoleType(CasinoMethodConfig.RoleType.WHITE.value());//角色类型
        cmsLog.setAmount(commission.getPlatformAmount());
        cmsLog.setOrderNo(commission.getOrderNo());
        cmsLog.setTotalAmount(totalAmount);
        addLog(cmsLog, user, user);
        casinoCommissionMapper.insert(commission);
        //成为拿到直推或者间接推荐收益的会员的推荐人数
        CasinoParam casinoParam = getParam(Constants.CasinoParam.REBATE_CONDITION, info.getExchangeId());
        if (casinoParam == null) {
            throw new UserInvalidException(Resources.getMessage("未获取到推荐限制条件信息"));
        }
        //享受分成条件
        int count = Integer.valueOf(casinoParam.getCasinoValue());
        List<CasinoRole> roleList = getCasinoRole(user.getCasinoUserInfo().getExchangeId());
        //升降级条件
        CasinoRole manager = getRole(roleList, CasinoRole.CasinoRoleType.DEPUTY_GENERAL_MANAGER.value());
        LinkedList<FrontUser> listAll = getSuperiorList(user.getCasinoUserInfo().getParentId());
        ArrayList<Long> parentIds = InstanceUtil.newArrayList();
        Integer allChild = 0;
        for (FrontUser frontUser : listAll) {
            CasinoUserInfo casinoUserInfo = frontUser.getCasinoUserInfo();
            casinoUserInfo.setAllChild(casinoUserInfo.getAllChild() + 1);
            if (user.getCasinoUserInfo().getParentId().equals(casinoUserInfo.getUserId())) {
                casinoUserInfo.setDirectChild(casinoUserInfo.getDirectChild() + 1);
            } else {
                if (casinoUserInfo.getDirectChild() == count) {
                    casinoUserInfo.setStatus(EnableType.ENABLE.value());
                }
            }
            if (casinoUserInfo.getDirectChild() >= count) {
                casinoUserInfo.setStatus(EnableType.ENABLE.value());
            }
            if (casinoUserInfo.getAllChild() >= manager.getAllChild()
                    && casinoUserInfo.getType().equals(CasinoUserInfo.CasinoUserInfoType.CREAM.value())
                    && casinoUserInfo.getDirectChild() >= manager.getDirectChild()) {
                casinoUserInfo.setType(CasinoUserInfo.CasinoUserInfoType.GM.value());
                if (casinoUserInfo.getParentId() != null && !casinoUserInfo.getParentId().equals(0L)) {
                    parentIds.add(casinoUserInfo.getParentId());
                    allChild = casinoUserInfo.getAllChild();
                }
                //这链上的所有用户跟着降级
            }
            casinoUserInfoBiz.updateById(casinoUserInfo);
        }
        if (!StringUtil.listIsBlank(parentIds)) {
            for (Long aLong : parentIds) {
                LinkedList<FrontUser> linkedList = getSuperiorList(aLong);
                if (!StringUtil.listIsBlank(linkedList)) {
                    for (FrontUser frontUser : linkedList) {
                        dropLever(frontUser.getCasinoUserInfo(), manager.getAllChild(), manager.getDirectChild(), allChild);
                    }
                }
            }
        }
    }


    private void dropLever(CasinoUserInfo userInfo, Integer allChild, Integer directChild, Integer currentChild) {

        if (userInfo != null) {
            //这链上的所有用户跟着降级
            if (userInfo.getAllChild() >= directChild  && !userInfo.getType().equals(CasinoUserInfo.CasinoUserInfoType.VP.value())) {
                userInfo.setAllChild(userInfo.getAllChild() - currentChild);
                if (userInfo.getAllChild() < 0) {
                    userInfo.setAllChild(userInfo.getDirectChild());
                }
                if (userInfo.getAllChild() < allChild && userInfo.getType().equals(CasinoUserInfo.CasinoUserInfoType.GM.value())) {
                    userInfo.setType(CasinoUserInfo.CasinoUserInfoType.CREAM.value());
                }
                casinoUserInfoBiz.updateById(userInfo);
            }
        }


    }

    private CasinoCommission performanceShare(CasinoCommission entity, FrontUser user,
                                              LinkedList<FrontUser> linkedList, CasinoMethodConfig methodConfig) {
        if (!StringUtil.listIsBlank(linkedList)) {
            for (FrontUser frontUser : linkedList) {
                CasinoCommissionLog cmsLog = new CasinoCommissionLog();
                cmsLog.setOrderNo(entity.getOrderNo());
                cmsLog.setCmsType(CasinoCommissionLog.CmsType.CMS.value());
                if (user.getCasinoUserInfo().getParentId().equals(frontUser.getId())) {
                    //直推
                    cmsLog.setCmsRate(methodConfig.getDirectUserRate());//分成比例
                    cmsLog.setRoleType(CasinoMethodConfig.RoleType.DIRECT.value());//角色类型
                    cmsLog.setAmount(entity.getPlatformAmount().multiply(methodConfig.getDirectUserRate()));
                    entity.setStraightAmout(cmsLog.getAmount());
                } else {
                    cmsLog.setCmsRate(methodConfig.getIndirectUserRate());//分成比例
                    cmsLog.setRoleType(CasinoMethodConfig.RoleType.INDIRECT.value());//角色类型
                    cmsLog.setAmount(entity.getPlatformAmount().multiply(methodConfig.getIndirectUserRate()));
                    entity.setIndirectAmout(cmsLog.getAmount());
                }
                cmsLog.setTotalAmount(entity.getTotalAmount());
                addLog(cmsLog, user, frontUser);
            }
        }
        return entity;
    }


    /**
     * 处理副总裁和副经理
     *
     * @param totalAmount
     * @param parenId
     * @param commission
     * @param platform
     * @param user
     * @return
     */
    public CasinoCommission handlePresidentAndManager(BigDecimal totalAmount, Long parenId,
                                                      CasinoCommission commission, BigDecimal platform, FrontUser user, Integer type) {
        LinkedList<FrontUser> linkedList = getSuperiorList(parenId);
        BigDecimal presidentAmount = BigDecimal.ZERO;
        BigDecimal managerAmount = BigDecimal.ZERO;
        List<CasinoRole> roleList = getCasinoRole(user.getCasinoUserInfo().getExchangeId());
        if (!StringUtil.listIsBlank(linkedList)) {
            for (FrontUser frontUser : linkedList) {
                //副总裁
                if (frontUser.getCasinoUserInfo().getType().equals(CasinoUserInfo.CasinoUserInfoType.VP.value())) {
                    CasinoRole casinoRole = getRole(roleList, CasinoRole.CasinoRoleType.VICE_PRESIDENT.value());
                    CasinoCommissionLog cmsLog = new CasinoCommissionLog();
                    cmsLog.setCmsType(type);
                    cmsLog.setCmsRate(casinoRole.getCmsRate());//分成比例
                    cmsLog.setRoleType(CasinoMethodConfig.RoleType.GM.value());//角色类型
                    cmsLog.setAmount(platform.multiply(casinoRole.getCmsRate()));
                    cmsLog.setOrderNo(commission.getOrderNo());
                    cmsLog.setTotalAmount(totalAmount);
                    addLog(cmsLog, user, frontUser);
                    presidentAmount = presidentAmount.add(cmsLog.getAmount());
                }
                //经理
                if (frontUser.getCasinoUserInfo().getType().equals(CasinoUserInfo.CasinoUserInfoType.GM.value())) {
                    CasinoRole casinoRole = getRole(roleList, CasinoRole.CasinoRoleType.DEPUTY_GENERAL_MANAGER.value());
                    CasinoCommissionLog cmsLog = new CasinoCommissionLog();
                    cmsLog.setCmsType(type);
                    cmsLog.setCmsRate(casinoRole.getCmsRate());//分成比例
                    cmsLog.setRoleType(CasinoMethodConfig.RoleType.VP.value());//角色类型
                    cmsLog.setAmount(platform.multiply(casinoRole.getCmsRate()));
                    cmsLog.setOrderNo(commission.getOrderNo());
                    cmsLog.setTotalAmount(totalAmount);
                    if (managerAmount.compareTo(BigDecimal.ZERO) == 0) {
                        managerAmount = managerAmount.add(cmsLog.getAmount());
                        addLog(cmsLog, user, frontUser);
                    }
                }
            }
        }
        //处理如果是顶级用户录入业绩,并且是副总裁
        if (user.getCasinoUserInfo().getParentId().equals(0L) && user.getCasinoUserInfo().getType().equals(CasinoUserInfo.CasinoUserInfoType.VP.value())) {
            CasinoRole casinoRole = getRole(roleList, CasinoRole.CasinoRoleType.VICE_PRESIDENT.value());
            CasinoCommissionLog cmsLog = new CasinoCommissionLog();
            cmsLog.setCmsType(type);
            cmsLog.setCmsRate(casinoRole.getCmsRate());//分成比例
            cmsLog.setRoleType(CasinoMethodConfig.RoleType.GM.value());//角色类型
            cmsLog.setAmount(platform.multiply(casinoRole.getCmsRate()));
            cmsLog.setOrderNo(commission.getOrderNo());
            cmsLog.setTotalAmount(totalAmount);
            addLog(cmsLog, user, user);
            presidentAmount = presidentAmount.add(cmsLog.getAmount());
        }
        commission.setManagerAmount(managerAmount);
        commission.setPresidentAmount(presidentAmount);
        if (type.equals(CasinoCommissionLog.CmsType.REBATE.value())){
            commission.setPlatformAmount(platform.subtract(managerAmount.add(presidentAmount).add(commission.getStraightAmout()).add(commission.getIndirectAmout())));
        }else {
            commission.setPlatformAmount(commission.getTotalAmount().subtract(commission.getMemberAmount())
                    .subtract(presidentAmount).subtract(managerAmount).subtract(commission.getStraightAmout()).subtract(commission.getIndirectAmout()));

        }
        return commission;
    }

    /**
     * 添加分成明细
     *
     * @param cmsLog
     * @param userInfo
     * @param currentUser
     */

    public void addLog(CasinoCommissionLog cmsLog, FrontUser userInfo, FrontUser currentUser) {
        cmsLog.setOrderTime(new Date().getTime() / 1000);//时间戳
        cmsLog.setExchangeId(userInfo.getCasinoUserInfo().getExchangeId());
        cmsLog.setCreateTime(new Date());
        if (!cmsLog.getRoleType().equals(CasinoRebateConfig.CasinoRebateConfigType.WHITE_LABEL_DIVISION.value())) {
            cmsLog.setReceiveUserName(currentUser.getUserName());
            cmsLog.setReceiveUserId(currentUser.getId());
            CasinoUserInfo casinoUserInfo = currentUser.getCasinoUserInfo();
            casinoUserInfo.setTotalAmount(casinoUserInfo.getTotalAmount().add(cmsLog.getAmount()));
            casinoUserInfoBiz.updateSelectiveById(casinoUserInfo);
        }else {
            cmsLog.setReceiveUserName("");
            cmsLog.setReceiveUserId(cmsLog.getExchangeId());
        }
        cmsLog.setDirectName(userInfo.getUserName());
        cmsLog.setDirectUserId(userInfo.getId());
        mapper.insertSelective(cmsLog);
    }

    /**
     * 获取利润分成配置
     */
    private List<CasinoMethodConfig> getCmsConfigList(Long exchId) {
        CasinoMethodConfig config = new CasinoMethodConfig();
        config.setExchId(exchId);
        List<CasinoMethodConfig> configList = casinoMethodConfigMapper.select(config);
        if (StringUtil.listIsBlank(configList) || configList.size() < 2) {
            throw new UserInvalidException("开工模式配置信息有误");
        }
        return configList;
    }

    /**
     * 获取返佣分成配置
     */
    public List<CasinoRebateConfig> getRebateConfigList(Long exchId) {
        CasinoRebateConfig config = new CasinoRebateConfig();
        config.setExchId(exchId);
        List<CasinoRebateConfig> configList = casinoRebateConfigMapper.select(config);
        if (StringUtil.listIsBlank(configList) || configList.size() < 3) {
            throw new UserInvalidException("邀请返佣配置信息有误,交易所id:" + exchId);
        }
        return configList;
    }

    /**
     * 获取团体配置信息
     *
     * @param exchId
     * @return
     */

    public List<CasinoRole> getCasinoRole(Long exchId) {
        CasinoRole role = new CasinoRole();
        role.setExchId(exchId);
        List<CasinoRole> roleList = casinoRoleMapper.select(role);
        if (StringUtil.listIsBlank(roleList) || roleList.size() < 2) {
            throw new UserInvalidException("团队利润配置异常,请联系管理员");
        }
        return roleList;
    }

    public CasinoParam getParam(String key, Long exchId) {
        CasinoParam param = new CasinoParam();
        param.setCasinoKey(key);
        param.setExchId(exchId);
        CasinoParam casinoParam = paramMapper.selectOne(param);
        if (casinoParam == null) {
            throw new UserInvalidException(Resources.getMessage("CONFIG_CASINO_PARAM"));
        }
        return casinoParam;
    }


    //获取间接推荐
    public CasinoRebateConfig getRebateConfig(List<CasinoRebateConfig> rebateConfigList, Integer type) {
        return rebateConfigList.stream().filter((l) -> l.getType().equals(type)).findFirst().orElse(null);
    }

    //开工模式--> 会员与平台对赌
    private CasinoMethodConfig getMethodConfig(List<CasinoMethodConfig> configList, Integer type) {
        return configList.stream().filter((l) -> l.getType().equals(type)).findFirst().orElse(null);
    }


    //获取副总裁分成比例
    public CasinoRole getRole(List<CasinoRole> roleList, Integer type) {
        //CasinoRole.CasinoRoleType.VICE_PRESIDENT.value()
        return roleList.stream().filter((l) -> l.getType().equals(type)).findFirst().orElse(null);
    }

    /**
     * @param userId 用户id
     * @return
     */
    private LinkedList<FrontUser> getSuperiorList(Long userId) {
        LinkedList<FrontUser> userIdList = new LinkedList<>();
        while (userId.intValue() != 0 && userId != null) {
            Map<String, Object> param = InstanceUtil.newHashMap("userId", userId);
            FrontUser user = frontUserMapper.selectCasinoUionUserInfo(param);
            if (user == null) {
                log.info("Id为" + userId + "的用户不存在");
                break;
            }
            userIdList.add(user);
            userId = user.getCasinoUserInfo().getParentId();
        }
        return userIdList;
    }

    private LinkedList<FrontUser> getRebateValid(Long userId) {
        LinkedList<FrontUser> userIdList = new LinkedList<>();
        //最多取2级
        FrontUser user = null;
        int tempLevel = 1;
        int level = 2;
        while (userId != null && userId != 0L && tempLevel <= level) {
            HashMap<String, Object> map = InstanceUtil.newHashMap();
            map.put("userId", userId);
            user = frontUserMapper.selectCasinoUionUserInfo(map);
            if (user == null) {
                log.info("Id为" + userId + "的用户不存在");
                break;
            }
            if (EnableType.ENABLE.value().equals(user.getCasinoUserInfo().getStatus())
                    && !user.getCasinoUserInfo().getType().equals(EnableType.DISABLE.value())) {
                userIdList.add(user);
            }
            userId = user.getCasinoUserInfo().getParentId();
            tempLevel++;
        }
        return userIdList;
    }
}
