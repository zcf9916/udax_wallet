package com.github.wxiaoqi.security.admin.biz.front;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.admin.biz.front.AssetAccountBiz;
import com.github.wxiaoqi.security.admin.vo.UserOfferVo;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.UserOfferInfo;
import com.github.wxiaoqi.security.common.enums.AccountLogType;
import com.github.wxiaoqi.security.common.enums.AccountSignType;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.admin.CfgCurrencyTransferMapper;
import com.github.wxiaoqi.security.common.mapper.admin.UserOfferInfoMapper;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import com.github.wxiaoqi.security.common.util.Query;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserOfferInfoBiz extends BaseBiz<UserOfferInfoMapper, UserOfferInfo> {


    @Autowired
    private CfgCurrencyTransferMapper cfgCurrencyTransferMapper;

    @Autowired
    private AssetAccountBiz assetAccountBiz;


    @Override
    public void insertSelective(UserOfferInfo entity) {
        if (entity.getSrcSymbol().equals(entity.getDstSymbol())) {
            throw new UserInvalidException(Resources.getMessage("CONFIG_CURRENCY_INFO_DSTSYMBOL"));
        }
        Map<String, Object> param = new HashMap<>();
        param.put("srcSymbol", entity.getSrcSymbol());
        param.put("dstSymbol", entity.getDstSymbol());
        //正常状态
        param.put("status", EnableType.ENABLE.value());
        Integer transferCount = cfgCurrencyTransferMapper.selectTransferBySymbol(param);
        Integer offerInfoCount = mapper.selectOfferInfoBySymbol(param);
        //是否重复交易对
        if (transferCount > 0 || offerInfoCount > 0) {
            throw new UserInvalidException(Resources.getMessage("CONFIG_TRANSFER_REPEAT"));
        }
        EntityUtils.setCreatAndUpdatInfo(entity);
        //剩余数量
        entity.setRemainVolume(entity.getOrderVolume());
        //白标提交报价时 设置默认为禁用
        entity.setStatus(EnableType.DISABLE.value());
        entity.setAdminId(BaseContextHandler.getUserID());
        EntityUtils.setUpdatedInfo(entity);
        mapper.insertOfferInfo(entity);
    }

    @Override
    public void updateSelectiveById(UserOfferInfo entity) {
        UserOfferInfo offerInfo = mapper.selectUserOffer(entity.getId());
        if (offerInfo.getFrontUser() == null) {
            throw new UserInvalidException(Resources.getMessage("USER_OFFER_VERIFY"));
        }
        if (!offerInfo.getAdminId().equals(BaseContextHandler.getUserID())) {
            throw new UserInvalidException(Resources.getMessage("OFFER_INFO_MAKE_UP"));
        }
        //补仓数量
        try {
            if (entity.getRemainVolume().compareTo(BigDecimal.ZERO) > 0) {
                AccountAssertLogVo payVo = new AccountAssertLogVo();
                payVo.setUserId(offerInfo.getFrontUser().getId());
                payVo.setSymbol(offerInfo.getSrcSymbol());
                payVo.setAmount(entity.getRemainVolume());
                payVo.setChargeSymbol(offerInfo.getSrcSymbol());
                payVo.setType(AccountLogType.QUOTED_FREEZE);
                payVo.setTransNo(String.valueOf(IdGenerator.nextId()));
                payVo.setRemark(AccountLogType.QUOTED_FREEZE.name());
                assetAccountBiz.dcSignUpdateAssert(payVo, AccountSignType.ACCOUNT_WITHDRAW);
                offerInfo.setRemainVolume(offerInfo.getRemainVolume().add(entity.getRemainVolume()));
                offerInfo.setOrderVolume(offerInfo.getOrderVolume().add(entity.getRemainVolume()));
            }
            mapper.updateOfferInfo(offerInfo);
        } catch (UserInvalidException e) {
            throw new UserInvalidException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TableResultResponse<UserOfferInfo> selectByQuery(Query query) {
        HashMap<String, Object> param = new HashMap<>();
        if (query.entrySet().size() > 0) {
            for (Map.Entry<String, Object> entry : query.entrySet()) {
                param.put(entry.getKey(), entry.getValue());
            }
        }
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<UserOfferInfo> list = mapper.pageQuery(param);
        return new TableResultResponse<UserOfferInfo>(result.getTotal(), list);
    }

    public UserOfferInfo selectById(Object id) {
        return mapper.selectUserOffer((Long) id);
    }

    public List<UserOfferInfo> listSrcSymbol() {
        return mapper.listSrcSymbol();
    }

    public void updateVerifyById(UserOfferVo entity) throws Exception {
        //只能是管理员才能审批
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            throw new UserInvalidException(Resources.getMessage("WHETHER_ADMIN"));
        }
        UserOfferInfo userOfferInfo = mapper.selectByPrimaryKey(entity.getId());
        //报价单是禁用状态的才可以审批
        if (userOfferInfo.getStatus().equals(EnableType.ENABLE.value())) {
            throw new UserInvalidException(Resources.getMessage("CURRENT_VERIFY"));
        }
        //冻结报价用户资产
        AccountAssertLogVo payVo = new AccountAssertLogVo();
        payVo.setUserId(entity.getFrontUser().getId());
        payVo.setSymbol(userOfferInfo.getSrcSymbol());
        payVo.setAmount(userOfferInfo.getOrderVolume());
        payVo.setChargeSymbol(userOfferInfo.getSrcSymbol());
        payVo.setType(AccountLogType.QUOTED_FREEZE);
        payVo.setTransNo(String.valueOf(IdGenerator.nextId()));
        payVo.setRemark(AccountLogType.QUOTED_FREEZE.name());
        assetAccountBiz.dcSignUpdateAssert(payVo, AccountSignType.ACCOUNT_WITHDRAW);
        userOfferInfo.setStatus(entity.getStatus());
        userOfferInfo.setFrontUser(entity.getFrontUser());
        mapper.updateOfferInfo(userOfferInfo);

    }

    public void userInfoFreezeAndDelete(Long id) throws Exception {
        //锁住记录
        UserOfferInfo offerInfo = mapper.selectUserOffer(id);
        //解冻资产
        AccountAssertLogVo payVo = new AccountAssertLogVo();
        payVo.setUserId(offerInfo.getFrontUser().getId());
        payVo.setSymbol(offerInfo.getSrcSymbol());
        payVo.setAmount(offerInfo.getRemainVolume());
        payVo.setChargeSymbol(offerInfo.getSrcSymbol());
        payVo.setType(AccountLogType.QUOTED_FREEZE);
        payVo.setTransNo(String.valueOf(IdGenerator.nextId()));
        payVo.setRemark(AccountLogType.QUOTED_FREEZE.name());
        assetAccountBiz.dcSignUpdateAssert(payVo, AccountSignType.ACCOUNT_WITHDRAW_DEDUTION);
        mapper.deleteByPrimaryKey(id);
    }
}
