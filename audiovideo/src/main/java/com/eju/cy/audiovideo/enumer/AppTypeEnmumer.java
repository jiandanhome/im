package com.eju.cy.audiovideo.enumer;


/**
 * 应用类型
 */
public enum AppTypeEnmumer {

    JDJ("JDJ"),
    JDM("JDM"),
    JDD("JDD"),

    JDL("JDL"),
    YL_C("YL_C");

    private String appChannel = "10";

    private AppTypeEnmumer(String appChannel) {
        this.appChannel = appChannel;
    }

    public String value() {
        return this.appChannel;
    }

}
