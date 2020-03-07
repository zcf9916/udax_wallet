package com.udax.front.task.jobs;

import com.github.wxiaoqi.security.common.entity.front.CommissionLog;
import com.github.wxiaoqi.security.common.entity.front.RedPacketSend;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.enums.RedPacketOrderStatus;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.udax.front.biz.CommissionLogBiz;
import com.udax.front.biz.RedPacketsSendBiz;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 *
 *  每日分成一次
 * @author Tang
 *
 */

@Component
@ScheduledJob(name = "CmsDailyJob", cronExp = "0 0 0 * * ?") //每天执行一次
@DisallowConcurrentExecution
public class CmsDailyJob implements Job, ApplicationContextAware {

	protected final Logger logger = LogManager.getLogger(this.getClass());

	static ApplicationContext applicationContext;


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		CmsDailyJob.applicationContext=applicationContext;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("--------结算分成的任务开始,当前时间:"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
		CommissionLogBiz cmsLogBiz = CmsDailyJob.applicationContext.getBean(CommissionLogBiz.class);
		cmsLogBiz.settleDailyCms();
		logger.info("--------结算分成的任务结束-,当前时间:"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
	}

}
