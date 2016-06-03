package com.zyy.rob.robredpackage.base;
/**
 * User: xiaoming
 * Date: 2016-05-14
 * Time: 19:01
 * 描述一下这个类吧
 */

/**
 * Created by apple on 16/5/14.
 */
public class EventLineStates {
    public boolean isBack_nearbydetail = false;//附近的人详情页是否是返回出来的
    public boolean isBack_nearbylist = false;//附近的人列表页是否是返回出来的
    public boolean isBack_nearbymain = false;//微信主页面是否是返回出来的
    public int maxListCount;//附近的人列表页最多个数
    public int currentDoing;//当前正在处理的list中的item的position
    public boolean isBack_groupchat = false;//组聊页面是否是从红包详情页面返回过来的

    public boolean isBack_grouplist = false;//组成员list列表
    public boolean isBack_groupgrid = false;//组详情信息列表
    public boolean isAutoClilckToRedPackageDetail = false;//是否是助手自动点进去的
}
