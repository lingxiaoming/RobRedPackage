package com.zyy.rob.robredpackage.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zyy.rob.robredpackage.FloatService;
import com.zyy.rob.robredpackage.MyApplication;
import com.zyy.rob.robredpackage.R;
import com.zyy.rob.robredpackage.tools.AndroidUtils;
import com.zyy.rob.robredpackage.tools.LogUtils;
import com.zyy.rob.robredpackage.tools.PrefsUtils;
import com.zyy.rob.robredpackage.tools.UmengAgentUtils;
import com.zyy.rob.robredpackage.tools.VersionUtils;
import com.zyy.rob.robredpackage.tools.alipay.AlipayTool;
import com.zyy.rob.robredpackage.tools.alipay.PayCommonResult;
import com.zyy.rob.robredpackage.tools.alipay.RechargeInfo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * User: xiaoming
 * Date: 2016-05-23
 * Time: 20:47
 * 描述一下这个类吧
 * <p>
 * Created by apple on 16/5/23.
 */
public class MainFragment extends Fragment implements View.OnClickListener, AccessibilityManager.AccessibilityStateChangeListener {

    private static final String TAG = "MainFragment";
    private Button btnOpen;//开始助手
    private TextView tvVersion;
    private String activationCode = "";
    private AccessibilityManager accessibilityManager;
    private View ivShare, ivArrowLeft, ivArrowRight;

    private String androidID;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private boolean showAskPermission(){
        return (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 200:
                boolean writeAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                if(writeAccepted){
                    File file = new File(localShareImagepath);
                    if(!file.exists()){
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    Toast.makeText(MyApplication.getInstance(), "写入文件权限获取失败，您将不能分享朋友圈", Toast.LENGTH_LONG).show();
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    private String localShareImagepath;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        copyResToSdcard(Environment.getExternalStorageDirectory().getPath());

        localShareImagepath=Environment.getExternalStorageDirectory().getPath()+"/"+"qrcode.png";
        if(showAskPermission()){
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
            int permsRequestCode = 200;
            requestPermissions(perms, permsRequestCode);
        }else {
            File file = new File(localShareImagepath);
            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        btnOpen = (Button) view.findViewById(R.id.btn_open);
        btnOpen.setOnClickListener(this);
        tvVersion = (TextView) view.findViewById(R.id.tv_version);

        view.findViewById(R.id.tv_setting).setOnClickListener(this);
        view.findViewById(R.id.tv_help).setOnClickListener(this);
        ivArrowLeft = view.findViewById(R.id.iv_arrow_left);
        ivArrowLeft.setOnClickListener(this);
        ivArrowRight = view.findViewById(R.id.iv_arrow_right);
        ivArrowRight.setOnClickListener(this);

        ivShare = view.findViewById(R.id.iv_share);
        ivShare.setOnClickListener(this);

        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.5f);
        alphaAnimation.setDuration(800);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        alphaAnimation.setRepeatCount(-1);
        animationSet.addAnimation(alphaAnimation);

        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(800);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setRepeatCount(-1);
        animationSet.addAnimation(scaleAnimation);
        ivArrowLeft.startAnimation(animationSet);
        ivArrowRight.startAnimation(animationSet);


        showServices();

        accessibilityManager = (AccessibilityManager) MyApplication.getInstance().getSystemService(Context.ACCESSIBILITY_SERVICE);
        accessibilityManager.addAccessibilityStateChangeListener(this);

    }


    private void createActiveCodeDialog(String code) {
        final EditText inputServer = new EditText(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("请输入注册密码").setMessage("注册号：" + code).setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                PrefsUtils.getInstance().saveActivationCode(inputServer.getText().toString());
                MyApplication.getInstance().updateCode();
            }
        });
        builder.show();
    }

    private void createPayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("快点目前稳定支持自动抢红包、自动添加附近好友、自动添加群聊好友。\n立即支付1元即可激活～")
                .setNegativeButton("免费试用", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UmengAgentUtils.event(getActivity(), "MainFragment_register_cancel");

                        int freeCount = PrefsUtils.getInstance().getIntByKey(PrefsUtils.KEY_COUNT_FREE);
                        if(freeCount == -888){
                            freeCount = 0;
                            PrefsUtils.getInstance().saveIntByKey(PrefsUtils.KEY_COUNT_FREE, freeCount);
                        }

                        if(freeCount>=0 && freeCount<2){
                            Toast.makeText(getActivity(), "剩余免费自动抢红包个数："+ (2-freeCount), Toast.LENGTH_SHORT).show();
                            if(!isAccessibilitySettingsOn()){
                                gotoSwitchService();
                            }
                        }else {
                            Toast.makeText(getActivity(), "免费试用结束，请激活永久使用", Toast.LENGTH_LONG).show();
                        }

                    }
                }).setPositiveButton("立即激活", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        UmengAgentUtils.event(getActivity(), "MainFragment_register_sure");
                        pay();
                    }
        });
        builder.setCancelable(false);
        builder.show();
    }


    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
