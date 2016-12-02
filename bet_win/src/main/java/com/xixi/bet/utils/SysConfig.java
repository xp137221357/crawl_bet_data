package com.xixi.bet.utils;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Created by xiong on 2015/6/4.
 */

public class SysConfig  extends PropertyPlaceholderConfigurer {

    @Override
    protected String convertProperty(String propertyName, String propertyValue) {
        props.setProperty(propertyName,propertyValue);
        return super.convertProperty(propertyName, propertyValue);
    }

    private static final Properties props = new Properties();

    public static void load(InputStream is, Charset cs){
        try {
            Reader reader = new InputStreamReader(is, cs);
            props.load(reader);

        } catch (Exception e) {
            throw new RuntimeException(
                    "配置文件初始化失败, 请检查配置文件");
        }

    }

    public static String getProperty(String key, String defaultValue){
        return props.getProperty(key, defaultValue);
    }

    public static String getProperty(String key){
        return props.getProperty(key);
    }

    public static void setProperty(String key, String value){
        props.setProperty(key, value);
    }

    public static Properties getInstantce(){
        return props;
    }
}

