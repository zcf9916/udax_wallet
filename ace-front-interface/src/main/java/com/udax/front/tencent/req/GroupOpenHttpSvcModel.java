package com.udax.front.tencent.req;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

//设置用户头像
@Getter
@Setter
public class GroupOpenHttpSvcModel {

    @JSONField(name="GroupId")
    private String GroupId;//群组的 UserId（选填）


    @JSONField(name="User_Account")
    private String[] User_Account;//成员列表


}
