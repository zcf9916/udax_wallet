package com.github.wxiaoqi.security.admin.biz.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.CommonConstants;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.Element;
import com.github.wxiaoqi.security.common.mapper.admin.ElementMapper;
import com.github.wxiaoqi.security.common.mapper.admin.GroupMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.EntityUtils;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-06-23 20:27
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ElementBiz extends BaseBiz<ElementMapper, Element> {
    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private ElementMapper elementMapper;

    @Override
    public void insertSelective(Element entity) {
        EntityUtils.setCreatAndUpdatInfo(entity);
        mapper.insertSelective(entity);
        cacheReturn();
    }

    @Override
    public void updateSelectiveById(Element entity) {
        EntityUtils.setUpdatedInfo(entity);
        mapper.updateByPrimaryKeySelective(entity);
        cacheReturn();
    }

    @Override
    public void deleteById(Object id) {
        mapper.deleteByPrimaryKey(id);
        cacheReturn();
    }


    public List<Element> getAuthorityElementByUserId(Long userId) {
        return mapper.selectAuthorityElementByUserId(userId);
    }

    public List<Element> getAuthorityElementByUserId(Long userId, Long menuId) {
        return mapper.selectAuthorityMenuElementByUserId(userId, menuId);
    }

    public List<Element> getAllElementPermissions() {
        return (List<Element>) CacheUtil.getCache().get(Constants.CacheServiceType.ADMIN_ELEMENT_ALL);
    }

    public List<Element> cacheReturn() {
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.ADMIN_ELEMENT_ALL);
        List<Element> list = null;
        try {
            list = mapper.selectAllElementPermissions();
            CacheUtil.getCache().set(Constants.CacheServiceType.ADMIN_ELEMENT_ALL, (ArrayList<Element>) list);
            logger.info("==资源:Element缓存完成,缓存条数：{}", list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Element> selectElementByMenuIdAndUser(Long menuId) {
        Long currentGroupId = groupMapper.selectGroup(BaseContextHandler.getUserID());
        List<Long> elementIds = elementMapper.selectElementByMenuIdAndUserId(currentGroupId, menuId, CommonConstants.RESOURCE_TYPE_BTN);
        if (elementIds.size() <= 0) {
            return new ArrayList<>();
        }
        HashMap<String, Object> param = new HashMap<>();
        param.put("ids", elementIds);
        return elementMapper.selectElement(param);
    }


}
