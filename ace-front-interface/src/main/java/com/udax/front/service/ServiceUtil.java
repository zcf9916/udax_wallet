package com.udax.front.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.alibaba.fastjson.JSON;
import com.github.wxiaoqi.security.common.annotation.Sign;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.BasicSymbol;
import com.github.wxiaoqi.security.common.entity.admin.FrontCountry;
import com.github.wxiaoqi.security.common.entity.admin.FrontFreezeInfo;
import com.github.wxiaoqi.security.common.entity.ud.HParam;
import com.github.wxiaoqi.security.common.entity.ud.HUserInfo;
import com.github.wxiaoqi.security.common.util.*;
import com.github.wxiaoqi.security.common.vo.UdaxLastPricesBean;
import com.udax.front.biz.CacheBiz;
import com.udax.front.biz.ud.HParamBiz;
import com.udax.front.biz.ud.HUserInfoBiz;
import com.udax.front.util.CacheBizUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.enums.FreezeFunctionType;
import com.udax.front.biz.FrontUserBiz;

/**
 * Created by ace on 2017/9/12.
 *
 * 一些经常调用的方法
 */

@Slf4j
public class ServiceUtil {


    //获取代币最新价格(USDT)
    public static BigDecimal getUSDTPrice(String quotationUrl,String symbol){
        BigDecimal lastPrice = null;
        String returnJson = HttpUtils.postJson(quotationUrl,symbol+"-USDT-USDT");
        UdaxLastPricesBean jsonBean = JSON.parseObject(returnJson,UdaxLastPricesBean.class);
        //获取交易对的最新价格
        if(jsonBean != null && jsonBean.getCode().intValue() == 200){
            lastPrice = jsonBean.getData();
        }
        return lastPrice;
    }



    /**
     *   选择UD用户信息
     * @return
     */
    public static HUserInfo selectUdUserInfo(HUserInfoBiz userInfoBiz){
        if(BaseContextHandler.getUserID() == null){
            return  null;
        }
        HUserInfo userInfo = new HUserInfo();
        userInfo.setUserId(BaseContextHandler.getUserID());
        return userInfoBiz.selectOne(userInfo);
    }


    /**
     *  获取ud设置的参数
      * @param key
     * @param biz
     * @return
     */
    public static HParam  getUdParamByKey(String key,HParamBiz biz){
        return  getUdParamByKey(key,biz,BaseContextHandler.getAppExId());
    }

    public static HParam  getUdParamByKey(String key,HParamBiz biz,Long exchId){
        HParam param = new HParam();
        param.setUdKey(key);
        param.setExchId(exchId);
        return  biz.selectOne(param);
    }




    //验证countryCode是否合法
    public static boolean valiCountryCode(String countryCode,CacheBiz cacheBiz){
        //获取所有
        List<FrontCountry> list = CacheBizUtil.getFrontCountry(cacheBiz);
        if(StringUtil.listIsBlank(list)){
            return false;
        }
        for (FrontCountry country : list) {
            if( country.getCountryCode().equalsIgnoreCase(countryCode)  && country.getStatus().intValue() == EnableType.ENABLE.value().intValue()){
                return true;
            }
        }
        return false;
    }



    //验证代币是否合法
    public static boolean validDcCode(String dcCode,CacheBiz cacheBiz){
        //获取所有可用币种列表信息
        List<BasicSymbol> symbolList = CacheBizUtil.getBasicSymbol(cacheBiz);
        //List<BasicSymbol> symbolList = CacheBizUtil.getBasicSymbol(cacheBiz);
        if(StringUtil.listIsBlank(symbolList)){
            return false;
        }
        for (BasicSymbol basicSymbol : symbolList) {
            if(basicSymbol.getSymbol().equalsIgnoreCase(dcCode) && basicSymbol.getStatus().intValue() == EnableType.ENABLE.value().intValue()){
                return true;
            }
        }
        return false;
    }


    /**
     *   判断交易密码是否正确
     * @param password 密码
     * @param userId  用户id
     * @param biz
     * @return
     */
    public static  boolean judgePayPassword(String password,Long userId,FrontUserBiz biz){
        if(StringUtils.isBlank(password)){
            return  false;
        }
        //通过用户名查询出用户
        FrontUser frontUser = biz.selectById(userId);
        //如果查不出用户
        if (frontUser == null || StringUtils.isBlank(frontUser.getTradePwd()) ||StringUtils.isBlank(frontUser.getSalt())) {
            return false;
        }
        String encryPwd = SecurityUtil.encryptPassword(password+frontUser.getSalt());
        if (!encryPwd.equals(frontUser.getTradePwd())) {
            return false;
        }
        //密码正确,设置免密失效时间
        //CacheUtil.getCache().set(Constants.CacheServiceType.PWD_EXPIRE + userId.toString(),"0",CacheBizUtil.getPwdExpire() * 60);

        return  true;
    }


    /**
     * 对对象的所有非空字段按字段字母大小顺序并生成签名
     * @param object
     * @param signKey
     * @return
     */
    public static String  macSign(Object object,String signKey) throws  Exception{
        TreeMap<String, Object> params = new TreeMap<String, Object>();
        Class<?> cl = object.getClass();
        Field[] fields = cl.getDeclaredFields();// 获取model的所有字段
        for (Field field : fields) {
            //字段上是否有sign注解
            Sign sign =  field.getAnnotation(Sign.class);
            if(sign == null ){
                continue;
            }
            String getMethodName = "get" + StringUtils.capitalize(field.getName());
            Method method = cl.getDeclaredMethod(getMethodName);
            Object ob = method.invoke(object);
            if (ob == null || (field.getType() == String.class && StringUtils.isBlank((String) ob))) {
                continue;
            }
            if (field.getType() == BigDecimal.class ) {
                params.put(field.getName(), ((BigDecimal)ob).toPlainString());
                continue;
            }
            if (ob != null) {
                // 参数不为空,放到map
                params.put(field.getName(), ob);
            }
        }
        return  VerifySign.signature(params, signKey);
    }


