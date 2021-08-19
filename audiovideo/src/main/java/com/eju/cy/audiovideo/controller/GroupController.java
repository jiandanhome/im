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
 * @ Description： 圈子相关
 */
public class GroupController {


    private static GroupController instance;
    private String notification = "";//群通知
    private String notificationTag = "";//群通知标识
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
//        createGroup("圈子名字", "abcdefg", TIMGroupAddOpt.TIM_GROUP_ADD_AUTH, infos, new ImCallBack() {
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
     * 创建圈子
     *
     * @param groupName           圈子名字
     * @param groupId             圈子ID
     * @param groupMemberInfoList 圈子成员 id
     * @param groupTypeEnmumer    圈子类型
     * @param groupAddOptEnmunmer 加圈验证类型
     * @param imCallBack          结果回调
     */

    public void createGroup(String groupName, String groupId, List<String> groupMemberInfoList, GroupTypeEnmumer groupTypeEnmumer, GroupAddOptEnmunmer groupAddOptEnmunmer, final ImCallBack imCallBack) {

        //圈子类型，圈子名字
        TIMGroupManager.CreateGroupParam param = new TIMGroupManager.CreateGroupParam(groupTypeEnmumer.value(), groupName);
        //加圈选项
        int addAddOpt = (int) groupAddOptEnmunmer.getValue();

        LogUtils.w("addAddOpt---------" + TIMGroupAddOpt.values()[addAddOpt]);
        param.setAddOption(TIMGroupAddOpt.values()[addAddOpt]);
        //圈子ID
        param.setGroupId(groupId);

        //圈成员---现在id是拼接的
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
                LogUtils.e("创建圈子失败 failed, code: " + code + "|desc: " + desc);
            }

            @Override
            public void onSuccess(final String groupId) {
                LogUtils.e("创建圈子成功" + groupId);
                String message = TIMManager.getInstance().getLoginUser() + "创建圈子";
                final TIMMessage createTips = MessageInfoUtil.buildGroupCustomMessage(MessageInfoUtil.GROUP_CREATE, message);
                TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, groupId);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //发送创建成功消息
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
     * 创建圈子
     *
     * @param groupName           圈子名字
     * @param groupId             圈子ID
     * @param groupMemberInfoList 圈子成员 id
     * @param groupTypeEnmumer    圈子类型
     * @param groupAddOptEnmunmer 加圈验证类型
     * @param imCallBack          结果回调
     */

    public void createGroupForMember(String groupName, String groupId, List<GroupMemberInfo> groupMemberInfoList, GroupTypeEnmumer groupTypeEnmumer, GroupAddOptEnmunmer groupAddOptEnmunmer, final ImCallBack imCallBack) {

        //圈子类型，圈子名字
        TIMGroupManager.CreateGroupParam param = new TIMGroupManager.CreateGroupParam(groupTypeEnmumer.value(), groupName);
        //加圈选项
        int addAddOpt = (int) groupAddOptEnmunmer.getValue();

        LogUtils.w("addAddOpt---------" + TIMGroupAddOpt.values()[addAddOpt]);
        param.setAddOption(TIMGroupAddOpt.values()[addAddOpt]);
        //圈子ID
        param.setGroupId(groupId);

        //圈成员
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
                LogUtils.e("创建圈子失败 failed, code: " + code + "|desc: " + desc);
            }

            @Override
            public void onSuccess(final String groupId) {
                LogUtils.e("创建圈子成功" + groupId);
                String message = TIMManager.getInstance().getLoginUser() + "创建圈子";
                final TIMMessage createTips = MessageInfoUtil.buildGroupCustomMessage(MessageInfoUtil.GROUP_CREATE, message);
                TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, groupId);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //发送创建成功消息
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
     * 设置圈子头像
     *
     * @param groupId    圈子ID
     * @param groupUrl   圈子头像
     * @param imCallBack 结果回调
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
     * 设置圈子名字
     *
     * @param groupId    圈子ID
     * @param groupName  圈子名字
     * @param imCallBack 结果回调
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

