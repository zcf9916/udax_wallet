package com.udax.front.vo.rspvo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wxiaoqi.security.common.annotation.DateToTimeStampSerializer;
import com.github.wxiaoqi.security.common.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;


public class OwnMemberVo {



    private Integer isValid;

    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    private String email;

    private String mobile;

    @JsonSerialize(using = DateToTimeStampSerializer.class)
    private Date createTime;


    public Integer getIsValid() {
        return isValid;
    }

    public void setIsValid(Integer isValid) {
        this.isValid = isValid;
    }

    public String getEmail() {
        if(StringUtils.isBlank(email)){
            return "";
        }
        String[] arr = email.split("@");
        return StringUtil.midleReplaceStar(arr[0]) + "@" + arr[1];
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return StringUtil.midleReplaceStar(mobile);
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}