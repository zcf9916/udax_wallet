package com.udax.front.biz;

import com.alibaba.fastjson.JSON;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.FrontCountry;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.front.FrontUserInfo;
import com.github.wxiaoqi.security.common.entity.front.GeneratorId;
import com.github.wxiaoqi.security.common.entity.ud.HCommissionRelation;
import com.github.wxiaoqi.security.common.entity.ud.HUserInfo;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.exception.BaseException;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.mapper.front.FrontUserInfoMapper;
import com.github.wxiaoqi.security.common.mapper.front.FrontUserMapper;
import com.github.wxiaoqi.security.common.mapper.front.GeneratorIdMapper;
import com.github.wxiaoqi.security.common.mapper.ud.HCommissionRelationMapper;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.support.Assert;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.ExceptionUtil;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.merchant.GenerateCodeUtil;
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

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.github.wxiaoqi.security.common.config.Resources.getMessage;

/**
 * 
 *
 * @author Mr.AG
 * @email 463540703@qq.com
 * @date 2017-12-26 19:43:46
 */
@Service
@Slf4j
public class FrontUserBiz extends BaseBiz<FrontUserMapper,FrontUser> {
    @Autowired
    private FrontUserMapper frontUserMapper;

    @Autowired
    private FrontUserInfoMapper frontUserInfoMapper;

    @Autowired
    private GeneratorIdMapper generatorIdMapper;


    @Autowired
    private HCommissionRelationMapper commissionRelationMapper;
    @Autowired
    private CacheBiz cacheBiz;


    /**
     * 用户认证并通知用户审核人员
     */
    @Transactional(rollbackFor = Exception.class)
    public void authUserInfo(FrontUserInfo frontUserInfo) {
        updateUserInfoByUserId(frontUserInfo);
    }

    /**
     * 賬戶手機或者郵箱綁定
     * @param param
     * @param type
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindAccount(Map<String,Object> param, AccountType type) {
        Long userId = (Long) param.get("userId");
        String account = (String) param.get("account");
        FrontUser user = new FrontUser();
        FrontUserInfo userInfo = new FrontUserInfo();
        if(type == AccountType.EMAIL){
            Assert.email(account);
            user.setId(userId);
            user.setEmail(account);
            user.setUpdateTime(new Date());
            userInfo.setUserId(userId);
            userInfo.setIsWithdraw(EnableType.DISABLE.value());//修改信息后不允许提币
            userInfo.setIsValidEmail(VerificationType.OPEN.value());// 默认开启邮箱验证
        }else if(type == AccountType.MOBILE){
            String countrycode = (String) param.get("countrycode");
            Assert.notNull(countrycode, "COUNTRYCODE");
            Assert.mobile(account);
            String locationCountry = null;
            List<FrontCountry> list =  CacheBizUtil.getFrontCountry(cacheBiz);
            if(StringUtil.listIsNotBlank(list)){
                for(FrontCountry f : list){
                    if(f.getCountryCode().equals(countrycode)){
                        locationCountry = f.getCode();
                    }
                };
            }
            //更新user
            user.setId(userId);
            user.setMobile(account);
            user.setUpdateTime(new Date());
            //更新userInfo
            userInfo.setUserId(userId);
            userInfo.setLocationCode(countrycode);
            userInfo.setLocationCountry(locationCountry);
            userInfo.setIsWithdraw(EnableType.DISABLE.value());//修改信息后不允许提币
            userInfo.setIsValidPhone(VerificationType.OPEN.value());// 默认开启手机验证
        }

        //修改用户表
        frontUserMapper.updateByPrimaryKeySelective(user);

        //修改用户信息表
        updateUserInfoByUserId(userInfo);

    }


    /**
     * 更新用户密码
     * @param user
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTradePwd(FrontUser user) {
        //修改密码
        FrontUser frontUser = new FrontUser();
        frontUser.setTradePwd(user.getTradePwd());
        frontUser.setId(user.getId());
        frontUserMapper.updateByPrimaryKeySelective(frontUser);


        //修改可以提币的权限
        FrontUserInfo frontUserInfo = new FrontUserInfo();
        frontUserInfo.setUserId(user.getId());
        frontUserInfo.setIsWithdraw(EnableType.DISABLE.value());
        updateUserInfoByUserId(frontUserInfo);

    }

    private void updateUserInfoByUserId(FrontUserInfo userInfo){
        //修改可以提币的权限
        Example example = new Example(FrontUserInfo.class);
        example.createCriteria().andEqualTo("userId", userInfo.getUserId());
        frontUserInfoMapper.updateByExampleSelective(userInfo, example);

        cacheAndReturn(userInfo.getUserId());
    }

    /**
     * 更新用户密码
     * @param user
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePwd(FrontUser user) {
        //修改密码
        FrontUser frontUser = new FrontUser();
        frontUser.setUserPwd(user.getUserPwd());
        frontUser.setId(user.getId());
        frontUserMapper.updateByPrimaryKeySelective(frontUser);


        //修改可以提币的权限
        //修改可以提币的权限
        FrontUserInfo frontUserInfo = new FrontUserInfo();
        frontUserInfo.setUserId(user.getId());
        frontUserInfo.setIsWithdraw(EnableType.DISABLE.value());
        updateUserInfoByUserId(frontUserInfo);

    }

    /**
     * 根据userId  把用户不变的信息保存到缓存中
     * @param userId
     * @return
     */
    public FrontUser cacheAndReturn(Long userId) {
        FrontUser fr = frontUserMapper.selectByPrimaryKey(userId);
        if(fr != null){
            FrontUser cacheUser = new FrontUser();
            cacheUser.setUid(fr.getUid());
            cacheUser.setId(fr.getId());
            cacheUser.setUserName(fr.getUserName());
            cacheUser.setEmail(fr.getEmail());
            cacheUser.setMobile(fr.getMobile());
            cacheUser.setRemark(fr.getRemark());


            FrontUserInfo userInfoParam = new FrontUserInfo();
            userInfoParam.setUserId(userId);
            FrontUserInfo  userInfo = frontUserInfoMapper.selectOne(userInfoParam);
            cacheUser.setUserInfo(userInfo);

            CacheUtil.getCache().hset(Constants.CacheServiceType.FRONTUSER+ (fr.getId().intValue()%Constants.REDIS_MAP_BATCH),fr.getId().toString(),JSON.toJSONString(cacheUser));
            if(StringUtils.isNotBlank(fr.getEmail())) {
                CacheUtil.getCache().hset(Constants.CacheServiceType.FRONTUSER+ fr.getEmail(),fr.getEmail(),JSON.toJSONString(cacheUser));
            }
            return cacheUser;
        }
        return null;
    }


