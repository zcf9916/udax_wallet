package com.github.wxiaoqi.security.common.entity.admin;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import lombok.Data;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 用户各种交易
 */
@Data
public class UserTransactionModel {
	
	String userId;//用户

	@Transient
	private FrontUser frontUser;

	private String direction;//买入或卖出

	private String symbol;//代币代码

	private String userAddress;//提币或充币地址

	private String rechargeAmount;//提币金额

	private String rechargeAmountTotal;//提币金额总和

	private String transactionAmount;//交易数量

	private String price;//价格
	@JsonSerialize(using= BigDecimalCoinSerializer.class)
	private BigDecimal transactionTotal;//交易总量

	private String transNo;//订单号

	private Date updateTime;//操作时间

	@JsonSerialize(using= BigDecimalCoinSerializer.class)
	private BigDecimal chargeAmount;//手续费收益总和

	private Long id;
	@JsonSerialize(using= BigDecimalCoinSerializer.class)
	private BigDecimal totalAmount;//操作总金额(如转账等)
	@JsonSerialize(using= BigDecimalCoinSerializer.class)
	private BigDecimal expendAmount;//手续费支出总和
}
