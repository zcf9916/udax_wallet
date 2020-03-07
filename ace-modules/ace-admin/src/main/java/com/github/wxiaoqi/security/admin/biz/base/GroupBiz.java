package com.github.wxiaoqi.security.admin.biz.base;

import com.github.wxiaoqi.security.admin.util.GroupsIdUtil;
import com.github.wxiaoqi.security.admin.vo.AuthorityMenuTree;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.*;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.admin.*;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import com.github.wxiaoqi.security.common.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-06-12 8:48
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GroupBiz extends BaseBiz<GroupMapper, Group> {

    @Autowired
    private ResourceAuthorityMapper resourceAuthorityMapper;
    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private GroupLeaderBiz groupLeaderBiz;

    @Autowired
    private ElementMapper elementMapper;

    @Autowired
    private WhiteExchInfoMapper whiteExchInfoMapper;

    @Override
    public void insertSelective(Group entity) {
        //角色编码唯一
        isGroupRepeat(entity);
        Integer count = groupMapper.selectGroupCode(entity.getCode());
        if (count>0){
            throw new UserInvalidException(Resources.getMessage("BASE_USER_GROUP_CODE"));
        }
        if (AdminCommonConstant.ROOT == entity.getParentId()) {
            entity.setPath("/" + entity.getCode());
        } else {
            Group parent = this.selectById(entity.getParentId());
            entity.setPath(parent.getPath() + "/" + entity.getCode());
        }
        super.insertSelective(entity);
    }

    private void isGroupRepeat(Group entity) {
        if (!org.apache.commons.lang3.StringUtils.isNotBlank(entity.getCode())){
            throw new UserInvalidException(Resources.getMessage("BASE_USER_GROUP"));
        }
    }

    public void updateSelectiveById(Group entity) {
        isGroupRepeat(entity);
        EntityUtils.setUpdatedInfo(entity);
        if (AdminCommonConstant.ROOT == entity.getParentId()) {
            entity.setPath("/" + entity.getCode());
        } else {
            Group parent = this.selectById(entity.getParentId());
            entity.setPath(parent.getPath() + "/" + entity.getCode());
        }
        mapper.updateByPrimaryKeySelective(entity);

    }


    /**
     * 变更群组关联的菜单
     *
     * @param groupId
     * @param menus
     */
    public void modifyAuthorityMenu(Long groupId, String[] menus) {
        isPermission(groupId);
        resourceAuthorityMapper.deleteByAuthorityIdAndResourceType(groupId + "", AdminCommonConstant.RESOURCE_TYPE_MENU);
        List<Menu> menuList = menuMapper.selectAll();
        Map<String, String> map = new HashMap<String, String>();
        for (Menu menu : menuList) {
            map.put(menu.getId().toString(), menu.getParentId().toString());
        }
        Set<String> relationMenus = new HashSet<String>();
        relationMenus.addAll(Arrays.asList(menus));
        ResourceAuthority authority = null;
        for (String menuId : menus) {
            findParentID(map, relationMenus, menuId);
        }
        for (String menuId : relationMenus) {
            authority = new ResourceAuthority(AdminCommonConstant.AUTHORITY_TYPE_GROUP, AdminCommonConstant.RESOURCE_TYPE_MENU);
            authority.setAuthorityId(groupId + "");
            authority.setResourceId(menuId);
            authority.setParentId("-1");
            resourceAuthorityMapper.insertSelective(authority);
        }
    }

    private void isPermission(Long groupId) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT) || !groupId.equals(1L)) {
            Long currentGroupId = groupMapper.selectGroup(BaseContextHandler.getUserID());
            //如果当前变更的GroupId 是当前用户关联的id 则不能修改
            if (currentGroupId.equals(groupId)) {
                throw new UserInvalidException(Resources.getMessage("BASE_USER_UNPDATE_PERMISSION"));
            }
        }
    }

    private void findParentID(Map<String, String> map, Set<String> relationMenus, String id) {
        String parentId = map.get(id);
        if (String.valueOf(AdminCommonConstant.ROOT).equals(id)) {
            return;
        }
        relationMenus.add(parentId);
        findParentID(map, relationMenus, parentId);
    }

    /**
     * 分配资源权限
     *
     * @param groupId
     * @param menuId
     * @param elementId
     */
    public void modifyAuthorityElement(Long groupId, int menuId, int elementId) {
        isPermission(groupId);
        ResourceAuthority authority = new ResourceAuthority(AdminCommonConstant.AUTHORITY_TYPE_GROUP, AdminCommonConstant.RESOURCE_TYPE_BTN);
        authority.setAuthorityId(groupId + "");
        authority.setResourceId(elementId + "");
        authority.setParentId("-1");
        resourceAuthorityMapper.insertSelective(authority);
    }

    /**
     * 移除资源权限
     *
     * @param groupId
     * @param menuId
     * @param elementId
     */
    public void removeAuthorityElement(Long groupId, Long menuId, Long elementId) {
        isPermission(groupId);
        //批量删除关联关系
        if (elementId == -1) {
            List<Element> elements = elementMapper.selectElementByMenuId(menuId + "");
            if (elements.size() == 0) {
                return;
            }
            for (Element element : elements) {
                deleteResourceAuthority(groupId, element.getId());
            }

        }
        //单个删除关联关系
        deleteResourceAuthority(groupId, elementId);
    }

    private void deleteResourceAuthority(Long groupId, Long elementId) {
        ResourceAuthority authority = new ResourceAuthority();
        authority.setAuthorityId(groupId + "");
        authority.setResourceId(elementId + "");
        authority.setParentId("-1");
        resourceAuthorityMapper.delete(authority);
    }


    /**
     * 获取群主关联的菜单
     *
     * @param groupId
     * @return
     */
    public List<AuthorityMenuTree> getAuthorityMenu(int groupId) {
        List<Menu> menus = menuMapper.selectMenuByAuthorityId(String.valueOf(groupId), AdminCommonConstant.AUTHORITY_TYPE_GROUP);
//        List<Menu> menus = menuMapper.selectAuthorityMenuByUserId(Integer.valueOf(BaseContextHandler.getUserID()));
        List<AuthorityMenuTree> trees = new ArrayList<AuthorityMenuTree>();
        AuthorityMenuTree node = null;
        for (Menu menu : menus) {
            node = new AuthorityMenuTree();
            node.setText(menu.getTitle());
            BeanUtils.copyProperties(menu, node);
            trees.add(node);
        }
        return trees;
    }

    /**
     * 获取群组关联的资源
     *
     * @param groupId
     * @return
     */
    public List<Integer> getAuthorityElement(int groupId) {
        ResourceAuthority authority = new ResourceAuthority(AdminCommonConstant.AUTHORITY_TYPE_GROUP, AdminCommonConstant.RESOURCE_TYPE_BTN);
        authority.setAuthorityId(groupId + "");
        List<ResourceAuthority> authorities = resourceAuthorityMapper.select(authority);
        List<Integer> ids = new ArrayList<Integer>();
        for (ResourceAuthority auth : authorities) {
            ids.add(Integer.parseInt(auth.getResourceId()));
        }
        return ids;
    }

    /**
     * 递归获取用户关联的群组
     *
     * @param userId
     * @return
     */

    public List<Group> getGroupByUserId(Long userId) {
        return GroupsIdUtil.getGroups(userId, groupMapper);
    }


    @Override
    public void deleteById(Object id) {
        //主键ID为id不允许删除
        if (id.equals(1)) {
            throw new UserInvalidException(Resources.getMessage("BASE_USER_DELETE_GROUP"));
        }
        //当前角色有关联用户不允许删除
        ArrayList<Long> groupsIds = new ArrayList<>();
        Example example = new Example(GroupLeader.class);
        example.createCriteria().andEqualTo("groupId", id);
        List<GroupLeader> groupLeaders = groupLeaderBiz.selectByExample(example);
        if (groupLeaders.size() > 0) {
            throw new UserInvalidException(Resources.getMessage("BASE_ASSOCIATED_USERS"));
        }

        Group g = mapper.selectByPrimaryKey(id);
        if (!StringUtils.isEmpty(g.getBroker())){
            WhiteExchInfo info = new WhiteExchInfo();
            info.setGroupId((Long) id);
            whiteExchInfoMapper.delete(info);
        }

        List<Group> groups = mapper.selectGroupByParent((Long) id);
        if (groups.size() > 0) {
            for (Group group : groups) {
                isGroupExist(group);
            }
        }
        //递归删除
        groupsIds.add((Long) id);
        if (groups.size() > 0) {
            for (Group group : groups) {
                addGroupsIds(group, groupsIds);
            }

        }

        for (Long groupsId : groupsIds) {
            mapper.deleteByPrimaryKey(groupsId);
        }
    }

    private void addGroupsIds(Group group, ArrayList<Long> groupsIds) {
        groupsIds.add(group.getId());
        List<Group> groups = mapper.selectGroupByParent(group.getId());
        if (groups.size() > 0) {
            for (Group g : groups) {
                addGroupsIds(g, groupsIds);
            }

        }
    }

    private void isGroupExist(Group group) {
        //当前角色有关联用户不允许删除
        Example example = new Example(GroupLeader.class);
        example.createCriteria().andEqualTo("groupId", group.getId());
        List<GroupLeader> groupLeaders = groupLeaderBiz.selectByExample(example);
        if (groupLeaders.size() > 0) {
            throw new UserInvalidException(Resources.getMessage("BASE_CURRENT_ASSOCIATE_USERS"));
        }
        List<Group> groups = mapper.selectGroupByParent(group.getId());
        if (groups.size() > 0) {
            for (Group g : groups) {
                this.isGroupExist(g);
            }
        }
    }

    public void modifyElementAuthority(Long groupId ,String[] menus,boolean check) {
        HashMap<String, Object> hashMap = new HashMap<>();
        List<String> list = Arrays.asList(menus);
        hashMap.put("ids", list);
        List<Element> elements = elementMapper.selectElementByMenuAll(hashMap);
        if (!StringUtil.listIsNotBlank(elements)) {
            return;
        }
        //选中状态
        if (check){
            elements.forEach(element -> {
                //删除有可能重复的数据
                deleteResourceAuthority(groupId, element.getId());
                ResourceAuthority aoub = new ResourceAuthority(AdminCommonConstant.AUTHORITY_TYPE_GROUP, AdminCommonConstant.RESOURCE_TYPE_BTN);
                aoub.setAuthorityId(groupId + "");
                aoub.setResourceId(element.getId() + "");
                aoub.setParentId("-1");
                resourceAuthorityMapper.insertSelective(aoub);
            });
        }else {
            //取消选中
            elements.forEach(element -> {
                deleteResourceAuthority(groupId, element.getId());
            });
        }
    }
}
