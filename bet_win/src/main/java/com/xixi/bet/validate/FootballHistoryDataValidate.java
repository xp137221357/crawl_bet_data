package com.xixi.bet.validate;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Administrator on 2016/12/2.
 */
public class FootballHistoryDataValidate {

    public int validateFootballData(Elements eles) {
        if(eles.size()==0){
            return -1;
        }

        Elements elesTitle= eles.get(0).select("td");

        if(elesTitle.size()!=13){
            return -1;
        }

        if(!elesTitle.get(0).html().equals("类型")){
            return -1;
        }

        if(!elesTitle.get(1).html().equals("时间")){
            return -1;
        }

        if(!elesTitle.get(2).html().equals("主队")){
            return -1;
        }

        if(!elesTitle.get(3).html().equals("主")){
            return -1;
        }

        if(!elesTitle.get(4).html().equals("和")){
            return -1;
        }

        if(!elesTitle.get(5).html().equals("客")){
            return -1;
        }

        if(!elesTitle.get(6).html().equals("主胜率")){
            return -1;
        }

        if(!elesTitle.get(7).html().equals("和率")){
            return -1;
        }

        if(!elesTitle.get(8).html().equals("客胜率")){
            return -1;
        }

        if(!elesTitle.get(9).html().equals("返还率")){
            return -1;
        }

        if(!elesTitle.get(10).html().equals("客队")){
            return -1;
        }

        if(!elesTitle.get(11).html().equals("比分")){
            return -1;
        }

        if(!elesTitle.get(12).html().equals("详情")){
            return -1;
        }

        return 0;
    }
}
