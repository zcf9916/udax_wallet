package com.github.wxiaoqi.security.common.mapper.ud;

import com.github.wxiaoqi.security.common.base.WalletBaseMapper;
import com.github.wxiaoqi.security.common.entity.ud.HNodeAward;

import java.util.List;

public interface HNodeAwardMapper extends WalletBaseMapper<HNodeAward> {
    public List<HNodeAward> selectListAll();
}