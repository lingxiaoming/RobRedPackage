package com.zyy.rob.robredpackage.ui;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import com.zyy.rob.robredpackage.R;
import com.zyy.rob.robredpackage.RobService;
import com.zyy.rob.robredpackage.tools.MD5;
import com.zyy.rob.robredpackage.tools.PrefsUtils;
import com.zyy.rob.robredpackage.tools.VersionUtils;
import com.zyy.rob.robredpackage.tools.alipay.AlipayTool;
import com.zyy.rob.robredpackage.tools.alipay.PayCommonResult;
import com.zyy.rob.robredpackage.tools.alipay.RechargeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * User: xiaoming
 * Date: 2016-05-23
 * Time: 20:47
 * 描述一下这个类吧
 *
 * Created by apple on 16/5/23.
 */
public class MainFragment extends Fragment implements View.OnClickListener, AccessibilityManager.AccessibilityStateChangeListener {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        AnimationSet animationSet= new AnimationSet(true);
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

        accessibilityManager = (AccessibilityManager) getActivity().getSystemService(Context.ACCESSIBILITY_SERVICE);
        accessibilityManager.addAccessibilityStateChangeListener(this);
    }


    private void createActiveCodeDialog(String code){
        final EditText inputServer = new EditText(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("请输入注册密码").setMessage("注册号："+code).setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                PrefsUtils.getInstance(getActivity()).saveActivationCode(inputServer.getText().toString());
            }
        });
        builder.show();
    }

    private void createPayDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("快点目前稳定支持自动抢红包、自动添加附近好友、自动添加群聊好友。\n立即支付1元即可激活～")
                .setNegativeButton("取消", null);
        builder.setPositiveButton("立即激活", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                pay();
            }
        });
        builder.show();
    }


    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     *
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

    private void pay(){

        RechargeInfo rechargeInfo = new RechargeInfo();
//        rechargeInfo.setOrder_id(getOutTradeNo());
        rechargeInfo.setTitle("快点_"+VersionUtils.getPackageInfo(getActivity()).versionName+"_"+
                Build.MODEL+"_"+Build.BRAND+"_"+Build.VERSION.RELEASE+"_"+androidID);
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



        PayCommonResult payResult = AlipayTool.newInstance().pay(rechargeInfo, getActivity(), new AlipayTool.PayResultListner()
        {
            @Override
            public void onResult(int payResult, String msg)
            {
                if (AlipayTool.PAY_SUCCESS == payResult)
                {
                    Toast.makeText(getActivity(), "支付成功，点击开启让“快点”为您服务吧～", Toast.LENGTH_SHORT).show();
                    PrefsUtils.getInstance(getActivity()).saveActivationCode(activationCode);
                } else if (AlipayTool.PAY_FAIL == payResult || AlipayTool.PAY_UNKNOW == payResult)
                {
                    Toast.makeText(getActivity(), "支付失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (payResult.resultCode == PayCommonResult.RESULT_PARAM_ERROR)
        {
            Toast.makeText(getActivity(), payResult.reasonDes, Toast.LENGTH_SHORT).show();
        }
    }



    private void showServices() {
        androidID  = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        StringBuffer sb = new StringBuffer();
        sb.append("AndroidId:");
        sb.append(androidID);

        MobclickAgent.onProfileSignIn(androidID);//友盟统计登陆的账号

        sb.append("\n");
        sb.append("AndroidId(MD5):");
        String androidIdToMd5String = MD5.Md5(androidID+"ling4766897");
        sb.append(androidIdToMd5String);

        sb.append("\n");
        sb.append("AndroidId(最终激活码):");
        char[] chars = androidIdToMd5String.toCharArray();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(chars[4]);
        stringBuffer.append(chars[7]);
        stringBuffer.append(chars[1]);
        stringBuffer.append(chars[6]);
        stringBuffer.append(chars[5]);
        sb.append(stringBuffer);

        activationCode = stringBuffer.toString();

        sb.append("\n");
        sb.append("AndroidId(pref):");
        String s = PrefsUtils.getInstance(getActivity()).getActivationCode();
        sb.append(s);

//        tvAllService.setText(sb);
        tvVersion.setText(VersionUtils.getPackageInfo(getActivity()).versionName);

        tvVersion.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
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

                if (!TextUtils.equals(PrefsUtils.getInstance(getActivity()).getActivationCode(), activationCode)) {
                    //TODO 支付宝支付
                    createPayDialog();

                    return;
                }


                if (isWorked("com.zyy.rob.robredpackage.FloatService")) {
                    hide();

                } else {
                    if (!isServiceEnabled()) {
                        Intent mAccessbilitySettings = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivity(mAccessbilitySettings);
                        Toast.makeText(getActivity(), "在辅助功能-服务中\n开启\"快点\"", Toast.LENGTH_LONG).show();
                        return;
                    }
                    show();
                }


                break;
            case R.id.tv_help:
            case R.id.iv_arrow_left:
                ((MainActivity) getActivity()).scrollToPage(0);
                break;
            case R.id.tv_setting:
            case R.id.iv_arrow_right:
                ((MainActivity) getActivity()).scrollToPage(2);
                break;
            case R.id.iv_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.pgyer.com/5KMi");
                shareIntent.setType("text/plain");

                //设置分享列表的标题，并且每次都显示分享列表
                startActivity(Intent.createChooser(shareIntent, "分享到"));
                break;

        }
    }

    private boolean isServiceEnabled() {
        List<AccessibilityServiceInfo> accessibilityServices =
                accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(getActivity().getPackageName() + "/.RobService")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onAccessibilityStateChanged(boolean enabled) {
        updateServiceStatus();
    }

    /**
     * 更新当前 HongbaoService 显示状态
     */
    private void updateServiceStatus() {
        if (isServiceEnabled()) {
            show();
        } else {
            hide();
        }
    }

    /**
     * 隐藏显示歌词的TextView
     */
    private void hide() {
        btnOpen.setText("開");
        Intent intent = new Intent(getActivity(), FloatService.class);
        getActivity().stopService(intent);
        getActivity().stopService(new Intent(getActivity(), RobService.class));
    }

    /**
     * 显示悬浮的TextView
     */
    private void show() {
        btnOpen.setText("閉");
        Intent intent = new Intent(getActivity(), FloatService.class);
        getActivity().startService(intent);
        getActivity().startService(new Intent(getActivity(), RobService.class));
    }

    private boolean isWorked(String className) {
        ActivityManager myManager = (ActivityManager) getActivity().getApplicationContext().getSystemService(
                Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(className)) {
                return true;
            }
        }
        return false;

    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainFragment"); //统计页面，"MainScreen"为页面名称，可自定义
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainFragment");
    }

}
