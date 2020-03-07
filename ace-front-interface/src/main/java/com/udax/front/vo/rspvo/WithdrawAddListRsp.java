package com.udax.front.vo.rspvo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class WithdrawAddListRsp implements Serializable {

	/**
	 * 提现流水号
	 */
	private Long id;

	/**
	 * 提现地址地址
	 */
	private String withdrawAdd;

	private String remark;

}
