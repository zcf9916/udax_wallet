package com.github.wxiaoqi.security.common.enums;

public enum CommissionConfigType {
	FIRST_LEVEL_USER(1),//第一级用户
	SECOND_LEVEL_USER(2),//第二级用户
	THIRD_LEVEL_USER(3),//第三级用户
	RECOMMENDED_USER_SHARE(4),//推荐用户分成比例
	EXCH_PROPORTION(5);//白标分成比例
	private final Integer value;

	private CommissionConfigType(Integer value) {
		this.value = value;
	}

	public Integer value() {
		return this.value;
	}


	public String toString() {
		return this.value.toString();
	}
}