    /**
     * 验证对象签名
     * @param object
     * @param signKey
     * @param signStr  待验证的签名
     * @return
     */
    public static boolean  verifySign(Object object,String signKey,String signStr) throws  Exception{
        TreeMap<String, Object> params = new TreeMap<String, Object>();
        Class<?> cl = object.getClass();
        Field[] fields = cl.getDeclaredFields();// 获取model的所有字段
        for (Field field : fields) {
            //字段上是否有sign注解
            Sign sign =  field.getAnnotation(Sign.class);
            if(sign == null ){
                continue;
            }
            String getMethodName = "get" + StringUtils.capitalize(field.getName());
            Method method = cl.getDeclaredMethod(getMethodName);
            Object ob = method.invoke(object);
            if (ob == null || (field.getType() == String.class && StringUtils.isBlank((String) ob))) {
                continue;
            }
            if (field.getType() == BigDecimal.class ) {
                params.put(field.getName(), ((BigDecimal)ob).toPlainString());
                continue;
            }
            if (ob != null) {
                // 参数不为空,放到map
                params.put(field.getName(), ob);
            }
        }
        return  VerifySign.verify(params,signStr,signKey);
    }



    /**
     * 通过用户名获取UD用户信息所有关联信息
     * @param username
     * @param biz
     * @return
     */
    public static FrontUser selectUDUnionUserInfoByUserName(String username, HUserInfoBiz biz) {
        try{
            FrontUser user = biz.selectUDUnionUserInfoByUserName(username);
            return user;
        }catch(Exception e){
            e.printStackTrace();
            return new FrontUser();
        }
    }



    /**
     * 通过用户ID获取UD用户信息所有关联信息
     * @param userId
     * @param biz
     * @return
     */
    public static FrontUser selectUDUnionUserInfoByID(Long userId, HUserInfoBiz biz) {
        try{
            FrontUser user = biz.selectUDUnionUserInfoById(userId);
            return user;
        }catch(Exception e){
            e.printStackTrace();
            return new FrontUser();
        }
    }


    /**
     * 通过手机或者邮件或者用户名获取用户
     * @return
     */
    public static  FrontUser getUniFrontUserByName(String name, FrontUserBiz biz){
        try{
            FrontUser user = biz.selectByUsername(name);
            return user;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 通过手机或者邮件或者用户名获取用户以及用户关联信息
     * @return
     */
    public static  FrontUser selectUnionUserInfoByUserName(String name, FrontUserBiz biz){
        try{
            FrontUser user = biz.selectUnionUserInfoByUserName(name);
            return user;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }
    
    /**
     * 判断进行的交易功能是否被冻结
     * @param freezeType 进行的交易功能
     * @param userId  用户id
     * @return
     */
	public static boolean checkTradeFunctionIsFreeze(FreezeFunctionType freezeType, Long userId) {
//		 if(null==freezeType||null==userId){
//		     return false;
//		 }
//         ArrayList<FrontFreezeInfo> freezeInfoList = (ArrayList<FrontFreezeInfo>)
//          CacheUtil.getCache().hget(Constants.CacheServiceType.FRONT_FREEZE_INFO + (userId.longValue() %
//         Constants.REDIS_MAP_BATCH),
//         userId.toString());
//         if(freezeInfoList!=null&&!freezeInfoList.isEmpty()) {
//             for(FrontFreezeInfo freezeInfo:freezeInfoList) {
//             if(freezeType.equals(freezeInfo.getFreezeType())) {
//             if(EnableType.DISABLE.value().equals(freezeInfo.getEnable())) {//冻结
//             return false;
//             }
//             }
//              }
//		 }
		return true;
	}

    /**
     * 通过用户Id获取用户以及用户关联信息
     * @return
     */
    public static  FrontUser selectUnionUserInfoById(Long userId, FrontUserBiz biz){
        try{
            FrontUser user = biz.selectUnionUserInfoById(userId);
            return user;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 判断进行的操作功能是否被冻结
     * @param freezeType 进行的操作功能
     * @param userId  用户id
     * @return
     */
    public static boolean checkOperationAuthority(Integer freezeType, Long userId) {
        if(null==freezeType||null==userId){
            return  false;
        }
        ArrayList<FrontFreezeInfo> freezeInfoList = (ArrayList<FrontFreezeInfo>) CacheUtil.getCache().hget(
                Constants.CacheServiceType.FRONT_FREEZE_INFO + (userId.longValue() % Constants.REDIS_MAP_BATCH),
                userId.toString());
//        if(freezeInfoList==null||freezeInfoList.size()<1) {
//            Parameter parameter = new Parameter("frontFreezeInfoService", "cacheAndReturnFreezeInfo", userId);
//            freezeInfoList = (ArrayList<FrontFreezeInfo>) sysProvider.execute(parameter).getResultList();
//        }
        if(StringUtil.listIsNotBlank(freezeInfoList)) {
            for(FrontFreezeInfo freezeInfo:freezeInfoList) {
                if(freezeType.equals(freezeInfo.getFreezeType())) {
                    if(EnableType.DISABLE.value().equals(freezeInfo.getEnable())) {//冻结
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
