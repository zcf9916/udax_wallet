package com.github.wxiaoqi.security.common.enums;

public enum UploadType {
	/**
	 * 身份证正面
	 *
	 */
	ID_CARD_ZM(1,"idcard"),
	/**
	 * 身份证反面
	 *
	 */
	ID_CARD_FM(2,"idcard"),
	/**
	 * 头像
	 */
	HEAD_PICTURE(3,"head"),
	/**
	 * 商户认证信息正面上传
	 */
	MERCHANT_INFO_ZM(4,"merchant"),
	/**
	 * 商户认证信息反面上传
	 */
	MERCHANT_INFO_FM(5,"merchant"),
	/**
	 * 后台管理端文件上传
	 */
	ADMIN_PICTURE(6,"admin"),

	/**
	 * 腾讯群头像生成
	 */
	TENCENT_GROUP_INFO(7,"tencent");

	private final Integer value;

	private final String path;//上传的路径

	private UploadType(Integer value,String path) {
		this.value = value;
		this.path = path;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}

	public String path() {
		return this.path;
	}

	public static boolean isType(String value){
		for(UploadType type: UploadType.values()){
			if(type.value == Integer.valueOf(value)){
				return true;
			}
		}
		return false;
	}

	public static UploadType getType(String value){
		for(UploadType type: UploadType.values()){
			if(type.value == Integer.valueOf(value)){
				return type;
			}
		}
		return null;
	}


}
