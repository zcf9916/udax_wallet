package com.udax.front.vo.reqvo.fund;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@SuppressWarnings("serial")
@Getter
@Setter
public class FundDetailVo implements Serializable{

    @NotNull( message = "{ID_IS_NULL}")
    private String id;//id

}
