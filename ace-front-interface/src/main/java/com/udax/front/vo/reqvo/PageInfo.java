package com.udax.front.vo.reqvo;

import java.io.Serializable;

import com.github.wxiaoqi.security.common.constant.Constants;

@SuppressWarnings("serial")
public class PageInfo implements Serializable{


	protected int limit;//每页查询几条数据

    protected int page;//当前页数

    public PageInfo() {
    }

    public PageInfo(int limit, int page) {
        this.limit = limit;
        this.page = page;
    }

    //每次返回的数量
    public int getLimit() {

        int tempLimit = limit;
        if( tempLimit > 50){
            tempLimit  = 50;
        }

      return  tempLimit < 1 ? Constants.DEFAULT_NUMBER_PERPAGE : tempLimit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getPage() {
        int tempPage = page;
//        if(tempPage > 100){
//            tempPage  = 100;
//        }
        return tempPage < 1 ? 1 : tempPage;
    }

    public void setPage(int page) {
        this.page = page;
    }

}
