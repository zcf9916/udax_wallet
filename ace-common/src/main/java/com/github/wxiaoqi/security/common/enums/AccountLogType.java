package com.github.wxiaoqi.security.common.enums;

import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.entity.front.DcAssetAccount;
import com.github.wxiaoqi.security.common.entity.front.DcAssetAccountLog;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;

import java.util.Arrays;
import java.util.List;

public enum AccountLogType {
	/**
	 1.转账给平台其他用户 2.收到转账  3.转入基金账户 4.从基金账户转出
	 5.提现成功  6.转换币支出 7.转换币收入  8. 充值 9.用户给商户支付款项
	 10.商户收到用户支付的款项,并划转到可用余额里  25.商户收到用户支付的款项增加待结算资产 11.商户退款支出 12.用户收到退款
	 13.提现冻结 14.用户报价冻结 15.资产解冻  16.ud社区排队冻结  17.ud社区解冻  18.ud社区返回利润
	 19.ud 解锁支出  20.UD扣除手续费 21 UD社区分成 22节点奖分成 23 超级用户全球利润分配 24 UD社区充值赠送
	 25. 商户收到支付款(冻结)
	 26. 发红包   27 红包退回  28 红包转账   29 收到红包转账
	 30. 抢到红包  31.資產鎖定  32.鎖倉返回
	 33. 用户每日分成
	 */

	TRANSFER(1),
	RECEIVE_TRANSFER(2),
	ACCOUNT_TO_FUND(3),
	FUND_TO_ACCOUNT(4),
	WITHDRAW(5),
	TRANS_COIN_PAY(6),
	TRANS_COIN_INCOME(7),
	RECHARGE(8),
	PAY_MERCHANT(9)	,
	MERCHANT_SETTLE(10),
	MERCHANT_REFUND(11),
	RECEIVE_REFUND(12),
	WITHDRAW_FREEZE(13),
	QUOTED_FREEZE(14),
	ASSERT_UNFREEZE(15),
	UD_FREEZE(16),
	UD_UNFREEZE(17),
	UD_PROFIT(18),
	UD_UNLOCK(19),
	UD_CHARGE(20),
	UD_CMS(21),
	NODE_AWARD(22),
	GLOBAL_AWARD(23),
	UD_REG_SEND(24),
	MERCHANT_ADD_ASSERT(25),
	SEND_REDPACKETS(26),
	REDPACKETS_RETURN(27),
	RP_TRANSFER(28),
	RP_RECEIVE_TRANSFER(29),
	SNATCH_REDPACKETS(30),
	LOCK_ASSERT(31),
	UNLOCK_ASSERT(32),
	CMS_ADD_ASSERT(33)
	;
	private final Integer value;

	private AccountLogType(Integer value) {
		this.value = value;
	}
	
