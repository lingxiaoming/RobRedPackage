package com.zyy.rob.robredpackage.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.umeng.analytics.MobclickAgent;
import com.zyy.rob.robredpackage.R;
import com.zyy.rob.robredpackage.base.Constants;

import java.util.ArrayList;
import java.util.List;
/**
 * User: xiaoming
 * Date: 2016-05-23
 * Time: 20:33
 * 描述一下这个类吧
 */

/**
 * Created by apple on 16/5/23.
 */
public class MainActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private List<Fragment> fragmentList;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        UmengUpdateAgent.update(this);
        UmengUpdateAgent.setUpdateCheckConfig(false);

        fragmentList = new ArrayList<>();
        fragmentList.add(HelpFragment.newInstance());
        fragmentList.add(MainFragment.newInstance());
        fragmentList.add(SettingFragment.newInstance());


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);

        initBroadCast();
    }

    public void showGG(){
//        IQhFloatbannerAd floatBanner = Qhad.showFloatbannerAd(this,"uPFlGuayEN",false, Qhad.FLOAT_BANNER_SIZE.SIZE_DEFAULT,Qhad.FLOAT_LOCATION.TOP);
    }

    private void initBroadCast() {
        IntentFilter intentFilter = new IntentFilter(Constants.ACTION_FLOAT_CLOSE);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ((SettingFragment) fragmentList.get(2)).updateCheckBox();
            }
        };
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public void scrollToPage(int position){
        mViewPager.setCurrentItem(position, true);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        showGG();

        if(mViewPager.getCurrentItem() != 1){
            scrollToPage(1);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
