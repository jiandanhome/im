package com.eju.cy.audiovideo.controller;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eju.cy.audiovideo.TUIKit;
import com.eju.cy.audiovideo.base.IMEventListener;
import com.eju.cy.audiovideo.base.IUIKitCallBack;
import com.eju.cy.audiovideo.callback.StatisticActivityLifecycleCallback;
import com.eju.cy.audiovideo.dto.CsationStatusDto;
import com.eju.cy.audiovideo.dto.CustomMessage;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.dto.RoomDto;
import com.eju.cy.audiovideo.dto.SetGuoupStaticDto;
import com.eju.cy.audiovideo.dto.SigDto;
import com.eju.cy.audiovideo.dto.UpdateStatusDto;
import com.eju.cy.audiovideo.enumer.AppChannelEnmumer;
import com.eju.cy.audiovideo.enumer.AppTypeEnmumer;
import com.eju.cy.audiovideo.enumer.DelFriendTypeEnmumer;
import com.eju.cy.audiovideo.enumer.MoreCustomActionEnmumer;
import com.eju.cy.audiovideo.helper.ConfigHelper;
import com.eju.cy.audiovideo.helper.CustomAVCallUIController;
import com.eju.cy.audiovideo.helper.CustomGroupAVCallUIController;
import com.eju.cy.audiovideo.helper.CustomVideoCallUIController;
import com.eju.cy.audiovideo.modules.chat.ChatLayout;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.eju.cy.audiovideo.modules.message.MessageInfoUtil;
import com.eju.cy.audiovideo.net.AppNetInterface;
import com.eju.cy.audiovideo.net.RetrofitManager;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.tags.AppConfig;
import com.eju.cy.audiovideo.tags.Constants;
import com.eju.cy.audiovideo.trtcs.service.CallService;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.eju.cy.audiovideo.utils.ParameterUtils;
import com.eju.cy.audiovideo.utils.TUIKitLog;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.friendship.TIMFriend;
import com.tencent.imsdk.friendship.TIMFriendRequest;
import com.tencent.imsdk.friendship.TIMFriendResult;
import com.tencent.imsdk.session.SessionWrapper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * IM 控制类2
 *
 * @ Name: Caochen
 * @ Date: 2020-03-20
 * @ Time: 10:16
 * @ Description：
 */
public class EjuImController {

    private static final String TAG = EjuImController.class.getSimpleName();
    private static EjuImController mEjuImController;
    private WeakReference<Application> mWeakReference;
    private Application application;

    private String mUserToken, mUserId;
    public String mImId = "";
    int mRoomId = 0;

    private ChatLayout mUISender;
    private List<String> mC2CInputMoreCustomActionList = new ArrayList<>();//设置输入界面弹出更多显示

    private List<String> mGroupInputMoreCustomActionList = new ArrayList<>();//设置输入界面弹出更多显示

    //存放  ChatActivity  左侧  右侧显示
    //单聊
    private String mChatAtRightTitle = "";
    private int mChatAtRightIcon = 0;
    private int mChatAtLeftIcon = 0;


    //群聊
    private String mChatAtGroupRightTitle = "";
    private int mChatAtGroupRightIcon = 0;


    //存放单聊黄V
    private Map<String, String> vMap = new HashMap<>();


    private Boolean isdebug;


    //单例
    public static EjuImController getInstance() {

        if (mEjuImController == null) {
            synchronized (EjuImController.class) {
                if (mEjuImController == null) {
                    mEjuImController = new EjuImController();
                }
            }

        }
        return mEjuImController;
    }


