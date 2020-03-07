package com.github.wxiaoqi.security.common.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class UdaxLastPricesBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    //币种名称
	private Integer code; //现金货币编码名称

	private String msg; //现金货币编码名称

	private Long timestamp;

	private BigDecimal data;
	//private Map<String,QuotationBean> data;


}
