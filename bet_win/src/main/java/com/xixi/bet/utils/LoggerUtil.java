package com.xixi.bet.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2016/12/2.
 */
public class LoggerUtil {
    public static void error(String className,String reason,Throwable e){
        Logger LOG = LoggerFactory.getLogger(className);
        String title="An error occurred when excute "+className;
        String content="Reason:"+ reason +";Error:"+e;
        MailUtil.sendErroeToMail(title,content);
        LOG.error(reason,e);
    }

    public static void error(String className,String reason){
        error(className,reason,null);
    }

    public static void error(String reason){
        error("className",reason,null);
    }

    public static void info(String className,String content){
        Logger LOG = LoggerFactory.getLogger(className);
        LOG.info(className,content);
    }

    public static void warn(String className,String content){
        Logger LOG = LoggerFactory.getLogger(className);
        LOG.warn(className,content);
    }

    public static void debug(String className,String content){
        Logger LOG = LoggerFactory.getLogger(className);
        LOG.debug(className,content);
    }

}
