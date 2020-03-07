package com.udax.front.vo.reqvo.fund;

import com.udax.front.annotation.DateFormat;
import com.udax.front.vo.reqvo.PageInfo;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
/**
 * 查询用户申购基金列表
 */
@SuppressWarnings("serial")
@Getter
@Setter
public class UserFundListModel extends PageInfo {


	@DateFormat( message="{DATEFORMAT_ERROR}")
	private String  beginDate;//开始时间


	@DateFormat( message="{DATEFORMAT_ERROR}")
	private String  endDate;//结束时间

	@Length(min=0, max=30, message="{FUND_NAME_LENGTH}")
	private String  fundName;//基金名称

	private Integer  status;//基金状态
}
