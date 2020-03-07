package com.udax.front.biz.casino;

import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.FrontCountry;
import com.github.wxiaoqi.security.common.entity.casino.CasinoParam;
import com.github.wxiaoqi.security.common.entity.casino.CasinoUserInfo;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.front.FrontUserInfo;
import com.github.wxiaoqi.security.common.entity.front.GeneratorId;
import com.github.wxiaoqi.security.common.entity.ud.*;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.enums.ud.QueueStatus;
import com.github.wxiaoqi.security.common.enums.ud.UDOrderDetailStatus;
import com.github.wxiaoqi.security.common.exception.BaseException;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.mapper.casino.CasinoParamMapper;
import com.github.wxiaoqi.security.common.mapper.casino.CasinoUserInfoMapper;
import com.github.wxiaoqi.security.common.mapper.front.FrontUserInfoMapper;
import com.github.wxiaoqi.security.common.mapper.front.FrontUserMapper;
import com.github.wxiaoqi.security.common.mapper.front.GeneratorIdMapper;
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
import com.udax.front.biz.ud.HParamBiz;
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
public class CasinoUserInfoBiz extends BaseBiz<CasinoUserInfoMapper, CasinoUserInfo> {


    @Autowired
    private HCommissionRelationMapper commissionRelationMapper;
    @Autowired
    private FrontUserMapper frontUserMapper;

    @Autowired
    private FrontUserInfoMapper frontUserInfoMapper;


    @Autowired
    private GeneratorIdMapper generatorIdMapper;

    @Autowired
    private CasinoParamMapper paramMapper;

    @Autowired
    private CacheBiz cacheBiz;


    /**
     * 通过用户名查询用户,需要匹配邮箱或者手机号
     * @return
     */
    public FrontUser selectCasinoUnionUserInfoByUserName(String userName){
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
        FrontUser user = frontUserMapper.selectCasinoUionUserInfo(param);

        return  user;
    }

    /**
     * 通过ID查询用户,需要匹配邮箱或者手机号
     * @return
     */
    public FrontUser selectCasinoUnionUserInfoById(Long userId){
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
        FrontUser user = frontUserMapper.selectCasinoUionUserInfo(param);

        return  user;
    }

    //ud社区注册
    @Transactional(rollbackFor = Exception.class)
    public void registerForUd(FrontUserRegisterVo vo){
        try {
            CasinoUserInfo parentInfo = new CasinoUserInfo();
//            CasinoParam param = new CasinoParam();
//            param.setCasinoKey("activate_member");
//            param.setExchId(vo.getExId());
//            CasinoParam casinoParam = paramMapper.selectOne(param);
            String visitCode =  CacheBizUtil.getCasinoParam(Constants.CasinoParam.VISIT_CODE,cacheBiz,BaseContextHandler.getAppExId());
            //如果是系统邀请码,构建一个默认用户
            if(StringUtils.isNotBlank(visitCode) && vo.getRecommondCode().equalsIgnoreCase(visitCode)){
                parentInfo.setTopId(0L);
                parentInfo.setUserId(0L);
                parentInfo.setLevel(0);
                parentInfo.setExchangeId(vo.getExId());
            } else {
                CasinoUserInfo parentParam = new CasinoUserInfo();
                parentParam.setVisitCode(vo.getRecommondCode());
                parentInfo = mapper.selectOne(parentParam);

            }
            if (parentInfo == null) {
                throw new BusinessException("VISITCODE_IS_ILLEGAL");
            }

            CasinoUserInfo casinoUserInfo = new CasinoUserInfo();
            //如果在钱包中不存在

            //插入一条记录
            FrontUser frontUser = new FrontUser();
            BeanUtils.copyProperties(vo,frontUser);
            if(StringUtils.isNotBlank(vo.getCountryCode())){
                frontUser.setMobile(frontUser.getUserName());//手机注册
            }else{
                frontUser.setEmail(frontUser.getUserName());//邮箱注册
            }
            frontUser.setUserStatus(FrontUserStatus.ACTIVE.value());//默认激活状态
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


            BeanUtils.copyProperties(userInfo,casinoUserInfo);
            casinoUserInfo.setId(null);
            casinoUserInfo.setIsValid(EnableType.DISABLE.value());
            casinoUserInfo.setType(CasinoUserInfo.CasinoUserInfoType.NORMAL.value());//角色类型
            casinoUserInfo.setSettleType(CasinoUserInfo.SettleType.RATIO.value());//结算类型
            casinoUserInfo.setStatus(EnableType.DISABLE.value());//默认不享有收益
            mapper.insertSelective(casinoUserInfo);


            //批量插入用户和他的所有上级的关系
            List<HCommissionRelation>  relationList= InstanceUtil.newArrayList();
            if(casinoUserInfo.getParentId() > 0){
                queryParentInfo(casinoUserInfo.getParentId(),casinoUserInfo,relationList);
            }
            if(StringUtil.listIsNotBlank(relationList)){
                commissionRelationMapper.insertList(relationList);
            }


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
    private  void queryParentInfo(Long parentId,CasinoUserInfo casinoUserInfo,List<HCommissionRelation> relationList){
        CasinoUserInfo queryParam = new CasinoUserInfo();
        queryParam.setUserId(parentId);
        CasinoUserInfo pUserInfo = mapper.selectOne(queryParam);
        HCommissionRelation relation = new HCommissionRelation();
        relation.setLevel(casinoUserInfo.getLevel());
        relation.setUserId(casinoUserInfo.getUserId());
        relation.setReceiveLevel(pUserInfo.getLevel());
        relation.setReceiveUserId(pUserInfo.getUserId());
        relationList.add(relation);
        if(pUserInfo.getParentId().longValue() > 0){
            queryParentInfo(pUserInfo.getParentId(),casinoUserInfo,relationList);
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