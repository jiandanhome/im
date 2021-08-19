package com.eju.cy.audiovideo.component.gatherimage;

import com.eju.cy.audiovideo.utils.ScreenUtil;
import com.eju.cy.audiovideo.modules.message.MessageInfo;

public abstract class DynamicChatUserIconView extends DynamicLayoutView<MessageInfo> {

    private int iconRadius = -1;

    public int getIconRadius() {
        return iconRadius;
    }

    /**
     * 设置聊天头像圆角
     *
     * @param iconRadius
     */
    public void setIconRadius(int iconRadius) {
        this.iconRadius = ScreenUtil.getPxByDp(iconRadius);
    }
}
