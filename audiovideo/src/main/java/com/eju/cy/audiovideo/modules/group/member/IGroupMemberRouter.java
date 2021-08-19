package com.eju.cy.audiovideo.modules.group.member;

import com.eju.cy.audiovideo.modules.group.info.GroupInfo;

public interface IGroupMemberRouter {

    void forwardListMember(GroupInfo info);

    void forwardAddMember(GroupInfo info);

    void forwardDeleteMember(GroupInfo info);
}
