package com.udax.front.vo.reqvo.merchant;

import com.github.wxiaoqi.security.common.constant.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.math.BigDecimal;


@Getter
@Setter
public class MchRefundQueryModel extends OrderBaseModel {


	@Length(min= 0,max=32, message="{ORDER_NOT_EXIST}")
	private String refundTransNo;//钱包退款订单号

	@Length(min= 0,max=32, message="{MERCHANT_ORDER_NO}")
	private String mchRefundOrderNo;//商户退款订单号

	@Length(min= 0,max=32, message="{MERCHANT_ORDER_NO}")
	private String mchOrderNo;//商户订单号

	@Length(min= 0,max=32, message="{ORDER_NOT_EXIST}")
	private String transNo;//钱包原订单号



    //当部分退款次数超过10次时可使用，表示返回的查询结果从这个偏移量开始取记录
	private int page = 1;//页数

	private int limit =  Constants.DEFAULT_NUMBER_PERPAGE;


	//每次返回的数量
	public int getLimit() {

		int tempLimit = limit;
		if( tempLimit > 50){
			tempLimit  = 50;
		}

		return  tempLimit < 1 ? Constants.DEFAULT_NUMBER_PERPAGE : tempLimit;
	}


	public int getPage() {
		int tempPage = page;
        if(tempPage > 100){
            tempPage  = 100;
        }
		return tempPage < 1 ? 1 : tempPage;
	}

}
