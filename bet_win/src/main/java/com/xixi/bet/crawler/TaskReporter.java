package com.xixi.bet.crawler;

import com.xixi.bet.bean.TaskWatermark;
import com.xixi.bet.utils.MailUtil;
import com.xixi.bet.dao.TaskWatermarkDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TaskReporter {

	@Autowired
	@Qualifier("TaskWatermarkDao")
	private TaskWatermarkDao taskWatermarkDao;
	
	SimpleDateFormat reportSDF = new SimpleDateFormat("yyyy-MM-dd");
	
	public void run(){
		List<TaskWatermark> taskReports = taskWatermarkDao.taskReport(null);
		StringBuilder sb = new StringBuilder();
		for(TaskWatermark taskWatermark : taskReports){
			sb.append(taskWatermark.getTaskName()+"\t" + reportSDF.format(taskWatermark.getLastExecuted()) 
					+ "\t" + reportSDF.format(taskWatermark.getNextExecute()) + "\n");
		}
		MailUtil.sendTaskReportToMail(String.format("%s任务报告", reportSDF.format(new Date())),sb.toString());
	}
}
