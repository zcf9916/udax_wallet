package com.github.wxiaoqi.security.common.util;


import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

/**
 * 查询参数
 */
@Data
public class Query extends LinkedHashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	//当前页码
    private int page = 1;
    //每页条数
    private int limit = 10;

    private String orderByInfo;

    public Query(){
    }


    public Query(Map<String, Object> params){
        this.putAll(params);
        //分页参数
        if(params.get("page")!=null) {
            this.page = Integer.parseInt(params.get("page").toString());
        }
        if(params.get("limit")!=null) {
            this.limit = Integer.parseInt(params.get("limit").toString());
        }
        if(params.get("orderByInfo")!=null) {
            this.orderByInfo = (String)(params.get("orderByInfo"));
        }
        this.remove("page");
        this.remove("limit");
        this.remove("orderByInfo");
    }

}
