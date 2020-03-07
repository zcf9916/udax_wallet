package com.udax.front.event;

import com.github.wxiaoqi.security.common.entity.front.FrontTransferDetail;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;


@Getter
@Setter
public class TransferCoinEvent extends ApplicationEvent {


    private FrontTransferDetail detail;

    private Long exchId;


    public TransferCoinEvent(Object source, FrontTransferDetail detail,Long exchId) {
        super(source);
        this.exchId = exchId;
        this.detail = detail;

    }


}
