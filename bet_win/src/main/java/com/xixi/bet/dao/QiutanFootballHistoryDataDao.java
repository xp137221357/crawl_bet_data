package com.xixi.bet.dao;

import com.xixi.bet.bean.QiutanFootballDataBean;
import com.xixi.bet.bean.QiutanFootballHistoryDataBean;
import com.xixi.bet.bean.QiutanFootballMatchInfoBean;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.List;

@Repository(value = "qiutanFootballDataDao")
public interface QiutanFootballHistoryDataDao {

	public HashMap queryQiutanMaxScheduleID() throws Exception;

	public List<HashMap> queryQiutanCompanyParams() throws Exception;

	public int insertQiutanData(QiutanFootballDataBean qiutanDataBean) throws Exception;

	public int insertQiutanFootballHistoryData(QiutanFootballHistoryDataBean QiutanFootballDataBean) throws Exception;



}
