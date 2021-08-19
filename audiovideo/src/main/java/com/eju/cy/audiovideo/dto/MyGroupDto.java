package com.eju.cy.audiovideo.dto;

import com.tencent.imsdk.TIMGroupAddOpt;

/**
 * @ Name: Caochen
 * @ Date: 2020-05-08
 * @ Time: 10:52
 * @ Description： 我的圈子
 */
public class MyGroupDto {


    private String groupId = "";//圈子ID
    private String groupName = "";//圈子名字
    private String groupOwner = "";//获取群组创建者帐号

    private String groupNotice = "";//获取群公告内容
    private String groupIntroduction = "";//获取群简介内容
    private String groupFaceUrl = "";//获取群头像 URL

    private String groupType = "";// 群类型
    private long createTime;// 群组创建时间
    private long lastInfoTime;//获取群组信息最后修改时间

    private long lastMsgTime;//获取最新群组消息时间
    private long memberNum;//获取群组成员数量
    private long maxMemberNum;//获取允许的最大群成员数

    private long onlineMemberNum;
    private TIMGroupAddOpt addOption; //获取加群选项
    private int intAddOption;//获取加群选项

    private boolean isSilenceAll = false;//获取此群组是否被设置了全员禁言
    private String lastMsg = "";// 获取群组内最新一条消息
    private int unreadMsgNum = 0;//未读消息

    private String lastMsgAuthor = "";//最后一条消息发送作者
    private int lastMsgType = -1;//最后一条消息类型

    private  int role = 0;//用户角色

    private boolean isTop;//是否置顶

    private  boolean isDisturb;//是否免打扰


    private boolean isAitMsg;//是否有@的未读消息

    public boolean isSilenceAll() {
        return isSilenceAll;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public int getLastMsgType() {
        return lastMsgType;
    }

    public void setLastMsgType(int lastMsgType) {
        this.lastMsgType = lastMsgType;
    }


    @Override
    public String toString() {
        return "MyGroupDto{" +
                "groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupOwner='" + groupOwner + '\'' +
                ", groupNotice='" + groupNotice + '\'' +
                ", groupIntroduction='" + groupIntroduction + '\'' +
                ", groupFaceUrl='" + groupFaceUrl + '\'' +
                ", groupType='" + groupType + '\'' +
                ", createTime=" + createTime +
                ", lastInfoTime=" + lastInfoTime +
                ", lastMsgTime=" + lastMsgTime +
                ", memberNum=" + memberNum +
                ", maxMemberNum=" + maxMemberNum +
                ", onlineMemberNum=" + onlineMemberNum +
                ", addOption=" + addOption +
                ", intAddOption=" + intAddOption +
                ", isSilenceAll=" + isSilenceAll +
                ", lastMsg='" + lastMsg + '\'' +
                ", unreadMsgNum=" + unreadMsgNum +
                ", lastMsgAuthor='" + lastMsgAuthor + '\'' +
                ", lastMsgType=" + lastMsgType +
                ", role=" + role +
                ", isTop=" + isTop +
                ", isDisturb=" + isDisturb +
                ", isAitMsg=" + isAitMsg +
                '}';
    }

    public boolean isAitMsg() {
        return isAitMsg;
    }

    public void setAitMsg(boolean aitMsg) {
        isAitMsg = aitMsg;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupOwner() {
        return groupOwner;
    }

    public void setGroupOwner(String groupOwner) {
        this.groupOwner = groupOwner;
    }

    public String getGroupNotice() {
        return groupNotice;
    }

    public void setGroupNotice(String groupNotice) {
        this.groupNotice = groupNotice;
    }

    public String getGroupIntroduction() {
        return groupIntroduction;
    }

    public void setGroupIntroduction(String groupIntroduction) {
        this.groupIntroduction = groupIntroduction;
    }

    public String getGroupFaceUrl() {
        return groupFaceUrl;
    }

    public void setGroupFaceUrl(String groupFaceUrl) {
        this.groupFaceUrl = groupFaceUrl;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLastInfoTime() {
        return lastInfoTime;
    }

    public void setLastInfoTime(long lastInfoTime) {
        this.lastInfoTime = lastInfoTime;
    }

    public long getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(long lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    public long getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(long memberNum) {
        this.memberNum = memberNum;
    }

    public long getMaxMemberNum() {
        return maxMemberNum;
    }

    public void setMaxMemberNum(long maxMemberNum) {
        this.maxMemberNum = maxMemberNum;
    }

    public long getOnlineMemberNum() {
        return onlineMemberNum;
    }

    public void setOnlineMemberNum(long onlineMemberNum) {
        this.onlineMemberNum = onlineMemberNum;
    }

    public TIMGroupAddOpt getAddOption() {
        return addOption;
    }

    public void setAddOption(TIMGroupAddOpt addOption) {
        this.addOption = addOption;
    }

    public int getIntAddOption() {
        return intAddOption;
    }

    public void setIntAddOption(int intAddOption) {
        this.intAddOption = intAddOption;
    }

    public boolean isSilenceAll(boolean silenceAll) {
        return isSilenceAll;
    }

    public void setSilenceAll(boolean silenceAll) {
        isSilenceAll = silenceAll;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public int getUnreadMsgNum() {
        return unreadMsgNum;
    }

    public void setUnreadMsgNum(int unreadMsgNum) {
        this.unreadMsgNum = unreadMsgNum;
    }

    public String getLastMsgAuthor() {
        return lastMsgAuthor;
    }

    public void setLastMsgAuthor(String lastMsgAuthor) {
        this.lastMsgAuthor = lastMsgAuthor;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public boolean isDisturb() {
        return isDisturb;
    }

    public void setDisturb(boolean disturb) {
        isDisturb = disturb;
    }
}
