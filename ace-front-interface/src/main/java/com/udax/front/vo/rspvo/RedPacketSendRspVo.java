package com.udax.front.vo.rspvo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class RedPacketSendRspVo implements Serializable {



	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 红包类型 0. 普通红包  1. 随机红包
	 */
	private Integer type;

	/**
	 * 0 个人红包   1 群红包
	 */
	@Column(name = "send_type")
	private Integer sendType;

	/**
	 * 红包个数
	 */
	private Integer num;

	/**
	 * 总金额
	 */
	@JsonSerialize (using = BigDecimalCoinSerializer.class)
	private BigDecimal totalAmount;



	private String symbol;
}
