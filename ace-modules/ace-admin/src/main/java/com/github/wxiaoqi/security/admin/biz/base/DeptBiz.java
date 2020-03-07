package com.github.wxiaoqi.security.admin.biz.base;


import java.util.List;

import com.github.wxiaoqi.security.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.entity.admin.Dept;
import com.github.wxiaoqi.security.common.entity.admin.User;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.admin.DeptMapper;

import tk.mybatis.mapper.entity.Example;

@Service
@Transactional(rollbackFor = Exception.class)
public class DeptBiz extends BaseBiz<DeptMapper, Dept> {
    @Autowired
    private UserBiz userBiz;

    @Override
    public void deleteById(Object id) {
        Example example = new Example(User.class);
        example.createCriteria().andEqualTo("deptId", id);
        List<User> users = userBiz.selectByExample(example);
        if (!StringUtil.listIsBlank(users)) {
            throw new UserInvalidException(Resources.getMessage("BASE_ASSOCIATE_DEPT_DELETE"));
        }
        mapper.deleteByPrimaryKey(id);
    }
}
