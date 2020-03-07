package com.udax.front.vo.rspvo;

import com.github.wxiaoqi.security.common.enums.MchResponseCode;
import com.github.wxiaoqi.security.common.enums.ResponseCode;

public class BlockchainWithdrawVo<T> {
    private int code = ResponseCode.OK.value();
    private String message;
    T data;
    boolean rel = true;

    public BlockchainWithdrawVo(int status, String message) {
        this.code = status;
        this.message = message;
    }

    public BlockchainWithdrawVo() {
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isRel() {
        return rel;
    }

    public void setRel(boolean rel) {
        this.rel = rel;
    }

    public BlockchainWithdrawVo<T> rel(boolean rel) {
        this.setRel(rel);
        return this;
    }

    public BlockchainWithdrawVo<T> data(T data) {
        this.setData(data);
        return this;
    }

    public BlockchainWithdrawVo<T> msg(String msg) {
        this.setMessage(msg);
        return this;
    }

    public BlockchainWithdrawVo<T> status(int status) {
        this.setCode(status);
        return this;
    }

    public BlockchainWithdrawVo<T> status(ResponseCode responseCode) {
        this.setCode(responseCode.value());
        this.setMessage(responseCode.msg());
        if(responseCode != ResponseCode.OK){
            this.setRel(false);
        }
        return this;
    }
    public BlockchainWithdrawVo<T> status(MchResponseCode responseCode) {
        this.setCode(responseCode.value());
        this.setMessage(responseCode.msg());
        if(responseCode != MchResponseCode.OK){
            this.setRel(false);
        }
        return this;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
