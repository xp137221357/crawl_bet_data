/**  
 * Project Name:stk_extraction  
 * File Name:QiutanData.java  
 * Package Name:com.stk.cyq  
 * Date:2015年8月18日下午4:20:00  
 * Copyright (c) 2015, Dell All Rights Reserved.  
 *  
*/  
  
package com.xixi.bet.crawler;

import com.xixi.bet.bean.QiutanDataBean;
import com.xixi.bet.bean.QiutanMatchInfoBean;
import com.xixi.bet.dao.QiutanDataDao;
import com.xixi.bet.utils.*;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import sun.org.mozilla.javascript.internal.NativeObject;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

/**  
 * ClassName:QiutanData <br/>
 * @version    
 * @since    JDK 1.7  
 * @see        
 */
public class QiutanData {
	
	protected static String CLASSNAME="com.xixi.bet.crawler.QiutanData";
	
	@Autowired
	@Qualifier("qiutanDataDao")
	private QiutanDataDao qiutanDataDao;

	@Autowired
	@Qualifier("commonThreadPoolExecutor")
	private ThreadPoolExecutor commonThreadPoolExecutor;

	
	public void run(){
		try{
			//如果是本地的机器，则复制云服务器上的数据后直接返回
			if(SysConfig.getProperty("machine.flag.is.in.local", "false").equals("true")){
				//copyFromCloudMachine();
				return;
			}
			
			//获取下次启动该任务的时间,数据库，可配置
			Date nextTime = TaskManager.getNextStartUp(this.getClass());
			/*if(null == nextTime){
				LOG.info("---xiaopan-----return");
				return;
			}*/

			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("js");

			HashMap map=qiutanDataDao.queryQiutanMaxScheduleID();
			Long minNumber=new Long(256999);
			Long orderNumber= map==null?minNumber:(Long) map.get("schedule_id");
			orderNumber=orderNumber>minNumber?orderNumber:minNumber;
			orderNumber++;
			String url= String.format(SysConfig.getProperty("qiutan.data.url.formatter"), orderNumber);
			Elements urlEles = getUrlConnect(url,"script");
			String targetUrl="";
			for(Element ele : urlEles){
				String str=ele.select("script").get(0).attr("src");
				if(str.contains("/data1x2/")){
					targetUrl=str;
					break;
				}
			}
			targetUrl = "http://nba.win007.com"+targetUrl;
			try {
				Elements eles = getUrlConnect(targetUrl,"body");
				if(eles.size()==0 || eles.get(0).html().equals("")){
					return;
				}
				for(Element ele : eles){
					if(!ele.html().contains("game=Array")){
						continue;
					}
					String content = ele.select("body").html();
					BetDataValidate betDataValidate =new BetDataValidate();
					int resutlValidate=betDataValidate.validateBetData(content);
					if(resutlValidate<0){
						LoggerUtil.error(CLASSNAME,"数据验证发生错误");
						return;
					}
					String[] itemArr = parseQiutanData(content,engine);
					String[] itemInfoArr = parseMatchInfo(content,engine);
					String scheduleID=itemInfoArr[3];
					itemInfoArr[2]=parseTime(itemInfoArr[2]);
					if(itemArr == null || itemArr.length == 0){
						continue;
					}
					for(String str:itemArr){
						if(getWordsCount(str,"|")!=19){
							continue;
						}
						String [] strArr=str.split("\\|");
						strArr[15]=parseTime(strArr[15]);
						qiutanDataDao.insertQiutanData(transQiutanArrToQiutanData(strArr,scheduleID));
					}
					qiutanDataDao.insertQiutanMatchInfo(transQiutanMatchArrToQiutanMatchInfo(itemInfoArr));
				}
			} catch (Throwable e) {
				LoggerUtil.error(CLASSNAME,"爬取出错"+url,e);
			}
            // 文件持久化设置
			/*FileUtil.serializeObjByJson(SysConfig.getProperty("data.persistence.rootpath") +
					SysConfig.getProperty("data.persistence.subpath.cmfb")
					.replace("{date}", Constants.FILE_DATE_FORMAT.get().format(DateUtil.getStockRealDate())), CMFBBeanList);*/
			TaskManager.refreshTaskWatermark(getClass(), DateUtil.getStockRealDate());
		}catch(Throwable e){
			LoggerUtil.error(CLASSNAME,"爬取球探数据时发生异常:",e);
		}
	}

	private Elements getUrlConnect(String url,String elem) throws IOException {
		Document doc = null;
		Connection conn = JsoupUtil.getConn();
		conn.header("referer", "http://www.win007.com/");
		doc = JsoupUtil.crawlPage(url);
		return doc.select(elem);
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
		int positionStart=content.indexOf("game=Array");
		int positionEnd=content.indexOf("gameDetail=Array");
		if(positionStart==positionEnd){
			return null;
		}
		String targetStrAll=content.substring(positionStart,positionEnd).split("=")[1];
		String targetStr=targetStrAll.substring(5).replaceAll("\\(\"|\\)\"", "");
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
		int position=content.indexOf("game=Array");
		String matchInfo=content.substring(0,position).replaceAll("\"", "");
		String[] itemArr = matchInfo.split(";");
		int arrLen=itemArr.length-1;
		String[] strArr=new String[arrLen];
		for(int i=0;i<arrLen;i++){
			if(itemArr[i].length()>0){
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
	
	private QiutanDataBean transQiutanArrToQiutanData(String[] itemArr,String scheduleID){
		QiutanDataBean qiutanDataBean=new QiutanDataBean();
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
		qiutanDataBean.setValue7(itemArr[8]);
		qiutanDataBean.setValue8(itemArr[9]);
		qiutanDataBean.setValue9(itemArr[10]);
		qiutanDataBean.setValue10(itemArr[11]);
		qiutanDataBean.setValue11(itemArr[12]);
		qiutanDataBean.setValue12(itemArr[13]);
		qiutanDataBean.setValue13(itemArr[14]);
		qiutanDataBean.setValue14(itemArr[15]);
		qiutanDataBean.setValue15(itemArr[16]);
		qiutanDataBean.setValue16(itemArr[17]);
		qiutanDataBean.setValue17(itemArr[18]);
		qiutanDataBean.setValue18(itemArr[19]);
		return qiutanDataBean;
	}

	private QiutanMatchInfoBean transQiutanMatchArrToQiutanMatchInfo(String[] itemArr){

		QiutanMatchInfoBean qiutanMatchInfoBean= new QiutanMatchInfoBean();

		qiutanMatchInfoBean.setMatch_name(itemArr[0]);
		qiutanMatchInfoBean.setMatch_name_cn(itemArr[1]);
		qiutanMatchInfoBean.setMatch_time(itemArr[2]);
		qiutanMatchInfoBean.setSchedule_id(itemArr[3]);
		qiutanMatchInfoBean.setHome_team(itemArr[4]);
		qiutanMatchInfoBean.setGuest_team(itemArr[5]);
		qiutanMatchInfoBean.setHome_team_cn(itemArr[6]);
		qiutanMatchInfoBean.setGuest_team_cn(itemArr[7]);
		qiutanMatchInfoBean.setHome_team_id(itemArr[8]);
		qiutanMatchInfoBean.setGuest_team_id(itemArr[9]);
		qiutanMatchInfoBean.setSeason(itemArr[10]);

		return qiutanMatchInfoBean;
	}

	public static void main(String[] args){
		new QiutanData().run();
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
  
