package com.eju.cy.audiovideo.controller;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.activity.chat.ChatActivity;
import com.eju.cy.audiovideo.activity.trtc.TRTCVideoCallActivity;
import com.eju.cy.audiovideo.base.IUIKitCallBack;
import com.eju.cy.audiovideo.dto.CsationStatusDto;
import com.eju.cy.audiovideo.dto.CustomContentDto;
import com.eju.cy.audiovideo.dto.CustomMessage;
import com.eju.cy.audiovideo.dto.GroupAvNumberDto;
import com.eju.cy.audiovideo.dto.GroupListWithKeyDto;
import com.eju.cy.audiovideo.dto.GuoupMemberDto;
import com.eju.cy.audiovideo.dto.GuoupStaticDto;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.dto.InviteGroupMemberDto;
import com.eju.cy.audiovideo.dto.MyGroupDto;
import com.eju.cy.audiovideo.dto.SetGuoupStaticDto;
import com.eju.cy.audiovideo.dto.UserLevelDto;
import com.eju.cy.audiovideo.enumer.GroupAddOptEnmunmer;
import com.eju.cy.audiovideo.enumer.GroupMemberRoleTypeEnmunmer;
import com.eju.cy.audiovideo.enumer.GroupTypeEnmumer;
import com.eju.cy.audiovideo.modules.chat.GroupChatManagerKit;
import com.eju.cy.audiovideo.modules.chat.base.ChatInfo;
import com.eju.cy.audiovideo.modules.conversation.base.ConversationInfo;
import com.eju.cy.audiovideo.modules.group.apply.GroupApplyInfo;
import com.eju.cy.audiovideo.modules.group.info.GroupInfo;
import com.eju.cy.audiovideo.modules.group.info.GroupInfoProvider;
import com.eju.cy.audiovideo.modules.group.member.GroupMemberInfo;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.eju.cy.audiovideo.modules.message.MessageInfoUtil;
import com.eju.cy.audiovideo.net.AppNetInterface;
import com.eju.cy.audiovideo.net.RetrofitManager;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.tags.AppConfig;
import com.eju.cy.audiovideo.tags.Constants;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.eju.cy.audiovideo.utils.ParameterUtils;
import com.eju.cy.audiovideo.utils.ToastUtil;
import com.google.gson.Gson;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMGroupAddOpt;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMGroupSystemElem;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.group.TIMGroupBaseInfo;
import com.tencent.imsdk.ext.group.TIMGroupDetailInfoResult;
import com.tencent.imsdk.ext.group.TIMGroupMemberResult;
import com.tencent.imsdk.ext.group.TIMGroupMemberRoleFilter;
import com.tencent.imsdk.ext.group.TIMGroupMemberSucc;
import com.tencent.imsdk.ext.group.TIMGroupPendencyGetParam;
import com.tencent.imsdk.ext.group.TIMGroupPendencyItem;
import com.tencent.imsdk.ext.group.TIMGroupPendencyListGetSucc;
import com.tencent.imsdk.ext.group.TIMGroupSelfInfo;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMGroupApplication;
import com.tencent.imsdk.v2.V2TIMGroupApplicationResult;
import com.tencent.imsdk.v2.V2TIMGroupMemberInfoResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @ Name: Caochen
 * @ Date: 2020-05-07
 * @ Time: 16:02
 * @ Description??? ????????????
 */
public class GroupController {


    private static GroupController instance;
    private String notification = "";//?????????
    private String notificationTag = "";//???????????????
    private Application application;

    private Map<String, String> groupRoleMap = new HashMap<>();

    public static GroupController getInstance() {
        if (instance == null) {
            synchronized (GroupController.class) {
                if (instance == null) {
                    instance = new GroupController();
                }
            }
        }
        return instance;
    }


    private void test() {
        List<TIMGroupMemberInfo> infos = new ArrayList<TIMGroupMemberInfo>();
        TIMGroupMemberInfo member = new TIMGroupMemberInfo("cat");
        infos.add(member);
//        createGroup("????????????", "abcdefg", TIMGroupAddOpt.TIM_GROUP_ADD_AUTH, infos, new ImCallBack() {
//            @Override
//            public void onError(int var1, String var2) {
//
//            }
//
//            @Override
//            public void onSuccess(Object var1) {
//
//            }
//
//
//        });

    }


    public void init(Application application) {
        this.application = application;
    }

    /**
     * ????????????
     *
     * @param groupName           ????????????
     * @param groupId             ??????ID
     * @param groupMemberInfoList ???????????? id
     * @param groupTypeEnmumer    ????????????
     * @param groupAddOptEnmunmer ??????????????????
     * @param imCallBack          ????????????
     */

