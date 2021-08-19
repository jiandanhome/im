package com.eju.cy.audiovideo.enumer;


/**
 * 应用通道
 */
public enum AppChannelEnmumer {

    JDMF_AND_YILOU("10"),//美房-易楼
    JDD("11");//简单搭


    private String appChannel = "10";

    private AppChannelEnmumer(String appChannel) {
        this.appChannel = appChannel;
    }

    public String value() {
        return this.appChannel;
    }

}
