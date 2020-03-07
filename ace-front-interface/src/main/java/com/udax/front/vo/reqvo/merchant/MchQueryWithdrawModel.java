package com.udax.front.vo.reqvo.merchant;

import com.udax.front.annotation.DcCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;


@Getter
@Setter
public class MchQueryWithdrawModel extends OrderBaseModel {

	private String transNo;//平台流水号

	private String mchOrderNo;//商户订单号

}
