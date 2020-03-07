package com.udax.front.biz.ud;

import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.FrontCountry;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.front.FrontUserInfo;
import com.github.wxiaoqi.security.common.entity.front.GeneratorId;
import com.github.wxiaoqi.security.common.entity.front.UserLoginLog;
import com.github.wxiaoqi.security.common.entity.ud.*;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.enums.ud.QueueStatus;
import com.github.wxiaoqi.security.common.enums.ud.UDOrderDetailStatus;
import com.github.wxiaoqi.security.common.exception.BaseException;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.mapper.front.FrontUserInfoMapper;
import com.github.wxiaoqi.security.common.mapper.front.FrontUserMapper;
import com.github.wxiaoqi.security.common.mapper.front.GeneratorIdMapper;
import com.github.wxiaoqi.security.common.mapper.front.UserLoginLogMapper;
import com.github.wxiaoqi.security.common.mapper.ud.*;
import com.github.wxiaoqi.security.common.util.ExceptionUtil;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.LocalDateUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.github.wxiaoqi.security.common.util.merchant.GenerateCodeUtil;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import com.udax.front.biz.CacheBiz;
import com.udax.front.biz.DcAssertAccountBiz;
import com.udax.front.service.ServiceUtil;
import com.udax.front.util.CacheBizUtil;
import com.udax.front.vo.FrontUserRegisterVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
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
public class HUserInfoBiz extends BaseBiz<HUserInfoMapper, HUserInfo> {


    @Autowired
    private HCommissionRelationMapper commissionRelationMapper;
    @Autowired
    private FrontUserMapper frontUserMapper;

    @Autowired
    private FrontUserInfoMapper frontUserInfoMapper;

    @Autowired
    private HUserInfoMapper hUserInfoMapper;//ud设置用户信息


    @Autowired
    private GeneratorIdMapper generatorIdMapper;


    @Autowired
    private CacheBiz cacheBiz;


    @Autowired
    private HOrderDetailMapper orderDetailMapper;

    @Autowired
    private DcAssertAccountBiz assertAccountBiz;


    @Autowired
    private HQueueMapper queueMapper;

    @Autowired
    private HParamBiz paramBiz;

    @Autowired
    private HUnLockDetailMapper unLockDetailMapper;

    @Transactional(rollbackFor = Exception.class)
    public void lockUser(HUserInfo userInfo,Long expire){
        //匹配中的队列
        HQueue queueParam = new HQueue();
        queueParam.setStatus(QueueStatus.WAIT_MATCH.value());
        queueParam.setUserId(userInfo.getUserId());
        int count = queueMapper.selectCount(queueParam);
        if(count > 0){
            return;
        }
        //匹配上,运行中的订单
        HOrderDetail orderDetailParam = new HOrderDetail();
        orderDetailParam.setStatus(UDOrderDetailStatus.INIT.value());
        orderDetailParam.setUserId(userInfo.getUserId());
        count = orderDetailMapper.selectCount(orderDetailParam);
        if(count > 0){
            return;
        }


        PageHelper.startPage(1,1);
        Example example = new Example(HUnLockDetail.class);
        example.createCriteria().andEqualTo("userId",BaseContextHandler.getUserID());
        example.setOrderByClause("id desc");
        List<HUnLockDetail> unLockDetailList = unLockDetailMapper.selectByExample(example);
        if(StringUtil.listIsNotBlank(unLockDetailList)){
            Date unlockTime = unLockDetailList.get(0).getCreateTime();
            Long days = expire - (LocalDate.now().toEpochDay() - LocalDateUtil.date2LocalDate(unlockTime).toEpochDay());
            //如果超出时间
            if(days.intValue() <= 0){
                //锁定用户
                HUserInfo updateUserParam = new HUserInfo();
                updateUserParam.setId(userInfo.getId());
                updateUserParam.setStatus(EnableType.DISABLE.value());
                hUserInfoMapper.updateByPrimaryKeySelective(updateUserParam);
            }
        }

    }


    /**
     * 通过用户名查询用户,需要匹配邮箱或者手机号
     * @return
     */
    public FrontUser selectUDUnionUserInfoByUserName(String userName){
        if(StringUtils.isBlank(userName)) {
            return null;
        }

//        Example example = new Example(FrontUser.class);//实例化
//        //等价于 (  .. or ..or)
//        Example.Criteria criteria = example.createCriteria();
//        criteria.orEqualTo("email","");
//        criteria.orEqualTo("mobile","");
//        criteria.orEqualTo("userName","");
//        List<FrontUser> list = frontUserMapper.selectByExample(example);

        Map<String,Object> param = InstanceUtil.newHashMap("userName",userName);
        FrontUser user = frontUserMapper.selectUDUionUserInfo(param);

        return  user;
    }

