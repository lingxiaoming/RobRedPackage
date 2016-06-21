package com.zyy.rob.robredpackage.tools;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 保存在sdcard的prefrence
 * 用法
 * PropertiesConfig.getInstance("/sdcard/client_config.xml").setProperty("187", "name");
 * PropertiesConfig.getInstance("/sdcard/client_config.xml").get("187");
 * Created by lingxiaoming on 2016/6/21 0021.
 */
public class SDCardPrefrence extends Properties {
    private static SDCardPrefrence sdCardPrefrence;
    private static File file;
    private String propertyPath;
    private SDCardPrefrence() {
        propertyPath = getSDPath() +"/app";
        file = new File(propertyPath);
        if (file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static SDCardPrefrence getInstance() {
        {
           if(sdCardPrefrence == null){
               sdCardPrefrence = new SDCardPrefrence();
           }
            try {
                InputStream is = new FileInputStream(file);
                sdCardPrefrence.load(is);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return sdCardPrefrence;
        }

    }

    @Override
    public Object setProperty(String key, String value) {
        super.setProperty(key, value);
        try {
            this.store(new FileOutputStream(this.propertyPath),
                    "utf-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    public Object put(String key, String value) {
        super.put(key, value);
        try {
            this.store(new FileOutputStream(this.propertyPath),
                    "utf-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if(sdCardExist){
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            return sdDir.toString();
        }else {
            return null;
        }
    }
}
