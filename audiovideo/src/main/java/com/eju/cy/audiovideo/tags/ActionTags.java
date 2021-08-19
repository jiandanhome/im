package com.eju.cy.audiovideo.tags;

public interface ActionTags {
    /*-----------------事件发送tag------------------*/
    int ACTION_SEND_BUSINESS_CARD = 1;//发送名片
    int ACTION_SEND_HOUSING = 2;//发送房源


    int CHAT_GUOUP_RIGHT = 3;//群聊右侧按钮点击   strJson部分  ChatInfo 解析   ChatInfo中 conversationInfoStr用 ConversationInfo解析（--###---需要判断有无值###）
    int CHAT_C2C_RIGHT = 4;//单聊右侧按钮点击   strJson部分  ChatInfo 解析   ChatInfo中 conversationInfoStr用 ConversationInfo解析（--###---需要判断有无值###）

    int C2C_VOICE_CALL = 5;//单聊-语音通话
    int GROUP_VOICE_CALL = 6;//群聊-语音通话
    int CLOSE_VIDEO_ACTIVITY = 7;//C2C--关闭语音通话（非应用端状态）
    int ON_FORCE_OFF_LIN = 8;//账号在其他端登陆
    int ON_NEW_GROUP_MSG = 10;//群聊收到新消息


    int UPDATE_GUOUP_NAME = 11;//修改群聊名字成功
    int QUIT_GROUP_SUCCESS = 12;//退出群聊成功
    int DELETE_GROUP_SUCCESS = 13;//解散圈子成功


    int APP_THE_FRONT_DESK = 14;//应用在前台
    int APP_THE_BACKGROUND = 15;//应用在后台

    int PUST_NEW_GROUP_AV_IM_LIST = 16;//发送新邀请参加群视频的用户--（非应用端状态）
    int GROUP_VIDEO_CALL = 17;//群聊-视频通话  ----  strJson部分 GroupAvNumberDto 解析  size都是0的时候代表第一次邀请

    int CLOSE_CHAT_ACTIVITY = 18;//关闭聊天界面


    int PUST_FRIENDS_UNREAD_COUNT = 19;//获取好友（人脉）未读消息     strJson部分为未读消息
    int PUST_NON_FRIENDS_UNREAD_COUNT = 20;//获取非好友（消息）未读消息  strJson部分为未读消息

    int UPLOAD_C2C_AND_FRIENDS_CONVERSATION = 21;//重新拉取C2C好友会话列表

    int SWITCH_AUDIO_CALL = 22;//C2C--切换到语音通话非应用端状态）


    int SEND_AUTO_MES = 23;//C2C--发送标识自动回复消息）
    /*-----------------事件点击回调tag------------------*/
    int ACTION_CLICK_BUSINESS_CARD = 10001;// 名片点击 strJson部分 用CustomContentDto实体接收解析
    int ACTION_CLICK_HOUSING = 10002;// 房源点击   strJson部分 用CustomContentDto实体接收解析


    int ACTION_CLICK_GROUP_NOTIFICATION = 10003;// 群通知点击
    int ACTION_CLICK_GROUP_TO_DO = 10004;// 群代办点击  根据list  size  区分,Str部分用 List<GroupApplyInfo> 接收解析，UNHANDLED显示同意，APPLIED已经同意 ，已经拒绝REFUSED

    int ACTION_CLICK_GROUP_LOAD_APPLY = 10005;// 群代办点击
    int ACTION_CLICK_USER_AVATAR_APPLY = 10006;// 用户头像点击回调  strJson部分为当前用户IM ID
    int ACTION_CLICK_GROUP_OPERATION_MSG = 10007;// 圈子信息被操作点击事件（如创建圈子 进圈  修改圈信息等）  strJson部分为当前用户IM ID
    int ACTION_CLICK_FILE_MESSAGE_PATH = 10008;//文件消息点击   strJson部分为 被点击文件的本地地址

    int ACTION_CLICK_IMAGE_MESSAGE_PATH = 10009;//图片消息点击    strJson部分为 被点击图片的本地地址
    int ACTION_CLICK_C2C_VOICEL = 10010;//单聊 群聊-语音通话
    int ACTION_CLICK_TEXT_MESSAGE_VALUE = 10011;//单聊 群聊-文本消息点击(只有消息是网址地址的时候会触发)   strJson部分为 被点击文本内容
    int ACTION_CLICK_ARTICLE_MESSAGE_VALUE = 10012;//单聊 群聊-分享文章（文章消息的点击事件）   strJson部分 传的callback内容

    int ACTION_CLICK_RVIEW_CARD = 100013;// 小区评测报告 strJson部分  为 被点击评测小区报告地址
    int ACTION_CLICK_DELETE_CARD = 100014;// 小区评测报告 strJson部分  为 被点击评测小区报告地址
    int ACTION_CLICK_IMAGE_LIST_MESSAGE_PATH = 100015;//图片消息点击     strJson部分  图片地址List,其中第一条为点击的

}
