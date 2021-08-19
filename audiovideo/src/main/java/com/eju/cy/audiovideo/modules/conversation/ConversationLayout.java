package com.eju.cy.audiovideo.modules.conversation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.base.IUIKitCallBack;
import com.eju.cy.audiovideo.component.TitleBarLayout;
import com.eju.cy.audiovideo.modules.conversation.base.ConversationInfo;
import com.eju.cy.audiovideo.modules.conversation.interfaces.IConversationAdapter;
import com.eju.cy.audiovideo.modules.conversation.interfaces.IConversationLayout;
import com.eju.cy.audiovideo.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class ConversationLayout extends RelativeLayout implements IConversationLayout {

    private TitleBarLayout mTitleBarLayout;
    private ConversationListLayout mConversationList;
    private ConversationListAdapter adapter;

    private List<ConversationInfo> customCadList = new ArrayList<>();

    public ConversationLayout(Context context) {
        super(context);
        init();
    }

    public ConversationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ConversationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化相关UI元素
     */
    private void init() {
        inflate(getContext(), R.layout.conversation_layout, this);
        mTitleBarLayout = findViewById(R.id.conversation_title);
        mConversationList = findViewById(R.id.conversation_list);
    }


    /**
     * 设置数据源
     *
     * @param conversationProvider
     */
    public void setDataSource(ConversationProvider conversationProvider) {

        LogUtils.w("setDataSource---"+conversationProvider.getDataSource().size());
        if (null != adapter) {

            adapter.setDataProvider(conversationProvider);
        } else {

            adapter = new ConversationListAdapter();
            adapter.setDataProvider(conversationProvider);
        }

      adapter.setItemAvatarRadius(100);

    }




    public void initDefault() {
        mTitleBarLayout.setTitle(getResources().getString(R.string.conversation_title), TitleBarLayout.POSITION.MIDDLE);
        mTitleBarLayout.getLeftGroup().setVisibility(View.GONE);
        mTitleBarLayout.setRightIcon(R.drawable.conversation_more);

        adapter = new ConversationListAdapter();
        mConversationList.setAdapter(adapter);




//        mTitleBarLayout.setTitle(getResources().getString(R.string.conversation_title), TitleBarLayout.POSITION.MIDDLE);
//        mTitleBarLayout.getLeftGroup().setVisibility(View.GONE);
//        mTitleBarLayout.setRightIcon(R.drawable.conversation_more);
//




    }

    public TitleBarLayout getTitleBar() {
        return mTitleBarLayout;
    }

    @Override
    public void setParentLayout(Object parent) {

    }

    @Override
    public ConversationListLayout getConversationList() {
        return mConversationList;
    }

    public void addConversationInfo(int position, ConversationInfo info) {
        LogUtils.w("addConversationInfo----");
        mConversationList.getAdapter().addItem(position, info);
    }

    public void removeConversationInfo(int position) {
        mConversationList.getAdapter().removeItem(position);
    }

    @Override
    public void setConversationTop(int position, ConversationInfo conversation) {
        ConversationManagerKit.getInstance().setConversationTop(position, conversation);
    }

    @Override
    public void deleteConversation(int position, ConversationInfo conversation) {
        ConversationManagerKit.getInstance().deleteConversation(position, conversation);
    }
}
