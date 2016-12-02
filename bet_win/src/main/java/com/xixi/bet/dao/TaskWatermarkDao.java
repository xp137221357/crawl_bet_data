package com.xixi.bet.dao;

import com.xixi.bet.bean.TaskWatermark;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository(value = "TaskWatermarkDao") 
public interface TaskWatermarkDao extends BaseDao<TaskWatermark> {

	public int updateExecutedTimeByTaskClass(Map obj);
	
	public int updateExecutedTimeByTask(TaskWatermark obj);
	
	public List<TaskWatermark> queryIsActive(Map params);
	
	public List<TaskWatermark> taskReport(Map params);
}
