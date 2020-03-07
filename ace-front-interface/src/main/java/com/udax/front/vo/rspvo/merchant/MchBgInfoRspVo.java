package com.udax.front.vo.rspvo.merchant;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class MchBgInfoRspVo implements Serializable {



	private String bindAddress;//收入

	private String withdrawCallback;//支出

	private String rechargeCallback;

	private String mchName;//商戶名


	private String secretKey;//秘钥

	private Integer isValidPhone;

	private Integer isValidEmail;

    @JsonSerialize(using = ToStringSerializer.class)
	private Long mchNo;

	private String mobile;

	private String email;

	private String exInfo;//白標標示
}
