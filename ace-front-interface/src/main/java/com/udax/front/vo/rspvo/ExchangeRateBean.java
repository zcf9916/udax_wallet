package com.udax.front.vo.rspvo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ExchangeRateBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    //币种名称
	private String currencyName; //现金货币编码名称
//    //币种转黄金币的比例
//	private BigDecimal currencyRate; //现金货币对应的汇率
//	//币种转美元价格比例
//	private BigDecimal currencyUSDRate; //现金货币对应的汇率
	
	private Map<String,BigDecimal> rateMap=new HashMap<String,BigDecimal>();

//	public BigDecimal getCurrencyRate() {
//		return currencyRate;
//	}
//
//	public void setCurrencyRate(BigDecimal currencyRate) {
//		this.currencyRate = currencyRate;
//	}

	public Map<String, BigDecimal> getRateMap() {
		return rateMap;
	}

	public void setRateMap(Map<String, BigDecimal> rateMap) {
		this.rateMap = rateMap;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

}
