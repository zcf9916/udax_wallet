package com.udax.front.vo.reqvo.merchant;

import com.udax.front.annotation.DateTimeFormat;
import com.udax.front.annotation.DcCode;
import com.udax.front.vo.reqvo.GetAssertLogModel;
import com.udax.front.vo.reqvo.PageInfo;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MchAssertLogModel extends GetAssertLogModel {

    private Integer direction;//收入支出方向

}
