package com.eju.cy.audiovideo.enumer;


/**
 * 加群验证方式
 */
public enum GroupAddOptEnmunmer {
    TIM_GROUP_ADD_FORBID(0L),//禁止
    TIM_GROUP_ADD_AUTH(1L),//身份验证
    TIM_GROUP_ADD_ANY(2L);//所有人

    private long value = 1L;

    private GroupAddOptEnmunmer(long i) {
        this.value = i;
    }

    public long getValue() {
        return this.value;
    }
}
