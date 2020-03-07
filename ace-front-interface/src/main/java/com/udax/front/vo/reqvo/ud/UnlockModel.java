package com.udax.front.vo.reqvo.ud;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


@Getter
@Setter
public class UnlockModel implements Serializable {



    @NotBlank(message="{TRADE_PWD_ERROR}")
    @Length(min=6, max=6, message="{TRADE_PWD_ERROR}")
    private String password;//支付密码




}
