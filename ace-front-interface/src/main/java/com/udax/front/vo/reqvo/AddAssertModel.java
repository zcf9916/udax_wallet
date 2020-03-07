package com.udax.front.vo.reqvo;

import com.udax.front.annotation.DcCode;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 修改密码model
 */
@Data
public class AddAssertModel implements Serializable{

    @DcCode
	String dcCode;
	BigDecimal amount;
	String userName;

	String type;
}
