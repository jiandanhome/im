package com.eju.cy.audiovideo.trtcs.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.controller.GroupController;
import com.eju.cy.audiovideo.tags.AppConfig;
import com.eju.cy.audiovideo.trtcs.manager.ProfileManager;
import com.eju.cy.audiovideo.trtcs.model.ITRTCAudioCall;
import com.eju.cy.audiovideo.trtcs.model.ITRTCVideoCall;
import com.eju.cy.audiovideo.trtcs.model.TRTCAudioCallImpl;
import com.eju.cy.audiovideo.trtcs.model.TRTCAudioCallListener;
import com.eju.cy.audiovideo.trtcs.model.TRTCVideoCallImpl;
import com.eju.cy.audiovideo.trtcs.model.TRTCVideoCallListener;
import com.eju.cy.audiovideo.trtcs.model.UserModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CallService extends Service {
    private ITRTCAudioCall mITRTCAudioCall;
    private TRTCAudioCallListener mTRTCAudioCallListener = new TRTCAudioCallListener() {
        // <editor-fold  desc="音频监听代码">
        @Override
        public void onError(int code, String msg) {
        }

        @Override
        public void onInvited(String sponsor, final List<String> userIdList, boolean isFromGroup, int callType) {
            //1. 收到邀请，先到服务器查询
            ProfileManager.getInstance().getUserInfoByUserId(sponsor, new ProfileManager.GetUserInfoCallback() {
                @Override
                public void onSuccess(final UserModel model) {
                    if (!CollectionUtils.isEmpty(userIdList)) {
                        ProfileManager.getInstance().getUserInfoBatch(userIdList, new ProfileManager.GetUserInfoBatchCallback() {
                            @Override
                            public void onSuccess(List<UserModel> modelList) {
                                //  TRTCAudioCallActivity.startBeingCall(CallService.this, model, modelList);
                            }

                            @Override
                            public void onFailed(int code, String msg) {
                                //   TRTCAudioCallActivity.startBeingCall(CallService.this, model, null);
                            }
                        });
                    } else {
                        //TRTCAudioCallActivity.startBeingCall(CallService.this, model, null);
                    }
                }

                @Override
                public void onFailed(int code, String msg) {

                }
            });
        }

        @Override
        public void onGroupCallInviteeListUpdate(List<String> userIdList) {
        }

        @Override
        public void onUserEnter(String userId) {
        }

        @Override
        public void onUserLeave(String userId) {
        }

        @Override
        public void onReject(String userId) {
        }

        @Override
        public void onNoResp(String userId) {
        }

        @Override
        public void onLineBusy(String userId) {
        }

        @Override
        public void onCallingCancel() {
        }

        @Override
        public void onCallingTimeout() {
        }

        @Override
        public void onCallEnd() {
        }

        @Override
        public void onUserAudioAvailable(String userId, boolean isVideoAvailable) {
        }

        @Override
        public void onUserVoiceVolume(Map<String, Integer> volumeMap) {
        }
        // </editor-fold>
    };


    //视频通话视频通话视频通话视频通话视频通话
    //视频通话视频通话视频通话视频通话视频通话
    //视频通话视频通话视频通话视频通话视频通话
    //视频通话视频通话视频通话视频通话视频通话
    //视频通话视频通话视频通话视频通话视频通话
    private ITRTCVideoCall mITRTCVideoCall;
    private TRTCVideoCallListener mTRTCVideoCallListener = new TRTCVideoCallListener() {
        // <editor-fold  desc="视频监听代码">
        @Override
        public void onError(int code, String msg) {
        }

        @Override
        public void onInvited(String sponsor, final List<String> userIdList, List<String> jurisdictionList, boolean isFromGroup, String groupId, int callType) {
            LogUtils.w("哪个先收到邀请---------" + userIdList.size() + "sponsor---" + sponsor);


            List<String> allUserIdList = new ArrayList<>();
            allUserIdList.add(AppConfig.appImId);
            allUserIdList.add(sponsor);
            allUserIdList.addAll(userIdList);


            HashSet set = new HashSet(allUserIdList);
            allUserIdList.clear();
            allUserIdList.addAll(set);


            //如果要自己在能开视频的列表里面 需要排序一下
            List<String> myJurisdictionList = new ArrayList<>();

            if (null != jurisdictionList) {
                if (jurisdictionList.contains(AppConfig.appImId)) {
                    myJurisdictionList.add(AppConfig.appImId);
                    jurisdictionList.remove(AppConfig.appImId);
                    myJurisdictionList.addAll(jurisdictionList);
                } else {
                    myJurisdictionList.addAll(jurisdictionList);
                }
            }

            GroupController.getInstance().answerGroupVideoCall(sponsor, allUserIdList, myJurisdictionList, groupId);

            for (String user : userIdList) {
                LogUtils.w("被邀请的用户有-------" + user);


            }

            if (null != jurisdictionList)
                for (String user : jurisdictionList) {
                    LogUtils.w("被邀请的用户有权限-----" + user);

                }


        }

        @Override
        public void onGroupCallInviteeListUpdate(List<String> userIdList, List<String> jurisdictionList) {

        }

        @Override
        public void onUserEnter(String userId) {

        }

        @Override
        public void onUserLeave(String userId) {

        }

        @Override
        public void onReject(String userId) {

        }

        @Override
        public void onNoResp(String userId) {

        }

        @Override
        public void onLineBusy(String userId) {

        }

        @Override
        public void onCallingCancel() {

        }

        @Override
        public void onCallingTimeout() {

        }

        @Override
        public void onCallEnd() {

        }

        @Override
        public void onUserVideoAvailable(String userId, boolean isVideoAvailable) {

        }

        @Override
        public void onUserAudioAvailable(String userId, boolean isVideoAvailable) {

        }

        @Override
        public void onUserVoiceVolume(Map<String, Integer> volumeMap) {

        }
        // </editor-fold  desc="视频监听代码">
    };


    public CallService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            //数字是随便写的“40”，
            nm.createNotificationChannel(new NotificationChannel("40", "App Service", NotificationManager.IMPORTANCE_DEFAULT));
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "40");


            if (AppConfig.appType == 0) {
                builder.setSmallIcon(R.drawable.icon_notify);
                builder.setContentTitle("易楼经纪人");
                builder.setContentText("易楼经纪人正在运行中");
            } else {
                builder.setSmallIcon(R.drawable.icon_im_yy);
                builder.setContentTitle("易楼");
                builder.setContentText("易楼正在运行中");
            }

            builder.setAutoCancel(true);
            builder.setShowWhen(true);
            startForeground(2, builder.build());
        }

        //  LogUtils.w("初始化--initVideoCallData----");
        // initAudioCallData();
        initVideoCallData();
        // 由于两个模块公用一个IM，所以需要在这里先进行登录，您的App只使用一个model，可以直接调用VideoCall 对应函数
        // 目前 Demo 为了方便您接入，使用的是本地签发 sig 的方式，您的项目上线，务必要保证将签发逻辑转移到服务端，否者会出现 key 被盗用，流量盗用的风险。
