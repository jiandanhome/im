package com.eju.cy.audiovideo.activity.chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.view.KeyEvent;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.TUIKit;
import com.eju.cy.audiovideo.base.BaseActivity;
import com.eju.cy.audiovideo.dialog.SelectCallDialog;
import com.eju.cy.audiovideo.dto.GroupAvNumberDto;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.modules.chat.base.ChatInfo;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.observer.EjuHomeImObserver;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.tags.AppConfig;
import com.eju.cy.audiovideo.tags.Constants;
import com.eju.cy.audiovideo.utils.CheckDoubleClick;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.imsdk.TIMConversationType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.reactivex.functions.Consumer;

/**
 * 单人聊天界面
 */
public class ChatActivity extends BaseActivity implements EjuHomeImObserver {

    private static final String TAG = ChatActivity.class.getSimpleName();

    private ChatFragment mChatFragment;
    private ChatInfo mChatInfo;
    private static final int REQ_PERMISSION_CODE = 0x100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        chat(getIntent());


        EjuHomeImEventCar.getDefault().register(this);
        ImmersionBar.with(this)
                .statusBarColor(AppConfig.appType == 0 ? R.color.white : R.color.status_bar_color)
                .statusBarDarkFont(true, 0.2f)
                .fitsSystemWindows(true)
                .keyboardEnable(true)
                .init();
        checkPermission(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        LogUtils.i(TAG, "onNewIntent");
        super.onNewIntent(intent);
        chat(intent);
    }

    @Override
    protected void onResume() {
        LogUtils.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EjuHomeImEventCar.getDefault().unregister(this);
    }

    private void chat(Intent intent) {
        Bundle bundle = intent.getExtras();
        LogUtils.i(TAG, "bundle: " + bundle + " intent: " + intent);
        if (bundle == null) {
            Uri uri = intent.getData();
            if (uri != null) {
                // 离线推送测试代码，oppo scheme url解析
                Set<String> set = uri.getQueryParameterNames();
                if (set != null) {
                    for (String key : set) {
                        String value = uri.getQueryParameter(key);
                        LogUtils.i(TAG, "oppo push scheme url key: " + key + " value: " + value);
                    }
                }
            }
            ToastUtils.showLong("无法打开当前对话");
            finish();
        } else {

            // 离线推送测试代码，华为和oppo可以通过在控制台设置打开应用内界面为ChatActivity来测试发送的ext数据
            String ext = bundle.getString("ext");
            LogUtils.i(TAG, "huawei push custom data ext: " + ext);

            Set<String> set = bundle.keySet();
            if (set != null) {
                for (String key : set) {
                    String value = bundle.getString(key);
                    LogUtils.i(TAG, "oppo push custom data key: " + key + " value: " + value);
                }
            }
            // 离线推送测试代码结束

            mChatInfo = (ChatInfo) bundle.getSerializable(Constants.CHAT_INFO);
            if (mChatInfo == null) {
                ToastUtils.showLong("无法打开当前对话");
                finish();
                return;
            }
            mChatFragment = new ChatFragment();
            mChatFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.empty_view, mChatFragment).commitAllowingStateLoss();
        }
    }


    /**
     * 用于收到解散  退群消息的 时候自动关闭activity
     *
     * @param obj
     */
    @Override
    public void action(Object obj) {

        if (null != obj) {

            String srt = (String) obj;

            ImActionDto imActionDto = JsonUtils.fromJson(srt, ImActionDto.class);

            switch (imActionDto.getAction()) {


                case ActionTags.QUIT_GROUP_SUCCESS:

                case ActionTags.DELETE_GROUP_SUCCESS:

                case ActionTags.CLOSE_CHAT_ACTIVITY:

                    finish();

                    break;
                case ActionTags.ACTION_CLICK_C2C_VOICEL:
                    setPermission();
                    break;

            }


        }


    }


    //权限检查
    public boolean checkPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TUIKit.getAppContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TUIKit.getAppContext(), Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TUIKit.getAppContext(), Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }

            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TUIKit.getAppContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
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

    /**
     * 检测权限
     */
    @SuppressLint("CheckResult")
    private void setPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RxPermissions rxPermissions = new RxPermissions(ChatActivity.this);

            rxPermissions.request(Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            )
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean) {
                                voiceCall();

                            } else {

                                ToastUtils.showLong("权限未授予，该功能不可使用");
                            }
                        }
                    });

        } else {
            voiceCall();
        }
    }


    /**
     * C2C OR   GROUP
     */
    private void voiceCall() {


        if (CheckDoubleClick.isFastDoubleClick()) {
            return;
        }
        LogUtils.w("去请求几次------");

        if (null != mChatFragment.getChatLayout() && mChatFragment.getChatLayout().getChatInfo().getType() == TIMConversationType.C2C) {
            SelectCallDialog announcementDialog = new SelectCallDialog(this);
            announcementDialog.builder().setCancelable(true).setCancelOutside(false).setDialogWidth(1f)
                    .setTvValue("dsdsd");

            announcementDialog.setNegativeButton(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            announcementDialog.show();


        } else if (null != mChatFragment.getChatLayout() && mChatFragment.getChatLayout().getChatInfo().getType() == TIMConversationType.Group) {

            GroupAvNumberDto groupAvNumberDto = new GroupAvNumberDto();

            List<String> allUseList = new ArrayList<>();
            groupAvNumberDto.setAllUseList(allUseList);
            groupAvNumberDto.setOpenVideoList(allUseList);
            groupAvNumberDto.setGroupId(mChatFragment.getChatLayout().getChatInfo().getId());

            String groupAvNumberDtoStr = JsonUtils.toJson(groupAvNumberDto);
            LogUtils.w("groupAvNumberDtoStr-------" + groupAvNumberDtoStr);


            ImActionDto imActionDto = new ImActionDto();
            imActionDto.setAction(ActionTags.GROUP_VIDEO_CALL);
            imActionDto.setJsonStr(groupAvNumberDtoStr);//告诉服务端当前用户
            String voiceCall = JsonUtils.toJson(imActionDto);
            EjuHomeImEventCar.getDefault().post(voiceCall);

        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != mChatFragment && null != mChatFragment.getChatLayout() && null != mChatFragment.getChatLayout().getAitManager()) {
            mChatFragment.getChatLayout().getAitManager().onActivityResult(requestCode, resultCode, data);
        }

    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            LogUtils.w("返回事件");
            mChatFragment.deleteMsg();
            //不执行父类点击事件
            return true;
        }
        //继续执行父类其他点击事件
        return super.onKeyUp(keyCode, event);
    }
}
