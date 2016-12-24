/**  
 * Project Name:stk_extraction  
 * File Name:QiutanData.java  
 * Package Name:com.stk.cyq  
 * Date:2015年8月18日下午4:20:00  
 * Copyright (c) 2015, Dell All Rights Reserved.  
 *  
*/  
  
package com.xixi.bet.crawler;

import com.xixi.bet.bean.QiutanFootballDataBean;
import com.xixi.bet.bean.QiutanFootballHistoryDataBean;
import com.xixi.bet.bean.QiutanFootballMatchInfoBean;
import com.xixi.bet.dao.QiutanFootballDataDao;
import com.xixi.bet.dao.QiutanFootballHistoryDataDao;
import com.xixi.bet.utils.*;
import com.xixi.bet.validate.FootballHistoryDataValidate;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

/**  
 * ClassName:QiutanFootballData <br/>
 * @version    
 * @since    JDK 1.7  
 * @see        
 */
public class QiutanFootballHistoryData {
	
	protected static String CLASSNAME="com.xixi.bet.crawler.QiutanFootballData";
	private int sleepTime=100;
	private static final int ONE_SECOND=1000;
	private static final int MAX_TRY_NUMBER=100;

	@Autowired
	@Qualifier("qiutanFootballDataDao")
	private QiutanFootballHistoryDataDao qiutanFootballDataDao;

	@Autowired
	@Qualifier("commonThreadPoolExecutor")
	private ThreadPoolExecutor commonThreadPoolExecutor;


