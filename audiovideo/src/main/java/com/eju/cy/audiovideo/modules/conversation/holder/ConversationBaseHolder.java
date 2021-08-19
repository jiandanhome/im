package com.eju.cy.audiovideo.modules.conversation.holder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.eju.cy.audiovideo.modules.conversation.base.ConversationInfo;
import com.eju.cy.audiovideo.modules.conversation.ConversationListAdapter;

public abstract class ConversationBaseHolder extends RecyclerView.ViewHolder {

    protected View rootView;
    protected ConversationListAdapter mAdapter;

    public ConversationBaseHolder(View itemView) {
        super(itemView);
        rootView = itemView;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mAdapter = (ConversationListAdapter) adapter;
    }

    public abstract void layoutViews(ConversationInfo conversationInfo, int position);

}
