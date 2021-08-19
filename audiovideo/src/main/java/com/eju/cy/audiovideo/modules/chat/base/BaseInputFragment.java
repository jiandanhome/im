package com.eju.cy.audiovideo.modules.chat.base;

import com.eju.cy.audiovideo.base.BaseFragment;
import com.eju.cy.audiovideo.modules.chat.interfaces.IChatLayout;

public class BaseInputFragment extends BaseFragment {

    private IChatLayout mChatLayout;

    public IChatLayout getChatLayout() {
        return mChatLayout;
    }

    public BaseInputFragment setChatLayout(IChatLayout layout) {
        mChatLayout = layout;
        return this;
    }
}
