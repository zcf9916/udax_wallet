package com.udax.front.task.jobTest;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.common.entity.front.RedPacketSend;
import com.github.wxiaoqi.security.common.entity.ud.HOrderDetail;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.enums.RedPacketOrderStatus;
import com.github.wxiaoqi.security.common.enums.ud.UDOrderDetailStatus;
import com.github.wxiaoqi.security.common.mapper.ud.HOrderDetailMapper;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.udax.front.biz.RedPacketsSendBiz;
import com.udax.front.biz.ud.HOrderDetailBiz;
import com.udax.front.task.jobs.RedPacketJob;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.github.wxiaoqi.security.common.constant.Constants.BATCHUPDATE_LIMIT;


@RunWith(SpringRunner.class)
@SpringBootTest
public class JobTest {

    @Autowired
    private RedPacketsSendBiz redPacketsSendBiz;


    @Test
    public void execute() throws JobExecutionException {


        //更新到期的红包为退款中
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String format =   LocalDateTime.now().minusDays(1).format(formatter);
        RedPacketSend param = new RedPacketSend();
        param.setStatus(RedPacketOrderStatus.RETURNING.value());//状态更改为退款中
        Example example = new Example(RedPacketSend.class);
        example.createCriteria()
                .andEqualTo("status",RedPacketOrderStatus.INIT.value())
                .andLessThanOrEqualTo("createTime", format);
        redPacketsSendBiz.updateByExampleSelective(param,example);

        //查询退款中的订单
        RedPacketSend queryParam = new RedPacketSend();
        queryParam.setStatus(RedPacketOrderStatus.RETURNING.value());//退款中
        List<RedPacketSend> list = redPacketsSendBiz.selectList(queryParam);
        if(StringUtil.listIsNotBlank(list)){
            list.stream().forEach((l) ->{
                //测试异常的抛出
                redPacketsSendBiz.refund(l);
            });
        }

    }

    public static void main(String[] args) {
        int count =0 , num =0;
        for(int i = 0 ;i <= 100 ;i++){
            num = num + i;
            count = count++;
        }
        System.out.println(count * num);

    }

}

