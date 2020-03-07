package com.udax.front.vo.rspvo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FrontCountryVo  {


    /**
     * 国家编码(china)
     */
    private String code;

    /**
     * 国家名称
     */
    private String name;

    /**
     * 国家编号(0086)
     */
    private String countryCode;

    private String countryImage;

}