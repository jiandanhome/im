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
 * IM ?????????2
 *
 * @ Name: Caochen
 * @ Date: 2020-03-20
 * @ Time: 10:16
 * @ Description???
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
    private List<String> mC2CInputMoreCustomActionList = new ArrayList<>();//????????????????????????????????????

    private List<String> mGroupInputMoreCustomActionList = new ArrayList<>();//????????????????????????????????????

    //??????  ChatActivity  ??????  ????????????
    //??????
    private String mChatAtRightTitle = "";
    private int mChatAtRightIcon = 0;
    private int mChatAtLeftIcon = 0;


    //??????
    private String mChatAtGroupRightTitle = "";
    private int mChatAtGroupRightIcon = 0;


    //???????????????V
    private Map<String, String> vMap = new HashMap<>();


    private Boolean isdebug;


    //??????
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
     * @param appType     ????????????  ??????0?????????1
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
        //????????????
        IMEventListener imEventListener = new IMEventListener() {
            @Override
            public void onNewMessages(List<TIMMessage> msgs) {
                LogUtils.w(TAG, "onNewMessagesinitSDK----");
//
//
                CustomMessage data = CustomMessage.convert2VideoCallData(msgs);
//                if (data != null) {
//                    //?????????
//                    if (!data.isAudioCall() && null != data.getInvited_user_list() && data.getInvited_user_list().size() > 0) {
//                        CustomGroupAVCallUIController.getInstance().onNewMessage(msgs);
//                    }
//                }
                if (null != data && data.isAudioCall()) {
//                //C2C??????
                    CustomAVCallUIController.getInstance().onNewMessage(msgs);
                }


                if (null != data && !data.isAudioCall()) {
                    //C2C??????
                    CustomVideoCallUIController.getInstance().onNewMessage(msgs);

                }

                //??????????????????
                GroupController.getInstance().onNewMessage(msgs);
            }
        };
        TUIKit.addIMEventListener(imEventListener);


        initC2CInputMoreCustomActionList();
        initGroupInputMoreCustomActionList();
    }


    /**
     * ????????????
     *
     * @param userId            ??????id
     * @param userToken         ??????token
     * @param userPortraitUrl   userPortraitUrl   ????????????
     * @param userName          userName ????????????
     * @param appTypeEnmumer    APP??????
     * @param appChannelEnmumer ????????????
     * @param ejuImSdkCallBack  ??????????????????
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
                            //????????????????????????
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
                                            LogUtils.w("??????????????????-????????????");
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
     * @ Description???
     * ????????????IM
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
                            //????????????????????????
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
     * @ Description???
     * ????????????APP MainActivity
     */
    public void setAppMainActivity(Activity mainActivity) {

        // CustomAVCallUIController.getInstance().setActivityContext(mainActivity);

    }

    /**
     * @ Name: Caochen
     * @ Date: 2020-03-20
     * @ Time: 11:00
     * userId   ???????????????ID
     * userToken ???????????????Token
     * type     ?????? ???????????? ?????????TypeState.JDM
     * userPortrait  ?????????????????????
     * userName   ????????????????????????
     * <p>
     * othersUserId    ???????????????ID
     * othersUserPortrait   ?????????????????????
     * othersUserName  ????????????????????????
     * isAudioCall     ?????? ????????????  ??????????????????true
     * @ Description??? ????????????????????????
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
                        LogUtils.w("??????" + e.toString());
                        ToastUtils.showLong("???????????????");
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }


    //??????????????????
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
                            LogUtils.w("??????????????????" + updateStatusDto.getMsg());
                        } else {
                            LogUtils.w("??????" + updateStatusDto.getMsg() + "---" + updateStatusDto.getCode());
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
     * @ Description??? ????????????
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

                //??????????????????????????????
                Intent intent = new Intent(application, CallService.class);
                application.stopService(intent);
            }
        });
    }


    /**
     * ????????????
     *
     * @param userImId
     * @param mAddWording
     * @param ejuImSdkCallBack
     */
    public void addFriend(String userImId, String mAddWording, final ImCallBack ejuImSdkCallBack) {


        TIMFriendRequest timFriendRequest = new TIMFriendRequest(userImId);
        timFriendRequest.setAddWording(mAddWording);
        timFriendRequest.setAddSource("android");//?????????
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
     * ????????????
     *
     * @param listImId             ????????????ID??????
     * @param delFriendTypeEnmumer ??????????????????
     * @param callBack             ??????
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
     * ?????????????????????
     *
     * @param ejuImSdkCallBack
     */
    public void getFriendList(final ImCallBack ejuImSdkCallBack) {
        TIMFriendshipManager.getInstance().getFriendList(new TIMValueCallBack<List<TIMFriend>>() {
            @Override
            public void onError(int code, String desc) {
                TUIKitLog.e(TAG, "????????????????????? err code = " + code);
                ejuImSdkCallBack.onError(code, desc);
            }

            @Override
            public void onSuccess(List<TIMFriend> timFriends) {
                if (timFriends == null) {
                    timFriends = new ArrayList<>();
                }
                TUIKitLog.i(TAG, "????????????????????? success result = " + timFriends.size());

                ejuImSdkCallBack.onSuccess(timFriends);

            }
        });

    }


    /**
     * ??????C2C??????????????????+????????????????????????Action??????
     *
     * @param list
     */
    public void setC2CInputMoreCustomActionList(List<String> list) {

        this.mC2CInputMoreCustomActionList.clear();
        this.mC2CInputMoreCustomActionList.addAll(list);


    }

    /**
     * ??????C2C??????????????????+????????????????????????Action??????
     *
     * @return
     */
    public List<String> getC2CInputMoreCustomActionList() {

        return this.mC2CInputMoreCustomActionList;
    }

    /**
     * ?????????C2C??????????????????+????????????????????????Action??????
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
     * ??????Group????????????????????????????????????+????????????????????????Action??????
     *
     * @param list
     */
    public void setGroupInputMoreCustomActionList(List<String> list) {

        this.mGroupInputMoreCustomActionList.clear();
        this.mGroupInputMoreCustomActionList.addAll(list);


    }

    /**
     * ???????????????Group????????????????????????????????????+????????????????????????Action??????
     *
     * @return
     */
    public List<String> getGroupInputMoreCustomActionList() {

        return this.mGroupInputMoreCustomActionList;
    }

    /**
     * ?????????Group??????????????????+????????????????????????Action??????
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
     * ??????ChatActivity  ?????? ?????????
     *
     * @param title
     */
    public void setChatAtRightTitle(String title) {

        this.mChatAtRightTitle = title;
    }

    /**
     * ??????ChatActivity  ?????? ?????????
     *
     * @return
     */
    public String getChatAtRightTitle() {

        return this.mChatAtRightTitle;
    }


    /**
     * ??????ChatActivity ?????? ?????? image?????????
     *
     * @param icon
     */
    public void setChatAtLeftIcon(int icon) {

        this.mChatAtLeftIcon = icon;
    }

    /**
     * ??????ChatActivity ?????? ??????image????????? ?????????
     *
     * @return
     */
    public int getChatAtLiftIcon() {

        return this.mChatAtLeftIcon;
    }


    /**
     * ??????ChatActivity ?????? ?????? image?????????
     *
     * @param icon
     */
    public void setChatAtRightIcon(int icon) {

        this.mChatAtRightIcon = icon;
    }

    /**
     * ??????ChatActivity ?????? ??????image????????? ?????????
     *
     * @return
     */
    public int getChatAtRightIcon() {

        return this.mChatAtRightIcon;
    }

    /**
     * ??????ChatActivity ??? ?????? ?????????
     *
     * @param title
     */
    public void setChatAtGroupRightTitle(String title) {

        this.mChatAtGroupRightTitle = title;
    }

    /**
     * ??????ChatActivity ?????? ?????? ?????????
     *
     * @return
     */
    public String getChatAtGroupRightTitle() {

        return this.mChatAtGroupRightTitle;
    }


    /**
     * ??????ChatActivity ?????? ?????? ????????? ??????
     *
     * @param icon
     */
    public void setChatAtGroupRightIcon(int icon) {

        this.mChatAtGroupRightIcon = icon;
    }

    /**
     * ??????ChatActivity ?????? ?????? ?????????
     *
     * @return
     */
    public int getmChatAtGroupRightIcon() {

        return this.mChatAtGroupRightIcon;
    }


    /**
     * ??????IM????????????????????????
     *
     * @param mImId IM???????????????IM ID
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
     * C2C????????????????????????
     *
     * @param path           ??????????????????
     * @param conversationId ????????????ID
     * @param imCallBack     ??????
     * @deprecated ????????????????????? C2CController???????????????
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
     * ?????????????????????????????????
     *
     * @param disturb ????????????????????? 0?????? 1??????
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
     * ??????????????????
     */
    public void closeChatActivity() {

        ImActionDto imActionDto = new ImActionDto();
        imActionDto.setAction(ActionTags.CLOSE_CHAT_ACTIVITY);
        imActionDto.setJsonStr("closeChatActivity");


        String str = JsonUtils.toJson(imActionDto);
        EjuHomeImEventCar.getDefault().post(str);


    }

    /**
     * ?????????V??????
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
     * ?????????V??????
     */
    public Map<String, String> getvMap() {

//        Map<String, String> map = new HashMap<>();
//        map.put("10211425", "1");
//        return map;
        return vMap;
    }

    /**
     * ?????????V????????????????????????????????????????????????
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
