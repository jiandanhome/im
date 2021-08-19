package com.eju.cy.audiovideosample.menu;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideosample.R;
import com.tencent.imsdk.friendship.TIMFriendResult;
import com.tencent.imsdk.friendship.TIMFriendStatus;
import com.eju.cy.audiovideo.base.BaseActivity;

import com.eju.cy.audiovideo.component.TitleBarLayout;
import com.eju.cy.audiovideo.controller.EjuImController;
import com.eju.cy.audiovideo.controller.GroupController;
import com.eju.cy.audiovideo.controller.ImCallBack;
import com.eju.cy.audiovideo.utils.SoftKeyBoardUtil;
import com.eju.cy.audiovideo.utils.TUIKitConstants;
import com.eju.cy.audiovideo.utils.ToastUtil;

public class AddMoreActivity extends BaseActivity {

    private static final String TAG = AddMoreActivity.class.getSimpleName();

    private TitleBarLayout mTitleBar;
    private EditText mUserID;
    private EditText mAddWording;
    private boolean mIsGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            mIsGroup = getIntent().getExtras().getBoolean(TUIKitConstants.GroupType.GROUP);
        }

        setContentView(R.layout.contact_add_activity);

        mTitleBar = findViewById(R.id.add_friend_titlebar);
        mTitleBar.setTitle(mIsGroup ? getResources().getString(R.string.add_group) : getResources().getString(R.string.add_friend), TitleBarLayout.POSITION.LEFT);
        mTitleBar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleBar.getRightGroup().setVisibility(View.GONE);

        mUserID = findViewById(R.id.user_id);
        mAddWording = findViewById(R.id.add_wording);
    }

    public void add(View view) {
        if (mIsGroup) {
            addGroup(view);
        } else {
            addFriend(view);
        }
    }

    //添加好友
    public void addFriend(View view) {
        String id = mUserID.getText().toString();
        if (TextUtils.isEmpty(id)) {
            return;
        }


        EjuImController.getInstance().addFriend(id, mAddWording.getText().toString(), new ImCallBack() {
            @Override
            public void onError(int var1, String var2) {

            }

            @Override
            public void onSuccess(Object data) {
                TIMFriendResult timFriendResult = (TIMFriendResult) data;

                switch (timFriendResult.getResultCode()) {
                    case TIMFriendStatus.TIM_FRIEND_STATUS_SUCC:
                        ToastUtil.toastShortMessage("成功");
                        break;
                    case TIMFriendStatus.TIM_FRIEND_PARAM_INVALID:
                        if (TextUtils.equals(timFriendResult.getResultInfo(), "Err_SNS_FriendAdd_Friend_Exist")) {
                            ToastUtil.toastShortMessage("对方已是您的好友");
                            break;
                        }
                    case TIMFriendStatus.TIM_ADD_FRIEND_STATUS_SELF_FRIEND_FULL:
                        ToastUtil.toastShortMessage("您的好友数已达系统上限");
                        break;
                    case TIMFriendStatus.TIM_ADD_FRIEND_STATUS_THEIR_FRIEND_FULL:
                        ToastUtil.toastShortMessage("对方的好友数已达系统上限");
                        break;
                    case TIMFriendStatus.TIM_ADD_FRIEND_STATUS_IN_SELF_BLACK_LIST:
                        ToastUtil.toastShortMessage("被加好友在自己的黑名单中");
                        break;
                    case TIMFriendStatus.TIM_ADD_FRIEND_STATUS_FRIEND_SIDE_FORBID_ADD:
                        ToastUtil.toastShortMessage("对方已禁止加好友");
                        break;
                    case TIMFriendStatus.TIM_ADD_FRIEND_STATUS_IN_OTHER_SIDE_BLACK_LIST:
                        ToastUtil.toastShortMessage("您已被被对方设置为黑名单");
                        break;
                    case TIMFriendStatus.TIM_ADD_FRIEND_STATUS_PENDING:
                        ToastUtil.toastShortMessage("等待好友审核同意");
                        break;
                    default:
                        ToastUtil.toastLongMessage(timFriendResult.getResultCode() + " " + timFriendResult.getResultInfo());
                        break;
                }

                finish();
            }


        });
    }


    //添加圈子
    public void addGroup(View view) {
        String id = mUserID.getText().toString();
        if (TextUtils.isEmpty(id)) {
            return;
        }


        GroupController.getInstance().applyJoinGroup(id, mAddWording.getText().toString(), new ImCallBack() {
            @Override
            public void onError(int i, String s) {
                LogUtils.e(TAG, "addGroup err code = " + i + ", desc = " + s);
                ToastUtil.toastShortMessage("Error code = " + i + ", desc = " + s);
            }

            @Override
            public void onSuccess(Object var1) {
                LogUtils.i(TAG, "addGroup success");
                ToastUtil.toastShortMessage("加群请求已发送");
                finish();
            }
        });


    }

    @Override
    public void finish() {
        super.finish();
        SoftKeyBoardUtil.hideKeyBoard(mUserID.getWindowToken());
    }
}
