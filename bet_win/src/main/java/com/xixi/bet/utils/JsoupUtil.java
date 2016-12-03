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
	    	Connection conn = Jsoup.connect("http://www.win007.com/");
	    	conn.header("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.75 Safari/537.36");
	    	conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
	    	conn.header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
			conn.header("Accept-Encoding", "gzip, deflate, sdch");
			conn.header("Cache-Control", "no-cache");
	    	conn.header("connection", "keep-alive");
	    	conn.header("host", "op1.win007.com");
			conn.header("Pragma", "no-cache");
			conn.header("Upgrade-Insecure-Requests", "1");
	    	conn.header("Cookie", "bskbetCookie=null; ASP.NET_SessionId=vx44y0i5ber3en55rfvyu555");
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
