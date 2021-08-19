package com.eju.cy.audiovideo.enumer;


/**
 * 群类型枚举
 */
public enum GroupTypeEnmumer {


    PUBLIC("Public"),//公开

    PRIVATE("Private"),//私有

    CHAT_ROOM("ChatRoom");//聊天室


    private String groupType = "Public";

    private GroupTypeEnmumer(String appChannel) {
        this.groupType = appChannel;
    }

    public String value() {
        return this.groupType;
    }
}
