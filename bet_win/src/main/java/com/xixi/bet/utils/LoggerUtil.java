package com.xixi.bet.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2016/12/2.
 */
public class LoggerUtil {

    private static final boolean DEBUG=false;
    public static void error(String className,String reason,Throwable e){
        Logger LOG = LoggerFactory.getLogger(className);
        String title="An error occurred when excute "+className;
        String content="Reason:"+ reason +";Error:"+e;
        if(DEBUG){
            if(e==null){
                LOG.error(reason);
            }else{
                LOG.error(reason,e);
            }
            MailUtil.sendErroeToMail(title,content);
        }
    }

    public static void error(String className,String reason){

        error(reason);
    }

    public static void error(String reason,Throwable e){

        error(reason,e);
    }

    public static void error(String reason){
        error(reason);
    }

    public static void info(String className,String content){
        Logger LOG = LoggerFactory.getLogger(className);
        if(DEBUG) {
            LOG.info(content);
        }
    }

    public static void warn(String className,String content){
        Logger LOG = LoggerFactory.getLogger(className);
        if(DEBUG) {
            LOG.warn(content);
        }
    }

    public static void debug(String className,String content){
        Logger LOG = LoggerFactory.getLogger(className);
        if(DEBUG) {
            LOG.debug(content);
        }
    }

}
