package com.udax.front.task.jobs;

import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.front.FrontUserInfo;
import com.github.wxiaoqi.security.common.entity.fund.FundProductInfo;
import com.github.wxiaoqi.security.common.entity.fund.FundPurchaseInfo;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.enums.fund.FundPurchaseStatus;
import com.github.wxiaoqi.security.common.enums.fund.FundStatus;
import com.github.wxiaoqi.security.common.mapper.front.FrontUserInfoMapper;
import com.github.wxiaoqi.security.common.mapper.front.FrontUserMapper;
import com.github.wxiaoqi.security.common.mapper.fund.FundProductInfoMapper;
import com.github.wxiaoqi.security.common.mapper.fund.FundPurchaseInfoMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.DateUtil;
import com.github.wxiaoqi.security.common.util.LocalDateUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.udax.front.biz.FrontUserBiz;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时修改基金产品的状态 ,到达相应时间 更改对应状态值
 *
 * @author Tang
 *
 */

@Component
@ScheduledJob(name = "UnlockUserJobpayOrder", cronExp = "0 0/10 * * * ?") //每1分钟扫一次
@DisallowConcurrentExecution
public class UnlockUserJob implements Job, ApplicationContextAware {

	protected final Logger logger = LogManager.getLogger(this.getClass());

	static ApplicationContext applicationContext;


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		UnlockUserJob.applicationContext=applicationContext;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("--------执行定时解锁用户锁定状态------------");
		try {
			FrontUserMapper frontUserMapper = UnlockUserJob.applicationContext.getBean(FrontUserMapper.class);
		FrontUserInfoMapper frontUserInfoMapper = UnlockUserJob.applicationContext.getBean(FrontUserInfoMapper.class);
		//查询限制提现的用户
		FrontUserInfo frontUserInfo = new FrontUserInfo();
		frontUserInfo.setIsWithdraw(EnableType.DISABLE.value());
        List<FrontUserInfo> infoList = frontUserInfoMapper.select(frontUserInfo);
        int hour = 1;
        int pwdLockTime = 1;
        if (StringUtil.listIsNotBlank(infoList)) {
            for (FrontUserInfo info : infoList) {
				FrontUser user = frontUserMapper.selectByPrimaryKey(info.getUserId());
                if (user != null && DateUtil.getHourBetween(user.getUpdateTime(), new Date()) > hour) {
					info.setIsWithdraw(EnableType.ENABLE.value());
					FrontUserInfo updateParam = new FrontUserInfo();
					updateParam.setIsWithdraw(EnableType.ENABLE.value());
					updateParam.setId(info.getId());
					frontUserInfoMapper.updateByPrimaryKeySelective(updateParam);
                }
            }
        }

		FrontUser queryParam = new FrontUser();
		queryParam.setLoginErrTimes(5);
        List<FrontUser> lockUsesrList = frontUserMapper.select(queryParam);
        for (FrontUser frontUser : lockUsesrList) {
            if (DateUtil.getHourBetween(frontUser.getUpdateTime(), new Date()) > pwdLockTime) {
				FrontUser updateParam = new FrontUser();
				updateParam.setId(frontUser.getId());
				updateParam.setLoginErrTimes(0);
				frontUserMapper.updateByPrimaryKeySelective(frontUser);
            }
        }

		}catch (Exception e){
			logger.error("执行定时解锁用户锁定状态异常:"+e.getMessage(),e);
		}
	}

}
