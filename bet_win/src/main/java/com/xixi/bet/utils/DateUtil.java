package com.xixi.bet.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	static Calendar calendar = Calendar.getInstance();
	
	public static Date getStockRealDate(){
		Date d = new Date();
		calendar.setTime(d);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		if(calendar.get(Calendar.HOUR_OF_DAY) < 9){
			calendar.add(Calendar.DATE, -1);
		}
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		return calendar.getTime();
	}
	
	public static Date getStockRealDateByDate(Date dt){
		calendar.setTime(dt);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		if(calendar.get(Calendar.HOUR_OF_DAY) < 9){
			calendar.add(Calendar.DATE, -1);
		}
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		return calendar.getTime();
	}
	
	public static Date dateCalculate(Date date, Integer offeset, Integer timeUnits){
		calendar.setTime(date);
		calendar.add(timeUnits, offeset);
		return calendar.getTime();
	}
	
	public static boolean date1IsBeforeDate2(Date date1, Date date2){
		return Integer.parseInt(Constants.FILE_DATE_FORMAT.get().format(date1)) < Integer.parseInt(Constants.FILE_DATE_FORMAT.get().format(date2));
	}
	
	public static boolean isWeekEnd(Date date){
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_WEEK) - 1 == 0 || calendar.get(Calendar.DAY_OF_WEEK) - 1 == 6;
	}
}
