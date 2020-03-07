package com.udax.front.vo.rspvo.casino;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
public class CasinoUserInfoRspVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userName;//用户名

	private String mobile;//手机

	private String visitCode;//邀请码
    
   // private Integer isSetTradePwd;//是否设置了交易密码

    private Integer status;  // 0 锁定，1 正常


	//private String firstName;//姓

	//private String realName;//名

	private String email;//邮箱

	//private Integer isWithdraw;

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



	/**
	 * 赌场账户
	 */
	private String casinoName;



	/**
	 * 直推有效用户
	 */
	private Integer directChild;

	/**
	 * 所有有效用户
	 */
	private Integer allChild;

//	/**
//	 * 总结算资金量(冻结的不算  一定要结算了之后才算有效)
//	 */
//	private BigDecimal totalAmount;


	/**
	 * 0 未激活用户   1 精英用户   2 副总经理  3  副总裁
	 */
	private Integer type;


}
