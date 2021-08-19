package com.eju.cy.audiovideo.dto;

public class ReplaceMsgDto {

    String MsgType = "";

    public MsgContentDto getMsgContent() {
        return MsgContent;
    }

    public void setMsgContent(MsgContentDto msgContent) {
        MsgContent = msgContent;
    }

    MsgContentDto  MsgContent ;

    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }


}
