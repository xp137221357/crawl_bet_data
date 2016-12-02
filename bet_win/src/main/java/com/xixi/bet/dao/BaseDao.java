package com.xixi.bet.dao;

import java.util.List;
import java.util.Map;

public interface BaseDao<T> {

	public int insert(T obj);
	
	public List<T> query(Map params);
	
	public int merge(T obj); 
	
}
