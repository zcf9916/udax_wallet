package com.udax.front.biz;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.entity.admin.FrontNotice;
import com.github.wxiaoqi.security.common.mapper.admin.FrontNoticeMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class FrontNoticeBiz extends BaseBiz<FrontNoticeMapper, FrontNotice> {

}
