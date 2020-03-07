package com.udax.front.vo.reqvo;

import com.udax.front.annotation.DcCode;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

@SuppressWarnings("serial")
@Data
public class QRCodeModel implements Serializable{

	@NotBlank(message="{QRCODE_TYPE_ERROR}")
	@Length(min=1, max=1, message="{QRCODE_TYPE_ERROR}")
	private String type;//二维码类型,参考QRCode枚举


	private String dcCode;//代币

	private BigDecimal amount;//金额;有可能不填
}
