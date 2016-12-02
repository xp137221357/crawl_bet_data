package com.xixi.bet.dao;

import com.xixi.bet.bean.QiutanDataBean;
import com.xixi.bet.bean.QiutanMatchInfoBean;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.List;

@Repository(value = "qiutanDataDao")
public interface QiutanDataDao {

	public HashMap queryQiutanMaxScheduleID() throws Exception;

	public int insertQiutanData(QiutanDataBean qiutanDataBean) throws Exception;

	public int insertQiutanMatchInfo(QiutanMatchInfoBean qiutanMatchInfoBean) throws Exception;

}
