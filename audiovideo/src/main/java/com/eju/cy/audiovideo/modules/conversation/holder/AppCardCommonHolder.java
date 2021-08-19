package com.eju.cy.audiovideo.modules.conversation.holder;

import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.controller.ConversationController;
import com.eju.cy.audiovideo.modules.conversation.base.ConversationIconView;
import com.eju.cy.audiovideo.modules.conversation.base.ConversationInfo;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.eju.cy.audiovideo.utils.DateTimeUtil;
import com.eju.cy.audiovideo.utils.TUIKitConstants;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class AppCardCommonHolder extends ConversationBaseHolder {

    public ConversationIconView conversationIconView;
    protected RelativeLayout leftItemLayout;
    protected TextView titleText;
    protected TextView messageText;
    protected TextView timelineText;
    protected TextView unreadText;

    protected ImageView iv_user_angle;

    public AppCardCommonHolder(View itemView) {
        super(itemView);
        leftItemLayout = rootView.findViewById(R.id.item_left);
        //头像
        conversationIconView = rootView.findViewById(R.id.conversation_icon);
        //标题
        titleText = rootView.findViewById(R.id.conversation_title);
        //最后一条消息
        messageText = rootView.findViewById(R.id.conversation_last_msg);
        //时间
        timelineText = rootView.findViewById(R.id.conversation_time);
        //未读消息
        unreadText = rootView.findViewById(R.id.conversation_unread);


        iv_user_angle = rootView.findViewById(R.id.iv_user_angle);
    }

    public void layoutViews(ConversationInfo conversation, int position) {
        MessageInfo lastMsg = conversation.getLastMessage();
        if (lastMsg != null && lastMsg.getStatus() == MessageInfo.MSG_STATUS_REVOKE) {
            if (lastMsg.isSelf()) {
                lastMsg.setExtra("您撤回了一条消息");
            } else if (lastMsg.isGroup()) {
                String message = TUIKitConstants.covert2HTMLString(
                        TextUtils.isEmpty(lastMsg.getGroupNameCard())
                                ? lastMsg.getFromUser()
                                : lastMsg.getGroupNameCard());
                lastMsg.setExtra(message + "撤回了一条消息");
            } else {
                lastMsg.setExtra("对方撤回了一条消息");
            }
        }

        if (conversation.isTop()) {
            leftItemLayout.setBackgroundColor(rootView.getResources().getColor(R.color.conversation_top_color));
        } else {
            leftItemLayout.setBackgroundColor(Color.WHITE);
        }

        titleText.setText(conversation.getTitle());
        messageText.setText("");
        timelineText.setText("");
        if (lastMsg != null) {
            if (lastMsg.getExtra() != null) {
                messageText.setText(Html.fromHtml(lastMsg.getExtra().toString()));
                messageText.setTextColor(rootView.getResources().getColor(R.color.list_bottom_text_bg));
            }
            timelineText.setText(DateTimeUtil.getTimeFormatText(new Date(lastMsg.getMsgTime() * 1000)));
        }

//        if (conversation.getUnRead() > 0) {
//            unreadText.setVisibility(View.VISIBLE);
//            if (conversation.getUnRead() > 99) {
//                unreadText.setText("99+");
//            } else {
//                unreadText.setText("" + conversation.getUnRead());
//            }
//        } else {
//            unreadText.setVisibility(View.GONE);
//        }

        unreadText.setVisibility(View.GONE);

        conversationIconView.setRadius(mAdapter.getItemAvatarRadius());
        if (mAdapter.getItemDateTextSize() != 0) {
            timelineText.setTextSize(mAdapter.getItemDateTextSize());
        }
        if (mAdapter.getItemBottomTextSize() != 0) {
            messageText.setTextSize(mAdapter.getItemBottomTextSize());
        }
        if (mAdapter.getItemTopTextSize() != 0) {
            titleText.setTextSize(mAdapter.getItemTopTextSize());
        }
        if (!mAdapter.hasItemUnreadDot()) {
            unreadText.setVisibility(View.GONE);
        }

        if (conversation.getIconUrlList() != null) {
            conversationIconView.setConversation(conversation);
        }


        //设置角标


        Iterator<Map.Entry<String, Integer>> entries = ConversationController.getInstance().getUserLevel().entrySet().iterator();


        if (null != entries) {
            while (entries.hasNext()) {
                Map.Entry<String, Integer> entry = entries.next();
                String key = entry.getKey();
                Integer value = entry.getValue();


                if (null != key && key.equals(conversation.getId())) {

                    if (null != value && value > 0) {

                        iv_user_angle.setVisibility(View.VISIBLE);

                    } else {
                        iv_user_angle.setVisibility(View.GONE);
                    }

                }


            }
        }





            //// 由子类设置指定消息类型的views
            layoutVariableViews(conversation, position);
    }

    public void layoutVariableViews(ConversationInfo conversationInfo, int position) {

    }
}
