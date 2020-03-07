package com.udax.front.vo.reqvo.fund;


import com.udax.front.annotation.DateFormat;
import com.udax.front.vo.reqvo.PageInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * 查询用户申购基金列表
 */

@Getter
@Setter
public class FundAccountLogModel extends PageInfo {

	@DateFormat( message="{DATEFORMAT_ERROR}")
	private String  beginDate;//开始时间
	@DateFormat( message="{DATEFORMAT_ERROR}")
	private String  endDate;//结束时间


}
