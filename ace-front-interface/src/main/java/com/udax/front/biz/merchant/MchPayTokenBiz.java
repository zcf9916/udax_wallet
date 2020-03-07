package com.udax.front.biz.merchant;

import org.springframework.stereotype.Service;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.entity.merchant.MchPayToken;
import com.github.wxiaoqi.security.common.mapper.merchant.MchPayTokenMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MchPayTokenBiz extends BaseBiz<MchPayTokenMapper, MchPayToken> {

}
