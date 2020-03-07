package com.udax.front.tencent.rsp;


import com.alibaba.fastjson.annotation.JSONField;
import com.udax.front.tencent.req.MultiMsgModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupOpenHttpSvcRspModel {

    @JSONField(name="ActionStatus")
    private String ActionStatus;//群组的 UserId（选填）


    @JSONField(name="ErrorInfo")
    private String ErrorInfo;//成员列表


    @JSONField(name="ErrorCode")
    private String ErrorCode;//成员列表


    @JSONField(name="UserIdList")
    private List<MsgBodyClass>  UserIdList;


    @Getter
    @Setter
    public static class MsgBodyClass {

        @JSONField(name="Member_Account")
        private String Member_Account;//自定义类型

        @JSONField(name="Role")
        private String Role;

    }

}
