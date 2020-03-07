package com.github.wxiaoqi.security.admin.rest.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.github.wxiaoqi.security.admin.biz.base.MenuBiz;
import com.github.wxiaoqi.security.admin.biz.base.UserBiz;
import com.github.wxiaoqi.security.admin.service.PermissionService;
import com.github.wxiaoqi.security.admin.vo.AdminUser;
import com.github.wxiaoqi.security.admin.vo.MenuTree;
import com.github.wxiaoqi.security.common.entity.admin.Menu;
import com.github.wxiaoqi.security.common.entity.admin.User;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-06-08 11:51
 */
@RestController
@RequestMapping("user")
public class UserController extends BaseController<UserBiz, User> {
    @Autowired
    private PermissionService permissionService;

    @Autowired
    private MenuBiz menuBiz;



    @RequestMapping(value = "/front/info", method = RequestMethod.GET)
    public ResponseEntity<?> getUserInfo(String token) throws Exception {
        AdminUser userInfo = permissionService.getUserInfo(token, getLanguage());
        if (userInfo == null) {
            return ResponseEntity.status(401).body(false);
        } else {
            return ResponseEntity.ok(userInfo);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ObjectRestResponse<User> update(@RequestBody User entity) {
        if (StringUtils.isEmpty(entity.getPassword())) {
            baseBiz.updateSelectiveById(entity);
        } else {
            //修改密码
            baseBiz.updateUserPasById(entity);
        }
        return new ObjectRestResponse<User>();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ObjectRestResponse<User> get(@PathVariable Long id) {
        ObjectRestResponse<User> entityObjectRestResponse = new ObjectRestResponse<>();
        User user = baseBiz.selectById(id);
        user.setPassword("");
        entityObjectRestResponse.data(user);
        return entityObjectRestResponse;
    }

    @RequestMapping(value = "/front/menus", method = RequestMethod.GET)
    List<MenuTree> getMenusByUsername(String token) throws Exception {
        return permissionService.getMenusByUsername(token, getLanguage());
    }

    @RequestMapping(value = "/front/menu/all", method = RequestMethod.GET)
    public List<Menu> getAllMenus() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("language", getLanguage());
        return menuBiz.selectList(map);
    }

}
