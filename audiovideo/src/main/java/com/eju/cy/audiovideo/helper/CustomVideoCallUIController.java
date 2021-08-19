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
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.activity.chat.ChatActivity;
import com.eju.cy.audiovideo.activity.trtc.C2CVideoActivity;
import com.eju.cy.audiovideo.component.AudioPlayer;
import com.eju.cy.audiovideo.controller.ImCallBack;
import com.eju.cy.audiovideo.controller.UserInfoController;
import com.eju.cy.audiovideo.dialog.ImTrtcDialog;
import com.eju.cy.audiovideo.dto.CustomMessage;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.dto.RoomDto;
import com.eju.cy.audiovideo.modules.chat.ChatLayout;
import com.eju.cy.audiovideo.modules.chat.base.ChatInfo;
import com.eju.cy.audiovideo.modules.chat.layout.message.holder.ICustomMessageViewGroup;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.eju.cy.audiovideo.modules.message.MessageInfoUtil;
import com.eju.cy.audiovideo.net.AppNetInterface;
import com.eju.cy.audiovideo.net.RetrofitManager;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.tags.AppConfig;
import com.eju.cy.audiovideo.tags.Constants;
import com.eju.cy.audiovideo.trtcs.manager.AudioPlayerManager;
import com.eju.cy.audiovideo.utils.DateTimeUtil;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.eju.cy.audiovideo.utils.ParameterUtils;
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

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.eju.cy.audiovideo.dto.CustomMessage.JSON_VERSION_3_ANDROID_IOS_TRTC;
import static com.eju.cy.audiovideo.dto.CustomMessage.SWITCH_AUDIO;
import static com.eju.cy.audiovideo.dto.CustomMessage.VIDEO_CALL_ACTION_ACCEPTED;
import static com.eju.cy.audiovideo.dto.CustomMessage.VIDEO_CALL_ACTION_DIALING;
import static com.eju.cy.audiovideo.dto.CustomMessage.VIDEO_CALL_ACTION_HANGUP;
import static com.eju.cy.audiovideo.dto.CustomMessage.VIDEO_CALL_ACTION_LINE_BUSY;
import static com.eju.cy.audiovideo.dto.CustomMessage.VIDEO_CALL_ACTION_REJECT;
import static com.eju.cy.audiovideo.dto.CustomMessage.VIDEO_CALL_ACTION_SPONSOR_CANCEL;
import static com.eju.cy.audiovideo.dto.CustomMessage.VIDEO_CALL_ACTION_SPONSOR_TIMEOUT;


/**
 * @ Name: Caochen
 * @ Date: 2020-09-04
 * @ Time: 14:58
 * @ Description： C2C视频
 */
public class CustomVideoCallUIController extends TRTCCloudListener {


    private Context activityContext;
    WeakReference<Activity> mWeakReference;
    public Application application;
    private ImTrtcDialog mTrtcDialog;

    private static final String TAG = CustomVideoCallUIController.class.getSimpleName();

    private static final int VIDEO_CALL_STATUS_FREE = 1; //空闲
    private static final int VIDEO_CALL_STATUS_BUSY = 2; //忙
    private static final int VIDEO_CALL_STATUS_WAITING = 3;//等待
    private int mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;

    private static CustomVideoCallUIController mController;

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
            AudioPlayerManager.getInstance().stopMyPlay();
            closeVideoActivity();
            LogUtils.w("超时111111111");
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


