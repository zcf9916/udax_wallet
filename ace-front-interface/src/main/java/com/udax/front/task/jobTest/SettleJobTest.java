package com.udax.front.task.jobTest;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.common.entity.ud.HOrderDetail;
import com.github.wxiaoqi.security.common.entity.ud.HSettledProfit;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.enums.ud.SettledProfitStatus;
import com.github.wxiaoqi.security.common.mapper.ud.HOrderDetailMapper;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.udax.front.biz.ud.HOrderDetailBiz;
import com.udax.front.biz.ud.HSettledProfitBiz;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;

import static com.github.wxiaoqi.security.common.constant.Constants.BATCHUPDATE_LIMIT;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SettleJobTest {

    @Autowired
    private HSettledProfitBiz hOrderDetailBiz;


    @Test
    public void getLearn(){

        HSettledProfit queryParam = new HSettledProfit();
        queryParam.setStatus(SettledProfitStatus.WAIT_SETTLE.value());
        queryParam.setIfQueueNextOrder(EnableType.ENABLE.value());
        List<HSettledProfit> list = hOrderDetailBiz.selectList(queryParam);
        if(StringUtil.listIsNotBlank(list)){
            list.stream().forEach((l) ->{
                hOrderDetailBiz.settleProfit(l);
            });
        }


       //orderDetailBiz.calcDailyProfit();
    }
}

