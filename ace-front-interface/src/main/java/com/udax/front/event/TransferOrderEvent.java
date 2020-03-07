package com.udax.front.event;

import com.github.wxiaoqi.security.common.entity.front.TransferOrder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;


@Getter
@Setter
public class TransferOrderEvent extends ApplicationEvent {


    private Long exchId;//白标id
    private TransferOrder order;
    public TransferOrderEvent(Object source,TransferOrder order,Long exchId) {
        super(source);
        this.exchId = exchId;
        this.order = order;
    }


}
