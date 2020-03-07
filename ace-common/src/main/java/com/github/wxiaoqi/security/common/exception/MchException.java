package com.github.wxiaoqi.security.common.exception;


/**
 * @author ShenHuaJie
 * @version 2016年5月20日 下午3:19:19
 */
@SuppressWarnings("serial")
public class MchException extends BaseException {
	public MchException() {
	}

	public MchException(Throwable ex) {
		super(ex);
	}

	public MchException(String message) {
		super(message);
	}

	public MchException(String message, Throwable ex) {
		super(message, ex);
	}

}