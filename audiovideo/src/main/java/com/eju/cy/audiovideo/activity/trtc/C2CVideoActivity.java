package com.eju.cy.audiovideo.activity.trtc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.base.BaseActivity;
import com.eju.cy.audiovideo.component.picture.imageEngine.impl.GlideEngine;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.helper.CustomVideoCallUIController;
import com.eju.cy.audiovideo.helper.TRTCListener;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.observer.EjuHomeImObserver;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.tags.AppConfig;
import com.eju.cy.audiovideo.tags.Constants;
import com.eju.cy.audiovideo.trtcs.manager.AudioPlayerManager;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.eju.cy.audiovideo.video.videolayout.TRTCVideoLayout;
import com.eju.cy.audiovideo.video.videolayout.TRTCVideoLayoutManager;
import com.gyf.barlibrary.ImmersionBar;
import com.noober.background.view.BLImageView;
import com.squareup.picasso.Picasso;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @ Name: Caochen
 * @ Date: 2020-08-31
 * @ Time: 14:39
 * @ Description： C2C视频通话
 */
public class C2CVideoActivity extends BaseActivity implements View.OnClickListener, EjuHomeImObserver {

    private BLImageView iv_other;
    private TextView tv_other_name, tv_ing;

    private TRTCVideoLayoutManager trtc_layout_manager;
    private RelativeLayout rl_initiative_call;//主动拨打
    private ImageView iv_switch_audio;
    private LinearLayout ll_close;
    private TextView tv_switch_audio;

    private RelativeLayout rl_be_call;//被呼叫
    private LinearLayout ll_switch_audio, ll_cancel, ll_answer;


    private RelativeLayout rl_in_the_call;//通话中
    private LinearLayout ll_in_the_call_switch_audio, ll_in_the_call_close, ll_in_the_call_switch_camera;


    private RelativeLayout rl_in_the_call_yy;//语音
    private LinearLayout ll_in_the_call_switch_audio_yy, ll_in_the_call_close_yy, ll_in_the_call_switch_camera_yy;
    private ImageView iv_in_the_call_yy, iv_in_the_call_switch_camera_yy;


    private RelativeLayout rl_time;//切换到语音界面后
    private ImageView iv_portrait;
    private TextView tv_user_name, tv_hint;


