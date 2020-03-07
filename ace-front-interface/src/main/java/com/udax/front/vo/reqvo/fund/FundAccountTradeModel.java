package com.udax.front.vo.reqvo.fund;

import com.udax.front.annotation.DcCode;
import com.udax.front.vo.reqvo.PageInfo;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 查询用户申购基金列表
 */
@Getter
@Setter
public class FundAccountTradeModel extends PageInfo {

	@NotNull(message = "{AMOUNT_MUST_NOT_ZERO}")
	@DecimalMin(value = "0.00000001", message = "{MIN_AMOUNT}")
	@Digits(integer = 10,fraction = 8, message = "{FRACTIONFRACTION}")
	private BigDecimal amount;//转入金额

	@Length(min=1, max=20, message="{AMOUNT_MUST_NOT_ZERO}")
	@DcCode
	private String dcCode;//币种

	private int tradeType;//参考 fundChangeType

	@NotNull(message = "{PAY_PASSWORD_WRONG}")
	@Length( min =6 ,max = 6,message = "{PAY_PASSWORD_WRONG}")
	private String payPassword;//支付密码


}
