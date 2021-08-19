package com.eju.cy.audiovideo.dto;

import java.util.List;

public class GroupAvNumberDto {

    //房间所有人
    private List<String> allUseList;
    //房间能开视频的人
    private List<String> openVideoList;

    private  String groupId;




    public List<String> getAllUseList() {
        return allUseList;
    }

    public void setAllUseList(List<String> allUseList) {
        this.allUseList = allUseList;
    }

    public List<String> getOpenVideoList() {
        return openVideoList;
    }

    public void setOpenVideoList(List<String> openVideoList) {
        this.openVideoList = openVideoList;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
