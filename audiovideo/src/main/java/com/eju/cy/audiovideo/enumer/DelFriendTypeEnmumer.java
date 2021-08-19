package com.eju.cy.audiovideo.enumer;

public enum DelFriendTypeEnmumer {

    TIM_FRIEND_DEL_SINGLE(1),//单方面删除
    TIM_FRIEND_DEL_BOTH(2);//双方面删除

    private int value = 1;

    public int value() {
        return this.value;
    }

    private DelFriendTypeEnmumer(int value) {
        this.value = value;
    }
}
