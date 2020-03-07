package com.udax.front.vo.reqvo;

import com.github.wxiaoqi.security.common.constant.Constants;
import com.udax.front.annotation.DcCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@SuppressWarnings("serial")
@Getter
@Setter
public class WithdrawAddListModel extends  PageInfo{

    @DcCode
    private String symbol;


}
