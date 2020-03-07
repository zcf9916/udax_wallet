package com.github.wxiaoqi.security.admin.biz.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.entity.admin.ResourceAuthority;
import com.github.wxiaoqi.security.common.mapper.admin.ResourceAuthorityMapper;

/**
 * Created by Ace on 2017/6/19.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ResourceAuthorityBiz extends BaseBiz<ResourceAuthorityMapper, ResourceAuthority> {
    @Autowired
    private ResourceAuthorityMapper resourceAuthorityMapper;

    public void deleteByAuthorityIdAndResourceType(String authorityId, String resourceType) {
        resourceAuthorityMapper.deleteByAuthorityIdAndResourceType(authorityId, resourceType);
    }
}
