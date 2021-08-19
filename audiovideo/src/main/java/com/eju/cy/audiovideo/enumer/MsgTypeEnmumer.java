package com.eju.cy.audiovideo.enumer;


/**
 * 会话类型
 */
public enum MsgTypeEnmumer {

    C2C("C2C"),//单聊
    GROUP("GROUP");//群聊


    private String appChannel = "10";

    private MsgTypeEnmumer(String appChannel) {
        this.appChannel = appChannel;
    }

    public String value() {
        return this.appChannel;
    }

}
