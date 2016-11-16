package com.zyy.rob.robredpackage;
/**
 * User: xiaoming
 * Date: 2016-05-14
 * Time: 23:12
 * 描述一下这个类吧
 */

import android.app.Application;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.zyy.rob.robredpackage.base.Constants;
import com.zyy.rob.robredpackage.tools.AndroidUtils;
import com.zyy.rob.robredpackage.tools.LogUtils;
import com.zyy.rob.robredpackage.tools.PrefsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 16/5/14.
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    public static boolean robRedPackage = false;
    public static boolean robredpackageQQ = false;
    public static boolean addNearFriend = false;
    public static boolean addGroupFriend = false;
    public static boolean openPackage = false;//拆红包声音
    public static boolean openFloat = false;//悬浮窗开关
    public static boolean filterSwitch = false;//过滤词开关
    public static boolean replySwitch = false;//回复词开关
    public static int robRedPackageLateTime = 0;//以秒＊10为单位，如延迟2.3秒，这个值就是23
    public static String registerCode;//本地保存的激活码
    public static String realRegisterCode;//生成的激活码,这个才是有效的

    public static String topClassname = "";//保存当前屏幕显示的页面
    public static final boolean debug = false;

    private SoundPool pool;
    private int sourceid;
    private static MyApplication myApplication;
    public List<String> filters = new ArrayList<>();
    public List<String> replys = new ArrayList<>();

    public static MyApplication getInstance(){
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        lusis.gciklusis.gcik.a3.e.a(getApplicationContext());
        realRegisterCode = AndroidUtils.getMyCode();

        PrefsUtils.getInstance().saveBooleanByKey(Constants.PREF_KEY_REDPACKAGE, true);
        registerCode = PrefsUtils.getInstance().getActivationCode();
        robRedPackage = PrefsUtils.getInstance().getBooleanByKey(Constants.PREF_KEY_REDPACKAGE);
        robredpackageQQ = PrefsUtils.getInstance().getBooleanByKey(Constants.PREF_KEY_REDPACKAGE_QQ);
        addNearFriend = PrefsUtils.getInstance().getBooleanByKey(Constants.PREF_KEY_ADDNEAR);
        addGroupFriend = PrefsUtils.getInstance().getBooleanByKey(Constants.PREF_KEY_ADDGROUP);
        openFloat = PrefsUtils.getInstance().getBooleanByKey(Constants.PREF_OPEN_FLOAT);
        openPackage = PrefsUtils.getInstance().getBooleanByKey(Constants.PREF_KEY_OPENPACKAGE);
        filterSwitch = PrefsUtils.getInstance().getBooleanByKey(Constants.PREF_KEY_FILTER);
        replySwitch = PrefsUtils.getInstance().getBooleanByKey(Constants.PREF_KEY_REPLY);
        robRedPackageLateTime = PrefsUtils.getInstance().getIntByKey(Constants.PREF_KEY_LATETIME);

        filters = PrefsUtils.getInstance().getStringListByKey(PrefsUtils.KEY_FILTER_WORDS);
        replys = PrefsUtils.getInstance().getStringListByKey(PrefsUtils.KEY_REPLY_WORDS);
        //指定声音池的最大音频流数目为10，声音品质为5
        pool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        //载入音频流，返回在池中的id
        sourceid = pool.load(this, R.raw.recover_coin, 0);
    }

    public boolean hasRegiste(){
        return TextUtils.equals(realRegisterCode, registerCode);
    }

    public void updateCode(){
        registerCode = PrefsUtils.getInstance().getActivationCode();
    }

    public void playMononey(long delay){
        if(PrefsUtils.getInstance().getBooleanByKey(Constants.PREF_KEY_OPENPACKAGE)){
            handler.sendEmptyMessageDelayed(1, delay);
        }

    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            LogUtils.e(TAG, "play money");
            pool.play(sourceid, 1, 1, 0, 0, 1);
            return false;
        }
    });

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
