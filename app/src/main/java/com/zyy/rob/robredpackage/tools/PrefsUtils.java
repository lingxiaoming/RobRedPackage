package com.zyy.rob.robredpackage.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.zyy.rob.robredpackage.MyApplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
//    public final static String KEY_TIMESTAMP_FREE = "free_timestamp_key";
    public final static String KEY_COUNT_FREE = "free_count_key";
    public final static String KEY_FILTER_WORDS = "key_filter_words";
    public final static String KEY_REPLY_WORDS = "key_reply_words";

    private static PrefsUtils mPrefsUtils;
    private SharedPreferences preference;

    public static PrefsUtils getInstance() {
        if (null == mPrefsUtils) {
            mPrefsUtils = new PrefsUtils();
        }
        return mPrefsUtils;
    }

    private PrefsUtils() {
        preference = MyApplication.getInstance().getSharedPreferences(PREFERENCE_ACTIVATION_CODE, Context.MODE_PRIVATE);
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

    public void saveStringSetByKey(String key, List<String> values){
        Editor edit = preference.edit();
        edit.putStringSet(key, new HashSet<>(values));
        edit.commit();
    }

    public List<String> getStringListByKey(String key){
        Set set = preference.getStringSet(key, new HashSet<String>());
        return new ArrayList<>(set);
    }

    public String getStringByKey(String key){
        return preference.getString(key, "");
    }

    public void saveIntByKey(String key, int value){
        Editor edit = preference.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public int getIntByKey(String key){
        return preference.getInt(key, -888);//这个默认是故意这样写的，避免被用户串改prefrence数据
    }

    public void saveFloatByKey(String key, float value){
        Editor edit = preference.edit();
        edit.putFloat(key, value);
        edit.commit();
    }

    public float getFloatByKey(String key){
        return preference.getFloat(key, -1);
    }

    public void saveLongByKey(String key, long value){
        Editor edit = preference.edit();
        edit.putLong(key, value);
        edit.commit();
    }

    public long getLongByKey(String key){
        return preference.getLong(key, -888);
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
