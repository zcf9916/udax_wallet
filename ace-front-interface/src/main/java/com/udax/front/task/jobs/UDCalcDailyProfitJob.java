package com.udax.front.task.jobs;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.front.FrontUserInfo;
import com.github.wxiaoqi.security.common.entity.ud.HOrderDetail;
import com.github.wxiaoqi.security.common.entity.ud.HParam;
import com.github.wxiaoqi.security.common.entity.ud.HUserInfo;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.enums.ud.UDOrderDetailStatus;
import com.github.wxiaoqi.security.common.mapper.front.FrontUserInfoMapper;
import com.github.wxiaoqi.security.common.mapper.front.FrontUserMapper;
import com.github.wxiaoqi.security.common.mapper.ud.HOrderDetailMapper;
import com.github.wxiaoqi.security.common.mapper.ud.HSettledProfitMapper;
import com.github.wxiaoqi.security.common.util.DateUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static com.github.wxiaoqi.security.common.constant.Constants.BATCHUPDATE_LIMIT;

/**
 * 定时修改基金产品的状态 ,到达相应时间 更改对应状态值
 *
 * @author Tang
 *
 */

@Component
@ScheduledJob(name = "UDCalcDailyProfitJob", cronExp = "0 0 0 * * ?") //每天执行一次
@DisallowConcurrentExecution
public class UDCalcDailyProfitJob implements Job, ApplicationContextAware {

	protected final Logger logger = LogManager.getLogger(this.getClass());

	static ApplicationContext applicationContext;


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		UDCalcDailyProfitJob.applicationContext=applicationContext;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("--------执行定时UD有效订单的每日利润记录开始,当前时间:"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));

		HOrderDetailBiz hOrderDetailBiz = UDCalcDailyProfitJob.applicationContext.getBean(HOrderDetailBiz.class);
		//查询是否有未到期的订单
		Example example = new Example(HOrderDetail.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("status", UDOrderDetailStatus.INIT.value());//未到期订单
		example.setOrderByClause(" id");//正序查询
		int total =   hOrderDetailBiz.selectCountByExample(example);//计算总量
		int totalPageCount  =  new BigDecimal(total).divide( new BigDecimal(BATCHUPDATE_LIMIT),BigDecimal.ROUND_CEILING).intValue();//需要分页的数量
		int beginCount = 1;
		Long lastId = 0L;//上一批次的最后一个Id,下次分页查询要从上一次的最后一个开始查询
		while( beginCount <= totalPageCount){
			if(lastId >  0){
				criteria.andGreaterThan("id",lastId);
			}
			Page<Object> result = PageHelper.startPage(beginCount, BATCHUPDATE_LIMIT);
			List<HOrderDetail> orderDetailList = hOrderDetailBiz.selectByExample(example);
			//这个批次的最大Id
			if(StringUtil.listIsNotBlank(orderDetailList)){
				orderDetailList.stream().forEach((l) ->{
					HUserInfo userInfo = hOrderDetailBiz.settleOrderDetail(l);
					if(userInfo != null){
						hOrderDetailBiz.autoRepeat(userInfo,l);
					}

				});
				lastId = orderDetailList.get(orderDetailList.size() - 1).getId();
			}
			beginCount ++;
		}

		logger.info("--------执行定时UD有效订单的每日利润记录结束,当前时间:"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));


		logger.info("--------执行定时锁定用户记录开始,当前时间:"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));

		HUserInfoBiz hUserInfoBiz = UDCalcDailyProfitJob.applicationContext.getBean(HUserInfoBiz.class);
		HParamBiz paramBiz = UDCalcDailyProfitJob.applicationContext.getBean(HParamBiz.class);
		HParam hparam = ServiceUtil.getUdParamByKey("EXPIRE",paramBiz);
		Long expire = Long.valueOf(hparam.getUdValue());//解锁有效期
		//查询是否有未到期的订单
		Example userExample = new Example(HUserInfo.class);
		Example.Criteria userCriteria = example.createCriteria();
		userCriteria.andEqualTo("status", EnableType.ENABLE.value());//查询用户状态为激活的
		List<HUserInfo> userList = hUserInfoBiz.selectByExample(userExample);
		//这个批次的最大Id
		if(StringUtil.listIsNotBlank(userList)){
			userList.stream().forEach((l) ->{
				hUserInfoBiz.lockUser(l,expire);

			});
		}

		logger.info("--------执行定时锁定用户记录结束,当前时间:"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));



	}

}
