package com.zyy.rob.robredpackage.tools;
/**
 * User: xiaoming
 * Date: 2016-08-15
 * Time: 21:06
 * 描述一下这个类吧
 */

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by apple on 16/8/15.
 */
public class DeleteWordDialog {
    AlertDialog.Builder builder;
    public DeleteWordDialog(Context context, String title, AlertDialog.OnClickListener sureOnClickListener){
        builder = new AlertDialog.Builder(context)
                .setTitle("确定删除:"+title+"吗")
                .setPositiveButton("确定", sureOnClickListener)
                .setNegativeButton("取消", null);
    }

    public void show(){
        builder.show();
    }

}
