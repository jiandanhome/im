package com.eju.cy.audiovideo.activity.trtc;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.adapter.VideoViewAdapter;
import com.eju.cy.audiovideo.component.MarginDecoration;
import com.eju.cy.audiovideo.dto.GroupAvCallDto;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.dto.UserStatusDto;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.observer.EjuHomeImObserver;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.tags.AppConfig;
import com.eju.cy.audiovideo.tags.Constants;
import com.eju.cy.audiovideo.trtcs.model.ITRTCVideoCall;
import com.eju.cy.audiovideo.trtcs.model.TRTCVideoCallImpl;
import com.eju.cy.audiovideo.trtcs.model.TRTCVideoCallListener;
import com.eju.cy.audiovideo.trtcs.service.CallService;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.tencent.imsdk.TIMManager;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 群视频Activity
 * 处理相关逻辑
 */
public class GroupVideoCallActivity extends AppCompatActivity implements View.OnClickListener, EjuHomeImObserver {
    private static final String TAG = GroupVideoCallActivity.class.getName();


    private ImageView iv_add_im_user, iv_close_call;
    private TextView tv_call_time;
    private LinearLayout ll_mute, ll_camera, ll_hands_free;


    private ImageView iv_mute, iv_camera, iv_hands_free;

    private TextView tv_mute, tv_camera, tv_hands_free;

    private RecyclerView rl_list;
    private VideoViewAdapter mAdapter;
    private List<UserStatusDto> mData = new ArrayList<>();

    //本地预览
    private TXCloudVideoView trtc_local_preview_view,trtc_local_preview_view2;




    private boolean isHandsFree = true;
    private boolean isMuteMic = false;

    //trtc重要
    //trtc重要
    //trtc重要
    //trtc重要
    //trtc重要

    private ITRTCVideoCall mITRTCVideoCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (TIMManager.getInstance().getLoginStatus() == TIMManager.TIM_STATUS_LOGINED
                && !ServiceUtils.isServiceRunning(CallService.class)) {
            Intent intent = new Intent(this, CallService.class);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.startForegroundService(intent);
            }else {
                this.startService(intent);
            }
            LogUtils.w("初始化CallService---");
        }
        else {
            LogUtils.w("初始化CallService失败---"+TIMManager.getInstance().getLoginStatus());
        }

        // 应用运行时，保持不锁屏、全屏化
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_group_video_call);

        initTrtc();
        initView();
        initData();
        getImIdList();


