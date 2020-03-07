package com.udax.front.vo.reqvo;

import com.udax.front.annotation.DcCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *
 */
@Getter
@Setter
public class DcCodeModel implements Serializable{

	@DcCode
	private String dcCode;

	@Length(max = 10 )
	private String protocolType;//公链类型

}
