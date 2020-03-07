package com.udax.front.vo.reqvo.merchant;

import com.udax.front.annotation.DateTimeFormat;
import com.udax.front.annotation.DcCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class MchBgTransferQueryModel implements Serializable {

	@DcCode(required = false)
	private String symbol;


	@DateTimeFormat
	private String beginDate;

	@DateTimeFormat
	private String endDate;

	private String receiveUserName;//收款方用戶名

}
