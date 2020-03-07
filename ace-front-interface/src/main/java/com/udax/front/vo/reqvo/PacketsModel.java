package com.udax.front.vo.reqvo;

import com.udax.front.annotation.DcCode;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

@SuppressWarnings("serial")
@Data
public class PacketsModel implements Serializable{


	private String orderNo;//订单号

    private String groupID;

}
