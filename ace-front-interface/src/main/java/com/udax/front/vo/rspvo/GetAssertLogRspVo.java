package com.udax.front.vo.rspvo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.BigDecimalCoinSerializer;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import com.github.wxiaoqi.security.common.enums.BillType;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class GetAssertLogRspVo implements Serializable {



	private BigDecimal incomeAmount;//收入

	private BigDecimal payAmount;//支出

    private List<BillVo> typeList;//账单类型

	private List<DataVo> dataVoList;

	private long total;

	public DataVo newDataVo(){
		return new DataVo();
	}

	public BillVo newBillVo(){
		return new BillVo();
	}

	@Getter
	@Setter
	public class BillVo{
		private String name;

		private Integer billType;
	}


	@Getter
	@Setter
	public class DataVo{
		/**
		 * 实际收入/支出数量
		 */
		private String amount;//-0.6BTC

        private Long id;


		/**
		 * 流水生成时间
		 */
		@JsonSerialize(using = DateToTimeStampSerializer.class)
		private Date createTime;


		private String remark;

		private Integer billType;
	}

}
