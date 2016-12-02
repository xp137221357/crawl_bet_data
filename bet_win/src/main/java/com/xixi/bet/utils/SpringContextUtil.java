package com.xixi.bet.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

public class SpringContextUtil implements ApplicationContextAware{
	public static ApplicationContext applicationContext;
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextUtil.applicationContext=applicationContext;
	}
	
	public synchronized static <T> T getBean(String name){
		return (T) applicationContext.getBean(name);
	}
	
	public synchronized static boolean containsBean(String name){
		return null!=applicationContext&&applicationContext.containsBean(name);
	}
	
	public synchronized static <T> Map<String, T> getBeansOfType(Class<T> type){
		if(null==applicationContext){
			return null;
		}
		return applicationContext.getBeansOfType(type);
	}
	
	public synchronized static <T> T getSingeBeansOfType(Class<T> type){
		Map<String, T> beanMap = getBeansOfType(type);
		if(null!=beanMap&&beanMap.size()>0){
			return (T) beanMap.values().toArray()[0];
		}
		return null;
	}
}