//    private String getOutTradeNo() {
//        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
//        Date date = new Date();
//        String key = format.format(date);
//
//        Random r = new Random();
//        key = key + r.nextInt();
//        key = key.substring(0, 15);
//        return key;
//    }
    private void pay() {

        RechargeInfo rechargeInfo = new RechargeInfo();
//        rechargeInfo.setOrder_id(getOutTradeNo());
        rechargeInfo.setTitle("快点_" + VersionUtils.getPackageInfo(getActivity()).versionName + "_" +
                Build.MODEL + "_" + Build.BRAND + "_" + Build.VERSION.RELEASE + "_" + androidID);
        rechargeInfo.setDesc("用户充值desc");
        rechargeInfo.setAmount("1");
        rechargeInfo.setReturn_url("http://notify.msp.hk/notify.htm");
        rechargeInfo.setAlipay_pid("2088221928432959");
        rechargeInfo.setAlipay_account("ling4766897@163.com");
        rechargeInfo.setAlipay_public_key("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB");
        rechargeInfo.setAlipay_private_key(
                "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANEtvPGDc5jf1OYD" +
                        "pgqLGnnPWzL2SmJqMzYs2SjmqJhYhyyUESQptmXZIGnW1SihZwX7ECqhkpEaeYld" +
                        "W2tnt8iBGOA0z6Ou3dPOVXjr8ngagqNC2hyAfLyrvdIu81AQlUnZlpN4N+8Hq9b0" +
                        "YpXIOfmULJ95KPljHJo1PxBR/66TAgMBAAECgYACEXm0yZ/y+wOX4qFZqVZxreMw" +
                        "9c52eqZW+sqK5Pz1xKpRfoVM3jy3dRYk9cLVzALAxGK8iSxx0tkwyoOE5Fmk1U1O" +
                        "Pmdc0TLQlpP8QQdR+gY43X6OGu8cNre4O3xg7VZ3UKRjjnSYQg7eilTxWyepNdSa" +
                        "ScSu9cYIhCjQzpH1QQJBAPHRrDHsfMIiFTRsfs1x6V/xf7TF0vbt5OQ4OxOUG+8P" +
                        "BxikNAL/LV4sjXfSquxdspzA06cygZt3ewp3hNYThe0CQQDdcgyesnjgPJ5yLWOY" +
                        "WXHAzzUMul8Sjy34iSpiPdr1hGwxQuMTL8iD6oHmpJV8pNME7VRdbkdm24aY3t3m" +
                        "SXZ/AkAqHZBV6ZAY54K17Kdw9IPmt9K8EzAY3Xnd3YU8dbEfw4hC3GZKl1K5chz5" +
                        "X3FxVShEcLjsB7nW78o4GnTCLAhJAkB8NNcdQC+KXpXkps7BChJCujYgMHzY9RQs" +
                        "3gq21cj1gtQIgWLKRTfrveIkktYB9pUho1h5mzxTVfhV0FOYMkZTAkEAqt81LzPU" +
                        "kunX9IJk8VHkxTKFklf6ecG6L/0QCb+9V/ENLOzFuG8wx8g4+R3Z87hEc8OaPSM1" +
                        "JcCuswucuXxTww==");


        PayCommonResult payResult = AlipayTool.newInstance().pay(rechargeInfo, getActivity(), new AlipayTool.PayResultListner() {
            @Override
            public void onResult(int payResult, String msg) {
                if (AlipayTool.PAY_SUCCESS == payResult) {
                    Toast.makeText(getActivity(), "支付成功，点击开启让“快点抢红包”为您服务吧～", Toast.LENGTH_SHORT).show();
                    PrefsUtils.getInstance().saveActivationCode(activationCode);
                    MyApplication.getInstance().updateCode();
                } else if (AlipayTool.PAY_FAIL == payResult || AlipayTool.PAY_UNKNOW == payResult) {
                    Toast.makeText(getActivity(), "支付失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (payResult.resultCode == PayCommonResult.RESULT_PARAM_ERROR) {
            Toast.makeText(getActivity(), payResult.reasonDes, Toast.LENGTH_SHORT).show();
        }
    }


    private void showServices() {
        androidID = AndroidUtils.getAndroidId();

        MobclickAgent.onProfileSignIn(androidID);//友盟统计登陆的账号

        activationCode = AndroidUtils.getMyCode();

        tvVersion.setText(VersionUtils.getPackageInfo(getActivity()).versionName);

        tvVersion.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                UmengAgentUtils.event(getActivity(), "MainFragment_longClick");
                ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
                cmb.setText(androidID);
                Toast.makeText(getActivity(), "注册码复制成功", Toast.LENGTH_LONG).show();
                createActiveCodeDialog(androidID);
                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open:

                UmengAgentUtils.event(getActivity(), "MainFragment_onClick_btn_open");

                if (!TextUtils.equals(PrefsUtils.getInstance().getActivationCode(), activationCode)) {
                    //TODO 支付宝支付
                    createPayDialog();
                    return;
                }
                gotoSwitchService();
                break;
            case R.id.tv_help:
            case R.id.iv_arrow_left:
                UmengAgentUtils.event(getActivity(), "MainFragment_onClick_help");
                ((MainActivity) getActivity()).scrollToPage(0);
                break;
            case R.id.tv_setting:
            case R.id.iv_arrow_right:
                UmengAgentUtils.event(getActivity(), "MainFragment_onClick_setting");
                ((MainActivity) getActivity()).scrollToPage(2);
                break;
            case R.id.iv_share:
                UmengAgentUtils.event(getActivity(), "MainFragment_onClick_share");
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
//                shareIntent.putExtra(Intent.EXTRA_TEXT, "123");
//                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "快点抢红包");
                shareIntent.putExtra("Kdescription", "快点抢红包神器，官方下载地址http://zhushou.360.cn/detail/index/soft_id/3299276");//微信朋友圈专用

                copyResToSdcard(Environment.getExternalStorageDirectory().getPath());
                Uri image = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath()+"/qrcode.png"));
//                Uri uri = Uri.parse("file:///android_asset/qrcode.png");
//                Toast.makeText(getActivity(), ""+Environment.getExternalStorageDirectory().getPath(), Toast.LENGTH_LONG).show();
                shareIntent.putExtra(Intent.EXTRA_STREAM, image);

                //设置分享列表的标题，并且每次都显示分享列表
                startActivity(Intent.createChooser(shareIntent, "分享到"));
                break;
        }
    }

    /*
* 将raw里的文件copy到sd卡下
* */
    public void copyResToSdcard(String name){//name为sd卡下制定的路径
        Field[] raw = R.raw.class.getFields();
        for (Field r : raw) {
            try {
                //     System.out.println("R.raw." + r.getName());
                int id=getResources().getIdentifier(r.getName(), "raw", MyApplication.getInstance().getPackageName());
                if(r.getName().equals("qrcode")){
                    String path=name+"/"+r.getName()+".png";
                    File file = new File(path);
                    if(!file.exists()){
                        file.createNewFile();
                    }
                    BufferedOutputStream bufEcrivain = new BufferedOutputStream((new FileOutputStream(file)));
                    BufferedInputStream VideoReader = new BufferedInputStream(getResources().openRawResource(id));
                    byte[] buff = new byte[20*1024];
                    int len;
                    while( (len = VideoReader.read(buff)) > 0 ){
                        bufEcrivain.write(buff,0,len);
                    }
                    bufEcrivain.flush();
                    bufEcrivain.close();
                    VideoReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void gotoSwitchService() {
        if (isAccessibilitySettingsOn()) {
            Intent mAccessbilitySettings = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(mAccessbilitySettings);
            Toast.makeText(MyApplication.getInstance(), "在辅助功能-服务中\n关闭\"快点抢红包\"", Toast.LENGTH_LONG).show();
        } else {
            Intent mAccessbilitySettings = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(mAccessbilitySettings);
            Toast.makeText(MyApplication.getInstance(), "在辅助功能-服务中\n开启\"快点抢红包\"", Toast.LENGTH_LONG).show();
            return;
        }
    }


    // To check if service is enabled
    private boolean isAccessibilitySettingsOn() {
        Context mContext = getContext();
        int accessibilityEnabled = 0;
        final String service = "com.zyy.rob.robredpackage/com.zyy.rob.robredpackage.RobService";
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            LogUtils.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            LogUtils.d(TAG, "***ACCESSIBILIY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();

                    LogUtils.d(TAG, "-------------- > accessabilityService :: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        LogUtils.d(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            LogUtils.d(TAG, "***ACCESSIBILIY IS DISABLED***");
        }

        return accessibilityFound;
    }

    @Override
    public void onAccessibilityStateChanged(boolean enabled) {
        LogUtils.d(TAG, "onAccessibilityStateChanged " + enabled);
//        Toast.makeText(getActivity(), "onAccessibilityStateChanged " + enabled, Toast.LENGTH_LONG).show();
        if (enabled) {
            show();
        } else {
            hide();
        }
    }

    private void hide() {
        int freeCount = PrefsUtils.getInstance().getIntByKey(PrefsUtils.KEY_COUNT_FREE);
        if(freeCount == -888){
            freeCount = 0;
            PrefsUtils.getInstance().saveIntByKey(PrefsUtils.KEY_COUNT_FREE, freeCount);
        }

        if(!TextUtils.equals(PrefsUtils.getInstance().getActivationCode(),
                AndroidUtils.getMyCode()) && (freeCount<0 || freeCount>=2)){
            btnOpen.setText("激活");
        }else {
            btnOpen.setText("开");
            Intent intent = new Intent(MyApplication.getInstance(), FloatService.class);
            MyApplication.getInstance().stopService(intent);
        }
    }

    private void show() {
        int freeCount = PrefsUtils.getInstance().getIntByKey(PrefsUtils.KEY_COUNT_FREE);
        if(freeCount == -888){
            freeCount = 0;
            PrefsUtils.getInstance().saveIntByKey(PrefsUtils.KEY_COUNT_FREE, freeCount);
        }

        if(!TextUtils.equals(PrefsUtils.getInstance().getActivationCode(),
                AndroidUtils.getMyCode()) && (freeCount<0 || freeCount>=2)){
            btnOpen.setText("激活");
        }else {
            btnOpen.setText("关");
            Intent intent = new Intent(MyApplication.getInstance(), FloatService.class);
            MyApplication.getInstance().startService(intent);
        }
    }

    public void onResume() {
        super.onResume();
        if (isAccessibilitySettingsOn()) {
            show();
        } else {
            hide();
        }
        MobclickAgent.onPageStart("MainFragment"); //统计页面，"MainScreen"为页面名称，可自定义
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainFragment");
    }

}
