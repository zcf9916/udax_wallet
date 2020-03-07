package com.udax.front.vo.reqvo;

import com.udax.front.annotation.DcCode;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

@SuppressWarnings("serial")
@Data
public class SendRedPacketsModel implements Serializable{

	@NotNull(message = "{MERCHANT_AMOUNT_LIMIT}")
	@DecimalMin(value = "0.00000001", message = "{MERCHANT_AMOUNT_LIMIT}")
	@Digits(integer = 10,fraction = 8, message = "{MERCHANT_AMOUNT_LIMIT}")
	private BigDecimal amount;//红包金额

	@DcCode
	private String symbol;//代币

	@Length(min=0, max=50, message="{REMARK_TOOLONG}")
	private String remark;

    //接受者的用户名
	private String userID;

	//群号
	private String groupID;


	@NotNull(message = "{PARAM_ERROR}")
	@Max(value=100, message = "{PARAM_ERROR}")
	@Min(value=1, message = "{PARAM_ERROR}")
	private Integer number = 1;

	private Integer type;//红包类型   0. 普通红包  1. 随机红包

	private Integer sendType;//发送类型  0 个人红包   1 群红包

	@NotBlank(message="{TRADE_PWD_ERROR}")
	@Length(min=6, max=6, message="{TRADE_PWD_ERROR}")
	private String password;//支付密码


    private Long receiveUserId;

}
