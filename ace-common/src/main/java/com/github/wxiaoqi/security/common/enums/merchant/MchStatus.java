package com.github.wxiaoqi.security.common.enums.merchant;

//用户状态
public enum MchStatus {

	//0未认证;1.提交审核;2,正常;3,冻结;
	/**
	 * 未认证
	 *
	 */
	NOAUTH(0),
	/**
	 * 已提交待审核
	 */
	SUBMIT(1),
	/** 已激活 */
	ACTIVE(2),
	/** 冻结 */
	FREEZE(3);

	private final Integer value;

	private MchStatus(Integer value) {
		this.value = value;
	}

	public Integer value() {
		return this.value;
	}

	public String toString() {
		return this.value.toString();
	}


}
