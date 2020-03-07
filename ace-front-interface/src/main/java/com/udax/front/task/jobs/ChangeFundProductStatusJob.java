package com.udax.front.task.jobs;

import com.github.wxiaoqi.security.common.entity.fund.FundProductInfo;
import com.github.wxiaoqi.security.common.entity.fund.FundPurchaseInfo;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.enums.fund.FundPurchaseStatus;
import com.github.wxiaoqi.security.common.enums.fund.FundStatus;
import com.github.wxiaoqi.security.common.mapper.fund.FundProductInfoMapper;
import com.github.wxiaoqi.security.common.mapper.fund.FundPurchaseInfoMapper;
import com.github.wxiaoqi.security.common.util.LocalDateUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.udax.front.task.jobConfigure.ScheduledJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 定时修改基金产品的状态 ,到达相应时间 更改对应状态值
 *
 * @author Tang
 *
 */

@Component
@ScheduledJob(name = "ChangeFundProductStatusJob", cronExp = "0 0/5 * * * ?") //每5分钟扫一次
@DisallowConcurrentExecution
public class ChangeFundProductStatusJob implements Job, ApplicationContextAware {

	protected final Logger logger = LogManager.getLogger(this.getClass());

	private static ApplicationContext applicationContext;


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ChangeFundProductStatusJob.applicationContext=applicationContext;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void execute(JobExecutionContext context){
		logger.info("--------执行定时修改基金产品的状态 ,到达相应时间 更改对应状态值,当前时间:"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
		try {
			FundProductInfoMapper fundProductMapper = ChangeFundProductStatusJob.applicationContext.getBean(FundProductInfoMapper.class);
			Example example = new Example(FundProductInfo.class);
			Example.Criteria criteria = example.createCriteria();
			criteria.andEqualTo("enable", EnableType.ENABLE.value());
			criteria.andLessThan("status",FundStatus.SETTLEING.value());
			List<FundProductInfo> fundProductList = fundProductMapper.selectByExample(example);
			if(StringUtil.listIsNotBlank(fundProductList)) {
				for (FundProductInfo fundProduct : fundProductList) {
					LocalDateTime buyStarttime = LocalDateUtil.date2LocalDateTime(fundProduct.getBuyStarttime());//募集开始时间
					LocalDateTime buyEndtime = LocalDateUtil.date2LocalDateTime(fundProduct.getBuyEndtime());//募集结束时间
					LocalDateTime cycleStarttime = LocalDateUtil.date2LocalDateTime(fundProduct.getCycleStarttime());//锁定开始时间
					LocalDateTime cycleEndtime = LocalDateUtil.date2LocalDateTime(fundProduct.getCycleEndtime());//锁定结束时间
					//LocalDateTime runStarttime = fundProduct.getRunStarttime()!=null?LocalDateUtil.date2LocalDateTime(fundProduct.getRunStarttime()):null;
					//LocalDateTime runEndtime = LocalDateUtil.date2LocalDateTime(fundProduct.getRunEndtime());//运行结束时间
					if (LocalDateTime.now().isAfter(buyStarttime)&&LocalDateTime.now().isBefore(buyEndtime)) {
						fundProduct.setStatus(FundStatus.SUBSCRIBE.value());//认购中(募集中)
					}
					if (LocalDateTime.now().isAfter(cycleStarttime)&&LocalDateTime.now().isBefore(cycleEndtime)) {

						//更新基金申购信息表的产品状态为运行中 2
						FundPurchaseInfoMapper purchaseInfoMapper = ChangeFundProductStatusJob.applicationContext.getBean(FundPurchaseInfoMapper.class);
						FundPurchaseInfo fundPurchaseInfo = new FundPurchaseInfo();
						fundPurchaseInfo.setStatus(FundPurchaseStatus.RUNNING.value());
						example = new Example(FundPurchaseInfo.class);
						criteria = example.createCriteria();
						criteria.andEqualTo("fundId", fundProduct.getFundId());
						purchaseInfoMapper.updateByExampleSelective(fundPurchaseInfo,example);

						fundProduct.setStatus(FundStatus.RUNNING.value());//已启动
					}
					if (LocalDateTime.now().isAfter(cycleEndtime)&&FundStatus.SETTLED.value()!=fundProduct.getStatus()) {

						//更新基金申购信息表的产品状态为结算中 3
						FundPurchaseInfoMapper purchaseInfoMapper = ChangeFundProductStatusJob.applicationContext.getBean(FundPurchaseInfoMapper.class);
						FundPurchaseInfo fundPurchaseInfo = new FundPurchaseInfo();
						fundPurchaseInfo.setStatus(FundPurchaseStatus.SETTLEING.value());
						example = new Example(FundPurchaseInfo.class);
						criteria = example.createCriteria();
						criteria.andEqualTo("fundId", fundProduct.getFundId());
						purchaseInfoMapper.updateByExampleSelective(fundPurchaseInfo,example);

						fundProduct.setStatus(FundStatus.SETTLEING.value());//清盘中
					}
					fundProductMapper.updateByPrimaryKeySelective(fundProduct);
				}
			}

		}catch (Exception e){
			logger.error("修改基金产品状态的定时任务任务异常:"+e.getMessage(),e);
			throw new RuntimeException();
		}
	}

}
