package com.udax.front.event.listerner;


import com.udax.front.biz.CommissionLogBiz;
import com.udax.front.event.TransferCoinEvent;
import com.udax.front.event.TransferOrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 提成相关事件
 */
@Component
@Slf4j
public class BonusEventListerner {

    @Autowired
    private CommissionLogBiz logBiz;

    //转币事件
    @EventListener(condition = "#event.detail != null")
    @Async
    public void handleEvent(TransferCoinEvent event) throws Exception {
        logBiz.insertCmsLog(event);
    }

    //转账事件
    @EventListener(condition = "#event.order != null")
    @Async
    public void handleEvent(TransferOrderEvent event) throws Exception {
        logBiz.insertCmsLog(event);
    }


}
