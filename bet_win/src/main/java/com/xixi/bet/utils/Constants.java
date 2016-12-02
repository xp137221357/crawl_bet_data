package com.xixi.bet.utils;

import java.text.SimpleDateFormat;
import java.util.concurrent.Semaphore;

public class Constants {

	
	//存储数据文件名时，使用日期做为文件名，且采用如下日期格式
	public static final ThreadLocal<SimpleDateFormat> FILE_DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {  
        public SimpleDateFormat initialValue() {  
            return new SimpleDateFormat("yyyyMMdd");  
        }  
    };  
    
    public static final Semaphore semaphore = new Semaphore(12);
	
}
