package com.eju.cy.audiovideo.dto;

public class ImActionDto {
    private int action; //区分动作
    private String jsonStr; //用来存储动作所需要的参数

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getJsonStr() {
        return jsonStr;
    }

    public void setJsonStr(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    @Override
    public String toString() {
        return "ImActionDto{" +
                "action=" + action +
                ", jsonStr='" + jsonStr + '\'' +
                '}';
    }
}
