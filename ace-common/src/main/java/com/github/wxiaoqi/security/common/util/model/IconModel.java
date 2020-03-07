package com.github.wxiaoqi.security.common.util.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
public class IconModel implements Serializable {

    private String symbol;
    private String imageUrl;

    private Long exchangeId;//交易所id
}