package com.eju.cy.audiovideo.activity.trtc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.adapter.AudioViewAdapter;
import com.eju.cy.audiovideo.adapter.InviterViewAdapter;
import com.eju.cy.audiovideo.adapter.VideoViewAdapter;
import com.eju.cy.audiovideo.component.AudioPlayer;
import com.eju.cy.audiovideo.component.picture.imageEngine.impl.GlideEngine;
import com.eju.cy.audiovideo.controller.GroupController;
import com.eju.cy.audiovideo.controller.ImCallBack;
import com.eju.cy.audiovideo.controller.UserInfoController;
import com.eju.cy.audiovideo.dto.GroupAvNumberDto;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.dto.UserStatusDto;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.observer.EjuHomeImObserver;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.tags.AppConfig;
import com.eju.cy.audiovideo.tags.Constants;
import com.eju.cy.audiovideo.trtcs.manager.AudioPlayerManager;
import com.eju.cy.audiovideo.trtcs.model.ITRTCVideoCall;
import com.eju.cy.audiovideo.trtcs.model.TRTCVideoCallImpl;
import com.eju.cy.audiovideo.trtcs.model.TRTCVideoCallListener;
import com.eju.cy.audiovideo.trtcs.model.UserModel;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * 用于展示视频通话的主界面，通话的接听和拒绝就是在这个界面中完成的。
 *
 * @author guanyifeng
 */
public class TRTCVideoCallActivity extends FragmentActivity implements View.OnClickListener, EjuHomeImObserver {
    public static final int TYPE_BEING_CALLED = 1;
    public static final int TYPE_CALL = 2;

    public static final String PARAM_TYPE = "type";
    public static final String PARAM_USER = "user_model";
    public static final String PARAM_BEINGCALL_USER = "beingcall_user_model";
    public static final String PARAM_OTHER_INVITING_USER = "other_inviting_user_model";
    private static final int MAX_SHOW_INVITING_USER = 4;
    private static final int REQ_PERMISSION_CODE = 0x100;
    /**
     * 界面元素相关
     */


    private Runnable mTimeRunnable;
    private int mTimeCount;
    private Handler mTimeHandler;
    private HandlerThread mTimeHandlerThread;


    private ImageView iv_add_im_user, iv_close_call;
    private TextView tv_call_time;
    private LinearLayout ll_mute, ll_camera, ll_hands_free;


    private ImageView iv_mute, iv_camera, iv_hands_free;

    private TextView tv_mute, tv_camera, tv_hands_free;


    private RecyclerView rl_list, rl_audio_list;


    private RelativeLayout rl_title, rl_video, rl_menu;
    private TXCloudVideoView myTxCloudVideoView;

    //等待接听

    RelativeLayout rl_called;
    ImageView iv_initiator, iv_initiator_jujue, iv_initiator_jieting;
    TextView tv_initiator, tv_count;

    RecyclerView ry_inviter;

    private InviterViewAdapter inviterViewAdapter;
    private List<UserStatusDto> inviterViewList = new ArrayList<>();


    private VideoViewAdapter mAdapter;

    private AudioViewAdapter audioViewAdapter;
    private List<UserStatusDto> mData = new ArrayList<>();

    //无视频权限用户
    private List<UserStatusDto> notPermissionsList = new ArrayList<>();
    private String groupId;
    /**
     * 拨号相关成员变量
     */
    private UserModel mSelfModel;
    private List<UserModel> mCallUserModelList = new ArrayList<>(); // 呼叫方
    private Map<String, UserModel> mCallUserModelMap = new HashMap<>();
    private UserModel mSponsorUserModel; // 被叫方
    private List<UserModel> mOtherInvitingUserModelList;//同时被邀请的用户
    private int mCallType;
    private ITRTCVideoCall mITRTCVideoCall;
    private boolean isHandsFree = false;
    private boolean isMuteMic = false;
    private boolean openCamera = false;

    private List<String> countjurisdictionList = new ArrayList<>();

