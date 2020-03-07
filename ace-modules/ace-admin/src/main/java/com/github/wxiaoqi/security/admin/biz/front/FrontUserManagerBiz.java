package com.github.wxiaoqi.security.admin.biz.front;

import com.github.wxiaoqi.security.admin.vo.SymbolLockVo;
import com.github.wxiaoqi.security.admin.vo.UserValidVo;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.FrontUserManager;
import com.github.wxiaoqi.security.common.entity.admin.UserTransactionModel;
import com.github.wxiaoqi.security.common.entity.front.*;
import com.github.wxiaoqi.security.common.entity.fund.FundAccountAssert;
import com.github.wxiaoqi.security.common.entity.lock.UserSymbolLock;
import com.github.wxiaoqi.security.common.entity.lock.UserSymbolLockDetail;
import com.github.wxiaoqi.security.common.entity.merchant.Merchant;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.admin.FrontUserManagerMapper;
import com.github.wxiaoqi.security.common.mapper.admin.SymbolExchMapper;
import com.github.wxiaoqi.security.common.mapper.admin.UserTransactionModelMapper;
import com.github.wxiaoqi.security.common.mapper.admin.WhiteExchInfoMapper;
import com.github.wxiaoqi.security.common.mapper.front.*;
import com.github.wxiaoqi.security.common.mapper.fund.FundAccountAssertMapper;
import com.github.wxiaoqi.security.common.mapper.lock.UserSymbolLockDetailMapper;
import com.github.wxiaoqi.security.common.mapper.lock.UserSymbolLockMapper;
import com.github.wxiaoqi.security.common.mapper.merchant.MerchantMapper;
import com.github.wxiaoqi.security.common.util.*;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Transactional(rollbackFor = Exception.class)
public class FrontUserManagerBiz extends BaseBiz<FrontUserManagerMapper, FrontUserManager> {

    @Autowired
    public UserTransactionModelMapper modelMapper;
    @Autowired
    public WhiteExchInfoMapper whiteExchInfoMapper;
    @Autowired
    private FrontUserManagerMapper frontUserManagerMapper;
    @Autowired
    private FrontUserInfoMapper frontUserInfoMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private FrontUserMapper frontUserMapper;

    @Autowired
    private DcAssetAccountMapper dcAssetAccountMapper;

    @Autowired
    private FundAccountAssertMapper fundAccountAssertMapper;

    @Autowired
    private FrontWithdrawMapper frontWithdrawMapper;

    @Autowired
    private TransferOrderMapper transferOrderMapper;

    @Autowired
    private FrontTransferDetailMapper frontTransferDetailMapper;

    @Autowired
    private DcAssetAccountLogMapper dcAssetAccountLogMapper;

    @Autowired
    private UserSymbolLockMapper userSymbolLockMapper;

    @Autowired
    private UserSymbolLockDetailMapper userSymbolLockDetailMapper;

    private static ExecutorService excutorService = Executors.newFixedThreadPool(2);


    public List<FrontUserManager> selectListByExchId(Map<String, Object> params) {

        List<FrontUserManager> managerList = frontUserManagerMapper.selectListByExchId(params);
        managerList.forEach(frontUserManager -> {
            UserSymbolLock symbolLock = new UserSymbolLock();
            symbolLock.setUserId(frontUserManager.getId());
            symbolLock.setHasDetail(EnableType.DISABLE.value());
            List<UserSymbolLock> locks = userSymbolLockMapper.select(symbolLock);
            if (!StringUtil.listIsBlank(locks)){
                frontUserManager.setUserLocks(locks);
            }
        });
        return managerList;
    }

