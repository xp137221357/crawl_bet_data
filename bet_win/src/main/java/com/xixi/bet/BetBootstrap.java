package com.xixi.bet;

import com.xixi.bet.utils.ResourceUtils;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class BetBootstrap {
	
	public static void main(String[] args){
		System.out.println("加载自带配置文件,用户路径:"+System.getProperty("user.dir"));
		try{
			File file = ResourceUtils.getFile("file:bet_win/config/log4j2.xml");
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			ConfigurationSource source = new ConfigurationSource(in);
			Configurator.initialize(null, source);
		}catch (Exception e){
			System.err.print(e.getMessage());
		}
		ArrayList<String> files = new ArrayList<String>();
		files.add("classpath:applicationContext.xml");
		String externalFilePath = System.getProperty("user.dir") + File.separator + "config"+File.separator+"applicationContext-crawler.xml";
		if(new File(externalFilePath).exists()){
			files.add(externalFilePath);
			System.out.println("加载外部配置文件");
		}else{
			files.add("classpath:applicationContext-crawler.xml");
			System.out.println("加载自带配置文件,用户路径:"+System.getProperty("user.dir"));
		}
		
		String[] str = files.toArray(new String[]{});
		new ClassPathXmlApplicationContext(str);
	}
}