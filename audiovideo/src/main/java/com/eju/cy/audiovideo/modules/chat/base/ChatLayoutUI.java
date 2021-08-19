package com.eju.cy.audiovideo.modules.chat.base;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.component.NoticeLayout;
import com.eju.cy.audiovideo.component.TitleBarLayout;
import com.eju.cy.audiovideo.controller.GroupController;
import com.eju.cy.audiovideo.controller.ImCallBack;
import com.eju.cy.audiovideo.enumer.MsgTypeEnmumer;
import com.eju.cy.audiovideo.modules.chat.interfaces.IChatLayout;
import com.eju.cy.audiovideo.modules.chat.layout.input.InputLayout;
import com.eju.cy.audiovideo.modules.chat.layout.message.MessageLayout;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.ext.group.TIMGroupDetailInfoResult;

import java.util.List;

public abstract class ChatLayoutUI extends LinearLayout implements IChatLayout {

    protected NoticeLayout mGroupApplyLayout;
    protected View mRecordingGroup;
    protected ImageView mRecordingIcon;
    protected TextView mRecordingTips;
    private TitleBarLayout mTitleBar;
    private MessageLayout mMessageLayout;
    private InputLayout mInputLayout;
    private NoticeLayout mNoticeLayout;
    private ChatInfo mChatInfo;
    private  TextView tv_ait;

    public ChatLayoutUI(Context context) {
        super(context);
        initViews();
    }

    public ChatLayoutUI(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public ChatLayoutUI(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        inflate(getContext(), R.layout.chat_layout, this);

        mTitleBar = findViewById(R.id.chat_title_bar);
        mMessageLayout = findViewById(R.id.chat_message_layout);
        mInputLayout = findViewById(R.id.chat_input_layout);
        mRecordingGroup = findViewById(R.id.voice_recording_view);
        mRecordingIcon = findViewById(R.id.recording_icon);
        mRecordingTips = findViewById(R.id.recording_tips);
        mGroupApplyLayout = findViewById(R.id.chat_group_apply_layout);
        mNoticeLayout = findViewById(R.id.chat_notice_layout);
        tv_ait = findViewById(R.id.tv_ait);


    }

    public TextView  getAitTextView(){
        return  tv_ait;
    }

    /**
     * 初始化底部菜单
     */
    protected void initActionMenu() {
        if (mChatInfo.getType().equals(TIMConversationType.C2C)) {
            mInputLayout.setMsgType(MsgTypeEnmumer.C2C);
        } else {
            mInputLayout.setMsgType(MsgTypeEnmumer.GROUP);
        }

    }

    @Override
    public InputLayout getInputLayout() {
        return mInputLayout;
    }

    @Override
    public MessageLayout getMessageLayout() {
        return mMessageLayout;
    }

    @Override
    public NoticeLayout getNoticeLayout() {
        return mNoticeLayout;
    }

    @Override
    public ChatInfo getChatInfo() {
        return mChatInfo;
    }

    @Override
    public void setChatInfo(ChatInfo chatInfo) {
        mChatInfo = chatInfo;
        if (chatInfo == null) {
            return;
        }


        String chatTitle = chatInfo.getChatName();
        getTitleBar().setTitle(chatTitle, TitleBarLayout.POSITION.MIDDLE);


        getTitleBar().setUserRole(chatInfo.getUserRole());

        initActionMenu();

        //设置圈人数
        if (mChatInfo.getType() == TIMConversationType.Group) {


            GroupController.getInstance().getGroupInfo(mChatInfo.getId(), new ImCallBack() {


                @Override
                public void onError(int var1, String var2) {

                }

                @Override
                public void onSuccess(Object var1) {


                    List<TIMGroupDetailInfoResult> timGroupDetailInfoResults = (List<TIMGroupDetailInfoResult>) var1;

                    if (null != timGroupDetailInfoResults && timGroupDetailInfoResults.size() > 0) {

                        getTitleBar().setGroupPeopleSum("("+timGroupDetailInfoResults.get(0).getMemberNum() + ")");

                    }
                }
            });

        }


    }

    @Override
    public void exitChat() {

    }

    @Override
    public void initDefault() {

    }

    @Override
    public void loadMessages() {

    }

    @Override
    public void sendMessage(MessageInfo msg, boolean retry) {

    }

    @Override
    public TitleBarLayout getTitleBar() {
        return mTitleBar;
    }

    @Override
    public void setParentLayout(Object parent) {

    }


    public   ChatInfo getmChatInfo(){


        return  mChatInfo;
    }
}