	public static AccountLogType valueOfMsgType(String value) {
		AccountLogType[] sendTypes = AccountLogType.values();
		for (AccountLogType sendMsgType : sendTypes) {
			if (sendMsgType.value.equals(value)) {
				return sendMsgType;
			}
		}
		return null;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public Integer value() {
		return this.value;
	}

	//是否商戶類型的
	public static boolean ifMerchantType(Integer value){
		if(value.intValue() == PAY_MERCHANT.value().intValue()
				||value.intValue() == MERCHANT_ADD_ASSERT.value().intValue()
				||value.intValue() == MERCHANT_SETTLE.value().intValue()
				||value.intValue() == MERCHANT_REFUND.value().intValue()
				||value.intValue() == RECEIVE_REFUND.value().intValue()){
            return  true;
		}
		return false;
	}


    //流水类型转换账单类型
	public static BillType getBillType(Integer value){
		AccountLogType type = null;
		for(AccountLogType code : AccountLogType.values()){
			if(code.value().equals(value)){
				type = code;
				break;
			}
		}

		switch (type) {
			case TRANSFER:
			case RECEIVE_TRANSFER:
			case RP_TRANSFER:
			case RP_RECEIVE_TRANSFER:
				return BillType.TRANSFER;//转账类型
			case ACCOUNT_TO_FUND:
			case FUND_TO_ACCOUNT:
			case MERCHANT_SETTLE:
			case RECEIVE_REFUND:
			case PAY_MERCHANT:
			case MERCHANT_REFUND:
			case WITHDRAW_FREEZE:
			case QUOTED_FREEZE:
			case ASSERT_UNFREEZE:
			case UD_UNLOCK:
			case UD_CHARGE:
			case UD_CMS:
			case NODE_AWARD:
			case GLOBAL_AWARD:
			case UD_PROFIT:
			case MERCHANT_ADD_ASSERT:
			case SEND_REDPACKETS:
			case REDPACKETS_RETURN:
			case SNATCH_REDPACKETS:
            case UNLOCK_ASSERT:
			case CMS_ADD_ASSERT:
				return BillType.TRADE;//交易
			case WITHDRAW:
				return BillType.WITHDRAW;//提现
			case RECHARGE:
			case UD_REG_SEND:
            case LOCK_ASSERT:
				return BillType.RECHARGE;//充值
			case TRANS_COIN_INCOME:
			case TRANS_COIN_PAY:
				return BillType.TRANS_COIN;//转币
			default:
				throw new BusinessException();
		}
	}

	//获取支出的所有类型id集合
	public static List<Integer> getPayList(){
		Integer[] arr = {SEND_REDPACKETS.value,RP_TRANSFER.value,TRANSFER.value,ACCOUNT_TO_FUND.value,WITHDRAW.value,TRANS_COIN_PAY.value,PAY_MERCHANT.value,MERCHANT_REFUND.value,UD_UNLOCK.value,UD_CHARGE.value};
		return Arrays.asList(arr);
	}
	//获取收入的所有类型id集合
	public static List<Integer> getIncomeList(){
		Integer[] arr = {CMS_ADD_ASSERT.value,LOCK_ASSERT.value,SNATCH_REDPACKETS.value,REDPACKETS_RETURN.value,RP_RECEIVE_TRANSFER.value,NODE_AWARD.value,GLOBAL_AWARD.value,RECEIVE_TRANSFER.value,FUND_TO_ACCOUNT.value,TRANS_COIN_INCOME.value,RECHARGE.value,MERCHANT_SETTLE.value,RECEIVE_REFUND.value,UD_CMS.value,UD_PROFIT.value,UD_REG_SEND.value,MERCHANT_ADD_ASSERT.value};

		return Arrays.asList(arr);
	}


	//获取账单类型对应的所有id集合(排除冻结资产的一些流水)
	public static List<Integer> getBillTypeList(Integer billType){
		Integer[] arr = {CMS_ADD_ASSERT.value,LOCK_ASSERT.value,UNLOCK_ASSERT.value,SNATCH_REDPACKETS.value,SEND_REDPACKETS.value,REDPACKETS_RETURN.value,NODE_AWARD.value,GLOBAL_AWARD.value,RP_TRANSFER.value,TRANSFER.value,RP_RECEIVE_TRANSFER.value,RECEIVE_TRANSFER.value,ACCOUNT_TO_FUND.value,FUND_TO_ACCOUNT.value,MERCHANT_SETTLE.value,RECEIVE_REFUND.value,
				PAY_MERCHANT.value,MERCHANT_REFUND.value,WITHDRAW.value,RECHARGE.value,TRANS_COIN_INCOME.value,TRANS_COIN_PAY.value,UD_UNLOCK.value,UD_CHARGE.value,UD_CMS.value,UD_PROFIT.value,UD_REG_SEND.value,MERCHANT_ADD_ASSERT.value};
		if(billType == null){
			return Arrays.asList(arr);
		}
		BillType type = null;
		for(BillType code : BillType.values()){
			if(code.value().equals(billType)){
				type = code;
				break;
			}
		}

		switch (type) {
			case TRANSFER:
				Integer[] rsp =  {RP_RECEIVE_TRANSFER.value,RP_TRANSFER.value,TRANSFER.value,RECEIVE_TRANSFER.value};//转账类型
				return Arrays.asList(rsp);
			case TRADE:
				Integer[] rsp1 =  {CMS_ADD_ASSERT.value,UNLOCK_ASSERT.value,SNATCH_REDPACKETS.value,SEND_REDPACKETS.value,REDPACKETS_RETURN.value,MERCHANT_ADD_ASSERT.value,NODE_AWARD.value,GLOBAL_AWARD.value,UD_PROFIT.value,ACCOUNT_TO_FUND.value,FUND_TO_ACCOUNT.value,MERCHANT_SETTLE.value,RECEIVE_REFUND.value,PAY_MERCHANT.value,MERCHANT_REFUND.value,UD_UNLOCK.value,UD_CHARGE.value,UD_CMS.value};//转账类型
				return Arrays.asList(rsp1);
			case WITHDRAW:
				Integer[] rsp2 =  {WITHDRAW.value};//转账类型
				return Arrays.asList(rsp2);
			case RECHARGE:
				Integer[] rsp3 =  {LOCK_ASSERT.value,RECHARGE.value,UD_REG_SEND.value};//充值类型
				return Arrays.asList(rsp3);
			case TRANS_COIN:
				Integer[] rsp4 =  {TRANS_COIN_INCOME.value,TRANS_COIN_PAY.value};//转账类型
				return Arrays.asList(rsp4);
			default:
				return Arrays.asList(arr);
		}
	}



    public static String getName(String name){

		return Resources.getMessage(name);
	}

	public static void setParam(AccountLogType type, DcAssetAccount afterAccount,DcAssetAccount preAccount, DcAssetAccountLog log){
		//设置收入支出方向
		switch (type) {
			case TRANSFER:
			case RP_TRANSFER:
			case ACCOUNT_TO_FUND:
			case WITHDRAW:
			case TRANS_COIN_PAY:
			case PAY_MERCHANT:
			case MERCHANT_REFUND:
			case WITHDRAW_FREEZE:
			case QUOTED_FREEZE:
			case UD_FREEZE:
			case UD_UNLOCK:
			case UD_CHARGE:
			case SEND_REDPACKETS:
			case LOCK_ASSERT:
				log.setDirection(DirectionType.PAY.value());
				break;
			case REDPACKETS_RETURN:
			case RP_RECEIVE_TRANSFER:
			case RECEIVE_TRANSFER:
			case FUND_TO_ACCOUNT:
			case TRANS_COIN_INCOME:
			case RECHARGE:
			case MERCHANT_SETTLE:
			case RECEIVE_REFUND:
			case ASSERT_UNFREEZE:
			case UD_UNFREEZE:
			case UD_PROFIT:
			case UD_CMS:
			case NODE_AWARD:
			case GLOBAL_AWARD:
			case UD_REG_SEND:
			case MERCHANT_ADD_ASSERT:
			case SNATCH_REDPACKETS:
			case UNLOCK_ASSERT:
			case CMS_ADD_ASSERT:
				log.setDirection(DirectionType.INCOME.value());
				break;
			default:
				throw new BusinessException();
		}
		log.setType(type.value());
        log.setPreAvailable(preAccount.getAvailableAmount());
		log.setPreFreeze(preAccount.getFreezeAmount());
		log.setPreTotal(preAccount.getTotalAmount());
		log.setPreWaitconfirm(preAccount.getWaitConfirmAmount());


		log.setAfterAvailable(afterAccount.getAvailableAmount());
		log.setAfterFreeze(afterAccount.getFreezeAmount());
		log.setAfterTotal(afterAccount.getTotalAmount());
		log.setAfterWaitconfirm(afterAccount.getWaitConfirmAmount());
	}

}
