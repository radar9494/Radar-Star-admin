package com.liuqi.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {
	private static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static Timestamp currentTimestamp() {
		return new Timestamp(System.currentTimeMillis());
	}

	public static String currentDateTime() {
		return new SimpleDateFormat(DATETIME_FORMAT).format(new Date());
	}

	public static String currentDate(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}

	public static long compare(String date1,String date2){
		Date firstDate = null;
		Date secondDate=null;
		try {
			firstDate = new SimpleDateFormat(DATETIME_FORMAT).parse(date1);
			secondDate = new SimpleDateFormat(DATETIME_FORMAT).parse(date2);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		//得到两个日期对象的总毫秒数
		long firstDateMilliSeconds = firstDate.getTime();
		long secondDateMilliSeconds = secondDate.getTime();

		//得到两者之差
		long firstMinusSecond = firstDateMilliSeconds - secondDateMilliSeconds;
		return firstMinusSecond;
	}


	public static Date getStartDate(Date date){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		return calendar.getTime();
	}

	public static Date getEndDate(Date date){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY,23);
		calendar.set(Calendar.MINUTE,59);
		calendar.set(Calendar.SECOND,59);
		return calendar.getTime();
	}

	/***
	 * convert Date to cron ,eg.  "0 07 10 15 1 ? 2016"
	 * @param date  : 时间点
	 * @return
	 */
	public static String getCron(Date  date){
		String dateFormat="ss mm HH dd MM ? yyyy";
		return formatDateByPattern(date, dateFormat);
	}

	/***
	 *  功能描述：日期转换cron表达式
	 * @param date
	 * @param dateFormat : e.g:yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String formatDateByPattern(Date date,String dateFormat){
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String formatTimeStr = null;
		if (date != null) {
			formatTimeStr = sdf.format(date);
		}
		return formatTimeStr;
	}
}
