package com.udax.front.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.security.common.configuration.KeyConfiguration;
import com.github.wxiaoqi.security.common.entity.front.FrontRecharge;
import com.github.wxiaoqi.security.common.entity.front.FrontTokenAddress;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.front.FrontWithdraw;
import com.github.wxiaoqi.security.common.entity.merchant.MchNotify;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.task.CallbackMsg;
import com.github.wxiaoqi.security.common.util.*;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.udax.front.bean.BlockChainBean;
import com.udax.front.biz.*;
import com.udax.front.util.CacheBizUtil;
import com.udax.front.vo.rspvo.BlockchainWithdrawVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

import static com.github.wxiaoqi.security.common.constant.Constants.MCH_CALLBACK_TASK;

/**
 * @author liuzz
 * @create 2018／3／12
 */
@RestController
@RequestMapping("/wallet/blockchain")
@Slf4j
public class BlockchainController extends BaseFrontController<BlockChainBiz,BlockChainBiz> {

    @Autowired
    private KeyConfiguration config;

    @Autowired
    private FrontWithdrawBiz frontWithdrawBiz;

    @Autowired
    private FrontUserBiz frontUserBiz;

    @Autowired
    private FrontRechargeBiz frontRechargeBiz;

    /**
     * 前置判断，区块链IP是否与配置文件中一致
     */
    @ModelAttribute
    private boolean isBlockchain(HttpServletRequest request) {
        //是否测试环境
        if(config.getIfTestEnv()){
            return true;
        }
        String ip = WebUtil.getHost(request);
        if (ip.indexOf(config.getBlockchainIp()) != -1) { //TODO 这里需要再KeyConfiguration增加IP参数，区块链平台IP需要进行限制
            return true;
        }
        throw new BusinessException(MchResponseCode.MERCHANT_INVALID_IP.name());
    }

    /**
     * 区块链平台推送代币地址
     * @param blockChainBean
     * @return
     */
    @PostMapping("users")
    public BlockchainWithdrawVo<String> receiveTokenUsers(@RequestBody BlockChainBean blockChainBean) {
        if (null != blockChainBean) {
            List<String> addressList = blockChainBean.getUserAddress();
            String symbol = blockChainBean.getSymbol();
            String exchMark = blockChainBean.getProxyCode();//白标标识
            log.info("接收区块链平台推送后的批量用户地址信息：" + addressList);
            // List<String> addList= JSON.parseArray(addressList,String.class);
            for (String address : addressList) {
                FrontTokenAddress tokenAddress = new FrontTokenAddress();
                tokenAddress.setSymbol(symbol);
                tokenAddress.setUserAddress(SecurityUtil.encryptDes(
                        SecurityUtil.decryptDes(new String(SecurityUtil.decryptBASE64(address)), config.getTokenKey().getBytes()),
                        config.getWalletKey().getBytes()));
                tokenAddress.setEnable(EnableType.DISABLE.value());// 未使用
                tokenAddress.setCreateTime(new Date());
                tokenAddress.setType(FrontUserType.USER.value());
                tokenAddress.setProxyCode(exchMark);
                baseBiz.insert(tokenAddress);
            }
        }
        return new BlockchainWithdrawVo<String>().rel(true);
    }

