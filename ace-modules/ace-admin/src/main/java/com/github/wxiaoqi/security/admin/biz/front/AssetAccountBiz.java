package com.github.wxiaoqi.security.admin.biz.front;

import com.alibaba.fastjson.JSON;
import com.github.wxiaoqi.security.admin.util.verifySignUtil;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.configuration.KeyConfiguration;
import com.github.wxiaoqi.security.common.entity.front.DcAssetAccount;
import com.github.wxiaoqi.security.common.entity.front.DcAssetAccountLog;
import com.github.wxiaoqi.security.common.enums.AccountLogType;
import com.github.wxiaoqi.security.common.enums.AccountSignType;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.front.DcAssetAccountLogMapper;
import com.github.wxiaoqi.security.common.mapper.front.DcAssetAccountMapper;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.HttpUtils;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import com.github.wxiaoqi.security.common.vo.UdaxLastPricesBean;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

import static com.github.wxiaoqi.security.common.enums.AccountSignType.ACCOUNT_PAY_AVAILABLE;
import static com.github.wxiaoqi.security.common.enums.AccountSignType.MERCHANT_REFUND_SETTLE;

@Service
public class AssetAccountBiz extends BaseBiz<DcAssetAccountMapper, DcAssetAccount> {


    @Autowired
    private KeyConfiguration configuration;

    @Autowired
    private DcAssetAccountMapper assetAccountMapper;

    @Autowired
    private DcAssetAccountLogMapper logMapper;

    @Autowired
    private Environment env;

    /**
     * 冻结报价用户资产
     *
     * @param userId
     * @param symbol
     * @param amount
     * @param type
     */
//    @Transactional(rollbackFor = Exception.class)
//    public int signUpdateAssert(Long userId, String symbol, BigDecimal amount, AccountSignType type, AccountLogType logType) throws Exception {
////        DcAssetAccount dcAssetAccountParam = new DcAssetAccount();
////        dcAssetAccountParam.setUserId(userId);
////        dcAssetAccountParam.setSymbol(symbol);
//        //锁表
//        DcAssetAccount account = lockRecord(symbol, userId);
//        //保留一份副本
//        DcAssetAccount preAccount = new DcAssetAccount();
//        BeanUtils.copyProperties(account, preAccount);//复制属性
//        if (account == null) {
//            throw new UserInvalidException(Resources.getMessage("NOT_USER_AMOUNT"));
//        }
//        boolean flag = verifySignUtil.verifySign(account, configuration.getSignKey(), account.getUmac());
//        if (!flag) {
//            // 资产异常
//            throw new UserInvalidException(Resources.getMessage("ASSERT_30001"));
//        }
//        // 更新字段
//        if (type == AccountSignType.ACCOUNT_WITHDRAW) {
//            account.setAvailableAmount(account.getAvailableAmount().subtract(amount));//
//            account.setFreezeAmount(account.getFreezeAmount().add(amount));
//        }
//        else if (type == AccountSignType.ACCOUNT_RECHARGE) {
//            account.setAvailableAmount(account.getAvailableAmount().add(amount));// 增加资产
//            account.setTotalAmount(account.getTotalAmount().add(amount));
//        }
//        if (type == AccountSignType.ACCOUNT_WITHDRAW_DEDUTION) {
//            //增加可用资产
//            account.setAvailableAmount(account.getAvailableAmount().add(amount));
//            //减少冻结
//            account.setFreezeAmount(account.getFreezeAmount().subtract(amount));
//        }
//        if (account.getTotalAmount().compareTo(BigDecimal.ZERO) < 0 ||
//                account.getAvailableAmount().compareTo(BigDecimal.ZERO) < 0
//                || account.getFreezeAmount().compareTo(BigDecimal.ZERO) < 0
//                || account.getWaitConfirmAmount().compareTo(BigDecimal.ZERO) < 0) {
//            throw new UserInvalidException(Resources.getMessage("NOT_USER_AMOUNT"));
//
//        }
//        // 重新生成mac
//        String sign = verifySignUtil.macSign(account, configuration.getSignKey());
//        account.setUmac(sign);
//        int count = assetAccountMapper.updateByPrimaryKey(account);
//        DcAssetAccountLog log = new DcAssetAccountLog();
//        log.setAmount(amount);
//        log.setSymbol(symbol);
//        log.setUserId(userId);
//        log.setRemark(logType.name());
//
//        log.setCreateTime(new Date());
//        AccountLogType.setParam(logType, account, preAccount, log);
//        logMapper.insertSelective(log);
//        return count;
//    }


