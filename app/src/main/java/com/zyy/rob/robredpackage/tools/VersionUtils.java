package com.zyy.rob.robredpackage.tools;
/**
 * User: xiaoming
 * Date: 2016-05-22
 * Time: 23:34
 * 描述一下这个类吧
 */

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by apple on 16/5/22.
 */
public class VersionUtils {

    /**
     * 获取版本名称用.versionName
     * @param context
     * @return
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }
}
