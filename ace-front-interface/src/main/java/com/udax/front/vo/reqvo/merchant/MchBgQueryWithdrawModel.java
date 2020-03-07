package com.udax.front.vo.reqvo.merchant;

import com.udax.front.annotation.DateTimeFormat;
import com.udax.front.annotation.DcCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
public class MchBgQueryWithdrawModel implements Serializable {

	@DcCode(required = false)
	private String symbol;


	@DateTimeFormat
	private String beginDate;

	@DateTimeFormat
	private String endDate;

	private String userAddress;//提幣地址

}
