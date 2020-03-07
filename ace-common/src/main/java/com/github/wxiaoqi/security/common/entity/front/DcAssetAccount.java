package com.github.wxiaoqi.security.common.entity.front;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.annotation.Sign;
import com.github.wxiaoqi.security.common.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 数字货币表
 */
@Getter
@Setter
@Table(name = "dc_asset_account")
public class DcAssetAccount extends BaseEntity {

	/**
	 * 用户id
	 */
	@Column(name = "user_id")
	@Sign
	private Long userId;

	/**
	 * 代币编码
	 */
	@Sign
	private String symbol;

	/**
	 * 总资产
	 */
	@Column(name = "total_amount")
	@Sign
	private BigDecimal totalAmount;

	/**
	 * 可用资产
	 */
	@Column(name = "available_amount")
	@Sign
	@JsonSerialize(using= BigDecimalCoinSerializer.class)
	private BigDecimal availableAmount;

	/**
	 * 冻结金额
	 */
	@Column(name = "freeze_amount")
	@Sign
	@JsonSerialize(using= BigDecimalCoinSerializer.class)
	private BigDecimal freezeAmount;

	/**
	 * 待结算金额
	 */
	@Column(name = "wait_confirm_amount")
	@Sign
	@JsonSerialize(using= BigDecimalCoinSerializer.class)
	private BigDecimal waitConfirmAmount;

	@Column(name = "create_time")
	private Date createTime;

	@Column(name = "update_time")
	private Date updateTime;
	
	/**
	 * 资金校验码
	 */
	private String umac;

	@Transient
	private FrontUser frontUser;
}