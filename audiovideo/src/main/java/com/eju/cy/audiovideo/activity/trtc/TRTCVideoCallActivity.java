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
 * ???????????????????????????????????????????????????????????????????????????????????????????????????
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
     * ??????????????????
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

    //????????????

    RelativeLayout rl_called;
    ImageView iv_initiator, iv_initiator_jujue, iv_initiator_jieting;
    TextView tv_initiator, tv_count;

    RecyclerView ry_inviter;

    private InviterViewAdapter inviterViewAdapter;
    private List<UserStatusDto> inviterViewList = new ArrayList<>();


    private VideoViewAdapter mAdapter;

    private AudioViewAdapter audioViewAdapter;
    private List<UserStatusDto> mData = new ArrayList<>();

    //?????????????????????
    private List<UserStatusDto> notPermissionsList = new ArrayList<>();
    private String groupId;
    /**
     * ????????????????????????
     */
    private UserModel mSelfModel;
    private List<UserModel> mCallUserModelList = new ArrayList<>(); // ?????????
    private Map<String, UserModel> mCallUserModelMap = new HashMap<>();
    private UserModel mSponsorUserModel; // ?????????
    private List<UserModel> mOtherInvitingUserModelList;//????????????????????????
    private int mCallType;
    private ITRTCVideoCall mITRTCVideoCall;
    private boolean isHandsFree = false;
    private boolean isMuteMic = false;
    private boolean openCamera = false;

    private List<String> countjurisdictionList = new ArrayList<>();

    /**
     * ???????????????
     */
    private TRTCVideoCallListener mTRTCVideoCallListener = new TRTCVideoCallListener() {
        @Override
        public void onError(int code, String msg) {
            //??????????????????????????????????????????
            ToastUtils.showLong("????????????[" + code + "]:" + msg);
            finish();
        }

        @Override
        public void onInvited(String sponsor, List<String> userIdList, List<String> jurisdictionList, boolean isFromGroup, String groupId, int callType) {
        }


        /**
         * ??????IM????????????????????????????????????????????????????????????????????????
         * ?????? A-B-C ??????IM????????????A??????[D???E]???????????????B???C?????????[D???E]?????????
         * ???????????? A ????????? F ?????????????????????B???C?????????[D???E???F]?????????
         * @param userIdList ????????????
         */
        @Override
        public void onGroupCallInviteeListUpdate(final List<String> userIdList, final List<String> jurisdictionList) {


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    countjurisdictionList.clear();
                    countjurisdictionList.addAll(jurisdictionList);
                    LogUtils.w("onGroupCallInviteeListUpdate" + userIdList.toString() + "\njurisdictionList---" + jurisdictionList.toString());


                    //????????????

                    List<String> newVideoList = new ArrayList<>();
                    newVideoList.addAll(jurisdictionList);
                    //???????????????
                    List<String> nowVideoList = new ArrayList<>();


                    for (UserStatusDto userStatusDto : mData) {
                        nowVideoList.add(userStatusDto.getImId());
                    }

                    newVideoList.removeAll(nowVideoList);


                    //????????????
                    List<String> newOpVoiceList = new ArrayList<>();

                    //???????????????
                    List<String> nowVoiceList = new ArrayList<>();
                    //??????
                    List<String> countList = new ArrayList<>();


                    if (null != userIdList && userIdList.size() > 0) {
                        for (UserStatusDto userStatusDto : notPermissionsList) {

                            nowVoiceList.add(userStatusDto.getImId());

                        }
                        //???????????????
                        countList.addAll(newOpVoiceList);
                        //???????????????
                        countList.addAll(jurisdictionList);
                        //????????????
                        countList.addAll(userIdList);

                        //???????????????
                        countList.removeAll(jurisdictionList);
                        //?????????????????????
                        countList.removeAll(newOpVoiceList);


                        LogUtils.w("newVideoList---???????????????" + newVideoList.toString() + "\ncountList---???????????????" + countList.toString());


                        //????????? ??????


                        //???????????????????????????
                        if (null != newVideoList && newVideoList.size() > 0) {
                            addNewVideoUser(newVideoList);

                        }


                        //???????????????
                        if (null != countList && countList.size() > 0) {
                            //?????????????????????
                            //?????????????????????
                            addNewAudioUser(countList);


                        }


                    }


                }
            });


        }


        //????????????????????????
        //????????????????????????
        //????????????????????????
        @Override
        public void onUserEnter(final String userId) {
            AudioPlayerManager.getInstance().stopMyPlay();

            LogUtils.w("????????????????????????--" + userId + "????????????" + AppConfig.appImId);

            if (null != mData && null != notPermissionsList) {

                //????????????????????????
                if (null != mData) {
                    UserStatusDto user;
                    for (int i = 0; i < mData.size(); i++) {
                        user = mData.get(i);
                        if (user.getImId().equals(userId)) {
                            user.setAnswer(true);
                            mAdapter.notifyItemChanged(i);
                        }
                    }


                    LogUtils.w("??????????????????????????????----" + userId);

                    // mAdapter.notifyDataSetChanged();
                }
                //????????????????????????
                if (null != notPermissionsList) {
                    for (UserStatusDto user : notPermissionsList) {

                        if (user.getImId().equals(userId)) {
                            user.setAnswer(true);
                        }

                    }
                    audioViewAdapter.notifyDataSetChanged();
                    LogUtils.w("??????????????????????????????----" + userId);
                }


            }

        }


        //?????????????????????????????????
        @Override
        public void onUserVideoAvailable(final String userId, final boolean isVideoAvailable) {
            LogUtils.w("???????????????????????????---" + userId + "isVideoAvailable" + isVideoAvailable);


            //??????????????????????????????????????????????????????????????????????????????UI
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


        //??????????????????
        @Override
        public void onUserLeave(final String userId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    // ToastUtils.showLong("??????????????????" + userId);


                    removeUser(userId, "onUserLeave");

                    //1. ??????????????????
                    //  mLayoutManagerTrtc.recyclerCloudViewView(userId);
                    //2. ????????????model
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
                    //  ToastUtils.showLong(userModel.userName + "??????");
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
//                        // ?????????????????????
//                        //1. ??????????????????
//                        // mLayoutManagerTrtc.recyclerCloudViewView(userId);
//                        //2. ????????????model
//                        UserModel userModel = mCallUserModelMap.remove(userId);
//                        if (userModel != null) {
//                            mCallUserModelList.remove(userModel);
//                            ToastUtils.showLong(userModel.userName + "?????????");
//                        }
//                    }


                    //  ToastUtils.showLong("??????" + userId + "?????????");
                }
            });
        }

        @Override
        public void onLineBusy(String userId) {
            if (mCallUserModelMap.containsKey(userId)) {
                // ?????????????????????
                //1. ??????????????????
                //mLayoutManagerTrtc.recyclerCloudViewView(userId);
                //2. ????????????model
                UserModel userModel = mCallUserModelMap.remove(userId);
                if (userModel != null) {
                    mCallUserModelList.remove(userModel);
                    ToastUtils.showLong(userModel.userName + "??????");
                }
            }
        }

        @Override
        public void onCallingCancel() {
            if (mSponsorUserModel != null) {
                ToastUtils.showLong(mSponsorUserModel.userName + " ???????????????");
            }
            finish();

            AudioPlayerManager.getInstance().stopMyPlay();
        }

        @Override
        public void onCallingTimeout() {
            if (mSponsorUserModel != null) {
                ToastUtils.showLong(mSponsorUserModel.userName + " ????????????");
            }
            finish();
        }

        //??????????????????????????????????????????
        @Override
        public void onCallEnd() {

            LogUtils.w("??????");
            finish();
        }


        //???????????????????????????
        @Override
        public void onUserAudioAvailable(String userId, boolean isVideoAvailable) {

        }

        // ????????????????????????
        @Override
        public void onUserVoiceVolume(Map<String, Integer> volumeMap) {
            for (Map.Entry<String, Integer> entry : volumeMap.entrySet()) {
                String userId = entry.getKey();
            }
        }
    };


    /**
     * ???????????????????????????
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
     * ??????????????????
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
        // ?????????????????????????????????????????????
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
        // ?????????????????????

        mTimeHandlerThread = new HandlerThread("time-count-thread");
        mTimeHandlerThread.start();
        mTimeHandler = new Handler(mTimeHandlerThread.getLooper());

        //???????????????
        //  mSelfModel = ProfileManager.getInstance().getUserModel();

//        mSelfModel = new UserModel();
//        mSelfModel.userId = AppConfig.appImId;
//        mSelfModel.userName = "??????";
//        mSelfModel.userSig = AppConfig.userSig;
//        mSelfModel.userAvatar = "http://img.zcool.cn/community/01e0e457b32f640000018c1b402bf2.png@1280w_1l_2o_100sh.png";


        // ?????????????????????????????????
        // ?????????????????????????????????
        // ?????????????????????????????????
        // ?????????????????????????????????
        // ?????????????????????????????????
        Intent intent = getIntent();
        //????????????
        //????????????
        //????????????
        if (intent.getIntExtra(Constants.OPEN_GROUP_VIDEO_CALL, 0) == Constants.OPEN_GROUP_VIDEO_CALL_10000) {
            //?????????????????????
            final List<String> allUserIdList = intent.getStringArrayListExtra(Constants.GROUP_AV_IM_LIST);
            //????????????????????????????????????
            final List<String> jurisdictionList = intent.getStringArrayListExtra(Constants.GROUP_PERMISSIONS_AV_IM_LIST);

            groupId = intent.getStringExtra(Constants.GROUP_ID);

            showTimeCount();


            if (null != allUserIdList && null != jurisdictionList) {

                //???????????????
                allUserIdList.remove(AppConfig.appImId);
                mITRTCVideoCall.groupCall(allUserIdList, jurisdictionList, ITRTCVideoCall.TYPE_VIDEO_CALL, groupId);
                // mITRTCVideoCall.removeInvited(AppConfig.appUserId);
                showView(false);
                //??????????????????????????????
                //??????????????????????????????
                //??????????????????????????????


                addNewVideoUser(jurisdictionList);


            }
            //?????????????????????
            //?????????????????????
            //?????????????????????
            if (null != jurisdictionList && jurisdictionList.size() > 0) {

                List<String> differencesList = new ArrayList<>();
                differencesList.addAll(allUserIdList);
                differencesList.removeAll(jurisdictionList);

                if (null != differencesList && differencesList.size() > 0) {
                    addNewAudioUser(differencesList);
                }


            }


        }
        //?????????????????????
        //?????????????????????
        //?????????????????????
        // ?????????????????????
        // ?????????????????????


        else {

            showView(true);

            //?????????????????????
            List<String> allUserIdList = intent.getStringArrayListExtra(Constants.GROUP_AV_IM_LIST);
            //????????????????????????????????????
            List<String> jurisdictionList = intent.getStringArrayListExtra(Constants.GROUP_PERMISSIONS_AV_IM_LIST);

            groupId = intent.getStringExtra(Constants.GROUP_ID);
            //?????????
            String sponsor = intent.getStringExtra(Constants.GROUP_AV_SPONSOR);

            //????????????????????????
            getInviterData(allUserIdList, sponsor);

            if (null != allUserIdList && null != jurisdictionList) {

                //??????????????????????????????
                //??????????????????????????????
                //??????????????????????????????
                addNewVideoUser(jurisdictionList);


            }
            //?????????????????????
            //?????????????????????
            //?????????????????????
            if (null != jurisdictionList) {

                List<String> differencesList = new ArrayList<>();
                differencesList.addAll(allUserIdList);
                differencesList.removeAll(jurisdictionList);


                //?????????????????????
                //?????????????????????
                //?????????????????????
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

        tv_call_time.setText("????????????");

        ll_camera.setEnabled(false);


        //?????? ?????????????????????RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rl_list.setLayoutManager(linearLayoutManager);
        mAdapter = new VideoViewAdapter(this, AppConfig.appImId, mData, mITRTCVideoCall);
        rl_list.setAdapter(mAdapter);

        //???????????????
        rl_audio_list = findViewById(R.id.rl_audio_list);
        audioViewAdapter = new AudioViewAdapter(this, notPermissionsList);
        LinearLayoutManager notPermissionsLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rl_audio_list.setLayoutManager(notPermissionsLinearLayoutManager);
        rl_audio_list.setAdapter(audioViewAdapter);


        //?????????
        inviterViewAdapter = new InviterViewAdapter(this, inviterViewList);
        LinearLayoutManager inviterViewAdapterLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        ry_inviter.setLayoutManager(inviterViewAdapterLayoutManager);
        ry_inviter.setAdapter(inviterViewAdapter);

    }


    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.ll_mute) {
            //??????
            isMuteMic = !isMuteMic;
            mITRTCVideoCall.setMicMute(isMuteMic);
            ToastUtils.showLong(isMuteMic ? "????????????" : "????????????");

            iv_mute.setImageDrawable(isMuteMic ? getResources().getDrawable(R.drawable.icon_sta_jingying) : getResources().getDrawable(R.drawable.icon_jingying));


        } else if (id == R.id.ll_camera) {

            openCamera = !openCamera;
            ToastUtils.showLong(openCamera ? "???????????????" : "???????????????");

            if (openCamera && null != myTxCloudVideoView) {
                mITRTCVideoCall.openCamera(true, myTxCloudVideoView);
                isOpenCamera(true);
            } else {
                mITRTCVideoCall.closeCamera();
                isOpenCamera(false);

            }

            iv_camera.setImageDrawable(openCamera ? getResources().getDrawable(R.drawable.icon_close_sxt_sta) : getResources().getDrawable(R.drawable.icon_close_sxt));

        } else if (id == R.id.ll_hands_free) {
            //?????????
            isHandsFree = !isHandsFree;
            mITRTCVideoCall.setHandsFree(isHandsFree);
            ToastUtils.showLong(isHandsFree ? "????????????" : "???????????????");

            iv_hands_free.setImageDrawable(isHandsFree ? getResources().getDrawable(R.drawable.icon_tingtong_im) : getResources().getDrawable(R.drawable.icon_tingtong_im_sta));

        } else if (id == R.id.iv_add_im_user) {
            //???????????????????????????
            newUser();
            // addUser();

        } else if (id == R.id.iv_close_call) {
            AudioPlayerManager.getInstance().stopMyPlay();
            mITRTCVideoCall.hangup();
            finish();

        } else if (id == R.id.iv_initiator_jujue) {
            //??????
            AudioPlayerManager.getInstance().stopMyPlay();
            mITRTCVideoCall.reject();
            finish();
        } else if (id == R.id.iv_initiator_jieting) {
            //??????
            mITRTCVideoCall.accept();
            showView(false);

            showTimeCount();
        }
    }


    /**
     * ?????????????????????UI????????????????????????????????????????????????????????????
     *
     * @param userId
     */
    private void removeUser(String userId, String type) {
        String userStatus = "";

        if (type.equals("onUserLeave")) {
            userStatus = "??????";
        } else if (type.equals("onReject")) {

            userStatus = "??????";

        } else if (type.equals("onNoResp")) {
            userStatus = "?????????";
        }

        //?????? ????????????
        if (null != mData) {

            for (int i = 0; i < mData.size(); i++) {


                if (mData.get(i).getImId().equals(userId)) {
                    LogUtils.w("????????????" + mData.get(i).getUserNakeName() + userStatus);
                    ToastUtils.showLong("????????????" + mData.get(i).getUserNakeName() + userStatus);

                    mData.remove(i);
                    i--;
                    mAdapter.notifyItemRemoved(i);
                    mAdapter.notifyItemRangeChanged(i, mData.size() - i);
                }


            }
        }

        //?????? ????????????
        if (null != notPermissionsList) {

            for (int i = 0; i < notPermissionsList.size(); i++) {


                if (notPermissionsList.get(i).getImId().equals(userId)) {
                    ToastUtils.showLong("????????????" + notPermissionsList.get(i).getUserNakeName() + userStatus);
                    LogUtils.w("????????????" + notPermissionsList.get(i).getUserNakeName() + userStatus);
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
            //??????
            rl_title.setVisibility(View.VISIBLE);
            rl_video.setVisibility(View.VISIBLE);
            rl_menu.setVisibility(View.VISIBLE);
            iv_close_call.setVisibility(View.VISIBLE);
            rl_called.setVisibility(View.GONE);
        } else {
            //?????????
            rl_title.setVisibility(View.GONE);
            rl_video.setVisibility(View.GONE);
            rl_menu.setVisibility(View.GONE);
            iv_close_call.setVisibility(View.GONE);
            rl_called.setVisibility(View.VISIBLE);
        }


    }


    /**
     * ?????????????????????
     *
     * @param list    ?????????
     * @param sponsor ?????????
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


                        tv_count.setText("??????" + (inviterViewList.size() - 1) + "???????????????");


                        for (UserStatusDto userStatusDto : inviterViewList) {
                            LogUtils.w("?????????????????????" + userStatusDto.getImId() + "\nsponsor" + sponsor);
                            //?????????????????????
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
     * ??????????????????????????????
     */
    private void newUser() {

        GroupAvNumberDto groupAvNumberDto = new GroupAvNumberDto();
        //??????
        List<String> allUseList = new ArrayList<>();
        //?????????
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
            //???????????????
            allUseList.addAll(openVideoList);
            //????????????
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
     * ?????????
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
     * ???????????????????????????ID
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
     * ???????????????????????????ID
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
     * ?????????????????????????????????
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

                    // ????????????????????????
                    List<String> allUserList = groupAvNumberDto.getAllUseList();

                    //??????????????????????????????
                    List<String> videoUserList = groupAvNumberDto.getOpenVideoList();


                    List<String> jurisdictionList = new ArrayList<>();
                    jurisdictionList.addAll(getOpenVideoList());
                    jurisdictionList.addAll(videoUserList);


                    if (null != videoUserList && videoUserList.size() > 0) {

                        addNewVideoUser(videoUserList);
                    }


                    mITRTCVideoCall.groupCall(allUserList, jurisdictionList, ITRTCVideoCall.TYPE_VIDEO_CALL, groupId);


                    //  ???????????????????????????
                    //  ???????????????????????????
                    //  ???????????????????????????

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
     * ???????????????
     *
     * @param users
     */
    private void addNewVideoUser(List<String> users) {

        //???????????????????????????
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

                    //?????????
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


                //??????????????????????????????
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
     * ???????????????
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
     * ????????????????????????
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


    //????????????
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
                                ToastUtils.showLong("???????????????????????????????????????");

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
