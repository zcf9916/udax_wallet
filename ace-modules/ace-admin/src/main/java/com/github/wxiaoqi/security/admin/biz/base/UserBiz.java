package com.github.wxiaoqi.security.admin.biz.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.admin.util.GroupsIdUtil;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.constant.UserConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.Dept;
import com.github.wxiaoqi.security.common.entity.admin.Group;
import com.github.wxiaoqi.security.common.entity.admin.GroupLeader;
import com.github.wxiaoqi.security.common.entity.admin.User;
import com.github.wxiaoqi.security.common.entity.admin.WhiteExchInfo;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.admin.DeptMapper;
import com.github.wxiaoqi.security.common.mapper.admin.GroupLeaderMapper;
import com.github.wxiaoqi.security.common.mapper.admin.GroupMapper;
import com.github.wxiaoqi.security.common.mapper.admin.UserMapper;
import com.github.wxiaoqi.security.common.mapper.admin.WhiteExchInfoMapper;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.Query;

import tk.mybatis.mapper.entity.Example;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-06-08 16:23
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserBiz extends BaseBiz<UserMapper, User> {

    @Autowired
    private DeptMapper deptMapper;


    @Autowired
    private GroupLeaderMapper groupLeaderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private WhiteExchInfoMapper whiteExchInfoMapper;

    @Override
    public void insertSelective(User entity) {
        if (entity.getGroupId() == null) {
            throw new UserInvalidException(Resources.getMessage("BASE_USER_INSERT"));
        }
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            Long currentGroupId = groupMapper.selectGroup(BaseContextHandler.getUserID());
            //如果当前变更的GroupId 是当前用户关联的GroupId 则不能修改
            if (currentGroupId.equals(entity.getGroupId())) {
                throw new UserInvalidException(Resources.getMessage("BASE_CANNOT_CHANGE_PERMISSION"));
            }
        }
        setTopParentId(entity);
        Example example = new Example(User.class);
        example.createCriteria().andEqualTo("username", entity.getUsername());
        List<User> users = this.selectByExample(example);
        if (users.size() > 0) {
            throw new UserInvalidException(Resources.getMessage("BASE_LOGIN_ALREADY_EXISTS"));
        }
        String password = new BCryptPasswordEncoder(UserConstant.PW_ENCORDER_SALT).encode(entity.getPassword());
        entity.setPassword(password);
        setDeptId(entity);
        super.insertSelective(entity);
        setGroupAndLeader(entity.getUsername(), entity.getGroupId());
    }

    private void setTopParentId(User entity) {
        if(null ==entity.getGroupId()){
            throw new UserInvalidException(Resources.getMessage("BASE_NO_GROUP_ID"));
        }
        //表示当前选择的是管理员
        if (entity.getGroupId().equals(1L)) {
            entity.setTopParentId(AdminCommonConstant.ROOT);
        } else {
            Group group = groupMapper.selectByPrimaryKey(entity.getGroupId());
            //跟管理员同级其他类型管理员
            if (group.getParentId().equals(AdminCommonConstant.ROOT)){
                entity.setTopParentId(AdminCommonConstant.ROOT);
            }else {
                //交易所或者交易所下级
                Long  exchInfoId = whiteExchInfoMapper.selectExchInfoByGroupCode(group.getPath().split("/")[2]);
                if (exchInfoId ==null ){
                    //角色还没关联属性表
                    throw new UserInvalidException(Resources.getMessage("BASE_USER_EXCHINFO"));
                }
                entity.setTopParentId(exchInfoId);
            }

        }
    }

    private void setGroupAndLeader(String username, Long groupId) {
        User user = this.getUserByUsername(username);
        GroupLeader leader = new GroupLeader();
        leader.setUserId(user.getId());
        GroupLeader groupLeader = groupLeaderMapper.selectOne(leader);
        if (groupLeader != null) {
            groupLeader.setGroupId(groupId);
            groupLeaderMapper.updateByPrimaryKey(groupLeader);
        } else {
            groupMapper.insertGroupLeadersById(user.getGroupId(), user.getId());
        }
    }

    private void setDeptId(User entity) {
        String deptName = entity.getDeptName();
        Dept dept = new Dept();
        dept.setFullName(deptName);
        dept = deptMapper.selectOne(dept);
        entity.setDeptId(dept.getId());
    }

    @Override
    public void updateSelectiveById(User entity) {
        Long userID = BaseContextHandler.getUserID();
        Long currentGroupId = groupMapper.selectGroup(userID);
        setTopParentId(entity);
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            if (currentGroupId.equals(entity.getGroupId()) && entity.getGroupId().equals(currentGroupId) && userID.equals(entity.getId())) {
                setDeptId(entity);
                setGroupAndLeader(entity.getUsername(), entity.getGroupId());
                super.updateSelectiveById(entity);
            } else if (!currentGroupId.equals(entity.getGroupId()) && userID.equals(entity.getId())) {
                throw new UserInvalidException(Resources.getMessage("BASE_UPDATE_PERMISSION"));
            } else if (currentGroupId.equals(entity.getGroupId())) {
                throw new UserInvalidException(Resources.getMessage("BASE_CANNOT_CHANGE_PERMISSION"));
            } else {
                setDeptId(entity);
                setGroupAndLeader(entity.getUsername(), entity.getGroupId());
                super.updateSelectiveById(entity);
            }
        } else {
            setDeptId(entity);
            setGroupAndLeader(entity.getUsername(), entity.getGroupId());
            super.updateSelectiveById(entity);
        }
    }

    public void updateUserPasById(User entity) {
        if (entity.getId().equals(BaseContextHandler.getUserID())|| BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)){
            String password = new BCryptPasswordEncoder(UserConstant.PW_ENCORDER_SALT).encode(entity.getPassword());
            entity.setPassword(password);
            super.updateSelectiveById(entity);
        }else {
            throw new UserInvalidException(Resources.getMessage("BASE_PASSWORD"));
        }
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username
     * @return
     */
    public User getUserByUsername(String username) {
        User user = new User();
        user.setUsername(username);
        user = mapper.selectOne(user);

        return user;
    }

    @Override
    public void deleteById(Object id) {
        //只能是管理员才能删除
        if (BaseContextHandler.getUserID().equals(id) || !BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            throw new UserInvalidException(Resources.getMessage("BASE_USER_DELETE"));
        }
        //删除用户时解除关联关系
        GroupLeader leader = new GroupLeader();
        leader.setUserId((Long) id);
        groupLeaderMapper.delete(leader);
        mapper.deleteByPrimaryKey(id);

    }


