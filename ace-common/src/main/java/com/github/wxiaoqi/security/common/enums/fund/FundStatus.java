package com.github.wxiaoqi.security.common.enums.fund;

//基金产品状态
public enum FundStatus {
	/** 已发布*/
	PUBLISH (1),
	/** 认购中 */
	SUBSCRIBE(2),
	/** 认购结束 */
	SUBSCRIBE_END(3),
	/** 已启动 */
	RUNNING(4),
	/** 清盘中 */
	SETTLEING(5),
	/** 已清盘 */
	SETTLED(6);

	private final Integer value;

	private FundStatus(Integer value) {
		this.value = value;
	}

	public Integer value() {
		return this.value;
	}

	public String toString() {
		return this.value.toString();
	}


}
