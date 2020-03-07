package com.github.wxiaoqi.security.common.entity.merchant;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Table;

import com.github.wxiaoqi.security.common.base.BaseEntity;

import lombok.Data;

@Data
@Table(name = "mch_pay_token")
public class MchPayToken{
	
	/**
	 * 商户订单表id
	 */
	@Column(name = "detail_id")
	private Long detailId;

	/**
	 * 支付币种
	 */
	@Column(name = "symbol")
	private String symbol;

	/**
	 * 支付数量
	 */
	@Column(name = "amount")
	private BigDecimal amount;
}