    public void createGroup(String groupName, String groupId, List<String> groupMemberInfoList, GroupTypeEnmumer groupTypeEnmumer, GroupAddOptEnmunmer groupAddOptEnmunmer, final ImCallBack imCallBack) {

        //???????????????????????????
        TIMGroupManager.CreateGroupParam param = new TIMGroupManager.CreateGroupParam(groupTypeEnmumer.value(), groupName);
        //????????????
        int addAddOpt = (int) groupAddOptEnmunmer.getValue();

        LogUtils.w("addAddOpt---------" + TIMGroupAddOpt.values()[addAddOpt]);
        param.setAddOption(TIMGroupAddOpt.values()[addAddOpt]);
        //??????ID
        param.setGroupId(groupId);

        //?????????---??????id????????????
        List<TIMGroupMemberInfo> infos = new ArrayList<>();
        for (int i = 0; i < groupMemberInfoList.size(); i++) {
            TIMGroupMemberInfo memberInfo = new TIMGroupMemberInfo(AppConfig.appChannel + groupMemberInfoList.get(i));
            infos.add(memberInfo);
        }

        param.setMembers(infos);


        TIMGroupManager.getInstance().createGroup(param, new TIMValueCallBack<String>() {
            @Override
            public void onError(final int code, final String desc) {
                imCallBack.onError(code, desc);
                LogUtils.e("?????????????????? failed, code: " + code + "|desc: " + desc);
            }

            @Override
            public void onSuccess(final String groupId) {
                LogUtils.e("??????????????????" + groupId);
                String message = TIMManager.getInstance().getLoginUser() + "????????????";
                final TIMMessage createTips = MessageInfoUtil.buildGroupCustomMessage(MessageInfoUtil.GROUP_CREATE, message);
                TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, groupId);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //????????????????????????
                sendTipsMessage(conversation, createTips, new ImCallBack() {

                    @Override
                    public void onError(int var1, String var2) {
                        imCallBack.onError(var1, var2);
                    }

                    @Override
                    public void onSuccess(Object var1) {

                        imCallBack.onSuccess(groupId);
                    }
                });
            }
        });

    }


    /**
     * ????????????
     *
     * @param groupName           ????????????
     * @param groupId             ??????ID
     * @param groupMemberInfoList ???????????? id
     * @param groupTypeEnmumer    ????????????
     * @param groupAddOptEnmunmer ??????????????????
     * @param imCallBack          ????????????
     */

    public void createGroupForMember(String groupName, String groupId, List<GroupMemberInfo> groupMemberInfoList, GroupTypeEnmumer groupTypeEnmumer, GroupAddOptEnmunmer groupAddOptEnmunmer, final ImCallBack imCallBack) {

        //???????????????????????????
        TIMGroupManager.CreateGroupParam param = new TIMGroupManager.CreateGroupParam(groupTypeEnmumer.value(), groupName);
        //????????????
        int addAddOpt = (int) groupAddOptEnmunmer.getValue();

        LogUtils.w("addAddOpt---------" + TIMGroupAddOpt.values()[addAddOpt]);
        param.setAddOption(TIMGroupAddOpt.values()[addAddOpt]);
        //??????ID
        param.setGroupId(groupId);

        //?????????
        List<TIMGroupMemberInfo> infos = new ArrayList<>();
        for (int i = 0; i < groupMemberInfoList.size(); i++) {
            TIMGroupMemberInfo memberInfo = new TIMGroupMemberInfo(groupMemberInfoList.get(i).getAccount());
            infos.add(memberInfo);
        }
        param.setMembers(infos);


        TIMGroupManager.getInstance().createGroup(param, new TIMValueCallBack<String>() {
            @Override
            public void onError(final int code, final String desc) {
                imCallBack.onError(code, desc);
                LogUtils.e("?????????????????? failed, code: " + code + "|desc: " + desc);
            }

            @Override
            public void onSuccess(final String groupId) {
                LogUtils.e("??????????????????" + groupId);
                String message = TIMManager.getInstance().getLoginUser() + "????????????";
                final TIMMessage createTips = MessageInfoUtil.buildGroupCustomMessage(MessageInfoUtil.GROUP_CREATE, message);
                TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, groupId);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //????????????????????????
                sendTipsMessage(conversation, createTips, new ImCallBack() {

                    @Override
                    public void onError(int var1, String var2) {
                        imCallBack.onError(var1, var2);
                    }

                    @Override
                    public void onSuccess(Object var1) {

                        imCallBack.onSuccess(groupId);
                    }
                });
            }
        });

    }


    /**
     * ??????????????????
     *
     * @param groupId    ??????ID
     * @param groupUrl   ????????????
     * @param imCallBack ????????????
     */
    public void setGroupFaceUrl(String groupId, String groupUrl, final ImCallBack imCallBack) {


        TIMGroupManager.ModifyGroupInfoParam param = new TIMGroupManager.ModifyGroupInfoParam(groupId);
        param.setFaceUrl(groupUrl);
        TIMGroupManager.getInstance().modifyGroupInfo(param, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                LogUtils.w("modify group icon failed, code:" + code + "|desc:" + desc);
                imCallBack.onError(code, desc);
            }

            @Override
            public void onSuccess() {
                imCallBack.onSuccess("success");
            }
        });


    }


    /**
     * ??????????????????
     *
     * @param groupId    ??????ID
     * @param groupName  ????????????
     * @param imCallBack ????????????
     */
    public void setGroupName(String groupId, final String groupName, final ImCallBack imCallBack) {


        TIMGroupManager.ModifyGroupInfoParam param = new TIMGroupManager.ModifyGroupInfoParam(groupId);
        param.setGroupName(groupName);
        TIMGroupManager.getInstance().modifyGroupInfo(param, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                LogUtils.w("modify group icon failed, code:" + code + "|desc:" + desc);
                imCallBack.onError(code, desc);
            }

            @Override
            public void onSuccess() {
                imCallBack.onSuccess("success");

                //ChatLayout  ?????????????????????
                ImActionDto imActionDto = new ImActionDto();
                imActionDto.setAction(ActionTags.UPDATE_GUOUP_NAME);
                imActionDto.setJsonStr(groupName);

                String str = JsonUtils.toJson(imActionDto);

                EjuHomeImEventCar.getDefault().post(str);

            }
        });


    }


    /**
     * ???????????????
     *
     * @param groupId         ??????ID
     * @param setNotification ????????????
     * @param imCallBack      ????????????
     */
    public void setNotification(String groupId, String setNotification, final ImCallBack imCallBack) {


        TIMGroupManager.ModifyGroupInfoParam param = new TIMGroupManager.ModifyGroupInfoParam(groupId);
        param.setNotification(setNotification);
        TIMGroupManager.getInstance().modifyGroupInfo(param, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                LogUtils.w("modify group icon failed, code:" + code + "|desc:" + desc);
                imCallBack.onError(code, desc);
            }

            @Override
            public void onSuccess() {
                imCallBack.onSuccess("success");
            }
        });
    }


    /**
     * ???????????????
     *
     * @param groupId    ??????ID
     * @param Introduce  ????????????
     * @param imCallBack ????????????
     */
    public void setGuoupIntroduce(String groupId, String Introduce, final ImCallBack imCallBack) {


        TIMGroupManager.ModifyGroupInfoParam param = new TIMGroupManager.ModifyGroupInfoParam(groupId);
        param.setIntroduction(Introduce);

        TIMGroupManager.getInstance().modifyGroupInfo(param, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                LogUtils.w("modify group icon failed, code:" + code + "|desc:" + desc);
                imCallBack.onError(code, desc);
            }

            @Override
            public void onSuccess() {
                imCallBack.onSuccess("success");
            }
        });
    }


    /**
     * ????????????????????????(??????????????????)
     *
     * @param groupId          ??????ID
     * @param myTimGroupAddOpt ????????????
     * @param imCallBack
     */
    public void setAddGroupOpt(String groupId, final TIMGroupAddOpt myTimGroupAddOpt, final ImCallBack imCallBack) {

        TIMGroupManager.ModifyGroupInfoParam param = new TIMGroupManager.ModifyGroupInfoParam(groupId);


        switch (myTimGroupAddOpt) {

            case TIM_GROUP_ADD_ANY:
                param.setAddOption(TIMGroupAddOpt.TIM_GROUP_ADD_ANY);
                break;
            case TIM_GROUP_ADD_AUTH:
                param.setAddOption(TIMGroupAddOpt.TIM_GROUP_ADD_AUTH);
                break;
            case TIM_GROUP_ADD_FORBID:
                param.setAddOption(TIMGroupAddOpt.TIM_GROUP_ADD_FORBID);
                break;

        }


        TIMGroupManager.getInstance().modifyGroupInfo(param, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                imCallBack.onError(code, desc);
            }

            @Override
            public void onSuccess() {
                imCallBack.onSuccess("success");
            }
        });
    }


