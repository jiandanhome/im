package com.eju.cy.audiovideo.activity.trtc;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.widget.AppCompatImageButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.TUIKit;
import com.eju.cy.audiovideo.base.BaseActivity;
import com.eju.cy.audiovideo.component.picture.imageEngine.impl.GlideEngine;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.helper.CustomAVCallUIController;
import com.eju.cy.audiovideo.helper.TRTCListener;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.observer.EjuHomeImObserver;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.tags.AppConfig;
import com.eju.cy.audiovideo.tags.Constants;
import com.eju.cy.audiovideo.trtcs.manager.AudioPlayerManager;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.noober.background.view.BLImageView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class C2CAudioCallActivity extends BaseActivity implements EjuHomeImObserver, View.OnClickListener {
    private static final int REQ_PERMISSION_CODE = 0x100;
    private static final String TAG = C2CAudioCallActivity.class.getName();
    private static final int VIDEO_CALL_OUT_INCOMING_TIME_OUT = 15 * 1000;
    /**
     * 重要信息
     */
    private int mRoomId;
    private String mUserId;
    private TRTCCloud mTRTCCloud;
    /**
     * 界面元素
     */

    // private Toolbar mToolbar;
    private AppCompatImageButton mMicBtn;
    private AppCompatImageButton mAudioBtn;
    private AppCompatImageButton mExitBtn;

    private BLImageView mIvPrtrait;
    private TextView mTvUserName;

    private Boolean isUserEnterRoom = false;
    private AppCompatImageButton mHandsfreeBtn;
    private TextView tv_close_call;

    private LinearLayout ll_jy, ll_close, ll_wf;
    private ImageView im_jy, im_wf;
    private boolean micSelected = true;//是否静音
    private boolean handsfreeSelected = true;//是否扬声器
    private TextView tv_hint, tv_time;

    private Handler mHandler = new Handler();
    Handler timeHandler = new Handler();
    private Runnable mVideoCallIncomingTimeOut = new Runnable() {
        @Override
        public void run() {

            CustomAVCallUIController.getInstance().chaoShi();


        }
    };


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                timeHandler.postDelayed(this, 1000);
                int time = (int) (System.currentTimeMillis() - CustomAVCallUIController.getInstance().getEnterRoomTime() + 500) / 1000;
                tv_time.setText(formatSeconds(time));

            } catch (Exception e) {

            }

        }
    };


    /**
     * 用于监听TRTC事件
     */
    private TRTCCloudListener mTRTCCloudListener = new TRTCCloudListener() {
        @Override
        public void onEnterRoom(long result) {
            if (result == 0) {
                ToastUtils.showShort("进房成功");
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

            finishAudioCall();
            finish();
        }

        @Override
        public void onRemoteUserEnterRoom(String userId) {
//            TRTCAudioLayout layout = mLayoutManagerTrtc.allocAudioCallLayout(userId);
//            layout.setUserId(userId);
//            layout.setBitmap(Utils.getAvatar(userId));
            CustomAVCallUIController.getInstance().staTime();

            LogUtils.w("远端朋友进入房间" + userId);
            isUserEnterRoom = true;

            mHandler.removeCallbacksAndMessages(null);


            ll_jy.setVisibility(View.VISIBLE);
            ll_wf.setVisibility(View.VISIBLE);


            tv_hint.setText("正在通话中...");
            timeHandler.postDelayed(runnable, 1000);

            tv_close_call.setText("挂断");
            AudioPlayerManager.getInstance().stopMyPlay();
        }

        @Override
        public void onRemoteUserLeaveRoom(String userId, int reason) {
            // mLayoutManagerTrtc.recyclerAudioCallLayout(userId);

            // 对方超时掉线
            if (reason == 1) {
                finishAudioCall();
                finish();
            }
        }

        @Override
        public void onUserVoiceVolume(ArrayList<TRTCCloudDef.TRTCVolumeInfo> userVolumes, int totalVolume) {
            for (TRTCCloudDef.TRTCVolumeInfo info : userVolumes) {
                String userId = info.userId;
                // 如果userId为空，代表自己
//                if (info.userId == null) {
//                    userId = mUserId;
//                }
//                TRTCAudioLayout layout = mLayoutManagerTrtc.findAudioCallLayout(info.userId);
//                if (layout != null) {
//                    layout.setAudioVolume(info.volume);
//                }


            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c2c_video_call);

        EjuHomeImEventCar.getDefault().register(this);

        ImmersionBar.with(this)
                .statusBarColor(R.color.color_222328)
                .statusBarDarkFont(true, 0.2f)
                .fitsSystemWindows(true)
                .init();
        checkPermission(this);
        initView();
        initData();
        initListener();
        enterTRTCRoom();

        mHandler.postDelayed(mVideoCallIncomingTimeOut, VIDEO_CALL_OUT_INCOMING_TIME_OUT);

    }

    @Override
    protected void onDestroy() {
        timeHandler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacksAndMessages(null);
        EjuHomeImEventCar.getDefault().unregister(this);
        exitRoom();
        super.onDestroy();
    }


    private void initListener() {
        mMicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean currentMode = !mMicBtn.isSelected();
                // 开关麦克风
                enableMic(currentMode);
                mMicBtn.setSelected(currentMode);
                if (currentMode) {
                    ToastUtils.showLong("您已开启麦克风");
                    mMicBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_av_jingyinyikaiqi));
                } else {
                    ToastUtils.showLong("您已关闭麦克风");
                    mMicBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_av_jingyinweikaiqi));

                }
            }
        });

        mHandsfreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean currentMode = !mHandsfreeBtn.isSelected();
                // 是否走扬声器
                enableHandfree(currentMode);
                mHandsfreeBtn.setSelected(currentMode);
                if (currentMode) {
                    ToastUtils.showLong("使用扬声器模式");
                    mHandsfreeBtn.setImageDrawable(getResources().getDrawable(R.drawable.icon_tingtong_im));
                } else {
                    ToastUtils.showLong("使用耳机模式");
                    mHandsfreeBtn.setImageDrawable(getResources().getDrawable(R.drawable.icon_tingtong_im_sta));
                }
            }
        });

        mExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isUserEnterRoom) {
                    LogUtils.w("结束通话--------");
                    finishAudioCall();
                } else {
                    quXiao();
                }
                finish();
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        mRoomId = intent.getIntExtra(Constants.ROOM_ID, 0);
        mUserId = intent.getStringExtra(Constants.USER_ID);
        String imgUrl = getIntent().getStringExtra(Constants.USER_PORTRAIT);
        String userName = getIntent().getStringExtra(Constants.USER_NAME);
        // mTitleToolbar.setText(getString(R.string.audiocall_title, mRoomId));
        mTRTCCloud = TRTCCloud.sharedInstance(this);

        LogUtils.w(TAG, "头像" + getIntent().getStringExtra(Constants.USER_PORTRAIT));
        // mIvPrtrait.setImageURI(Uri.parse(getIntent().getStringExtra(Constants.USER_PORTRAIT)));


        if (null != imgUrl && imgUrl.length() > 0) {
            GlideEngine.loadCornerImage(mIvPrtrait, imgUrl, null, 8);

        }

        mTvUserName.setText(userName);
        LogUtils.w("userId----" + mUserId);
        //layout.setBitmap(Utils.getAvatar(mUserId));
        //layout.setUserId(mUserId);
    }

    private void initView() {
        tv_close_call = (TextView) findViewById(R.id.tv_close_call);
        mMicBtn = (AppCompatImageButton) findViewById(R.id.btn_mic);
        mExitBtn = (AppCompatImageButton) findViewById(R.id.btn_exit);

        mIvPrtrait = (BLImageView) findViewById(R.id.iv_portrait);
        mTvUserName = (TextView) findViewById(R.id.tv_user_name);
        // mLayoutManagerTrtc = (TRTCAudioLayoutManager) findViewById(R.id.trtc_layout_manager);
        mHandsfreeBtn = (AppCompatImageButton) findViewById(R.id.btn_handsfree);

        //设置选中态
        mMicBtn.setActivated(true);
        //  mAudioBtn.setActivated(true);
        mHandsfreeBtn.setActivated(true);
        mMicBtn.setSelected(true);
        //  mAudioBtn.setSelected(true);
        mHandsfreeBtn.setSelected(true);


        tv_hint = (TextView) findViewById(R.id.tv_hint);
        tv_time = (TextView) findViewById(R.id.tv_time);
        ll_jy = (LinearLayout) findViewById(R.id.ll_jy);
        ll_close = (LinearLayout) findViewById(R.id.ll_close);
        ll_wf = (LinearLayout) findViewById(R.id.ll_wf);

        im_jy = (ImageView) findViewById(R.id.im_jy);
        im_wf = (ImageView) findViewById(R.id.im_wf);


        ll_jy.setOnClickListener(this);
        ll_close.setOnClickListener(this);
        ll_wf.setOnClickListener(this);


    }

    private void enterTRTCRoom() {


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

        mTRTCCloud.enterRoom(params, TRTCCloudDef.TRTC_APP_SCENE_AUDIOCALL);
    }

    private void exitRoom() {
        TRTCListener.getInstance().removeTRTCCloudListener(mTRTCCloudListener);
        mTRTCCloud.exitRoom();
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

    //静音
    public void enableAudio(boolean enable) {
        mTRTCCloud.muteAllRemoteAudio(!enable);
    }


    private void finishAudioCall() {
        CustomAVCallUIController.getInstance().hangup();
        mTRTCCloud.exitRoom();
    }

    private void quXiao() {
        CustomAVCallUIController.getInstance().quxiao();
        mTRTCCloud.exitRoom();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LogUtils.i(TAG, "onBackPressed");
        finishAudioCall();
    }

    @Override
    public void action(Object obj) {
        if (obj instanceof String) {

            ImActionDto imActionDto = JsonUtils.fromJson(obj.toString(), ImActionDto.class);

            if (imActionDto.getAction() == ActionTags.CLOSE_VIDEO_ACTIVITY) {
                mHandler.removeCallbacksAndMessages(null);
                mTRTCCloud.exitRoom();
                finish();
            }


        }

    }

    @Override
    public void onClick(View v) {

//        private LinearLayout ll_jy, ll_close, ll_wf;
//        private ImageView im_jy, im_wf;
        if (v == ll_jy) {

            boolean currentMode = !micSelected;
            // 开关麦克风
            enableMic(currentMode);
            micSelected = currentMode;
            if (currentMode) {
                ToastUtils.showLong("您已开启麦克风");
                im_jy.setImageDrawable(getResources().getDrawable(R.drawable.icon_yy_jy));
            } else {
                ToastUtils.showLong("您已关闭麦克风");
                im_jy.setImageDrawable(getResources().getDrawable(R.drawable.icon_sta_jingying));

            }
        } else if (v == ll_close) {
            if (isUserEnterRoom) {
                LogUtils.w("结束通话--------");
                finishAudioCall();
            } else {
                quXiao();
            }

            mHandler.removeCallbacksAndMessages(null);
            finish();


        } else if (v == ll_wf) {


            boolean currentMode = !handsfreeSelected;
            // 是否走扬声器
            enableHandfree(currentMode);
            handsfreeSelected = currentMode;
            if (currentMode) {
                ToastUtils.showLong("使用扬声器模式");
                im_wf.setImageDrawable(getResources().getDrawable(R.drawable.icon_yy_wf_close));
            } else {
                ToastUtils.showLong("使用耳机模式");
                im_wf.setImageDrawable(getResources().getDrawable(R.drawable.icon_yy_wf_open));
            }
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


    //权限检查
    public boolean checkPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();


            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TUIKit.getAppContext(), Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }

            if (permissions.size() != 0) {
                String[] permissionsArray = permissions.toArray(new String[1]);
                ActivityCompat.requestPermissions(activity,
                        permissionsArray,
                        REQ_PERMISSION_CODE);
                return false;
            }
        }

        return true;
    }

}