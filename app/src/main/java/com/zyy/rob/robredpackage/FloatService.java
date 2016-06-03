package com.zyy.rob.robredpackage;
/**
 * User: xiaoming
 * Date: 2016-05-17
 * Time: 21:19
 * 描述一下这个类吧
 */

import android.app.Dialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.zyy.rob.robredpackage.base.Constants;
import com.zyy.rob.robredpackage.tools.PrefsUtils;

/**
 * Created by apple on 16/5/17.
 */
public class FloatService extends Service implements CompoundButton.OnCheckedChangeListener {
    LayoutInflater inflater;
    //定义浮动窗口布局
    LinearLayout mFloatLayout, mFloatLayoutSimple;
    Dialog dialog;
    WindowManager.LayoutParams wmParams, wmParamsSimple;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;

    View mFloatView;

    private CheckBox cbNear, cbGroup, cbRobRed;


    private static final String TAG = "FloatService";

    @Override
    public void onCreate() {
        super.onCreate();
        inflater = LayoutInflater.from(getApplication());
        createFloatView();
        createFloatSimpleView();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createFloatView() {

        wmParams = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
//        wmParams.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 0;
        wmParams.y = 0;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

         /*// 设置悬浮窗口长宽数据
        wmParams.width = 200;
        wmParams.height = 80;*/

        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.view_float, null);

        //添加mFloatLayout
//        mWindowManager.addView(mFloatLayout, wmParams);
        //浮动窗口按钮
        mFloatView = mFloatLayout.findViewById(R.id.ll_floatview);
        cbNear = (CheckBox) mFloatView.findViewById(R.id.cb_near_add);
        cbGroup = (CheckBox) mFloatView.findViewById(R.id.cb_group_add);
        cbRobRed = (CheckBox) mFloatView.findViewById(R.id.cb_rob_red);

        cbRobRed.setChecked(PrefsUtils.getInstance(this).getBooleanByKey(Constants.PREF_KEY_REDPACKAGE));
        cbNear.setChecked(PrefsUtils.getInstance(this).getBooleanByKey(Constants.PREF_KEY_ADDNEAR));
        cbGroup.setChecked(PrefsUtils.getInstance(this).getBooleanByKey(Constants.PREF_KEY_ADDGROUP));

        cbNear.setOnCheckedChangeListener(this);
        cbGroup.setOnCheckedChangeListener(this);
        cbRobRed.setOnCheckedChangeListener(this);

        dialog = new Dialog(this, R.style.float_dialog);
        dialog.setContentView(mFloatLayout, wmParams);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.sendEmptyMessage(0);
            }
        });
        dialog.show();
    }

    private void createFloatSimpleView(){
        wmParamsSimple = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        //设置window type
        wmParamsSimple.type = WindowManager.LayoutParams.TYPE_TOAST;
        //设置图片格式，效果为背景透明
        wmParamsSimple.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParamsSimple.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParamsSimple.gravity = Gravity.LEFT|Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity

        wmParamsSimple.x = 0;
        wmParamsSimple.y = 0;

        //设置悬浮窗口长宽数据
        wmParamsSimple.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParamsSimple.height = WindowManager.LayoutParams.WRAP_CONTENT;

        mFloatLayoutSimple = (LinearLayout) inflater.inflate(R.layout.view_float_simple, null);
        mFloatLayoutSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(1);
            }
        });
        //设置监听浮动窗口的触摸移动
        mFloatLayoutSimple.setOnTouchListener(new View.OnTouchListener()
        {
            int lastX, lastY;
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int screenWidth = dm.widthPixels;
            int screenHeight = dm.heightPixels;
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {

                //getRawX是触摸位置相对于屏幕的坐标
                wmParamsSimple.x = (int) event.getRawX() - mFloatLayoutSimple.getWidth() / 2;
//                减25为状态栏的高度
                wmParamsSimple.y = (int) event.getRawY() - 75 - mFloatLayoutSimple.getHeight() / 2;
                //刷新
                mWindowManager.updateViewLayout(mFloatLayoutSimple, wmParamsSimple);
                return false;  //此处必须返回false，否则OnClickListener获取不到监听
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mFloatLayout != null) {
            //移除悬浮窗口
            dialog.dismiss();
            mWindowManager.removeView(mFloatLayoutSimple);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView == cbNear){
            MyApplication.addNearFriend = isChecked;
            PrefsUtils.getInstance(this).saveBooleanByKey(Constants.PREF_KEY_ADDNEAR, isChecked);
            sendBroadcast(new Intent(Constants.ACTION_FLOAT_CLOSE));
        }else if(buttonView == cbGroup){
            MyApplication.addGroupFriend = isChecked;
            PrefsUtils.getInstance(this).saveBooleanByKey(Constants.PREF_KEY_ADDGROUP, isChecked);
            sendBroadcast(new Intent(Constants.ACTION_FLOAT_CLOSE));
        }else if(buttonView == cbRobRed){
            MyApplication.robRedPackage = isChecked;
            PrefsUtils.getInstance(this).saveBooleanByKey(Constants.PREF_KEY_REDPACKAGE, isChecked);
            sendBroadcast(new Intent(Constants.ACTION_FLOAT_CLOSE));
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what){
                case 0:
                    dialog.dismiss();
                    mWindowManager.addView(mFloatLayoutSimple, wmParamsSimple);
//                    wmParamsSimple.x = 0;
//                    wmParamsSimple.y = 0;
                    break;
                case 1:
                    cbRobRed.setChecked(MyApplication.robRedPackage);
                    cbNear.setChecked(MyApplication.addNearFriend);
                    cbGroup.setChecked(MyApplication.addGroupFriend);
                    dialog.show();
                    mWindowManager.removeView(mFloatLayoutSimple);
                    break;
            }
        }
    };
}
