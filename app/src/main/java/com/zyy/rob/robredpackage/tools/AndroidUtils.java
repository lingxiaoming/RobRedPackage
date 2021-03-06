package com.zyy.rob.robredpackage.tools;

import android.provider.Settings;

import com.zyy.rob.robredpackage.MyApplication;

/**
 * 这里描述下用途吧
 * Created by lingxiaoming on 2016/6/21 0021.
 */
public class AndroidUtils {
    public static String getAndroidId(){
        return Settings.Secure.getString(MyApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getMyCode(){
        String androidIdToMd5String = MD5.Md5(getAndroidId() + "ling4766897");

        char[] chars = androidIdToMd5String.toCharArray();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(chars[4]);
        stringBuffer.append(chars[7]);
        stringBuffer.append(chars[1]);
        stringBuffer.append(chars[6]);
        stringBuffer.append(chars[5]);
        return stringBuffer.toString();
    }
}
