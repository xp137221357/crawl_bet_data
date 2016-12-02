/**  
 * Project Name:stk-common  
 * File Name:FileUtil.java  
 * Package Name:com.stk.utils.file  
 * Date:2015年8月18日下午6:10:32  
 * Copyright (c) 2015, Dell All Rights Reserved.  
 *  
*/  
  
package com.xixi.bet.utils;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**  
 * ClassName:FileUtil <br/>  
 * Function: 操作文件的工具类 <br/>  
 * Date:     2015年8月18日 下午6:10:32 <br/>  
 * @author   阳翅翔  
 * @version    
 * @since    JDK 1.7  
 * @see        
 */
public class FileUtil {
	
	protected static String CLASSNAME= "com.xixi.bet.utils.FileUtil";
	
	public static String readFile(String filePath){
		RandomAccessFile aFile = null;
		FileChannel inChannel = null;
		try{
			aFile = new RandomAccessFile(filePath, "r");
			inChannel = aFile.getChannel();
			ByteBuffer buf = ByteBuffer.allocate(aFile.length() > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)aFile.length());
			inChannel.read(buf);
			buf.flip();
			inChannel.close();
			aFile.close();
			return new String(buf.array());
		}catch(Throwable e){
			if(e instanceof FileNotFoundException){
				LoggerUtil.error(CLASSNAME,"文件"+filePath+"不存在，请检查.");
			}else{
				LoggerUtil.error(CLASSNAME,e);
			}
		}finally{
			if(aFile != null){
				try {
					aFile.close();
				} catch (IOException e) {
					LoggerUtil.error(CLASSNAME,e);
				}
			}
			
			if(inChannel != null){
				try {
					inChannel.close();
				} catch (IOException e) {
					LoggerUtil.error(CLASSNAME,e);
				}
			}
		}
		return "";
	}
	
	public static void serializeObjByJson(String filePath, Object object, boolean isOverrideFile){
		RandomAccessFile aFile = null;
		FileChannel inChannel = null;
		File f = new File(filePath);
		
		if(!isOverrideFile && f.exists()){
			return;
		}
		
		while(f.getName().contains(".")){
			f = f.getParentFile();
		}
		if(!f.exists()){
			f.mkdirs();
		}
		
		try{
			aFile = new RandomAccessFile(filePath, "rw");
			inChannel = aFile.getChannel();
			ByteBuffer buf = ByteBuffer.wrap(JSONObject.toJSONString(object).getBytes());
			inChannel.write(buf);
		}catch(Throwable e){
			LoggerUtil.error(CLASSNAME,e);
		}finally{
			if(aFile != null){
				try {
					aFile.close();
				} catch (IOException e) {
					LoggerUtil.error(CLASSNAME,e);
				}
			}
			
			if(inChannel != null){
				try {
					inChannel.close();
				} catch (IOException e) {
					LoggerUtil.error(CLASSNAME,e);
				}
			}
		}
	
	}
	
	public static void serializeObjByJson(String filePath, Object object){
		serializeObjByJson(filePath, object, true);
	}
	
	
	/**
	 * 传入一个目录路径，返回下一个需要处理的日期字符串
	 * 如:某文件夹下有20150902.json和20150903.json，此应该获取20150904的数据，则返回20150904
	 * @param path
	 * @return
	 */
	public static String getStartDate(String path, String dateFormat){
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		File directory = new File(path);
		if(!directory.exists()){
			directory.mkdirs();
		}
		File[] files = directory.listFiles();
		Date crawlDay = null;
		Date lastCrawlDay = null;
		for(File f : files){
			try{
				if(f.isDirectory()){
					continue;
				}
				crawlDay = Constants.FILE_DATE_FORMAT.get().parse(f.getName());
				lastCrawlDay = lastCrawlDay == null ? crawlDay : (lastCrawlDay.after(crawlDay) ? lastCrawlDay : crawlDay);
			}catch(Exception e){
				LoggerUtil.error(CLASSNAME,e);
			}
		}
		if(lastCrawlDay != null ){
			lastCrawlDay = DateUtil.dateCalculate(lastCrawlDay, 1, Calendar.DATE);
			return sdf.format(lastCrawlDay);
		}
		return null;
	}
}
  