                //ChatLayout  修改更新的名字
                ImActionDto imActionDto = new ImActionDto();
                imActionDto.setAction(ActionTags.UPDATE_GUOUP_NAME);
                imActionDto.setJsonStr(groupName);

                String str = JsonUtils.toJson(imActionDto);

                EjuHomeImEventCar.getDefault().post(str);

            }
        });


    }


    /**
     * 设置圈公告
     *
     * @param groupId         圈子ID
     * @param setNotification 圈子公告
     * @param imCallBack      结果回调
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
     * 设置圈介绍
     *
     * @param groupId    圈子ID
     * @param Introduce  圈子介绍
     * @param imCallBack 结果回调
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
     * 设置圈子是否公开(设置加圈选项)
     *
     * @param groupId          圈子ID
     * @param myTimGroupAddOpt 加群选项
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
//     * 设置圈消息是否置顶
//     *
//     * @param groupId 圈子ID
//     * @param flag    是否置顶
//     */
//    public void setNotification(String groupId, boolean flag) {
//        ConversationManagerKit.getInstance().setConversationTop(groupId, flag);
//
//    }


    public void deleteGroup(String groupId) {


    }

    /**
     * 邀请用户入圈
     *
     * @param groupId    圈子ID
     * @param memList    用户 ID 列表
     * @param imCallBack 结果回调---List<TIMGroupMemberResult>
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
     * 邀请用户入圈
     *
     * @param groupId     圈子ID
     * @param im_users_id IM通讯用户Id，用，隔开,例如 “101,1017,1023”
     * @param imCallBack  回调
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
     * 申请加入圈子
     *
     * @param groupId    圈子ID
     * @param reason     进圈理由
     * @param imCallBack 结果回调
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
     * 退出群聊
     * <p>
     * 私有群：全员可退出群组。
     * 公开群、聊天室和直播大群：群主不能退出
     *
     * @param groupId    圈子ID
     * @param imCallBack 结果回调
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
     * 删除群组成员
     *
     * @param groupId    圈子ID
     * @param memberList 用户ID列表
     * @param imCallBack 结果回调---List<TIMGroupMemberResult>
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
                //可以迭代出删除结果
            }
        });

    }


    /**
     * 删除群组成员
     *
     * @param groupId    圈子ID
     * @param mGroupInfo 圈子信息
     * @param memberList 用户ID列表
     * @param imCallBack 结果回调---List<TIMGroupMemberResult>
     */
    public void deleteGroupMember(String groupId, GroupInfo mGroupInfo, @NonNull List<GroupMemberInfo> memberList,
                                  @NonNull final ImCallBack imCallBack) {
        GroupInfoProvider provider = new GroupInfoProvider();
        provider.loadGroupInfo(mGroupInfo);
        provider.removeGroupMembers(memberList, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                ToastUtil.toastLongMessage("删除成员成功");

            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ToastUtil.toastLongMessage("删除成员失败:" + errCode + "=" + errMsg);
            }
        });
    }


    /**
     * 获取圈成员列表
     *
     * @param groupId    圈ID
     * @param imCallBack 结果回调  ---List<TIMGroupMemberInfo>
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
                //可以迭代出成员列表
            }
        });
    }


    /**
     * 获取圈成员列表(服务器)
     *
     * @param groupId    圈ID
     * @param imCallBack 结果回调  ---GuoupMemberDto
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
                            imCallBack.onError(888, "获取圈成员列表失败");
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        imCallBack.onError(888, "获取圈成员列表失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    /**
     * 获取加入的圈子列表
     *
     * @param imCallBack 结果回调----List<TIMGroupBaseInfo>
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
                //可以迭代出成员列表
            }
        });


    }

    /**
     * 获取腾讯云服务器存储的圈子信息
     *
     * @param groupIdList 圈子list
     * @param imCallBack  回调
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
     * 获取腾讯云服务器存储的圈子信息
     *
     * @param groupId    圈子ID
     * @param imCallBack 回调
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
     * 解散圈子
     *
     * @param groupId    圈子ID
     * @param imCallBack 结果回调
     */
    public void deleteGroup(@NonNull String groupId, @NonNull final ImCallBack imCallBack) {
        TIMGroupManager.getInstance().deleteGroup(groupId, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                imCallBack.onError(code, desc);
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 列表请参见错误码表
                LogUtils.d("login failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess() {
                //解散群组成功

                ImActionDto imActionDto = new ImActionDto();
                imActionDto.setAction(ActionTags.DELETE_GROUP_SUCCESS);


                String actionStr = JsonUtils.toJson(imActionDto);
                EjuHomeImEventCar.getDefault().post(actionStr);


                imCallBack.onSuccess("success");
            }
        });


    }

    /**
     * 转让圈子
     *
     * @param groupId    圈子ID
     * @param userId     新圈主ID
     * @param imCallBack 结果回调
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
     * 获取指定类型成员（可按照管理员、群主、普通成员拉取）接口定义如下
     *
     * @param groupId    圈 ID
     * @param flags      拉取资料标志
     * @param filter     角色过滤类型 GroupMemberRoleFilter.XX
     * @param custom     要获取的自定义 key 列表
     * @param nextSeq    nextSeq 分页拉取标志，第一次拉取填0，回调成功如果不为零，需要分页，传入再次拉取，直至为0
     * @param imCallBack 结果回调
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


    //获取我的圈子
    public void getMyGroup(final ImCallBack imCallBack) {


        TIMGroupManager.getInstance().getGroupList(new TIMValueCallBack<List<TIMGroupBaseInfo>>() {
            private List<MyGroupDto> myGroupDtoList = new ArrayList<>();

            // 查询所有消息列表用来获取最后一条消息
            List<TIMConversation> timConversationList = TIMManager.getInstance().getConversationList();
            private int groupSum = 0;//圈子数量
            int ait = 0;//@消息
            int aitCount = 0;
            Map<String, String> aitMsgMap = new HashMap<>();//存放有@消息的圈子ID

            @Override
            public void onError(int i, String s) {
                LogUtils.w("getMyGroupOnError-----" + s);


                if (i == 10004) {
                    //没有数据给个null的list
                    imCallBack.onSuccess(myGroupDtoList);
                } else {
                    imCallBack.onError(i, s);
                }


            }

            @Override
            public void onSuccess(List<TIMGroupBaseInfo> timGroupBaseInfos) {

                //组装群组信息
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


                                    //消息列表不为空
                                    if (timConversationList != null) {


                                        for (TIMConversation timConversation : timConversationList) {
                                            if (null != timConversation.getGroupName() && !"null".equals(timConversation.getGroupName())) {
                                                //是对应群组
                                                if (timConversation.getGroupName().equals(myGroupDto.getGroupName())) {
                                                    ConversationInfo conversationInfo = TIMConversation2ConversationInfo(timConversation);

                                                    if (null != conversationInfo) {


                                                        MessageInfo messageInfo = conversationInfo.getLastMessage();
                                                        if (null != messageInfo && null != messageInfo.getExtra()) {

                                                            //    LogUtils.w("idididi----" + conversationInfo.getId() + "类型----" + messageInfo.getMsgType() + "\n-----" + messageInfo.getExtra().toString() + "\n-----" + messageInfo.getFromUser());


                                                            myGroupDto.setLastMsgType(messageInfo.getMsgType());

                                                            myGroupDto.setLastMsg("" + messageInfo.getExtra().toString());

                                                            if (messageInfo.getExtra().toString().startsWith("\"<font")) {

                                                                String msg = messageInfo.getExtra().toString().substring(messageInfo.getExtra().toString().indexOf("</font>"));

                                                                myGroupDto.setLastMsg("" + msg.replace("</font>", ""));
                                                                myGroupDto.setLastMsgAuthor("系统消息");
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
                                    //返回组装数据
                                    if (groupSum == myGroupDtoList.size()) {

                                        if (groupSum == myGroupDtoList.size()) {


                                            //需要请求是否有@消息群组

                                            if (null != myGroupDtoList && myGroupDtoList.size() > 0) {
                                                List<MyGroupDto> selectAitList = new ArrayList<>();

                                                for (MyGroupDto groupDto : myGroupDtoList) {

                                                    if (groupDto.getUnreadMsgNum() > 0) {
                                                        selectAitList.add(groupDto);
                                                    }
                                                }


                                                aitCount = selectAitList.size();
                                                // TIMConversation mCurrentConversation = TIMManager.getInstance().getConversation(info.getType(), info.getId());


                                                //如果有需要查询未读消息的
                                                if (null != selectAitList && selectAitList.size() > 0) {


                                                    for (final MyGroupDto aitGroup : selectAitList) {
                                                        LogUtils.w("aitGroupgetPeer--------" + aitGroup.getGroupId());
                                                        TIMConversation mCurrentConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, aitGroup.getGroupId());

                                                        mCurrentConversation.getMessage(100, null, new TIMValueCallBack<List<TIMMessage>>() {
                                                            @Override
                                                            public void onError(int i, String s) {
                                                                ait++;


                                                                if (aitCount == ait) {
                                                                    LogUtils.w("@结束了-------");
                                                                    //循环@的Map


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
                                                                    // 获取到自定义消息的json数据


                                                                    if ((msgInfos.get(i).getElement() instanceof TIMCustomElem)) {


                                                                        TIMCustomElem elem = (TIMCustomElem) msgInfos.get(i).getElement();
                                                                        // 自定义的json数据，需要解析成bean实例
                                                                        CustomMessage data = null;

                                                                        try {
                                                                            data = new Gson().fromJson(new String(elem.getData()), CustomMessage.class);
                                                                        } catch (Exception e) {
                                                                            LogUtils.w("@invalid json: " + new String(elem.getData()) + " " + e.getMessage());
                                                                        }

                                                                        //是否是自定义消息且是@人消息且是未读消息且不是自己发送的消息且自己在@的Map中
                                                                        if (null != data && data.getAction() == CustomMessage.CUSTOM_AIT && !msgInfos.get(i).isRead() && !msgInfos.get(i).isSelf()) {

                                                                            CustomContentDto customContentDto = JsonUtils.fromJson(data.getContent(), CustomContentDto.class);

                                                                            if (null != customContentDto && null != customContentDto.getAitMap()) {
                                                                                Map<String, String> aitMap = customContentDto.getAitMap();
                                                                                if (null != aitMap.get(AppConfig.appImId) && !"".equals(aitMap.get(AppConfig.appImId))) {
                                                                                    //需要设置有@消息
                                                                                    aitMsgMap.put(timMessages.get(i).getConversation().getPeer(), "true");

                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }


                                                                if (aitCount == ait) {
                                                                    LogUtils.w("@结束了-------1111");
                                                                    //循环@的Map


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

//                                                    LogUtils.w("@结束了-------？？？？？");


                                                } else {
                                                    //没有需要查询未读消息
                                                    imCallBack.onSuccess(myGroupDtoList);
                                                }


                                            } else {
                                                //回调出数据
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
     * 获取单个圈子详情
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
                    //未读消息
                    myGroupDto.setUnreadMsgNum(unreadMsgNum);

                    myGroupDto.setRole(timGroupDetailInfoResult.getRole());
                    imCallBack.onSuccess(myGroupDto);

                }


            }
        });
    }


    /**
     * TIMConversation转换为ConversationInfo
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
     * 打开对应圈子聊天界面
     *
     * @param context   上下文
     * @param groupId   圈子ID
     * @param groupName 圈子名字
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
                        chatInfo.setChatName("我的圈子");
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
     * 打开对应圈子聊天界面且带黄V
     *
     * @param context   上下文
     * @param groupId   圈子ID
     * @param groupName 圈子名字
     */
    public void openChatActivityCarryV(final Context context, final String groupId, final String groupName) {


        if (null != groupId) {


            if (null == groupName) {
                getGroupInfo(groupId, new ImCallBack() {
                    @Override
                    public void onError(int var1, String var2) {


                        startChatAt(groupId, "我的圈子", context);
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
     * 打开群聊会话且带黄V
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
     * 发送群组提示消息
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
     * 告诉服务器全成员身份变动
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
     * 设置圈秘书（取消圈秘书）
     *
     * @param groupId                     圈ID
     * @param identifier                  被设置用户ID
     * @param groupMemberRoleTypeEnmunmer 被设置身份
     * @param imCallBack                  回调
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
     * 获取本人在群资料
     *
     * @param groupId    群ID
     * @param imCallBack 结果回调
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
     * 监听IM 是否有群组消息
     * 后续应该把群组信息返回出去
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
     * 同意入圈
     *
     * @param item       申请入圈信息
     * @param imCallBack 回调
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
     * 拒绝入圈
     *
     * @param item       申请入圈信息
     * @param imCallBack 回调
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
     * 发送群通知
     *
     * @param notification
     */
    public void setGroupNotification(String notification) {
        this.notification = notification;


    }

    /**
     * @param tag          通知唯一标识
     * @param notification 通知内容
     */
    public void setGroupNotification(String tag, String notification) {
        this.notification = notification;
        this.notificationTag = tag;

    }

    /**
     * 获取群通知标识
     *
     * @return
     */
    public String getGroupNotificationTag() {
        return this.notificationTag;
    }


    /**
     * 接收群通知
     *
     * @return
     */
    public String getGroupNotification() {
        return this.notification;
    }


    /**
     * 设置圈子是否公开
     *
     * @param groupId    圈子ID
     * @param isPublic   1：是，0：否
     * @param imCallBack 回调
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
     * 查询圈子信息（查询圈子是否公开，获取圈子推荐）
     *
     * @param groups     需要查询的群组ID，用”,”隔开，例如（”@TGS#1NVTZEAE4,@TGS#1CXTZEAET”）
     * @param IsFilter   是否需要过滤用户已经加入的群组（1：是，0：否）
     * @param imCallBack 回调
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
     * 设置是否免打扰
     *
     * @param conversation_id 会话 ID。会话ID组成方式：C2C+userID（单聊）GROUP+groupID（群聊） @TIM#SYSTEM（系统通知会话)
     * @param disturb         消息免打扰 0：否 1：是
     * @param imCallBack      回调
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
     * 设置是否置顶
     *
     * @param conversation_id 会话 ID。会话ID组成方式：C2C+userID（单聊）GROUP+groupID（群聊） @TIM#SYSTEM（系统通知会话)
     * @param stick           是否置顶 0：否 1：是
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
     * 查询会话状态（是否设置置顶  免打扰）
     *
     * @param conversations_id 会话ID，用”,”隔开。会话ID组成方式：C2C+userID（单聊）GROUP+groupID（群聊） @TIM#SYSTEM（系统通知会话)
     * @param imCallBack       回调
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
     * 根据用户ID批量获取用户名字
     *
     * @param imIds      用户ID
     * @param imCallBack 回调
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
     * 获取群 代办
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
     * 获取群代办
     *
     * @param imCallBack
     */
    public void getGroupPendencyList(final ImCallBack imCallBack) {

        final List<GroupApplyInfo> applies = new ArrayList<>();
        TIMGroupPendencyGetParam param = new TIMGroupPendencyGetParam();
        param.setTimestamp(0);//首次获取填 0
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
     * 获取群代办
     *
     * @param groupId    圈子ID
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
     * 同意某一条加群申请
     *
     * @param v2TIMGroupApplication ITEM
     * @param reason                同意理由
     * @param imCallBack            回调
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
     * 拒绝某一条加群申请
     *
     * @param v2TIMGroupApplication ITEM
     * @param reason                同意理由
     * @param imCallBack            回调
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
     * 发起群视频
     *
     * @param context
     * @param userIdList       被邀请者IM id
     * @param jurisdictionList 有权限开启视频的被邀请者IM id
     * @param groupId          群组ID
     */
    public void groupVideoCall(Context context, List<String> userIdList, List<String> jurisdictionList, String groupId) {//101444bee20249d9bcce813e11a8c393


        //去对应Activity
        Intent intent = new Intent(context, TRTCVideoCallActivity.class);
        intent.putExtra(Constants.OPEN_GROUP_VIDEO_CALL, Constants.OPEN_GROUP_VIDEO_CALL_10000);
        intent.putExtra(Constants.GROUP_ID, groupId);

        intent.putStringArrayListExtra(Constants.GROUP_AV_IM_LIST, (ArrayList<String>) userIdList);
        intent.putStringArrayListExtra(Constants.GROUP_PERMISSIONS_AV_IM_LIST, (ArrayList<String>) jurisdictionList);


        context.startActivity(intent);


    }

    /**
     * 接听群视频
     *
     * @param userIdList       被邀请者IM id
     * @param jurisdictionList 有权限开启视频的被邀请者IM id
     * @param groupId          群组ID
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
     * 邀请新的用户进行音视频
     *
     * @param userIdList       被邀请者IM id
     * @param jurisdictionList 有权限开启视频的被邀请者IM id
     * @param groupId          群组ID
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
     * 获取群成员信息
     *
     * @param groupID
     * @param filter   指定群成员类型
     * @param nextSeq  0
     * @param callback V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ALL：所有类型
     *                 V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_OWNER：群主
     *                 V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ADMIN：群管理员
     *                 V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_COMMON：普通群成员
     *                 nextSeq	分页拉取标志，第一次拉取填0，回调成功如果 nextSeq 不为零，需要分页，传入再次拉取，直至为0。
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
     * 根据关键字获取圈子列表（不包含未公开的，不包含用户已经加入的）
     *
     * @param imUser     IM用户ID（结果不包含用户已经加入的圈子）
     * @param nameKey    圈子名称包含的关键词
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
                            imCallBack.onError(88888, "getGroupListWithKey失败");
                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        imCallBack.onError(88888, "getGroupListWithKey失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }


    /**
     * Group发送一条文件消息
     *
     * @param fileUri        文件本地地址
     * @param conversationId 对方会话ID
     * @param imCallBack     回调
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
     * Group发送一条图片消息
     *
     * @param path           图片本地地址
     * @param conversationId 对方会话ID
     * @param imCallBack     回调
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
     * Group发送群上传了文件发送个提示消息
     *
     * @param groupId
     * @param callBack
     */
    public void sendTipsMessage(String groupId, final ImCallBack callBack) {


        String message = TIMManager.getInstance().getLoginUser() + "上传了文件";
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
     * 查询用户是否为黄V
     *
     * @param groupId    对方ID
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
                                imCallBack.onError(888888, "" + "非10000");
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
                imCallBack.onError(888, "设置单前群成员角色Map失败");
            }

            @Override
            public void onSuccess(Object var1) {

                List<TIMGroupMemberInfo> list = (List<TIMGroupMemberInfo>) var1;


                if (null != list && list.size() > 0) {
                    imCallBack.onSuccess("设置单前群成员角色Map成功");

                    if (null == groupRoleMap) {
                        groupRoleMap = new HashMap<>();
                    }

                    groupRoleMap.clear();
                    for (TIMGroupMemberInfo info : list) {
                        groupRoleMap.put(info.getUser(), info.getRole() + "");
                    }


                } else {
                    imCallBack.onError(888, "设置单前群成员角色Map失败");
                }


            }
        });


    }


}




