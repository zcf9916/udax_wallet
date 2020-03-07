package com.github.wxiaoqi.security.common.msg;

import com.github.wxiaoqi.security.common.enums.ResponseCode;

/**
 * Created by ace on 2017/8/23.
 */
public class BaseResponse {
    private int status = ResponseCode.OK.value();
    private String message;

    public BaseResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public BaseResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
