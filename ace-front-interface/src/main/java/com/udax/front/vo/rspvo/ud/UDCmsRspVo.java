package com.udax.front.vo.rspvo.ud;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import com.github.wxiaoqi.security.common.constant.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Getter
@Setter
public class UDCmsRspVo implements Serializable {

	private String orderNo;



	private String userName;

	/**
	 * 方案名称
	 */
	private String levelName;

    private String symbol;
	/**
	 * 实际可以分配的利润
	 */
	@JsonSerialize(using = BigDecimalCoinSerializer.class)
	private BigDecimal profit;

	@JsonSerialize(using = BigDecimalCoinSerializer.class)
	private BigDecimal orderProfit;

	@JsonSerialize(using = BigDecimalCoinSerializer.class)
	private BigDecimal profitRate;

	@JsonSerialize(using = DateToTimeStampSerializer.class)
	private Date createTime;


}
