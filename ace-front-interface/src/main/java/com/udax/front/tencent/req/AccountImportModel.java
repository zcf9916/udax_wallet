package com.udax.front.tencent.req;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

//单个帐号导入接口
@Getter
@Setter
public class AccountImportModel {

    @JSONField(name="Identifier")
    private String Identifier;//

    @JSONField(name="Nick")
    private String Nick; //



}
