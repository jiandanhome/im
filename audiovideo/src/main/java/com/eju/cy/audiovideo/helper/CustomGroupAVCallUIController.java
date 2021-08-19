package com.eju.cy.audiovideo.helper;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.activity.chat.ChatActivity;
import com.eju.cy.audiovideo.activity.trtc.C2CAudioCallActivity;
import com.eju.cy.audiovideo.activity.trtc.GroupVideoCallActivity;
import com.eju.cy.audiovideo.controller.ImCallBack;
import com.eju.cy.audiovideo.controller.UserInfoController;
import com.eju.cy.audiovideo.dialog.ImTrtcDialog;
import com.eju.cy.audiovideo.dto.CustomMessage;
import com.eju.cy.audiovideo.dto.GroupAvCallDto;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.modules.chat.ChatLayout;
import com.eju.cy.audiovideo.modules.chat.base.ChatInfo;
import com.eju.cy.audiovideo.modules.chat.layout.message.holder.ICustomMessageViewGroup;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.eju.cy.audiovideo.modules.message.MessageInfoUtil;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.tags.AppConfig;
import com.eju.cy.audiovideo.tags.Constants;
import com.eju.cy.audiovideo.trtcs.model.TRTCVideoCallImpl;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.eju.cy.audiovideo.utils.TUIKitLog;
import com.google.gson.Gson;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudListener;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;

import static com.eju.cy.audiovideo.dto.CustomMessage.JSON_VERSION_3_ANDROID_IOS_TRTC;
import static com.eju.cy.audiovideo.dto.CustomMessage.VIDEO_CALL_ACTION_ACCEPTED;
import static com.eju.cy.audiovideo.dto.CustomMessage.VIDEO_CALL_ACTION_DIALING;
import static com.eju.cy.audiovideo.dto.CustomMessage.VIDEO_CALL_ACTION_HANGUP;
import static com.eju.cy.audiovideo.dto.CustomMessage.VIDEO_CALL_ACTION_LINE_BUSY;
import static com.eju.cy.audiovideo.dto.CustomMessage.VIDEO_CALL_ACTION_REJECT;
import static com.eju.cy.audiovideo.dto.CustomMessage.VIDEO_CALL_ACTION_SPONSOR_CANCEL;
import static com.eju.cy.audiovideo.dto.CustomMessage.VIDEO_CALL_ACTION_SPONSOR_TIMEOUT;


public class CustomGroupAVCallUIController extends TRTCCloudListener {


    private Context activityContext;
    WeakReference<Activity> mWeakReference;
    public Application application;
    private ImTrtcDialog mTrtcDialog;

    private static final String TAG = CustomGroupAVCallUIController.class.getSimpleName();

    private static final int VIDEO_CALL_STATUS_FREE = 1; //空闲
    private static final int VIDEO_CALL_STATUS_BUSY = 2; //忙
    private static final int VIDEO_CALL_STATUS_WAITING = 3;//等待
    private int mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;

    private static CustomGroupAVCallUIController mController;

    private long mEnterRoomTime;
    private CustomMessage mOnlineCall;
    private ChatLayout mUISender;
    private TRTCDialog mDialog;
    private TRTCCloud mTRTCCloud;

    private static final int VIDEO_CALL_OUT_GOING_TIME_OUT = 15 * 1000;
    private static final int VIDEO_CALL_OUT_INCOMING_TIME_OUT = 15 * 1000;
    private Handler mHandler = new Handler();
    private Runnable mVideoCallOutgoingTimeOut = new Runnable() {
        @Override
        public void run() {

            closeVideoActivity();
            LogUtils.i(TAG, "time out, dismiss outgoing dialog");
            mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
            sendVideoCallAction(VIDEO_CALL_ACTION_SPONSOR_CANCEL, mOnlineCall);
            dismissDialog();
        }
    };

    private Runnable mVideoCallIncomingTimeOut = new Runnable() {
        @Override
        public void run() {

            closeVideoActivity();
            LogUtils.i(TAG, "time out, dismiss incoming dialog");
            mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
            sendVideoCallAction(VIDEO_CALL_ACTION_SPONSOR_TIMEOUT, mOnlineCall);
            dismissDialog();


        }
    };


