package com.udax.front.vo.rspvo.merchant;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RefundNotifyVo extends BaseCallbckRspVo {



    private String	mchOrderNo;//支付商户流水号

	private String transNo;//钱包退款流水号


	private String nonceStr;//随机字符串

	private String oriTransNo;//交易类型


	private String oriMchOrderNo;//交易类型

	private BigDecimal refundAmount;//退款金额

	private Integer refundAccountType;

}
