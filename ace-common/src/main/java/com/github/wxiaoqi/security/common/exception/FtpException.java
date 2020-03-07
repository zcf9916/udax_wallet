package com.github.wxiaoqi.security.common.exception;

import com.github.wxiaoqi.security.common.enums.ResponseCode;

/**
 * FTP异常
 * 
 * @author ShenHuaJie
 * @version 2016年5月20日 下午3:19:19
 */
@SuppressWarnings("serial")
public class FtpException extends BaseException {
    public FtpException() {
    }

    public FtpException(String message) {
        super(message);
    }

    public FtpException(String message, Throwable throwable) {
        super(message, throwable);
    }

    protected ResponseCode getCode() {
        return ResponseCode.UNKNOW_ERROR;
    }
}
