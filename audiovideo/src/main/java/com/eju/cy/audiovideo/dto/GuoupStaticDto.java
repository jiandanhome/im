package com.eju.cy.audiovideo.dto;

import java.util.List;

public class GuoupStaticDto {


    /**
     * msg : 成功
     * data : {"groups_info_list":[{"GroupId":"@TGS#2J4SZEAEL","Type":"Public","Name":"MyFirstGroup","Introduction":"TestGroup","Notification":"TestGroup","FaceUrl":"http://this.is.face.url","Owner_Account":"leckie","CreateTime":1426976500,"LastInfoTime":1426976500,"LastMsgTime":1426976600,"MemberNum":2,"MaxMemberNum":50,"ApplyJoinOption":"FreeAccess","IsPublic":1}]}
     */

    private String code;
    private String msg;
    private DataBean data;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<GroupsInfoListBean> groups_info_list;

        public List<GroupsInfoListBean> getGroups_info_list() {
            return groups_info_list;
        }

        public void setGroups_info_list(List<GroupsInfoListBean> groups_info_list) {
            this.groups_info_list = groups_info_list;
        }

        public static class GroupsInfoListBean {


            @Override
            public String toString() {
                return "GroupsInfoListBean{" +
                        "GroupId='" + GroupId + '\'' +
                        ", Type='" + Type + '\'' +
                        ", Name='" + Name + '\'' +
                        ", Introduction='" + Introduction + '\'' +
                        ", Notification='" + Notification + '\'' +
                        ", FaceUrl='" + FaceUrl + '\'' +
                        ", Owner_Account='" + Owner_Account + '\'' +
                        ", CreateTime=" + CreateTime +
                        ", LastInfoTime=" + LastInfoTime +
                        ", LastMsgTime=" + LastMsgTime +
                        ", MemberNum=" + MemberNum +
                        ", MaxMemberNum=" + MaxMemberNum +
                        ", ApplyJoinOption='" + ApplyJoinOption + '\'' +
                        ", IsPublic=" + IsPublic +
                        '}';
            }

            /**
             * GroupId : @TGS#2J4SZEAEL
             * Type : Public
             * Name : MyFirstGroup
             * Introduction : TestGroup
             * Notification : TestGroup
             * FaceUrl : http://this.is.face.url
             * Owner_Account : leckie
             * CreateTime : 1426976500
             * LastInfoTime : 1426976500
             * LastMsgTime : 1426976600
             * MemberNum : 2
             * MaxMemberNum : 50
             * ApplyJoinOption : FreeAccess
             * IsPublic : 1
             */

            private String GroupId;
            private String Type;
            private String Name;
            private String Introduction;
            private String Notification;
            private String FaceUrl;
            private String Owner_Account;
            private int CreateTime;
            private int LastInfoTime;
            private int LastMsgTime;
            private int MemberNum;
            private int MaxMemberNum;
            private String ApplyJoinOption;
            private int IsPublic;

            public String getGroupId() {
                return GroupId;
            }

            public void setGroupId(String GroupId) {
                this.GroupId = GroupId;
            }

            public String getType() {
                return Type;
            }

            public void setType(String Type) {
                this.Type = Type;
            }

            public String getName() {
                return Name;
            }

            public void setName(String Name) {
                this.Name = Name;
            }

            public String getIntroduction() {
                return Introduction;
            }

            public void setIntroduction(String Introduction) {
                this.Introduction = Introduction;
            }

            public String getNotification() {
                return Notification;
            }

            public void setNotification(String Notification) {
                this.Notification = Notification;
            }

            public String getFaceUrl() {
                return FaceUrl;
            }

            public void setFaceUrl(String FaceUrl) {
                this.FaceUrl = FaceUrl;
            }

            public String getOwner_Account() {
                return Owner_Account;
            }

            public void setOwner_Account(String Owner_Account) {
                this.Owner_Account = Owner_Account;
            }

            public int getCreateTime() {
                return CreateTime;
            }

            public void setCreateTime(int CreateTime) {
                this.CreateTime = CreateTime;
            }

            public int getLastInfoTime() {
                return LastInfoTime;
            }

            public void setLastInfoTime(int LastInfoTime) {
                this.LastInfoTime = LastInfoTime;
            }

            public int getLastMsgTime() {
                return LastMsgTime;
            }

            public void setLastMsgTime(int LastMsgTime) {
                this.LastMsgTime = LastMsgTime;
            }

            public int getMemberNum() {
                return MemberNum;
            }

            public void setMemberNum(int MemberNum) {
                this.MemberNum = MemberNum;
            }

            public int getMaxMemberNum() {
                return MaxMemberNum;
            }

            public void setMaxMemberNum(int MaxMemberNum) {
                this.MaxMemberNum = MaxMemberNum;
            }

            public String getApplyJoinOption() {
                return ApplyJoinOption;
            }

            public void setApplyJoinOption(String ApplyJoinOption) {
                this.ApplyJoinOption = ApplyJoinOption;
            }

            public int getIsPublic() {
                return IsPublic;
            }

            public void setIsPublic(int IsPublic) {
                this.IsPublic = IsPublic;
            }
        }
    }
}
