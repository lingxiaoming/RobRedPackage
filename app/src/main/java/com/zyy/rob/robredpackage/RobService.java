package com.zyy.rob.robredpackage;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.zyy.rob.robredpackage.addnearbypeople.AddNearbyPeopleCtrl;
import com.zyy.rob.robredpackage.base.BaseAccessibilityService;
import com.zyy.rob.robredpackage.base.Constants;
import com.zyy.rob.robredpackage.base.EventLineStates;
import com.zyy.rob.robredpackage.redpackage.QQRedPackageCtrl;
import com.zyy.rob.robredpackage.redpackage.RedPackageCtrl;
import com.zyy.rob.robredpackage.tools.LogUtils;
import com.zyy.rob.robredpackage.tools.PrefsUtils;
import com.zyy.rob.robredpackage.ui.MainActivity;

/**
 * 作者：lxm on 16/2/5.
 * 描述：<一定要写>
 */
public class RobService extends BaseAccessibilityService {
    private final String TAG = "RobService";
    private EventLineStates eventLineStates;
    private RedPackageCtrl redPackageCtrl;//红包功能分支
    private QQRedPackageCtrl redQQPackageCtrl;//QQ红包功能分支
    private AddNearbyPeopleCtrl addNearbyPeopleCtrl;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        boolean isRegister = MyApplication.getInstance().hasRegiste();//是否激活

        LogUtils.e(TAG, "onAccessibilityEvent " + String.format("%02X", event.getEventType()));
        if(!isRegister){
            int countFree = PrefsUtils.getInstance().getIntByKey(PrefsUtils.KEY_COUNT_FREE);
            if(countFree<0 || countFree>=2){
                Toast.makeText(RobService.this, "免费试用次数用完了啦，激活才能继续为您抢红包哦~", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            MyApplication.topClassname = event.getClassName().toString();
            LogUtils.d(TAG, "TopClass:"+MyApplication.topClassname);
        }

        if (MyApplication.addNearFriend) {//添加附近的人
            if(isRegister){
                addNearbyPeopleCtrl.dispathAddNearbyPeople(this, event);
            }else {
                Toast.makeText(this, "试用期间不能使用添加好友功能哦", Toast.LENGTH_SHORT).show();
            }
        }

        if (MyApplication.addGroupFriend) {//添加群组里的人
            if(isRegister) {
                dispathAddGroupFriendEvent(event);
            }else {
                Toast.makeText(this, "试用期间不能使用添加好友功能哦", Toast.LENGTH_SHORT).show();
            }
        }

        if(MyApplication.robRedPackage && TextUtils.equals(getPackageName(), Constants.PACKAGE_WEIXIN)){
            redPackageCtrl.dispathRedpackage(this, event);
        }

        if(MyApplication.robredpackageQQ && TextUtils.equals(event.getPackageName().toString(), Constants.PACKAGE_QQ)){
            redQQPackageCtrl.dispathRedpackage(this, event);
        }

        if (true) {//测试
//            dispathTest(event);
        }

    }

    @Override
    public void onInterrupt() {
        //服务中断，如授权关闭或者将服务杀死
        Toast.makeText(this, "快点不小心被关闭了，请重新启动哦～", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        //接收按键事件
        return super.onKeyEvent(event);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        //连接服务后,一般是在授权成功后会接收到
        eventLineStates = new EventLineStates();
        redPackageCtrl = new RedPackageCtrl();
        redQQPackageCtrl = new QQRedPackageCtrl();
        addNearbyPeopleCtrl = new AddNearbyPeopleCtrl();

        if(MyApplication.openFloat)
            startService(new Intent(this, FloatService.class));
        else stopService(new Intent(this, FloatService.class));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initNofitication();
        notificationManager.notify(NOTIFICATION_ID, notification);
        startForeground(NOTIFICATION_ID, notification);

        return START_STICKY;
    }

    public final static int NOTIFICATION_ID = "MainActivity".hashCode();
    private NotificationManager notificationManager;
    private Notification notification;

    public void initNofitication(){
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // params
        int smallIconId = R.drawable.ic_launcher_small;
        Bitmap largeIcon = ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap();
        String info = "快点抢红包打开啦";

        // action when clicked
        Intent intent = new Intent(this, MainActivity.class);

        builder.setLargeIcon(largeIcon)
                .setSmallIcon(smallIconId)
                .setContentTitle("快点")
                .setContentText(info)
                .setTicker(info)
                .setAutoCancel(false)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0));

        notification = builder.getNotification();
    }


