package com.eju.cy.audiovideosample.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideosample.R;
import com.tencent.imsdk.TIMConversationType;
import com.eju.cy.audiovideo.activity.chat.ChatActivity;
import com.eju.cy.audiovideo.component.action.PopDialogAdapter;
import com.eju.cy.audiovideo.component.action.PopMenuAction;
import com.eju.cy.audiovideo.modules.chat.base.ChatInfo;
import com.eju.cy.audiovideo.modules.conversation.ConversationLayout;
import com.eju.cy.audiovideo.modules.conversation.ConversationListLayout;
import com.eju.cy.audiovideo.modules.conversation.base.ConversationInfo;
import com.eju.cy.audiovideo.tags.Constants;

import java.util.ArrayList;
import java.util.List;


/**
 * @ Name: Caochen
 * @ Date: 2020-05-11
 * @ Time: 14:28
 * @ Description：  会话列表
 */
public class SessionListActivity extends AppCompatActivity {


    private View mBaseView;
    private ConversationLayout mConversationLayout;
    private ListView mConversationPopList;
    private PopDialogAdapter mConversationPopAdapter;
    private PopupWindow mConversationPopWindow;
    private List<PopMenuAction> mConversationPopActions = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_list);

        initView();


    }

    private void initView() {
        // 从布局文件中获取会话列表面板
        mConversationLayout = findViewById(R.id.conversation_layout);

        // 会话列表面板的默认UI和交互初始化
        mConversationLayout.initDefault();
        // 通过API设置ConversataonLayout各种属性的样例，开发者可以打开注释，体验效果
//        ConversationLayoutHelper.customizeConversation(mConversationLayout);


        mConversationLayout.getConversationList().setOnItemClickListener(new ConversationListLayout.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, ConversationInfo conversationInfo) {
                //此处为demo的实现逻辑，更根据会话类型跳转到相关界面，开发者可根据自己的应用场景灵活实现
                startChatActivity(conversationInfo);
            }
        });
        mConversationLayout.getConversationList().setOnItemLongClickListener(new ConversationListLayout.OnItemLongClickListener() {
            @Override
            public void OnItemLongClick(View view, int position, ConversationInfo conversationInfo) {
                //startPopShow(view, position, conversationInfo);
            }
        });
//        initTitleAction();
//        initPopMenuAction();
    }


    private void startChatActivity(ConversationInfo conversationInfo) {
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setType(conversationInfo.isGroup() ? TIMConversationType.Group : TIMConversationType.C2C);
        chatInfo.setId(conversationInfo.getId());
        chatInfo.setChatName(conversationInfo.getTitle());
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.CHAT_INFO, chatInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);

        LogUtils.w("conversationInfo.getId()-----" + conversationInfo.getId());

    }


}
