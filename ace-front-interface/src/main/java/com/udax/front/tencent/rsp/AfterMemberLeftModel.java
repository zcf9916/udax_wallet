package com.udax.front.tencent.rsp;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AfterMemberLeftModel {

    @JsonProperty("CallbackCommand")
    private String CallbackCommand;

    @JsonProperty("GroupId")
    private String GroupId;//操作的群 ID

    @JsonProperty("Type")
    private String Type;//

    @JsonProperty("Operator_Account")
    private String Operator_Account;//// 操作者成员

    @JsonProperty("ExitType")
    private String ExitType;/// 成员离开方式：Kicked-被踢；Quit-主动退群


    @JsonProperty("ExitMemberList")
    private AfterCreateGroupModel.MemberClass[] ExitMemberList;//初始化成员列表

    @Getter
    @Setter
    public static class MemberClass{

        @JsonProperty("Member_Account")
        private String Member_Account;
    }


}
