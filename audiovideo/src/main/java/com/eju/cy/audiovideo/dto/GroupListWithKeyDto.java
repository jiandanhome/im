package com.eju.cy.audiovideo.dto;

import java.util.List;

public class GroupListWithKeyDto {


    /**
     * msg : 成功
     * data : [{"group_id":"@TGS#2FIDIRTGJ","group_name":"上海二手公盘群"},{"group_id":"@TGS#2COZQSTGM","group_name":"上海二手房信息交流群"}]
     * code : 10000
     * encrypt : 0
     */

    private String msg;
    private String code;
    private int encrypt;
    private List<DataBean> data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(int encrypt) {
        this.encrypt = encrypt;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * group_id : @TGS#2FIDIRTGJ
         * group_name : 上海二手公盘群
         */

        private String group_id;
        private String group_name;

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }

        public String getGroup_name() {
            return group_name;
        }

        public void setGroup_name(String group_name) {
            this.group_name = group_name;
        }
    }
}
