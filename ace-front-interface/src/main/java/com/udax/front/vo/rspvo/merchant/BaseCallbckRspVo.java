package com.udax.front.vo.rspvo.merchant;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import com.github.wxiaoqi.security.common.enums.merchant.MchSignType;
import com.udax.front.vo.reqvo.merchant.MchPayTokenModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseCallbckRspVo implements Serializable {



	private String mchNo;// 商户号

	private String sign;// 签名

	private String notifyId; // 通知校验ID，商户可校验通知的真实性

//	private String amount;

//	private String nonceStr;// 随机字符串，不长于32位

//	private String symbol;// 交易币种
	List<MchPayTokenModel> tokenList;

	private String signType = MchSignType.HMAC_SHA256.toString();//签名算法,默认hmacsha256



	private String transNo;//平台流水号

	@JsonSerialize(using = DateToTimeStampSerializer.class)
	private Date time;//完成时间

	// @NotBlank(message="{20004}")
	// private String timestamp;//时间戳
	// @Length(min=1, max=32, message="{20004}")
	// private String randomStr;//随机字符串
}
