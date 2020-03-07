package com.udax.front.vo.rspvo.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawNotifyVo extends BaseCallbckRspVo {

	private String blockOrderId;//区块链事务Id;

    private String	mchOrderNo;//提现商户流水号


	private String nonceStr;//随机字符串

	private String withdrawAdd;//提现地址

	private Integer status;//提现状态  0.失败  1.成功

}
