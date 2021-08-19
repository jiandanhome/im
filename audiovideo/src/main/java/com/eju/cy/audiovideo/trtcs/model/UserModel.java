package com.eju.cy.audiovideo.trtcs.model;

import java.io.Serializable;

public class UserModel implements Serializable {
    public String phone;
    public String userId;
    public String userSig;
    public String userName;
    public String userAvatar;


    //是否开启视频
    public String isOpenVideoPermissions;


    @Override
    public String toString() {
        return "UserModel{" +
                "phone='" + phone + '\'' +
                ", userId='" + userId + '\'' +
                ", userSig='" + userSig + '\'' +
                ", userName='" + userName + '\'' +
                ", userAvatar='" + userAvatar + '\'' +
                ", isOpenVideoPermissions='" + isOpenVideoPermissions + '\'' +
                '}';
    }
}