    /**
     * 通过用户名查询用户,需要匹配邮箱或者手机号
     * @return
     */
    public FrontUser selectByUsername(String userName){
        if(StringUtils.isBlank(userName)) {
            return null;
        }

        Example example = new Example(FrontUser.class);//实例化
        //等价于 (  .. or ..or)
        Example.Criteria criteria = example.createCriteria();
        criteria.orEqualTo("email",userName);
        criteria.orEqualTo("mobile",userName);
        criteria.orEqualTo("userName",userName);
        List<FrontUser> list = frontUserMapper.selectByExample(example);
        if(StringUtil.listIsBlank(list)){
            return null;
        }
        return  list.get(0);
    }

    /**
     * 通过用户名查询用户,需要匹配邮箱或者手机号
     * @return
     */
    public FrontUser selectUnionUserInfoByUserName(String userName){
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
        FrontUser user = frontUserMapper.selectUnionUserInfo(param);

        return  user;
    }

    /**
     * 通过用户名查询用户,需要匹配邮箱或者手机号
     * @return
     */
    public FrontUser selectUnionUserInfoById(Long userId){
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
        FrontUser user = frontUserMapper.selectUnionUserInfo(param);

        return  user;
    }



    @Transactional(rollbackFor = Exception.class)
    public Long register(FrontUserRegisterVo vo){
        try {

            FrontUserInfo parentInfo = null;
            if(StringUtils.isNotBlank(vo.getRecommondCode())) {
                FrontUserInfo parentParam = new FrontUserInfo();
                parentParam.setVisitCode(vo.getRecommondCode());
                parentInfo = frontUserInfoMapper.selectOne(parentParam);
                if (parentInfo == null) {
                    throw new BusinessException("VISITCODE_IS_ILLEGAL");
                }
            }


            //record.setVisitCode(generateVisitCode());
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




            //如果填写邀请码进来的
            if(parentInfo != null){
                //根据父亲的leveCOde+自己的id生成levelCode,
               // userInfo.setLevelCode(parentInfo.getLevelCode() + Constants.USER_LEVELCODE_SPILT + parentInfo.getId());
                userInfo.setTopId(parentInfo.getTopId());
                userInfo.setParentId(parentInfo.getUserId());
                userInfo.setLevel(parentInfo.getLevel()+1);
                userInfo.setExchangeId(parentInfo.getExchangeId());
            }else {
                //如果没有父类,用自己的Id当作层级码
                //userInfo.setLevelCode(Constants.USER_LEVELCODE_SPILT + frontUser.getId());
                userInfo.setTopId(frontUser.getId());
                userInfo.setLevel(1);
                userInfo.setExchangeId(vo.getExId());
                userInfo.setParentId(0L);
            }
            userInfo.setIsValid(ValidType.NO_AUTH.value());

            if(StringUtils.isNotBlank(vo.getCountryCode())){
                userInfo.setIsValidPhone(VerificationType.OPEN.value());//默认开启验证
                userInfo.setIsValidEmail(VerificationType.CLOSE.value());//默认关闭验证
            }else{
                userInfo.setIsValidPhone(VerificationType.CLOSE.value());//默认关闭验证
                userInfo.setIsValidEmail(VerificationType.OPEN.value());//默认开启验证
            }
            frontUserInfoMapper.insertSelective(userInfo);


            //批量插入用户和他的所有上级的关系
            List<HCommissionRelation>  relationList= InstanceUtil.newArrayList();
            if(userInfo.getParentId() > 0){
                queryParentInfo(userInfo,userInfo.getParentId(),relationList);
            }
            if(StringUtil.listIsNotBlank(relationList)){
                commissionRelationMapper.insertList(relationList);
            }



            return frontUser.getId();
        } catch (DuplicateKeyException e) {
            log.error(e.getMessage(),e);
            throw new BaseException("已经存在相同的记录.");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(ExceptionUtil.getStackTraceAsString(e));
        }
    }
    //循环遍历上级信息
    private  void queryParentInfo(FrontUserInfo userInfo,Long parentId, List<HCommissionRelation> relationList){
        FrontUserInfo queryParam = new FrontUserInfo();
        queryParam.setUserId(parentId);
        FrontUserInfo pUserInfo = frontUserInfoMapper.selectOne(queryParam);
        HCommissionRelation relation = new HCommissionRelation();
        relation.setLevel(userInfo.getLevel());
        relation.setReceiveLevel(pUserInfo.getLevel());
        relation.setReceiveUserId(pUserInfo.getUserId());
        relation.setUserId(userInfo.getUserId());
        relationList.add(relation);
        if(pUserInfo.getParentId().longValue() > 0){
            queryParentInfo(userInfo,pUserInfo.getParentId(),relationList);
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