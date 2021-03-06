package com.eju.cy.audiovideo.modules.chat.layout.message.holder;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.controller.GroupController;
import com.eju.cy.audiovideo.controller.ImCallBack;
import com.eju.cy.audiovideo.controller.UserInfoController;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.tags.AppConfig;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.eju.cy.audiovideo.utils.TUIKitConstants;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMUserProfile;

import java.util.ArrayList;
import java.util.List;

public class MessageTipsHolder extends MessageEmptyHolder {

    private TextView mChatTipsTv, chat_tips_operationId, chat_tips_acceptId;



    public MessageTipsHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getVariableLayout() {
        return R.layout.message_adapter_content_tips;
    }

    @Override
    public void initVariableViews() {
        mChatTipsTv = rootView.findViewById(R.id.chat_tips_tv);

        chat_tips_operationId = rootView.findViewById(R.id.chat_tips_operationId);

        chat_tips_acceptId = rootView.findViewById(R.id.chat_tips_acceptId);
    }

    @Override
    public void layoutViews(final MessageInfo msg, int position) {
        super.layoutViews(msg, position);


        if (properties.getTipsMessageBubble() != null) {
            mChatTipsTv.setBackground(properties.getTipsMessageBubble());
            chat_tips_operationId.setBackground(properties.getTipsMessageBubble());

            chat_tips_acceptId.setBackground(properties.getTipsMessageBubble());
        }
        if (properties.getTipsMessageFontColor() != 0) {
            mChatTipsTv.setTextColor(properties.getTipsMessageFontColor());

            chat_tips_operationId.setTextColor(properties.getTipsMessageFontColor());

            chat_tips_acceptId.setTextColor(properties.getTipsMessageFontColor());
        }
        if (properties.getTipsMessageFontSize() != 0) {
            mChatTipsTv.setTextSize(properties.getTipsMessageFontSize());

            chat_tips_operationId.setTextSize(properties.getTipsMessageFontSize());

            chat_tips_acceptId.setTextSize(properties.getTipsMessageFontSize());
        }

        if (msg.getStatus() == MessageInfo.MSG_STATUS_REVOKE) {
            if (msg.isSelf()) {
                msg.setExtra("????????????????????????");
            } else if (msg.isGroup()) {
                String message = TUIKitConstants.covert2HTMLString(
                        (TextUtils.isEmpty(msg.getGroupNameCard())
                                ? msg.getFromUser()
                                : msg.getGroupNameCard()));
                msg.setExtra(message + "?????????????????????");
            } else {
                msg.setExtra("???????????????????????????");
            }
        }

        if (msg.getStatus() == MessageInfo.MSG_STATUS_REVOKE
                || (msg.getMsgType() >= MessageInfo.MSG_TYPE_GROUP_CREATE
                && msg.getMsgType() <= MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE)) {
            if (msg.getExtra() != null) {
                mChatTipsTv.setText(Html.fromHtml(msg.getExtra().toString()));
            }
        }


        chat_tips_operationId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String msgStr = msg.getExtra().toString();
                //???????????????  ????????????????????? ??????
                if (msg.isGroup() && msg.getExtra() != null && msg.getMsgType() >= MessageInfo.MSG_TYPE_GROUP_CREATE && msg.getMsgType() <= MessageInfo.MSG_TYPE_GROUP_UPLOAD_FILE) {
                    if (msg.getMsgType() == MessageInfo.MSG_TYPE_GROUP_CREATE) {
                        pushClick(msg.getFromUser());
                    } else if (msg.getMsgType() == MessageInfo.MSG_TYPE_GROUP_UPLOAD_FILE) {
                        pushClick(msg.getFromUser());
                    } else if (msg.getMsgType() == MessageInfo.MSG_TYPE_GROUP_KICK) {
                        pushClick(msgStr.substring(msgStr.indexOf(">") + 1, msgStr.indexOf("</font>")));
                    } else if (msg.getMsgType() == MessageInfo.MSG_TYPE_GROUP_JOIN) {
                        pushClick(msgStr.substring(msgStr.indexOf(">") + 1, msgStr.indexOf("</font>")));
                    } else if (msg.getMsgType() == MessageInfo.MSG_TYPE_GROUP_QUITE) {
                        pushClick(msgStr.substring(msgStr.indexOf(">") + 1, msgStr.indexOf("</font>")));
                    } else if (msg.getMsgType() == MessageInfo.MSG_TYPE_GROUP_MODIFY_NAME) {
                        pushClick(msgStr.substring(msgStr.indexOf(">") + 1, msgStr.indexOf("</font>")));
                    } else if (msg.getMsgType() == MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE) {
                        pushClick(msgStr.substring(msgStr.indexOf(">") + 1, msgStr.indexOf("</font>")));
                    }


                }

            }
        });


        chat_tips_acceptId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msg.isGroup() && msg.getExtra() != null && msg.getMsgType() >= MessageInfo.MSG_TYPE_GROUP_CREATE && msg.getMsgType() <= MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE) {
                    if (msg.getMsgType() == MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE) {
                        final String msgStr = msg.getExtra().toString();
                        if (msg.getExtra().toString().contains("???????????????")) {
                            String userId = msgStr.substring(msgStr.indexOf("???") + 1, msgStr.length()).replace("\"", "");
                            pushClick(userId);
                        }

                    }
                }


            }
        });


        if (msg.isGroup() && msg.getExtra() != null) {
            //    LogUtils.w("--------???ID???" + msg.getTIMMessage().getConversation().getPeer());
            if (msg.getMsgType() == MessageInfo.MSG_TYPE_GROUP_CREATE) {

                setView(msg.getFromUser(), "????????????", "");//show
            }
            if (msg.getMsgType() == MessageInfo.MSG_TYPE_GROUP_UPLOAD_FILE) {
                if (isShowMsg()) {
                    setView(msg.getFromUser(), "???????????????", "");//show
                }

            } else if (msg.getMsgType() == MessageInfo.MSG_TYPE_GROUP_KICK) {

                if (isShowMsg()) {
                    setView(msg.getExtra().toString(), "???????????????");//no
                }

            } else if (msg.getMsgType() == MessageInfo.MSG_TYPE_GROUP_JOIN) {
                setView(msg.getExtra().toString(), "????????????");//show

            } else if (msg.getMsgType() == MessageInfo.MSG_TYPE_GROUP_QUITE) {
                // setView(msg.getExtra().toString(), "????????????");//no

            } else if (msg.getMsgType() == MessageInfo.MSG_TYPE_GROUP_MODIFY_NAME) {

                final String msgStr = msg.getExtra().toString();
                setView(msgStr, "??????????????????");//show

            } else if (msg.getMsgType() == MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE) {


                final String msgStr = msg.getExtra().toString();


                if (msgStr.contains("??????????????????")) {//show
                    setView(msgStr, "??????????????????");

                } else if (msgStr.contains("???????????????")) {//show
                    setView(msgStr, "???????????????");
                } else if (msgStr.contains("??????????????????")) {//show
                    setView(msgStr, "??????????????????");
                } else if (msgStr.contains("??????????????????")) {//show
                    setView(msgStr, "??????????????????");

                } else if (msgStr.contains("??????????????????")) {//no


                    if (isShowMsg()) {
                        setView(msgStr, "??????????????????");
                    }
                } else if (msgStr.contains("??????????????????")) {//no
                    if (isShowMsg()) {
                        setView(msgStr, "??????????????????");
                    }

                }


            }


        }


    }


    private void setView(final String msgStr, final String actionStr) {

        UserInfoController.getInstance().getUsersProfile(msgStr.substring(msgStr.indexOf(">") + 1, msgStr.indexOf("</font>")), false, new ImCallBack() {
            @Override
            public void onError(int var1, String var2) {

            }

            @Override
            public void onSuccess(Object var1) {
                List<TIMUserProfile> timUserProfiles = (List<TIMUserProfile>) var1;
                if (null != timUserProfiles && timUserProfiles.size() > 0) {
                    chat_tips_operationId.setText("\"" + timUserProfiles.get(0).getNickName() + "\"");
                    mChatTipsTv.setText(actionStr);

                    switch (actionStr) {


                        case "??????????????????":
                        case "??????????????????":
                        case "??????????????????":
                            chat_tips_acceptId.setText(msgStr.substring(msgStr.indexOf("???") + 1, msgStr.length()));
                            break;
                        case "???????????????":


                            UserInfoController.getInstance().getUsersProfile(msgStr.substring(msgStr.indexOf("???") + 1, msgStr.length()).replace("\"", ""), false, new ImCallBack() {


                                @Override
                                public void onError(int var1, String var2) {

                                }

                                @Override
                                public void onSuccess(Object var1) {
                                    List<TIMUserProfile> timUserProfiles = (List<TIMUserProfile>) var1;
                                    if (null != timUserProfiles && timUserProfiles.size() > 0) {
//                                        chat_tips_acceptId.setText(msgStr.substring(msgStr.indexOf("???") + 1, msgStr.length()));

                                        chat_tips_acceptId.setText("\"" + timUserProfiles.get(0).getNickName() + "\"");
                                    }

                                }
                            });


                            break;
                        case "??????????????????":
                            chat_tips_acceptId.setText(msgStr.substring(msgStr.indexOf("???") + 1, msgStr.length()));
                            break;
                    }

                }


            }
        });

    }

    private void setView(final String msgStr, final String actionStr, String one) {

        UserInfoController.getInstance().getUsersProfile(msgStr, false, new ImCallBack() {
            @Override
            public void onError(int var1, String var2) {

            }

            @Override
            public void onSuccess(Object var1) {
                List<TIMUserProfile> timUserProfiles = (List<TIMUserProfile>) var1;
                if (null != timUserProfiles && timUserProfiles.size() > 0) {
                    chat_tips_operationId.setText("\"" + timUserProfiles.get(0).getNickName() + "\"");
                    mChatTipsTv.setText(actionStr);


                }


            }
        });

    }

    /**
     * ??????????????????
     * @return
     */
    private boolean isShowMsg() {

        if (null != GroupController.getInstance().getNowOpenGroupRole() && GroupController.getInstance().getNowOpenGroupRole().size() > 0) {
                LogUtils.w("?????????-------"+GroupController.getInstance().getNowOpenGroupRole().get(AppConfig.appImId));
            if (null != GroupController.getInstance().getNowOpenGroupRole().get(AppConfig.appImId) && !GroupController.getInstance().getNowOpenGroupRole().get(AppConfig.appImId).equals("200")) {

                return true;

            } else {

                return false;
            }


        }


        return true;
    }

    /**
     * ???????????????????????????????????????
     *
     * @param userId
     */
    private void pushClick(String userId) {
        ImActionDto imActionDto = new ImActionDto();
        imActionDto.setAction(ActionTags.ACTION_CLICK_GROUP_OPERATION_MSG);
        imActionDto.setJsonStr(userId);
        String jsonStr = JsonUtils.toJson(imActionDto);
        EjuHomeImEventCar.getDefault().post(jsonStr);

    }


}
