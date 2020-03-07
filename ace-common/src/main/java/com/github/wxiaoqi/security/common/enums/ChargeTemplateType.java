package com.github.wxiaoqi.security.common.enums;

import java.math.BigDecimal;

/**
 * 手续费类型
 */
public enum ChargeTemplateType {

    /**
     * 固定值
     */
    FIXATION_VALUE(1),
    /**
     * 比例 -- 百分比
     **/
    RATIO(2),

    /**
     *  点差
     */
    SPREAD(3);

    private final Integer value;

    private ChargeTemplateType(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }

    public String toString() {
        return this.value.toString();
    }


    //获取手续费数量
    public static BigDecimal getChargeValue(Integer chargeType,BigDecimal chargeValue,BigDecimal tradeAmount){
        //如果是固定值和点差,手续费等于模板手续费
        if(chargeType.equals(FIXATION_VALUE.value) || chargeType.equals(SPREAD.value)){
            return chargeValue;
        }
        if(chargeType.equals(RATIO.value)){
            return tradeAmount.multiply(chargeValue);
        }
        return null;
    }

}
