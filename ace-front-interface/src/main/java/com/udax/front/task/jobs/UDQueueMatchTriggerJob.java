package com.udax.front.task.jobs;

import com.udax.front.biz.ud.HQueueBiz;
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

/**
 *
 *
 * @author Tang
 *
 */

@Component
@ScheduledJob(name = "UDQueueTriggerJob", cronExp = "0 */1 * * * ?") //每分钟执行一次
@DisallowConcurrentExecution
public class UDQueueMatchTriggerJob implements Job, ApplicationContextAware {

	protected final Logger logger = LogManager.getLogger(this.getClass());

	static ApplicationContext applicationContext;


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		UDQueueMatchTriggerJob.applicationContext=applicationContext;
	}

	@Override
	public void execute(JobExecutionContext context){
		logger.info("--------执行UD排队队列状态检查,有效的用户进入有效队列订单 开始,当前时间:"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
		HQueueBiz hQueueBiz = UDQueueMatchTriggerJob.applicationContext.getBean(HQueueBiz.class);
		hQueueBiz.numMatchQueue();
		hQueueBiz.timeMatchQueue();
		logger.info("--------执行UD排队队列状态检查,有效的用户进入有效队列订单  结束,当前时间:"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
	}

}
