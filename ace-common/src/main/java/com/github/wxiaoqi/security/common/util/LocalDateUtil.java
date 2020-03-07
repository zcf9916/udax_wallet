package com.github.wxiaoqi.security.common.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;

/**
 * 日期操作辅助类
 * 
 * @author ShenHuaJie
 * @version $Id: DateUtil.java, v 0.1 2014年3月28日 上午8:58:11 ShenHuaJie Exp $
 */
public final class LocalDateUtil {

	/**
	 * localDate转Date
	 */
	public static Date localDateTime2Date(LocalDateTime localDateTime){
		ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
		Instant instant1 = zonedDateTime.toInstant();
		Date from = Date.from(instant1);
		return  from;
	}



	/**
	 * localDate转Date
	 */
	public static Date localDate2Date(LocalDate localDate){
		ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
		Instant instant1 = zonedDateTime.toInstant();
		Date from = Date.from(instant1);
		return  from;	}


	/**
	 * Date 转 localDate
	 */
	public static LocalDate date2LocalDate(Date date){
		Instant instant = date.toInstant();
		ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
		LocalDate localDate = zdt.toLocalDate();
		return localDate;
	}

	/**
	 * Date 转 localDate
	 */
	public static LocalDateTime date2LocalDateTime(Date date){
		Instant instant = date.toInstant();
		ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
		LocalDateTime localDate = zdt.toLocalDateTime();
		return localDate;
	}

	/**
	 * 获取上个月最后一天的时间
	 * @param monthFormat
	 * @return
	 */
	public static  LocalDate  getLastMonthLastDay (String monthFormat){
        LocalDate date = LocalDate.parse(monthFormat);
        date = date.minusMonths(1);
		Date lastDay  = getEndDayOfMonth(date.toString());

		return date2LocalDate(lastDay);
	}


	/**
	 * 获取上个月最后一天的时间
	 * @param monthFormat
	 * @return
	 */
	public static  LocalDate  getNextMonthFirstDay (String monthFormat){
		Date fistDay  = getStartDayOfMonth(monthFormat);
		LocalDate date = date2LocalDate(fistDay);
		date = date.plusMonths(1);
		return date;
	}

	
	//获取月第一天
	public static Date getStartDayOfMonth(String date) {
		LocalDate now = LocalDate.parse(date);
		return getStartDayOfMonth(now);
	}

	public static Date getStartDayOfMonth(LocalDate date) {
		LocalDate now = date.with(TemporalAdjusters.firstDayOfMonth());
		return localDate2Date(now);
	}
	//获取月最后一天
	public static Date getEndDayOfMonth(String date) {
		LocalDate localDate = LocalDate.parse(date);
		return getEndDayOfMonth(localDate);
	}

	public static Date getEndDayOfMonth(LocalDate date) {
		LocalDate now = date.with(TemporalAdjusters.lastDayOfMonth());

		Date.from(now.atStartOfDay(ZoneId.systemDefault()).plusDays(1L).minusNanos(1L).toInstant());
		return localDate2Date(now);
	}

	//获取周第一天
	public static Date getStartDayOfWeek(String date) {
		LocalDate now = LocalDate.parse(date);

		return getStartDayOfWeek(now);
	}

    /**
     * 这个季度第一天
     * @return
     */
    public static LocalDate getThisQuartzFirstDay(LocalDate date) {
        int oriMonth = date.getMonthValue();
        int months[] = { 1, 3, 6, 9 };
        int month = 1;
        for (int m : months) {
               if(oriMonth >= m){
                     month = m;
               }
        }

        date = date.minusMonths(oriMonth-month);
        return date2LocalDate(getStartDayOfMonth(date.toString()));
    }




	/**
	 * 上周的最后一天
	 * @return
	 */
	public static LocalDate getLastWeekLastDay(LocalDate date) {
		TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
		LocalDate localDate = LocalDate.from(date);
		localDate=localDate.with(fieldISO, 1);
		return localDate;
	}

	/**
	 * 获取下一周的第一天
	 * @return
	 */
	public static LocalDate getNextWeekFirstDay(LocalDate date) {
		TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
		LocalDate localDate = LocalDate.from(date);
		localDate=localDate.with(fieldISO, 7).plusDays(2L);
		return localDate;
	}




	public static Date getStartDayOfWeek(TemporalAccessor date) {
		TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
		LocalDate localDate = LocalDate.from(date);
		localDate=localDate.with(fieldISO, 1);
		return localDate2Date(localDate);
	}
	//获取周最后一天
	public static Date getEndDayOfWeek(String date) {
		LocalDate localDate = LocalDate.parse(date);
		return getEndDayOfWeek(localDate);
	}

	public static Date getEndDayOfWeek(TemporalAccessor date) {
		TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
		LocalDate localDate = LocalDate.from(date);
		localDate=localDate.with(fieldISO, 7);
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).plusDays(1L).minusNanos(1L).toInstant());
	}
	//一天的开始
	public Date getStartOfDay(String date) {
		LocalDate localDate = LocalDate.parse(date);
		return getStartOfDay(localDate);
	}

	public Date getStartOfDay(TemporalAccessor date) {
		LocalDate localDate = LocalDate.from(date);
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	//一天的结束
	public Date getEndOfDay(String date){
		LocalDate localDate = LocalDate.parse(date);
		return getEndOfDay(localDate);
	}
	public Date getEndOfDay(TemporalAccessor date) {
		LocalDate localDate = LocalDate.from(date);
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).plusDays(1L).minusNanos(1L).toInstant());
	}

	//时间字符串转时间戳,精确到秒
	public static Long convertTimeToLong(String time,String  pattern) {
		DateTimeFormatter ftf = DateTimeFormatter.ofPattern(pattern);
		LocalDateTime parse = LocalDateTime.parse(time, ftf);
		return LocalDateTime.from(parse).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
	}

    //时间戳转时间字符串,精确到秒
    public static String formatLongTime(Long timestamp) {
        LocalDateTime localDateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime.toString();
    }

}