    public void dispathAddGroupFriendEvent(AccessibilityEvent event) {
        String className = event.getClassName().toString();
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            MyApplication.topClassname = className;
            if (className.equals(Constants.ACTIVITY_MAIN)) {

            } else if (TextUtils.equals(className, Constants.ACTIVITY_GROUPMENBERLIST)) {//组成员列表grid

                if(eventLineStates.isBack_groupgrid){
                    eventLineStates.isBack_groupgrid = false;
                    return;
                }
                AccessibilityNodeInfo accessibilityNodeInfo = event.getSource();
                if (accessibilityNodeInfo == null) {
                    return;
                }
                AccessibilityNodeInfo nodeInfo = findNodeInfoByClassNameAndPartOfText(accessibilityNodeInfo, "全部群成员", "android.widget.TextView");
                if(nodeInfo != null){
                    boolean success = performClick(nodeInfo);
                }else {
                    accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                }
            }else if(TextUtils.equals(className, Constants.ACTIVITY_GROUPMENBERLIST2)){//组成员列表list

                AccessibilityNodeInfo accessibilityNodeInfo = findNodeInfoByClassName(event.getSource(), "android.widget.ListView");

                if (accessibilityNodeInfo == null) {
                    return;
                }

                if (!eventLineStates.isBack_grouplist) {
                    eventLineStates.currentDoing = 0;
                } else {
                    if (eventLineStates.currentDoing >= eventLineStates.maxListCount) {
                        eventLineStates.isBack_grouplist = false;
                        accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);

                        return;
                    }
                }

                int childCount = accessibilityNodeInfo.getChildCount();
                eventLineStates.maxListCount = childCount;
                accessibilityNodeInfo.getChild(eventLineStates.currentDoing).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }else if(TextUtils.equals(className, Constants.ACTIVITY_NEARBYDETAIL)){//一个人详细资料
                if (eventLineStates.isBack_nearbydetail) {//需要再次返回到列表页面，然后选择下一个
                    eventLineStates.isBack_grouplist = performBack(this);
                    eventLineStates.isBack_nearbydetail = false;
                    return;
                }
                AccessibilityNodeInfo accessibilityNodeInfo = findNodeInfoByTextAndClassName(event.getSource(), "添加到通讯录", "android.widget.Button");
                if (accessibilityNodeInfo == null) {//可能这时候已经有附近的人是自己的好友了,这时候就不是打招呼了,是发消息
                    eventLineStates.isBack_grouplist = performBack(this);
                    eventLineStates.isBack_nearbydetail = false;
                    eventLineStates.currentDoing += 1;
                    return;
                }

                if (!performClick(accessibilityNodeInfo)) {
                    performBack(this);
                }
            }else if(TextUtils.equals(className, Constants.ACTIVITY_GROUPMENBERADD)){//组成员发送添加请求
                AccessibilityNodeInfo accessibilityNodeInfo = findNodeInfoByTextAndClassName(event.getSource(), "发送", "android.widget.TextView");
                performClick(accessibilityNodeInfo);
//                    Toast.makeText(this, "点击加为朋友成功", Toast.LENGTH_LONG).show();

                AccessibilityNodeInfo backImageView = findNodeInfoByContentDescribeAndClassName(event.getSource(), "返回", "android.widget.ImageView");
                performClick(backImageView);
                eventLineStates.isBack_nearbydetail = true;
                eventLineStates.currentDoing += 1;
            }
        } else if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            if (className.equals("android.widget.ListView")) {
                if (TextUtils.equals(MyApplication.topClassname, Constants.ACTIVITY_GROUPMENBERLIST2)) {

                    AccessibilityNodeInfo accessibilityNodeInfo = event.getSource();
                    if (accessibilityNodeInfo == null) {
                        return;
                    }
                    int childCount = accessibilityNodeInfo.getChildCount();
                    eventLineStates.currentDoing = 1;
                    eventLineStates.maxListCount = childCount;
                    if (childCount > 1) {
                        accessibilityNodeInfo.getChild(eventLineStates.currentDoing).performAction(AccessibilityNodeInfo.ACTION_CLICK);

//                        Toast.makeText(this, "添加下一页附近的人", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "添加组成员完毕", Toast.LENGTH_LONG).show();
//                        performBack(this);//加完就主页面吧
                    }
                } else if (TextUtils.equals(MyApplication.topClassname, Constants.ACTIVITY_GROUPMENBERLIST)) {//组成员列表grid
                    if(eventLineStates.isBack_groupgrid){
                        eventLineStates.isBack_groupgrid = false;
                        return;
                    }
                    AccessibilityNodeInfo accessibilityNodeInfo = event.getSource();
                    if (accessibilityNodeInfo == null) {
                        return;
                    }
                    AccessibilityNodeInfo nodeInfo = findNodeInfoByClassNameAndPartOfText(accessibilityNodeInfo, "全部群成员", "android.widget.TextView");
                    if(nodeInfo != null){
                        boolean success = performClick(nodeInfo);
                    }else {
                        accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    }
                }
            }
        }
    }

    private void dispathTest(AccessibilityEvent event) {
        AccessibilityNodeInfo nodeInfo = event.getSource();
        String className = event.getClassName().toString();
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {//测试主页面的滚动
            if (className.equals(Constants.ACTIVITY_MAIN)) {
                AccessibilityNodeInfo accessibilityNodeInfo = findNodeInfoByClassName(nodeInfo, "android.widget.ListView");
                accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            }
        }
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        Intent intent = new Intent(Constants.ACTION_SERVICE_CLOSE);
        sendBroadcast(intent);
        if(notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_ID);
        }
        super.onDestroy();
    }
}

