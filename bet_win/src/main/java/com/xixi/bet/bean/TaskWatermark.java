package com.xixi.bet.bean;

import java.util.Date;

public class TaskWatermark {

	private String taskName;
	
	private String taskClass;
	
	private String cronExpress;
	
	private Date lastExecuted;
	
	private Date nextExecute;
	
	private String remark;
	
	private Integer periodValue;
	
	private String periodUnit;

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskClass() {
		return taskClass;
	}

	public void setTaskClass(String taskClass) {
		this.taskClass = taskClass;
	}

	public String getCronExpress() {
		return cronExpress;
	}

	public void setCronExpress(String cronExpress) {
		this.cronExpress = cronExpress;
	}

	public Date getLastExecuted() {
		return lastExecuted;
	}

	public void setLastExecuted(Date lastExecuted) {
		this.lastExecuted = lastExecuted;
	}

	public Date getNextExecute() {
		return nextExecute;
	}

	public void setNextExecute(Date nextExecute) {
		this.nextExecute = nextExecute;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getPeriodValue() {
		return periodValue;
	}

	public void setPeriodValue(Integer periodValue) {
		this.periodValue = periodValue;
	}

	public String getPeriodUnit() {
		return periodUnit;
	}

	public void setPeriodUnit(String periodUnit) {
		this.periodUnit = periodUnit;
	}
}
