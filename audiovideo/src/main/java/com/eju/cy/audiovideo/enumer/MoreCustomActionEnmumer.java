package com.eju.cy.audiovideo.enumer;

/**
 * 聊天界面更多菜单
 */
public enum MoreCustomActionEnmumer {


   // TAKING_PICTURES_CALL("拍照"),
    IMAGE_CALL("相册"),
    CAMERA_VIDEO_CALL("摄像"),

    BUINESS_CARD_CALL("名片"),
    HOUSING_CALL("房源"),
    AUDIO_CALL("音频通话"),
    FILE_CALL("文件");

    private String value = "";

    private MoreCustomActionEnmumer(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
