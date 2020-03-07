package com.github.wxiaoqi.security.admin.biz.front;

import com.github.wxiaoqi.security.admin.vo.UserValidVo;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.front.*;
import com.github.wxiaoqi.security.common.entity.merchant.MchRefundDetail;
import com.github.wxiaoqi.security.common.entity.merchant.MchTradeDetail;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.front.*;
import com.github.wxiaoqi.security.common.mapper.merchant.MchRefundDetailMapper;
import com.github.wxiaoqi.security.common.mapper.merchant.MchTradeDetailMapper;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.SendUtil;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class UserAssetsManagerBiz {
    private Logger logger = LoggerFactory.getLogger(UserAssetsManagerBiz.class);

    @Autowired
    private FrontRechargeMapper rechargeMapper;

    @Autowired
    private FrontWithdrawMapper frontWithdrawMapper;

    @Autowired
    private AssetAccountBiz assetAccountBiz;


    @Autowired
    private FrontUserMapper frontUserMapper;

    @Autowired
    private TransferOrderMapper transferOrderMapper;

    @Autowired
    private DcAssetAccountMapper dcAssetAccountMapper;


    @Autowired
    private FrontTransferDetailMapper frontTransferDetailMapper;

    @Autowired
    private MchTradeDetailMapper mchTradeDetailMapper;

    @Autowired
    private MchRefundDetailMapper mchRefundDetailMapper;

    /**
     * 用户入金记录查询
     *
     * @param param
     * @return
     */
    public List<FrontRecharge> getFrontRechargeList(Map<String, Object> param) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            param.put("exchangeId", BaseContextHandler.getExId());
        }
        return rechargeMapper.selectCustomPage(param);
    }

    /**
     * 用户提交记录查询
     *
     * @param param
     * @return
     */
    public List<FrontWithdraw> getFrontWithdraw(Map<String, Object> param) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            param.put("exchangeId", BaseContextHandler.getExId());
        }
        return frontWithdrawMapper.selectCustomPage(param);
    }

    /**
     * 用户资产
     *
     * @param param
     * @return
     */
    public List<DcAssetAccount> getFrontAccount(Map<String, Object> param) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            param.put("exchangeId", BaseContextHandler.getExId());
        }
        return dcAssetAccountMapper.selectCustomPage(param);
    }

    /**
     * 用户与用户之间转账记录
     *
     * @param param
     * @return
     */
    public List<TransferOrder> getTransferOrder(Map<String, Object> param) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            param.put("exchangeId", BaseContextHandler.getExId());
        }
        return transferOrderMapper.selectCustomPage(param);
    }


    /**
     * 币币之间转换记录查询
     *
     * @param param
     * @return
     */
    public List<FrontTransferDetail> getFrontTransferDetail(Map<String, Object> param) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            param.put("exchangeId", BaseContextHandler.getExId());
        }
        return frontTransferDetailMapper.selectCustomPage(param);
    }

    /**
     * 用户支付商家交易订单列表
     *
     * @param param
     * @return
     */
    public List<MchTradeDetail> getMchTradeDetail(Map<String, Object> param) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            param.put("exchangeId", BaseContextHandler.getExId());
        }
        return mchTradeDetailMapper.selectCustomPage(param);
    }

    /**
     * 商户退款订单列表
     *
     * @param param
     * @return
     */
    public List<MchRefundDetail> getMchRefundDetail(Map<String, Object> param) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            param.put("exchangeId", BaseContextHandler.getExId());
        }
        return mchRefundDetailMapper.selectCustomPage(param);
    }

    /**
     * 出金审核
     *
     * @param entity
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSelectiveById(UserValidVo entity) throws Exception {
        //锁住出金记录
        FrontWithdraw frontWithdraw = frontWithdrawMapper.selectForUpdate2(entity.getId());
        if (frontWithdraw.getStatus().equals(FrontWithdrawStatus.WaitAuto.value())) {
            if (entity.getUserValid().equals(EnableType.ENABLE.value())) {
                //通过审核
                frontWithdraw.setStatus(FrontWithdrawStatus.Audited.value());
                frontWithdrawMapper.updateByPrimaryKey(frontWithdraw);
            }
            if (entity.getUserValid().equals(EnableType.DISABLE.value())) {
                //解冻资产
                if (StringUtils.isBlank(entity.getDictData().getDictValue())) {
                    throw new UserInvalidException(Resources.getMessage("FRONT_USER_INFO_VALIE"));
                }
                AccountAssertLogVo payVo = new AccountAssertLogVo();
                payVo.setUserId(frontWithdraw.getUserId());
                payVo.setSymbol(frontWithdraw.getSymbol());
                payVo.setAmount(frontWithdraw.getTradeAmount());
                payVo.setChargeSymbol(frontWithdraw.getSymbol());
                payVo.setType(AccountLogType.ASSERT_UNFREEZE);
                payVo.setTransNo(String.valueOf(IdGenerator.nextId()));
                payVo.setRemark(AccountLogType.ASSERT_UNFREEZE.name());
                assetAccountBiz.dcSignUpdateAssert(payVo, AccountSignType.ACCOUNT_WITHDRAW_DEDUTION);
                Map<String, Object> param = InstanceUtil.newHashMap("userId", frontWithdraw.getUserId());
                FrontUser user = frontUserMapper.selectUnionUserInfo(param);
                //发送短信或邮件
                SendUtil.sendSmsOrEmail(SendMsgType.WITHDRAW_FAIL.value(), EmailTemplateType.WITHDRAW_BACK_AUTH.value(), user, entity.getDictData().getDictValue(), user.getUserName());
                //更新当前订单状态
                frontWithdraw.setStatus(FrontWithdrawStatus.TransError.value());
                frontWithdraw.setDictData(entity.getDictData());
                frontWithdrawMapper.updateById(frontWithdraw);
            }
        }
    }
}