    /**
     * @param application
     * @param imAppId
     * @param appType     应用类型  美房0，易楼1
     */
    public void initSDK(final Application application, int imAppId, int appType) {

        if (imAppId != 1400324570) {
            EjuImController.getInstance().setIsdebug(true);
        } else {
            EjuImController.getInstance().setIsdebug(false);
        }

        this.mWeakReference = new WeakReference<Application>(application);
        this.application = mWeakReference.get();
        AppConfig.appType = appType;

        if (SessionWrapper.isMainProcess(application)) {
            TUIKit.init(application, imAppId, new ConfigHelper().getConfigs(application));
            application.registerActivityLifecycleCallbacks(new StatisticActivityLifecycleCallback(application));
        }
        CustomAVCallUIController.getInstance().init(application);
        CustomVideoCallUIController.getInstance().init(application);
        CustomAVCallUIController.getInstance().onCreate();
        GroupController.getInstance().init(this.application);

        CustomGroupAVCallUIController.getInstance().init(this.application);

        C2CController.getInstance().init(this.application);
        //消息监听
        IMEventListener imEventListener = new IMEventListener() {
            @Override
            public void onNewMessages(List<TIMMessage> msgs) {
                LogUtils.w(TAG, "onNewMessagesinitSDK----");
//
//
                CustomMessage data = CustomMessage.convert2VideoCallData(msgs);
//                if (data != null) {
//                    //群视频
//                    if (!data.isAudioCall() && null != data.getInvited_user_list() && data.getInvited_user_list().size() > 0) {
//                        CustomGroupAVCallUIController.getInstance().onNewMessage(msgs);
//                    }
//                }
                if (null != data && data.isAudioCall()) {
//                //C2C语音
                    CustomAVCallUIController.getInstance().onNewMessage(msgs);
                }


                if (null != data && !data.isAudioCall()) {
                    //C2C视频
                    CustomVideoCallUIController.getInstance().onNewMessage(msgs);

                }

                //新群消息通知
                GroupController.getInstance().onNewMessage(msgs);
            }
        };
        TUIKit.addIMEventListener(imEventListener);


        initC2CInputMoreCustomActionList();
        initGroupInputMoreCustomActionList();
    }


