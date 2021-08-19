package com.eju.cy.audiovideo.dto;

public class SetGuoupStaticDto {


    private String msg;
    private CallRecordsDto.DataBean data;
    private String code;


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public CallRecordsDto.DataBean getData() {
        return data;
    }

    public void setData(CallRecordsDto.DataBean data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "SetGuoupStaticDto{" +
                "msg='" + msg + '\'' +
                ", data=" + data +
                ", code='" + code + '\'' +
                '}';
    }
}