    private TextView tv_time;
    private List<UserInfo> mCallUserInfoList = new ArrayList<>(); // 呼叫方
    private Map<String, UserInfo> mCallUserModelMap = new HashMap<>();
    private UserInfo mSelfModel = new UserInfo();
    Handler timeHandler = new Handler();
    private Handler mHandler = new Handler();
    private static final int VIDEO_CALL_OUT_INCOMING_TIME_OUT = 15 * 1000;
    private boolean isOPencamer = false;//是否切换为语音通话
    private boolean micSelected = true;//是否静音
    private boolean handsfreeSelected = true;//是否扬声器
    private boolean isThrough = false;//是否接通
    /**
     * 重要信息
     */
    private int mRoomId;
    private String mUserId;
    private TRTCCloud mTRTCCloud;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                timeHandler.postDelayed(this, 1000);
                int time = (int) (System.currentTimeMillis() - CustomVideoCallUIController.getInstance().getEnterRoomTime() + 500) / 1000;
                tv_time.setText(formatSeconds(time));

            } catch (Exception e) {

            }

        }
    };


    private Runnable mVideoCallIncomingTimeOut = new Runnable() {
        @Override
        public void run() {
            CustomVideoCallUIController.getInstance().chaoShi();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c2_cvideo);
        ImmersionBar.with(this)
                .statusBarColor(R.color.color_222328)
                .statusBarDarkFont(true, 0.2f)
                .fitsSystemWindows(true)
                .init();
        initView();
        initData();


    }

    private void initView() {

        tv_ing = findViewById(R.id.tv_ing);
        tv_other_name = findViewById(R.id.tv_other_name);
        iv_other = findViewById(R.id.iv_other);


        trtc_layout_manager = findViewById(R.id.trtc_layout_manager);


        rl_initiative_call = findViewById(R.id.rl_initiative_call);
        iv_switch_audio = findViewById(R.id.iv_switch_audio);
        ll_close = findViewById(R.id.ll_close);

        tv_switch_audio = findViewById(R.id.tv_switch_audio);
        ll_close.setOnClickListener(this);
        iv_switch_audio.setOnClickListener(this);

        rl_be_call = findViewById(R.id.rl_be_call);
        ll_switch_audio = findViewById(R.id.ll_switch_audio);
        ll_cancel = findViewById(R.id.ll_cancel);
        ll_cancel.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_time);
        ll_answer = findViewById(R.id.ll_answer);
        ll_answer.setOnClickListener(this);
        ll_switch_audio.setOnClickListener(this);

        rl_in_the_call = findViewById(R.id.rl_in_the_call);
        ll_in_the_call_switch_audio = findViewById(R.id.ll_in_the_call_switch_audio);
        ll_in_the_call_close = findViewById(R.id.ll_in_the_call_close);
        ll_in_the_call_close.setOnClickListener(this);
        ll_in_the_call_switch_camera = findViewById(R.id.ll_in_the_call_switch_camera);
        ll_in_the_call_switch_camera.setOnClickListener(this);
        ll_in_the_call_switch_audio.setOnClickListener(this);


        rl_in_the_call_yy = findViewById(R.id.rl_in_the_call_yy);

        ll_in_the_call_switch_audio_yy = findViewById(R.id.ll_in_the_call_switch_audio_yy);
        ll_in_the_call_close_yy = findViewById(R.id.ll_in_the_call_close_yy);
        ll_in_the_call_switch_camera_yy = findViewById(R.id.ll_in_the_call_switch_camera_yy);

        iv_in_the_call_yy = findViewById(R.id.iv_in_the_call_yy);
        iv_in_the_call_switch_camera_yy = findViewById(R.id.iv_in_the_call_switch_camera_yy);

        ll_in_the_call_switch_audio.setOnClickListener(this);
        ll_in_the_call_close_yy.setOnClickListener(this);
        ll_in_the_call_switch_camera_yy.setOnClickListener(this);

        ll_in_the_call_switch_audio_yy.setOnClickListener(this);

        //正在通话中
        rl_time = findViewById(R.id.rl_time);
        iv_portrait = findViewById(R.id.iv_portrait);
        tv_user_name = findViewById(R.id.tv_user_name);
        tv_hint = findViewById(R.id.tv_hint);

    }

    @Override
    public void onClick(View v) {


        if (v == ll_close) {
            quXiao();
            finish();
            mHandler.removeCallbacksAndMessages(null);
        } else if (v == ll_in_the_call_close) {
            finishAudioCall();
            mHandler.removeCallbacksAndMessages(null);
        } else if (v == ll_in_the_call_switch_camera) {
            mTRTCCloud.switchCamera();
        } else if (v == ll_answer) {
            enterTRTCRoom();//接听
            CustomVideoCallUIController.getInstance().jieTing();
        } else if (v == ll_cancel) {
            //拒绝
            timeHandler.removeCallbacksAndMessages(null);
            mHandler.removeCallbacksAndMessages(null);
            CustomVideoCallUIController.getInstance().jujue();
            mTRTCCloud.exitRoom();
            finish();
        } else if (v == ll_switch_audio) {
            //被呼叫点切换且进房
            enterTRTCRoom();//接听
            CustomVideoCallUIController.getInstance().switchAudio();
        } else if (v == iv_switch_audio) {
            CustomVideoCallUIController.getInstance().switchAudio();
        } else if (v == ll_in_the_call_switch_audio) {
            CustomVideoCallUIController.getInstance().switchAudio();
        } else if (v == ll_in_the_call_switch_audio_yy) {
            //静音
            boolean currentMode = !micSelected;
            // 开关麦克风
            enableMic(currentMode);
            micSelected = currentMode;
            if (currentMode) {
                ToastUtils.showLong("您已开启麦克风");
                iv_in_the_call_yy.setImageDrawable(getResources().getDrawable(R.drawable.icon_yy_jy));
            } else {
                ToastUtils.showLong("您已关闭麦克风");
                iv_in_the_call_yy.setImageDrawable(getResources().getDrawable(R.drawable.icon_sta_jingying));

            }

        } else if (v == ll_in_the_call_close_yy) {
            //挂断
            finishAudioCall();
            mHandler.removeCallbacksAndMessages(null);
            finish();

        } else if (v == ll_in_the_call_switch_camera_yy) {
            boolean currentMode = !handsfreeSelected;
            // 是否走扬声器
            enableHandfree(currentMode);
            handsfreeSelected = currentMode;
            if (currentMode) {
                ToastUtils.showLong("使用扬声器模式");
                iv_in_the_call_switch_camera_yy.setImageDrawable(getResources().getDrawable(R.drawable.icon_yy_wf_close));
            } else {
                ToastUtils.showLong("使用耳机模式");
                iv_in_the_call_switch_camera_yy.setImageDrawable(getResources().getDrawable(R.drawable.icon_yy_wf_open));
            }
        }


    }

    //启用麦克风
    public void enableMic(boolean enable) {
        if (enable) {
            mTRTCCloud.startLocalAudio();
        } else {
            mTRTCCloud.stopLocalAudio();
        }
    }

    //免提
    public void enableHandfree(boolean isUseHandsfree) {
        mTRTCCloud.setAudioRoute(isUseHandsfree ? TRTCCloudDef.TRTC_AUDIO_ROUTE_SPEAKER :
                TRTCCloudDef.TRTC_AUDIO_ROUTE_EARPIECE);
    }

    private void quXiao() {
        CustomVideoCallUIController.getInstance().quxiao();
        mTRTCCloud.exitRoom();
    }

    /**
     * 用于监听TRTC事件
     */
    private TRTCCloudListener mTRTCCloudListener = new TRTCCloudListener() {

        @Override
        public void onUserVideoAvailable(String userId, boolean isVideoAvailable) {
            super.onUserVideoAvailable(userId, isVideoAvailable);
            LogUtils.w("用户" + userId + "------视频状态" + isVideoAvailable);
            //有用户的视频开启了
            TRTCVideoLayout layout = trtc_layout_manager.findCloudViewView(userId);
            if (layout != null) {
                layout.setVideoAvailable(isVideoAvailable);
                if (isVideoAvailable) {
                    mTRTCCloud.startRemoteView(userId, layout.getVideoView());
                } else {
                    mTRTCCloud.stopRemoteView(userId);
                }
            } else {

            }

        }

        @Override
        public void onEnterRoom(long result) {
            if (result == 0) {
                ToastUtils.showShort("进房成功");
                AudioPlayerManager.getInstance().stopMyPlay();
            }
        }

        @Override
        public void onExitRoom(int i) {
            super.onExitRoom(i);
            finish();
        }

        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            ToastUtils.showLong("进房失败: " + errMsg);
            LogUtils.w("失败" + errMsg + "" + errCode);
            AudioPlayerManager.getInstance().stopMyPlay();
            finishAudioCall();
            finish();
        }

        @Override
        public void onRemoteUserEnterRoom(final String userId) {
            CustomVideoCallUIController.getInstance().staTime();
            LogUtils.w("远端朋友进入房间" + userId);
            AudioPlayerManager.getInstance().stopMyPlay();
            isThrough = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timeHandler.postDelayed(runnable, 1000);
                    mHandler.removeCallbacksAndMessages(null);
                    findViewById(R.id.tv_ing).setVisibility(View.GONE);

                    tv_other_name.setVisibility(View.GONE);
                    iv_other.setVisibility(View.GONE);

                    rl_initiative_call.setVisibility(View.GONE);
                    rl_be_call.setVisibility(View.GONE);

                    if (isOPencamer) {
                        rl_in_the_call.setVisibility(View.GONE);
                        rl_in_the_call_yy.setVisibility(View.VISIBLE);
                        setAudioUi();
                    } else {
                        rl_in_the_call.setVisibility(View.VISIBLE);
                        rl_in_the_call_yy.setVisibility(View.GONE);
                    }


                    //1.先造一个虚拟的用户添加到屏幕上
                    UserInfo model = new UserInfo();
                    model.userId = userId;
                    model.userName = userId;
                    model.userAvatar = "";
                    mCallUserInfoList.add(model);
                    mCallUserModelMap.put(model.userId, model);
                    TRTCVideoLayout videoLayout = addUserToManager(model);
                    if (videoLayout == null) {
                        return;
                    }
                    videoLayout.setVideoAvailable(false);
                }
            });


        }

        @Override
        public void onRemoteUserLeaveRoom(final String userId, int reason) {
            // mLayoutManagerTrtc.recyclerAudioCallLayout(userId);

            // 对方超时掉线
            if (reason == 1) {
                finishAudioCall();
                finish();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //1. 回收界面元素
                    trtc_layout_manager.recyclerCloudViewView(userId);
                    //2. 删除用户model
                    UserInfo userInfo = mCallUserModelMap.remove(userId);
                    if (userInfo != null) {
                        mCallUserInfoList.remove(userInfo);
                    }
                }
            });


        }

        @Override
        public void onUserVoiceVolume(ArrayList<TRTCCloudDef.TRTCVolumeInfo> userVolumes, int totalVolume) {
//            for (TRTCCloudDef.TRTCVolumeInfo info : userVolumes) {
//                String userId = info.userId;
//                // 如果userId为空，代表自己
////                if (info.userId == null) {
////                    userId = mUserId;
////                }
////                TRTCAudioLayout layout = mLayoutManagerTrtc.findAudioCallLayout(info.userId);
////                if (layout != null) {
////                    layout.setAudioVolume(info.volume);
////                }
//
//
//            }
        }
    };

    private void finishAudioCall() {
        CustomVideoCallUIController.getInstance().hangup();
        mTRTCCloud.exitRoom();
    }

    private TRTCVideoLayout addUserToManager(UserInfo userInfo) {
        TRTCVideoLayout layout = trtc_layout_manager.allocCloudVideoView(userInfo.userId);
        if (layout == null) {
            return null;
        }
        layout.getUserNameTv().setText(userInfo.userName);
        if (!TextUtils.isEmpty(userInfo.userAvatar)) {
            Picasso.get().load(userInfo.userAvatar).into(layout.getHeadImg());
        }
        return layout;
    }

    public static class UserInfo implements Serializable {
        public String userId;
        public String userAvatar;
        public String userName;
    }

    private void initData() {
        EjuHomeImEventCar.getDefault().register(this);
        mTRTCCloud = TRTCCloud.sharedInstance(this);
        Intent intent = getIntent();
        mRoomId = intent.getIntExtra(Constants.ROOM_ID, 0);
        mUserId = intent.getStringExtra(Constants.USER_ID);
        String imgUrl = getIntent().getStringExtra(Constants.USER_PORTRAIT);
        String userName = getIntent().getStringExtra(Constants.USER_NAME);


        mSelfModel.userId = mUserId;
        mSelfModel.userName = userName;
        mSelfModel.userAvatar = imgUrl;


        tv_other_name.setText(userName);


        if (null != imgUrl && imgUrl.length() > 0) {
            GlideEngine.loadCornerImage(iv_other, imgUrl, null, 8);
        }

        //被呼叫
        boolean isPassiveCall = intent.getBooleanExtra(Constants.IS_PASSIVE_CALL, false);

        if (isPassiveCall) {
            rl_initiative_call.setVisibility(View.GONE);
            rl_be_call.setVisibility(View.VISIBLE);
            rl_in_the_call.setVisibility(View.GONE);
            rl_in_the_call_yy.setVisibility(View.GONE);
            tv_ing.setText("邀请你视频通话");
        } else {
            rl_initiative_call.setVisibility(View.VISIBLE);
            rl_be_call.setVisibility(View.GONE);
            rl_in_the_call.setVisibility(View.GONE);
            rl_in_the_call_yy.setVisibility(View.GONE);
            mHandler.postDelayed(mVideoCallIncomingTimeOut, VIDEO_CALL_OUT_INCOMING_TIME_OUT);
            enterTRTCRoom();
        }


    }


    private void enterTRTCRoom() {

        TRTCListener.getInstance().addTRTCCloudListener(mTRTCCloudListener);
        mTRTCCloud.enableAudioVolumeEvaluation(800);
        mTRTCCloud.setListener(mTRTCCloudListener);
          mTRTCCloud.startLocalAudio();
        // 拼接进房参数
        TRTCCloudDef.TRTCParams params = new TRTCCloudDef.TRTCParams();
        params.userSig = AppConfig.userSig;
        params.roomId = mRoomId;
        params.sdkAppId = AppConfig.sdkAppId;
        params.role = TRTCCloudDef.TRTCRoleAnchor;
        params.userId = mUserId;


        LogUtils.w("---userSig" + params.userSig);
        LogUtils.w("---参数" + params.roomId);
        LogUtils.w("---sdkAppId" + params.sdkAppId);

        LogUtils.w("---userId" + params.userId);
        //  mTRTCCloud.startLocalPreview(true, local_video_preview);
        mTRTCCloud.enterRoom(params, TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL);

        trtc_layout_manager.setMySelfUserId(mUserId);


        TRTCVideoLayout videoLayout = addUserToManager(mSelfModel);
        if (videoLayout == null) {
            return;
        }
        videoLayout.setVideoAvailable(true);
        mTRTCCloud.startLocalPreview(true, videoLayout.getVideoView());

        if (isOPencamer) {

            mTRTCCloud.stopLocalPreview();
            trtc_layout_manager.setVisibility(View.GONE);
        }
    }

    public String formatSeconds(long seconds) {
        String standardTime;
        if (seconds <= 0) {
            standardTime = "00:00";
        } else if (seconds < 60) {
            standardTime = String.format(Locale.getDefault(), "00:%02d", seconds % 60);
        } else if (seconds < 3600) {
            standardTime = String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60);
        } else {
            standardTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", seconds / 3600, seconds % 3600 / 60, seconds % 60);
        }
        return standardTime;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioPlayerManager.getInstance().stopMyPlay();
        EjuHomeImEventCar.getDefault().unregister(this);
        timeHandler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void action(Object obj) {
        if (obj instanceof String) {

            ImActionDto imActionDto = JsonUtils.fromJson(obj.toString(), ImActionDto.class);

            if (imActionDto.getAction() == ActionTags.CLOSE_VIDEO_ACTIVITY) {
                timeHandler.removeCallbacksAndMessages(null);
                mTRTCCloud.exitRoom();
                finish();
            } else if (imActionDto.getAction() == ActionTags.SWITCH_AUDIO_CALL) {
                isOPencamer = true;
                mTRTCCloud.stopLocalPreview();

                //被呼叫
                boolean isPassiveCall = getIntent().getBooleanExtra(Constants.IS_PASSIVE_CALL, false);
                //是否接通
                if (isThrough) {
                    trtc_layout_manager.setVisibility(View.GONE);
                    rl_initiative_call.setVisibility(View.GONE);
                    rl_be_call.setVisibility(View.GONE);
                    rl_in_the_call.setVisibility(View.GONE);
                    rl_in_the_call_yy.setVisibility(View.VISIBLE);
                    setAudioUi();
                } else {

                    //被呼叫
                    if (isPassiveCall) {
                        trtc_layout_manager.setVisibility(View.GONE);
                        rl_initiative_call.setVisibility(View.GONE);
                        rl_be_call.setVisibility(View.VISIBLE);
                        rl_in_the_call.setVisibility(View.GONE);
                        rl_in_the_call_yy.setVisibility(View.GONE);
                        ll_switch_audio.setVisibility(View.GONE);
                    } else {
                        trtc_layout_manager.setVisibility(View.GONE);
                        rl_initiative_call.setVisibility(View.VISIBLE);

                        rl_be_call.setVisibility(View.GONE);
                        rl_in_the_call.setVisibility(View.GONE);
                        rl_in_the_call_yy.setVisibility(View.GONE);
                        ll_switch_audio.setVisibility(View.GONE);
                        iv_switch_audio.setVisibility(View.GONE);
                        tv_switch_audio.setVisibility(View.GONE);
                    }

                }


            }


        }

    }



    private  void  setAudioUi(){

        String userName = getIntent().getStringExtra(Constants.USER_NAME)+"";
        String imgUrl = getIntent().getStringExtra(Constants.USER_PORTRAIT);
        rl_time.setVisibility(View.VISIBLE);

        tv_user_name.setText(userName);
        tv_hint.setText("正在通话中...");
        if (null != imgUrl && imgUrl.length() > 0) {
            GlideEngine.loadCornerImage(iv_portrait, imgUrl, null, 8);
        }

    }
}
