package com.xixi.bet.utils;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
* @ClassName: 	JsoupUtil
* @Description: Jsoup工具类
* @Author: 		YangCX
* @date:		2015年5月20日 上午11:25:05
*
*/ 
public class JsoupUtil {

	private static final int MAX_TIMES_TO_TRY = 3;
	
	//提供本地线程变量，同时提供一个默认的connection对象，并赋予一些常用属性
	private static final ThreadLocal<Connection> connBean = new ThreadLocal<Connection>(){
	    protected Connection initialValue() {
	    	Connection conn = Jsoup.connect("http://zq.win007.com/");
	    	conn.header("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
	    	conn.header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	    	conn.header("accept-language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
	    	conn.header("connection", "keep-alive");
	    	conn.header("referer", "http://live3.win007.com/");
	    	conn.header("host", "live3.win007.com");
	    	conn.header("Cookie", "Bet007live_hiddenID=_; Bet007live_concernId=_1147424_1231608_1218512_; Cookie=2^0^1^1^1^1^1^0^0^0^0^0^1^2^1^1^1; detailCookie=null");
	    	conn.ignoreContentType(true);
	    	conn.timeout(30000);
	        return conn;
	    }
	};


	/**
	* @Description: 爬取某个链接到的页面
	* @param: 		@param url
	* @param: 		@return
	* @param: 		@throws IOException    
	* @return: 		Document    
	* @throws:		
	*/ 
	public static Document crawlPage(String url) throws IOException {
		int times = 0;
		while(times++ < MAX_TIMES_TO_TRY){
			try {
				return getConn().url(url).get();
			} catch (IOException e) {
				if(e instanceof HttpStatusException){
					throw new IOException("404异常",e);
				}
				if(times == MAX_TIMES_TO_TRY){
					throw e;
				}
			}
		}
		return null;
	}
	
	public static Connection getConn(){
		return connBean.get();
	}
	
	public static void setConn(Connection connection){
		connBean.set(connection);
	}
	
	public static void remove(){
		connBean.remove();
	}
}
