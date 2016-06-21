package com.zyy.rob.robredpackage.tools;

/**
 * 这里描述下用途吧
 * Created by lingxiaoming on 2016/6/21 0021.
 */
public class DateFormat {
    public static String transToMMSS(long timeLengh){
        if(timeLengh < 0) return "00分00秒";
        long timeSecend = timeLengh / 1000;
        int h = (int) timeSecend / 3600;
        int m = (int) (timeSecend % 3600 / 60);
        int s = (int) (timeSecend % 3600 % 60);
        return String.format("%02d分%02d秒", m, s);
    }
}
