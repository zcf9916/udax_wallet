package com.udax.front.task.jobs;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.common.entity.lock.UserSymbolLockDetail;
import com.github.wxiaoqi.security.common.entity.ud.HOrderDetail;
import com.github.wxiaoqi.security.common.entity.ud.HParam;
import com.github.wxiaoqi.security.common.entity.ud.HUserInfo;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.enums.ud.UDOrderDetailStatus;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.udax.front.biz.UserSymbolLockDetailBiz;
import com.udax.front.biz.ud.HOrderDetailBiz;
import com.udax.front.biz.ud.HParamBiz;
import com.udax.front.biz.ud.HUserInfoBiz;
import com.udax.front.service.ServiceUtil;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.github.wxiaoqi.security.common.constant.Constants.BATCHUPDATE_LIMIT;

/**
 * 定时修改基金产品的状态 ,到达相应时间 更改对应状态值
 *
 * @author Tang
 *
 */

@Component
@ScheduledJob(name = "ReleaseSymbolDailyProfitJob", cronExp = "0 0 0 * * ?") //每天执行一次
@DisallowConcurrentExecution
public class ReleaseSymbolDailyProfitJob implements Job, ApplicationContextAware {

	protected final Logger logger = LogManager.getLogger(this.getClass());

	static ApplicationContext applicationContext;

	private static Integer batchSize = 10;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ReleaseSymbolDailyProfitJob.applicationContext=applicationContext;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("--------执行定时释放锁仓的代币开始,当前时间:"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));

		UserSymbolLockDetailBiz detailBiz = ReleaseSymbolDailyProfitJob.applicationContext.getBean(UserSymbolLockDetailBiz.class);
		//查询是否有未到期的订单
		Example example = new Example(UserSymbolLockDetail.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("isFree", EnableType.DISABLE.value());//未释放的订单
		criteria.andEqualTo("createTime",LocalDate.now().toString() + "00:00:00");
		example.setOrderByClause(" id");//正序查询
		int total =   detailBiz.selectCountByExample(example);//计算总量
		int totalPageCount  =  new BigDecimal(total).divide( new BigDecimal(batchSize),BigDecimal.ROUND_CEILING).intValue();//需要分页的数量
		int beginCount = 1;
		Long lastId = 0L;//上一批次的最后一个Id,下次分页查询要从上一次的最后一个开始查询
		while( beginCount <= totalPageCount){
			if(lastId >  0){
				criteria.andGreaterThan("id",lastId);
			}
			PageHelper.startPage(beginCount, batchSize);
			List<UserSymbolLockDetail> detailList = detailBiz.selectByExample(example);
			//这个批次的最大Id
			if(StringUtil.listIsNotBlank(detailList)){
				detailList.stream().forEach((l) ->{
					detailBiz.releaseSymbol(l);
				});
				lastId = detailList.get(detailList.size() - 1).getId();
			}
			beginCount ++;
		}

		logger.info("--------执行定时释放锁仓的代币开始,当前时间:"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));



	}

}
