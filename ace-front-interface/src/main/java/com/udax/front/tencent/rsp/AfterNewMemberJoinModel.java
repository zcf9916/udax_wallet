package com.udax.front.tencent.rsp;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AfterNewMemberJoinModel {

    @JsonProperty("CallbackCommand")
    private String CallbackCommand;

    @JsonProperty("GroupId")
    private String GroupId;//操作的群 ID

    @JsonProperty("Operator_Account")
    private String Operator_Account;//// 操作者成员

    @JsonProperty("JoinType")
    private String JoinType;/// 入群方式：Apply（申请入群）；Invited（邀请入群）

    @JsonProperty("Type")
    private String Type;//


    @JsonProperty("MemberList")
    private AfterCreateGroupModel.MemberClass[] MemberList;//初始化成员列表

    @Getter
    @Setter
    public static class MemberClass{

        @JsonProperty("Member_Account")
        private String Member_Account;
    }


}
