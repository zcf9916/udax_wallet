package com.github.wxiaoqi.security.admin.rest.base;

import com.github.wxiaoqi.security.admin.biz.base.GroupBiz;
import com.github.wxiaoqi.security.admin.biz.base.ResourceAuthorityBiz;
import com.github.wxiaoqi.security.admin.util.TreeUtil;
import com.github.wxiaoqi.security.admin.vo.AuthorityMenuTree;
import com.github.wxiaoqi.security.admin.vo.GroupTree;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.Group;
import com.github.wxiaoqi.security.common.entity.admin.ResourceAuthority;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("group")
public class GroupController extends BaseController<GroupBiz, Group> {
    @Autowired
    private GroupBiz groupBiz;

    @Autowired
    private ResourceAuthorityBiz resourceAuthorityBiz;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<Group> list(String name, String groupType) {
        if (StringUtils.isBlank(name) && StringUtils.isBlank(groupType)) {
            return new ArrayList<Group>();
        }
        Example example = new Example(Group.class);
        if (StringUtils.isNotBlank(name)) {
            example.createCriteria().andLike("name", "%" + name + "%");
        }
        if (StringUtils.isNotBlank(groupType)) {
            example.createCriteria().andEqualTo("groupType", groupType);
        }

        return baseBiz.selectByExample(example);
    }


    @RequestMapping(value = "/{id}/authority/menu", method = RequestMethod.POST)
    public ObjectRestResponse modifyMenuAuthority(@PathVariable Long id, String menuTrees) {
        if (StringUtils.isEmpty(menuTrees)) {
            resourceAuthorityBiz.deleteByAuthorityIdAndResourceType(id + "", AdminCommonConstant.RESOURCE_TYPE_MENU);
            ResourceAuthority authority = new ResourceAuthority();
            authority.setAuthorityId(id + "");
            authority.setParentId("-1");
            resourceAuthorityBiz.delete(authority);
        } else {
            String[] menus = menuTrees.split(",");
            baseBiz.modifyAuthorityMenu(id, menus);
        }
        return new ObjectRestResponse().rel(true);
    }

    @RequestMapping(value = "/{id}/authority/element", method = RequestMethod.POST)
    public ObjectRestResponse modifyElementAuthority(@PathVariable Long id, String menuTrees, boolean check) {
        if (StringUtils.isEmpty(menuTrees)) {
            ResourceAuthority authority = new ResourceAuthority();
            authority.setAuthorityId(id + "");
            authority.setParentId("-1");
            resourceAuthorityBiz.delete(authority);
        } else {
            String[] menus = menuTrees.split(",");
            baseBiz.modifyElementAuthority(id, menus, check);
        }
        return new ObjectRestResponse().rel(true);
    }

    @RequestMapping(value = "/{id}/authority/menu", method = RequestMethod.GET)
    public ObjectRestResponse<List<AuthorityMenuTree>> getMenuAuthority(@PathVariable int id) {

        return new ObjectRestResponse().data(baseBiz.getAuthorityMenu(id)).rel(true);
    }

    @RequestMapping(value = "/{id}/authority/element/add", method = RequestMethod.POST)
    public ObjectRestResponse addElementAuthority(@PathVariable Long id, int menuId, int elementId) {
        baseBiz.modifyAuthorityElement(id, menuId, elementId);
        return new ObjectRestResponse().rel(true);
    }

    @RequestMapping(value = "/{id}/authority/element/remove", method = RequestMethod.POST)
    public ObjectRestResponse removeElementAuthority(@PathVariable Long id, Long menuId, Long elementId) {
        baseBiz.removeAuthorityElement(id, menuId, elementId);
        return new ObjectRestResponse().rel(true);
    }

    @RequestMapping(value = "/{id}/authority/element", method = RequestMethod.GET)
    public ObjectRestResponse<List<Integer>> getElementAuthority(@PathVariable int id) {
        return new ObjectRestResponse().data(baseBiz.getAuthorityElement(id)).rel(true);
    }

    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    public List<GroupTree> tree(String name, String groupType) {
        if (StringUtils.isBlank(name) && StringUtils.isBlank(groupType)) {
            return new ArrayList<GroupTree>();
        }
        Example example = new Example(Group.class);
        if (StringUtils.isNotBlank(name)) {
            example.createCriteria().andLike("name", "%" + name + "%");
        }
        if (StringUtils.isNotBlank(groupType)) {
            example.createCriteria().andEqualTo("groupType", groupType);
        }
        List<Group> groupList = null;
        if (BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            groupList = baseBiz.selectByExample(example);
        } else {
            groupList = groupBiz.getGroupByUserId(BaseContextHandler.getUserID());
        }
        return getTree(groupList);
    }

    private List<GroupTree> getTree(List<Group> groups) {
        List<GroupTree> trees = new ArrayList<GroupTree>();
        GroupTree node = null;
        for (Group group : groups) {
            node = new GroupTree();
            node.setLabel(group.getName());
            node.setPath(group.getPath());
            BeanUtils.copyProperties(group, node);
            trees.add(node);
        }
        return TreeUtil.bulid(trees, AdminCommonConstant.ROOT);
    }

}