    /**
     * 通过ID查询用户,需要匹配邮箱或者手机号
     * @return
     */
    public FrontUser selectUDUnionUserInfoById(Long userId){
        if( userId == null || userId.longValue() < 1) {
            return null;
        }

//        Example example = new Example(FrontUser.class);//实例化
//        //等价于 (  .. or ..or)
//        Example.Criteria criteria = example.createCriteria();
//        criteria.orEqualTo("email","");
//        criteria.orEqualTo("mobile","");
//        criteria.orEqualTo("userName","");
//        List<FrontUser> list = frontUserMapper.selectByExample(example);
        Map<String,Object> param = InstanceUtil.newHashMap("userId",userId);
        FrontUser user = frontUserMapper.selectUDUionUserInfo(param);

        return  user;
    }

    //ud社区注册
    @Transactional(rollbackFor = Exception.class)
    public void registerForUd(FrontUserRegisterVo vo){
        try {
            HUserInfo parentInfo  = new HUserInfo();
            HParam hparam = ServiceUtil.getUdParamByKey("VISIT_CODE",paramBiz);
            //如果是系统邀请码,构建一个默认用户
            if(hparam != null && vo.getRecommondCode().equalsIgnoreCase(hparam.getUdValue())){
                parentInfo.setTopId(0L);
                parentInfo.setUserId(0L);
                parentInfo.setLevel(0);
                parentInfo.setExchangeId(vo.getExId());
            } else {
                HUserInfo parentParam = new HUserInfo();
                parentParam.setVisitCode(vo.getRecommondCode());
                parentInfo  = hUserInfoMapper.selectOne(parentParam);
            }




            if (parentInfo == null) {
                throw new BusinessException("VISITCODE_IS_ILLEGAL");
            }

            HUserInfo hUserInfo = new HUserInfo();
            //如果在钱包中不存在

            //插入一条记录
            FrontUser frontUser = new FrontUser();
            BeanUtils.copyProperties(vo,frontUser);
            if(StringUtils.isNotBlank(vo.getCountryCode())){
                frontUser.setMobile(frontUser.getUserName());//手机注册
            }else{
                frontUser.setEmail(frontUser.getUserName());//邮箱注册
            }
            frontUser.setUserStatus(FrontUserStatus.ACTIVE.value());//默认未激活状态
            frontUser.setUserLevel(1);//用户等级？
            frontUser.setUserType(FrontUserType.USER.value());//用户类型
            frontUser.setUpdateTime(new Date());
            frontUser.setCreateTime(new Date());
            frontUserMapper.insertSelective(frontUser);


            //插入用户信息
            FrontUserInfo userInfo = new FrontUserInfo();
            BeanUtils.copyProperties(vo,userInfo);
            userInfo.setUserId(frontUser.getId());
            userInfo.setCreateTime(new Date());
            userInfo.setVisitCode(generateVisitCode());
            userInfo.setLocationCode(vo.getCountryCode());
            if(StringUtils.isNotBlank(vo.getCountryCode())){
                List<FrontCountry> list =  CacheBizUtil.getFrontCountry(cacheBiz);
                if(StringUtil.listIsNotBlank(list)){
                    for(FrontCountry f : list){
                        if(f.getCountryCode().equals(vo.getCountryCode())){
                            userInfo.setLocationCountry(f.getCode());
                        }
                    };
                }
            }

            if(parentInfo.getTopId().intValue() == 0 ){
                parentInfo.setTopId(frontUser.getId());
            }

            userInfo.setTopId(parentInfo.getTopId());
            userInfo.setParentId(parentInfo.getUserId());
            userInfo.setLevel(parentInfo.getLevel()+1);
            userInfo.setExchangeId(parentInfo.getExchangeId());
            userInfo.setIsValid(ValidType.NO_AUTH.value());
            if(StringUtils.isNotBlank(vo.getCountryCode())){
                userInfo.setIsValidPhone(VerificationType.OPEN.value());//默认开启验证
                userInfo.setIsValidEmail(VerificationType.CLOSE.value());//默认关闭验证
            }else{
                userInfo.setIsValidPhone(VerificationType.CLOSE.value());//默认关闭验证
                userInfo.setIsValidEmail(VerificationType.OPEN.value());//默认开启验证
            }
            frontUserInfoMapper.insertSelective(userInfo);


            BeanUtils.copyProperties(userInfo,hUserInfo);
            hUserInfo.setId(null);
            hUserInfo.setIsValid(EnableType.DISABLE.value());
            hUserInfo.setStatus(EnableType.DISABLE.value());//默认锁定状态
            hUserInfoMapper.insertSelective(hUserInfo);

//                //如果用户在钱包存在了
//                BeanUtils.copyProperties(userInfo,hUserInfo);
//                hUserInfo.setCreateTime(new Date());
//                hUserInfo.setRecommondCode(parentInfo.getVisitCode());
//                hUserInfo.setId(null);
//                hUserInfo.setIsValid(EnableType.DISABLE.value());
//                hUserInfo.setTopId(parentInfo.getTopId());
//                hUserInfo.setParentId(parentInfo.getUserId());
//                hUserInfo.setLevel(parentInfo.getLevel()+1);
//                hUserInfo.setExchangeId(parentInfo.getExchangeId());
//                hUserInfo.setStatus(EnableType.DISABLE.value());//默认锁定状态
//                hUserInfoMapper.insertSelective(hUserInfo);



            //批量插入用户和他的所有上级的关系
            List<HCommissionRelation>  relationList= InstanceUtil.newArrayList();
            if(hUserInfo.getParentId() > 0){
                queryParentInfo(hUserInfo.getParentId(),hUserInfo,relationList);
            }
            if(StringUtil.listIsNotBlank(relationList)){
                commissionRelationMapper.insertList(relationList);
            }

            sendAssert(userInfo.getUserId());
        } catch (DuplicateKeyException e) {
            log.error(e.getMessage(),e);
            throw new BaseException("已经存在相同的记录.");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(ExceptionUtil.getStackTraceAsString(e));
        }


    }
    //注册赠送资产
    private void sendAssert(Long userId) throws Exception {
        //代币
        HParam  symbolTwo = ServiceUtil.getUdParamByKey("HOME_SYMBOL_TWO",paramBiz);
        //充值赠送数量
        HParam  regSendAmount = ServiceUtil.getUdParamByKey("REG_SEND_AMOUNT",paramBiz);

        if(symbolTwo == null || StringUtils.isBlank(symbolTwo.getUdValue())){
            return;
        }
        if(regSendAmount == null){
            return;
        }
        BigDecimal sendAmount = new BigDecimal(regSendAmount.getUdValue());

        if(sendAmount.compareTo(BigDecimal.ZERO) <= 0){
            return;

        }
        //增加流水
        AccountAssertLogVo vo = new AccountAssertLogVo();
        vo.setUserId(userId);
        vo.setSymbol(symbolTwo.getUdValue());
        vo.setAmount(sendAmount);//数量
        vo.setChargeSymbol(vo.getSymbol());
        vo.setType(AccountLogType.UD_REG_SEND);
        vo.setTransNo(String.valueOf(IdGenerator.nextId()));//提现流水号
        vo.setRemark(AccountLogType.UD_REG_SEND.name());
        //增加资产
        assertAccountBiz.signUpdateAssert(vo, AccountSignType.ACCOUNT_RECHARGE);
    }


