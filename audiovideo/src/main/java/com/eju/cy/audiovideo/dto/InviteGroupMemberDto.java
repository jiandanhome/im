package com.eju.cy.audiovideo.dto;

import java.util.List;

public class InviteGroupMemberDto {


    /**
     * msg : 成功
     * data : [{"Member_Account":"tommy","Result":1},{"Member_Account":"jared","Result":1}]
     * status : OK
     */

    private String msg;
       private String code;
    private String status;
    private List<DataBean> data;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * Member_Account : tommy
         * Result : 1
         */

        private String Member_Account;
        private int Result;

        public String getMember_Account() {
            return Member_Account;
        }

        public void setMember_Account(String Member_Account) {
            this.Member_Account = Member_Account;
        }

        public int getResult() {
            return Result;
        }

        public void setResult(int Result) {
            this.Result = Result;
        }


        @Override
        public String toString() {
            return "DataBean{" +
                    "Member_Account='" + Member_Account + '\'' +
                    ", Result=" + Result +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "InviteGroupMemberDto{" +
                "msg='" + msg + '\'' +
                ", code='" + code + '\'' +
                ", status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