//    public List<User> selectUserAndSubordinateUser() {
//        ArrayList<User> users = new ArrayList<>();
//        User user = this.selectById(BaseContextHandler.getUserID());
//        users.add(user);
//        List<Group> groups = groupMapper.selectGroupByParent(user.getGroupId());
//        if (groups.size() == 0) {
//            return users;
//        }
//        for (Group group : groups) {
//            selectSubordinate(users, group);
//        }
//        return users;
//    }

//    public void selectSubordinate(ArrayList<User> users, Group group) {
//
//        List<User> userList = userMapper.selectUserByGroupId(group.getId());
//        List<Group> groupList = groupMapper.selectGroupByParent(group.getId());
//        if (userList.size() == 0) {
//            if (groupList.size() == 0) {
//                return;
//            } else {
//                for (Group g : groupList) {
//                    this.selectSubordinate(users, g);
//                }
//            }
//        }
//        for (User user : userList) {
//            users.add(user);
//            List<Group> groups = groupMapper.selectGroupByParent(user.getGroupId());
//            if (groups.size() == 0) {
//                return;
//            }
//            for (Group g : groups) {
//                this.selectSubordinate(users, g);
//            }
//        }
//    }

    @Override
    public TableResultResponse<User> selectByQuery(Query query) {
        HashMap<String, Object> params = new HashMap<>();
        if (query.entrySet().size() > 0) {
            for (Map.Entry<String, Object> entry : query.entrySet()) {
                params.put(entry.getKey(), entry.getValue());
            }
        }
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            Long currentUserId = BaseContextHandler.getUserID();
            //根据登录用户查询所属群组以及下级群组
            List<Group> groups = GroupsIdUtil.getGroups(currentUserId, groupMapper);
            ArrayList<Long> groupIds = new ArrayList<>();
            for (Group group : groups) {
                groupIds.add(group.getId());
            }
            //查询加分页
            params.put("ids", groupIds);
        }
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<User> list = userMapper.selectUserByQuery(params);
        return new TableResultResponse<User>(result.getTotal(), list);
    }
}
