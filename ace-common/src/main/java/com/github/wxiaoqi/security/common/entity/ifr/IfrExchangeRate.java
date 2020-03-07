package com.github.wxiaoqi.security.common.entity.ifr;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "ifr_exchange_rate")
@Data
public class IfrExchangeRate extends BaseAdminEntity {

    /**
     * 法币币种
     */
    private String symbol;

    /**
     * 与美元的汇率
     *
     *
     *
     */
    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate;

}