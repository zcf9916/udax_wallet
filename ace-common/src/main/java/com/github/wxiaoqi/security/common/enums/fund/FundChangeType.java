package com.github.wxiaoqi.security.common.enums.fund;

//基金产品状态
public enum FundChangeType {
	/** 未知*/
	UNKNOWN (0),
	/** 申购冻结*/
	FREEZE (1),
	/**申购解冻 */
	UNFREEZE(2),
	/** 手续费 */
	COMMISSION(3),
	/** 清盘收益增减 */
	PROFILT(4),
	/** 转出到币币账户 */
	DC_OUT(5),
	/** 从币币账户转入 */
	DC_IN(6);

	private final Integer value;

	private FundChangeType(Integer value) {
		this.value = value;
	}

	public Integer value() {
		return this.value;
	}

	public String toString() {
		return this.value.toString();
	}


}
