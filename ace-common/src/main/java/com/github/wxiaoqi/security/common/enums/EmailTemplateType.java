package com.github.wxiaoqi.security.common.enums;

public enum EmailTemplateType {

    /**
     * 释放资产
     */
    UNLOCK_ASSERT_TEMPLATE("UnlockAssertTemplate"),
    /**
     * 公用模板
     */
    COMMON_TEMPLATE("CommonTemplate"),
    /**
     * 用户注册验证码
     */
    USER_REG("Register"),
    /**
     * 忘记密码
     */
    FORGET_PASSWORD("ForgetPassword"),
    /**
     * 绑定邮箱
     */
    BIND_EMAIL("BindEmail"),
    /**
     * 提币到账
     */
    WITHDRAW_COIN_OUT("WithdrawCoin"),
    /**
     * 充值到账
     */
    RECHARGE_COIN_IN("RechargeCoin"),
    /**
     * 用户审核通知
     */
    USER_AUDIT("User_Audit"),

    /**
     * 商家审核通知
     */
    MERCHANT_REVIEW("Merchant_Review"),
    /**
     * 提币审核提醒 ，通知提币人员
     */
    WITHDRAW_AUDIT_REMIND("WithdrawAuditRemind"),
    /**
     * 提币审核结果通知
     */
    WITHDRAW_AUDIT_NOTICE("WithdrawAuditNotice"),
    /**
     * 用户审核通知审核人员
     */
    USER_AUDIT_REMIND("UserAuditRemind"),
    /**
     * 打回提币申请
     */
    WITHDRAW_BACK_AUTH("Withdraw_Backauth"),
    /**
     * 用户付款
     */

    USER_PAYMENT("UserPayment"),

    /**
     * 商家确认收款
     */

    MERCHANT_DETERMINES_PAYMENT("MerchantDeterminesPayment"),
    /**
     * OTC取消
     */
    CANCEL_FB_TAKER_ORDFER("CancelFbTakerOrder"),
    /**
     * 商家收款超时
     */
    PAYMENT_TIMEOUT("PaymentTimeout"),
    /**
     * 用户出售通知
     */
    POST_PURCHASE("PostPurchase"),
    /**
     * 手续费分成结算成功
     */
    COMMISSION_SETTLE("CommissionSettle"),
    /**
     * 出售订单通知购买方付款
     */
    SELLER_NOTICE_PAY("SellerNoticePay"),
    /**
     * 用户发起申诉,通知系统管理员处理
     */
    DO_APPEAL_REMIND("DoAppealRemind"),
    /**
     * 群发邮件
     */
    SEND_TO_ALL("SendToAll"),

    //UD社区锁定用户
    UD_COMMUNITY_LOCK_USER("CommunityLockUser"),

    //UD社区通知用户充钱
    UD_COMMUNITY_CHARGE_NOTICE("CommunityChargeNotice"),

    /**
     * 提币审核异常通知 ，通知提币人员
     */
    WITHDRAW_AUDIT_ERROR("withdrawAuditError");

    private final String value;

    private EmailTemplateType(String value) {
        this.value = value;
    }

    /**
     * Return the integer value of this status code.
     */
    public String value() {
        return this.value;
    }

    public String toString() {
        return this.value.toString();
    }

}
