package com.udax.front.tencent.rsp;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TencentRspModel {

    @JSONField( name = "ActionStatus")
    private String ActionStatus;

    @JSONField( name = "ErrorInfo")
    private String ErrorInfo;//

    @JSONField( name = "ErrorCode")
    private String ErrorCode;//


}
