package com.udax.front.task.jobs;

import com.github.wxiaoqi.security.common.entity.ud.HSettledProfit;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.enums.ud.SettledProfitStatus;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.udax.front.biz.ud.HOrderDetailBiz;
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
@ScheduledJob(name = "UDSettleProfitJob", cronExp = "0 */1 * * * ?") //每分钟执行一次
@DisallowConcurrentExecution
public class UDSettleProfitJob implements Job, ApplicationContextAware {

	protected final Logger logger = LogManager.getLogger(this.getClass());

	static ApplicationContext applicationContext;


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		UDSettleProfitJob.applicationContext=applicationContext;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("--------执行结算待解冻的利润,当前时间:"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
		HSettledProfitBiz hOrderDetailBiz = UDSettleProfitJob.applicationContext.getBean(HSettledProfitBiz.class);

		HSettledProfit queryParam = new HSettledProfit();
		queryParam.setStatus(SettledProfitStatus.WAIT_SETTLE.value());
		queryParam.setIfQueueNextOrder(EnableType.ENABLE.value());
		List<HSettledProfit> list = hOrderDetailBiz.selectList(queryParam);
		if(StringUtil.listIsNotBlank(list)){
			list.stream().forEach((l) ->{
				//测试异常的抛出
				hOrderDetailBiz.settleProfit(l);
			});
		}
		logger.info("--------执行结算待解冻的利润结束-,当前时间:"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
	}

}
