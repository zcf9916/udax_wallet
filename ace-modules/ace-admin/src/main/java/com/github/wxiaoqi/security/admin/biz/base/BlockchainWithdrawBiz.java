package com.github.wxiaoqi.security.admin.biz.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.security.admin.config.RequestUrlConfig;
import com.github.wxiaoqi.security.admin.vo.reqvo.*;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.configuration.KeyConfiguration;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.WhiteExchInfo;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.HttpUtils;
import com.github.wxiaoqi.security.common.util.SecurityUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockchainWithdrawBiz {

     Logger logger = LogManager.getLogger();

    @Autowired
    private RequestUrlConfig requestUrlConfig;

    @Autowired
    private KeyConfiguration keyConfiguration;

    //区块链提币申请
    public void withdraw(WithdrawReqVo entity) {
        String url = requestUrlConfig.getBlockchainUrl() + Constants.USER_TRANSACTION+"?platform=2";
        String toJSON = JSON.toJSONString(entity);
        toJSON = SecurityUtil
                .encryptBASE64(SecurityUtil.encryptDes(toJSON, keyConfiguration.getTokenKey().getBytes()).getBytes());
        String returnCode = HttpUtils.postJsonString(url, toJSON);
        handlingException(returnCode);
    }

    //区块链导入提币地址
    public void importAddress(importAddressVo entity) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            WhiteExchInfo  info = (WhiteExchInfo) CacheUtil.getCache().get(Constants.CacheServiceType.WHITE_EXCH_INFO + BaseContextHandler.getExId());
            entity.setProxyCode(info.getDomainName());
        }else {
            if (StringUtils.isEmpty(entity.getProxyCode())){
                throw new UserInvalidException(Resources.getMessage("CONFIG_EXCH_ID"));
            }
        }
        String url = requestUrlConfig.getBlockchainUrl() + Constants.IMPORT_ADDRESS+"?platform=2";
        List<addressVo> addressList = entity.getAddressList();
        addressVo vo = new addressVo();
        vo.setAddress(entity.getAddress());
        vo.setPrivkey(entity.getPrivkey());
        vo.setAutoWithdraw(entity.getAutoWithdraw());
        vo.setPassword(entity.getPassword());
        addressList.add(vo);
        String toJSON = JSON.toJSONString(entity);
        toJSON = SecurityUtil
                .encryptBASE64(SecurityUtil.encryptDes(toJSON, keyConfiguration.getTokenKey().getBytes()).getBytes());
        String returnCode = HttpUtils.postJsonString(url, toJSON);
        handlingException(returnCode);
    }

    public void addOrUpdateOrDeleteConvergenceAddress(WalletPublicConfVo vo) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            WhiteExchInfo  info = (WhiteExchInfo) CacheUtil.getCache().get(Constants.CacheServiceType.WHITE_EXCH_INFO + BaseContextHandler.getExId());
            vo.setProxyCode(info.getDomainName());
        }else {
            if (StringUtils.isEmpty(vo.getProxyCode())){
                throw new UserInvalidException(Resources.getMessage("CONFIG_EXCH_ID"));
            }
        }
        String url = requestUrlConfig.getBlockchainUrl() + Constants.SET_WALLET_PUBLIC_CONF+"platform=2";
        String toJSON = JSON.toJSONString(vo);
        toJSON = SecurityUtil
                .encryptBASE64(SecurityUtil.encryptDes(toJSON, keyConfiguration.getTokenKey().getBytes()).getBytes());
        String returnCode = HttpUtils.postJsonString(url, toJSON);
        handlingException(returnCode);
    }

    public void addOrUpdateOrDeleteFeeConfAddressAdd(WalletFeeConfVo vo) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            WhiteExchInfo  info = (WhiteExchInfo) CacheUtil.getCache().get(Constants.CacheServiceType.WHITE_EXCH_INFO + BaseContextHandler.getExId());
            vo.setProxyCode(info.getDomainName());
        }else {
            if (StringUtils.isEmpty(vo.getProxyCode())){
                throw new UserInvalidException(Resources.getMessage("CONFIG_EXCH_ID"));
            }
        }
        String url = requestUrlConfig.getBlockchainUrl() + Constants.SET_WALLET_FEE_CONF+"platform=2";
        String toJSON = JSON.toJSONString(vo);
        toJSON = SecurityUtil
                .encryptBASE64(SecurityUtil.encryptDes(toJSON, keyConfiguration.getTokenKey().getBytes()).getBytes());
        String returnCode = HttpUtils.postJsonString(url, toJSON);
        handlingException(returnCode);
    }

    private void handlingException(String returnCode) {
        JSONObject jsonObject = JSON.parseObject(returnCode);
        if (!"0".equals(jsonObject.getString("code"))) {
            logger.error("区块链调用异常信息:==========>"+ jsonObject);
            throw new UserInvalidException(Resources.getMessage("BLOCKCHAIN"));
        }
    }
}
