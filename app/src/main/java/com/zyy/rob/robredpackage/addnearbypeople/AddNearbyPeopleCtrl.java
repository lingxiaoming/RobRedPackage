package com.zyy.rob.robredpackage.addnearbypeople;
/**
 * User: xiaoming
 * Date: 2016-06-02
 * Time: 00:06
 * 添加附近的人
 */

import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.zyy.rob.robredpackage.MyApplication;
import com.zyy.rob.robredpackage.RobService;
import com.zyy.rob.robredpackage.base.Constants;
import com.zyy.rob.robredpackage.tools.LogUtils;
import com.zyy.rob.robredpackage.tools.MLog;

/**
 * Created by apple on 16/6/2.
 */
public class AddNearbyPeopleCtrl {
    private static final String TAG = "AddNearbyPeopleCtrl";
    private boolean isBackToNearbyList = false;
    private boolean isBackToNearbyDetail = false;
    private int currentDoing = -1, maxListCount = 0;

    public void dispathAddNearbyPeople(RobService robService, AccessibilityEvent event) {
        AccessibilityNodeInfo nodeInfo = event.getSource();
        String className = event.getClassName().toString();

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {//进入微信主页就要重置这些标志位了
            if(TextUtils.equals(Constants.ACTIVITY_MAIN, className)){
                isBackToNearbyList = false;
                isBackToNearbyDetail = false;
                currentDoing = -1;
                maxListCount = 0;
            }else if (TextUtils.equals(Constants.ACTIVITY_NEARBYLIST_FIRST, className)) {//首次进入附近的人，是定位确认页面
                AccessibilityNodeInfo accessibilityNodeInfo = robService.findNodeInfoByText(nodeInfo, "开始查看");
                robService.performClick(accessibilityNodeInfo);

            } else if (className.equals(Constants.ACTIVITY_NEARBYLISTWARN)) {//附近的人列表页面之前的提示确认
                AccessibilityNodeInfo btnSure = robService.findNodeInfoByTextAndClassName(nodeInfo, "确定", "android.widget.Button");
                robService.performClick(btnSure);
            } else if (className.equals(Constants.ACTIVITY_NEARBYLIST)) {//附近的人列表页面
                AccessibilityNodeInfo accessibilityNodeInfo = robService.findNodeInfoByClassName(nodeInfo, "android.widget.ListView");

                if (accessibilityNodeInfo == null) {
                    return;
                }

                if (!isBackToNearbyList) {
                    currentDoing = 1;
                } else {
                    if (currentDoing >= maxListCount) {
                        isBackToNearbyList = false;
                        accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                        return;
                    }
                }

                int childCount = accessibilityNodeInfo.getChildCount();
                maxListCount = childCount;
                accessibilityNodeInfo.getChild(currentDoing).performAction(AccessibilityNodeInfo.ACTION_CLICK);

                MLog.d(TAG, "childCount(" + childCount + ")");
            } else if (className.equals(Constants.ACTIVITY_NEARBYDETAIL)) {//附近一个人详细资料
                if (isBackToNearbyDetail) {//需要再次返回到列表页面，然后选择下一个
                    isBackToNearbyList = robService.performBack(robService);
                    isBackToNearbyDetail = false;
                    return;
                }
                AccessibilityNodeInfo accessibilityNodeInfo = robService.findNodeInfoByTextAndClassName(nodeInfo, "打招呼", "android.widget.Button");
                if (accessibilityNodeInfo == null) {//可能这时候已经有附近的人是自己的好友了,这时候就不是打招呼了,是发消息,所以直接返回
                    isBackToNearbyList = robService.performBack(robService);
                    isBackToNearbyDetail = false;
                    currentDoing += 1;
                    return;
                }

                if (!robService.performClick(accessibilityNodeInfo)) {
                    isBackToNearbyList = robService.performBack(robService);
                    isBackToNearbyDetail = false;
                    currentDoing += 1;
                }
//                    Toast.makeText(this, "点击打招呼成功", Toast.LENGTH_LONG).show();

            } else if (className.equals(Constants.ACTIVITY_NEARBYADDFRIREND)) {//与附近的一个人聊天页面或加为好友
                AccessibilityNodeInfo accessibilityNodeInfo = robService.findNodeInfoByTextAndClassName(nodeInfo, "加为朋友", "android.widget.Button");
                robService.performClick(accessibilityNodeInfo);
//                    Toast.makeText(this, "点击加为朋友成功", Toast.LENGTH_LONG).show();
                LogUtils.e(TAG, "add "+currentDoing);

                AccessibilityNodeInfo backImageView = robService.findNodeInfoByContentDescribeAndClassName(nodeInfo, "返回", "android.widget.ImageView");
                robService.performClick(backImageView);
                isBackToNearbyDetail = true;
                currentDoing += 1;
            }
        } else if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {//附近的人列表滚动
            if (TextUtils.equals(MyApplication.topClassname, Constants.ACTIVITY_NEARBYLIST)) {
                if (className.equals("android.widget.ListView")) {
                    AccessibilityNodeInfo accessibilityNodeInfo = nodeInfo;
                    if (accessibilityNodeInfo == null) {
                        return;
                    }
                    int childCount = accessibilityNodeInfo.getChildCount();
                    currentDoing = 0;
                    maxListCount = childCount;
                    if (childCount >= 1) {
                        accessibilityNodeInfo.getChild(currentDoing).performAction(AccessibilityNodeInfo.ACTION_CLICK);

//                        Toast.makeText(this, "添加下一页附近的人", Toast.LENGTH_LONG).show();
                    } else {
//                        performBack(this);//加完就主页面吧
                    }
                }
            }
        }
    }
}