	public void run() {
		try{
			//如果是本地的机器，则复制云服务器上的数据后直接返回
			if(SysConfig.getProperty("machine.flag.is.in.local", "false").equals("true")){
				//copyFromCloudMachine();
				return;
			}

			//获取下次启动该任务的时间,数据库，可配置
//			Date nextTime = TaskManager.getNextStartUp(this.getClass());
			/*if(null == nextTime){
				LoggerUtil.info(CLASSNAME,"---xiaopan-----return");
				return;
			}*/

			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("js");

			List<HashMap> params=qiutanFootballDataDao.queryQiutanCompanyParams();
			SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy_MM_dd");
			Date startTime = timeFormat.parse("2015-12-24");
			Date endTime = timeFormat.parse("2016-12-24");
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(startTime);
			String targetUrl = "http://op1.win007.com/overodds/cn/20161205.html";
			int tryNumber=0;
			while(true) {
				if(endTime.before(calendar.getTime())){
					return;
				}

				for (int i = 0; i < params.size(); i++) {
					calendar.add(java.util.Calendar.DATE, 1);
					String statTime = calendar.getTime().toString();
					targetUrl = "http://op1.win007.com/overodds/cn/20161205.html";
				}

				QiutanFootballHistoryDataBean dataBean = new QiutanFootballHistoryDataBean();
				try {
					Elements eles = getUrlConnect(targetUrl, "table");
					//验证
					FootballHistoryDataValidate footballDataValidate = new FootballHistoryDataValidate();
					int resutlValidate = footballDataValidate.validateFootballData(eles);
					if (resutlValidate < 0) {
						LoggerUtil.error(CLASSNAME, "数据验证发生错误");
						return;
					}
					for (Element ele : eles) {
						if (ele.toString().contains("id=\"tr_\"")) {
							//拿到值
							dataBean = getFirstData(dataBean, ele);
							ele.select("td").get(0).select("a");
						} else if (ele.toString().contains("id=\"tr2_\"")) {
							//插入值
							dataBean = getSecondData(dataBean, ele);
							//--------------------
							qiutanFootballDataDao.insertQiutanFootballHistoryData(dataBean);
						} else {
							continue;
						}
					}
				} catch (Throwable e) {
					LoggerUtil.error(CLASSNAME, "爬取出错" + targetUrl, e);
				}
			}
		}catch(Throwable e){
			LoggerUtil.error(CLASSNAME,"爬取球探数据时发生异常:",e);
			try {
				Thread.sleep(10*ONE_SECOND);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			sleepTime=sleepTime+100;
		}
	}


	private QiutanFootballHistoryDataBean getFirstData(QiutanFootballHistoryDataBean dataBean, Element ele){
		Elements eles=ele.select("td");
		dataBean.setValue0(getEleData(eles,0));
		dataBean.setValue1(getEleData(eles,1));
		dataBean.setValue2(getEleData(eles,2));
		dataBean.setValue3(getEleData(eles,3));
		dataBean.setValue4(getEleData(eles,4));
		dataBean.setValue5(getEleData(eles,5));
		dataBean.setValue6(getEleData(eles,6));
		dataBean.setValue7(getEleData(eles,7));
		dataBean.setValue8(getEleData(eles,8));
		dataBean.setValue9(getEleData(eles,9));
		dataBean.setValue10(getEleData(eles,10));
		dataBean.setValue11(getEleData(eles,11));
		dataBean.setValue12(getEleData(eles,12));
		return dataBean;
	}

	private QiutanFootballHistoryDataBean getSecondData(QiutanFootballHistoryDataBean dataBean, Element ele){
		Elements eles=ele.select("td");
		dataBean.setValue13(getEleData(eles,0));
		dataBean.setValue14(getEleData(eles,1));
		dataBean.setValue15(getEleData(eles,2));
		dataBean.setValue16(getEleData(eles,3));
		dataBean.setValue17(getEleData(eles,4));
		dataBean.setValue18(getEleData(eles,5));
		dataBean.setValue19(getEleData(eles,6));
		return dataBean;
	}

	private String getEleData(Elements eles,int index){
		if(eles.size()==0){
			return "";
		}
		Element ele=eles.get(index);
		if (ele.select("a").size()>0){
			return ele.select("a").html();
		}
		return ele.html();
	}

	private Elements getUrlConnect(String url,String elem) throws IOException {
		Document doc = null;
		Connection conn = JsoupUtil.getConn();
		conn.header("referer", "http://www.win007.com/");
		doc = JsoupUtil.crawlPage(url);
		Elements content=null;
		for (Element ele : doc.select(elem)) {
			if (!ele.toString().contains("id=\"table_schedule\"")) {
				continue;
			}
			content = ele.select("tbody").select("tr");
		}
		return content;
	}

	private String getTargetUrl(Long orderNumber){
		String targetUrl=null;
		try {
			String url = String.format(SysConfig.getProperty("qiutan.football.url.formatter"), orderNumber);
			Elements urlEles = null;
			urlEles = getUrlConnect(url, "script");
			if(urlEles==null || urlEles.size()==0){
				return null;
			}
			for (Element ele : urlEles) {
				String str = ele.select("script").get(0).attr("src");
				if (str.contains("/1x2.nowscore.com/")) {
					targetUrl = str;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return targetUrl;
	}

	/**
	 * methodName: parseQiutanData
	 *
	 * @return
	 * @throws ScriptException
	 */
	private String[] parseQiutanData(String content,ScriptEngine engine) throws ScriptException{
		//engine.eval(content.split(";")[0]);
		//String items = (String) engine.get("matchname");
		int flag=0;
		int positionStart=content.indexOf("game=Array");
		int positionEnd=content.indexOf("gameDetail=Array");
		if(positionStart==positionEnd){
			return null;
		}
		String targetStrAll=content.substring(positionStart+10,positionEnd).split(";")[0];
		String targetStr=targetStrAll.replaceAll("\\(\"|\"\\)", "");
		String[] itemArr = targetStr.split("\",\"");
		return itemArr;
	}

	/**
	 * methodName: parseQiutanData
	 * @return
	 * @throws ScriptException
	 */
	private String[] parseMatchInfo(String content,ScriptEngine engine) throws ScriptException{
		//engine.eval(content.split(";")[0]);
		//String items = (String) engine.get("matchname");
		int flag=0;
		int position=content.indexOf("game=Array");
		String matchInfo=content.substring(0,position).replaceAll("\"", "");
		String[] itemArr = matchInfo.split(";");
		int arrLen=itemArr.length-1;
		String[] strArr=new String[arrLen];
		for(int i=0;i<arrLen;i++){
			if(itemArr[i].length()>0 && itemArr[i].split("=").length>1){
				strArr[i]=itemArr[i].split("=")[1];
			}
		}
		return strArr;
	}


	/**
	 * methodName: transQiutanArrToQiutanData
	 *
	 * 转换<br/>
	 *
	 * @param itemArr
	 * @return
	 */

	private QiutanFootballDataBean transQiutanArrToQiutanData(String[] itemArr,String scheduleID){
		QiutanFootballDataBean qiutanDataBean=new QiutanFootballDataBean();
		for(int i=0;i<itemArr.length;i++){
			if(itemArr[i].equals("")){
				itemArr[i]="-1";
			}
		}
		qiutanDataBean.setSchedule_id(scheduleID);
		qiutanDataBean.setValue0(itemArr[0]);
		qiutanDataBean.setValue1(itemArr[1]);
		qiutanDataBean.setValue2(itemArr[3]);
		qiutanDataBean.setValue3(itemArr[4]);
		qiutanDataBean.setValue4(itemArr[5]);
		qiutanDataBean.setValue5(itemArr[6]);
		qiutanDataBean.setValue6(itemArr[7]);
		qiutanDataBean.setValue7(itemArr[7]);
		qiutanDataBean.setValue8(itemArr[8]);
		qiutanDataBean.setValue9(itemArr[9]);
		qiutanDataBean.setValue10(itemArr[10]);
		qiutanDataBean.setValue11(itemArr[11]);
		qiutanDataBean.setValue12(itemArr[12]);
		qiutanDataBean.setValue13(itemArr[13]);
		qiutanDataBean.setValue14(itemArr[14]);
		qiutanDataBean.setValue15(itemArr[15]);
		qiutanDataBean.setValue16(itemArr[16]);
		qiutanDataBean.setValue17(itemArr[17]);
		qiutanDataBean.setValue18(itemArr[18]);
		qiutanDataBean.setValue19(itemArr[19]);
		qiutanDataBean.setValue20(itemArr[20]);
		qiutanDataBean.setValue21(itemArr[21]);
		qiutanDataBean.setValue22(itemArr[22]);
		qiutanDataBean.setValue23(itemArr[23]);
		return qiutanDataBean;
	}

	private QiutanFootballMatchInfoBean transQiutanMatchArrToQiutanMatchInfo(String[] itemArr){

		QiutanFootballMatchInfoBean qiutanMatchInfoBean= new QiutanFootballMatchInfoBean();

		qiutanMatchInfoBean.setMatch_name(itemArr[0]);
		qiutanMatchInfoBean.setMatch_name_cn(itemArr[1]);
		qiutanMatchInfoBean.setMatch_time(itemArr[5]);
		qiutanMatchInfoBean.setSchedule_id(itemArr[6]);
		qiutanMatchInfoBean.setHome_team(itemArr[7]);
		qiutanMatchInfoBean.setGuest_team(itemArr[8]);
		qiutanMatchInfoBean.setHome_team_cn(itemArr[9]);
		qiutanMatchInfoBean.setGuest_team_cn(itemArr[10]);
		qiutanMatchInfoBean.setHome_team_id(itemArr[17]);
		qiutanMatchInfoBean.setGuest_team_id(itemArr[18]);
		qiutanMatchInfoBean.setHome_order(itemArr[19]);
		qiutanMatchInfoBean.setGuest_order(itemArr[20]);
		qiutanMatchInfoBean.setNeutrality(itemArr[21]);
		qiutanMatchInfoBean.setSeason(itemArr[22]);

		return qiutanMatchInfoBean;
	}

	public static void main(String[] args){
		new QiutanFootballData().run();
	}

	/**
	 * 字符在字符串中出现的次数
	 *
	 * @param str
	 * @param s
	 * @return
	 */
	public static int getWordsCount(String str, String s) {
		int pos = -2;
		int n = 0;

		while (pos != -1) {
			if (pos == -2) {
				pos = -1;
			}
			pos = str.indexOf(s, pos + 1);
			if (pos != -1) {
				n++;
			}
		}
		return n;
	}

	/**
	 * 解析时间
	 *
	 * @param
	 * @return
	 */
	public static String parseTime(String str) {
		String [] itemStr=str.split(",");
		String s=str;
		if(itemStr.length==6){
			s=itemStr[0]+"-"+itemStr[1].substring(0,2)+"-"+itemStr[2]+" "+itemStr[3]+":"+itemStr[4]+":"+itemStr[5];
		}
		return s;
	}
}
  
