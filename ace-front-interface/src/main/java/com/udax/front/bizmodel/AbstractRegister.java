package com.udax.front.bizmodel;

import com.udax.front.vo.FrontUserRegisterVo;

import javax.servlet.http.HttpServletRequest;

/**
 * 注册相关业务逻辑类
 * @ClassName: AbstractRegister
 * @Desc: TODO
 * @author: zhoucf
 * @date: 2018年5月24日 下午6:09:15
 * @version 1.0
 */
public abstract class AbstractRegister {


	public abstract void validateParam();

	public abstract FrontUserRegisterVo setParam(HttpServletRequest request);
}
