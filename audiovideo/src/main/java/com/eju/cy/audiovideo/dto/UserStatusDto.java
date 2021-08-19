package com.eju.cy.audiovideo.dto;

import com.tencent.rtmp.ui.TXCloudVideoView;

public class UserStatusDto {


    String userNakeName = "";
    String imId = "";
    String portraitUrl = "";


    boolean isOpenVideoPermissions = false;//是否有开启视频权限
    boolean isOpenVideo = false;//是否开启了视频
    boolean answer = false;//是否接听

  private TXCloudVideoView txCloudVideoView;


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

    public boolean isOpenVideo() {
        return isOpenVideo;
    }

    public void setOpenVideo(boolean openVideo) {
        isOpenVideo = openVideo;
    }

    public boolean isAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

    public TXCloudVideoView getTxCloudVideoView() {
        return txCloudVideoView;
    }

    public void setTxCloudVideoView(TXCloudVideoView txCloudVideoView) {
        this.txCloudVideoView = txCloudVideoView;
    }


    @Override
    public String toString() {
        return "UserStatusDto{" +
                "userNakeName='" + userNakeName + '\'' +
                ", imId='" + imId + '\'' +
                ", portraitUrl='" + portraitUrl + '\'' +
                ", isOpenVideoPermissions=" + isOpenVideoPermissions +
                ", isOpenVideo=" + isOpenVideo +
                ", answer=" + answer +
                ", txCloudVideoView=" + txCloudVideoView +
                '}';
    }
}
