package com.udax.front.vo.rspvo;

import com.github.wxiaoqi.security.common.enums.merchant.MchStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
public class UserInfoRspVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userName;//用户名
	
	private String firstName;//姓
	
	private String realName;//名
	
	private String mobile;//手机

	private String visitCode;//邀请码
	
	private String email;//邮箱

	private Integer isWithdraw;

	/**
	 * 是否开启了短信验证
	 */
	private Integer isValidPhone;


	private String locationCode;//国家编码

	/**
	 * 是否开启了邮箱验证s
	 */
	private Integer isValidEmail;
    
    private Integer isSetTradePwd;//是否设置了交易密码

    private Integer isValid;  // 0未上传，1待认证，2已认证

	private String headPictureUrl;//头像地址

	private Integer mchStatus = MchStatus.NOAUTH.value();//商户状态0未认证;1.提交审核;2,正常;3,冻结;

	private String userSig;

}