    @Transactional(rollbackFor = Exception.class)
    public void freedUserAsset(SymbolLockVo vo) {
        UserSymbolLock lock = new UserSymbolLock();
        lock.setUserId(vo.getUserId());
        lock.setSymbol(vo.getSymbol());
        lock.setHasDetail(EnableType.DISABLE.value());//未生成明细 未开始释放
        UserSymbolLock symbolLock = userSymbolLockMapper.selectOne(lock);
        if (symbolLock ==null){
            logger.error("用户后台释放锁定资产异常,userId: "+vo.getUserId() + "释放币种:" +vo.getSymbol());
            throw new UserInvalidException(Resources.getMessage("CONFIG_FRDDE_ERROR"));
        }
        updateUserLock(symbolLock);

    }
    @Transactional(rollbackFor = Exception.class)
    public void updateUserLock(UserSymbolLock symbolLock) {
        //开始释放
        symbolLock.setHasDetail(EnableType.ENABLE.value());
        //生成明细
        generateLockDetail(symbolLock);
        Example example  = new Example(UserSymbolLock.class);
        example.createCriteria().andEqualTo("version",symbolLock.getVersion())
                .andEqualTo("id",symbolLock.getId());
        symbolLock.setVersion(symbolLock.getVersion()+1);
        int result = userSymbolLockMapper.updateByExampleSelective(symbolLock,example);
        if (result <1){
            logger.error("乐观锁冲突  重新执行");
            throw new UserInvalidException(Resources.getMessage(Resources.getMessage("CONFIG_VERSION_ERROR")));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void generateLockDetail(UserSymbolLock symbolLock) {

        ArrayList<UserSymbolLockDetail> detailList = new ArrayList<>();
        BigDecimal amount =new BigDecimal(BigInteger.ZERO);
        //计算生成时间
        LocalDate createTime = LocalDate.now();
        createTime=createTime.plusDays(1);
        //根据总释放次数循环
        for (int i = 0; i <symbolLock.getTotalTime() ; i++) {
            UserSymbolLockDetail detail = new UserSymbolLockDetail();
            if (symbolLock.getTotalTime() == i+1){
                //表示最后一次释放
                detail.setFreeAmount(symbolLock.getTotalAmount().subtract(amount));
                amount =new BigDecimal(BigInteger.ZERO);
            }else {
                //释放数量 = 总数量 / 释放次数
                detail.setFreeAmount(symbolLock.getTotalAmount().divide(new BigDecimal(symbolLock.getTotalTime().toString()),8, BigDecimal.ROUND_HALF_UP));
                amount= amount.add(symbolLock.getTotalAmount().divide(new BigDecimal(symbolLock.getTotalTime().toString()),8,BigDecimal.ROUND_HALF_UP));
            }
            //排除第一次循环  计算释放时间
            if (i !=0){
                createTime =createTime.plusDays(symbolLock.getFreedCycle());
            }
            detail.setUserId(symbolLock.getUserId());
            detail.setLockId(symbolLock.getId());
            detail.setFreeType(EnableType.ENABLE.value());//默认制动释放
            detail.setIsFree(EnableType.DISABLE.value());//是否释放 :否
            detail.setCreateTime(LocalDateUtil.localDate2Date(createTime));
            detail.setVersion(1);
            detail.setSymbol(symbolLock.getSymbol());
            detailList.add(detail);
        }
        userSymbolLockDetailMapper.insertList(detailList);

    }





    public Integer  freedUserAssetALL(String symbol) {
        List<UserSymbolLock> lockList = userSymbolLockMapper.getLockList(symbol,
                BaseContextHandler.getExId(), EnableType.DISABLE.value());
        if (!StringUtil.listIsBlank(lockList)){
            excutorService.execute(new Runnable() {
                @Override
                public void run() {
                    //后台批量生成明细
                   lockList.forEach(userLock ->{
                       updateUserLock(userLock);
                   } );
                }
            });
        }
    return lockList.size();
    }



    public Map<String, Object> selectUserInfoByUserId(Long id) {
        HashMap<String, Object> map = new HashMap<>();
        //用户认证信息
        FrontUserInfo frontUserInfo = new FrontUserInfo();
        frontUserInfo.setUserId(id);
        frontUserInfo = frontUserInfoMapper.selectOne(frontUserInfo);
        map.put("frontUserInfo", frontUserInfo);
        //商家信息
        Merchant merchant = new Merchant();
        merchant.setUserId(id);
        merchant = merchantMapper.selectOne(merchant);
        map.put("merchant", merchant);
        return map;
    }

    /**
     * 充值明细
     *
     * @param params
     * @return
     */
    public List<UserTransactionModel> selectListRechargeDetail(Map<String, Object> params) {
        return modelMapper.queryRechargeDetail(params);
    }

    /**
     * 充值汇总
     *
     * @param params
     * @return
     */
    public List<UserTransactionModel> pageFrontRecharge(Map<String, Object> params) {
        return  modelMapper.queryRechargeTotal(params);
    }

    /**
     * 查询个人资产流水表 [个人转账汇总 or 币币交易汇总 or 商家与用户交易汇总]
     * @param params
     * @return
     */
    public List<UserTransactionModel> pageTransferOrder(Map<String, Object> params) {
        List<UserTransactionModel> modelList = modelMapper.queryTransferOrder(params);
        return modelList;
    }

    /**
     * 个人转账明细
     * TransferOrder
     * @param params
     * @return
     */

    public List<TransferOrder> selectListTransferDetail(Map<String, Object> params) {
        return transferOrderMapper.selectCustomPage(params);
    }

    /**
     * 币币转账明细
     * @param params
     * @return
     */
    public List<FrontTransferDetail> selectListFrontTransferDetail(Map<String, Object> params) {
        params.put("status",TransferOrderStatus.PAYED.value());
        return frontTransferDetailMapper.selectCustomPage(params);
    }

    /**
     * 提现汇总
     *
     * @param params
     * @return
     */
    public List<UserTransactionModel> pageFrontWithdraw(Map<String, Object> params) {
        List<UserTransactionModel> modelList = modelMapper.queryWithdrawTotal(params);
        return modelList;
    }

    /**
     * 提现明细
     *
     * @param params
     * @return
     */
    public List<FrontWithdraw> pageFrontWithdrawDetail(Map<String, Object> params) {
        return frontWithdrawMapper.selectCustomPage(params);
    }

    /**
     * 查询用户资产
     *
     * @param params
     * @return
     */
    public List<DcAssetAccount> pageAccounts(Map<String, Object> params) {
        Example example = new Example(DcAssetAccount.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", Long.parseLong((String) params.get("userId")));
        return dcAssetAccountMapper.selectByExample(example);
    }

    /**
     * 查询用户跟单账户资产
     *
     * @param params
     * @return
     */
    public List<FundAccountAssert> pageFundAccount(Map<String, Object> params) {
        Example example = new Example(FundAccountAssert.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", Long.parseLong((String) params.get("userId")));
        return fundAccountAssertMapper.selectByExample(example);
    }

    /**
     * UD 社区流水明细
     * @param params
     * @return
     */
    public List<DcAssetAccountLog> pageUDDetail(Map<String, Object> params) {
        return dcAssetAccountLogMapper.selectCustomPage(params);
    }

    /**
     * 用户认证 商家认证
     *
     * @param entity
     */
    public void updateSelectiveById(UserValidVo entity) {
        //用户认证
        if (entity.getUserType().equals(FrontUserType.USER.value())) {
            FrontUserInfo frontUserInfo = new FrontUserInfo();
            frontUserInfo.setUserId(entity.getId());
            frontUserInfo = frontUserInfoMapper.selectOne(frontUserInfo);
            if (entity.getUserValid().equals(EnableType.ENABLE.value())) {
                //审核通过
                frontUserInfo.setIsValid(ValidType.AUTH.value());
                frontUserInfoMapper.updateByPrimaryKey(frontUserInfo);
            } else {
                //审核不通过
                if (StringUtils.isBlank(entity.getDictData().getDictValue())) {
                    throw new UserInvalidException(Resources.getMessage("FRONT_USER_INFO_VALIE"));
                }
                if (!frontUserInfo.getIsValid().equals(ValidType.SUBMIT.value())) {
                    throw new UserInvalidException(Resources.getMessage("FRONT_USER_VALID"));
                }
                frontUserInfo.setIsValid(ValidType.NO_AUTH.value());
                frontUserInfo.setDictData(entity.getDictData());
                frontUserManagerMapper.updateUserByValid(frontUserInfo);
                Map<String, Object> param = InstanceUtil.newHashMap("userId", entity.getId());
                FrontUser user = frontUserMapper.selectUnionUserInfo(param);
                //发送短信或邮件
                SendUtil.sendSmsOrEmail(SendMsgType.IDENTITY_CERTIFY.value(), EmailTemplateType.USER_AUDIT.value(), user, entity.getDictData().getDictValue(), user.getUserName());

            }
        } else {
            //商家认证
            Merchant merchant = new Merchant();
            merchant.setUserId(entity.getId());
            merchant = merchantMapper.selectOne(merchant);
            //通过
            if (entity.getUserValid().equals(EnableType.ENABLE.value())) {
                merchant.setMchStatus(ValidType.AUTH.value());
                merchantMapper.updateByPrimaryKey(merchant);
            } else {
                //不通过
                if (StringUtils.isBlank(entity.getDictData().getDictValue())) {
                    throw new UserInvalidException(Resources.getMessage("FRONT_USER_INFO_VALIE"));
                }
                if (!merchant.getMchStatus().equals(ValidType.SUBMIT.value())) {
                    throw new UserInvalidException(Resources.getMessage("FRONT_USER_VALID"));
                }
                merchant.setMchStatus(ValidType.NO_AUTH.value());
                merchant.setDictData(entity.getDictData());
                frontUserManagerMapper.updateMerchantByValid(merchant);
                Map<String, Object> param = InstanceUtil.newHashMap("userId", entity.getId());
                FrontUser user = frontUserMapper.selectUnionUserInfo(param);
                //发送短信或邮件
                SendUtil.sendSmsOrEmail(SendMsgType.MERCHANT_REVIEW.value(), EmailTemplateType.MERCHANT_REVIEW.value(), user, entity.getDictData().getDictValue(), user.getUserName());

            }
        }
    }



}
