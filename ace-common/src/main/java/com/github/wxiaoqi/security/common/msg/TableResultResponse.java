package com.github.wxiaoqi.security.common.msg;

import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-06-14 22:40
 */
public class TableResultResponse<T> extends BaseResponse {

    public TableData<T> data;

    boolean rel = true;

    public boolean isRel() {
        return rel;
    }

    public void setRel(boolean rel) {
        this.rel = rel;
    }

    public TableResultResponse(long total, List<T> rows) {
        this.data = new TableData<T>(total, rows);
    }

    public TableResultResponse() {
        this.data = new TableData<T>();
    }

    TableResultResponse<T> total(int total) {
        this.data.setTotal(total);
        return this;
    }

    TableResultResponse<T> total(List<T> rows) {
        this.data.setRows(rows);
        return this;
    }

    public TableData<T> getData() {
        return data;
    }

    public void setData(TableData<T> data) {
        this.data = data;
    }


//    public long getTotal() {
//        return data.total;
//    }
//
//    public void setTotal(long total) {
//        this.data.total = total;
//    }
//
//    public List<T> getRows() {
//        return data.rows;
//    }
//
//    public void setRows(List<T> rows) {
//        this.data.rows = rows;
//    }



    public  class TableData<T> {
         Object otherProperty = null;
         long total;
         List<T> rows;

        public TableData(long total, List<T> rows) {
            this.total = total;
            this.rows = rows;
        }

        public TableData() {
        }

        public Object getOtherProperty() {
            return otherProperty;
        }

        public void setOtherProperty(Object otherProperty) {
            this.otherProperty = otherProperty;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public List<T> getRows() {
            return rows;
        }

        public void setRows(List<T> rows) {
            this.rows = rows;
        }
    }
}
