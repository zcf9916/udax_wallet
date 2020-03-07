package com.udax.front.vo.rspvo.ud;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.entity.ud.HOrderDetail;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
public class UDIndexRspVo implements Serializable {


    private Integer userLevel;//用户等级

	@JsonSerialize( using = BigDecimalCoinSerializer.class)
	private BigDecimal pos = BigDecimal.ZERO;

//	@JsonSerialize( using = BigDecimalCoinSerializer.class)
//	private BigDecimal pow = BigDecimal.ZERO;
	@JsonSerialize( using = BigDecimalCoinSerializer.class)
	private BigDecimal udxAssert = BigDecimal.ZERO;

	@JsonSerialize( using = BigDecimalCoinSerializer.class)
	private BigDecimal udAssert = BigDecimal.ZERO;


	private String udxSymbol;

	private String udSymbol;

	private String pow;

	private Integer currentAmount = 0 ;

	private Integer directChild = 0;

	private Integer allChild = 0;

	private Integer expire = 0;//有效期

	private Integer autoInvest;// 0.没开启自动复投   1.开启自动复投

    @JsonSerialize( using = BigDecimalCoinSerializer.class)
	private BigDecimal unlockAmount;

	private UDIndexLevelRspVo levelRspVo;


	private UDOrderRspVo orderRspVo;

	private UDCurrentQueueRspVo queueRspVo;
}
