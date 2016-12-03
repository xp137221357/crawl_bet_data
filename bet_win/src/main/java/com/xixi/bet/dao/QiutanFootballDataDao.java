package com.xixi.bet.dao;

import com.xixi.bet.bean.QiutanFootballDataBean;
import com.xixi.bet.bean.QiutanFootballMatchInfoBean;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.List;

@Repository(value = "qiutanFootballDataDao")
public interface QiutanFootballDataDao {

	public HashMap queryQiutanMaxScheduleID() throws Exception;

	public int insertQiutanData(QiutanFootballDataBean qiutanDataBean) throws Exception;

	public int insertQiutanMatchInfo(QiutanFootballMatchInfoBean qiutanMatchInfoBean) throws Exception;

}
