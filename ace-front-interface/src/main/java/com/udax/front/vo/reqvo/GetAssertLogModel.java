package com.udax.front.vo.reqvo;

import com.udax.front.annotation.DateFormat;
import com.udax.front.annotation.DcCode;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

@SuppressWarnings("serial")
@Data
public class GetAssertLogModel extends PageInfo{



	@DateFormat( message="{DATEFORMAT_ERROR}")
	private String  beginDate;//开始时间


	@DateFormat( message="{DATEFORMAT_ERROR}")
	private String  endDate;//结束时间


	@DcCode(required = false)
	private String symbol;//代币


	private Integer billType;//账单类型

}
