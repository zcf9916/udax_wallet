package com.github.wxiaoqi.security.common.mapper.admin;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.admin.UserOfferInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface UserOfferInfoMapper extends WalletBaseMapper<UserOfferInfo> {
    int insertOfferInfo(UserOfferInfo info);
    
    int updateOfferInfo(UserOfferInfo info);
    
    UserOfferInfo selectUserOffer(@Param("id") Long id);
    
    List<UserOfferInfo> pageQuery(@Param("cm") Map<String,Object> param);
    
    Integer selectOfferInfoBySymbol(@Param("cm")Map<String,Object> param);
    
    List<String> selectOfferInfoBySrcSymbol(@Param("srcSymbol") String srcSymbol);
    
    List<UserOfferInfo> listSrcSymbol();

	UserOfferInfo selectOneForUpdate(@Param("cm") UserOfferInfo offerInfo);
}