package com.udax.front.vo.rspvo.merchant;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import com.github.wxiaoqi.security.common.constant.Constants;

import com.github.wxiaoqi.security.common.enums.merchant.MchSignType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseMchRspVo implements Serializable {



	private String mchNo;// 商户号

	private String sign;// 签名

	@JsonSerialize(using = ToStringSerializer.class)
	private Long notifyTime = new Date().getTime(); // 通知时间

//	private String notifyId; // 通知校验ID，商户可校验通知的真实性

	private String charset = Constants.CHAR_SET; // 编码方式

	private String version = Constants.VERSION; // 版本号

	@JsonSerialize(using = BigDecimalCoinSerializer.class)
	private BigDecimal amount;

	private String nonceStr;// 随机字符串，不长于32位

	private String symbol;// 交易币种

	private String signType = MchSignType.HMAC_SHA256.toString();//签名算法,默认hmacsha256
	// @NotBlank(message="{20004}")
	// private String timestamp;//时间戳
	// @Length(min=1, max=32, message="{20004}")
	// private String randomStr;//随机字符串
}
