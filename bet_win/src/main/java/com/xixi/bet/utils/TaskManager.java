/**  
 * Project Name:stk-common  
 * File Name:TaskManager.java  
 * Package Name:com.stk.utils.system  
 * Date:2015年8月26日下午5:08:01  
 * Copyright (c) 2015, Dell All Rights Reserved.  
 *  
*/  
  
package com.xixi.bet.utils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.xixi.bet.bean.TaskWatermark;
import com.xixi.bet.dao.TaskWatermarkDao;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**  
 * ClassName:TaskManager <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason:   TODO ADD REASON. <br/>  
 * Date:     2015年8月26日 下午5:08:01 <br/>  
 * @author   阳翅翔  
 * @version    
 * @since    JDK 1.7  
 * @see        
 */
public class TaskManager {
	
	protected static String CLASSNAME = "com.xixi.bet.utils.TaskManager";
	
	public static Date getNextStartUp(Class taskClass){
		synchronized(TaskManager.class){
			TaskWatermarkDao taskWatermarkDao = SpringContextUtil.getBean("TaskWatermarkDao");
			HashMap<String,Object> params = new HashMap<String,Object>();
			params.put("TASK_CLASS", taskClass.getName());
			List<TaskWatermark> taskWatermarkList = taskWatermarkDao.query(params);
			if(taskWatermarkList== null || taskWatermarkList.isEmpty()){
				LoggerUtil.error(CLASSNAME,"任务不存在："+taskClass.getName());
				return null;
			}
			if(null == taskWatermarkList.get(0).getNextExecute()){
				return DateUtil.getStockRealDate();
			}

			if(taskWatermarkList.get(0).getNextExecute().before(DateUtil.getStockRealDate()) ||
					taskWatermarkList.get(0).getNextExecute().equals(DateUtil.getStockRealDate())){
				LoggerUtil.info(CLASSNAME,"任务"+taskClass.getName()+"-"+taskWatermarkList.get(0).getTaskName()+"开始执行; 理论下次开始时间"+Constants.FILE_DATE_FORMAT.get().format(taskWatermarkList.get(0).getNextExecute())+";当前时间"+Constants.FILE_DATE_FORMAT.get().format(DateUtil.getStockRealDate()));
				return taskWatermarkList.get(0).getNextExecute();
			}else{
				LoggerUtil.info(CLASSNAME,"任务"+taskClass.getName()+"-"+taskWatermarkList.get(0).getTaskName()+"已经完成,等待下一次再执行");
				return null;
			}
		}
	}

	public static boolean checkTaskStatusIsStartUp(Class taskClass){
		TaskWatermarkDao taskWatermarkDao = SpringContextUtil.getBean("TaskWatermarkDao");
		HashMap<String,Object> params = new HashMap<String,Object>();
		params.put("TASK_CLASS", taskClass.getName());
		List<TaskWatermark> taskWatermarkList = taskWatermarkDao.queryIsActive(params);
		if(taskWatermarkList== null || taskWatermarkList.isEmpty()){
			return false;
		}
		return true;
	}

	public static void refreshTaskWatermark(Class taskClass){
		TaskWatermarkDao taskWatermarkDao = SpringContextUtil.getBean("TaskWatermarkDao");
		HashMap<String,Object> params = new HashMap<String,Object>();
		params.put("TASK_CLASS", taskClass.getName());
		List<TaskWatermark> taskWatermarkList = taskWatermarkDao.query(params);
		if(taskWatermarkList== null || taskWatermarkList.isEmpty()){
			LoggerUtil.error(CLASSNAME,"任务不存在："+taskClass.getName());
			return ;
		}
		TaskWatermark TaskWatermark = taskWatermarkList.get(0);
		taskWatermarkDao.updateExecutedTimeByTask(TaskWatermark);
	}

