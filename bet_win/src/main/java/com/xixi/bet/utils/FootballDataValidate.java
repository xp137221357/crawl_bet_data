package com.xixi.bet.utils;

/**
 * Created by Administrator on 2016/12/2.
 */
public class FootballDataValidate {

    public int validateFootballData(String str) {
        if(str==null||str.indexOf("game=Array")<0){
            return -1;
        }
        int position=str.indexOf("game=Array");
        String targetStr=str.substring(0,position).replaceAll("\"", "");
        String[] arr = targetStr.split(";");
        if(arr.length<23){
            return -1;
        }

        if(arr[0].indexOf("matchname")<0 ||
                arr[1].indexOf("matchname_cn")<0 ||
                arr[5].indexOf("MatchTime")<0 ||
                arr[6].indexOf("ScheduleID")<0 ||
                arr[7].indexOf("hometeam")<0 ||
                arr[8].indexOf("guestteam")<0 ||
                arr[9].indexOf("hometeam_cn")<0 ||
                arr[10].indexOf("guestteam_cn")<0 ||
                arr[17].indexOf("hometeamID")<0 ||
                arr[18].indexOf("guestteamID")<0 ||
                arr[19].indexOf("hOrder")<0 ||
                arr[20].indexOf("gOrder")<0 ||
                arr[21].indexOf("neutrality")<0 ||
                arr[22].indexOf("season")<0 ){
            return -1;
        }
        return 0;
    }
}
