package com.udax.front.vo.rspvo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.annotation.Sign;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class AccountListModel implements Serializable {


	/**
	 * 代币编码
	 */
	private String symbol;

	/**
	 * 总资产
	 */
	@JsonSerialize(using = BigDecimalCoinSerializer.class)
	private BigDecimal totalAmount;

	/**
	 * 可用资产
	 */
	@JsonSerialize(using = BigDecimalCoinSerializer.class)
	private BigDecimal availableAmount;

	/**
	 * 冻结金额
	 */
	@JsonSerialize(using = BigDecimalCoinSerializer.class)
	private BigDecimal freezeAmount;

	/**
	 * 待结算金额
	 */
	@JsonSerialize(using = BigDecimalCoinSerializer.class)
	private BigDecimal waitConfirmAmount;


    //	是否允许充币，0允许，1不允许
	private Integer canRecharge;

	//是否允许提币，0允许，1不允许
	private Integer canWithdraw;

	private Set<String> protocolTypeList;




}