    /**
     * 拨号的回调
     */
    private TRTCVideoCallListener mTRTCVideoCallListener = new TRTCVideoCallListener() {
        @Override
        public void onError(int code, String msg) {
            //发生了错误，报错并退出该页面
            ToastUtils.showLong("发送错误[" + code + "]:" + msg);
            finish();
        }

        @Override
        public void onInvited(String sponsor, List<String> userIdList, List<String> jurisdictionList, boolean isFromGroup, String groupId, int callType) {
        }


        /**
         * 正在IM群组通话时，如果其他与会者邀请他人，会收到此回调
         * 例如 A-B-C 正在IM群组中，A邀请[D、E]进入通话，B、C会收到[D、E]的回调
         * 如果此时 A 再邀请 F 进入群聊，那么B、C会收到[D、E、F]的回调
         * @param userIdList 邀请群组
         */
        @Override
        public void onGroupCallInviteeListUpdate(final List<String> userIdList, final List<String> jurisdictionList) {


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    countjurisdictionList.clear();
                    countjurisdictionList.addAll(jurisdictionList);
                    LogUtils.w("onGroupCallInviteeListUpdate" + userIdList.toString() + "\njurisdictionList---" + jurisdictionList.toString());


                    //打开视频

                    List<String> newVideoList = new ArrayList<>();
                    newVideoList.addAll(jurisdictionList);
                    //现在视频的
                    List<String> nowVideoList = new ArrayList<>();


                    for (UserStatusDto userStatusDto : mData) {
                        nowVideoList.add(userStatusDto.getImId());
                    }

                    newVideoList.removeAll(nowVideoList);


                    //打开语音
                    List<String> newOpVoiceList = new ArrayList<>();

                    //现在语音的
                    List<String> nowVoiceList = new ArrayList<>();
                    //总的
                    List<String> countList = new ArrayList<>();


                    if (null != userIdList && userIdList.size() > 0) {
                        for (UserStatusDto userStatusDto : notPermissionsList) {

                            nowVoiceList.add(userStatusDto.getImId());

                        }
                        //现在语音的
                        countList.addAll(newOpVoiceList);
                        //现在视频的
                        countList.addAll(jurisdictionList);
                        //新邀请的
                        countList.addAll(userIdList);

                        //删除视频的
                        countList.removeAll(jurisdictionList);
                        //删除现在语音的
                        countList.removeAll(newOpVoiceList);


                        LogUtils.w("newVideoList---新视频用户" + newVideoList.toString() + "\ncountList---新语音用户" + countList.toString());


                        //添加新 视频


                        //添加新邀请视频的人
                        if (null != newVideoList && newVideoList.size() > 0) {
                            addNewVideoUser(newVideoList);

                        }


                        //添加新语音
                        if (null != countList && countList.size() > 0) {
                            //只能开语音的人
                            //只能开语音的人
                            addNewAudioUser(countList);


                        }


                    }


                }
            });


        }


        //进入通话的用户有
        //进入通话的用户有
        //进入通话的用户有
        @Override
        public void onUserEnter(final String userId) {
            AudioPlayerManager.getInstance().stopMyPlay();

            LogUtils.w("进入通话的用户有--" + userId + "本机用户" + AppConfig.appImId);

            if (null != mData && null != notPermissionsList) {

                //进入的是视频用户
                if (null != mData) {
                    UserStatusDto user;
                    for (int i = 0; i < mData.size(); i++) {
                        user = mData.get(i);
                        if (user.getImId().equals(userId)) {
                            user.setAnswer(true);
                            mAdapter.notifyItemChanged(i);
                        }
                    }


                    LogUtils.w("进入通话的是视频用户----" + userId);

                    // mAdapter.notifyDataSetChanged();
                }
                //进入的是音频用户
                if (null != notPermissionsList) {
                    for (UserStatusDto user : notPermissionsList) {

                        if (user.getImId().equals(userId)) {
                            user.setAnswer(true);
                        }

                    }
                    audioViewAdapter.notifyDataSetChanged();
                    LogUtils.w("进入通话的是音频用户----" + userId);
                }


            }

        }


        //远端用户开启关闭摄像头
        @Override
        public void onUserVideoAvailable(final String userId, final boolean isVideoAvailable) {
            LogUtils.w("远端用户摄像头状态---" + userId + "isVideoAvailable" + isVideoAvailable);


            //如果是有权限且开了视频的用户以及关闭视频的用户，跟新UI
            if (null != mData) {
                UserStatusDto videoUser;
                for (int i = 0; i < mData.size(); i++) {
                    videoUser = mData.get(i);
                    if (videoUser.getImId().equals(userId)) {

                        if (isVideoAvailable) {
                            videoUser.setOpenVideo(true);
                        } else {
                            videoUser.setOpenVideo(false);
                        }
                        mAdapter.notifyItemChanged(i);
                    }
                }

            }
        }


        //远端用户离开
        @Override
        public void onUserLeave(final String userId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    // ToastUtils.showLong("远端用户离开" + userId);


                    removeUser(userId, "onUserLeave");

                    //1. 回收界面元素
                    //  mLayoutManagerTrtc.recyclerCloudViewView(userId);
                    //2. 删除用户model
//                    UserModel userModel = mCallUserModelMap.remove(userId);
//                    if (userModel != null) {
//                        mCallUserModelList.remove(userModel);
//                    }
//
//
//


                }
            });
        }

        @Override
        public void onReject(final String userId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    removeUser(userId, "onReject");
                    //  ToastUtils.showLong(userModel.userName + "拒接");
                }
            });
        }

        @Override
        public void onNoResp(final String userId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    removeUser(userId, "onNoResp");

                    // removeUser(userId, "onNoResp");

//                    if (mCallUserModelMap.containsKey(userId)) {
//                        // 进入无响应环节
//                        //1. 回收界面元素
//                        // mLayoutManagerTrtc.recyclerCloudViewView(userId);
//                        //2. 删除用户model
//                        UserModel userModel = mCallUserModelMap.remove(userId);
//                        if (userModel != null) {
//                            mCallUserModelList.remove(userModel);
//                            ToastUtils.showLong(userModel.userName + "无响应");
//                        }
//                    }


                    //  ToastUtils.showLong("用户" + userId + "无响应");
                }
            });
        }

        @Override
        public void onLineBusy(String userId) {
            if (mCallUserModelMap.containsKey(userId)) {
                // 进入无响应环节
                //1. 回收界面元素
                //mLayoutManagerTrtc.recyclerCloudViewView(userId);
                //2. 删除用户model
                UserModel userModel = mCallUserModelMap.remove(userId);
                if (userModel != null) {
                    mCallUserModelList.remove(userModel);
                    ToastUtils.showLong(userModel.userName + "忙线");
                }
            }
        }

        @Override
        public void onCallingCancel() {
            if (mSponsorUserModel != null) {
                ToastUtils.showLong(mSponsorUserModel.userName + " 取消了通话");
            }
            finish();

            AudioPlayerManager.getInstance().stopMyPlay();
        }

        @Override
        public void onCallingTimeout() {
            if (mSponsorUserModel != null) {
                ToastUtils.showLong(mSponsorUserModel.userName + " 通话超时");
            }
            finish();
        }

        //收到该回调说明本次通话结束了
        @Override
        public void onCallEnd() {

            LogUtils.w("关闭");
            finish();
        }


        //远端用户关闭麦克风
        @Override
        public void onUserAudioAvailable(String userId, boolean isVideoAvailable) {

        }

        // 用户说话音量回调
        @Override
        public void onUserVoiceVolume(Map<String, Integer> volumeMap) {
            for (Map.Entry<String, Integer> entry : volumeMap.entrySet()) {
                String userId = entry.getKey();
            }
        }
    };


    /**
     * 主动拨打给某个用户
     *
     * @param context
     * @param models
     */
    public static void startCallSomeone(Context context, List<UserModel> models) {
        Intent starter = new Intent(context, TRTCVideoCallActivity.class);
        starter.putExtra(PARAM_TYPE, TYPE_CALL);
        starter.putExtra(PARAM_USER, new IntentParams(models));
        context.startActivity(starter);
    }

    /**
     * 作为用户被叫
     *
     * @param context
     * @param beingCallUserModel
     */
    public static void startBeingCall(Context context, UserModel beingCallUserModel, List<UserModel> otherInvitingUserModel) {
        Intent starter = new Intent(context, TRTCVideoCallActivity.class);
        starter.putExtra(PARAM_TYPE, TYPE_BEING_CALLED);
        starter.putExtra(PARAM_BEINGCALL_USER, beingCallUserModel);
        starter.putExtra(PARAM_OTHER_INVITING_USER, new IntentParams(otherInvitingUserModel));
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 应用运行时，保持不锁屏、全屏化
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.videocall_activity_online_call);


        ImmersionBar.with(this)
                .statusBarColor(R.color.color_222328)
                .statusBarDarkFont(true, 0.2f)
                .fitsSystemWindows(true)
                .init();

        mITRTCVideoCall = TRTCVideoCallImpl.sharedInstance(this);
        mITRTCVideoCall.addListener(mTRTCVideoCallListener);

        initView();

        AudioPlayerManager.getInstance().startPlay(this, R.raw.av_call, new AudioPlayer.Callback() {
            @Override
            public void onCompletion(Boolean success) {

            }
        }, true);


        checkPermission(this);

    }


    @Override
    public void onBackPressed() {
        mITRTCVideoCall.hangup();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mITRTCVideoCall.closeCamera();
        mITRTCVideoCall.removeListener(mTRTCVideoCallListener);
        stopTimeCount();
        if (null != mTimeHandlerThread) {
            mTimeHandlerThread.quit();
        }

        AudioPlayerManager.getInstance().stopMyPlay();
        EjuHomeImEventCar.getDefault().unregister(this);
    }


    private void initData() {
        // 初始化成员变量

        mTimeHandlerThread = new HandlerThread("time-count-thread");
        mTimeHandlerThread.start();
        mTimeHandler = new Handler(mTimeHandlerThread.getLooper());

        //自己的资料
        //  mSelfModel = ProfileManager.getInstance().getUserModel();

//        mSelfModel = new UserModel();
//        mSelfModel.userId = AppConfig.appImId;
//        mSelfModel.userName = "张三";
//        mSelfModel.userSig = AppConfig.userSig;
//        mSelfModel.userAvatar = "http://img.zcool.cn/community/01e0e457b32f640000018c1b402bf2.png@1280w_1l_2o_100sh.png";


        // 初始化从外界获取的数据
        // 初始化从外界获取的数据
        // 初始化从外界获取的数据
        // 初始化从外界获取的数据
        // 初始化从外界获取的数据
        Intent intent = getIntent();
        //主动呼叫
        //主动呼叫
        //主动呼叫
        if (intent.getIntExtra(Constants.OPEN_GROUP_VIDEO_CALL, 0) == Constants.OPEN_GROUP_VIDEO_CALL_10000) {
            //被邀请的所有人
            final List<String> allUserIdList = intent.getStringArrayListExtra(Constants.GROUP_AV_IM_LIST);
            //被邀请的有权限开视频的人
            final List<String> jurisdictionList = intent.getStringArrayListExtra(Constants.GROUP_PERMISSIONS_AV_IM_LIST);

            groupId = intent.getStringExtra(Constants.GROUP_ID);

            showTimeCount();


            if (null != allUserIdList && null != jurisdictionList) {

                //邀请人进房
                allUserIdList.remove(AppConfig.appImId);
                mITRTCVideoCall.groupCall(allUserIdList, jurisdictionList, ITRTCVideoCall.TYPE_VIDEO_CALL, groupId);
                // mITRTCVideoCall.removeInvited(AppConfig.appUserId);
                showView(false);
                //有权限开视频的人的人
                //有权限开视频的人的人
                //有权限开视频的人的人


                addNewVideoUser(jurisdictionList);


            }
            //只能开语音的人
            //只能开语音的人
            //只能开语音的人
            if (null != jurisdictionList && jurisdictionList.size() > 0) {

                List<String> differencesList = new ArrayList<>();
                differencesList.addAll(allUserIdList);
                differencesList.removeAll(jurisdictionList);

                if (null != differencesList && differencesList.size() > 0) {
                    addNewAudioUser(differencesList);
                }


            }


        }
        //被邀请的人进房
        //被邀请的人进房
        //被邀请的人进房
        // 被邀请的人进房
        // 被邀请的人进房


        else {

            showView(true);

            //被邀请的所有人
            List<String> allUserIdList = intent.getStringArrayListExtra(Constants.GROUP_AV_IM_LIST);
            //被邀请的有权限开视频的人
            List<String> jurisdictionList = intent.getStringArrayListExtra(Constants.GROUP_PERMISSIONS_AV_IM_LIST);

            groupId = intent.getStringExtra(Constants.GROUP_ID);
            //发起者
            String sponsor = intent.getStringExtra(Constants.GROUP_AV_SPONSOR);

            //获取被邀请人数据
            getInviterData(allUserIdList, sponsor);

            if (null != allUserIdList && null != jurisdictionList) {

                //有权限开视频的人的人
                //有权限开视频的人的人
                //有权限开视频的人的人
                addNewVideoUser(jurisdictionList);


            }
            //只能开语音的人
            //只能开语音的人
            //只能开语音的人
            if (null != jurisdictionList) {

                List<String> differencesList = new ArrayList<>();
                differencesList.addAll(allUserIdList);
                differencesList.removeAll(jurisdictionList);


                //只能开语音的人
                //只能开语音的人
                //只能开语音的人
                addNewAudioUser(differencesList);
            }
        }
    }


    private void initView() {
        EjuHomeImEventCar.getDefault().register(this);
        iv_add_im_user = findViewById(R.id.iv_add_im_user);
        tv_call_time = findViewById(R.id.tv_call_time);
        iv_close_call = findViewById(R.id.iv_close_call);

        ll_mute = findViewById(R.id.ll_mute);
        ll_camera = findViewById(R.id.ll_camera);
        ll_hands_free = findViewById(R.id.ll_hands_free);


        iv_mute = findViewById(R.id.iv_mute);
        iv_camera = findViewById(R.id.iv_camera);
        iv_hands_free = findViewById(R.id.iv_hands_free);


        tv_mute = findViewById(R.id.tv_mute);
        tv_camera = findViewById(R.id.tv_camera);
        tv_hands_free = findViewById(R.id.tv_hands_free);

        rl_list = findViewById(R.id.rl_list);


        rl_title = findViewById(R.id.rl_title);
        rl_video = findViewById(R.id.rl_video);
        rl_menu = findViewById(R.id.rl_menu);


        rl_called = findViewById(R.id.rl_called);
        iv_initiator = findViewById(R.id.iv_initiator);
        iv_initiator_jujue = findViewById(R.id.iv_initiator_jujue);

        iv_initiator_jieting = findViewById(R.id.iv_initiator_jieting);
        tv_initiator = findViewById(R.id.tv_initiator);
        tv_count = findViewById(R.id.tv_count);

        ry_inviter = findViewById(R.id.ry_inviter);

        iv_initiator_jujue.setOnClickListener(this);
        iv_initiator_jieting.setOnClickListener(this);


        ll_mute.setOnClickListener(this);
        ll_camera.setOnClickListener(this);
        ll_hands_free.setOnClickListener(this);

        iv_add_im_user.setOnClickListener(this);
        iv_close_call.setOnClickListener(this);

        tv_call_time.setText("等待接听");

        ll_camera.setEnabled(false);


        //设置 有视频权限用户RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rl_list.setLayoutManager(linearLayoutManager);
        mAdapter = new VideoViewAdapter(this, AppConfig.appImId, mData, mITRTCVideoCall);
        rl_list.setAdapter(mAdapter);

        //无视频权限
        rl_audio_list = findViewById(R.id.rl_audio_list);
        audioViewAdapter = new AudioViewAdapter(this, notPermissionsList);
        LinearLayoutManager notPermissionsLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rl_audio_list.setLayoutManager(notPermissionsLinearLayoutManager);
        rl_audio_list.setAdapter(audioViewAdapter);


        //被邀请
        inviterViewAdapter = new InviterViewAdapter(this, inviterViewList);
        LinearLayoutManager inviterViewAdapterLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        ry_inviter.setLayoutManager(inviterViewAdapterLayoutManager);
        ry_inviter.setAdapter(inviterViewAdapter);

    }


    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.ll_mute) {
            //静音
            isMuteMic = !isMuteMic;
            mITRTCVideoCall.setMicMute(isMuteMic);
            ToastUtils.showLong(isMuteMic ? "开启静音" : "关闭静音");

            iv_mute.setImageDrawable(isMuteMic ? getResources().getDrawable(R.drawable.icon_sta_jingying) : getResources().getDrawable(R.drawable.icon_jingying));


        } else if (id == R.id.ll_camera) {

            openCamera = !openCamera;
            ToastUtils.showLong(openCamera ? "开启摄像头" : "关闭摄像头");

            if (openCamera && null != myTxCloudVideoView) {
                mITRTCVideoCall.openCamera(true, myTxCloudVideoView);
                isOpenCamera(true);
            } else {
                mITRTCVideoCall.closeCamera();
                isOpenCamera(false);

            }

            iv_camera.setImageDrawable(openCamera ? getResources().getDrawable(R.drawable.icon_close_sxt_sta) : getResources().getDrawable(R.drawable.icon_close_sxt));

        } else if (id == R.id.ll_hands_free) {
            //扬声器
            isHandsFree = !isHandsFree;
            mITRTCVideoCall.setHandsFree(isHandsFree);
            ToastUtils.showLong(isHandsFree ? "使用听筒" : "使用扬声器");

            iv_hands_free.setImageDrawable(isHandsFree ? getResources().getDrawable(R.drawable.icon_tingtong_im) : getResources().getDrawable(R.drawable.icon_tingtong_im_sta));

        } else if (id == R.id.iv_add_im_user) {
            //邀请新人参加群视频
            newUser();
            // addUser();

        } else if (id == R.id.iv_close_call) {
            AudioPlayerManager.getInstance().stopMyPlay();
            mITRTCVideoCall.hangup();
            finish();

        } else if (id == R.id.iv_initiator_jujue) {
            //拒绝
            AudioPlayerManager.getInstance().stopMyPlay();
            mITRTCVideoCall.reject();
            finish();
        } else if (id == R.id.iv_initiator_jieting) {
            //接听
            mITRTCVideoCall.accept();
            showView(false);

            showTimeCount();
        }
    }


    /**
     * 移除用户，刷新UI（远端用户离开，拒接，无响应等情况使用）
     *
     * @param userId
     */
    private void removeUser(String userId, String type) {
        String userStatus = "";

        if (type.equals("onUserLeave")) {
            userStatus = "离开";
        } else if (type.equals("onReject")) {

            userStatus = "拒接";

        } else if (type.equals("onNoResp")) {
            userStatus = "无响应";
        }

        //移除 视频用户
        if (null != mData) {

            for (int i = 0; i < mData.size(); i++) {


                if (mData.get(i).getImId().equals(userId)) {
                    LogUtils.w("远端用户" + mData.get(i).getUserNakeName() + userStatus);
                    ToastUtils.showLong("远端用户" + mData.get(i).getUserNakeName() + userStatus);

                    mData.remove(i);
                    i--;
                    mAdapter.notifyItemRemoved(i);
                    mAdapter.notifyItemRangeChanged(i, mData.size() - i);
                }


            }
        }

        //移除 语音用户
        if (null != notPermissionsList) {

            for (int i = 0; i < notPermissionsList.size(); i++) {


                if (notPermissionsList.get(i).getImId().equals(userId)) {
                    ToastUtils.showLong("远端用户" + notPermissionsList.get(i).getUserNakeName() + userStatus);
                    LogUtils.w("远端用户" + notPermissionsList.get(i).getUserNakeName() + userStatus);
                    notPermissionsList.remove(i);
                    i--;
                    audioViewAdapter.notifyDataSetChanged();
                }


            }
        }


        LogUtils.w("mDatamData---" + mData.size() + "notPermissionsList-------" + notPermissionsList.size());


    }


    private void showView(boolean isShow) {


        if (!isShow) {
            //接听
            rl_title.setVisibility(View.VISIBLE);
            rl_video.setVisibility(View.VISIBLE);
            rl_menu.setVisibility(View.VISIBLE);
            iv_close_call.setVisibility(View.VISIBLE);
            rl_called.setVisibility(View.GONE);
        } else {
            //被呼叫
            rl_title.setVisibility(View.GONE);
            rl_video.setVisibility(View.GONE);
            rl_menu.setVisibility(View.GONE);
            iv_close_call.setVisibility(View.GONE);
            rl_called.setVisibility(View.VISIBLE);
        }


    }


    /**
     * 查询发起者信息
     *
     * @param list    受邀者
     * @param sponsor 发起者
     */
    private void getInviterData(List<String> list, final String sponsor) {


        if (null != list) {

            UserInfoController.getInstance().getUsersProfile(list, true, new ImCallBack() {
                @Override
                public void onError(int var1, String var2) {

                }

                @Override
                public void onSuccess(Object var1) {

                    List<TIMUserProfile> timUserProfileList = (List<TIMUserProfile>) var1;

                    if (null != timUserProfileList && timUserProfileList.size() > 0) {
                        for (TIMUserProfile timUserProfile : timUserProfileList) {


                            UserStatusDto userStatusDto = new UserStatusDto();
                            userStatusDto.setPortraitUrl(timUserProfile.getFaceUrl());
                            userStatusDto.setUserNakeName(timUserProfile.getNickName());
                            userStatusDto.setImId(timUserProfile.getIdentifier());
                            inviterViewList.add(userStatusDto);


                        }


                        tv_count.setText("还有" + (inviterViewList.size() - 1) + "人参与聊天");


                        for (UserStatusDto userStatusDto : inviterViewList) {
                            LogUtils.w("设置发起人信息" + userStatusDto.getImId() + "\nsponsor" + sponsor);
                            //设置发起者信息
                            if (userStatusDto.getImId().equals(sponsor)) {
                                GlideEngine.loadCornerImage(iv_initiator, userStatusDto.getPortraitUrl(), null, 6);
                                tv_initiator.setText("" + userStatusDto.getUserNakeName());

                                break;
                            }

                        }


                        for (UserStatusDto userStatusDto : inviterViewList) {

                            if (sponsor.equals(userStatusDto.getImId())) {
                                inviterViewList.remove(userStatusDto);
                                return;
                            }

                        }

                        inviterViewAdapter.notifyDataSetChanged();
                    }

                }
            });


        }


    }

    /**
     * 邀请新用户进行音视频
     */
    private void newUser() {

        GroupAvNumberDto groupAvNumberDto = new GroupAvNumberDto();
        //所有
        List<String> allUseList = new ArrayList<>();
        //开视频
        List<String> openVideoList = new ArrayList<>();


        if (null != mData) {
            for (UserStatusDto userStatusDto : mData) {
                openVideoList.add(userStatusDto.getImId());
            }

        }

        if (null != notPermissionsList) {
            for (UserStatusDto userStatusDto : notPermissionsList) {
                allUseList.add(userStatusDto.getImId());
            }
            //加上视频的
            allUseList.addAll(openVideoList);
            //加上自己
            //  allUseList.add(AppConfig.appImId);
        }

        groupAvNumberDto.setAllUseList(allUseList);
        groupAvNumberDto.setOpenVideoList(openVideoList);
        groupAvNumberDto.setGroupId(groupId);

        String groupAvNumberDtoStr = JsonUtils.toJson(groupAvNumberDto);
        ImActionDto imActionDto = new ImActionDto();
        imActionDto.setAction(ActionTags.GROUP_VIDEO_CALL);
        imActionDto.setJsonStr(groupAvNumberDtoStr);
        String voiceCall = JsonUtils.toJson(imActionDto);
        EjuHomeImEventCar.getDefault().post(voiceCall);


    }


    /**
     * 计时器
     */
    private void showTimeCount() {
        if (mTimeRunnable != null) {
            return;
        }
        mTimeCount = 0;
        tv_call_time.setText(getShowTime(mTimeCount));
        if (mTimeRunnable == null) {
            mTimeRunnable = new Runnable() {
                @Override
                public void run() {
                    mTimeCount++;
                    if (tv_call_time != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_call_time.setText(getShowTime(mTimeCount));
                            }
                        });
                    }
                    mTimeHandler.postDelayed(mTimeRunnable, 1000);
                }
            };
        }
        if (null != mTimeHandler) {
            mTimeHandler.postDelayed(mTimeRunnable, 1000);
        }
    }

    private void stopTimeCount() {

        if (null != mTimeHandler) {
            mTimeHandler.removeCallbacks(mTimeRunnable);
            mTimeRunnable = null;

        }

    }

    private String getShowTime(int count) {
        return String.format("%02d:%02d", count / 60, count % 60);
    }


    /**
     * 获取正在视频的用户ID
     *
     * @return
     */
    private List<String> getOpenVideoList() {
        List<String> userOpList = new ArrayList<>();

        if (null != mData) {
            for (UserStatusDto userStatusDto : mData) {
                userOpList.add(userStatusDto.getImId());
            }
        }
        return userOpList;
    }


    /**
     * 获取正在语音的用户ID
     *
     * @return
     */
    private List<String> getOpenVoiceList() {
        List<String> userVoice = new ArrayList<>();
        if (null != notPermissionsList) {
            for (UserStatusDto userStatusDto : notPermissionsList) {
                userVoice.add(userStatusDto.getImId());
            }
        }
        return userVoice;
    }


    private static class IntentParams implements Serializable {
        public List<UserModel> mUserModels;

        public IntentParams(List<UserModel> userModels) {
            mUserModels = userModels;
        }
    }

    /**
     * 处理新接收到的邀请用户
     *
     * @param obj
     */
    @Override
    public void action(Object obj) {


        if (null != obj) {

            String srt = (String) obj;

            ImActionDto imActionDto = JsonUtils.fromJson(srt, ImActionDto.class);
            switch (imActionDto.getAction()) {


                case ActionTags.PUST_NEW_GROUP_AV_IM_LIST:
                    GroupAvNumberDto groupAvNumberDto = JsonUtils.fromJson(imActionDto.getJsonStr(), GroupAvNumberDto.class);

                    // 现在邀请的新用户
                    List<String> allUserList = groupAvNumberDto.getAllUseList();

                    //现在邀请的新视频用户
                    List<String> videoUserList = groupAvNumberDto.getOpenVideoList();


                    List<String> jurisdictionList = new ArrayList<>();
                    jurisdictionList.addAll(getOpenVideoList());
                    jurisdictionList.addAll(videoUserList);


                    if (null != videoUserList && videoUserList.size() > 0) {

                        addNewVideoUser(videoUserList);
                    }


                    mITRTCVideoCall.groupCall(allUserList, jurisdictionList, ITRTCVideoCall.TYPE_VIDEO_CALL, groupId);


                    //  邀请的是只能语音的
                    //  邀请的是只能语音的
                    //  邀请的是只能语音的

                    List<String> voiceList = new ArrayList<>();
                    voiceList.addAll(allUserList);
                    voiceList.removeAll(videoUserList);


                    if (null != voiceList && voiceList.size() > 0) {

                        addNewAudioUser(voiceList);


                    }


                    break;

            }
        }

    }

    private void addUser() {


        List<String> suoyouList = new ArrayList<>();

        suoyouList.add("333");
        suoyouList.add("12345");


        List<String> quanxianList = new ArrayList<>();

        quanxianList.add("333");


        GroupController.getInstance().addNewUsersGroupVideoCall(suoyouList, quanxianList, groupId);

    }


    /**
     * 添加新视频
     *
     * @param users
     */
    private void addNewVideoUser(List<String> users) {

        //添加新邀请视频的人
        UserInfoController.getInstance().getUsersProfile(users, true, new ImCallBack() {


            @Override
            public void onError(int var1, String var2) {

            }

            @Override
            public void onSuccess(Object var1) {


                List<TIMUserProfile> timUserProfileList = (List<TIMUserProfile>) var1;
                int count = 0;

                for (TIMUserProfile timUserProfile : timUserProfileList) {
                    UserStatusDto userStatusDto = new UserStatusDto();
                    userStatusDto.setImId(timUserProfile.getIdentifier());
                    userStatusDto.setUserNakeName(timUserProfile.getNickName());

                    userStatusDto.setPortraitUrl(timUserProfile.getFaceUrl());
                    userStatusDto.setOpenVideoPermissions(true);
                    userStatusDto.setTxCloudVideoView(new TXCloudVideoView(TRTCVideoCallActivity.this));

                    //是自己
                    if (timUserProfile.getIdentifier().equals(AppConfig.appImId)) {
                        userStatusDto.setOpenVideo(true);
                        userStatusDto.setAnswer(true);

                    } else {
                        userStatusDto.setOpenVideo(false);
                        userStatusDto.setAnswer(false);
                    }

                    mData.add(userStatusDto);
                    count++;


                }
                int position = mData.size() > 0 ? mData.size() - 1 : 0;
                mAdapter.notifyItemRangeInserted(position, count);
                // mAdapter.notifyDataSetChanged();


                //开关视频按钮能否使用
                for (UserStatusDto userStatusDto : mData) {

                    if (userStatusDto.getImId().equals(AppConfig.appImId)) {
                        ll_camera.setEnabled(true);
                        openCamera = true;
                        iv_camera.setImageDrawable(getResources().getDrawable(R.drawable.icon_close_sxt_sta));
                        myTxCloudVideoView = userStatusDto.getTxCloudVideoView();

                        break;
                    }

                }


            }
        });

    }

    /**
     * 添加新音频
     *
     * @param users
     */
    private void addNewAudioUser(List<String> users) {

        UserInfoController.getInstance().getUsersProfile(users, true, new ImCallBack() {

            @Override
            public void onError(int var1, String var2) {

            }

            @Override
            public void onSuccess(Object var1) {


                List<TIMUserProfile> imVoiceList = (List<TIMUserProfile>) var1;

                for (TIMUserProfile user : imVoiceList) {

                    UserStatusDto userStatusDto = new UserStatusDto();

                    userStatusDto.setImId(user.getIdentifier());
                    userStatusDto.setOpenVideo(false);
                    userStatusDto.setOpenVideoPermissions(false);
                    userStatusDto.setAnswer(false);
                    userStatusDto.setPortraitUrl(user.getFaceUrl());
                    userStatusDto.setUserNakeName(user.getNickName());
                    notPermissionsList.add(userStatusDto);

                }
                audioViewAdapter.notifyDataSetChanged();

            }
        });
    }

    /**
     * 本地视频是否开启
     *
     * @param isOpen
     */
    private void isOpenCamera(boolean isOpen) {


        for (UserStatusDto userStatusDto : mData) {
            if (userStatusDto.getImId().equals(AppConfig.appImId)) {
                if (isOpen) {
                    userStatusDto.setOpenVideo(true);
                } else {
                    userStatusDto.setOpenVideo(false);
                }
                mAdapter.notifyItemChanged(0);
                break;
            }

        }

    }


    //权限检查
    @SuppressLint("CheckResult")
    public boolean checkPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RxPermissions rxPermissions = new RxPermissions(TRTCVideoCallActivity.this);

            rxPermissions.request(Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            )
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean) {
                                initData();
                            } else {
                                AudioPlayerManager.getInstance().stopMyPlay();
                                ToastUtils.showLong("权限未授予，该功能不可使用");

                                if (getIntent().getIntExtra(Constants.OPEN_GROUP_VIDEO_CALL, 0) == Constants.OPEN_GROUP_VIDEO_CALL_10000) {
                                    mITRTCVideoCall.hangup();

                                } else {
                                    mITRTCVideoCall.reject();
                                }


                                finish();
                            }
                        }
                    });

        } else {
            initData();
        }

        return true;
    }


}