    /**
     * 关闭语音通话界面
     */
    private void closeVideoActivity() {

        ImActionDto imActionDto = new ImActionDto();
        imActionDto.setAction(ActionTags.CLOSE_VIDEO_ACTIVITY);
        String closedStr = JsonUtils.toJson(imActionDto);
        EjuHomeImEventCar.getDefault().post(closedStr);


    }


    public void init(Application application) {

        this.application = application;
        mTRTCCloud = TRTCCloud.sharedInstance(this.application);
        TRTCListener.getInstance().addTRTCCloudListener(this);
        mTRTCCloud.setListener(TRTCListener.getInstance());

    }


    public static CustomGroupAVCallUIController getInstance() {

        if (mController == null) {
            mController = new CustomGroupAVCallUIController();
        }
        return mController;
    }

    public void setActivityContext(Activity context) {

        this.mWeakReference = new WeakReference<Activity>(context);
        this.activityContext = this.mWeakReference.get();

    }


    public void onCreate() {

        mTRTCCloud = TRTCCloud.sharedInstance(this.application);
        mTRTCCloud.setListener(this);
    }

    @Override
    public void onError(int errCode, String errMsg, Bundle extraInfo) {
        LogUtils.i(TAG, "trtc onError");
        mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
        sendVideoCallAction(VIDEO_CALL_ACTION_HANGUP, mOnlineCall);
        Toast.makeText(mUISender.getContext(), "通话异常: " + errMsg + "[" + errCode + "]", Toast.LENGTH_LONG).show();
        if (mTRTCCloud != null) {
            mTRTCCloud.exitRoom();
        }
    }

    @Override
    public void onEnterRoom(long elapsed) {
        LogUtils.i(TAG, "onEnterRoom " + elapsed);
        Toast.makeText(mUISender.getContext(), "开始通话", Toast.LENGTH_SHORT).show();
        mEnterRoomTime = System.currentTimeMillis();
    }

    @Override
    public void onExitRoom(int reason) {
        LogUtils.i(TAG, "onExitRoom " + reason);
        Toast.makeText(mUISender.getContext(), "结束通话", Toast.LENGTH_SHORT).show();
        mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
    }

    public void setUISender(ChatLayout layout) {
        LogUtils.i(TAG, "setUISender: " + layout);
        mUISender = layout;

        if (mCurrentVideoCallStatus == VIDEO_CALL_STATUS_WAITING) {
//            boolean success = showIncomingDialingDialog();
//            if (success) {
//                mCurrentVideoCallStatus = VIDEO_CALL_STATUS_BUSY;
//            } else {
//                mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
//                sendVideoCallAction(VIDEO_CALL_ACTION_REJECT, mOnlineCall);
//                Toast.makeText(mUISender.getContext(), "发起通话失败，没有弹出对话框权限", Toast.LENGTH_SHORT).show();
//            }
        }
    }