        AudioPlayerManager.getInstance().stopMyPlay();


    }


    public void init(Application application) {

        this.application = application;
        mTRTCCloud = TRTCCloud.sharedInstance(this.application);
        TRTCListener.getInstance().addTRTCCloudListener(this);
        mTRTCCloud.setListener(TRTCListener.getInstance());

    }


    public static CustomVideoCallUIController getInstance() {

        if (mController == null) {
            mController = new CustomVideoCallUIController();
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
//
//                mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
//                sendVideoCallAction(VIDEO_CALL_ACTION_REJECT, mOnlineCall);
//                Toast.makeText(mUISender.getContext(), "发起通话失败，没有弹出对话框权限", Toast.LENGTH_SHORT).show();
//            }


            othersEnterAudioRoom();

        }
    }

    public void onDraw(ICustomMessageViewGroup parent, CustomMessage data) {
        // 把自定义消息view添加到TUIKit内部的父容器里
        View view = LayoutInflater.from(this.application).inflate(R.layout.test_custom_message_av_layout1, null, false);
        parent.addMessageContentView(view);

        if (data == null) {
            LogUtils.i(TAG, "onCalling null data");
            return;
        }
        TextView textView = view.findViewById(R.id.test_custom_message_tv);


       // LogUtils.w("视频数据是------"+data.toString());
        String callingAction = "";
        switch (data.getAction()) {
            // 新接一个电话
            case VIDEO_CALL_ACTION_DIALING:
                callingAction = "[请求通话]";
                break;
            case VIDEO_CALL_ACTION_SPONSOR_CANCEL:
                callingAction = "[取消通话]";
                break;
            case VIDEO_CALL_ACTION_REJECT:
                if (data.getUserId().equals(AppConfig.appImId)) {
                    callingAction = "[已拒绝]";

                } else {
                    callingAction = "[对方已拒绝]";

                }
                //callingAction = "[拒绝通话]";
                break;
            case VIDEO_CALL_ACTION_SPONSOR_TIMEOUT:
                if (data.getUserId().equals(AppConfig.appImId)) {
                    callingAction = "[对方未接通]";

                } else {
                    callingAction = "[对方已取消]";
                }
                //   callingAction = "[无应答]";
                break;
            case VIDEO_CALL_ACTION_ACCEPTED:
                callingAction = "[开始通话]";
                break;
            case VIDEO_CALL_ACTION_HANGUP:

                callingAction = "[通话时长：" + DateTimeUtil.formatSeconds(data.getDuration()) + "]";
                break;
            case VIDEO_CALL_ACTION_LINE_BUSY:
                callingAction = "[对方已占线]";
                break;
            case SWITCH_AUDIO:
                callingAction = "[切换至语音通话]";
                break;
            default:
                LogUtils.e(TAG, "unknown data.action: " + data.getAction());
                callingAction = "[不能识别的通话指令]";
                break;
        }
        textView.setText(callingAction);
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

        Gson gson = new Gson();
        CustomMessage message = new CustomMessage();
        message.setVersion(JSON_VERSION_3_ANDROID_IOS_TRTC);
        message.setCall_id(roomInfo.getCall_id());
        message.setRoom_id(roomInfo.getRoom_id());
        message.setAction(action);
        message.setInvited_list(roomInfo.getInvited_list());


        message.setAudioCall(roomInfo.isAudioCall());

        //非拨打 调换下逻辑
        if (action == VIDEO_CALL_ACTION_DIALING || action == VIDEO_CALL_ACTION_SPONSOR_TIMEOUT || action == VIDEO_CALL_ACTION_SPONSOR_CANCEL || action == VIDEO_CALL_ACTION_HANGUP || action == VIDEO_CALL_ACTION_REJECT|| action == SWITCH_AUDIO) {
            message.setUserId(roomInfo.getUserId());
            message.setUserPortrait(roomInfo.getUserPortrait());
            message.setUserName(roomInfo.getUserName());


            message.setOthersUserId(roomInfo.getOthersUserId());
            message.setOthersUserPortrait(roomInfo.getOthersUserPortrait());
            message.setOthersUserName(roomInfo.getOthersUserName());

        } else {
            message.setUserId(roomInfo.getOthersUserId());
            message.setUserPortrait(roomInfo.getOthersUserPortrait());
            message.setUserName(roomInfo.getOthersUserName());


            message.setOthersUserId(roomInfo.getUserId());
            message.setOthersUserPortrait(roomInfo.getUserPortrait());
            message.setOthersUserName(roomInfo.getUserName());

        }


        if (action == VIDEO_CALL_ACTION_HANGUP) {
            int time = (int) (System.currentTimeMillis() - mEnterRoomTime + 500) / 1000;
            message.setDuration(time);
        }

        if (action == VIDEO_CALL_ACTION_REJECT) {
            mHandler.removeCallbacksAndMessages(null);
        }


        String data = gson.toJson(message);
      //  LogUtils.w("发送的数据是---" + data);
        MessageInfo info = MessageInfoUtil.buildCustomMessage(data);
        if (TextUtils.equals(mOnlineCall.getPartner(), roomInfo.getPartner())) {
            mUISender.sendMessage(info, false);
        } else {
            TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.C2C, roomInfo.getPartner());
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
        LogUtils.i(TAG, "sendVideoCallAction action: " + message.toString());
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


            mOnlineCall.setUserId(roomInfo.getOthersUserId());

            mOnlineCall.setUserPortrait(roomInfo.getOthersUserPortrait());
            mOnlineCall.setUserName(roomInfo.getOthersUserName());

            mOnlineCall.setOthersUserId(roomInfo.getUserId());
            mOnlineCall.setOthersUserPortrait(roomInfo.getUserPortrait());
            mOnlineCall.setOthersUserName(roomInfo.getUserName());
            mOnlineCall.setAudioCall(roomInfo.isAudioCall());
            //  mOnlineCall.setAction(0);
            mOnlineCall.setVersion(3);

            //  mOnlineCall = roomInfo;
        }
    }

    //组装语音电话
    private void assembleOnlineCall(CustomMessage roomInfo, String userId,
                                    String userPortrait,
                                    String userName,

                                    String othersUserId,
                                    String othersUserPortrait,
                                    String othersUserName,
                                    int roomId
            , boolean isAudioCall) {
        mOnlineCall = new CustomMessage();
        if (roomInfo == null) {
            mOnlineCall.setCall_id(createCallID());
            mOnlineCall.setRoom_id(roomId);
            //对方用户ID
            mOnlineCall.setInvited_list(new String[]{othersUserId});

            //对方用户ID
            mOnlineCall.setPartner(othersUserId);

            //
            mOnlineCall.setUserId(userId);

            mOnlineCall.setUserPortrait(userPortrait);
            mOnlineCall.setUserName(userName);

            mOnlineCall.setOthersUserId(othersUserId);
            mOnlineCall.setOthersUserPortrait(othersUserPortrait);
            mOnlineCall.setOthersUserName(othersUserName);
            mOnlineCall.setAudioCall(isAudioCall);
            mOnlineCall.setAction(0);
            mOnlineCall.setVersion(3);
            LogUtils.w(TAG, "组装语音电话无数据" + mOnlineCall.toString());
        } else {

            mOnlineCall.setCall_id(roomInfo.getCall_id());
            mOnlineCall.setRoom_id(roomInfo.getRoom_id());
            mOnlineCall.setInvited_list(roomInfo.getInvited_list());
            mOnlineCall.setPartner(roomInfo.getPartner());


            mOnlineCall.setUserId(roomInfo.getUserId());
            mOnlineCall.setUserPortrait(roomInfo.getUserPortrait());
            mOnlineCall.setUserName(roomInfo.getUserName());
            mOnlineCall.setAction(roomInfo.getAction());
            mOnlineCall.setVersion(roomInfo.getVersion());
            mOnlineCall.setOthersUserId(roomInfo.getOthersUserId());
            mOnlineCall.setOthersUserPortrait(roomInfo.getOthersUserPortrait());
            mOnlineCall.setOthersUserName(roomInfo.getOthersUserName());
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

    private void onNewComingCall(CustomMessage message) {
        LogUtils.i(TAG, "接收的数据是---" + message.toString());
        switch (message.getAction()) {
            case VIDEO_CALL_ACTION_DIALING:
                if (mCurrentVideoCallStatus == VIDEO_CALL_STATUS_FREE) {
                    mCurrentVideoCallStatus = VIDEO_CALL_STATUS_WAITING;
                    startC2CConversation(message);
                    assembleOnlineCall(message);


                    AudioPlayerManager.getInstance().startPlay(activityContext, R.raw.av_call, new AudioPlayer.Callback() {
                        @Override
                        public void onCompletion(Boolean success) {

                        }
                    }, true);


                } else {
                    sendVideoCallAction(VIDEO_CALL_ACTION_LINE_BUSY, message);
                }
                break;
            case VIDEO_CALL_ACTION_SPONSOR_CANCEL:
                mHandler.removeCallbacksAndMessages(null);
                if (mCurrentVideoCallStatus != VIDEO_CALL_STATUS_FREE) {
                    //  dismissDialog();
                    closeVideoActivity();
                    mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
                    AudioPlayerManager.getInstance().stopMyPlay();

                }
                break;
            case VIDEO_CALL_ACTION_REJECT:


                if (mCurrentVideoCallStatus != VIDEO_CALL_STATUS_FREE) {
                    AudioPlayerManager.getInstance().stopMyPlay();
                    dismissDialog();
                    mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
                    closeVideoActivity();
                }
                break;
            case VIDEO_CALL_ACTION_SPONSOR_TIMEOUT:

                if (mCurrentVideoCallStatus != VIDEO_CALL_STATUS_FREE) {
                    AudioPlayerManager.getInstance().stopMyPlay();
                    closeVideoActivity();
                    dismissDialog();
                    mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
                }
                break;
            case VIDEO_CALL_ACTION_ACCEPTED:
                if (mCurrentVideoCallStatus != VIDEO_CALL_STATUS_FREE) {
                    dismissDialog();
                }
                assembleOnlineCall(message);
                // enterRoom();
                break;
            case VIDEO_CALL_ACTION_HANGUP:
                AudioPlayerManager.getInstance().stopMyPlay();

                closeVideoActivity();
                dismissDialog();
                mTRTCCloud.exitRoom();
                mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
                break;
            case VIDEO_CALL_ACTION_LINE_BUSY:
                AudioPlayerManager.getInstance().stopMyPlay();
                if (mCurrentVideoCallStatus == VIDEO_CALL_STATUS_BUSY) {
                    mHandler.removeCallbacksAndMessages(null);

                    mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
                    ToastUtils.showLong("对方正在忙线中");
                    closeVideoActivity();

                }
                break;

            case SWITCH_AUDIO:
                ImActionDto imActionDto = new ImActionDto();
                imActionDto.setAction(ActionTags.SWITCH_AUDIO_CALL);
                String closedStr = JsonUtils.toJson(imActionDto);
                EjuHomeImEventCar.getDefault().post(closedStr);
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
        LogUtils.w("showIncomingDialingDialog------");
        dismissDialog();
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(mVideoCallIncomingTimeOut, VIDEO_CALL_OUT_INCOMING_TIME_OUT);


        LogUtils.w(" mOnlineCall.partner" + mOnlineCall.getOthersUserPortrait() + "\n---" + mOnlineCall.getOthersUserName());
        mTrtcDialog = new ImTrtcDialog(activityContext, mOnlineCall.getOthersUserPortrait(), mOnlineCall.getOthersUserName());

        mTrtcDialog.setPositiveButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i(TAG, "VIDEO_CALL_ACTION_ACCEPTED");
                mHandler.removeCallbacksAndMessages(null);
                sendVideoCallAction(VIDEO_CALL_ACTION_ACCEPTED, mOnlineCall);
                mCurrentVideoCallStatus = VIDEO_CALL_STATUS_BUSY;
                AudioPlayerManager.getInstance().stopMyPlay();
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
                AudioPlayerManager.getInstance().stopMyPlay();


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

    public void chaoShi() {
        AudioPlayerManager.getInstance().stopMyPlay();
        closeVideoActivity();
        LogUtils.i(TAG, "time out, dismiss incoming dialog");
        mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
        sendVideoCallAction(VIDEO_CALL_ACTION_SPONSOR_TIMEOUT, mOnlineCall);
        dismissDialog();

    }


    public void staTime() {
        mEnterRoomTime = System.currentTimeMillis();

    }

    public void quxiao() {
        AudioPlayerManager.getInstance().stopMyPlay();
        mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
        sendVideoCallAction(VIDEO_CALL_ACTION_SPONSOR_CANCEL, mOnlineCall);

    }

    public void jujue() {
        mCurrentVideoCallStatus = VIDEO_CALL_STATUS_FREE;
        sendVideoCallAction(VIDEO_CALL_ACTION_REJECT, mOnlineCall);
        AudioPlayerManager.getInstance().stopMyPlay();
    }


    public void jieTing() {
        sendVideoCallAction(VIDEO_CALL_ACTION_ACCEPTED, mOnlineCall);
        mCurrentVideoCallStatus = VIDEO_CALL_STATUS_BUSY;
        AudioPlayerManager.getInstance().stopMyPlay();
    }

    //切换到语音
    public void switchAudio() {
        sendVideoCallAction(SWITCH_AUDIO, mOnlineCall);
        ImActionDto imActionDto = new ImActionDto();
        imActionDto.setAction(ActionTags.SWITCH_AUDIO_CALL);
        String closedStr = JsonUtils.toJson(imActionDto);
        EjuHomeImEventCar.getDefault().post(closedStr);
    }


    //呼叫的用户进房
    private void enterAudioRoom() {
        AudioPlayerManager.getInstance().startPlay(activityContext, R.raw.av_call, new AudioPlayer.Callback() {
            @Override
            public void onCompletion(Boolean success) {

            }
        }, true);


        mEnterRoomTime = System.currentTimeMillis();
        Intent intent = new Intent(activityContext, C2CVideoActivity.class);
        intent.putExtra(Constants.ROOM_ID, mOnlineCall.getRoom_id());

        intent.putExtra(Constants.USER_ID, mOnlineCall.getUserId());
        intent.putExtra(Constants.USER_NAME, mOnlineCall.getOthersUserName());
        intent.putExtra(Constants.USER_PORTRAIT, mOnlineCall.getOthersUserPortrait());
        activityContext.startActivity(intent);
    }

    //被呼叫用户进房
    private void othersEnterAudioRoom() {
//        AudioPlayerManager.getInstance().startPlay(activityContext, R.raw.av_call, new AudioPlayer.Callback() {
//            @Override
//            public void onCompletion(Boolean success) {
//
//            }
//        }, true);

        LogUtils.w("othersEnterAudioRoom---" + mOnlineCall.toString());
        final Intent intent = new Intent(activityContext, C2CVideoActivity.class);
        intent.putExtra(Constants.ROOM_ID, mOnlineCall.getRoom_id());

        intent.putExtra(Constants.USER_ID, mOnlineCall.getUserId());
        intent.putExtra(Constants.USER_NAME, mOnlineCall.getOthersUserName());
        intent.putExtra(Constants.USER_PORTRAIT, mOnlineCall.getOthersUserPortrait());
        intent.putExtra(Constants.IS_PASSIVE_CALL, true);

        activityContext.startActivity(intent);
    }


    public long getEnterRoomTime() {
        return mEnterRoomTime;
    }
    /*---------------------------------------------------服务器相关--------------------------------------------------------------*/

    /**
     * 创建2人视频通话
     */
    public void createC2cVideoCall() {
        //获取对方用户信息
        UserInfoController.getInstance().getUsersProfile(mUISender.getChatInfo().getId(), true, new ImCallBack() {
            @Override
            public void onError(int var1, String var2) {

            }

            @Override
            public void onSuccess(Object var1) {

                if (null != var1) {

                    List<TIMUserProfile> timUserProfiles = (List<TIMUserProfile>) var1;
                    //被呼叫着信息
                    String calledName = timUserProfiles.get(0).getNickName();
                    String calledUserID = timUserProfiles.get(0).getIdentifier();
                    String calledFaceUrl = timUserProfiles.get(0).getFaceUrl();


                    createRoom(calledName, calledUserID, calledFaceUrl);


                }

            }


        });


    }

    /**
     * 创建音频房间
     *
     * @param calledName
     * @param calledUserID
     * @param calledFaceUrl
     */
    int i = 0;

    private void createRoom(final String calledName, final String calledUserID, final String calledFaceUrl) {
        final AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(application);
        //被呼叫方ID
        String calledId = calledUserID.substring(2);

        httpInterface.getRoom(ParameterUtils.prepareFormData(AppConfig.appChannel),
                ParameterUtils.prepareFormData(AppConfig.appUserId),
                ParameterUtils.prepareFormData(calledId),
                ParameterUtils.prepareFormData("1")//安卓
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RoomDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final RoomDto roomDto) {

                        if (null != roomDto && "10000".equals(roomDto.getCode())) {
                            mCurrentVideoCallStatus = VIDEO_CALL_STATUS_BUSY;
                            //进房


                            String userId = TIMManager.getInstance().getLoginUser();
                            if (null != userId && !"".equals(userId))
                                UserInfoController.getInstance().getUsersProfile(userId, true, new ImCallBack() {
                                    @Override
                                    public void onError(int var1, String var2) {
                                        ToastUtils.showLong("创建语音通话失败");
                                    }

                                    @Override
                                    public void onSuccess(Object var1) {
                                        i++;

                                        List<TIMUserProfile> timUserProfiles = (List<TIMUserProfile>) var1;

                                        assembleOnlineCall(null, AppConfig.appImId, timUserProfiles.get(0).getFaceUrl(), timUserProfiles.get(0).getNickName(), calledUserID, calledFaceUrl, calledName, roomDto.getData().getId(), false);

                                        // if (null!=mOnlineCall&&mOnlineCall.getRoom_id()!=roomDto.getData().getId()){
                                        sendVideoCallAction(VIDEO_CALL_ACTION_DIALING, mOnlineCall);
                                        //删除所有回调消息
                                        mHandler.removeCallbacksAndMessages(null);
                                        enterAudioRoom();
                                        //  }
                                    }
                                });

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        ToastUtils.showLong("创建语音通话失败，请重试！");

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }
}