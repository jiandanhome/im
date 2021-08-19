package com.eju.cy.audiovideo.dto;


import com.tencent.rtmp.ui.TXCloudVideoView;

import java.io.Serializable;

/**
 * 用于发送群语音视频 参数拼接
 */
public class GroupAvCallDto implements Serializable {

    String userNakeName = "";
    String imId = "";
    String portraitUrl = "";


    boolean isOpenVideoPermissions = false;//是否有开启视频权限

    public String getUserNakeName() {
        return userNakeName;
    }

    public void setUserNakeName(String userNakeName) {
        this.userNakeName = userNakeName;
    }

    public String getImId() {
        return imId;
    }

    public void setImId(String imId) {
        this.imId = imId;
    }

    public String getPortraitUrl() {
        return portraitUrl;
    }

    public void setPortraitUrl(String portraitUrl) {
        this.portraitUrl = portraitUrl;
    }

    public boolean isOpenVideoPermissions() {
        return isOpenVideoPermissions;
    }

    public void setOpenVideoPermissions(boolean openVideoPermissions) {
        isOpenVideoPermissions = openVideoPermissions;
    }


    @Override
    public String toString() {
        return "GroupAvCallDto{" +
                "userNakeName='" + userNakeName + '\'' +
                ", imId='" + imId + '\'' +
                ", portraitUrl='" + portraitUrl + '\'' +
                ", isOpenVideoPermissions=" + isOpenVideoPermissions +
                '}';
    }
}
