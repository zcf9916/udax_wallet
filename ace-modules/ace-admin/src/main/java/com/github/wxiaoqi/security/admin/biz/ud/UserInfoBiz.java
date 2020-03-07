package com.github.wxiaoqi.security.admin.biz.ud;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.entity.ud.HUnLockDetail;
import com.github.wxiaoqi.security.common.entity.ud.HUserInfo;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.mapper.ud.HUnLockDetailMapper;
import com.github.wxiaoqi.security.common.mapper.ud.HUserInfoMapper;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserInfoBiz extends BaseBiz<HUserInfoMapper, HUserInfo> {

    @Autowired
    private HUnLockDetailMapper hUnLockDetail;

    /**
     * 设置排单等级
     * @param entity
     */
    public void updateSelectiveById(HUserInfo entity) {
        HUserInfo hUserInfo = mapper.selectByPrimaryKey(entity.getId());
        hUserInfo.setAddAmount(entity.getAddAmount().subtract(hUserInfo.getTotalAmount()));
        mapper.updateByPrimaryKey(hUserInfo);
    }

    /**
     * 设置节点等级
     * @param entity
     */
    public void updateNodeByUserId(HUserInfo entity) {
        HUserInfo hUserInfo = mapper.selectByPrimaryKey(entity.getId());
        List<HUserInfo> infos = mapper.selectAllByChildUserId(hUserInfo.getUserId());
        //变更数量
        BigDecimal changeAmount=entity.getAddNodeAmount().subtract(hUserInfo.getChildInvest().add(hUserInfo.getAddNodeAmount()));
        hUserInfo.setAddNodeAmount(hUserInfo.getAddNodeAmount().add(changeAmount));
        mapper.updateByPrimaryKey(hUserInfo);
        if (StringUtil.listIsNotBlank(infos)){
            infos.forEach(parentUser ->{
                    parentUser.setAddNodeAmount(parentUser.getAddNodeAmount().add(changeAmount));
                    mapper.updateByPrimaryKey(parentUser);
                } );
            }
    }

    public void unlockUserInfoStatus(Long id) {
        HUserInfo hUserInfo = mapper.selectByPrimaryKey(id);
        if (hUserInfo !=null && hUserInfo.getStatus().equals(EnableType.DISABLE.value())){
            //设置用户UU机器人激活状态
            hUserInfo.setStatus(EnableType.ENABLE.value());
            mapper.updateByPrimaryKey(hUserInfo);
            //添加激活明细
            HUnLockDetail detail = new HUnLockDetail();
            detail.setAmount(new BigDecimal(0));
            detail.setOrderNo(String.valueOf(IdGenerator.nextId()));
            detail.setCreateTime(new Date());
            detail.setUserId(hUserInfo.getUserId());
            detail.setType(EnableType.ENABLE.value());
            hUnLockDetail.insertSelective(detail);
        }
    }
}
