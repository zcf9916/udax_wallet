package com.udax.front.vo.rspvo.ud;

import com.github.wxiaoqi.security.common.enums.merchant.MchStatus;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class UDUserInfoRspVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userName;//用户名

	private String mobile;//手机

	private String visitCode;//邀请码
    
    private Integer isSetTradePwd;//是否设置了交易密码

    private Integer status;  // 0 锁定，1 正常


	private String firstName;//姓

	private String realName;//名

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

	private Integer isValid;  // 0未上传，1待认证，2已认证

	private String headPictureUrl;//头像地址

}
