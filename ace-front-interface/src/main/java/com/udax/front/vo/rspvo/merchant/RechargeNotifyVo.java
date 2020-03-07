package com.udax.front.vo.rspvo.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RechargeNotifyVo extends BaseCallbckRspVo {

	private String blockOrderId;//区块链事务Id;

    private String userAddress;//充值地址

    private String userId;//用户标示
}
