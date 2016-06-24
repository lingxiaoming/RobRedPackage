package com.zyy.rob.robredpackage.redpackage;
/**
 * User: xiaoming
 * Date: 2016-05-29
 * Time: 13:37
 * 抢红包流程,当抢红包开关打开就会进入这里
 */

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Rect;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.zyy.rob.robredpackage.MyApplication;
import com.zyy.rob.robredpackage.RobService;
import com.zyy.rob.robredpackage.base.Constants;
import com.zyy.rob.robredpackage.tools.LogUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by apple on 16/5/29.
 */
public class RedPackageCtrl {

    private static final String TAG = "RedPackageCtrl";
    private static final String WECHAT_RED_PACKAGE = "[微信红包]";
    private boolean isAutoClickToRedPackageDetail = false;
    private boolean isAutoClickToRedPackageDialog = false;
    private boolean isAutoBackToChatActivity = false;


    public void dispathRedpackage(final RobService robService, AccessibilityEvent event){
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = event.getText();
                if (!texts.isEmpty()) {
                    for (CharSequence text : texts) {
                        String content = text.toString();
                        LogUtils.i(TAG, "notification text:" + content);
                        if (content.contains(WECHAT_RED_PACKAGE)) {
                            wakeUpAndUnlock(robService);
                            //模拟打开通知栏消息
                            if (event.getParcelableData() != null
                                    && event.getParcelableData() instanceof Notification) {
                                Notification notification = (Notification) event.getParcelableData();
                                PendingIntent pendingIntent = notification.contentIntent;
                                try {
                                    pendingIntent.send();
                                } catch (PendingIntent.CanceledException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:

                if(TextUtils.equals(event.getClassName().toString(), Constants.ACTIVITY_MAIN)){//主页面与聊天页面同属一个activity
                    if(isAutoBackToChatActivity){
                        isAutoBackToChatActivity = false;
                        return;
                    }
                    if(isChatActivity(robService, event)){//如果是聊天页面才能点击红包
                        if(findLatestRedPackageAndClickIt(robService, event.getSource())){//找到最后一个红包Item并点击它
                            isAutoClickToRedPackageDetail = true;
                            isAutoClickToRedPackageDialog = true;
                        }else {
                            isAutoClickToRedPackageDetail = false;
                            isAutoClickToRedPackageDialog = false;
                        }
                    }
                }else if(TextUtils.equals(MyApplication.topClassname, Constants.ACTIVITY_DIALOG_REDPACKAGE)){
                    final AccessibilityNodeInfo accessibilityNodeInfo = robService.findNodeInfoByClassName(event.getSource(), "android.widget.Button");

                    TimerTask task = new TimerTask(){

                        public void run(){

                            if(robService.performClick(accessibilityNodeInfo)){//TODO 这里可以做延时拆包
                                isAutoClickToRedPackageDetail = true;
                            }else {
                                isAutoClickToRedPackageDetail = false;
                                if(isAutoClickToRedPackageDialog){
                                    isAutoBackToChatActivity = true;
                                    robService.performBack(robService);//红包没来得及拆开
                                }
                            }

                        }

                    };

                    Timer timer = new Timer();

                    timer.schedule(task, 0);//这里做延时，做0-x秒内随机抢



                }else if(TextUtils.equals(MyApplication.topClassname, Constants.ACTIVITY_REDPACKAGE_SUCCESS)){

                    if(isAutoClickToRedPackageDetail){//是软件自动点进来抢红包的，就退出这个页面
                        if(robService.performBack(robService)){//退出需要知道是不是点击返回退出
                            isAutoBackToChatActivity = true;
                            isAutoClickToRedPackageDetail = false;
                        }
                    }
                }

                break;

            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED://窗口内容改变
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED://列表内容滚动，用这个来判断正在聊天

                if(event.getItemCount() != event.getToIndex()+1) return;//不是最后一个item，什么都不管

                if(TextUtils.equals(MyApplication.topClassname, Constants.ACTIVITY_MAIN)) {
                    if (isChatActivity(robService, event)) {
                        LogUtils.d(TAG, "TYPE_VIEW_SCROLLED"+event.isScrollable()+" "+event.getItemCount()+" "+event.getCurrentItemIndex()+" "+event.getFromIndex()+" "+event.getToIndex()+" --- "+event.getAddedCount()+" "+event.getMovementGranularity()+" "+event.getRecordCount());

                        AccessibilityNodeInfo nodeInfo = event.getSource();
                        if(TextUtils.equals(nodeInfo.getClassName(), "android.widget.ListView") && nodeInfo.getChildCount() > 0){
                                    if(findLatestRedPackageAndClickIt(robService, nodeInfo)){
                                        isAutoClickToRedPackageDetail = true;
                                        isAutoClickToRedPackageDialog = true;
                                    }else{
                                        isAutoClickToRedPackageDetail = false;
                                        isAutoClickToRedPackageDialog = false;
                                    }
                        }
                    }
                }
                break;
        }
    }

    /**
     * 采取的办法是验证listview，然后判断listview下面是不是聊天操作栏
     * @param robService
     * @param event
     * @return 是否是微信聊天页面
     */
    private boolean isChatActivity(RobService robService, AccessibilityEvent event){
        if(robService == null || event == null) return false;

        AccessibilityNodeInfo rootNodeInfo = robService.getRootInActiveWindow();//得到rootView
        if(rootNodeInfo == null) return false;

        AccessibilityNodeInfo backImageView = robService.findNodeInfoByContentDescribeAndClassName(rootNodeInfo, "返回", "android.widget.ImageView");

        if(backImageView != null && TextUtils.equals(MyApplication.topClassname, Constants.ACTIVITY_MAIN)){
            return true;
        }

        return false;
    }

    private boolean findLatestRedPackageAndClickIt(RobService robService, AccessibilityNodeInfo listviewNodeInfo){
        if(robService == null || listviewNodeInfo == null) return false;

        AccessibilityNodeInfo listNodeInfo = robService.findLatestNodeInfoByClassName(listviewNodeInfo, "android.widget.ListView");
        if(listNodeInfo == null) return false;

        int[] buttomArray = new int[2];
        AccessibilityNodeInfo lastRedpackageTextView = robService.findNodeInfoByTextLast(listNodeInfo, "领取红包");
        if(lastRedpackageTextView == null) return false;

        Rect rect = new Rect();
        lastRedpackageTextView.getBoundsInScreen(rect);
        buttomArray[0] = rect.bottom;

        AccessibilityNodeInfo lastHadBeenOpenTextNodeInfo = robService.findLatestNodeInfoByClassNameAndPartOfTextStartAndEnd(listNodeInfo, "你领取了", "的红包", "android.widget.TextView");
        if(lastHadBeenOpenTextNodeInfo == null){
            robService.performClick(lastRedpackageTextView);
            return true;
        }

        Rect rect2 = new Rect();
        lastHadBeenOpenTextNodeInfo.getBoundsInScreen(rect2);
        buttomArray[1] = rect2.bottom;

        if(buttomArray[0] > buttomArray[1]){//红包在"你领取了xxx的红包"之后
            return robService.performClick(lastRedpackageTextView);
        }
        return false;
    }

    public void wakeUpAndUnlock(Context context){
        KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();
    }

}
