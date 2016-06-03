package com.zyy.rob.robredpackage;
/**
 * User: xiaoming
 * Date: 2016-05-14
 * Time: 23:12
 * 描述一下这个类吧
 */

import android.app.Application;

import com.zyy.rob.robredpackage.base.Constants;
import com.zyy.rob.robredpackage.tools.PrefsUtils;

/**
 * Created by apple on 16/5/14.
 */
public class MyApplication extends Application {
    public static boolean robRedPackage = false;
    public static boolean addNearFriend = false;
    public static boolean addGroupFriend = false;
    public static boolean openFloat = true;//悬浮窗开关

    public static String topClassname = "";//保存当前屏幕显示的页面
    public static final boolean debug = false;

    @Override
    public void onCreate() {
        super.onCreate();
        robRedPackage = PrefsUtils.getInstance(this).getBooleanByKey(Constants.PREF_KEY_REDPACKAGE);
        addNearFriend = PrefsUtils.getInstance(this).getBooleanByKey(Constants.PREF_KEY_ADDNEAR);
        addGroupFriend = PrefsUtils.getInstance(this).getBooleanByKey(Constants.PREF_KEY_ADDGROUP);
        openFloat = PrefsUtils.getInstance(this).getBooleanByKey(Constants.PREF_OPEN_FLOAT);

    }
}
