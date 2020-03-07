package com.udax.front.vo.rspvo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class QuotationBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	// 交易日
//	private String tradingDay;
//	// 自然日
//	private String realDay;
//	// 最后修改时间
//	private String updateTime;
//	// 最后修改毫秒
//	private String updateMillisec;

	// 交易对代码
	private String symbol;

	// 最新成交价
	@JsonSerialize(using = BigDecimalCoinSerializer.class)
	private BigDecimal lastPrice;
	@JsonSerialize(using = BigDecimalCoinSerializer.class)
	private BigDecimal rose;//涨跌幅
	@JsonSerialize(using = BigDecimalCoinSerializer.class)
	private BigDecimal spread;//点差


	private Integer decimalPlaces;//保留多少小数位

//	// 最新成交量
//
//	private Double lastVolume;
//	// 今开盘
//
	@JsonSerialize(using = BigDecimalCoinSerializer.class)
	private BigDecimal openPrice;
//	// 最高价
//	private Double highestPrice;
//	// 最低价
//
//	private Double lowestPrice;
//	// 总成交金额
//
//	private Double totolTurnover;
//	// 总成交数量
//
//	private Double totolVolume;
//	// 买卖方向
//	private Integer orderDir;

//	private List<Double> bidPrices; // 申买价
//	private List<Double> bidVolumes; // 申买总量
//	private List<Double> bidLastVolumes; // 申买最新量
//	private List<Double> askPrices; // 申卖价
//	private List<Double> askVolumes; // 申卖总量
//	private List<Double> askLastVolumes; // 申卖最新量

//	private String publishTime; // 发布时间
//
//	private Long publishTimeStamp;// 发布时间戳
//
//	private Long updateTimeStamp;// 更新时间戳
//
//	public Long getPublishTimeStamp() {
//		return publishTimeStamp;
//	}
//
//	public void setPublishTimeStamp(Long publishTimeStamp) {
//		this.publishTimeStamp = publishTimeStamp;
//	}
//
//	public Long getUpdateTimeStamp() {
//		return updateTimeStamp;
//	}
//
//	public void setUpdateTimeStamp(Long updateTimeStamp) {
//		this.updateTimeStamp = updateTimeStamp;
//	}
//
//	public Integer getOrderDir() {
//		return orderDir;
//	}
//
//	public void setOrderDir(Integer orderDir) {
//		this.orderDir = orderDir;
//	}
//
//	public String getTradingDay() {
//		return tradingDay;
//	}
//
//	public void setTradingDay(String tradingDay) {
//		this.tradingDay = tradingDay;
//	}
//
//	public String getRealDay() {
//		return realDay;
//	}
//
//	public void setRealDay(String realDay) {
//		this.realDay = realDay;
//	}
//
//	public String getUpdateTime() {
//		return updateTime;
//	}
//
//	public void setUpdateTime(String updateTime) {
//		this.updateTime = updateTime;
//	}
//
//	public String getUpdateMillisec() {
//		return updateMillisec;
//	}
//
//	public void setUpdateMillisec(String updateMillisec) {
//		this.updateMillisec = updateMillisec;
//	}
//
//	public String getSymbol() {
//		return symbol;
//	}
//
//	public void setSymbol(String symbol) {
//		this.symbol = symbol;
//	}
//
//	public Double getLastPrice() {
//		return lastPrice;
//	}
//
//	public void AftertPrice(Double lastPrice) {
//		this.lastPrice = lastPrice;
//	}
//
//	public Double getLastVolume() {
//		return lastVolume;
//	}
//
//	public void setLastVolume(Double lastVolume) {
//		this.lastVolume = lastVolume;
//	}
//
//	public Double getOpenPrice() {
//		return openPrice;
//	}
//
//	public void setOpenPrice(Double openPrice) {
//		this.openPrice = openPrice;
//	}
//
//	public Double getHighestPrice() {
//		return highestPrice;
//	}
//
//	public void setHighestPrice(Double highestPrice) {
//		this.highestPrice = highestPrice;
//	}
//
//	public Double getLowestPrice() {
//		return lowestPrice;
//	}
//
//	public void setLowestPrice(Double lowestPrice) {
//		this.lowestPrice = lowestPrice;
//	}
//
//	public Double getTotolTurnover() {
//		return totolTurnover;
//	}
//
//	public void setTotolTurnover(Double totolTurnover) {
//		this.totolTurnover = totolTurnover;
//	}
//
//	public Double getTotolVolume() {
//		return totolVolume;
//	}
//
//	public void setTotolVolume(Double totolVolume) {
//		this.totolVolume = totolVolume;
//	}
//
//
//	public String getPublishTime() {
//		return publishTime;
//	}
//
//	public void setPublishTime(String publishTime) {
//		this.publishTime = publishTime;
//	}
//
//	@Override
//	public String toString() {
//		return "QuotationBean{" +
//				"tradingDay='" + tradingDay + '\'' +
//				", realDay='" + realDay + '\'' +
//				", updateTime='" + updateTime + '\'' +
//				", updateMillisec='" + updateMillisec + '\'' +
//				", symbol='" + symbol + '\'' +
//				", lastPrice=" + lastPrice +
//				", lastVolume=" + lastVolume +
//				", openPrice=" + openPrice +
//				", highestPrice=" + highestPrice +
//				", lowestPrice=" + lowestPrice +
//				", totolTurnover=" + totolTurnover +
//				", totolVolume=" + totolVolume +
//				", orderDir=" + orderDir +
//				", publishTime='" + publishTime + '\'' +
//				", publishTimeStamp=" + publishTimeStamp +
//				", updateTimeStamp=" + updateTimeStamp +
//				'}';
//	}
}
