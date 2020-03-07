package com.udax.front.vo.rspvo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoinVo implements Serializable {

	/**
	 * 转换币种
	 */
	private String symbol;

//	/**
//	 * 目标币种
//	 */
//	private String targetSymbol;

	/**
	 * 可兑换的目标币种集合
	 */
	private List<TargetVo> targetList;

//	/**
//	 * 可转数量
//	 */
//	private BigDecimal availableAmount;



//	/**
//	 * 成交偏差最大值
//	 */
//	private BigDecimal offsetPrice;



	@Getter
	@Setter
	public class TargetVo implements Serializable{
		private String symbol;//币种
		private String chargeSymbol;//手续费币种
		//1.平台接收; 2.用户报价接收
		private Integer transferType;//用户报价类型

		/**
		 * 手续费类型
		 */
		private Integer chargeType;

		/**
		 * 手续费（根据chargeType） 固定或点差直接取该值，比例则乘以该值
		 */
		@JsonSerialize(using=BigDecimalCoinSerializer.class)
		private BigDecimal chargeAmount;

		/**
		 * 原货币单次最小转币量
		 */
		@JsonSerialize(using=BigDecimalCoinSerializer.class)
		private BigDecimal minTransAmount;

		/**
		 * 原货币单次最大转币量
		 */
		@JsonSerialize(using=BigDecimalCoinSerializer.class)
		private BigDecimal maxTransAmount;



		@JsonSerialize(using=BigDecimalCoinSerializer.class)
		private BigDecimal lastPrice;//最新报价,只有类型是用户报价的时候才会有
		@JsonSerialize(using=BigDecimalCoinSerializer.class)
		private BigDecimal availableAmount;//目标币种最大接收数量,只有用户报价的时候才会有
	}
}
