package com.udax.front.bean;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;


/**
 * 用于返回xml格式数据的实体
 */
@Getter
@Setter
public class XMLModel implements Serializable {

    private String billno; //ifr充值现金,回调返回参数 系统的参考编号

    private String partner_orderid; //ifr充值现金,回调返回参数 订单号

    private String status;//ifr充值现金,回调返回参数  返回状态(成功OK)
}
