package com.github.wxiaoqi.security.admin.rest.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.security.admin.biz.base.BlockchainWithdrawBiz;
import com.github.wxiaoqi.security.admin.config.RequestUrlConfig;
import com.github.wxiaoqi.security.admin.vo.BlockchainReqVo;
import com.github.wxiaoqi.security.admin.vo.reqvo.*;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.WhiteExchInfo;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.HttpUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/blockchain")
public class BlockchainWithdrawController {

    @Autowired
    private RequestUrlConfig requestUrlConfig;

    protected Logger logger = LogManager.getLogger();


    @Autowired
    private BlockchainWithdrawBiz blockchainWithdrawBiz;


    //调用区块链币种列表
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Map<String, Object> getBlockcchainSymbol(BlockchainReqVo vo) {
        String data = getBlockchainData(vo, Constants.GET_WALLET_TYPE);
        return JSON.parseObject(data, Map.class);
    }

    //币种地址列表
    @RequestMapping(value = "/listAddress", method = RequestMethod.GET)
    public Map<String, Object> getAddressList(BlockchainReqVo vo) {
        String data = getBlockchainData(vo, Constants.GET_WITHDRAW_WALLET);
        return JSON.parseObject(data, Map.class);
    }

    //待提币列表
    @RequestMapping(value = "/waitWithdraw", method = RequestMethod.GET)
    public Map<String, Object> getWaitWithdrawList(BlockchainReqVo vo) {
        String data = getBlockchainData(vo, Constants.GET_WALLET_USER_WITHDRAW);
        return JSON.parseObject(data, Map.class);
    }

    //出人金记录查询
    @RequestMapping(value = "/record", method = RequestMethod.GET)
    public recordVo getRecordList(BlockchainReqVo vo) {
        String data = getBlockchainData(vo, Constants.GET_WALLET_TRANSACTION);
        return JSON.parseObject(data, recordVo.class);
    }

    //提币请求接口
    @RequestMapping(value = "/withdrawReqVo", method = RequestMethod.POST)
    public ObjectRestResponse<WithdrawReqVo> update(@RequestBody WithdrawReqVo entity) {
        blockchainWithdrawBiz.withdraw(entity);
        return new ObjectRestResponse<>();
    }


    //添加提币地址
    @RequestMapping(value = "/importAddress", method = RequestMethod.POST)
    public ObjectRestResponse<importAddressVo> importAddress(@RequestBody importAddressVo entity) {
        blockchainWithdrawBiz.importAddress(entity);
        return new ObjectRestResponse<importAddressVo>();
    }


    // 查询汇聚地址
    @RequestMapping(value = "/convergence", method = RequestMethod.GET)
    public Map<String, Object> getConvergenceList(BlockchainReqVo vo) {
        String data = getBlockchainData(vo, Constants.GET_WALLET_PUBLIC_CONF);
        return JSON.parseObject(data, Map.class);
    }


    //汇聚地址的新增 更新 删除
    @RequestMapping(value = "/addConvergence", method = RequestMethod.POST)
    public ObjectRestResponse<WalletPublicConfVo> addOrUpdateOrDeleteConvergence(@RequestBody WalletPublicConfVo vo) {
        blockchainWithdrawBiz.addOrUpdateOrDeleteConvergenceAddress(vo);
        return new ObjectRestResponse<WalletPublicConfVo>();
    }


    // 查询手续费地址列表
    @RequestMapping(value = "/feeConfAddress", method = RequestMethod.GET)
    public Map<String, Object> feeConfAddress(BlockchainReqVo vo) {
        String data = getBlockchainData(vo, Constants.GET_WALLET_FEE_CONF);
        return JSON.parseObject(data, Map.class);
    }

    //汇聚地址的新增 更新 删除
    @RequestMapping(value = "/feeConfAddressAdd", method = RequestMethod.POST)
    public ObjectRestResponse<WalletFeeConfVo> addOrUpdateOrDeleteFeeConfAddressAdd(@RequestBody WalletFeeConfVo vo) {
        blockchainWithdrawBiz.addOrUpdateOrDeleteFeeConfAddressAdd(vo);
        return new ObjectRestResponse<WalletFeeConfVo>();
    }


    //过滤条件统一处理
    private String getBlockchainData(BlockchainReqVo vo, String getWithdrawWallet) {
        String url = requestUrlConfig.getBlockchainUrl() +
                getWithdrawWallet + "&limit=" + vo.getLimit() + "&page=" + vo.getPage() + "&platform=2";
        if (!StringUtils.isEmpty(vo.getTransNo())) {
            url = url + "&transNo=" + vo.getTransNo();
        }
        if (vo.getStep() != null && vo.getStep() > AdminCommonConstant.ROOT) {
            url = url + "&step=" + vo.getStep();
        }
        if (vo.getAutoWithdraw() != null && vo.getAutoWithdraw() > AdminCommonConstant.ROOT) {
            url = url + "&autoWithdraw=" + vo.getAutoWithdraw();
        }
        if (!StringUtils.isEmpty(vo.getSymbol())) {
            url = url + "&symbol=" + vo.getSymbol();
        }
        if (!StringUtils.isEmpty(vo.getBusiness())) {
            url = url + "&business=" + vo.getBusiness();
        }
        //页面高级查询条件
        if (!StringUtils.isEmpty(vo.getProxyCode())) {
            url = url + "&proxyCode=" + vo.getProxyCode();
        }
        if (vo.getStatus() != null) {
            url = url + "&status=" + vo.getStatus();
        }
        //不是管理员才带此条件
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            WhiteExchInfo info = (WhiteExchInfo) CacheUtil.getCache().get(Constants.CacheServiceType.WHITE_EXCH_INFO + BaseContextHandler.getExId());
            url = url + "&proxyCode=" + info.getDomainName();
        }
        String jsonData = HttpUtils.get(url);
        if ("error".equals(jsonData) || StringUtils.isEmpty(jsonData)) {
            logger.error("区块链调用异常信息:==========>"+JSONObject.parse(jsonData));
            throw new UserInvalidException(Resources.getMessage("BLOCKCHAIN"));
        }
        JSONObject jsonObject = JSON.parseObject(jsonData);
        return jsonObject.getString("data");
    }
}