//    /**
//     * ???????????????????????????
//     *
//     * @param groupId ??????ID
//     * @param flag    ????????????
//     */
//    public void setNotification(String groupId, boolean flag) {
//        ConversationManagerKit.getInstance().setConversationTop(groupId, flag);
//
//    }


    public void deleteGroup(String groupId) {


    }

    /**
     * ??????????????????
     *
     * @param groupId    ??????ID
     * @param memList    ?????? ID ??????
     * @param imCallBack ????????????---List<TIMGroupMemberResult>
     */
    public void inviteGroupMember(String groupId, @NonNull List<String> memList, final ImCallBack imCallBack) {
        TIMGroupManager.getInstance().inviteGroupMember(groupId, memList, new TIMValueCallBack<List<TIMGroupMemberResult>>() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess(List<TIMGroupMemberResult> timGroupMemberResults) {
                imCallBack.onSuccess(timGroupMemberResults);
            }
        });
    }


    /**
     * ??????????????????
     *
     * @param groupId     ??????ID
     * @param im_users_id IM????????????Id???????????????,?????? ???101,1017,1023???
     * @param imCallBack  ??????
     */
    public void inviteGroupMember(String groupId, @NonNull String im_users_id, final ImCallBack imCallBack) {
        final AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(application);

        httpInterface.inviteGroupMember(ParameterUtils.prepareFormData(groupId),
                ParameterUtils.prepareFormData(im_users_id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<InviteGroupMemberDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(InviteGroupMemberDto inviteGroupMemberDto) {

                        if (null != inviteGroupMemberDto && "10000".equals(inviteGroupMemberDto.getCode())) {
                            imCallBack.onSuccess(inviteGroupMemberDto);
                        } else {
                            imCallBack.onError(888, inviteGroupMemberDto.toString());
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        imCallBack.onError(888, e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    /**
     * ??????????????????
     *
     * @param groupId    ??????ID
     * @param reason     ????????????
     * @param imCallBack ????????????
     */
    public void applyJoinGroup(String groupId, @NonNull String reason, final ImCallBack imCallBack) {

        TIMGroupManager.getInstance().applyJoinGroup(groupId, reason, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess() {
                imCallBack.onSuccess("success");
            }
        });

    }

    /**
     * ????????????
     * <p>
     * ????????????????????????????????????
     * ?????????????????????????????????????????????????????????
     *
     * @param groupId    ??????ID
     * @param imCallBack ????????????
     */
    public void quitGroup(String groupId, final ImCallBack imCallBack) {
        TIMGroupManager.getInstance().quitGroup(groupId, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess() {
                imCallBack.onSuccess("success");

                ImActionDto imActionDto = new ImActionDto();
                imActionDto.setAction(ActionTags.QUIT_GROUP_SUCCESS);


                String actionStr = JsonUtils.toJson(imActionDto);
                EjuHomeImEventCar.getDefault().post(actionStr);


            }
        });

    }


    /**
     * ??????????????????
     *
     * @param groupId    ??????ID
     * @param memberList ??????ID??????
     * @param imCallBack ????????????---List<TIMGroupMemberResult>
     */
    public void deleteGroupMember(String groupId, @NonNull List<String> memberList,
                                  @NonNull final ImCallBack imCallBack) {

        TIMGroupManager.DeleteMemberParam param = new TIMGroupManager.DeleteMemberParam(groupId, memberList);
        param.setReason("some reason");


        TIMGroupManager.getInstance().deleteGroupMember(param, new TIMValueCallBack<List<TIMGroupMemberResult>>() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess(List<TIMGroupMemberResult> timGroupMemberResults) {
                imCallBack.onSuccess(timGroupMemberResults);
                //???????????????????????????
            }
        });

    }


    /**
     * ??????????????????
     *
     * @param groupId    ??????ID
     * @param mGroupInfo ????????????
     * @param memberList ??????ID??????
     * @param imCallBack ????????????---List<TIMGroupMemberResult>
     */
    public void deleteGroupMember(String groupId, GroupInfo mGroupInfo, @NonNull List<GroupMemberInfo> memberList,
                                  @NonNull final ImCallBack imCallBack) {
        GroupInfoProvider provider = new GroupInfoProvider();
        provider.loadGroupInfo(mGroupInfo);
        provider.removeGroupMembers(memberList, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                ToastUtil.toastLongMessage("??????????????????");

            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ToastUtil.toastLongMessage("??????????????????:" + errCode + "=" + errMsg);
            }
        });
    }


    /**
     * ?????????????????????
     *
     * @param groupId    ???ID
     * @param imCallBack ????????????  ---List<TIMGroupMemberInfo>
     */
    public void getGroupMembers(@NonNull String groupId, @NonNull final ImCallBack imCallBack) {

        TIMGroupManager.getInstance().getGroupMembers(groupId, new TIMValueCallBack<List<TIMGroupMemberInfo>>() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
                imCallBack.onSuccess(timGroupMemberInfos);
                //???????????????????????????
            }
        });
    }


    /**
     * ?????????????????????(?????????)
     *
     * @param groupId    ???ID
     * @param imCallBack ????????????  ---GuoupMemberDto
     */
    public void getGroupMembersByService(@NonNull String groupId, @NonNull final ImCallBack imCallBack) {
        final AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(application);


        httpInterface.getGuoupMember(ParameterUtils.prepareFormData(groupId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GuoupMemberDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GuoupMemberDto guoupMemberDto) {

                        if (null != guoupMemberDto && "10000".equals(guoupMemberDto.getCode())) {

                            imCallBack.onSuccess(guoupMemberDto);
                        } else {
                            imCallBack.onError(888, "???????????????????????????");
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        imCallBack.onError(888, "???????????????????????????");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    /**
     * ???????????????????????????
     *
     * @param imCallBack ????????????----List<TIMGroupBaseInfo>
     */
    public void getGroupList(@NonNull final ImCallBack imCallBack) {

        TIMGroupManager.getInstance().getGroupList(new TIMValueCallBack<List<TIMGroupBaseInfo>>() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess(List<TIMGroupBaseInfo> timGroupBaseInfos) {
                imCallBack.onSuccess(timGroupBaseInfos);
                //???????????????????????????
            }
        });


    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param groupIdList ??????list
     * @param imCallBack  ??????
     */
    public void getGroupInfo(@NonNull List<String> groupIdList, @NonNull final ImCallBack imCallBack) {


        TIMGroupManager.getInstance().getGroupInfo(groupIdList, new TIMValueCallBack<List<TIMGroupDetailInfoResult>>() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess(List<TIMGroupDetailInfoResult> timGroupDetailInfoResults) {
                imCallBack.onSuccess(timGroupDetailInfoResults);
            }
        });


    }


    /**
     * ?????????????????????????????????????????????
     *
     * @param groupId    ??????ID
     * @param imCallBack ??????
     */
    public void getGroupInfo(@NonNull String groupId, @NonNull final ImCallBack imCallBack) {


        List<String> stringList = new ArrayList<>();
        stringList.add(groupId);

        TIMGroupManager.getInstance().getGroupInfo(stringList, new TIMValueCallBack<List<TIMGroupDetailInfoResult>>() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess(List<TIMGroupDetailInfoResult> timGroupDetailInfoResults) {
                imCallBack.onSuccess(timGroupDetailInfoResults);
            }
        });


    }


    /**
     * ????????????
     *
     * @param groupId    ??????ID
     * @param imCallBack ????????????
     */
    public void deleteGroup(@NonNull String groupId, @NonNull final ImCallBack imCallBack) {
        TIMGroupManager.getInstance().deleteGroup(groupId, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                imCallBack.onError(code, desc);
                //????????? code ??????????????? desc????????????????????????????????????
                //????????? code ???????????????????????????
                LogUtils.d("login failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess() {
                //??????????????????

                ImActionDto imActionDto = new ImActionDto();
                imActionDto.setAction(ActionTags.DELETE_GROUP_SUCCESS);


                String actionStr = JsonUtils.toJson(imActionDto);
                EjuHomeImEventCar.getDefault().post(actionStr);


                imCallBack.onSuccess("success");
            }
        });


    }

    /**
     * ????????????
     *
     * @param groupId    ??????ID
     * @param userId     ?????????ID
     * @param imCallBack ????????????
     */
    public void modifyGroupOwner(@NonNull final String groupId, @NonNull final String userId, final @NonNull ImCallBack imCallBack) {

        TIMGroupManager.getInstance().modifyGroupOwner(groupId, userId, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess() {
                imCallBack.onSuccess("success");


                setRoleType(groupId, userId, GroupMemberRoleTypeEnmunmer.ROLE_TYPE_OWNER);


            }
        });

    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param groupId    ??? ID
     * @param flags      ??????????????????
     * @param filter     ?????????????????? GroupMemberRoleFilter.XX
     * @param custom     ????????????????????? key ??????
     * @param nextSeq    nextSeq ???????????????????????????????????????0??????????????????????????????????????????????????????????????????????????????0
     * @param imCallBack ????????????
     */
    public void getGroupMembersByFilter(@NonNull String groupId, long flags, @NonNull TIMGroupMemberRoleFilter filter,
                                        List<String> custom, long nextSeq, final ImCallBack imCallBack) {

        TIMGroupManager.getInstance().getGroupMembersByFilter(groupId, flags, filter, custom, nextSeq, new TIMValueCallBack<TIMGroupMemberSucc>() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess(TIMGroupMemberSucc timGroupMemberSucc) {
                imCallBack.onSuccess(timGroupMemberSucc);
            }
        });
    }


    //??????????????????
    public void getMyGroup(final ImCallBack imCallBack) {


        TIMGroupManager.getInstance().getGroupList(new TIMValueCallBack<List<TIMGroupBaseInfo>>() {
            private List<MyGroupDto> myGroupDtoList = new ArrayList<>();

            // ??????????????????????????????????????????????????????
            List<TIMConversation> timConversationList = TIMManager.getInstance().getConversationList();
            private int groupSum = 0;//????????????
            int ait = 0;//@??????
            int aitCount = 0;
            Map<String, String> aitMsgMap = new HashMap<>();//?????????@???????????????ID

            @Override
            public void onError(int i, String s) {
                LogUtils.w("getMyGroupOnError-----" + s);


                if (i == 10004) {
                    //??????????????????null???list
                    imCallBack.onSuccess(myGroupDtoList);
                } else {
                    imCallBack.onError(i, s);
                }


            }

            @Override
            public void onSuccess(List<TIMGroupBaseInfo> timGroupBaseInfos) {

                //??????????????????
                if (null != timGroupBaseInfos && timGroupBaseInfos.size() > 0) {

                    groupSum = timGroupBaseInfos.size();

                    for (TIMGroupBaseInfo timGroupBaseInfo : timGroupBaseInfos) {

                        getGroupInfo(timGroupBaseInfo.getUnReadMessageNum(), timGroupBaseInfo.getGroupId(), new ImCallBack() {
                            @Override
                            public void onError(int i, String s) {
                                groupSum--;
                                imCallBack.onError(i, s);
                            }

                            @Override
                            public void onSuccess(Object var1) {


                                if (null != var1) {

                                    MyGroupDto myGroupDto = (MyGroupDto) var1;


                                    //?????????????????????
                                    if (timConversationList != null) {


                                        for (TIMConversation timConversation : timConversationList) {
                                            if (null != timConversation.getGroupName() && !"null".equals(timConversation.getGroupName())) {
                                                //???????????????
                                                if (timConversation.getGroupName().equals(myGroupDto.getGroupName())) {
                                                    ConversationInfo conversationInfo = TIMConversation2ConversationInfo(timConversation);

                                                    if (null != conversationInfo) {


                                                        MessageInfo messageInfo = conversationInfo.getLastMessage();
                                                        if (null != messageInfo && null != messageInfo.getExtra()) {

                                                            //    LogUtils.w("idididi----" + conversationInfo.getId() + "??????----" + messageInfo.getMsgType() + "\n-----" + messageInfo.getExtra().toString() + "\n-----" + messageInfo.getFromUser());


                                                            myGroupDto.setLastMsgType(messageInfo.getMsgType());

                                                            myGroupDto.setLastMsg("" + messageInfo.getExtra().toString());

                                                            if (messageInfo.getExtra().toString().startsWith("\"<font")) {

                                                                String msg = messageInfo.getExtra().toString().substring(messageInfo.getExtra().toString().indexOf("</font>"));

                                                                myGroupDto.setLastMsg("" + msg.replace("</font>", ""));
                                                                myGroupDto.setLastMsgAuthor("????????????");
                                                                //  LogUtils.w("msgmsgmsgmsg------" + msg.replace("</font>", ""));

                                                            } else {
                                                                myGroupDto.setLastMsg("" + messageInfo.getExtra().toString());
                                                                myGroupDto.setLastMsgAuthor("" + messageInfo.getFromUser());
                                                            }

                                                        }
//

                                                    }

                                                }

                                            }

                                        }


                                    }


                                    myGroupDtoList.add(myGroupDto);
                                    //??????????????????
                                    if (groupSum == myGroupDtoList.size()) {

                                        if (groupSum == myGroupDtoList.size()) {


                                            //?????????????????????@????????????

                                            if (null != myGroupDtoList && myGroupDtoList.size() > 0) {
                                                List<MyGroupDto> selectAitList = new ArrayList<>();

                                                for (MyGroupDto groupDto : myGroupDtoList) {

                                                    if (groupDto.getUnreadMsgNum() > 0) {
                                                        selectAitList.add(groupDto);
                                                    }
                                                }


                                                aitCount = selectAitList.size();
                                                // TIMConversation mCurrentConversation = TIMManager.getInstance().getConversation(info.getType(), info.getId());


                                                //????????????????????????????????????
                                                if (null != selectAitList && selectAitList.size() > 0) {


                                                    for (final MyGroupDto aitGroup : selectAitList) {
                                                        LogUtils.w("aitGroupgetPeer--------" + aitGroup.getGroupId());
                                                        TIMConversation mCurrentConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, aitGroup.getGroupId());

                                                        mCurrentConversation.getMessage(100, null, new TIMValueCallBack<List<TIMMessage>>() {
                                                            @Override
                                                            public void onError(int i, String s) {
                                                                ait++;


                                                                if (aitCount == ait) {
                                                                    LogUtils.w("@?????????-------");
                                                                    //??????@???Map


                                                                    for (int j = 0; j < myGroupDtoList.size(); j++) {


                                                                        if (null != aitMsgMap.get(myGroupDtoList.get(j).getGroupId()) && "true".equals(myGroupDtoList.get(j).getGroupId())) {


                                                                            myGroupDtoList.get(j).setAitMsg(true);

                                                                        }


                                                                    }

                                                                    imCallBack.onSuccess(myGroupDtoList);


                                                                }

                                                            }

                                                            @Override
                                                            public void onSuccess(List<TIMMessage> timMessages) {
                                                                ait++;
                                                                List<MessageInfo> msgInfos = MessageInfoUtil.TIMMessages2MessageInfos(timMessages, true);


                                                                for (int i = 0; i < msgInfos.size(); i++) {
                                                                    // ???????????????????????????json??????


                                                                    if ((msgInfos.get(i).getElement() instanceof TIMCustomElem)) {


                                                                        TIMCustomElem elem = (TIMCustomElem) msgInfos.get(i).getElement();
                                                                        // ????????????json????????????????????????bean??????
                                                                        CustomMessage data = null;

                                                                        try {
                                                                            data = new Gson().fromJson(new String(elem.getData()), CustomMessage.class);
                                                                        } catch (Exception e) {
                                                                            LogUtils.w("@invalid json: " + new String(elem.getData()) + " " + e.getMessage());
                                                                        }

                                                                        //??????????????????????????????@?????????????????????????????????????????????????????????????????????@???Map???
                                                                        if (null != data && data.getAction() == CustomMessage.CUSTOM_AIT && !msgInfos.get(i).isRead() && !msgInfos.get(i).isSelf()) {

                                                                            CustomContentDto customContentDto = JsonUtils.fromJson(data.getContent(), CustomContentDto.class);

                                                                            if (null != customContentDto && null != customContentDto.getAitMap()) {
                                                                                Map<String, String> aitMap = customContentDto.getAitMap();
                                                                                if (null != aitMap.get(AppConfig.appImId) && !"".equals(aitMap.get(AppConfig.appImId))) {
                                                                                    //???????????????@??????
                                                                                    aitMsgMap.put(timMessages.get(i).getConversation().getPeer(), "true");

                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }


                                                                if (aitCount == ait) {
                                                                    LogUtils.w("@?????????-------1111");
                                                                    //??????@???Map


                                                                    for (int i = 0; i < myGroupDtoList.size(); i++) {
                                                                        if (null != aitMsgMap.get(myGroupDtoList.get(i).getGroupId())) {
                                                                            myGroupDtoList.get(i).setAitMsg(true);
                                                                        }
                                                                    }

                                                                    imCallBack.onSuccess(myGroupDtoList);


                                                                }


                                                            }
                                                        });


                                                    }

//                                                    LogUtils.w("@?????????-------???????????????");


                                                } else {
                                                    //??????????????????????????????
                                                    imCallBack.onSuccess(myGroupDtoList);
                                                }


                                            } else {
                                                //???????????????
                                                imCallBack.onSuccess(myGroupDtoList);

                                            }


                                        }


                                    }


                                }


                            }
                        });


                    }

                }


            }
        });

    }


    /**
     * ????????????????????????
     *
     * @param unreadMsgNum
     * @param groupId
     * @param imCallBack
     */
    private void getGroupInfo(final int unreadMsgNum, String groupId, final ImCallBack imCallBack) {

        List<String> groupIdList = new ArrayList<>();
        groupIdList.add(groupId);

        TIMGroupManager.getInstance().getGroupInfo(groupIdList, new TIMValueCallBack<List<TIMGroupDetailInfoResult>>() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess(List<TIMGroupDetailInfoResult> timGroupDetailInfoResults) {
                for (TIMGroupDetailInfoResult timGroupDetailInfoResult : timGroupDetailInfoResults) {

                    MyGroupDto myGroupDto = new MyGroupDto();

                    myGroupDto.setGroupId(timGroupDetailInfoResult.getGroupId());
                    myGroupDto.setGroupName(timGroupDetailInfoResult.getGroupName());
                    myGroupDto.setGroupOwner(timGroupDetailInfoResult.getGroupOwner());


                    myGroupDto.setGroupNotice(timGroupDetailInfoResult.getGroupNotification());
                    myGroupDto.setGroupIntroduction(timGroupDetailInfoResult.getGroupIntroduction());
                    myGroupDto.setGroupFaceUrl(timGroupDetailInfoResult.getFaceUrl());


                    myGroupDto.setGroupType(timGroupDetailInfoResult.getGroupType());
                    myGroupDto.setCreateTime(timGroupDetailInfoResult.getCreateTime());
                    myGroupDto.setLastInfoTime(timGroupDetailInfoResult.getLastInfoTime());

                    myGroupDto.setLastMsgTime(timGroupDetailInfoResult.getLastMsgTime());
                    myGroupDto.setMemberNum(timGroupDetailInfoResult.getMemberNum());
                    myGroupDto.setMaxMemberNum(timGroupDetailInfoResult.getMaxMemberNum());

                    myGroupDto.setOnlineMemberNum(timGroupDetailInfoResult.getOnlineMemberNum());
                    myGroupDto.setAddOption(timGroupDetailInfoResult.getAddOption());


                    myGroupDto.isSilenceAll(timGroupDetailInfoResult.isSilenceAll());
                    //  myGroupDto.setLastMsg(timGroupDetailInfoResult.getLastMsg());
                    //????????????
                    myGroupDto.setUnreadMsgNum(unreadMsgNum);

                    myGroupDto.setRole(timGroupDetailInfoResult.getRole());
                    imCallBack.onSuccess(myGroupDto);

                }


            }
        });
    }


    /**
     * TIMConversation?????????ConversationInfo
     *
     * @param conversation
     * @return
     */
    private ConversationInfo TIMConversation2ConversationInfo(final TIMConversation conversation) {
        if (conversation == null) {
            return null;
        }
        LogUtils.i("TIMConversation2ConversationInfo id:" + conversation.getPeer()
                + "|name:" + conversation.getGroupName()
                + "|unreadNum:" + conversation.getUnreadMessageNum());
        TIMMessage message = conversation.getLastMsg();
        if (message == null) {
            return null;
        }
        final ConversationInfo info = new ConversationInfo();
        TIMConversationType type = conversation.getType();
        if (type == TIMConversationType.System) {
            if (message.getElementCount() > 0) {
                TIMElem ele = message.getElement(0);
                TIMElemType eleType = ele.getType();
                if (eleType == TIMElemType.GroupSystem) {
                    TIMGroupSystemElem groupSysEle = (TIMGroupSystemElem) ele;
                    //groupSystMsgHandle(groupSysEle);
                }
            }
            return null;
        }

        boolean isGroup = type == TIMConversationType.Group;
        info.setLastMessageTime(message.timestamp());
        List<MessageInfo> list = MessageInfoUtil.TIMMessage2MessageInfo(message, isGroup);
        if (list != null && list.size() > 0) {
            info.setLastMessage(list.get(list.size() - 1));
        }
        if (isGroup) {
            //fillConversationWithGroupInfo(conversation, info);
        } else {
            //fillConversationWithUserProfile(conversation, info);
        }
        info.setId(conversation.getPeer());
        info.setGroup(conversation.getType() == TIMConversationType.Group);
        info.setUnRead((int) conversation.getUnreadMessageNum());
        return info;
    }


    /**
     * ??????????????????????????????
     *
     * @param context   ?????????
     * @param groupId   ??????ID
     * @param groupName ????????????
     */
    public void openChatActivity(final Context context, final String groupId, final String groupName) {


        if (null != groupId) {


            if (null == groupName) {
                getGroupInfo(groupId, new ImCallBack() {
                    @Override
                    public void onError(int var1, String var2) {

                        ChatInfo chatInfo = new ChatInfo();
                        chatInfo.setType(TIMConversationType.Group);
                        chatInfo.setId(groupId);
                        chatInfo.setChatName("????????????");
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra(Constants.CHAT_INFO, chatInfo);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    }

                    @Override
                    public void onSuccess(Object var1) {


                        List<TIMGroupDetailInfoResult> timGroupDetailInfoResults = (List<TIMGroupDetailInfoResult>) var1;

                        if (null != timGroupDetailInfoResults && timGroupDetailInfoResults.size() > 0) {

                            ChatInfo chatInfo = new ChatInfo();
                            chatInfo.setType(TIMConversationType.Group);
                            chatInfo.setId(groupId);
                            chatInfo.setChatName(timGroupDetailInfoResults.get(0).getGroupName());
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra(Constants.CHAT_INFO, chatInfo);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);

                        }


                    }
                });


            } else {


                ChatInfo chatInfo = new ChatInfo();
                chatInfo.setType(TIMConversationType.Group);
                chatInfo.setId(groupId);
                chatInfo.setChatName(groupName);
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(Constants.CHAT_INFO, chatInfo);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }


    /**
     * ???????????????????????????????????????V
     *
     * @param context   ?????????
     * @param groupId   ??????ID
     * @param groupName ????????????
     */
    public void openChatActivityCarryV(final Context context, final String groupId, final String groupName) {


        if (null != groupId) {


            if (null == groupName) {
                getGroupInfo(groupId, new ImCallBack() {
                    @Override
                    public void onError(int var1, String var2) {


                        startChatAt(groupId, "????????????", context);
                    }

                    @Override
                    public void onSuccess(Object var1) {


                        List<TIMGroupDetailInfoResult> timGroupDetailInfoResults = (List<TIMGroupDetailInfoResult>) var1;

                        if (null != timGroupDetailInfoResults && timGroupDetailInfoResults.size() > 0) {
                            startChatAt(groupId, timGroupDetailInfoResults.get(0).getGroupName(), context);

                        }


                    }
                });


            } else {
                startChatAt(groupId, groupName, context);
            }
        }
    }

    /**
     * ???????????????????????????V
     *
     * @param groupId
     * @param groupName
     * @param context
     */
    private void startChatAt(final String groupId, final String groupName, final Context context) {

        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setType(TIMConversationType.Group);
        chatInfo.setId(groupId);
        chatInfo.setChatName(groupName);
        final Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.CHAT_INFO, chatInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        getGroupUserGrade(groupId, new ImCallBack() {
            @Override
            public void onError(int var1, String var2) {
                context.startActivity(intent);
            }

            @Override
            public void onSuccess(Object var1) {
                context.startActivity(intent);
            }
        });
    }


    /**
     * ????????????????????????
     *
     * @param conversation
     * @param message
     * @param callBack
     */
    public void sendTipsMessage(TIMConversation conversation, TIMMessage message, final ImCallBack callBack) {
        conversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(final int code, final String desc) {
                LogUtils.i("sendTipsMessage fail:" + code + "=" + desc);
                if (callBack != null) {
                    callBack.onError(code, desc);
                }
            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                LogUtils.i("sendTipsMessage onSuccess");
                if (callBack != null) {
                    callBack.onSuccess(timMessage);
                }
            }
        });


    }


    /**
     * ????????????????????????????????????
     *
     * @param groupId
     * @param identifier
     * @param groupMemberRoleTypeEnmunmer
     */
    public void setRoleType(final String groupId, final String identifier, final GroupMemberRoleTypeEnmunmer groupMemberRoleTypeEnmunmer) {


        String role = "";

        if (groupMemberRoleTypeEnmunmer.getValue() == GroupMemberRoleTypeEnmunmer.ROLE_TYPE_NORMAL.getValue()) {
            role = "Member";
        } else if (groupMemberRoleTypeEnmunmer.getValue() == GroupMemberRoleTypeEnmunmer.ROLE_TYPE_ADMIN.getValue()) {
            role = "Admin";
        } else if (groupMemberRoleTypeEnmunmer.getValue() == GroupMemberRoleTypeEnmunmer.ROLE_TYPE_NOT_MEMBER.getValue()) {
            role = "Member";
        } else if (groupMemberRoleTypeEnmunmer.getValue() == GroupMemberRoleTypeEnmunmer.ROLE_TYPE_OWNER.getValue()) {
            role = "Owner";
        }


        AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(application);
        httpInterface.setMemberRole(ParameterUtils.prepareFormData(identifier), ParameterUtils.prepareFormData(groupId), ParameterUtils.prepareFormData(role))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SetGuoupStaticDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SetGuoupStaticDto setGuoupStaticDto) {

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
     * ????????????????????????????????????
     *
     * @param groupId                     ???ID
     * @param identifier                  ???????????????ID
     * @param groupMemberRoleTypeEnmunmer ???????????????
     * @param imCallBack                  ??????
     */
    public void modifyMemberInfo(final String groupId, final String identifier, final GroupMemberRoleTypeEnmunmer groupMemberRoleTypeEnmunmer, final ImCallBack imCallBack) {

        TIMGroupManager.ModifyMemberInfoParam param = new TIMGroupManager.ModifyMemberInfoParam(groupId, identifier);
        param.setRoleType(groupMemberRoleTypeEnmunmer.getValue());


        TIMGroupManager.getInstance().modifyMemberInfo(param, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess() {
                imCallBack.onSuccess("ok");

                setRoleType(groupId, identifier, groupMemberRoleTypeEnmunmer);

            }
        });

    }

    /**
     * ????????????????????????
     *
     * @param groupId    ???ID
     * @param imCallBack ????????????
     */
    public void getSelfInfo(String groupId, final ImCallBack imCallBack) {
        TIMGroupManager.getInstance().getSelfInfo(groupId, new TIMValueCallBack<TIMGroupSelfInfo>() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess(TIMGroupSelfInfo timGroupSelfInfo) {
                imCallBack.onSuccess(timGroupSelfInfo);
            }
        });

    }


    /**
     * ??????IM ?????????????????????
     * ???????????????????????????????????????
     *
     * @param msgs
     */
    public void onNewMessage(List<TIMMessage> msgs) {

        if (null == msgs || msgs.size() == 0) {
            return;
        }
        for (TIMMessage msg : msgs) {
            TIMConversation conversation = msg.getConversation();
            TIMConversationType type = conversation.getType();
            if (type == TIMConversationType.Group) {

                ImActionDto imActionDto = new ImActionDto();
                imActionDto.setAction(ActionTags.ON_NEW_GROUP_MSG);


                String actionStr = JsonUtils.toJson(imActionDto);
                EjuHomeImEventCar.getDefault().post(actionStr);
                continue;
            }
        }
    }


    /**
     * ????????????
     *
     * @param item       ??????????????????
     * @param imCallBack ??????
     */
    public void acceptGuoupApply(GroupApplyInfo item, final ImCallBack imCallBack) {


        GroupInfoProvider mProvider = GroupChatManagerKit.getInstance().getProvider();
        mProvider.acceptApply(item, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                imCallBack.onSuccess("success"
                );
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                imCallBack.onError(errCode, errMsg);
            }
        });


    }

    /**
     * ????????????
     *
     * @param item       ??????????????????
     * @param imCallBack ??????
     */

    public void refuseGroupApply(GroupApplyInfo item, final ImCallBack imCallBack) {


        GroupInfoProvider mProvider = GroupChatManagerKit.getInstance().getProvider();
        mProvider.refuseApply(item, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                imCallBack.onSuccess("success"
                );
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                imCallBack.onError(errCode, errMsg);
            }
        });


    }

    /**
     * ???????????????
     *
     * @param notification
     */
    public void setGroupNotification(String notification) {
        this.notification = notification;


    }

    /**
     * @param tag          ??????????????????
     * @param notification ????????????
     */
    public void setGroupNotification(String tag, String notification) {
        this.notification = notification;
        this.notificationTag = tag;

    }

    /**
     * ?????????????????????
     *
     * @return
     */
    public String getGroupNotificationTag() {
        return this.notificationTag;
    }


    /**
     * ???????????????
     *
     * @return
     */
    public String getGroupNotification() {
        return this.notification;
    }


    /**
     * ????????????????????????
     *
     * @param groupId    ??????ID
     * @param isPublic   1?????????0??????
     * @param imCallBack ??????
     */
    public void setGroupStatic(final String groupId, String isPublic, final ImCallBack imCallBack) {

        final AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(application);
        httpInterface.setGuoupState(ParameterUtils.prepareFormData(groupId), ParameterUtils.prepareFormData(isPublic))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SetGuoupStaticDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SetGuoupStaticDto setGuoupStaticDto) {

                        if (null != setGuoupStaticDto && "10000".equals(setGuoupStaticDto.getCode())) {

                            imCallBack.onSuccess(groupId);
                        } else {
                            imCallBack.onError(888, setGuoupStaticDto.toString());
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        imCallBack.onError(888, e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * ?????????????????????????????????????????????????????????????????????
     *
     * @param groups     ?????????????????????ID?????????,????????????????????????@TGS#1NVTZEAE4,@TGS#1CXTZEAET??????
     * @param IsFilter   ????????????????????????????????????????????????1?????????0?????????
     * @param imCallBack ??????
     */
    public void getGroupStatic(final String groups, final String IsFilter, final ImCallBack imCallBack) {

        final AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(application);

        httpInterface.getGuoupState(

                ParameterUtils.prepareFormData(groups),
                ParameterUtils.prepareFormData(IsFilter),
                ParameterUtils.prepareFormData(AppConfig.appImId)
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GuoupStaticDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GuoupStaticDto guoupStaticDto) {
                        if (null != guoupStaticDto && "10000".equals(guoupStaticDto.getCode())) {

                            imCallBack.onSuccess(guoupStaticDto);
                        } else {
                            imCallBack.onError(888, guoupStaticDto.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        imCallBack.onError(888, e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * ?????????????????????
     *
     * @param conversation_id ?????? ID?????????ID???????????????C2C+userID????????????GROUP+groupID???????????? @TIM#SYSTEM?????????????????????)
     * @param disturb         ??????????????? 0?????? 1??????
     * @param imCallBack      ??????
     */
    public void setDisturb(final String conversation_id, String disturb, final ImCallBack imCallBack) {


        final AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(application);
        httpInterface.setDisturb(ParameterUtils.prepareFormData(conversation_id),
                ParameterUtils.prepareFormData(AppConfig.appImId),
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
                            imCallBack.onSuccess(conversation_id);
                        } else {
                            imCallBack.onError(888, setGuoupStaticDto.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        imCallBack.onError(888, e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    /**
     * ??????????????????
     *
     * @param conversation_id ?????? ID?????????ID???????????????C2C+userID????????????GROUP+groupID???????????? @TIM#SYSTEM?????????????????????)
     * @param stick           ???????????? 0?????? 1??????
     * @param imCallBack
     */
    public void setStick(final String conversation_id, String stick, final ImCallBack imCallBack) {


        final AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(application);
        httpInterface.setStick(ParameterUtils.prepareFormData(conversation_id),
                ParameterUtils.prepareFormData(AppConfig.appImId),
                ParameterUtils.prepareFormData(stick))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SetGuoupStaticDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SetGuoupStaticDto setGuoupStaticDto) {

                        if (null != setGuoupStaticDto && "10000".equals(setGuoupStaticDto.getCode())) {
                            imCallBack.onSuccess(conversation_id);
                        } else {
                            imCallBack.onError(888, setGuoupStaticDto.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        imCallBack.onError(888, e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    /**
     * ???????????????????????????????????????  ????????????
     *
     * @param conversations_id ??????ID?????????,??????????????????ID???????????????C2C+userID????????????GROUP+groupID???????????? @TIM#SYSTEM?????????????????????)
     * @param imCallBack       ??????
     */
    public void getCsationStatus(String conversations_id, final ImCallBack imCallBack) {
        final AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(application);

        httpInterface.getCsationStatus(ParameterUtils.prepareFormData(conversations_id),
                ParameterUtils.prepareFormData(AppConfig.appImId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CsationStatusDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CsationStatusDto csationStatusDto) {
                        if (null != csationStatusDto && "10000".equals(csationStatusDto.getCode())) {
                            imCallBack.onSuccess(csationStatusDto);
                        } else {
                            imCallBack.onError(888, csationStatusDto.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        imCallBack.onError(888, e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    /**
     * ????????????ID????????????????????????
     *
     * @param imIds      ??????ID
     * @param imCallBack ??????
     */
    public void ImIdToNickName(List<String> imIds, final ImCallBack imCallBack) {


        UserInfoController.getInstance().getUsersProfile(imIds, true, new ImCallBack() {
            @Override
            public void onError(int var1, String var2) {
                imCallBack.onError(888, var2);
            }

            @Override
            public void onSuccess(Object var1) {

                if (null != var1) {

                    List<TIMUserProfile> timUserProfiles = (List<TIMUserProfile>) var1;

                    Map<String, String> nickNames = new HashMap<>();


                    for (TIMUserProfile timUserProfile : timUserProfiles) {
                        nickNames.put(timUserProfile.getIdentifier(), timUserProfile.getNickName());
                    }


                    imCallBack.onSuccess(nickNames);


                }

            }
        });


    }

    /**
     * ????????? ??????
     *
     * @param groupID
     * @param groupName
     * @param imCallBack
     */
    public void loadApplyList(String groupID, String groupName, final ImCallBack imCallBack) {

        GroupChatManagerKit mGroupChatManager = GroupChatManagerKit.getInstance();

        mGroupChatManager.setGroupHandler(new GroupChatManagerKit.GroupNotifyHandler() {
            @Override
            public void onGroupForceExit() {

            }

            @Override
            public void onGroupNameChanged(String newName) {

            }

            @Override
            public void onApplied(int size) {

            }
        });

        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setId(groupID);
        groupInfo.setChatName(groupName);
        mGroupChatManager.setCurrentChatInfo(groupInfo);

        mGroupChatManager.getProvider().loadGroupApplies(new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {

                imCallBack.onSuccess(data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                imCallBack.onError(errCode, errMsg);
            }
        });


    }


    /**
     * ???????????????
     *
     * @param imCallBack
     */
    public void getGroupPendencyList(final ImCallBack imCallBack) {

        final List<GroupApplyInfo> applies = new ArrayList<>();
        TIMGroupPendencyGetParam param = new TIMGroupPendencyGetParam();
        param.setTimestamp(0);//??????????????? 0
        param.setNumPerPage(100);

        TIMGroupManager.getInstance().getGroupPendencyList(param, new TIMValueCallBack<TIMGroupPendencyListGetSucc>() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess(TIMGroupPendencyListGetSucc timGroupPendencyListGetSucc) {

                List<TIMGroupPendencyItem> pendencies = timGroupPendencyListGetSucc.getPendencies();
                for (int i = 0; i < pendencies.size(); i++) {
                    GroupApplyInfo info = new GroupApplyInfo(pendencies.get(i));
                    info.setStatus(0);
                    applies.add(info);
                }
                imCallBack.onSuccess(applies);

            }
        });


    }


    /**
     * ???????????????
     *
     * @param groupId    ??????ID
     * @param imCallBack List<V2TIMGroupApplication>
     */
    public void getGroupPendencyList(final String groupId, final ImCallBack imCallBack) {


        V2TIMManager.getGroupManager().getGroupApplicationList(new V2TIMValueCallback<V2TIMGroupApplicationResult>() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess(V2TIMGroupApplicationResult v2TIMGroupApplicationResult) {


                V2TIMGroupApplicationResult mData = v2TIMGroupApplicationResult;

                List<V2TIMGroupApplication> mDataList = mData.getGroupApplicationList();

                if (null != mData) {


                    if (null != mData.getGroupApplicationList() && mData.getGroupApplicationList().size() > 0) {


                        Iterator<V2TIMGroupApplication> it = mDataList.iterator();

                        while (it.hasNext()) {
                            V2TIMGroupApplication v2TIMGroupApplication = it.next();

                            if (!v2TIMGroupApplication.getGroupID().equals(groupId)) {
                                it.remove();
                            }
                        }

                    }


                }
                imCallBack.onSuccess(mDataList);

            }
        });


    }


    /**
     * ???????????????????????????
     *
     * @param v2TIMGroupApplication ITEM
     * @param reason                ????????????
     * @param imCallBack            ??????
     */
    public void acceptGroupApplication(V2TIMGroupApplication v2TIMGroupApplication,
                                       String reason,
                                       final ImCallBack imCallBack) {


        V2TIMManager.getGroupManager().acceptGroupApplication(v2TIMGroupApplication, reason, new V2TIMCallback() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess() {
                imCallBack.onSuccess("success");
            }
        });


    }


    /**
     * ???????????????????????????
     *
     * @param v2TIMGroupApplication ITEM
     * @param reason                ????????????
     * @param imCallBack            ??????
     */
    public void refuseGroupApplication(V2TIMGroupApplication v2TIMGroupApplication,
                                       String reason,
                                       final ImCallBack imCallBack) {


        V2TIMManager.getGroupManager().refuseGroupApplication(v2TIMGroupApplication, reason, new V2TIMCallback() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess() {
                imCallBack.onSuccess("success");
            }
        });


    }


    /**
     * ???????????????
     *
     * @param context
     * @param userIdList       ????????????IM id
     * @param jurisdictionList ????????????????????????????????????IM id
     * @param groupId          ??????ID
     */
    public void groupVideoCall(Context context, List<String> userIdList, List<String> jurisdictionList, String groupId) {//101444bee20249d9bcce813e11a8c393


        //?????????Activity
        Intent intent = new Intent(context, TRTCVideoCallActivity.class);
        intent.putExtra(Constants.OPEN_GROUP_VIDEO_CALL, Constants.OPEN_GROUP_VIDEO_CALL_10000);
        intent.putExtra(Constants.GROUP_ID, groupId);

        intent.putStringArrayListExtra(Constants.GROUP_AV_IM_LIST, (ArrayList<String>) userIdList);
        intent.putStringArrayListExtra(Constants.GROUP_PERMISSIONS_AV_IM_LIST, (ArrayList<String>) jurisdictionList);


        context.startActivity(intent);


    }

    /**
     * ???????????????
     *
     * @param userIdList       ????????????IM id
     * @param jurisdictionList ????????????????????????????????????IM id
     * @param groupId          ??????ID
     */
    public void answerGroupVideoCall(String sponsor, List<String> userIdList, List<String> jurisdictionList, String groupId) {

        Intent intent = new Intent(application, TRTCVideoCallActivity.class);
        intent.putExtra(Constants.OPEN_GROUP_VIDEO_CALL, Constants.OPEN_GROUP_VIDEO_CALL_20000);
        intent.putExtra(Constants.GROUP_ID, groupId);
        intent.putExtra(Constants.GROUP_AV_SPONSOR, sponsor);

        intent.putStringArrayListExtra(Constants.GROUP_AV_IM_LIST, (ArrayList<String>) userIdList);
        intent.putStringArrayListExtra(Constants.GROUP_PERMISSIONS_AV_IM_LIST, (ArrayList<String>) jurisdictionList);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        application.startActivity(intent);
    }


    /**
     * ?????????????????????????????????
     *
     * @param userIdList       ????????????IM id
     * @param jurisdictionList ????????????????????????????????????IM id
     * @param groupId          ??????ID
     */
    public void addNewUsersGroupVideoCall(List<String> userIdList, List<String> jurisdictionList, String groupId) {


        GroupAvNumberDto groupAvNumberDto = new GroupAvNumberDto();
        groupAvNumberDto.setAllUseList(userIdList);
        groupAvNumberDto.setOpenVideoList(jurisdictionList);
        groupAvNumberDto.setGroupId(groupId);
        String groupAvNumberDtoStr = JsonUtils.toJson(groupAvNumberDto);

        ImActionDto imActionDto = new ImActionDto();
        imActionDto.setAction(ActionTags.PUST_NEW_GROUP_AV_IM_LIST);
        imActionDto.setJsonStr(groupAvNumberDtoStr);
        String voiceCall = JsonUtils.toJson(imActionDto);
        EjuHomeImEventCar.getDefault().post(voiceCall);

    }

    /**
     * ?????????????????????
     *
     * @param groupID
     * @param filter   ?????????????????????
     * @param nextSeq  0
     * @param callback V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ALL???????????????
     *                 V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_OWNER?????????
     *                 V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ADMIN???????????????
     *                 V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_COMMON??????????????????
     *                 nextSeq	???????????????????????????????????????0????????????????????? nextSeq ?????????????????????????????????????????????????????????0???
     */
    public void getGroupMemberList(String groupID, int filter, long nextSeq, final ImCallBack callback) {


        V2TIMManager.getGroupManager().getGroupMemberList(groupID, filter, 0, new V2TIMValueCallback<V2TIMGroupMemberInfoResult>() {
            @Override
            public void onError(int i, String s) {
                callback.onError(i, s);
            }

            @Override
            public void onSuccess(V2TIMGroupMemberInfoResult v2TIMGroupMemberInfoResult) {
                callback.onSuccess(v2TIMGroupMemberInfoResult);
            }
        });
    }


    public void getConversation(String groupID, ImCallBack imCallBack) {

        TIMManager.getInstance().getConversation(TIMConversationType.Group, groupID);


    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param imUser     IM??????ID????????????????????????????????????????????????
     * @param nameKey    ??????????????????????????????
     * @param imCallBack
     */
    public void getGroupListWithKey(String imUser, String nameKey, final ImCallBack imCallBack) {


        final AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(application);

        httpInterface.getGroupListWithKey(ParameterUtils.prepareFormData(imUser),
                ParameterUtils.prepareFormData(nameKey))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GroupListWithKeyDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GroupListWithKeyDto groupListWithKeyDto) {


                        if (null != groupListWithKeyDto && "10000".equals(groupListWithKeyDto.getCode())) {
                            imCallBack.onSuccess(groupListWithKeyDto);
                        } else {
                            imCallBack.onError(88888, "getGroupListWithKey??????");
                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        imCallBack.onError(88888, "getGroupListWithKey??????");
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }


    /**
     * Group????????????????????????
     *
     * @param fileUri        ??????????????????
     * @param conversationId ????????????ID
     * @param imCallBack     ??????
     */
    public void sendFileMessage(Uri fileUri, String conversationId, final ImCallBack imCallBack) {

        MessageInfo info = MessageInfoUtil.buildFileMessage(fileUri);

        TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.Group, conversationId);
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
     * Group????????????????????????
     *
     * @param path           ??????????????????
     * @param conversationId ????????????ID
     * @param imCallBack     ??????
     */
    public void sendImageMessage(Uri path, String conversationId, final ImCallBack imCallBack) {

        MessageInfo info = MessageInfoUtil.buildImageMessage(path, true);

        TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.Group, conversationId);
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
     * Group?????????????????????????????????????????????
     *
     * @param groupId
     * @param callBack
     */
    public void sendTipsMessage(String groupId, final ImCallBack callBack) {


        String message = TIMManager.getInstance().getLoginUser() + "???????????????";
        final TIMMessage uploadFileTips = MessageInfoUtil.buildGroupCustomMessage(MessageInfoUtil.GROUP_UPLOAD_FILE, message);
        TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, groupId);

        conversation.sendMessage(uploadFileTips, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(final int code, final String desc) {
                LogUtils.i("sendTipsMessage fail:" + code + "=" + desc);
                if (callBack != null) {
                    callBack.onError(code, desc);
                }
            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                LogUtils.i("sendTipsMessage onSuccess");
                if (callBack != null) {
                    callBack.onSuccess(timMessage);
                }
            }
        });


    }


    /**
     * ????????????????????????V
     *
     * @param groupId    ??????ID
     * @param imCallBack
     */
    private void getGroupUserGrade(final String groupId, final ImCallBack imCallBack) {


        final AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(application);
        httpInterface.getGroupMembersGrade(ParameterUtils.prepareFormData(groupId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserLevelDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UserLevelDto userLevelDto) {

                        if (null != userLevelDto && "10000".equals(userLevelDto.getCode())) {
                            Map<String, String> gradeMap = new HashMap<>();

                            if (null != userLevelDto.getData() && userLevelDto.getData().size() > 0) {
                                gradeMap.putAll(userLevelDto.getData());
                                imCallBack.onSuccess(gradeMap);
                                EjuImController.getInstance().setvMap(gradeMap);
                            } else {
                                imCallBack.onError(888888, "" + "???10000");
                            }


                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        imCallBack.onError(888888, "" + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public Map<String, String> getNowOpenGroupRole() {


        return groupRoleMap;
    }

    public void setNowOpenGroupRole(String groupRole, final ImCallBack imCallBack) {
        LogUtils.w("setNowOpenGroupRole---");

        getGroupMembers(groupRole, new ImCallBack() {
            @Override
            public void onError(int var1, String var2) {
                imCallBack.onError(888, "???????????????????????????Map??????");
            }

            @Override
            public void onSuccess(Object var1) {

                List<TIMGroupMemberInfo> list = (List<TIMGroupMemberInfo>) var1;


                if (null != list && list.size() > 0) {
                    imCallBack.onSuccess("???????????????????????????Map??????");

                    if (null == groupRoleMap) {
                        groupRoleMap = new HashMap<>();
                    }

                    groupRoleMap.clear();
                    for (TIMGroupMemberInfo info : list) {
                        groupRoleMap.put(info.getUser(), info.getRole() + "");
                    }


                } else {
                    imCallBack.onError(888, "???????????????????????????Map??????");
                }


            }
        });


    }


}




