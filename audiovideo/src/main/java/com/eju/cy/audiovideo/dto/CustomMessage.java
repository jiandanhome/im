package com.eju.cy.audiovideo.dto;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.eju.cy.audiovideo.modules.message.MessageInfoUtil;
import com.google.gson.Gson;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMMessage;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义消息的bean实体，用来与json的相互转化
 */
public class CustomMessage implements Serializable {

    private static final String TAG = CustomMessage.class.getSimpleName();


    public static final int VIDEO_CALL_ACTION_UNKNOWN = -1;
    /**
     * 正在呼叫
     */
    public static final int VIDEO_CALL_ACTION_DIALING = 0;
    /**
     * 发起人取消
     */
    public static final int VIDEO_CALL_ACTION_SPONSOR_CANCEL = 1;
    /**
     * 拒接电话
     */
    public static final int VIDEO_CALL_ACTION_REJECT = 2;
    /**
     * 无人接听
     */
    public static final int VIDEO_CALL_ACTION_SPONSOR_TIMEOUT = 3;
    /**
     * 连接进入通话
     */
    public static final int VIDEO_CALL_ACTION_ACCEPTED = 4;
    /**
     * 挂断
     */
    public static final int VIDEO_CALL_ACTION_HANGUP = 5;
    /**
     * 电话占线
     */
    public static final int VIDEO_CALL_ACTION_LINE_BUSY = 6;


    /**
     * 切换到语音
     */
    public static final int SWITCH_AUDIO = 7;


    public static final int JSON_VERSION_1_HELLOTIM = 1;
    public static final int JSON_VERSION_2_ONLY_IOS_TRTC = 2;
    public static final int JSON_VERSION_3_ANDROID_IOS_TRTC = 3;

    /**
     * 通话类型
     */
    public static final int CALL_TYPE_UNKNOWN = 0;
    public static final int CALL_TYPE_AUDIO = 1;
    public static final int CALL_TYPE_VIDEO = 2;


    /*自定义消息-名片*/
    public static final int CUSTOM_BUSINESS_CARD = 10;
    /*自定义消息-房源*/
    public static final int CUSTOM_HOUSING = 11;


    /*自定义消息-@圈选人*/
    public static final int CUSTOM_AIT = 12;


    /*自定义消息-分享的文章*/
    public static final int CUSTOM_ARTICLE = 13;


    /*自定义消息-分享的文章*/
    public static final int CUSTOM_CARD_REVIEW = 14;


    /*自定义消息-分享的文章*/
    public static final int CUSTOM_CARD_SEND_REVIEW = 15;

    /*自定义消息-分享的文章*/
    public static final int CUSTOM_CARD_REVIEW_READ = 16;


    // 一个欢迎提示富文本
    public static final int HELLO_TXT = 1;


    private String partner = "";

    String text = "简单家";
    String link = "https://cloud.tencent.com/document/product/269/3794";

    /**
     * 1: 仅仅是一个带链接的文本消息
     * 2: iOS支持的视频通话版本，后续已经不兼容
     * 3: Android/iOS/Web互通的视频通话版本
     */
    int version = 1;
    /**
     * 表示一次通话的唯一ID
     */
    String call_id = "";
    int room_id = 0;
    int action = VIDEO_CALL_ACTION_UNKNOWN;
    int duration = 0;
    /**
     * 群组时需要添加邀请人，接受者判断自己是否在邀请队列来决定是否加入通话
     */
    String[] invited_list;


    /**
     * 群组时需要添加邀请人，接受者判断自己是否在邀请队列来决定是否加入通话
     */
    List<GroupAvCallDto> invited_user_list;


    /*
     * 所有自定义消息Bean
     *
     * */

    String content;

    /**
     * 回调给客户端
     */
    String callback;


    //呼叫方用户信息
    String userId;
    String userPortrait;
    String userName;

