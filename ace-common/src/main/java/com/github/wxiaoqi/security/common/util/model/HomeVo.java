package com.github.wxiaoqi.security.common.util.model;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HomeVo {
    private String name;
    private BigDecimal value;
    public HomeVo(String name,BigDecimal value){
        this.name=name;
        this.value=value;
    }
    public HomeVo(){

    }
}
