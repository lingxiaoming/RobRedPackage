package com.zyy.rob.robredpackage.tools;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * 作者 : lxm on 2016/1/21
 * 描述 : 友盟统计帮助类
 */
public class UmengAgentUtils {

    public static void event(Context context, String eventId){
        MobclickAgent.onEvent(context, eventId);
    }

    public static void event(Context context, String eventId, HashMap map){
//        HashMap<String,String> map = new HashMap<String,String>();
//        map.put("type","book");
//        map.put("quantity","3");
        MobclickAgent.onEvent(context, eventId, map);
    }

}
