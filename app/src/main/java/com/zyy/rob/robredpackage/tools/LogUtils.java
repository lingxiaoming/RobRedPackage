package com.zyy.rob.robredpackage.tools;
/**
 * User: xiaoming
 * Date: 2016-05-29
 * Time: 13:57
 * 日志类，调试测试模式下才输出打印，发布关闭，一面影响程序运行效率
 */

import android.util.Log;

import com.zyy.rob.robredpackage.MyApplication;

/**
 * Created by apple on 16/5/29.
 */
public class LogUtils {

    public static void i(String tag, String msg){
        if(MyApplication.debug){
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg){
        if(MyApplication.debug){
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg){
        if(MyApplication.debug){
            Log.e(tag, msg);
        }
    }
}
