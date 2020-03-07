package com.udax.front.vo.rspvo.merchant;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PayNotifyVo extends BaseCallbckRspVo {



    private String	mchOrderNo;//支付商户流水号


	private String nonceStr;//随机字符串

	private String tradeType;//交易类型

	private BigDecimal chargeAmount;//收取手续费数量

}
