package com.eju.cy.audiovideo.modules.chat;

import android.content.Context;

import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.ait.AitManager;
import com.eju.cy.audiovideo.controller.GroupController;
import com.eju.cy.audiovideo.controller.ImCallBack;
import com.eju.cy.audiovideo.dialog.AnnouncementDialog;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.modules.chat.base.AbsChatLayout;
import com.eju.cy.audiovideo.modules.chat.base.ChatInfo;
import com.eju.cy.audiovideo.modules.chat.base.ChatManagerKit;
import com.eju.cy.audiovideo.modules.group.info.GroupInfo;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.observer.EjuHomeImObserver;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.tags.Constants;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.ext.group.TIMGroupDetailInfoResult;
import com.tencent.imsdk.v2.V2TIMGroupApplication;

import java.util.List;

import static com.eju.cy.audiovideo.tags.ActionTags.ACTION_CLICK_DELETE_CARD;


public class ChatLayout extends AbsChatLayout {

    private GroupInfo mGroupInfo;
    private GroupChatManagerKit mGroupChatManager;
    private C2CChatManagerKit mC2CChatManager;
    private boolean isGroup = false;

    public ChatLayout(Context context) {
        super(context);
    }

    public ChatLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setChatInfo(ChatInfo chatInfo) {
        getInputLayout().setChatInfo(chatInfo);

        super.setChatInfo(chatInfo);
        if (chatInfo == null) {
            return;
        }

        if (chatInfo.getType() == TIMConversationType.C2C) {
            isGroup = false;
        } else {
            isGroup = true;
        }

        if (isGroup) {


            mGroupChatManager = GroupChatManagerKit.getInstance();
            //   mGroupChatManager.setGroupHandler(this);
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setId(chatInfo.getId());
            groupInfo.setChatName(chatInfo.getChatName());
            mGroupChatManager.setCurrentChatInfo(groupInfo);
            mGroupInfo = groupInfo;
            loadChatMessages(null);
            loadApplyList(false);
            getTitleBar().getRightIcon().setImageResource(R.drawable.chat_group);

            mGroupApplyLayout.setOnNoticeClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    //入圈信息处理圈代办
                    //入圈信息处理圈代办
                    //入圈信息处理圈代办
//                    Intent intent = new Intent(getContext(), GroupApplyManagerActivity.class);
//                    intent.putExtra(TUIKitConstants.Group.GROUP_INFO, mGroupInfo);
//                    getContext().startActivity(intent);
//

                    loadApplyList(true);

                }
            });


            //群通知
            //群通知
            //群通知
            getGroupAnnouncement(chatInfo);


        } else {
            getTitleBar().getRightIcon().setImageResource(R.drawable.chat_c2c);
            mC2CChatManager = C2CChatManagerKit.getInstance();
            mC2CChatManager.setCurrentChatInfo(chatInfo);
            loadChatMessages(null);
        }
    }

    @Override
    public ChatManagerKit getChatManager() {
        if (isGroup) {
            return mGroupChatManager;
        } else {
            return mC2CChatManager;
        }
    }

    /**
     * 加载群代办
     */
    public void loadApplyList(final boolean isClick) {


        GroupController.getInstance().getGroupPendencyList(mGroupInfo.getId(), new ImCallBack() {
            @Override
            public void onError(int var1, String var2) {
                LogUtils.w("loadApplyList---" + var1 + "\n" + var2);
            }

            @Override
            public void onSuccess(Object data) {
                int count = 0;
                List<V2TIMGroupApplication> applies = (List<V2TIMGroupApplication>) data;


                for (V2TIMGroupApplication v2TIMGroupApplication : applies) {
                    if (v2TIMGroupApplication.getHandleStatus() == 0) {
                        count++;
                    }

                }

                if (applies != null && count > 0) {
                    mGroupApplyLayout.getContent().setText("圈待办： " + getContext().getString(R.string.group_apply_tips, count));
                    mGroupApplyLayout.setVisibility(VISIBLE);
                    mGroupApplyLayout.showToDo();


                    if (isClick) {


                        /**----------new-----*/
                        ImActionDto imAction = new ImActionDto();
                        imAction.setAction(ActionTags.ACTION_CLICK_GROUP_LOAD_APPLY);

                        String strMsg = JsonUtils.toJson(mGroupInfo);
                        imAction.setJsonStr(strMsg);

                        String sendStrMsg = JsonUtils.toJson(imAction);
                        EjuHomeImEventCar.getDefault().post(sendStrMsg);

                    }


                } else {
                    if (isClick) {

                        /**----------new-----*/
                        ImActionDto imAction = new ImActionDto();
                        imAction.setAction(ActionTags.ACTION_CLICK_GROUP_LOAD_APPLY);

                        String strMsg = JsonUtils.toJson(mGroupInfo);
                        imAction.setJsonStr(strMsg);

                        String sendStrMsg = JsonUtils.toJson(imAction);
                        EjuHomeImEventCar.getDefault().post(sendStrMsg);


                    }
                    mGroupApplyLayout.setVisibility(GONE);

                }


            }
        });


    }


    /**
     * 获取群公告
     *
     * @param chatInfo
     */
    private void getGroupAnnouncement(ChatInfo chatInfo) {

        if (null != chatInfo) {
            GroupController.getInstance().getGroupInfo(chatInfo.getId(), new ImCallBack() {
                @Override
                public void onError(int var1, String var2) {
                    LogUtils.w("getGroupAnnouncement------onError");
                }

                @Override
                public void onSuccess(Object var1) {
                    List<TIMGroupDetailInfoResult> timGroupDetailInfoResults = (List<TIMGroupDetailInfoResult>) var1;
                    setNotification(timGroupDetailInfoResults);
                }
            });
        }


    }

    private void setNotification(List<TIMGroupDetailInfoResult> list) {
        List<TIMGroupDetailInfoResult> timGroupDetailInfoResults = list;

        if (null != timGroupDetailInfoResults && timGroupDetailInfoResults.size() > 0) {

            String notification = timGroupDetailInfoResults.get(0).getGroupNotification();
            GroupController.getInstance().setGroupNotification(notification, notification);


            if (!"".equals(GroupController.getInstance().getGroupNotification())) {

                if (!SPStaticUtils.getString(Constants.NOTIFICATION_TAG).equals(GroupController.getInstance().getGroupNotificationTag())) {
                    getNoticeLayout().setVisibility(VISIBLE);
                    getNoticeLayout().showNotification(GroupController.getInstance().getGroupNotification());
                }


            } else {
                getNoticeLayout().setVisibility(GONE);
            }


            getNoticeLayout().setOnNoticeClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    getNoticeLayout().setVisibility(GONE);

                    //发送事件
                    ImActionDto imActionDto = new ImActionDto();
                    imActionDto.setAction(ActionTags.ACTION_CLICK_GROUP_NOTIFICATION);
                    imActionDto.setJsonStr(GroupController.getInstance().getGroupNotification());
                    String sendStr = JsonUtils.toJson(imActionDto);
                    EjuHomeImEventCar.getDefault().post(sendStr);


                    //设置是否可再次点
                    SPStaticUtils.put(Constants.NOTIFICATION_TAG, GroupController.getInstance().getGroupNotificationTag());


                    //dialog
                    AnnouncementDialog announcementDialog = new AnnouncementDialog(getContext());
                    announcementDialog.builder().setCancelable(true).setCancelOutside(true).setDialogWidth(1f)
                            .setTvValue(GroupController.getInstance().getGroupNotification());

                    announcementDialog.setNegativeButton(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });


                    announcementDialog.show();


                }
            });
        }


    }

    public AitManager getAitManager() {


        return getInputLayout().getAitManager();
    }


    @Override
    public void sendMessage(MessageInfo msg, boolean retry) {
        super.sendMessage(msg, retry);
//
//        if (null != getInputLayout().getAitManager()) {
//            getInputLayout().getAitManager().reset();
//        }

    }

    @Override
    public void exitChat() {
        super.exitChat();

//        if (null != getInputLayout().getAitManager()) {
//            getInputLayout().getAitManager().reset();
//        }
    }



}
