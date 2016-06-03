package com.zyy.rob.robredpackage.ui;
/**
 * User: xiaoming
 * Date: 2016-05-25
 * Time: 00:48
 * 描述一下这个类吧
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zyy.rob.robredpackage.R;

/**
 * Created by apple on 16/5/25.
 */
public class HelpFragment extends Fragment implements View.OnClickListener {

    private View ll1, ll2, ll3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ll1 = view.findViewById(R.id.ll_setting_1);
        ll2 = view.findViewById(R.id.ll_setting_2);
        ll3 = view.findViewById(R.id.ll_setting_3);

        view.findViewById(R.id.tv_setting_1).setOnClickListener(this);
        view.findViewById(R.id.tv_setting_2).setOnClickListener(this);
        view.findViewById(R.id.tv_setting_3).setOnClickListener(this);
        view.findViewById(R.id.tv_feedback).setOnClickListener(this);
        view.findViewById(R.id.tv_qqqun).setOnClickListener(this);
    }

    public static HelpFragment newInstance() {
        HelpFragment fragment = new HelpFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_setting_1:
                if(ll1.getVisibility() == View.GONE){
                    ll1.setVisibility(View.VISIBLE);
                }else {
                    ll1.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_setting_2:
                if(ll2.getVisibility() == View.GONE){
                    ll2.setVisibility(View.VISIBLE);
                }else {
                    ll2.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_setting_3:
                if(ll3.getVisibility() == View.GONE){
                    ll3.setVisibility(View.VISIBLE);
                }else {
                    ll3.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_feedback:
                break;

            case R.id.tv_qqqun:
                ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
                cmb.setText("301674698");
                Toast.makeText(getActivity(), "qq群号复制成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("HelpFragment"); //统计页面，"MainScreen"为页面名称，可自定义
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("HelpFragment");
    }
}
