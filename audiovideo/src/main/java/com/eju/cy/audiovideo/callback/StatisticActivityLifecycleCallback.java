package com.eju.cy.audiovideo.callback;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.eju.cy.audiovideo.TUIKit;
import com.eju.cy.audiovideo.base.IMEventListener;
import com.eju.cy.audiovideo.dto.CustomMessage;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.trtcs.service.CallService;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.tencent.imsdk.TIMBackgroundParam;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMOfflinePushNotification;

import java.util.List;

public class StatisticActivityLifecycleCallback implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = StatisticActivityLifecycleCallback.class.getSimpleName();
    private int foregroundActivities = 0;
    private boolean isChangingConfiguration;
    private Application application;

    public StatisticActivityLifecycleCallback(Application application) {
        this.application = application;
    }

    private IMEventListener mIMEventListener = new IMEventListener() {
        @Override
        public void onNewMessages(List<TIMMessage> msgs) {
            if (CustomMessage.convert2VideoCallData(msgs) != null) {
                // 会弹出接电话的对话框，不再需要通知
                return;
            }
            for (TIMMessage msg : msgs) {
                // 小米手机需要在设置里面把demo的"后台弹出权限"打开才能点击Notification跳转。TIMOfflinePushNotification后续不再维护，如有需要，建议应用自己调用系统api弹通知栏消息。
                TIMOfflinePushNotification notification = new TIMOfflinePushNotification(application, msg);
                //  notification.doNotify(application, R.drawable.default_user_icon);
            }
        }
    };

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        LogUtils.w(TAG, "onActivityCreated bundle: " + bundle);
        if (bundle != null) { // 若bundle不为空则程序异常结束
            // 重启整个程序
//                Intent intent = new Intent(activity, SplashActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
            LogUtils.w(TAG, "程序异常需要重启");
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        foregroundActivities++;
        if (foregroundActivities == 1 && !isChangingConfiguration) {
            // 应用切到前台
            LogUtils.w(TAG, "application enter foreground");
            TIMManager.getInstance().doForeground(new TIMCallBack() {
                @Override
                public void onError(int code, String desc) {
                    LogUtils.w(TAG, "doForeground err = " + code + ", desc = " + desc);
                }

                @Override
                public void onSuccess() {
                    LogUtils.w(TAG, "doForeground success");
                }
            });
            TUIKit.removeIMEventListener(mIMEventListener);


            LogUtils.w("前台------");

            ImActionDto imActionDto = new ImActionDto();
            imActionDto.setAction(ActionTags.APP_THE_FRONT_DESK);
            String actionStr = JsonUtils.toJson(imActionDto);
            EjuHomeImEventCar.getDefault().post(actionStr);

        }
        isChangingConfiguration = false;


    }

    @Override
    public void onActivityResumed(Activity activity) {
        //登陆了就启动接收视频的service
        //登陆了就启动接收视频的service
        //登陆了就启动接收视频的service
        if (TIMManager.getInstance().getLoginStatus() == TIMManager.TIM_STATUS_LOGINED
                && !ServiceUtils.isServiceRunning(CallService.class)) {
            Intent intent = new Intent(application, CallService.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                application.startForegroundService(intent);
            } else {
                application.startService(intent);
            }
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        foregroundActivities--;
        if (foregroundActivities == 0) {
            // 应用切到后台
            LogUtils.w(TAG, "application enter background");
            int unReadCount = 0;
            List<TIMConversation> conversationList = TIMManager.getInstance().getConversationList();
            for (TIMConversation timConversation : conversationList) {
                unReadCount += timConversation.getUnreadMessageNum();
            }
            TIMBackgroundParam param = new TIMBackgroundParam();
            param.setC2cUnread(unReadCount);
            TIMManager.getInstance().doBackground(param, new TIMCallBack() {
                @Override
                public void onError(int code, String desc) {
                    LogUtils.w(TAG, "doBackground err = " + code + ", desc = " + desc);
                }

                @Override
                public void onSuccess() {
                    LogUtils.w(TAG, "doBackground success");
                }
            });
            // 应用退到后台，消息转化为系统通知

            LogUtils.w("后台------");

            ImActionDto imActionDto = new ImActionDto();
            imActionDto.setAction(ActionTags.APP_THE_BACKGROUND);
            String actionStr = JsonUtils.toJson(imActionDto);
            EjuHomeImEventCar.getDefault().post(actionStr);
        }
        isChangingConfiguration = activity.isChangingConfigurations();


        TUIKit.addIMEventListener(mIMEventListener);


    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}