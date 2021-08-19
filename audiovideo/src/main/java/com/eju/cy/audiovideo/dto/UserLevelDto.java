package com.eju.cy.audiovideo.dto;

import java.util.HashMap;
import java.util.Map;

public class UserLevelDto {


    /**
     * msg : 成功
     * data : {"10476515":0,"10476475":0,"10476412":0,"10476415":0,"10476512":0,"10476560":0,"10476576":0,"10476432":0,"10476347":1,"10476319":1,"10476555":0,"10476571":0,"10476367":1,"10476328":1,"10476362":1,"10476322":1,"10476392":1,"10476587":0,"10476585":0,"10476565":0,"10476582":0,"10476482":0,"10476528":0,"10476446":0,"10476422":0,"10476522":0,"10476396":1,"10476465":0,"10476359":1,"10304639":0,"10476406":0,"10476350":1,"10476540":0,"10476547":0,"10476426":0,"10476401":0,"10476377":1,"10476373":1,"10476332":1,"10476508":0,"10476499":0,"10476598":0,"10476491":0,"10476597":0,"10476495":0,"10142610":0,"10304688":0,"10476458":0,"10476606":0,"10476382":1,"10476385":1,"10476438":0,"10476452":0}
     * code : 10000
     * encrypt : 0
     */

    private String msg;
    private Map<String,String> data = new HashMap<>();
    private String code;
    private int encrypt;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
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

    @Override
    public String toString() {
        return "UserLevelDto{" +
                "msg='" + msg + '\'' +
                ", data=" + data +
                ", code='" + code + '\'' +
                ", encrypt=" + encrypt +
                '}';
    }
}
