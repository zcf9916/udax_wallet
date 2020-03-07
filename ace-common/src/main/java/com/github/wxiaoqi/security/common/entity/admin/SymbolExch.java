package com.github.wxiaoqi.security.common.entity.admin;

import com.github.wxiaoqi.security.common.base.BaseAdminEntity;
import lombok.Data;

import java.util.Date;
import javax.persistence.*;
@Data
@Table(name = "symbol_exch")
public class SymbolExch extends BaseAdminEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "exch_id")
    private Long exchId;

    @Column(name = "symbol_id")
    private Long symbolId;

    private String symbol;

    private Integer hasLock;//开启锁定:0否 1:是

    private Integer freedNumber;//释放次数

    private Integer freedCycle;//释放周期(天)

}