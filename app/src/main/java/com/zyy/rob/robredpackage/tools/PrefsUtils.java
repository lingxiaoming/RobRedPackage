package com.zyy.rob.robredpackage.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 作者 : lxm on 2015/12/28
 * 描述 : sharedprefrence操作封装
 */
public class PrefsUtils {
    /**
     * 系统配置的配置文件
     */
    public final static String PREFERENCE_ACTIVATION_CODE = "activation_code_pref";
    public final static String KEY_ACTIVATION_CODE = "activation_code_key";

    private static PrefsUtils mPrefsUtils;
    private SharedPreferences preference;

    public static PrefsUtils getInstance(Context context) {
        if (null == mPrefsUtils) {
            mPrefsUtils = new PrefsUtils(context);
        }
        return mPrefsUtils;
    }

    private PrefsUtils(Context context) {
        preference = context.getSharedPreferences(PREFERENCE_ACTIVATION_CODE, Context.MODE_PRIVATE);
    }

    public void saveActivationCode(String loginAccount) {
        Editor edit = preference.edit();
        edit.putString(KEY_ACTIVATION_CODE, loginAccount);
        edit.commit();
    }

    public String getActivationCode() {
        return preference.getString(KEY_ACTIVATION_CODE, "");
    }


    public void saveStringByKey(String key, String value){
        Editor edit = preference.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public String getStringByKey(String key){
        return preference.getString(key, "");
    }

    public void saveBooleanByKey(String key, boolean value){
        Editor edit = preference.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public boolean getBooleanByKey(String key){
        return preference.getBoolean(key, false);
    }

}
