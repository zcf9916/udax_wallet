package com.github.wxiaoqi.security.admin.biz.base;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.entity.admin.GroupLeader;
import com.github.wxiaoqi.security.common.mapper.admin.GroupLeaderMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class GroupLeaderBiz extends BaseBiz<GroupLeaderMapper, GroupLeader> {



}
