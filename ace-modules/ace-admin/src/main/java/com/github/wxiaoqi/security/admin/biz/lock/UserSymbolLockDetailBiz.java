package com.github.wxiaoqi.security.admin.biz.lock;

import com.github.wxiaoqi.security.admin.biz.front.AssetAccountBiz;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.lock.UserSymbolLock;
import com.github.wxiaoqi.security.common.entity.lock.UserSymbolLockDetail;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.front.FrontUserMapper;
import com.github.wxiaoqi.security.common.mapper.lock.UserSymbolLockDetailMapper;
import com.github.wxiaoqi.security.common.mapper.lock.UserSymbolLockMapper;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.SendUtil;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.Map;

/**
 * @author Mr.AG
 * @email 463540703@qq.com
 * @date 2017-12-26 19:43:46
 */
@Service
@Slf4j
public class UserSymbolLockDetailBiz extends BaseBiz<UserSymbolLockDetailMapper, UserSymbolLockDetail> {


    @Autowired
    UserSymbolLockMapper lockMapper;
    @Autowired
    private AssetAccountBiz assertAccountBiz;
    @Autowired
    private FrontUserMapper frontUserMapper;

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void manualFreedAssets(Long id) {
        UserSymbolLockDetail detail = mapper.selectByPrimaryKey(id);
        if (detail == null) {
            logger.error("后台手动释放异常,明细id: " + id);
            throw new UserInvalidException(Resources.getMessage("CONFIG_FRDDE_ERROR"));
        }
        try {
            if(EnableType.ENABLE.value() == detail.getIsFree().intValue()){
                return;
            }
            AccountAssertLogVo payVo = new AccountAssertLogVo();
            payVo.setUserId(detail.getUserId());
            payVo.setSymbol(detail.getSymbol());
            payVo.setAmount(detail.getFreeAmount());
            payVo.setChargeSymbol(detail.getSymbol());
            payVo.setType(AccountLogType.UNLOCK_ASSERT);
            payVo.setTransNo(String.valueOf(IdGenerator.nextId()));
            payVo.setRemark(AccountLogType.UNLOCK_ASSERT.name());
            assertAccountBiz.dcSignUpdateAssert(payVo, AccountSignType.ACCOUNT_WITHDRAW_DEDUTION);
            while (true) {
                UserSymbolLockDetail param = new UserSymbolLockDetail();
                param.setId(detail.getId());
                param.setIsFree(EnableType.ENABLE.value());
                param.setFreeType(LoginType.ANDROID.value());//手动释放
                param.setVersion(detail.getVersion() + 1);//乐观锁
                param.setUpdateTime(new Date());
                Example example = new Example(UserSymbolLockDetail.class);
                example.createCriteria().andEqualTo("version", detail.getVersion())
                        .andEqualTo("id", detail.getId());
                int result = mapper.updateByExampleSelective(param, example);
                //乐观锁冲突  重新执行
                if (result < 1) {
                    detail = mapper.selectByPrimaryKey(detail.getId());
                    if(EnableType.ENABLE.value() == detail.getIsFree().intValue()){
                        return;
                    }
                    continue;
                }
                break;
            }
            while (true) {
                UserSymbolLock lockParam = new UserSymbolLock();
                lockParam.setSymbol(detail.getSymbol());
                lockParam.setUserId(detail.getUserId());
                lockParam.setId(detail.getLockId());
                UserSymbolLock lock = lockMapper.selectOne(lockParam);
                if (lock == null) {
                    throw new UserInvalidException(Resources.getMessage("CONFIG_LOCK"));
                }
                lockParam.setSymbol(null);
                lockParam.setUserId(null);
                lockParam.setFreedTime(lock.getFreedTime() + 1);
                lockParam.setVersion(lock.getVersion() + 1);
                lockParam.setFreedAmount(lock.getFreedAmount().add(detail.getFreeAmount()));
                //如果是最后一次释放
                if (lockParam.getFreedTime() == lock.getTotalTime()) {
                    lockParam.setFreedAmount(lock.getTotalAmount());
                    lockParam.setIsFreed(EnableType.ENABLE.value());//释放完毕
                }
                Example example = new Example(UserSymbolLock.class);
                example.createCriteria().andEqualTo("version", lock.getVersion())
                        .andEqualTo("id", lock.getId());
                int result = lockMapper.updateByExampleSelective(lockParam, example);
                //乐观锁冲突  重新执行
                if (result < 1) {
                    continue;
                }
                break;
            }
            Map<String, Object> param = InstanceUtil.newHashMap("userId", detail.getUserId());
            FrontUser frontUser = frontUserMapper.selectUnionUserInfo(param);
            if (frontUser == null || frontUser.getUserInfo() == null) {
                return;
            }
            SendUtil.sendEmail(EmailTemplateType.UNLOCK_ASSERT_TEMPLATE.value(), frontUser.getEmail(), frontUser.getUserInfo().getExchangeId(), null, frontUser.getUserName(), detail.getFreeAmount(), detail.getSymbol());
        } catch (Exception e) {
            logger.error("释放代币出错,用戶Id:" + detail.getUserId() + ";代币:" + detail.getSymbol(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }
}