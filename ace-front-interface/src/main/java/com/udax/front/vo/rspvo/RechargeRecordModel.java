package com.udax.front.vo.rspvo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class RechargeRecordModel implements Serializable {

	private String userAddress;

	/**
	 * 充值金额
	 */
	@JsonSerialize(using = BigDecimalCoinSerializer.class)
	private BigDecimal rechargeAmount;

	/**
	 * 货币编码
	 */
	private String symbol;

	@JsonSerialize(using = DateToTimeStampSerializer.class)
	private Date createTime;

	/**
	 * 充值状态1:充值成功,2:充值失败
	 */
	private Boolean status;

	@JsonSerialize(using=ToStringSerializer.class)
	private Long orderId;

	/**
	 * 区块链订单ID
	 */

	private String blockOrderId;

	/**
	 * 区块链汇聚手续费币种
	 */
	private String feeSymbol;

	/**
	 * 区块链汇聚手续费
	 */
	@JsonSerialize(using = BigDecimalCoinSerializer.class)
	private BigDecimal rechargeFee;

}
