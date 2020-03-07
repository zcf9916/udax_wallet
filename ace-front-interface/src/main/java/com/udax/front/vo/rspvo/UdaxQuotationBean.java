package com.udax.front.vo.rspvo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class UdaxQuotationBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    //币种名称
	private Integer code; //现金货币编码名称

	private String msg; //现金货币编码名称

	private Long timestamp;

	private Map<String,Object> data;
	//private Map<String,QuotationBean> data;


}
