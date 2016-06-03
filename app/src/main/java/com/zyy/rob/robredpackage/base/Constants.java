package com.zyy.rob.robredpackage.base;
/**
 * User: xiaoming
 * Date: 2016-05-13
 * Time: 23:09
 * 要查找的NodeInfo所有的常量定义，包括text、class、id等
 */

/**
 * Created by apple on 16/5/13.
 */
public class Constants {
    public static final String TEXT_WEIXINHONGBAO = "[微信红包]";

    public static final String ACTIVITY_MAIN = "com.tencent.mm.ui.LauncherUI";//微信主页面
    public static final String ACTIVITY_NEARBYLISTWARN = "com.tencent.mm.ui.base.h";//附近的人列表页面之前的提示确认
    public static final String ACTIVITY_NEARBYLISTPERPROGRESS = "com.tencent.mm.ui.base.p";//附近的人列表页面之前的加载页面
    public static final String ACTIVITY_NEARBYLIST = "com.tencent.mm.plugin.nearby.ui.NearbyFriendsUI";//附近的人列表页面
    public static final String ACTIVITY_NEARBYLIST_FIRST = "com.tencent.mm.plugin.nearby.ui.NearbyFriendsIntroUI";//首次进入附近的人是“开始查看”页面
    public static final String ACTIVITY_NEARBYDETAIL = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";//附近一个人详细资料
    public static final String ACTIVITY_NEARBYADDFRIREND = "com.tencent.mm.ui.chatting.ChattingUI";//与附近的一个人聊天页面或加为好友

    public static final String ACTIVITY_GROUPMENBERLIST = "com.tencent.mm.plugin.chatroom.ui.ChatroomInfoUI";//群组成员列表页面grid
    public static final String ACTIVITY_GROUPMENBERLIST2 = "com.tencent.mm.plugin.chatroom.ui.SeeRoomMemberUI";//组成员列表list
    public static final String ACTIVITY_GROUPMENBERADD = "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI";//组成员发送添加请求

    public static final String ACTIITY_GROUPCHAT = "";//组聊页面
    public static final String ACTIVITY_DIALOG_REDPACKAGE = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";//红包dialog
    public static final String ACTIVITY_REDPACKAGE_SUCCESS = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";//领取红包成功


    public static final String PREF_KEY_REDPACKAGE = "perf_key_redpackage";//自动抢红包开关key
    public static final String PREF_KEY_ADDNEAR = "perf_key_addnear";//自动添加附近的人开关key
    public static final String PREF_KEY_ADDGROUP = "perf_key_addgroup";//自动添加组里的人开关key
    public static final String PREF_OPEN_FLOAT = "perf_open_float";//悬浮窗的开关


    public static final String ACTION_FLOAT_CLOSE = "broadcast_float_closed";
    public static final String ACTION_SERVICE_CLOSE = "broadcast_service_closed";

}
