package com.eju.cy.audiovideo.enumer;


/**
 * 加群验证方式
 */
public enum GroupMemberRoleTypeEnmunmer {
    ROLE_TYPE_OWNER(400),//圈主
    ROLE_TYPE_ADMIN(300),//管理员  秘书
    ROLE_TYPE_NORMAL(200),//普通

    ROLE_TYPE_NOT_MEMBER(0);//成员


    private int value = 300;

    private GroupMemberRoleTypeEnmunmer(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