    /**
     * 自动登陆
     *
     * @param userId            用户id
     * @param userToken         用户token
     * @param userPortraitUrl   userPortraitUrl   用户头像
     * @param userName          userName 用户名字
     * @param appTypeEnmumer    APP类型
     * @param appChannelEnmumer 通道类型
     * @param ejuImSdkCallBack  成功与否回调
     */
    public void autoLogin(final String userId, final String userToken, final String userPortraitUrl, final String userName, AppTypeEnmumer appTypeEnmumer, final AppChannelEnmumer appChannelEnmumer, final ImCallBack ejuImSdkCallBack) {

        RetrofitManager.getDefault().init(userId, userToken, appTypeEnmumer.value());

        final AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(application);
        httpInterface.genUserSig(ParameterUtils.prepareFormData(appChannelEnmumer.value()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SigDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SigDto sigDto) {

                        if (null != sigDto && "10000".equals(sigDto.getCode())) {

                            storeImUser(sigDto.getData().getIm_userId(), AppConfig.appType == 0 ? 0 : 1);
                            //设置全局保存参数
                            AppConfig.userSig = sigDto.getData().getUserSig();
                            AppConfig.sdkAppId = sigDto.getData().getSdkAppId();
                            AppConfig.appUserId = userId;
                            AppConfig.appImId = sigDto.getData().getIm_userId();

                            AppConfig.userPortraitUrl = userPortraitUrl;
                            AppConfig.appChannel = appChannelEnmumer.value();


                            mImId = sigDto.getData().getIm_userId();

                            TUIKit.login(sigDto.getData().getIm_userId(), sigDto.getData().getUserSig(), new IUIKitCallBack() {
                                @Override
                                public void onError(String module, final int code, final String desc) {

                                    LogUtils.w("im  login  onError--------");
                                    ejuImSdkCallBack.onError(code, desc);
                                }

                                @Override
                                public void onSuccess(Object data) {

                                    ejuImSdkCallBack.onSuccess(mImId);


                                    //TIMManager.getInstance().getLoginUser()
                                    SharedPreferences shareInfo = application.getSharedPreferences(Constants.USERINFO, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = shareInfo.edit();
                                    editor.putBoolean(Constants.AUTO_LOGIN, true);
                                    editor.commit();


                                    UserInfoController.getInstance().setUserNameAndUserPortraitUrl(userName, userPortraitUrl, new ImCallBack() {
                                        @Override
                                        public void onError(int var1, String var2) {

                                        }

                                        @Override
                                        public void onSuccess(Object var1) {
                                            LogUtils.w("设置用户名字-头像成功");
                                        }
                                    });


                                }
                            });
                        } else {
                            ejuImSdkCallBack.onError(888, sigDto.getMsg());
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        ejuImSdkCallBack.onError(888, " genUserSig onError---");
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    /**
     * @ Name: Caochen
     * @ Date: 2020-03-20
     * @ Time: 10:51
     * @ Description：
     * 用户登录IM
     */
    public void loginSDK(final String userId, final String userToken, AppTypeEnmumer appTypeEnmumer, final AppChannelEnmumer appChannelEnmumer, final ImCallBack ejuImSdkCallBack) {

        RetrofitManager.getDefault().init(userId, userToken, appTypeEnmumer.value());
        final AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(application);
        httpInterface.genUserSig(ParameterUtils.prepareFormData(appChannelEnmumer.value()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SigDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SigDto sigDto) {

                        if (null != sigDto && "10000".equals(sigDto.getCode())) {
                            storeImUser(sigDto.getData().getIm_userId(), AppConfig.appType == 0 ? 0 : 1);
                            //设置全局保存参数
                            AppConfig.userSig = sigDto.getData().getUserSig();
                            AppConfig.sdkAppId = sigDto.getData().getSdkAppId();
                            AppConfig.appUserId = userId;
                            AppConfig.appImId = sigDto.getData().getIm_userId();


                            AppConfig.appChannel = appChannelEnmumer.value();


                            mImId = sigDto.getData().getIm_userId();

                            TUIKit.login(sigDto.getData().getIm_userId(), sigDto.getData().getUserSig(), new IUIKitCallBack() {
                                @Override
                                public void onError(String module, final int code, final String desc) {


                                    ejuImSdkCallBack.onError(code, desc);
                                }

                                @Override
                                public void onSuccess(Object data) {

                                    ejuImSdkCallBack.onSuccess(data);


                                    mUserId = userId;
                                    mUserToken = userToken;

                                    //TIMManager.getInstance().getLoginUser()
                                    SharedPreferences shareInfo = application.getSharedPreferences(Constants.USERINFO, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = shareInfo.edit();
                                    editor.putBoolean(Constants.AUTO_LOGIN, true);
                                    editor.commit();


                                }
                            });
                        } else {
                            ejuImSdkCallBack.onError(888, " genUserSig onError---");
                        }

                    }


                    @Override
                    public void onError(Throwable e) {
                        ejuImSdkCallBack.onError(888, " genUserSig onError---");
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    /**
     * @ Name: Caochen
     * @ Date: 2020-03-20
     * @ Time: 10:58
     * @ Description：
     * 设置应用APP MainActivity
     */
    public void setAppMainActivity(Activity mainActivity) {

        // CustomAVCallUIController.getInstance().setActivityContext(mainActivity);

    }

    /**
     * @ Name: Caochen
     * @ Date: 2020-03-20
     * @ Time: 11:00
     * userId   发送方用户ID
     * userToken 发送方用户Token
     * type     项目 美房项目 请传：TypeState.JDM
     * userPortrait  发送方用户头像
     * userName   发送方用户用户名
     * <p>
     * othersUserId    受邀方用户ID
     * othersUserPortrait   受邀方用户头像
     * othersUserName  受邀方用户用户名
     * isAudioCall     是否 语音通话  语音通话请传true
     * @ Description： 创建语音通话请求
     */
    @SuppressLint("CheckResult")
    public void createAudioCallRequest(
            final String appType,
            final String userId,
            final String userToken,
            final String type,
            final String userPortrait,
            final String userName,

            final String othersUserId,
            final String othersUserPortrait,
            final String othersUserName,

            final boolean isAudioCall) {


        final AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(application);

        // String roomID = "0";
        httpInterface.getRoom(
                ParameterUtils.prepareFormData(appType),
                ParameterUtils.prepareFormData(userId),
                ParameterUtils.prepareFormData(othersUserId),
                ParameterUtils.prepareFormData("1")
        ).subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<RoomDto, ObservableSource<UpdateStatusDto>>() {
                    @Override
                    public ObservableSource<UpdateStatusDto> apply(RoomDto roomDto) throws Exception {

                        mRoomId = roomDto.getData().getId();

                        return httpInterface.updateTalkStatus(ParameterUtils.prepareFormData(roomDto.getData().getId() + ""), ParameterUtils.prepareFormData("1"));


                    }
                }).observeOn(Schedulers.io())
                .flatMap(new Function<UpdateStatusDto, ObservableSource<UpdateStatusDto>>() {
                    @Override
                    public ObservableSource<UpdateStatusDto> apply(UpdateStatusDto o) throws Exception {
                        return httpInterface.updateTalkStatus(ParameterUtils.prepareFormData(mRoomId + ""), ParameterUtils.prepareFormData("2"));
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UpdateStatusDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {


                    }

                    @Override
                    public void onNext(UpdateStatusDto updateStatusDto) {
                        if (null != updateStatusDto && "10000".equals(updateStatusDto.getCode())) {
                            //  CustomAVCallUIController.getInstance().createVideoCallRequest(userId, userPortrait, userName, othersUserId, othersUserPortrait, othersUserName, mRoomId, isAudioCall);
                        } else {
                            ToastUtils.showLong(updateStatusDto.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.w("错误" + e.toString());
                        ToastUtils.showLong("请稍后再试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }


    //修改房间状态
    public void updatTalk(String roomId, String room_talk) {

        AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(application);
        httpInterface.updateTalkStatus(ParameterUtils.prepareFormData(roomId),
                ParameterUtils.prepareFormData(room_talk)
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UpdateStatusDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UpdateStatusDto updateStatusDto) {
                        if (null != updateStatusDto && "10000".equals(updateStatusDto.getCode())) {
                            LogUtils.w("修改房间状态" + updateStatusDto.getMsg());
                        } else {
                            LogUtils.w("失败" + updateStatusDto.getMsg() + "---" + updateStatusDto.getCode());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }


    /**
     * @ Name: Caochen
     * @ Date: 2020-03-24
     * @ Time: 15:06
     * @ Description： 退出登陆
     */
    public void logInOut(final ImCallBack ejuImSdkCallBack) {


        TIMManager.getInstance().logout(new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                ejuImSdkCallBack.onError(code, desc);
            }

            @Override
            public void onSuccess() {
                ejuImSdkCallBack.onSuccess("Success");

                //停止接收视频听话监听
                Intent intent = new Intent(application, CallService.class);
                application.stopService(intent);
            }
        });
    }


    /**
     * 添加好友
     *
     * @param userImId
     * @param mAddWording
     * @param ejuImSdkCallBack
     */
    public void addFriend(String userImId, String mAddWording, final ImCallBack ejuImSdkCallBack) {


        TIMFriendRequest timFriendRequest = new TIMFriendRequest(userImId);
        timFriendRequest.setAddWording(mAddWording);
        timFriendRequest.setAddSource("android");//添加源
        TIMFriendshipManager.getInstance().addFriend(timFriendRequest, new TIMValueCallBack<TIMFriendResult>() {


                    @Override
                    public void onError(int i, String s) {
                        ejuImSdkCallBack.onError(i, s);
                    }

                    @Override
                    public void onSuccess(TIMFriendResult timFriendResult) {
                        ejuImSdkCallBack.onSuccess(timFriendResult);
                    }
                }
        );

    }

    /**
     * 删除好友
     *
     * @param listImId             删除用户ID列表
     * @param delFriendTypeEnmumer 单双方面删除
     * @param callBack             回调
     */
    public void deleteFriends(List<String> listImId, DelFriendTypeEnmumer delFriendTypeEnmumer, final ImCallBack callBack) {


        TIMFriendshipManager.getInstance().deleteFriends(listImId, delFriendTypeEnmumer.value(), new TIMValueCallBack<List<TIMFriendResult>>() {
            @Override
            public void onError(int i, String s) {
                callBack.onError(i, s);
            }

            @Override
            public void onSuccess(List<TIMFriendResult> timFriendResults) {
                callBack.onSuccess(timFriendResults);
            }
        });

    }


    /**
     * 获取联系人列表
     *
     * @param ejuImSdkCallBack
     */
    public void getFriendList(final ImCallBack ejuImSdkCallBack) {
        TIMFriendshipManager.getInstance().getFriendList(new TIMValueCallBack<List<TIMFriend>>() {
            @Override
            public void onError(int code, String desc) {
                TUIKitLog.e(TAG, "获取联系人列表 err code = " + code);
                ejuImSdkCallBack.onError(code, desc);
            }

            @Override
            public void onSuccess(List<TIMFriend> timFriends) {
                if (timFriends == null) {
                    timFriends = new ArrayList<>();
                }
                TUIKitLog.i(TAG, "获取联系人列表 success result = " + timFriends.size());

                ejuImSdkCallBack.onSuccess(timFriends);

            }
        });

    }


    /**
     * 设置C2C输入界面点击+号后更多面板弹出Action数量
     *
     * @param list
     */
    public void setC2CInputMoreCustomActionList(List<String> list) {

        this.mC2CInputMoreCustomActionList.clear();
        this.mC2CInputMoreCustomActionList.addAll(list);


    }

    /**
     * 获取C2C输入界面点击+号后更多面板弹出Action数量
     *
     * @return
     */
    public List<String> getC2CInputMoreCustomActionList() {

        return this.mC2CInputMoreCustomActionList;
    }

    /**
     * 初始化C2C输入界面点击+号后更多面板弹出Action数量
     */
    private void initC2CInputMoreCustomActionList() {
        this.mC2CInputMoreCustomActionList.clear();

        this.mC2CInputMoreCustomActionList.add(MoreCustomActionEnmumer.AUDIO_CALL.value());
        this.mC2CInputMoreCustomActionList.add(MoreCustomActionEnmumer.HOUSING_CALL.value());
        this.mC2CInputMoreCustomActionList.add(MoreCustomActionEnmumer.BUINESS_CARD_CALL.value());

        this.mC2CInputMoreCustomActionList.add(MoreCustomActionEnmumer.CAMERA_VIDEO_CALL.value());
        this.mC2CInputMoreCustomActionList.add(MoreCustomActionEnmumer.IMAGE_CALL.value());

        this.mC2CInputMoreCustomActionList.add(MoreCustomActionEnmumer.FILE_CALL.value());

    }


    /**
     * 设置Group输入界面点击输入界面点击+号后更多面板弹出Action数量
     *
     * @param list
     */
    public void setGroupInputMoreCustomActionList(List<String> list) {

        this.mGroupInputMoreCustomActionList.clear();
        this.mGroupInputMoreCustomActionList.addAll(list);


    }

    /**
     * 获取初始化Group输入界面点击输入界面点击+号后更多面板弹出Action数量
     *
     * @return
     */
    public List<String> getGroupInputMoreCustomActionList() {

        return this.mGroupInputMoreCustomActionList;
    }

    /**
     * 初始化Group输入界面点击+号后更多面板弹出Action数量
     */
    private void initGroupInputMoreCustomActionList() {
        this.mGroupInputMoreCustomActionList.clear();

        this.mGroupInputMoreCustomActionList.add(MoreCustomActionEnmumer.CAMERA_VIDEO_CALL.value());
        this.mGroupInputMoreCustomActionList.add(MoreCustomActionEnmumer.IMAGE_CALL.value());

        this.mGroupInputMoreCustomActionList.add(MoreCustomActionEnmumer.AUDIO_CALL.value());
        this.mGroupInputMoreCustomActionList.add(MoreCustomActionEnmumer.HOUSING_CALL.value());
        this.mGroupInputMoreCustomActionList.add(MoreCustomActionEnmumer.BUINESS_CARD_CALL.value());


    }


    /**
     * 设置ChatActivity  右侧 显示值
     *
     * @param title
     */
    public void setChatAtRightTitle(String title) {

        this.mChatAtRightTitle = title;
    }

    /**
     * 获取ChatActivity  右侧 显示值
     *
     * @return
     */
    public String getChatAtRightTitle() {

        return this.mChatAtRightTitle;
    }


    /**
     * 设置ChatActivity 单聊 左侧 image显示值
     *
     * @param icon
     */
    public void setChatAtLeftIcon(int icon) {

        this.mChatAtLeftIcon = icon;
    }

    /**
     * 获取ChatActivity 单聊 左侧image显示值 显示值
     *
     * @return
     */
    public int getChatAtLiftIcon() {

        return this.mChatAtLeftIcon;
    }


    /**
     * 设置ChatActivity 单聊 右侧 image显示值
     *
     * @param icon
     */
    public void setChatAtRightIcon(int icon) {

        this.mChatAtRightIcon = icon;
    }

    /**
     * 获取ChatActivity 单聊 右侧image显示值 显示值
     *
     * @return
     */
    public int getChatAtRightIcon() {

        return this.mChatAtRightIcon;
    }

    /**
     * 设置ChatActivity 群 右侧 显示值
     *
     * @param title
     */
    public void setChatAtGroupRightTitle(String title) {

        this.mChatAtGroupRightTitle = title;
    }

    /**
     * 获取ChatActivity 群聊 右侧 显示值
     *
     * @return
     */
    public String getChatAtGroupRightTitle() {

        return this.mChatAtGroupRightTitle;
    }


    /**
     * 设置ChatActivity 群聊 右侧 显示值 群聊
     *
     * @param icon
     */
    public void setChatAtGroupRightIcon(int icon) {

        this.mChatAtGroupRightIcon = icon;
    }

    /**
     * 获取ChatActivity 群聊 右侧 显示值
     *
     * @return
     */
    public int getmChatAtGroupRightIcon() {

        return this.mChatAtGroupRightIcon;
    }


    /**
     * 存储IM用户信息到服务器
     *
     * @param mImId IM通讯用户的IM ID
     */
    private void storeImUser(String mImId, int appType) {


        final AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(application);
        httpInterface.storeImUser(ParameterUtils.prepareFormData(mImId), ParameterUtils.prepareFormData("" + appType))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CsationStatusDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CsationStatusDto csationStatusDto) {

                        if (null != csationStatusDto && "10000".equals(csationStatusDto.getCode())) {
                            //  LogUtils.w("csationStatusDto------" + csationStatusDto);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public void setUISender(ChatLayout layout) {
        LogUtils.i(TAG, "setUISender: " + layout);
        this.mUISender = layout;


    }


    /**
     * C2C发送一条图片消息
     *
     * @param path           图片本地地址
     * @param conversationId 对方会话ID
     * @param imCallBack     回调
     * @deprecated 已废弃，请使用 C2CController中同名方法
     */
    public void sendImageMessage(Uri path, String conversationId, final ImCallBack imCallBack) {

        MessageInfo info = MessageInfoUtil.buildImageMessage(path, true);

        TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.C2C, conversationId);
        con.sendMessage(info.getTIMMessage(), new TIMValueCallBack<TIMMessage>() {

            @Override
            public void onError(int code, String desc) {
                LogUtils.w("sendMessage fail:" + code + "=" + desc);
                imCallBack.onError(code, desc);

            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                LogUtils.w("sendMessage onSuccess");
                imCallBack.onSuccess(timMessage);
            }
        });


    }


    /**
     * 全局设置用户是否免打扰
     *
     * @param disturb 是否设为免打扰 0：否 1：是
     */
    public void setDisturb(String disturb, final ImCallBack imCallBack) {
        final AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(application);
        httpInterface.setUserDisturb(ParameterUtils.prepareFormData(AppConfig.appImId),
                ParameterUtils.prepareFormData(disturb))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SetGuoupStaticDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SetGuoupStaticDto setGuoupStaticDto) {
                        if (null != setGuoupStaticDto && "10000".equals(setGuoupStaticDto.getCode())) {
                            //  LogUtils.w("csationStatusDto------" + csationStatusDto);
                            imCallBack.onSuccess(setGuoupStaticDto.toString());
                        } else {
                            imCallBack.onError(88888, setGuoupStaticDto.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        imCallBack.onError(88888, e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     * 关闭聊天界面
     */
    public void closeChatActivity() {

        ImActionDto imActionDto = new ImActionDto();
        imActionDto.setAction(ActionTags.CLOSE_CHAT_ACTIVITY);
        imActionDto.setJsonStr("closeChatActivity");


        String str = JsonUtils.toJson(imActionDto);
        EjuHomeImEventCar.getDefault().post(str);


    }

    /**
     * 设置黄V用户
     *
     * @param map
     */
    public void setvMap(Map<String, String> map) {
        if (null == vMap) {
            vMap = new HashMap<>();
        }
        vMap.clear();

        vMap.putAll(map);

    }

    /**
     * 获取黄V用户
     */
    public Map<String, String> getvMap() {

//        Map<String, String> map = new HashMap<>();
//        map.put("10211425", "1");
//        return map;
        return vMap;
    }

    /**
     * 清除黄V用户，这个方法是在聊天退出的时候
     */
    public void clearVmap() {
        if (null != vMap) {
            vMap.clear();
        }

    }


    public Boolean getIsdebug() {
        return isdebug;
    }

    public void setIsdebug(Boolean isdebug) {
        this.isdebug = isdebug;
    }

}
