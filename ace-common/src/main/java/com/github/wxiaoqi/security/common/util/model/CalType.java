package com.github.wxiaoqi.security.common.util.model;

import lombok.Data;

import java.io.Serializable;

//计价类型
@Data
public class CalType implements Serializable {

    private String dictLabel;//描述
    private String dictValue;//对应的值

}