    //循环遍历上级信息
    private  void queryParentInfo(Long parentId,HUserInfo hUserInfo,List<HCommissionRelation> relationList){
        HUserInfo queryParam = new HUserInfo();
        queryParam.setUserId(parentId);
        HUserInfo pUserInfo = hUserInfoMapper.selectOne(queryParam);
        HCommissionRelation relation = new HCommissionRelation();
        relation.setLevel(hUserInfo.getLevel());
        relation.setReceiveLevel(pUserInfo.getLevel());
        relation.setReceiveUserId(pUserInfo.getUserId());
        relation.setUserId(hUserInfo.getUserId());
        relationList.add(relation);
        if(pUserInfo.getParentId().longValue() > 0){
            queryParentInfo(pUserInfo.getParentId(),hUserInfo,relationList);
        }
    }
    //生成邀请码,通过 select for update加行锁来限制
    public String generateVisitCode()  throws Exception{

        GeneratorId id = generatorIdMapper.selectForUpdateByKey(GeneratorIdType.VISIT_CODE.value());
        if( id == null ){
            throw new IllegalArgumentException("邀请码配置没有对应的数据");
        }
        //根据配置生成邀请码
        String visitCode = GenerateCodeUtil.generateCode(id.getV(),id.getIncreLen(),1,GenerateCodeUtil.Data.VISIT_CODE);
        if(StringUtils.isBlank(visitCode)){
            throw new IllegalArgumentException("生成邀请码失败,id:"+id.getV()+",increLen:" + id.getIncreLen());
        }

        Example example = new Example(GeneratorId.class);
        example.createCriteria().andEqualTo("version", id.getVersion()).andEqualTo("k",GeneratorIdType.VISIT_CODE.value());
        GeneratorId record = new GeneratorId();
        record.setVersion(id.getVersion() + 1);
        record.setV(visitCode);
        int result = generatorIdMapper.updateByExampleSelective(record, example);
        //根据版本更新
        if( result == 1 ){
            return visitCode;
        }
        throw new IllegalArgumentException("生成邀请码失败");
    }

}