    /**
     * 充币到账接口
     *
     * @param info
     * @return
     */
    @PostMapping("recharge")
    public BlockchainWithdrawVo<String> recharge(@RequestParam(name = "info", required = true) String info)  throws Exception{
        log.info("充值提现到账信息：" + info);
        info = SecurityUtil.decryptDes(new String(SecurityUtil.decryptBASE64(info)), config.getTokenKey().getBytes());
        JSONObject recharge = JSON.parseObject(info);
        String tokenAddress = recharge.getString("token_address");
        String transNo = recharge.getString("transNo");
        String proxyCode = recharge.getString("proxyCode");
        String protocolType = recharge.getString("protocolType");
        JSONObject currentBlance = recharge.getJSONObject("recharge_amount");
        BlockchainWithdrawVo<String> restResponse = new BlockchainWithdrawVo<String>();
        Set<Entry<String, Object>> set = currentBlance.entrySet();
        Iterator<Entry<String, Object>> iter = set.iterator();
        while (iter.hasNext()) {
            Entry<String, Object> entry = iter.next();
            String symbol = entry.getKey();
            BigDecimal rechargeAmount = (BigDecimal) entry.getValue();
            if (rechargeAmount.compareTo(BigDecimal.ZERO) > 0) {
                JSONObject fee = recharge.getJSONObject("fee");
                FrontRecharge frontRecharge = new FrontRecharge();
                frontRecharge.setSymbol(symbol);
                frontRecharge.setRechargeAmount(rechargeAmount);
                frontRecharge.setUserAddress(tokenAddress);
                frontRecharge.setProxyCode(proxyCode);
                frontRecharge.setProtocolType(protocolType);
                frontRecharge.setStatus(CommonStatus.SUCC.value());// 充值成功
                frontRecharge.setBlockOrderId(transNo);
                frontRecharge.setFeeSymbol(fee.getString("symbol"));
                frontRecharge.setRechargeFee(fee.getBigDecimal("amount"));
                FrontTokenAddress tokenUser = new FrontTokenAddress();
                tokenUser.setUserAddress(SecurityUtil.encryptDes(tokenAddress, config.getWalletKey().getBytes()));
                // Wrapper<TokenUser> wrapper = new EntityWrapper<TokenUser>(tokenUser);
                FrontTokenAddress token = baseBiz.selectOne(tokenUser);
                if (token == null) {
                    restResponse.setRel(false);
                    return restResponse;
                }
                Long userId = token.getUserId();
                frontRecharge.setUserId(userId);
                frontRecharge.setOrderId(IdGenerator.nextId());
                frontRecharge.setCreateTime(new Date());
                frontRecharge.setType(token.getType());
                MchNotify notify = baseBiz.addRecharge(frontRecharge);
                if(notify != null){
                    //加入回调队列
                    CallbackMsg msg = new CallbackMsg();
                    BeanUtils.copyProperties(notify,msg);
                    CacheUtil.getCache().setSAdd(MCH_CALLBACK_TASK,msg);
                }
            }
        }
        return new BlockchainWithdrawVo<String>().rel(true);
    }

    /**
     * 区块链平台扫描交易平台获取提现记录
     *
     * @param request
     * @param modelMap
     * @param tokenSymbol
     * @return
     */
    @PostMapping("withdrawReq")
    public BlockchainWithdrawVo<Map<String, String>> withdrawReq(HttpServletRequest request, ModelMap modelMap,
                                                                 @RequestParam(name = "tokenSymbol", required = true) String tokenSymbol) throws Exception {
        BlockchainWithdrawVo<Map<String, String>> result = new BlockchainWithdrawVo<Map<String, String>>().rel(false);
        log.info("---------------区块链平台扫描交易平台获取提现记录:{},host Ip{1}", tokenSymbol, WebUtil.getHost(request));
        FrontWithdraw frontWithdrawals = new FrontWithdraw();
        frontWithdrawals.setBasicSymbol(tokenSymbol);
        frontWithdrawals.setStatus(FrontWithdrawStatus.Audited.value());// 后台已审核
        List<FrontWithdraw> withdrawList = (List<FrontWithdraw>) baseBiz.updateWithdrawStatus(frontWithdrawals);
        if (withdrawList != null && !withdrawList.isEmpty()) {
            String withDrawJson = JSON.toJSONString(withdrawList);
            withDrawJson = SecurityUtil
                    .encryptBASE64(SecurityUtil.encryptDes(withDrawJson, config.getTokenKey().getBytes()).getBytes());
            result.setData(InstanceUtil.newHashMap("withdrawList", withDrawJson));
        }
        return result;
    }

