package com.udax.front.vo.reqvo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;


@Data
public  class LoginModel implements Serializable {

    @NotBlank(message="{ACCOUNT_LENGTH}")
    @Length(min=8, max=30, message="{ACCOUNT_LENGTH}")
    private String username;
    @NotBlank(message="{PASSWORD_IS_NULL}")
    @Pattern(regexp="^(?=.*[a-zA-Z])(?=.*\\d)[\\s\\S]{8,20}$",message = "{PASSWORD_LENGTH}")
    private String password;
//    @NotBlank(message="{VERIFY_IS_NULL}")
//    @Length(min=4, max=6, message="{VERIFY_IS_ILLEGAL}")


    @NotBlank(message = "{TICKT_IS_NULL}")
    private String ticket;//服务器返回的票据

    @NotBlank(message = "{TICKT_IS_NULL}")
    private String randstr;//服务器返回的随机字符串

}