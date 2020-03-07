package com.udax.front.vo.rspvo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class WithdrawRecordModel implements Serializable {

	/**
	 * 提现流水号
	 */
	private String transNo;

	/**
	 * token地址
	 */
	private String userAddress;


	/**
	 * 提现金额
	 */
	@JsonSerialize(using = BigDecimalCoinSerializer.class)
	private BigDecimal tradeAmount;

	/**
	 * 区块链转账事物id
	 */
	private String transactionId;

	/**
	 * 货币编码
	 */
	private String symbol;

	/**
	 * 基础代币编码
	 */
	private String basicSymbol;

	/**
	 * 提现手续费
	 */
	@JsonSerialize(using = BigDecimalCoinSerializer.class)
	private BigDecimal chargeAmount;

	/**
	 * 到账金额
	 */
	@JsonSerialize(using = BigDecimalCoinSerializer.class)
	private BigDecimal arrivalAmoumt;

	@JsonSerialize(using = DateToTimeStampSerializer.class)
	private Date createTime;


	/**
	 * 提现状态1:待审核,2:已审核待转账，3:已提现
	 */
	private Integer status;

	/**
	 * 区块确认数
	 */
	private Integer confirmations;

}