    //被呼叫方用户信息
    String othersUserId;
    String othersUserPortrait;
    String othersUserName;

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }

    public List<String> getInvitedList() {
        return invitedList;
    }

    public void setInvitedList(List<String> invitedList) {
        this.invitedList = invitedList;
    }

    //是否语音通话 true
    boolean isAudioCall = true;


    /**
     * 通话类型
     * 0-未知
     * 1-语音通话
     * 2-视频通话
     */
    int callType;

    /**
     * 邀请的列表
     */
    List<String> invitedList;

    @Override
    public String toString() {
        return "CustomMessage{" +
                "partner='" + partner + '\'' +
                ", text='" + text + '\'' +
                ", link='" + link + '\'' +
                ", version=" + version +
                ", call_id='" + call_id + '\'' +
                ", room_id=" + room_id +
                ", action=" + action +
                ", duration=" + duration +
                ", invited_list=" + Arrays.toString(invited_list) +
                ", invited_user_list=" + invited_user_list +
                ", content='" + content + '\'' +
                ", callback='" + callback + '\'' +
                ", userId='" + userId + '\'' +
                ", userPortrait='" + userPortrait + '\'' +
                ", userName='" + userName + '\'' +
                ", othersUserId='" + othersUserId + '\'' +
                ", othersUserPortrait='" + othersUserPortrait + '\'' +
                ", othersUserName='" + othersUserName + '\'' +
                ", isAudioCall=" + isAudioCall +
                ", callType=" + callType +
                ", invitedList=" + invitedList +
                '}';
    }

    public List<GroupAvCallDto> getInvited_user_list() {
        return invited_user_list;
    }

    public void setInvited_user_list(List<GroupAvCallDto> invited_user_list) {
        this.invited_user_list = invited_user_list;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOthersUserId() {
        return othersUserId;
    }

    public void setOthersUserId(String othersUserId) {
        this.othersUserId = othersUserId;
    }

    public String getOthersUserPortrait() {
        return othersUserPortrait;
    }

    public void setOthersUserPortrait(String othersUserPortrait) {
        this.othersUserPortrait = othersUserPortrait;
    }

    public String getOthersUserName() {
        return othersUserName;
    }

    public void setOthersUserName(String othersUserName) {
        this.othersUserName = othersUserName;
    }

    public boolean isAudioCall() {
        return isAudioCall;
    }

    public void setAudioCall(boolean audioCall) {
        isAudioCall = audioCall;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCall_id() {
        return call_id;
    }

    public void setCall_id(String call_id) {
        this.call_id = call_id;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String[] getInvited_list() {
        return invited_list;
    }

    public void setInvited_list(String[] invited_list) {
        this.invited_list = invited_list;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }


    public static CustomMessage convert2VideoCallData(List<TIMMessage> msgs) {
        if (null == msgs || msgs.size() == 0) {
            return null;
        }
        for (TIMMessage msg : msgs) {
            TIMConversation conversation = msg.getConversation();
            TIMConversationType type = conversation.getType();
            if (type != TIMConversationType.C2C) {

                continue;
            }
            List<MessageInfo> list = MessageInfoUtil.TIMMessage2MessageInfo(msg, false);
            for (MessageInfo info : list) {
                if (info.getMsgType() != MessageInfo.MSG_TYPE_CUSTOM) {

                    continue;
                }
                // 获取到自定义消息的json数据
                if (!(info.getElement() instanceof TIMCustomElem)) {

                    continue;
                }
                TIMCustomElem elem = (TIMCustomElem) info.getElement();
                // 自定义的json数据，需要解析成bean实例
                CustomMessage data = null;
                try {
                    data = new Gson().fromJson(new String(elem.getData()), CustomMessage.class);
                } catch (Exception e) {

                    LogUtils.e(TAG, "invalid json: " + new String(elem.getData()) + " " + e.getMessage());
                }
                if (data == null) {

                    LogUtils.e(TAG, "No Custom Data: " + new String(elem.getData()));
                    continue;
                } else if (data.version != JSON_VERSION_3_ANDROID_IOS_TRTC) {

                    continue;
                }
                data.setPartner(info.getFromUser());
                return data;
            }
        }
        return null;
    }
}


