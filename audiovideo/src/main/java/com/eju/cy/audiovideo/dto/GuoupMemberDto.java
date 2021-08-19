package com.eju.cy.audiovideo.dto;

import java.io.Serializable;
import java.util.List;

public class GuoupMemberDto implements Serializable {


    /**
     * msg : 成功
     * data : [{"nick_name":"159****0404","face_url":"https://img.jiandanhome.com/attach/1911/12/2103479a050e11ea9d0800163e0cb531.jpg","im_user":"1027454"},{"nick_name":"笑笑","face_url":"https://img.jiandanhome.com/yilou/2006/30/12/52c25f4cba8911eaa65900163e0caf5f.jpg","im_user":"106115"}]
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

    public static class DataBean implements Serializable {
        /**
         * nick_name : 159****0404
         * face_url : https://img.jiandanhome.com/attach/1911/12/2103479a050e11ea9d0800163e0cb531.jpg
         * im_user : 1027454
         */

        private String nick_name;
        private String face_url;
        private String im_user;

        private String zi_mu;//大写字母


        @Override
        public String toString() {
            return "DataBean{" +
                    "nick_name='" + nick_name + '\'' +
                    ", face_url='" + face_url + '\'' +
                    ", im_user='" + im_user + '\'' +
                    ", zi_mu='" + zi_mu + '\'' +
                    '}';
        }

        public String getZi_mu() {
            return zi_mu;
        }

        public void setZi_mu(String zi_mu) {
            this.zi_mu = zi_mu;
        }

        public String getNick_name() {
            return nick_name;
        }

        public void setNick_name(String nick_name) {
            this.nick_name = nick_name;
        }

        public String getFace_url() {
            return face_url;
        }

        public void setFace_url(String face_url) {
            this.face_url = face_url;
        }

        public String getIm_user() {
            return im_user;
        }

        public void setIm_user(String im_user) {
            this.im_user = im_user;
        }
    }
}
