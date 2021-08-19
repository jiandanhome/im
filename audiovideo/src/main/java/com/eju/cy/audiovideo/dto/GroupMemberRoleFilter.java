package com.eju.cy.audiovideo.dto;

public enum GroupMemberRoleFilter {

    All(0L),
    Owner(1L),
    Admin(2L),
    Normal(4L);

    private long filter = 0L;

    private GroupMemberRoleFilter(long x) {
        this.filter = x;
    }

    public long value() {
        return this.filter;
    }
}