    /**
     * 更新资产信息（复用前台资产代码，前台代码改动需同步改动）

     * @param type   资产类型：充值(Recharge) or 提现(Withdraw)
     */
    @Transactional(rollbackFor = Exception.class)
    public void dcSignUpdateAssert(AccountAssertLogVo vo, AccountSignType type)
            throws Exception {
        Long userId = vo.getUserId();
        String symbol = vo.getSymbol();
        BigDecimal amount = vo.getAmount();

        ObjectRestResponse rspResult = new ObjectRestResponse();
        DcAssetAccount account = lockRecord(symbol, userId);
        boolean flag = verifySignUtil.verifySign(account, configuration.getSignKey(), account.getUmac());
        if (!flag) {
            // 返回校验失败
            throw new BusinessException(ResponseCode.ASSERT_30001.name());
        }
        //保留一份副本
        DcAssetAccount preAccount = new DcAssetAccount();
        BeanUtils.copyProperties(account, preAccount);//复制属性
        // 更新字段
        if (type == AccountSignType.ACCOUNT_WITHDRAW) {
            account.setTotalAmount(account.getTotalAmount().subtract(amount)); // 扣除冻结资产
            account.setFreezeAmount(account.getFreezeAmount().subtract(amount));
        } else if (type == AccountSignType.ACCOUNT_WITHDRAW_FREEZE) { // 冻结资产
            account.setAvailableAmount(account.getAvailableAmount().subtract(amount));//
            account.setFreezeAmount(account.getFreezeAmount().add(amount));
        } else if (type == AccountSignType.ACCOUNT_WITHDRAW_DEDUTION) {
            account.setAvailableAmount(account.getAvailableAmount().add(amount));// 解冻资产
            account.setFreezeAmount(account.getFreezeAmount().subtract(amount));
        } else if (type == AccountSignType.ACCOUNT_RECHARGE) {
            account.setAvailableAmount(account.getAvailableAmount().add(amount));// 增加资产
            account.setTotalAmount(account.getTotalAmount().add(amount));
        } else if (type == AccountSignType.MERCHANT_ADD_ASSERT) {
            //商户增加待结算资产
            account.setTotalAmount(account.getTotalAmount().add(amount));
            account.setWaitConfirmAmount(account.getWaitConfirmAmount().add(amount));
        } else if (type == AccountSignType.MERCHANT_SETTLE) {
            //商户结算
            account.setAvailableAmount(account.getAvailableAmount().add(amount));
            account.setWaitConfirmAmount(account.getWaitConfirmAmount().subtract(amount));
        } else if (type == ACCOUNT_PAY_AVAILABLE) {
            account.setTotalAmount(account.getTotalAmount().subtract(amount)); // 扣除可用资产
            account.setAvailableAmount(account.getAvailableAmount().subtract(amount));
        } else if (type == MERCHANT_REFUND_SETTLE) {
            account.setTotalAmount(account.getTotalAmount().subtract(amount)); // 扣除待结算资产
            account.setWaitConfirmAmount(account.getWaitConfirmAmount().subtract(amount));
        } else {
            throw new BusinessException(ResponseCode.UNKNOW_ERROR.name());
        }

        if (account.getTotalAmount().compareTo(BigDecimal.ZERO) < 0 ||
                account.getAvailableAmount().compareTo(BigDecimal.ZERO) < 0
                || account.getFreezeAmount().compareTo(BigDecimal.ZERO) < 0
                || account.getWaitConfirmAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ResponseCode.BALANCE_NOT_ENOUGH.name());
        }

        // 重新生成mac
        String sign = verifySignUtil.macSign(account, configuration.getSignKey());
        account.setUmac(sign);
        account.setUpdateTime(new Date());
        int count = mapper.updateByPrimaryKeySelective(account);
        System.out.println("更新返回结果"+count);
        if (count <= 0) {
            rspResult.setRel(false);
        }

        DcAssetAccountLog log = new DcAssetAccountLog();
        BeanUtils.copyProperties(vo,log);
        log.setCreateTime(new Date());
        AccountLogType.setParam(vo.getType(),account,preAccount,log);



        BigDecimal lastPrice = BigDecimal.ZERO;
        String url = env.getProperty("udax.lastprices");
        try{
            String returnJson = HttpUtils.postJson(url,vo.getSymbol()+"-USDT-USDT");
            UdaxLastPricesBean jsonBean = JSON.parseObject(returnJson,UdaxLastPricesBean.class);
            //获取交易对的最新价格
            if(jsonBean != null && jsonBean.getCode().intValue() == 200){
                lastPrice = jsonBean.getData();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        log.setUsdtRate(lastPrice);
        log.setUsdtAmount(log.getUsdtRate().multiply(log.getAmount()));

        logMapper.insertSelective(log);
    }

    //锁记录
    @Transactional(rollbackFor = Exception.class)
    public DcAssetAccount lockRecord(String dcCode,Long userId) throws Exception {
        DcAssetAccount dcAssetAccountParam = new DcAssetAccount();
        dcAssetAccountParam.setUserId(userId);
        dcAssetAccountParam.setSymbol(dcCode);
        //锁表
        DcAssetAccount dcAssetAccount = mapper.selectForUpdate(dcAssetAccountParam);
        if(dcAssetAccount == null ) {
            insertNewRecord(userId, dcCode);
            dcAssetAccount = mapper.selectForUpdate(dcAssetAccountParam);
        }
        return dcAssetAccount;
    }

    //插入一条新记录
    private DcAssetAccount insertNewRecord(Long userId,String dcCode) throws Exception {
        DcAssetAccount	dcAssetAccount = new DcAssetAccount();
        dcAssetAccount.setSymbol(dcCode);
        dcAssetAccount.setUserId(userId);
        dcAssetAccount.setTotalAmount(BigDecimal.ZERO.setScale(8,BigDecimal.ROUND_HALF_UP));
        dcAssetAccount.setAvailableAmount(BigDecimal.ZERO.setScale(8,BigDecimal.ROUND_HALF_UP));
        dcAssetAccount.setFreezeAmount(BigDecimal.ZERO.setScale(8,BigDecimal.ROUND_HALF_UP));
        dcAssetAccount.setWaitConfirmAmount(BigDecimal.ZERO.setScale(8,BigDecimal.ROUND_HALF_UP));
        dcAssetAccount.setCreateTime(new Date());
        dcAssetAccount.setUpdateTime(new Date());
        dcAssetAccount.setUmac(verifySignUtil.macSign(dcAssetAccount,configuration.getSignKey()));
        mapper.insertSelective(dcAssetAccount);
        return dcAssetAccount;
    }

}