    public void onDraw(ICustomMessageViewGroup parent, TRTCVideoCallImpl.CallModel data) {
        // 把自定义消息view添加到TUIKit内部的父容器里
        View view = LayoutInflater.from(this.application).inflate(R.layout.test_custom_message_av_layout1, null, false);
        parent.addMessageContentView(view);

        if (data == null) {
            LogUtils.i(TAG, "onCalling null data");
            return;
        }
        TextView textView = view.findViewById(R.id.test_custom_message_tv);
        LinearLayout ll_view = view.findViewById(R.id.ll_view);
        String callingAction = "";


        if (data.getAction() == TRTCVideoCallImpl.CallModel.VIDEO_CALL_ACTION_DIALING) {
            callingAction = "[发起通话]";
        } else if (data.getAction() == TRTCVideoCallImpl.CallModel.VIDEO_CALL_ACTION_SPONSOR_CANCEL) {
            callingAction = "[取消通话]";
        } else if (data.getAction() == TRTCVideoCallImpl.CallModel.VIDEO_CALL_ACTION_REJECT) {
            callingAction = "[拒绝通话]";
        } else if (data.getAction() == TRTCVideoCallImpl.CallModel.VIDEO_CALL_ACTION_SPONSOR_TIMEOUT) {
            //callingAction = "[无应答]";
            //callingAction = "[语音通话已结束]";
        } else if (data.getAction() == TRTCVideoCallImpl.CallModel.VIDEO_CALL_ACTION_HANGUP) {
//            callingAction = "[结束通话，通话时长121：" + DateTimeUtil.formatSeconds(data.getDuration()) + "]";
//            callingAction = "[结束通话]";
            callingAction = "[结束通话]";
        } else if (data.getAction() == TRTCVideoCallImpl.CallModel.VIDEO_CALL_ACTION_LINE_BUSY) {
            callingAction = "[正在通话中]";
        }


       // LogUtils.w("callingAction----" + callingAction);
        textView.setText(callingAction);
        //判断是否在被邀请名单里面从而是否显示
//        for (GroupAvCallDto groupAvCallDto : data.getInvited_user_list()) {
//
//
//            if (groupAvCallDto.getImId().equals(AppConfig.appImId)) {
//                LogUtils.w("imid-----"+groupAvCallDto.getImId()+"\n-----appImId"+AppConfig.appImId);
//                ll_view.setVisibility(View.VISIBLE);
//                textView.setText(callingAction);
//
//            } else {
//                ll_view.setVisibility(View.GONE);
//            }
//        }
    }

    public void createVideoCallRequest() {
        // 显示通话UI
        boolean success = showOutgoingDialingDialog();
        if (success) {
            mCurrentVideoCallStatus = VIDEO_CALL_STATUS_BUSY;
            assembleOnlineCall(null);
            sendVideoCallAction(VIDEO_CALL_ACTION_DIALING, mOnlineCall);
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(mVideoCallOutgoingTimeOut, VIDEO_CALL_OUT_GOING_TIME_OUT);
        } else {
            Toast.makeText(mUISender.getContext(), "发起通话失败，没有弹出对话框权限", Toast.LENGTH_SHORT).show();
        }
    }

    public void hangup() {
        LogUtils.i(TAG, "hangup");
        mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
        sendVideoCallAction(VIDEO_CALL_ACTION_HANGUP, mOnlineCall);
    }

    private void enterRoom() {
        final Intent intent = new Intent(mUISender.getContext(), TRTCActivity.class);
        intent.putExtra(TRTCActivity.KEY_ROOM_ID, mOnlineCall.getRoom_id());
        mUISender.getContext().startActivity(intent);
    }

