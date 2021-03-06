package com.eju.cy.audiovideo.modules.group.info;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.base.IUIKitCallBack;
import com.eju.cy.audiovideo.component.LineControllerView;
import com.eju.cy.audiovideo.component.SelectionActivity;
import com.eju.cy.audiovideo.component.TitleBarLayout;
import com.eju.cy.audiovideo.component.dialog.TUIKitDialog;
import com.eju.cy.audiovideo.modules.group.interfaces.IGroupMemberLayout;
import com.eju.cy.audiovideo.modules.group.member.IGroupMemberRouter;
import com.eju.cy.audiovideo.utils.TUIKitConstants;
import com.eju.cy.audiovideo.utils.TUIKitLog;
import com.eju.cy.audiovideo.utils.ToastUtil;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMGroupManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class GroupInfoLayout extends LinearLayout implements IGroupMemberLayout, View.OnClickListener {

    private static final String TAG = GroupInfoLayout.class.getSimpleName();
    private TitleBarLayout mTitleBar;
    private LineControllerView mMemberView;
    private GroupInfoAdapter mMemberAdapter;
    private IGroupMemberRouter mMemberPreviewListener;
    private LineControllerView mGroupTypeView;
    private LineControllerView mGroupIDView;
    private LineControllerView mGroupNameView;
    private LineControllerView mGroupIcon;
    private LineControllerView mGroupNotice;
    private LineControllerView mNickView;
    private LineControllerView mJoinTypeView;
    private LineControllerView mTopSwitchView;
    private Button mDissolveBtn;

    private GroupInfo mGroupInfo;
    private GroupInfoPresenter mPresenter;
    private ArrayList<String> mJoinTypes = new ArrayList<>();

    public GroupInfoLayout(Context context) {
        super(context);
        init();
    }

    public GroupInfoLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GroupInfoLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.group_info_layout, this);
        // ??????
        mTitleBar = findViewById(R.id.group_info_title_bar);
        mTitleBar.getRightGroup().setVisibility(GONE);
        mTitleBar.setTitle(getResources().getString(R.string.group_detail), TitleBarLayout.POSITION.MIDDLE);
        mTitleBar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) getContext()).finish();
            }
        });
        // ????????????
        mMemberView = findViewById(R.id.group_member_bar);
        mMemberView.setOnClickListener(this);
        mMemberView.setCanNav(true);
        // ????????????
        GridView memberList = findViewById(R.id.group_members);
        mMemberAdapter = new GroupInfoAdapter();
        memberList.setAdapter(mMemberAdapter);
        // ??????????????????
        mGroupTypeView = findViewById(R.id.group_type_bar);
        // ???ID?????????
        mGroupIDView = findViewById(R.id.group_account);
        // ????????????
        mGroupNameView = findViewById(R.id.group_name);
        mGroupNameView.setOnClickListener(this);
        mGroupNameView.setCanNav(true);
        // ?????????
        mGroupIcon = findViewById(R.id.group_icon);
        mGroupIcon.setOnClickListener(this);
        mGroupIcon.setCanNav(false);
        // ?????????
        mGroupNotice = findViewById(R.id.group_notice);
        mGroupNotice.setOnClickListener(this);
        mGroupNotice.setCanNav(true);
        // ????????????
        mJoinTypeView = findViewById(R.id.join_type_bar);
        mJoinTypeView.setOnClickListener(this);
        mJoinTypeView.setCanNav(true);
        mJoinTypes.addAll(Arrays.asList(getResources().getStringArray(R.array.group_join_type)));
        // ?????????
        mNickView = findViewById(R.id.self_nickname_bar);
        mNickView.setOnClickListener(this);
        mNickView.setCanNav(true);
        // ????????????
        mTopSwitchView = findViewById(R.id.chat_to_top_switch);
        mTopSwitchView.setCheckListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPresenter.setTopConversation(isChecked);
            }
        });
        // ??????
        mDissolveBtn = findViewById(R.id.group_dissolve_button);
        mDissolveBtn.setOnClickListener(this);

        mPresenter = new GroupInfoPresenter(this);
    }

    @Override
    public void onClick(View v) {
        if (mGroupInfo == null) {
            TUIKitLog.e(TAG, "mGroupInfo is NULL");
            return;
        }
        if (v.getId() == R.id.group_member_bar) {
            if (mMemberPreviewListener != null) {
                mMemberPreviewListener.forwardListMember(mGroupInfo);
            }
        } else if (v.getId() == R.id.group_name) {
            Bundle bundle = new Bundle();
            bundle.putString(TUIKitConstants.Selection.TITLE, getResources().getString(R.string.modify_group_name));
            bundle.putString(TUIKitConstants.Selection.INIT_CONTENT, mGroupNameView.getContent());
            bundle.putInt(TUIKitConstants.Selection.LIMIT, 20);
            SelectionActivity.startTextSelection((Activity) getContext(), bundle, new SelectionActivity.OnResultReturnListener() {
                @Override
                public void onReturn(final Object text) {
                    mPresenter.modifyGroupName(text.toString());
                    mGroupNameView.setContent(text.toString());
                }
            });


//            GroupController.getInstance().setGroupName(mGroupInfo.getId(), "dsdsdsds", new ImCallBack() {
//                @Override
//                public void onError(int var1, String var2) {
//
//                }
//
//                @Override
//                public void onSuccess(Object var1) {
//
//                }
//            });


        } else if (v.getId() == R.id.group_icon) {
            String groupUrl = String.format("https://picsum.photos/id/%d/200/200", new Random().nextInt(1000));
            TIMGroupManager.ModifyGroupInfoParam param = new TIMGroupManager.ModifyGroupInfoParam(mGroupInfo.getId());
            param.setFaceUrl(groupUrl);
            TIMGroupManager.getInstance().modifyGroupInfo(param, new TIMCallBack() {
                @Override
                public void onError(int code, String desc) {
                    TUIKitLog.e(TAG, "modify group icon failed, code:" + code + "|desc:" + desc);
                    ToastUtil.toastLongMessage("?????????????????????, code = " + code + ", info = " + desc);
                }

                @Override
                public void onSuccess() {
                    ToastUtil.toastLongMessage("?????????????????????");
                }
            });

        } else if (v.getId() == R.id.group_notice) {
            Bundle bundle = new Bundle();
            bundle.putString(TUIKitConstants.Selection.TITLE, getResources().getString(R.string.modify_group_notice));
            bundle.putString(TUIKitConstants.Selection.INIT_CONTENT, mGroupNotice.getContent());
            bundle.putInt(TUIKitConstants.Selection.LIMIT, 200);
            SelectionActivity.startTextSelection((Activity) getContext(), bundle, new SelectionActivity.OnResultReturnListener() {
                @Override
                public void onReturn(final Object text) {
                    mPresenter.modifyGroupNotice(text.toString());
                    mGroupNotice.setContent(text.toString());
                }
            });
        } else if (v.getId() == R.id.self_nickname_bar) {
            Bundle bundle = new Bundle();
            bundle.putString(TUIKitConstants.Selection.TITLE, getResources().getString(R.string.modify_nick_name_in_goup));
            bundle.putString(TUIKitConstants.Selection.INIT_CONTENT, mNickView.getContent());
            bundle.putInt(TUIKitConstants.Selection.LIMIT, 20);
            SelectionActivity.startTextSelection((Activity) getContext(), bundle, new SelectionActivity.OnResultReturnListener() {
                @Override
                public void onReturn(final Object text) {
                    mPresenter.modifyMyGroupNickname(text.toString());
                    mNickView.setContent(text.toString());
                }
            });
        } else if (v.getId() == R.id.join_type_bar) {
            if (mGroupTypeView.getContent().equals("?????????")) {
                ToastUtil.toastLongMessage("???????????????????????????????????????????????????");
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString(TUIKitConstants.Selection.TITLE, getResources().getString(R.string.group_join_type));
            bundle.putStringArrayList(TUIKitConstants.Selection.LIST, mJoinTypes);
            bundle.putInt(TUIKitConstants.Selection.DEFAULT_SELECT_ITEM_INDEX, mGroupInfo.getJoinType());
            SelectionActivity.startListSelection((Activity) getContext(), bundle, new SelectionActivity.OnResultReturnListener() {
                @Override
                public void onReturn(final Object text) {
                    mPresenter.modifyGroupInfo((Integer) text, TUIKitConstants.Group.MODIFY_GROUP_JOIN_TYPE);
                    mJoinTypeView.setContent(mJoinTypes.get((Integer) text));

                }
            });
        } else if (v.getId() == R.id.group_dissolve_button) {
            if (mGroupInfo.isOwner() && !mGroupInfo.getGroupType().equals("Private")) {
                new TUIKitDialog(getContext())
                        .builder()
                        .setCancelable(true)
                        .setCancelOutside(true)
                        .setTitle("??????????????????????")
                        .setDialogWidth(0.75f)
                        .setPositiveButton("??????", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPresenter.deleteGroup();
                            }
                        })
                        .setNegativeButton("??????", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .show();
            } else {
                new TUIKitDialog(getContext())
                        .builder()
                        .setCancelable(true)
                        .setCancelOutside(true)
                        .setTitle("????????????????????????")
                        .setDialogWidth(0.75f)
                        .setPositiveButton("??????", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPresenter.quitGroup();
                            }
                        })
                        .setNegativeButton("??????", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .show();
            }
        }
    }

    public void setGroupId(String groupId) {


        mPresenter.loadGroupInfo(groupId, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                setGroupInfo((GroupInfo) data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });
    }

    private void setGroupInfo(GroupInfo info) {
        if (info == null) {
            return;
        }
        this.mGroupInfo = info;
        mGroupNameView.setContent(info.getGroupName());
        mGroupIDView.setContent(info.getId());
        mGroupNotice.setContent(info.getNotice());
        mMemberView.setContent(info.getMemberCount() + "???");
        mMemberAdapter.setDataSource(info);
        mGroupTypeView.setContent(convertGroupText(info.getGroupType()));
        mJoinTypeView.setContent(mJoinTypes.get(info.getJoinType()));
        mNickView.setContent(mPresenter.getNickName());
        mTopSwitchView.setChecked(mGroupInfo.isTopChat());

        mDissolveBtn.setText(R.string.dissolve);
        if (mGroupInfo.isOwner()) {
            mGroupNotice.setVisibility(VISIBLE);
            mJoinTypeView.setVisibility(VISIBLE);
            if (mGroupInfo.getGroupType().equals("Private")) {
                mDissolveBtn.setText(R.string.exit_group);
            }
        } else {
            mGroupNotice.setVisibility(GONE);
            mJoinTypeView.setVisibility(GONE);
            mDissolveBtn.setText(R.string.exit_group);
        }
    }

    private String convertGroupText(String groupType) {
        String groupText = "";
        if (TextUtils.isEmpty(groupType)) {
            return groupText;
        }
        if (TextUtils.equals(groupType, TUIKitConstants.GroupType.TYPE_PRIVATE)) {
            groupText = "?????????";
        } else if (TextUtils.equals(groupType, TUIKitConstants.GroupType.TYPE_PUBLIC)) {
            groupText = "?????????";
        } else if (TextUtils.equals(groupType, TUIKitConstants.GroupType.TYPE_CHAT_ROOM)) {
            groupText = "?????????";
        }
        return groupText;
    }

    public void onGroupInfoModified(Object value, int type) {
        switch (type) {
            case TUIKitConstants.Group.MODIFY_GROUP_NAME:
                ToastUtil.toastLongMessage(getResources().getString(R.string.modify_group_name_success));
                mGroupNameView.setContent(value.toString());
                break;
            case TUIKitConstants.Group.MODIFY_GROUP_NOTICE:
                mGroupNotice.setContent(value.toString());
                ToastUtil.toastLongMessage(getResources().getString(R.string.modify_group_notice_success));
                break;
            case TUIKitConstants.Group.MODIFY_GROUP_JOIN_TYPE:
                mJoinTypeView.setContent(mJoinTypes.get((Integer) value));
                break;
            case TUIKitConstants.Group.MODIFY_MEMBER_NAME:
                ToastUtil.toastLongMessage(getResources().getString(R.string.modify_nickname_success));
                mNickView.setContent(value.toString());
                break;
        }
    }

    public void setRouter(IGroupMemberRouter listener) {
        mMemberPreviewListener = listener;
        mMemberAdapter.setManagerCallBack(listener);
    }

    @Override
    public void setDataSource(GroupInfo dataSource) {

    }

    @Override
    public TitleBarLayout getTitleBar() {
        return mTitleBar;
    }

    @Override
    public void setParentLayout(Object parent) {

    }

}
