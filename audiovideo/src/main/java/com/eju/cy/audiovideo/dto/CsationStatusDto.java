package com.eju.cy.audiovideo.dto;

import java.util.List;

public class CsationStatusDto {


    /**
     * msg : 成功
     * data : {"conversation_id":"C2C1017","stick":0,"disturb":0}
     * code : 10000
     * encrypt : 0
     */

    private String msg;
    private List<DataBean> data;
    private String code;
    private int encrypt;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
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

    public static class DataBean {
        /**
         * conversation_id : C2C1017
         * stick : 0
         * disturb : 0
         */

        private String conversation_id;
        private int stick;
        private int disturb;

        public String getConversation_id() {
            return conversation_id;
        }

        public void setConversation_id(String conversation_id) {
            this.conversation_id = conversation_id;
        }

        public int getStick() {
            return stick;
        }

        public void setStick(int stick) {
            this.stick = stick;
        }

        public int getDisturb() {
            return disturb;
        }

        public void setDisturb(int disturb) {
            this.disturb = disturb;
        }


        @Override
        public String toString() {
            return "DataBean{" +
                    "conversation_id='" + conversation_id + '\'' +
                    ", stick=" + stick +
                    ", disturb=" + disturb +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "CsationStatusDto{" +
                "msg='" + msg + '\'' +
                ", data=" + data +
                ", code='" + code + '\'' +
                ", encrypt=" + encrypt +
                '}';
    }
}
