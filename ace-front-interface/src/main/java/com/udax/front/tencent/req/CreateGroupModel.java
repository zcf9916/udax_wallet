package com.udax.front.tencent.req;


import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.udax.front.tencent.rsp.AfterCreateGroupModel;
import lombok.Getter;
import lombok.Setter;

//发送单对单消息
@Getter
@Setter
public class CreateGroupModel {

    @JSONField(name="Owner_Account")
    private String Owner_Account;//群主的 UserId（选填）

    @JSONField(name="Type")
    private String Type; // 群组类型：Private/Public/ChatRoom/AVChatRoom/BChatRoom（必填）
    @JSONField(name="Name")
    private String Name;// 群名称（必填）

    @JSONField(name="MemberList")
    private MemberClass[] MemberList;//初始化成员列表

    @Getter
    @Setter
    public static class MemberClass{

        @JSONField(name="Member_Account")
        private String Member_Account;
        @JSONField(name="Role")
        private String Role;
    }


}
