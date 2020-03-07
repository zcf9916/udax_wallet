package com.github.wxiaoqi.security.admin.vo.reqvo;

import com.github.wxiaoqi.security.admin.vo.recordModel;
import lombok.Data;

import java.util.List;

@Data
public class recordVo {
    private Long count;
    private List<recordModel> transactionList;
}
