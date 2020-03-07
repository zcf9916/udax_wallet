package com.udax.front.vo.reqvo.merchant;

import com.udax.front.annotation.DateTimeFormat;
import com.udax.front.annotation.DcCode;
import com.udax.front.vo.reqvo.PageInfo;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class MchBgQueryRechargeModel extends PageInfo {

	@DcCode(required = false)
	private String symbol;


	@DateTimeFormat
	private String beginDate;

	@DateTimeFormat
	private String endDate;

}
