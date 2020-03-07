package com.github.wxiaoqi.security.common.mapper.admin;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.admin.BaseEmailTemplate;
import com.github.wxiaoqi.security.common.entity.admin.FrontUserManager;
import com.github.wxiaoqi.security.common.entity.front.FrontUserInfo;
import com.github.wxiaoqi.security.common.entity.merchant.Merchant;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface FrontUserManagerMapper extends WalletBaseMapper<FrontUserManager> {

    List<FrontUserManager> selectListByExchId(@Param("cm") Map<String, Object> params);
    public int updateUserByValid(FrontUserInfo info);
    public int updateMerchantByValid(Merchant chant);

}