package com.github.wxiaoqi.security.admin.biz.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.Menu;
import com.github.wxiaoqi.security.common.mapper.admin.MenuMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-06-12 8:48
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MenuBiz extends BaseBiz<MenuMapper, Menu> {

    @Autowired
    private ElementBiz elementBiz;

    public List<Menu> selectList(Map<String, Object> map) {
        List<Menu> menus;
        if (map.get("language").equals("zh")) {
            menus = (List<Menu>) CacheUtil.getCache().get(Constants.CacheServiceType.ADMIN_MENU_ALL);
        } else {
            menus = mapper.selectListByLanguage(map);
            menus.forEach((r) -> {
                r.setTitle(r.getLanguageTitle());
            });
        }
        return menus;
    }

    @Override
    public void insertSelective(Menu entity) {
        if (AdminCommonConstant.ROOT == entity.getParentId()) {
            entity.setPath("/" + entity.getCode());
        } else {
            Menu parent = this.selectById(entity.getParentId());
            entity.setPath(parent.getPath() + "/" + entity.getCode());
        }
        super.insertSelective(entity);
        cacheReturn();
        elementBiz.cacheReturn();
    }

    @Override
    public void updateById(Menu entity) {
        if (AdminCommonConstant.ROOT == entity.getParentId()) {
            entity.setPath("/" + entity.getCode());
        } else {
            Menu parent = this.selectById(entity.getParentId());
            entity.setPath(parent.getPath() + "/" + entity.getCode());
        }
        super.updateById(entity);
        cacheReturn();
        elementBiz.cacheReturn();
    }

    @Override
    public void updateSelectiveById(Menu entity) {
        super.updateSelectiveById(entity);
        cacheReturn();
        elementBiz.cacheReturn();
    }

    @Override
    public void deleteById(Object id) {
        mapper.deleteByPrimaryKey(id);
        cacheReturn();
        elementBiz.cacheReturn();
    }


    /**
     * 获取用户可以访问的菜单
     *
     * @param id
     * @return
     */
    public List<Menu> getUserAuthorityMenuByUserId(Long id, String language) {
        List<Menu> menus;
        if (language.equals("zh")) {
            menus = mapper.selectAuthorityMenuByUserId(id);
        } else {
            menus = mapper.selectAuthorityMenuByUserIdAndLanguage(id, language);
            menus.forEach((r) -> {
                r.setTitle(r.getLanguageTitle());
            });
        }
        return menus;
    }

    /**
     * 根据用户获取可以访问的系统
     *
     * @param id
     * @return
     */
    public List<Menu> getUserAuthoritySystemByUserId(Long id) {
        return mapper.selectAuthoritySystemByUserId(id);
    }


    public List<Menu> cacheReturn() {
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.ADMIN_MENU_ALL);
        List<Menu> list = null;
        try {
            list = mapper.selectAll();
            CacheUtil.getCache().set(Constants.CacheServiceType.ADMIN_MENU_ALL, (ArrayList<Menu>) list);
            logger.info("==菜单:menu缓存完成,缓存条数：{}", list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
