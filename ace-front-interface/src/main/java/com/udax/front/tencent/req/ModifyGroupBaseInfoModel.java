package com.udax.front.tencent.req;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

//修改群头像
@Getter
@Setter
public class ModifyGroupBaseInfoModel {

    @JSONField(name="GroupId")
    private String GroupId;//群组Id

    @JSONField(name="FaceUrl")
    private String FaceUrl;//群头像


}
