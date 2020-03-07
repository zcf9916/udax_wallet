package com.github.wxiaoqi.security.common.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 计价方式vo
 */
@Data
public class ValuationModeVo implements Serializable {
    private Long id;
    private String dictLabel;
    private String dictValue;
    private Long exchId;
    private String languageType;
    private String defaultSymbol;
}
