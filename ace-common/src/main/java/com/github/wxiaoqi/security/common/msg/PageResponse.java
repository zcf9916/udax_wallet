package com.github.wxiaoqi.security.common.msg;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-06-14 22:40
 */
@Getter
@Setter
public class PageResponse<T> extends BaseResponse {

    private long total;
    private List<T> rows;

    public PageResponse(List<T> rows,long total) {
       this.total = total;
       this.rows = rows;
    }


}