//        mITRTCVideoCall.startRemoteView("1234",trtc_local_preview_view);
//        mITRTCVideoCall.startRemoteView("12345",trtc_local_preview_view2);


    }

    private void initTrtc() {
        mITRTCVideoCall = TRTCVideoCallImpl.sharedInstance(this);
        mITRTCVideoCall.addListener(mTRTCVideoCallListener);
    }

    /**
     * 获取被邀请人列表
     */
    private void getImIdList() {
        ArrayList<GroupAvCallDto> imList = (ArrayList<GroupAvCallDto>) getIntent().getSerializableExtra(Constants.IMID_LIST);

        if (null != imList) {


            for (GroupAvCallDto groupAvCallDto : imList) {
                LogUtils.w("getImIdList----" + groupAvCallDto.toString());
            }

        }


    }


    /**
     * 处理再次邀请的人
     */

    @Override
    public void action(Object obj) {

        if (null != obj) {

            String srt = (String) obj;

            ImActionDto imActionDto = JsonUtils.fromJson(srt, ImActionDto.class);


            switch (imActionDto.getAction()) {

                //接收新邀请参加群视频的用户
                case ActionTags.PUST_NEW_GROUP_AV_IM_LIST:

                    //循环添加到现有的

                    break;

            }


        }


    }


    private void initData() {

        UserStatusDto groupAvCallDto = new UserStatusDto();
        groupAvCallDto.setImId(AppConfig.appImId);
        groupAvCallDto.setOpenVideo(true);
        groupAvCallDto.setUserNakeName("张三");
        groupAvCallDto.setOpenVideoPermissions(true);
        groupAvCallDto.setAnswer(true);
        groupAvCallDto.setPortraitUrl("http://img.zcool.cn/community/01e0e457b32f640000018c1b402bf2.png@1280w_1l_2o_100sh.png");
        TXCloudVideoView txCloudVideoView1 = new TXCloudVideoView(this);
        txCloudVideoView1.setTag(groupAvCallDto.getImId());

        groupAvCallDto.setTxCloudVideoView(txCloudVideoView1);


        UserStatusDto groupAvCallDto2 = new UserStatusDto();
        groupAvCallDto2.setImId("12345");
        groupAvCallDto2.setOpenVideo(true);
        groupAvCallDto2.setUserNakeName("刘德华");
        groupAvCallDto2.setOpenVideoPermissions(true);
        groupAvCallDto2.setAnswer(true);
        TXCloudVideoView txCloudVideoView2 = new TXCloudVideoView(this);
        // txCloudVideoView2.setTag(groupAvCallDto2.getImId());

        groupAvCallDto2.setTxCloudVideoView(txCloudVideoView2);
        groupAvCallDto2.setPortraitUrl("http://img.zcool.cn/community/01e0e457b32f640000018c1b402bf2.png@1280w_1l_2o_100sh.png");


        UserStatusDto groupAvCallDto3 = new UserStatusDto();
        groupAvCallDto3.setImId("1234");
        groupAvCallDto3.setOpenVideo(true);
        groupAvCallDto3.setUserNakeName("周星驰");
        groupAvCallDto3.setOpenVideoPermissions(true);
        groupAvCallDto3.setAnswer(true);
        TXCloudVideoView txCloudVideoView3 = new TXCloudVideoView(this);
        // txCloudVideoView3.setTag(groupAvCallDto3.getImId());

        groupAvCallDto3.setTxCloudVideoView(txCloudVideoView3);
        groupAvCallDto3.setPortraitUrl("http://img.zcool.cn/community/01e0e457b32f640000018c1b402bf2.png@1280w_1l_2o_100sh.png");


        UserStatusDto groupAvCallDto4 = new UserStatusDto();
        groupAvCallDto4.setImId("33221");
        groupAvCallDto4.setOpenVideo(false);
        groupAvCallDto4.setUserNakeName("哈哈哈");
        groupAvCallDto4.setOpenVideoPermissions(false);
        groupAvCallDto4.setAnswer(false);
        TXCloudVideoView txCloudVideoView4 = new TXCloudVideoView(this);
        //  txCloudVideoView4.setTag(groupAvCallDto4.getImId());

        groupAvCallDto4.setTxCloudVideoView(txCloudVideoView4);

        groupAvCallDto4.setPortraitUrl("http://img.zcool.cn/community/01e0e457b32f640000018c1b402bf2.png@1280w_1l_2o_100sh.png");


        mData.add(groupAvCallDto);
        mData.add(groupAvCallDto2);
        mData.add(groupAvCallDto3);

        mData.add(groupAvCallDto4);
        mData.add(groupAvCallDto4);
        mData.add(groupAvCallDto4);

        mData.add(groupAvCallDto4);
        mData.add(groupAvCallDto4);
        mData.add(groupAvCallDto4);

        mData.add(groupAvCallDto4);
        mData.add(groupAvCallDto4);
        mData.add(groupAvCallDto4);

        mData.add(groupAvCallDto4);
        mData.add(groupAvCallDto4);
        mData.add(groupAvCallDto4);

        mData.add(groupAvCallDto4);
        mData.add(groupAvCallDto4);

        mData.add(groupAvCallDto3);
        mData.add(groupAvCallDto3);
        mData.add(groupAvCallDto3);
        mData.add(groupAvCallDto3);
        mData.add(groupAvCallDto3);
        mData.add(groupAvCallDto3);
        mData.add(groupAvCallDto3);
        mData.add(groupAvCallDto3);
        mData.add(groupAvCallDto3);
        mData.add(groupAvCallDto3);
        mData.add(groupAvCallDto3);
        mData.add(groupAvCallDto3);
        mData.add(groupAvCallDto3);


        mAdapter.notifyDataSetChanged();

    }


    private void initView() {
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


        ll_mute.setOnClickListener(this);
        ll_camera.setOnClickListener(this);
        ll_hands_free.setOnClickListener(this);

        iv_add_im_user.setOnClickListener(this);
        iv_close_call.setOnClickListener(this);

        trtc_local_preview_view = findViewById(R.id.trtc_local_preview_view);
        trtc_local_preview_view2= findViewById(R.id.trtc_local_preview_view2);

        //设置 RecyclerView
        GridLayoutManager gridLayoutManage = new GridLayoutManager(this, 2) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rl_list.setLayoutManager(gridLayoutManage);


        rl_list.addItemDecoration(new MarginDecoration(2, 1, false));

        mAdapter = new VideoViewAdapter(this, AppConfig.appImId, mData, mITRTCVideoCall);
        rl_list.setAdapter(mAdapter);


    }


    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.ll_mute) {
            //静音
            isMuteMic = !isMuteMic;
            mITRTCVideoCall.setMicMute(isMuteMic);
            ToastUtils.showLong(isMuteMic ? "开启静音" : "关闭静音");

        } else if (id == R.id.ll_camera) {
        } else if (id == R.id.ll_hands_free) {

            //扬声器
            isHandsFree = !isHandsFree;
            mITRTCVideoCall.setHandsFree(isHandsFree);
            ToastUtils.showLong(isHandsFree ? "使用扬声器" : "使用听筒");
        } else if (id == R.id.iv_add_im_user) {
            //邀请新人参加群视频
            ImActionDto imActionDto = new ImActionDto();
            imActionDto.setAction(ActionTags.GROUP_VIDEO_CALL);
            imActionDto.setJsonStr("" + mData.size());//告诉应用端当前有权限开视频的用户有多少
            String voiceCall = JsonUtils.toJson(imActionDto);
            EjuHomeImEventCar.getDefault().post(voiceCall);


        } else if (id == R.id.iv_close_call) {

            mITRTCVideoCall.hangup();
            finish();

        }

    }

    @Override
    public void onBackPressed() {
        mITRTCVideoCall.hangup();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mITRTCVideoCall.closeCamera();
        mITRTCVideoCall.removeListener(mTRTCVideoCallListener);
        super.onDestroy();


    }


    /*----------------------------------------------------*/

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
        public void onInvited(String sponsor, List<String> userIdList, List<String> jurisdictionList, boolean isFromGroup, String groupId,int callType) {
        }


        /**
         * 正在IM群组通话时，如果其他与会者邀请他人，会收到此回调
         * 例如 A-B-C 正在IM群组中，A邀请[D、E]进入通话，B、C会收到[D、E]的回调
         * 如果此时 A 再邀请 F 进入群聊，那么B、C会收到[D、E、F]的回调
         * @param userIdList 邀请群组
         */
        @Override
        public void onGroupCallInviteeListUpdate(List<String> userIdList, List<String> jurisdictionList) {
        }

        //进入通话的用户有
        @Override
        public void onUserEnter(final String userId) {
            LogUtils.w("Group---进入通话的用户有---" + userId + "");

        }


        //远端用户开启关闭摄像头
        @Override
        public void onUserVideoAvailable(final String userId, final boolean isVideoAvailable) {
            LogUtils.w("Group---远端用户摄像头状态---" + userId + "\n-----" + isVideoAvailable);


            if (true) {
                UserStatusDto groupAvCallDto4 = new UserStatusDto();
                groupAvCallDto4.setImId(userId);
                groupAvCallDto4.setOpenVideo(true);
                groupAvCallDto4.setUserNakeName("哈哈哈");
                groupAvCallDto4.setOpenVideoPermissions(true);
                groupAvCallDto4.setAnswer(true);
                TXCloudVideoView txCloudVideoView4 = new TXCloudVideoView(GroupVideoCallActivity.this);
                // mData.add(groupAvCallDto4);
                //mAdapter.notifyDataSetChanged();
            } else {


            }

        }


        //远端用户离开
        @Override
        public void onUserLeave(final String userId) {

        }

        //拒绝接听
        @Override
        public void onReject(final String userId) {

        }


        //无响应
        @Override
        public void onNoResp(final String userId) {

        }

        //忙线
        @Override
        public void onLineBusy(String userId) {

        }

        //用户取消
        @Override
        public void onCallingCancel() {

            finish();
        }

        //超时
        @Override
        public void onCallingTimeout() {

            finish();
        }

        //收到该回调说明本次通话结束了
        @Override
        public void onCallEnd() {
            finish();
        }


        //远端用户关闭麦克风
        @Override
        public void onUserAudioAvailable(String userId, boolean isVideoAvailable) {

        }

        // 用户说话音量回调
        @Override
        public void onUserVoiceVolume(Map<String, Integer> volumeMap) {

        }
    };


}