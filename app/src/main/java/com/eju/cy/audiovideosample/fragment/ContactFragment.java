package com.eju.cy.audiovideosample.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideosample.IApplication;
import com.eju.cy.audiovideosample.R;
import com.eju.cy.audiovideosample.activity.FriendProfileActivity;
import com.eju.cy.audiovideosample.activity.MyGroupListActivity;
import com.eju.cy.audiovideosample.activity.TestActivity;
import com.eju.cy.audiovideosample.menu.Menu;
import com.eju.cy.audiovideo.base.BaseFragment;
import com.eju.cy.audiovideo.modules.contact.ContactItemBean;
import com.eju.cy.audiovideo.modules.contact.ContactLayout;
import com.eju.cy.audiovideo.modules.contact.ContactListView;
import com.eju.cy.audiovideo.utils.TUIKitConstants;

/**
 * @ Name: Caochen
 * @ Date: 2020-05-11
 * @ Time: 15:45
 * @ Description： 联系人列表
 */
public class ContactFragment extends BaseFragment {

    private static final String TAG = ContactFragment.class.getSimpleName();
    private ContactLayout mContactLayout;
    private Menu mMenu;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View baseView = inflater.inflate(R.layout.contact_fragment, container, false);
        initViews(baseView);

        return baseView;
    }

    private void initViews(View view) {
        // 从布局文件中获取通讯录面板
        mContactLayout = view.findViewById(R.id.contact_layout);
        mMenu = new Menu(getActivity(), mContactLayout.getTitleBar(), Menu.MENU_TYPE_CONTACT);
        mContactLayout.getTitleBar().setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMenu.isShowing()) {
                    mMenu.hide();
                } else {
                    mMenu.show();
                }
            }
        });
        mContactLayout.getContactListView().setOnItemClickListener(new ContactListView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ContactItemBean contact) {
                if (position == 0) {
                    Intent intent = new Intent(IApplication.instance(), TestActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    IApplication.instance().startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent(getActivity(), MyGroupListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivity(intent);
                } else if (position == 2) {
//                    Intent intent = new Intent(IApplication.instance(), BlackListActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    IApplication.instance().startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), FriendProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(TUIKitConstants.ProfileType.CONTENT, contact);
                    getActivity().startActivity(intent);
                }
            }
        });
    }

    private void refreshData() {
        // 通讯录面板的默认UI和交互初始化
        mContactLayout.initDefault();
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.i(TAG, "onResume");
        refreshData();
    }
}
