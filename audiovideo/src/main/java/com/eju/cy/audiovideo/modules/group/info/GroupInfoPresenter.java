package com.eju.cy.audiovideo.modules.group.info;

import android.app.Activity;

import com.eju.cy.audiovideo.base.IUIKitCallBack;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.eju.cy.audiovideo.utils.TUIKitConstants;
import com.eju.cy.audiovideo.utils.TUIKitLog;
import com.eju.cy.audiovideo.utils.ToastUtil;


public class GroupInfoPresenter {

    private GroupInfoLayout mInfoLayout;
    private GroupInfoProvider mProvider;

    public GroupInfoPresenter(GroupInfoLayout layout) {
        this.mInfoLayout = layout;
        mProvider = new GroupInfoProvider();
    }

    public void loadGroupInfo(String groupId, final IUIKitCallBack callBack) {
        mProvider.loadGroupInfo(groupId, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                callBack.onSuccess(data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                TUIKitLog.e("loadGroupInfo", errCode + ":" + errMsg);
                callBack.onError(module, errCode, errMsg);
                ToastUtil.toastLongMessage(errMsg);
            }
        });
    }

    public void modifyGroupName(final String name) {
        mProvider.modifyGroupInfo(name, TUIKitConstants.Group.MODIFY_GROUP_NAME, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                mInfoLayout.onGroupInfoModified(name, TUIKitConstants.Group.MODIFY_GROUP_NAME);


                //ChatLayout  修改更新的名字
                ImActionDto imActionDto = new ImActionDto();
                imActionDto.setAction(ActionTags.UPDATE_GUOUP_NAME);
                imActionDto.setJsonStr(name);

                String str= JsonUtils.toJson(imActionDto);

                EjuHomeImEventCar.getDefault().post(str);

            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                TUIKitLog.e("modifyGroupName", errCode + ":" + errMsg);
                ToastUtil.toastLongMessage(errMsg);
            }
        });
    }

    public void modifyGroupNotice(final String notice) {
        mProvider.modifyGroupInfo(notice, TUIKitConstants.Group.MODIFY_GROUP_NOTICE, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                mInfoLayout.onGroupInfoModified(notice, TUIKitConstants.Group.MODIFY_GROUP_NOTICE);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                TUIKitLog.e("modifyGroupNotice", errCode + ":" + errMsg);
                ToastUtil.toastLongMessage(errMsg);
            }
        });
    }


    public String getNickName() {
        String nickName = "";
        if (mProvider.getSelfGroupInfo() != null) {
            if (mProvider.getSelfGroupInfo().getDetail() != null) {
                nickName = mProvider.getSelfGroupInfo().getDetail().getNameCard();
            }
        }
        return nickName == null ? "" : nickName;
    }

    public void modifyMyGroupNickname(final String nickname) {
        mProvider.modifyMyGroupNickname(nickname, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                mInfoLayout.onGroupInfoModified(nickname, TUIKitConstants.Group.MODIFY_MEMBER_NAME);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                TUIKitLog.e("modifyMyGroupNickname", errCode + ":" + errMsg);
                ToastUtil.toastLongMessage(errMsg);
            }
        });
    }

    public void deleteGroup() {
        mProvider.deleteGroup(new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                ((Activity) mInfoLayout.getContext()).finish();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                TUIKitLog.e("deleteGroup", errCode + ":" + errMsg);
                ToastUtil.toastLongMessage(errMsg);
            }
        });
    }

    public void setTopConversation(boolean flag) {
        mProvider.setTopConversation(flag);
    }

    public void quitGroup() {
        mProvider.quitGroup(new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                ((Activity) mInfoLayout.getContext()).finish();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ((Activity) mInfoLayout.getContext()).finish();
                TUIKitLog.e("quitGroup", errCode + ":" + errMsg);
            }
        });
    }

    public void modifyGroupInfo(int value, int type) {
        mProvider.modifyGroupInfo(value, type, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                mInfoLayout.onGroupInfoModified(data, TUIKitConstants.Group.MODIFY_GROUP_JOIN_TYPE);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ToastUtil.toastLongMessage("modifyGroupInfo fail :" + errCode + "=" + errMsg);
            }
        });
    }
}