//        if (SessionWrapper.isMainProcess(this)) {
//            TIMSdkConfig config = new TIMSdkConfig(GenerateTestUserSig.SDKAPPID)
//                    .enableLogPrint(true)
//                    .setLogLevel(TIMLogLevel.DEBUG)
//                    .setLogPath(Environment.getExternalStorageDirectory().getPath() + "/justfortest/");
//            //初始化 SDK
//            TIMManager.getInstance().init(this, config);
//        }
//        String userId  = ProfileManager.getInstance().getUserModel().userId;
//        String userSig = ProfileManager.getInstance().getUserModel().userSig;
//        Log.d("Login", "login: " + userId + " " + userSig);
//        // 由于这里提前登陆了，所以会导致上一次的消息收不到，您在APP中单独使用 ITRTCAudioCall.login 不会出现这种问题
//        TIMManager.getInstance().login(userId, userSig, new TIMCallBack() {
//            @Override
//            public void onError(int i, String s) {
//                // 登录IM失败
//                ToastUtils.showLong("登录IM失败，所有功能不可用[" + i + "]" + s);
//            }
//
//            @Override
//            public void onSuccess() {
//                //1. 登录IM成功
//                ToastUtils.showLong("登录IM成功");
//                initAudioCallData();
//                initVideoCallData();
//                initLiveRoom();
//            }
//        });
    }

//    private void initLiveRoom() {
//        final UserModel userModel = ProfileManager.getInstance().getUserModel();
//        mTRTCLiveRoom = TRTCLiveRoom.sharedInstance(this);
//        boolean                            useCDNFirst = SPUtils.getInstance().getBoolean(TCConstants.USE_CDN_PLAY, false);
//        //您可以设置类似于 http://{bizid}.liveplay.myqcloud.com/live 的播放地址
//        TRTCLiveRoomDef.TRTCLiveRoomConfig config      = new TRTCLiveRoomDef.TRTCLiveRoomConfig(useCDNFirst, "");
//        mTRTCLiveRoom.login(GenerateTestUserSig.SDKAPPID, userModel.userId, userModel.userSig, config, new TRTCLiveRoomCallback.ActionCallback() {
//            @Override
//            public void onCallback(int code, String msg) {
//                if (code == 0) {
//                    mTRTCLiveRoom.setSelfProfile(userModel.userName, userModel.userAvatar, new TRTCLiveRoomCallback.ActionCallback() {
//                        @Override
//                        public void onCallback(int code, String msg) {
//                        }
//                    });
//                }
//            }
//        });
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mTRTCAudioCallListener && null != mITRTCAudioCall) {
            mITRTCAudioCall.removeListener(mTRTCAudioCallListener);
        }

        TRTCAudioCallImpl.destroySharedInstance();
        if (null != mTRTCVideoCallListener && null != mITRTCVideoCall) {
            mITRTCVideoCall.removeListener(mTRTCVideoCallListener);
        }

        TRTCVideoCallImpl.destroySharedInstance();
    }

    private void initAudioCallData() {
        mITRTCAudioCall = TRTCAudioCallImpl.sharedInstance(this);
        mITRTCAudioCall.init();
        mITRTCAudioCall.addListener(mTRTCAudioCallListener);
        //为了方便接入和测试
//        int appid = AppConfig.sdkAppId;
//        String userId = ProfileManager.getInstance().getUserModel().userId;
//        String userSig = ProfileManager.getInstance().getUserModel().userSig;
//        mITRTCAudioCall.login(appid, userId, userSig, null);
    }

    private void initVideoCallData() {
        mITRTCVideoCall = TRTCVideoCallImpl.sharedInstance(this);
        mITRTCVideoCall.init();
        mITRTCVideoCall.addListener(mTRTCVideoCallListener);
        int appid = AppConfig.sdkAppId;
        String userId = AppConfig.appImId;
        String userSig = AppConfig.userSig;
        mITRTCVideoCall.login(appid, userId, userSig, null);
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
