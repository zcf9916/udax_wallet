package com.github.wxiaoqi.security.common.entity.ud;

import com.github.wxiaoqi.security.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Table(name = "h_commission_relation")
@Getter
@Setter
public class HCommissionRelation extends BaseEntity {

    /**
     * 产生利润的用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 产生利润的代数
     */
    private Integer level;

    /**
     * 可能享有分成的用户id
     */
    @Column(name = "receive_user_id")
    private Long receiveUserId;

    @Column(name = "receive_level")
    private Integer receiveLevel;

}