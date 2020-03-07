package com.github.wxiaoqi.security.common.mapper.ud;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.ud.HCommissionRelation;

import java.util.List;

public interface HCommissionRelationMapper extends WalletBaseMapper<HCommissionRelation> {


    int insertList(List<HCommissionRelation> list);
}