    /**
     * 提现到账通知
     *
     * @param request
     * @param modelMap
     * @return
     */
    @PostMapping("withdrawCallback")
    public Object withdrawCallback(HttpServletRequest request, ModelMap modelMap,
                                   @RequestParam(name = "info", required = true) String info) throws Exception {
        log.info("提现到账通知处理 :{},host Ip{}" , info, WebUtil.getHost(request));
        BlockchainWithdrawVo<String> result = new BlockchainWithdrawVo<String>().rel(false);
        info = SecurityUtil.decryptDes(new String(SecurityUtil.decryptBASE64(info)), config.getTokenKey().getBytes());
        JSONObject withdrawJson = JSON.parseObject(info);
        String transNo = withdrawJson.getString("transNo");
        String userAddress = withdrawJson.getString("userAddress");
        String status = withdrawJson.getString("status");
        System.out.println("tranNo:=======================>"+transNo);
        System.out.println("status:=======================>"+status);
        if (StringUtils.isNotEmpty(status)) {
            FrontWithdraw frontWithdraw = new FrontWithdraw();
            frontWithdraw.setTransNo(transNo);
            frontWithdraw.setUserAddress(userAddress);
            frontWithdraw.setStatus(FrontWithdrawStatus.WITHDRAWSEND.value());// 区块链已扫描提现信息
            FrontWithdraw withdraw = baseBiz.queryByWithdraw(frontWithdraw);
            if (withdraw != null) {
                String sync_time = withdrawJson.getString("sync_time");
                withdraw.setUpdateTime(DateUtil.stringToDate(sync_time));
                JSONObject fee = withdrawJson.getJSONObject("fee");
                withdraw.setFeeSymbol(fee.getString("symbol"));
                withdraw.setWithdrawFee(fee.getBigDecimal("amount"));
                // withdraw.setAccountId(AccountType.COIN.value());
                if (status.equals("6")) {  //更新区块确认数
                    Integer confirmations = withdrawJson.getInteger("confirmations");
                    withdraw.setConfirmations(confirmations);
                    frontWithdrawBiz.updateById(withdraw);
                    result.setRel(true);
                    return result;
                } else if (status.equals("7")) { // 提现成功
                    String transactionId = withdrawJson.getString("transactionId");
                    withdraw.setTransactionId(transactionId);
                    withdraw.setStatus(FrontWithdrawStatus.TransSuccess.value());// 已提现
                } else if (status.equals("8")) {// 转账失败 --->提现状态更新为待审核
                    withdraw.setStatus(FrontWithdrawStatus.WaitAuto.value());// 区块链返回提现失败
                    frontWithdrawBiz.updateById(withdraw);
                    FrontUser frontUser = frontUserBiz.selectUnionUserInfoById(withdraw.getUserId());
                    //向管理员发送提币审核钉钉群消息通知
                    SendUtil.noticeManager(EmailTemplateType.WITHDRAW_AUDIT_ERROR.value(),frontUser.getUserName());
                    result.setRel(true);
                    return request;
                }
                MchNotify notify = baseBiz.updateWithdraw(withdraw); // 更新提币状态，扣除冻结资产
                if(notify != null){
                    //加入回调队列
                    CallbackMsg msg = new CallbackMsg();
                    BeanUtils.copyProperties(notify,msg);
                    CacheUtil.getCache().setSAdd(MCH_CALLBACK_TASK,msg);
                }
                result.setRel(true);
            }
        }
        return result;
    }
    /**
     * 充值汇聚 手续费通知接口
     *
     * @param request
     * @param modelMap
     * @return
     */
    @PostMapping("rechargeFee")
    public Object rechargeFee(HttpServletRequest request, ModelMap modelMap,
                              @RequestParam(name = "info", required = true) String info) throws Exception {
        log.info("多笔充值汇聚 手续费通知接口 :{},host Ip{}" , info, WebUtil.getHost(request));
        BlockchainWithdrawVo<String> result = new BlockchainWithdrawVo<String>().rel(false);
        info = SecurityUtil.decryptDes(new String(SecurityUtil.decryptBASE64(info)), config.getTokenKey().getBytes());
        JSONObject withdrawJson = JSON.parseObject(info);
        String transNo = withdrawJson.getString("transNo");
        String userAddress = withdrawJson.getString("token_address");
        String symbol = withdrawJson.getString("symbol");
        //交易所标识
        String proxyCode = withdrawJson.getString("proxyCode");
        FrontRecharge frontRecharge = new FrontRecharge();
        frontRecharge.setBlockOrderId(transNo);
        frontRecharge.setUserAddress(userAddress);
        frontRecharge.setProxyCode(proxyCode);
        frontRecharge.setSymbol(symbol);
        FrontRecharge recharge = frontRechargeBiz.selectOne(frontRecharge);
        if (recharge !=null){
            JSONObject fee = withdrawJson.getJSONObject("fee");
            recharge.setRechargeFee(fee.getBigDecimal("amount"));
            recharge.setFeeSymbol(fee.getString("symbol"));
            frontRechargeBiz.updateById(recharge);
            result.setRel(true);
            return result;
        }
        return result;
    }
}
