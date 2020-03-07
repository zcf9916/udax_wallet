package com.udax.front.vo.reqvo;

import com.udax.front.annotation.DcCode;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 */
@Getter
@Setter
public class CheckRechargeAddModel implements Serializable{

	@NotBlank(message="{USER_ADD_NULL}")
	@Length(min=1, max=1024, message="{USER_ADD_NULL}")
	String userAddress;

	@NotBlank(message="{DCCODE_LENGTH}")
	@Length(min=1, max=20, message="{DCCODE_LENGTH}")
	@DcCode
	String symbol;


	private String protocolType;

	private String tag;

}
