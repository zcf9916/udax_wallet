package com.udax.front.task.jobs;

import com.github.wxiaoqi.security.common.entity.front.RedPacketSend;
import com.github.wxiaoqi.security.common.entity.ud.HSettledProfit;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.enums.RedPacketOrderStatus;
import com.github.wxiaoqi.security.common.enums.ud.SettledProfitStatus;
import com.github.wxiaoqi.security.common.util.LocalDateUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.udax.front.biz.RedPacketsSendBiz;
import com.udax.front.biz.ud.HSettledProfitBiz;
import com.udax.front.task.jobConfigure.ScheduledJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 *
 * @author Tang
 *
 */

@Component
@ScheduledJob(name = "RedPacketJob", cronExp = "0 */1 * * * ?") //每分钟执行一次
@DisallowConcurrentExecution
public class RedPacketJob implements Job, ApplicationContextAware {

	protected final Logger logger = LogManager.getLogger(this.getClass());

	static ApplicationContext applicationContext;


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		RedPacketJob.applicationContext=applicationContext;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("--------执行红包退回的任务开始,当前时间:"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
		RedPacketsSendBiz redPacketsSendBiz = RedPacketJob.applicationContext.getBean(RedPacketsSendBiz.class);

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
		logger.info("--------执行退回的任务结束-,当前时间:"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
	}

	public static void main(String[] args) {



	}

}
