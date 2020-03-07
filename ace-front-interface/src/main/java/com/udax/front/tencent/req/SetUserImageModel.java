package com.udax.front.tencent.req;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

//设置用户头像
@Getter
@Setter
public class SetUserImageModel {

    @JSONField(name="From_Account")
    private String From_Account;//群主的 UserId（选填）


    @JSONField(name="ProfileItem")
    private PersonalInfo[] ProfileItem;//初始化成员列表

    @Getter
    @Setter
    public static class PersonalInfo{

        @JSONField(name="Tag")
        private String Tag;
        @JSONField(name="Value")
        private String Value;
    }


}
