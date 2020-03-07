package com.udax.front.vo.rspvo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class RedPacketQueryRspVo implements Serializable {


	private boolean ifReceive = false;//是否抢过红包


	private boolean ifExpire = false;//是否失效
	/**
	 * 红包总数
	 */
	private Integer totalNum;

	/**
	 * 红包领取个数
	 */
	private Integer receiveNum;

	/**
	 * 红包类型 0. 普通红包  1. 随机红包
	 */
	private List<Serializable> list;


    @Getter
	@Setter
	public static class RedPacketLogRspVO implements Serializable{
		/**
		 * 用户名
		 */
		private String userName;

		/**
		 * 抢到的红包金额
		 */
		@JsonSerialize( using = BigDecimalCoinSerializer.class)
		private BigDecimal amount;

		/**
		 * 记录生成时间
		 */
		@JsonSerialize( using = DateToTimeStampSerializer.class)
		private Date createTime;
		private String symbol;
	}

}
