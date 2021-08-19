package com.eju.cy.audiovideo.dto;

import com.tencent.imsdk.TIMGroupAddOpt;

/**
* @ Name: Caochen
* @ Date: 2020-05-07
* @ Time: 16:18
* @ Description：  创建群组所需参数
*/
public class GroupDto {

    private  String type;//圈子类型 私有群（Private）、公开群（Public）、聊天室（ChatRoom）、音视频聊天室（AVChatRoom）和在线成员广播大群（BChatRoom）
    private  String groupId;//圈子ID
    private  String name;//圈子名字


    private  String notification;//圈子公告
    private  String introduction; //圈子简介
    private  String faceUrl;//圈子头像
    private TIMGroupAddOpt timGroupAddOpt;// 加群选项


}
