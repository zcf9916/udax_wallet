package com.github.wxiaoqi.security.common.msg;

import com.github.wxiaoqi.security.common.enums.MchResponseCode;
import com.github.wxiaoqi.security.common.enums.ResponseCode;

/**
 * Created by Ace on 2017/6/11.
 */
public class ObjectRestResponse<T> extends BaseResponse {

	T data;
	boolean rel = true;

	public boolean isRel() {
		return rel;
	}

	public ObjectRestResponse() {
	}

	public ObjectRestResponse(boolean rel) {
		this.rel = rel;
	}

	public void setRel(boolean rel) {
		this.rel = rel;
	}

	public ObjectRestResponse<T> rel(boolean rel) {
		this.setRel(rel);
		return this;
	}

	public ObjectRestResponse<T> data(T data) {
		this.setData(data);
		return this;
	}

	public ObjectRestResponse<T> msg(String msg) {
		this.setMessage(msg);
		return this;
	}

	public ObjectRestResponse<T> status(int status) {
		this.setStatus(status);
		return this;
	}

	public ObjectRestResponse<T> status(ResponseCode responseCode) {
		this.setStatus(responseCode.value());
		this.setMessage(responseCode.msg());
		if(responseCode != ResponseCode.OK){
			this.setRel(false);
		}
		return this;
	}
	public ObjectRestResponse<T> status(MchResponseCode responseCode) {
		this.setStatus(responseCode.value());
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