	public static void refreshTaskWatermark(Class taskClass, Date lastExecuted){
		synchronized(TaskManager.class){
			TaskWatermarkDao taskWatermarkDao = SpringContextUtil.getBean("TaskWatermarkDao");
			HashMap<String,Object> params = new HashMap<String,Object>();
			Date NEXT_EXECUTE;
			HashMap<String,Object> paramsQuery = new HashMap<String,Object>();
			paramsQuery.put("TASK_CLASS", taskClass.getName());
			List<TaskWatermark> taskWatermarkList = taskWatermarkDao.query(paramsQuery);
			if(taskWatermarkList== null || taskWatermarkList.isEmpty()){
				NEXT_EXECUTE = DateUtil.dateCalculate(lastExecuted, 1, Calendar.DATE);
			}else{
				TaskWatermark taskWatermark = taskWatermarkList.get(0);
				if(taskWatermark.getPeriodUnit().equalsIgnoreCase("day")){
					NEXT_EXECUTE = DateUtil.dateCalculate(lastExecuted, taskWatermark.getPeriodValue(), Calendar.DATE);
				}else if(taskWatermark.getPeriodUnit().equalsIgnoreCase("week")){
					NEXT_EXECUTE = DateUtil.dateCalculate(lastExecuted, taskWatermark.getPeriodValue()*7, Calendar.DATE);
				}else if(taskWatermark.getPeriodUnit().equalsIgnoreCase("month")){
					NEXT_EXECUTE = DateUtil.dateCalculate(lastExecuted, taskWatermark.getPeriodValue(), Calendar.MONDAY);
				}else{
					LoggerUtil.error(CLASSNAME,"当前没有被考虑的或者非法的任务周期单位");
					return;
				}
			}
			params.put("NEXT_EXECUTE", NEXT_EXECUTE);
			params.put("TASK_CLASS", taskClass.getName());
			params.put("LAST_EXECUTED", lastExecuted);
			int updateRows = taskWatermarkDao.updateExecutedTimeByTaskClass(params);
			if(updateRows > 0){
				LoggerUtil.info(CLASSNAME,"任务{}完成并更新到数据库"+taskClass.getName());
			}
		}
	}



	public static Date isStartUpNewTask_pre(Class taskClass){
		Date realDate = DateUtil.getStockRealDate();
		try{
			String content = FileUtil.readFile(SysConfig.getProperty("data.persistence.rootpath") + SysConfig.getProperty("data.persistence.task.dairy"));
			if(!StringUtils.isEmpty(content)){
				HashMap<String, String> tasks = JSONObject.parseObject(content, new TypeReference<HashMap<String, String>>(){});
				String lastExecutedDate = tasks.get(taskClass.getName());
				if(lastExecutedDate != null){
					Date lastExecutedDateDT = null;
					try {
						lastExecutedDateDT = Constants.FILE_DATE_FORMAT.get().parse(lastExecutedDate);
					} catch (Exception e1) {
						LoggerUtil.error(CLASSNAME,"该任务"+taskClass.getName()+"上次执行时保存的日期格式不符合规范{}"+ lastExecutedDate,e1);
					}
					if(lastExecutedDateDT != null){
						if(!DateUtil.date1IsBeforeDate2(lastExecutedDateDT, realDate)){
							LoggerUtil.info(CLASSNAME,"任务"+SysConfig.getProperty(taskClass.getName()+"已经执行完成，不再启动"+taskClass.getName()));
							return null;
						}
					}else{
						return lastExecutedDateDT;
					}
				}
			}
			LoggerUtil.info(CLASSNAME,"开始执行任务"+ SysConfig.getProperty(taskClass.getName()+";task class name:"+taskClass.getName()));
			return realDate;
		}catch(JSONException e){
			LoggerUtil.error(CLASSNAME,"任务管理异常",e);
			LoggerUtil.info(CLASSNAME,"开始执行任务"+SysConfig.getProperty(taskClass.getName()+";task class name:"+taskClass.getName()));
			return realDate;
		}
	}

	public static void isStartUpNewTask_end(Class taskClass, Date realDate){
		LoggerUtil.info(CLASSNAME,"任务："+SysConfig.getProperty(taskClass.getName(), taskClass.getName())+"执行完成");
		String content = FileUtil.readFile(SysConfig.getProperty("data.persistence.rootpath") + SysConfig.getProperty("data.persistence.task.dairy"));
		HashMap<String, String> tasks;
		if(StringUtils.isEmpty(content)){
			tasks = new HashMap<String,String>();
		}else{
			tasks = JSONObject.parseObject(content, new TypeReference<HashMap<String, String>>(){});
		}
		tasks.put(taskClass.getName(), Constants.FILE_DATE_FORMAT.get().format(realDate));
		FileUtil.serializeObjByJson(SysConfig.getProperty("data.persistence.rootpath") + SysConfig.getProperty("data.persistence.task.dairy"), tasks, true);
		LoggerUtil.info(CLASSNAME,"任务："+SysConfig.getProperty(taskClass.getName(), taskClass.getName())+"执行完成且保存到日志文件，已保存到"+Constants.FILE_DATE_FORMAT.get().format(realDate)+"的数据");
	}
	
}
  
