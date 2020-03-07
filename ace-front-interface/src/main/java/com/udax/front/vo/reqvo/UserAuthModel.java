package com.udax.front.vo.reqvo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


@Data
public class UserAuthModel implements Serializable {

//	@NotBlank(message="{COUNTRY_IS_NULL}")
//	private String countryName;
	@NotBlank(message="{IDCARD_IS_NULL}")
	@Length(min=1, max=30, message="{IDCARD_LENGTH}")
	private String idCard;
	@NotBlank(message="{NAME_IS_NULL}")
	@Length(min=1, max=25, message="{NAME_LENGTH}")
	private String firstName;
	@NotBlank(message="{NAME_IS_NULL}")
	@Length(min=1, max=25, message="{NAME_LENGTH}")
	private String realName;
	@NotBlank(message="{IDCARDIMG_IS_NULL}")
	@Length(min=1, max=1024, message="{IDCARDIMG_IS_NULL}")
	private String idCardImgZm;
	@NotBlank(message="{IDCARDIMG_IS_NULL}")
	@Length(min=1, max=1024, message="{IDCARDIMG_IS_NULL}")
	private String idCardImgFm;


}
