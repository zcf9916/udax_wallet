/**
 *
 */
package com.udax.front.vo.reqvo;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *
 * @author ShenHuaJie
 * @version 2017年3月18日 下午1:40:45
 */
@SuppressWarnings("serial")
public class CkAccountModel implements Serializable {

    @NotBlank(message="{ACCOUNT_LENGTH}")
    @Length(min=8, max=30, message="{ACCOUNT_LENGTH}")
    private String account;
    /**
     * @return the account
     */
    public String getAccount() {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(String account) {
        this.account = account;
    }



}
