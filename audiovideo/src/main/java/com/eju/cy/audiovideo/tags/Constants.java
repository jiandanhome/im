package com.eju.cy.audiovideo.tags;


public class Constants {

    // 存储
    public static final String USERINFO = "userInfo";
    public static final String ACCOUNT = "account";
    public static final String PWD = "password";
    public static final String ROOM = "room";
    public static final String AUTO_LOGIN = "auto_login";
    public static final String LOGOUT = "logout";
    public static final String ICON_URL = "icon_url";

    public static final String CHAT_INFO = "chatInfo";


    public static final String NOTIFICATION_TAG = "notificationTag";


    //被呼叫方ID
    public static final String LISTENING_USER_ID = "listening_user_id";

    //房间ID
    public static final String ROOM_ID = "room_id";
    //用户ID
    public static final String USER_ID = "user_id";
    //用户名字
    public static final String USER_NAME = "user_name";

    //用户头像
    public static final String USER_PORTRAIT = "user_portrait";
    //群组ID
    public static final String GROUP_ID = "group_id";

    public static final String IS_PASSIVE_CALL = "is_passive_call";//是否被呼叫

    //被邀请的Im用户ID
    public static final String IMID_LIST = "imid_list";


    //群视频相关
    public static final String OPEN_GROUP_VIDEO_CALL = "open_group_video_call";
    public static final int OPEN_GROUP_VIDEO_CALL_10000 = 10000;//主动--主动发起群视频
    public static final int OPEN_GROUP_VIDEO_CALL_20000 = 20000;//被冻-接收群视频


    public static final String GROUP_AV_IM_LIST = "group_av_im_list";//发送邀请参加群视频的用户
    public static final String GROUP_PERMISSIONS_AV_IM_LIST = "group_permissions_av_im_list";//发送邀请参加群视频有权限开群视频的用户

    public static final String GROUP_AV_SPONSOR = "sponsor";//群视频发起者

}
