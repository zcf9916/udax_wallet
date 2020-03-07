package com.udax.front.vo.reqvo.merchant;

import com.github.wxiaoqi.security.common.enums.merchant.MchSignType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


@Getter
@Setter
public class OrderBaseModel implements Serializable {

	@NotBlank(message="{MERCHANT_ID_NULL}")
	@Length(min=10, max=10, message="{MERCHANT_ID_NULL}")
	private String mchNo;//商户号

	@NotBlank(message="{MERCHANT_SIGNTYPE_ERROR}")
	@Length(min=3, max=20, message="{MERCHANT_SIGNTYPE_ERROR}")
	private String signType = MchSignType.HMAC_SHA256.toString();//签名算法,默认hmacsha256
	@NotBlank(message="{MERCHANT_SIGNT_NOTNULL}")
	@Length( min=1,max=1024,message = "{MERCHANT_SIGNT_NOTNULL}")
	private String sign;//签名

	@NotBlank(message="{MERCHANT_NONCE_STR}")
	@Length( min=1,max=32,message = "{MERCHANT_NONCE_STR}")
	private String nonceStr;// 随机字符串，不长于32位

//	@NotBlank(message="{20004}")
//	private String timestamp;//时间戳
//	@Length(min=1, max=32, message="{20004}")
//	private String randomStr;//随机字符串
}
