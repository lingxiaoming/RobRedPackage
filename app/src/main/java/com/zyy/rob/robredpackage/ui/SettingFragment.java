package com.zyy.rob.robredpackage.ui;
/**
 * User: xiaoming
 * Date: 2016-05-23
 * Time: 20:47
 * 描述一下这个类吧
 */

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zyy.rob.robredpackage.MyApplication;
import com.zyy.rob.robredpackage.R;
import com.zyy.rob.robredpackage.base.Constants;
import com.zyy.rob.robredpackage.tools.PrefsUtils;

/**
 * Created by apple on 16/5/23.
 */
public class SettingFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private CheckBox cbRedPackage, cbAddNear, cbAddGroup, cbOpenFloat, cbOpenPackage;
    private SeekBar sbRedPackage;
    private TextView tvCurrentTime;
    private TextView tvRedPackageInfoCount, tvRedPackageInfoMoney;

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView == null)
            rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cbRedPackage = (CheckBox) view.findViewById(R.id.cb_red_package);
        cbAddNear = (CheckBox) view.findViewById(R.id.cb_near_add);
        cbAddGroup = (CheckBox) view.findViewById(R.id.cb_group_add);
        cbOpenFloat = (CheckBox) view.findViewById(R.id.cb_open_float);
        tvCurrentTime = (TextView) view.findViewById(R.id.tv_now_time);
        cbOpenPackage = (CheckBox) view.findViewById(R.id.cb_open_package);
        tvRedPackageInfoCount = (TextView) view.findViewById(R.id.tv_redpackage_info_count);
        tvRedPackageInfoMoney = (TextView) view.findViewById(R.id.tv_redpackage_info_money);

        cbRedPackage.setOnCheckedChangeListener(this);
        cbAddNear.setOnCheckedChangeListener(this);
        cbAddGroup.setOnCheckedChangeListener(this);
        cbOpenFloat.setOnCheckedChangeListener(this);
        cbOpenPackage.setOnCheckedChangeListener(this);

        cbRedPackage.setChecked(PrefsUtils.getInstance().getBooleanByKey(Constants.PREF_KEY_REDPACKAGE));
        cbAddNear.setChecked(PrefsUtils.getInstance().getBooleanByKey(Constants.PREF_KEY_ADDNEAR));
        cbAddGroup.setChecked(PrefsUtils.getInstance().getBooleanByKey(Constants.PREF_KEY_ADDGROUP));
//        cbOpenFloat.setChecked(PrefsUtils.getInstance(getActivity()).getBooleanByKey(Constants.PREF_OPEN_FLOAT));
        cbOpenFloat.setChecked(true);//TODO 目前还没有实现这个功能
        cbOpenPackage.setChecked(PrefsUtils.getInstance().getBooleanByKey(Constants.PREF_KEY_OPENPACKAGE));

        sbRedPackage = (SeekBar) view.findViewById(R.id.sb_red_package);
        sbRedPackage.setProgress(PrefsUtils.getInstance().getIntByKey(Constants.PREF_KEY_LATETIME));
        tvCurrentTime.setText(sbRedPackage.getProgress()/10.0f + "秒");
        sbRedPackage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvCurrentTime.setText(progress/10.0f + "秒");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getActivity(), "延时抢红包时间将在0～"+seekBar.getProgress()/10.0f+"秒之间随机", Toast.LENGTH_SHORT).show();
                MyApplication.robRedPackageLateTime = seekBar.getProgress();
                PrefsUtils.getInstance().saveIntByKey(Constants.PREF_KEY_LATETIME, MyApplication.robRedPackageLateTime);
            }
        });
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.cb_red_package:
                MyApplication.robRedPackage = isChecked;
                PrefsUtils.getInstance().saveBooleanByKey(Constants.PREF_KEY_REDPACKAGE, isChecked);
                break;

            case R.id.cb_near_add:
                MyApplication.addNearFriend = isChecked;
                PrefsUtils.getInstance().saveBooleanByKey(Constants.PREF_KEY_ADDNEAR, isChecked);
                break;

            case R.id.cb_group_add:
                MyApplication.addGroupFriend = isChecked;
                PrefsUtils.getInstance().saveBooleanByKey(Constants.PREF_KEY_ADDGROUP, isChecked);
                break;

            case R.id.cb_open_float:
                MyApplication.openFloat = isChecked;
                PrefsUtils.getInstance().saveBooleanByKey(Constants.PREF_OPEN_FLOAT, isChecked);
                break;
            case R.id.cb_open_package:
                MyApplication.openPackage = isChecked;
                PrefsUtils.getInstance().saveBooleanByKey(Constants.PREF_KEY_OPENPACKAGE, isChecked);
                if(isChecked){
//                    MyApplication.getInstance().playMononey(0);//todo 这个声音很烦
                }
                break;
        }
    }

    public void updateCheckBox(){
        if(cbRedPackage == null || cbAddNear == null || cbAddGroup==null) return;
        cbRedPackage.setChecked(MyApplication.robRedPackage);
        cbAddNear.setChecked(MyApplication.addNearFriend);
        cbAddGroup.setChecked(MyApplication.addGroupFriend);
    }

    public void onResume() {
        super.onResume();
        updateRedPackageInfo();
        MobclickAgent.onPageStart("SettingFragment"); //统计页面，"MainScreen"为页面名称，可自定义
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SettingFragment");
    }

    public void updateRedPackageInfo(){
        int count = PrefsUtils.getInstance().getIntByKey(Constants.PREF_KEY_COUNT);
        if(count < 0) count = 0;
        float money = PrefsUtils.getInstance().getFloatByKey(Constants.PREF_KEY_MONEY);
        if(money < 0) money = 0;

        tvRedPackageInfoCount.setText(count+"");
        tvRedPackageInfoMoney.setText(money+"");
    }

}
