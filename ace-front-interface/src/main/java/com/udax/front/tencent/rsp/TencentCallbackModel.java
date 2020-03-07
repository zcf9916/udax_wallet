package com.udax.front.tencent.rsp;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TencentCallbackModel implements Serializable {

    @JsonProperty("CallbackCommand")
    private String CallbackCommand;

    @JsonProperty("GroupId")
    private String GroupId;//操作的群 ID


    @JsonProperty("Operator_Account")
    private String Operator_Account;//发起创建群组请求的操作者 Identifier

    @JsonProperty("Owner_Account")
    private String Owner_Account;//请求创建的群的群主 Identifier

    @JsonProperty("Type")
    private String Type;//

    @JsonProperty("Name")
    private String Name;//

    @JsonProperty("MemberList")
    private MemberClass[] MemberList;//初始化成员列表



    @JsonProperty("NewMemberList")
    private MemberClass[] NewMemberList;//新增成员列表

    @JsonProperty("ExitType")
    private String ExitType;/// 成员离开方式：Kicked-被踢；Quit-主动退群


    @JsonProperty("ExitMemberList")
    private MemberClass[] ExitMemberList;//初始化成员列表


    @JsonProperty("JoinType")
    private String JoinType;/// 入群方式：Apply（申请入群）；Invited（邀请入群）



    @Getter
    @Setter
    public static class MemberClass{

        @JsonProperty("Member_Account")
        private String Member_Account;
    }

}