    public void sendVideoCallAction(int action, CustomMessage roomInfo) {
        LogUtils.i(TAG, "sendVideoCallAction action: " + roomInfo.toString());
        Gson gson = new Gson();
        CustomMessage message = new CustomMessage();
        message.setVersion(JSON_VERSION_3_ANDROID_IOS_TRTC);
        message.setCall_id(roomInfo.getCall_id());
        message.setRoom_id(roomInfo.getRoom_id());
        message.setAction(action);
        message.setInvited_list(roomInfo.getInvited_list());
        message.setInvited_user_list(roomInfo.getInvited_user_list());

        message.setAudioCall(roomInfo.isAudioCall());
        message.setUserId(roomInfo.getUserId());
        //message.setUserPortrait(roomInfo.getUserPortrait());
        message.setUserName(roomInfo.getUserName());


        if (action == VIDEO_CALL_ACTION_HANGUP) {
            int time = (int) (System.currentTimeMillis() - mEnterRoomTime + 500) / 1000;
            message.setDuration(time);
        }

        if (action == VIDEO_CALL_ACTION_REJECT) {
            mHandler.removeCallbacksAndMessages(null);
        }

        LogUtils.w("messagemessagemessage----" + message.toString());

        String data = JsonUtils.toJson(message);
        MessageInfo info = MessageInfoUtil.buildCustomMessage(data);


        if (TextUtils.equals(mOnlineCall.getPartner(), roomInfo.getPartner())) {
            mUISender.sendMessage(info, false);

        } else {
            TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.Group, roomInfo.getPartner());
            con.sendMessage(info.getTIMMessage(), new TIMValueCallBack<TIMMessage>() {

                @Override
                public void onError(int code, String desc) {
                    LogUtils.i(TAG, "sendMessage fail:" + code + "=" + desc);
                }

                @Override
                public void onSuccess(TIMMessage timMessage) {
                    TUIKitLog.i(TAG, "sendMessage onSuccess");
                }
            });
        }
    }

    private String createCallID() {
        final String CHARS = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append(AppConfig.sdkAppId).append("-").append(TIMManager.getInstance().getLoginUser()).append("-");
        for (int i = 0; i < 32; i++) {
            int index = random.nextInt(CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        return sb.toString();
    }


    //组装语音通话
    private void assembleOnlineCall(CustomMessage roomInfo) {
        mOnlineCall = new CustomMessage();
        if (roomInfo == null) {
            mOnlineCall.setCall_id(createCallID());
            mOnlineCall.setRoom_id(new Random().nextInt());
            mOnlineCall.setInvited_list(new String[]{mUISender.getChatInfo().getId()});
            mOnlineCall.setPartner(mUISender.getChatInfo().getId());
        } else {
            mOnlineCall.setCall_id(roomInfo.getCall_id());
            mOnlineCall.setRoom_id(roomInfo.getRoom_id());
            mOnlineCall.setInvited_list(roomInfo.getInvited_list());
            mOnlineCall.setPartner(roomInfo.getPartner());
            mOnlineCall.setVersion(roomInfo.getVersion());
            mOnlineCall.setInvited_user_list(roomInfo.getInvited_user_list());
            mOnlineCall.setVersion(roomInfo.getVersion());
            //  mOnlineCall = roomInfo;
        }
    }

    /**
     * @param roomInfo     通话信息
     * @param userId       用户ID
     * @param userPortrait 用户脑壳
     * @param userName     用户名字
     * @param invited_list 被呼叫方List
     * @param roomId       房间号
     * @param isAudioCall  是否音频
     */
    private void assembleOnlineCall(CustomMessage roomInfo, String userId,
                                    String userPortrait,
                                    String userName,

                                    List<GroupAvCallDto> invited_list,
                                    int roomId
            , boolean isAudioCall) {
        mOnlineCall = new CustomMessage();
        if (roomInfo == null) {
            mOnlineCall.setCall_id(createCallID());
            mOnlineCall.setRoom_id(roomId);
            //被呼叫方方用户列表
            mOnlineCall.setInvited_user_list(invited_list);

            mOnlineCall.setUserId(userId);
            mOnlineCall.setUserPortrait(userPortrait);
            mOnlineCall.setUserName(userName);

            mOnlineCall.setPartner(mUISender.getChatInfo().getId());
            mOnlineCall.setAudioCall(isAudioCall);
            mOnlineCall.setAction(0);
            mOnlineCall.setVersion(3);
            LogUtils.w(TAG, "组装语音电话无数据" + mOnlineCall.toString());
        } else {

            mOnlineCall.setCall_id(roomInfo.getCall_id());
            mOnlineCall.setRoom_id(roomInfo.getRoom_id());
            mOnlineCall.setInvited_user_list(roomInfo.getInvited_user_list());
            mOnlineCall.setPartner(roomInfo.getPartner());


            mOnlineCall.setUserId(roomInfo.getUserId());
            mOnlineCall.setUserPortrait(roomInfo.getUserPortrait());
            mOnlineCall.setUserName(roomInfo.getUserName());
            mOnlineCall.setAction(roomInfo.getAction());
            mOnlineCall.setVersion(roomInfo.getVersion());

            mOnlineCall.setAudioCall(roomInfo.isAudioCall());
            LogUtils.w(TAG, "组装语音电话有数据" + mOnlineCall.toString());
        }
    }


    public void onNewMessage(List<TIMMessage> msgs) {
        CustomMessage data = CustomMessage.convert2VideoCallData(msgs);
        if (data != null) {
            onNewComingCall(data);
        }
    }

    //收到新消息
    private void onNewComingCall(CustomMessage message) {
        LogUtils.i(TAG, "onNewComingCall current state: " + message.toString());


        switch (message.getAction()) {
            case VIDEO_CALL_ACTION_DIALING:
                if (mCurrentVideoCallStatus == VIDEO_CALL_STATUS_FREE) {
                    mCurrentVideoCallStatus = VIDEO_CALL_STATUS_WAITING;
                    startC2CConversation(message);
                    assembleOnlineCall(message);

                    ToastUtils.showLong("弹出视频接听界面");

                } else {
                    sendVideoCallAction(VIDEO_CALL_ACTION_LINE_BUSY, message);
                }
                break;
            case VIDEO_CALL_ACTION_SPONSOR_CANCEL:
                mHandler.removeCallbacksAndMessages(null);
                if (mCurrentVideoCallStatus != VIDEO_CALL_STATUS_FREE && TextUtils.equals(message.getCall_id(), mOnlineCall.getCall_id())) {
                    dismissDialog();
                    mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
                }
                break;
            case VIDEO_CALL_ACTION_REJECT:


                if (mCurrentVideoCallStatus != VIDEO_CALL_STATUS_FREE && TextUtils.equals(message.getCall_id(), mOnlineCall.getCall_id())) {
                    dismissDialog();
                    mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
                    closeVideoActivity();
                }
                break;
            case VIDEO_CALL_ACTION_SPONSOR_TIMEOUT:

                if (mCurrentVideoCallStatus != VIDEO_CALL_STATUS_FREE && TextUtils.equals(message.getCall_id(), mOnlineCall.getCall_id())) {
                    closeVideoActivity();
                    dismissDialog();
                    mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
                }
                break;
            case VIDEO_CALL_ACTION_ACCEPTED:
                if (mCurrentVideoCallStatus != VIDEO_CALL_STATUS_FREE && TextUtils.equals(message.getCall_id(), mOnlineCall.getCall_id())) {
                    dismissDialog();
                }
                assembleOnlineCall(message);
                // enterRoom();
                break;
            case VIDEO_CALL_ACTION_HANGUP:

                closeVideoActivity();
                dismissDialog();
                mTRTCCloud.exitRoom();
                mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
                break;
            case VIDEO_CALL_ACTION_LINE_BUSY:
                if (mCurrentVideoCallStatus == VIDEO_CALL_STATUS_BUSY && TextUtils.equals(message.getCall_id(), mOnlineCall.getCall_id())) {
                    dismissDialog();
                }
                break;
            default:
                LogUtils.e(TAG, "unknown data.action: " + message.getAction());
                break;
        }
    }

    private void startC2CConversation(CustomMessage message) {
        // 小米手机需要在安全中心里面把demo的"后台弹出权限"打开，才能当应用退到后台时弹出通话请求对话框。
        LogUtils.i(TAG, "startC2CConversation " + message.getPartner());
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setType(TIMConversationType.C2C);
        chatInfo.setId(message.getPartner());
        chatInfo.setChatName(message.getPartner());
        Intent intent = new Intent(this.application, ChatActivity.class);
        intent.putExtra(Constants.CHAT_INFO, chatInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.application.startActivity(intent);
    }

    private boolean showIncomingDialingDialog() {

        dismissDialog();
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(mVideoCallIncomingTimeOut, VIDEO_CALL_OUT_INCOMING_TIME_OUT);


        LogUtils.w(" mOnlineCall.partner" + mOnlineCall.getUserPortrait() + "\n---" + mOnlineCall.getUserName());
        mTrtcDialog = new ImTrtcDialog(activityContext, mOnlineCall.getUserPortrait(), mOnlineCall.getUserName());

        mTrtcDialog.setPositiveButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i(TAG, "VIDEO_CALL_ACTION_ACCEPTED");
                mHandler.removeCallbacksAndMessages(null);
                sendVideoCallAction(VIDEO_CALL_ACTION_ACCEPTED, mOnlineCall);
                mCurrentVideoCallStatus = VIDEO_CALL_STATUS_BUSY;
                othersEnterAudioRoom();
            }
        });
        mTrtcDialog.setNegativeButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i(TAG, "VIDEO_CALL_ACTION_REJECT" + new Thread().getName());
                mHandler.removeCallbacksAndMessages(null);
                mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
                sendVideoCallAction(VIDEO_CALL_ACTION_REJECT, mOnlineCall);


                //EjuHomeImEventCar.getDefault().post("finish");
            }
        });
        return mTrtcDialog.showSystemDialog();


    }

    private boolean showOutgoingDialingDialog() {
        dismissDialog();
        mDialog = new TRTCDialog(mUISender.getContext());
        mDialog.setTitle("等待对方接听");
        mDialog.setPositiveButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i(TAG, "VIDEO_CALL_ACTION_SPONSOR_CANCEL");
                mHandler.removeCallbacksAndMessages(null);
                sendVideoCallAction(VIDEO_CALL_ACTION_SPONSOR_CANCEL, mOnlineCall);
            }
        });
        return mDialog.showSystemDialog();
    }

    private void dismissDialog() {
        mHandler.removeCallbacksAndMessages(null);
        if (mDialog != null) {
            mDialog.dismiss();
        }

        if (mTrtcDialog != null) {
            mTrtcDialog.dismiss();
        }

    }




    /*扩展---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public void staTime() {
        mEnterRoomTime = System.currentTimeMillis();

    }

    public void quxiao() {

        mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
        sendVideoCallAction(VIDEO_CALL_ACTION_SPONSOR_CANCEL, mOnlineCall);

    }

    public void jujue() {
        LogUtils.i(TAG, "jujue");
        mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
    }


    //呼叫的用户进房
    private void enterAudioRoom(final List<GroupAvCallDto> imIdList) {


        final Intent intent = new Intent(activityContext, GroupVideoCallActivity.class);

        intent.putExtra(Constants.IMID_LIST, (Serializable) imIdList);
//        intent.putExtra(Constants.ROOM_ID, mOnlineCall.getRoom_id());
//
//        intent.putExtra(Constants.USER_ID, mOnlineCall.getUserId());
//        intent.putExtra(Constants.USER_NAME, mOnlineCall.getOthersUserName());
        // intent.putExtra(Constants.USER_PORTRAIT, mOnlineCall.getOthersUserPortrait());
        activityContext.startActivity(intent);
    }

    //被呼叫用户进房
    private void othersEnterAudioRoom() {

        LogUtils.w("othersEnterAudioRoom---" + mOnlineCall.toString());
        final Intent intent = new Intent(activityContext, C2CAudioCallActivity.class);
        intent.putExtra(Constants.ROOM_ID, mOnlineCall.getRoom_id());

        intent.putExtra(Constants.USER_ID, mOnlineCall.getOthersUserId());
        intent.putExtra(Constants.USER_NAME, mOnlineCall.getUserName());
        intent.putExtra(Constants.USER_PORTRAIT, mOnlineCall.getUserPortrait());
        activityContext.startActivity(intent);
    }

    /*---------------------------------------------------服务器相关--------------------------------------------------------------*/

    /**
     * 群音频
     *
     * @param imIdList 被呼叫方用户ID信息  等
     */
    public void createGroupCall(final List<GroupAvCallDto> imIdList) {

        //设置自己状态正忙
        mCurrentVideoCallStatus = VIDEO_CALL_STATUS_BUSY;
        //进房--获取当前用户信息
        String userId = TIMManager.getInstance().getLoginUser();
        if (null != userId && !"".equals(userId)) {
            UserInfoController.getInstance().getUsersProfile(userId, true, new ImCallBack() {
                @Override
                public void onError(int var1, String var2) {
                    ToastUtils.showLong("创建语音通话失败");
                }

                @Override
                public void onSuccess(Object var1) {

                    List<TIMUserProfile> timUserProfiles = (List<TIMUserProfile>) var1;
                    assembleOnlineCall(null, AppConfig.appImId, timUserProfiles.get(0).getFaceUrl(), timUserProfiles.get(0).getNickName(), imIdList, new Random().nextInt(), false);
                    sendVideoCallAction(VIDEO_CALL_ACTION_DIALING, mOnlineCall);

                    //删除所有回调消息
                    mHandler.removeCallbacksAndMessages(null);
                    enterAudioRoom(imIdList);


                }
            });
        }


    }

}