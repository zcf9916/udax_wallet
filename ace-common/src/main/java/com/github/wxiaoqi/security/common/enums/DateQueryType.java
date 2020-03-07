package com.github.wxiaoqi.security.common.enums;


import com.github.wxiaoqi.security.common.util.LocalDateUtil;
import com.github.wxiaoqi.security.common.util.model.DateVo;

import java.time.LocalDate;

/**
 * 团队等级类型
 */
public enum DateQueryType {
	/** 用户团队等级 */
	TODAY(1),
	THISWEEK(2),
	THISMONTH(3),
	THISQUARTER(4),
	THISYEAR(5);

	private final Integer type;
	private DateQueryType(Integer type) {
		this.type = type;
	}

	public Integer value() {
		return this.type;
	}


	public static DateVo getDate(int value) {
		LocalDate today = LocalDate.now();
		DateVo vo = new DateVo();
		switch (value) {
		case 1://今日
			String todayBegin = today.toString();
			String todayEnd = today.plusDays(1).toString();
			vo.setBeginDate(todayBegin);
			vo.setEndDate(todayEnd);
			return vo;
		case 2:
			//周数据起始时间
			String weekBeginDate = LocalDateUtil.getLastWeekLastDay(today).toString();
			String weekEndDate = LocalDateUtil.getNextWeekFirstDay(today).toString();
			vo.setBeginDate(weekBeginDate);
			vo.setEndDate(weekEndDate);
			return vo;
		case 3://这个月
			String monthBeginDate = LocalDateUtil.date2LocalDate(LocalDateUtil.getStartDayOfMonth(today.toString())).toString();
			String monthEndDate = LocalDateUtil.getNextMonthFirstDay(today.toString()).toString();
			vo.setBeginDate(monthBeginDate);
			vo.setEndDate(monthEndDate);
			return vo;
		case 4://这个季度
			String quartzBeginDate = LocalDateUtil.getThisQuartzFirstDay(today).toString();
			String quartzEndDate = today.plusDays(1).toString();
			vo.setBeginDate(quartzBeginDate);
			vo.setEndDate(quartzEndDate);
			return vo;
		case 5://今年
			String yearBeginDate = today.getYear() + "-01-01";
			String yearEndDate =  (today.getYear()+1) + "-01-01";
			vo.setBeginDate(yearBeginDate);
			vo.setEndDate(yearEndDate);
			return vo;
		default:
			return null;
		}
	}


}
