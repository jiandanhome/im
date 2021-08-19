package com.eju.cy.audiovideosample.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.eju.cy.audiovideosample.R;
import com.tencent.imsdk.TIMConversationType;
import com.eju.cy.audiovideo.activity.chat.ChatActivity;
import com.eju.cy.audiovideo.base.BaseActivity;
import com.eju.cy.audiovideo.controller.EjuImController;
import com.eju.cy.audiovideo.controller.ImCallBack;
import com.eju.cy.audiovideo.enumer.DelFriendTypeEnmumer;
import com.eju.cy.audiovideo.modules.chat.base.ChatInfo;
import com.eju.cy.audiovideo.modules.contact.ContactItemBean;
import com.eju.cy.audiovideo.modules.contact.FriendProfileLayout;
import com.eju.cy.audiovideo.tags.Constants;
import com.eju.cy.audiovideo.utils.TUIKitConstants;

import java.util.ArrayList;
import java.util.List;


/**
 * 好友详情界面
 */
public class FriendProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_friend_profile_activity);
        FriendProfileLayout layout = findViewById(R.id.friend_profile);

        layout.initData(getIntent().getSerializableExtra(TUIKitConstants.ProfileType.CONTENT));
        layout.setOnButtonClickListener(new FriendProfileLayout.OnButtonClickListener() {
            @Override
            public void onStartConversationClick(ContactItemBean info) {
                ChatInfo chatInfo = new ChatInfo();
                chatInfo.setType(TIMConversationType.C2C);
                chatInfo.setId(info.getId());
                String chatName = info.getId();

                if (!TextUtils.isEmpty(info.getRemark())) {
                    chatName = info.getRemark();
                } else if (!TextUtils.isEmpty(info.getNickname())) {
                    chatName = info.getNickname();
                }
                chatInfo.setChatName(chatName);
                Intent intent = new Intent(FriendProfileActivity.this, ChatActivity.class);
                intent.putExtra(Constants.CHAT_INFO, chatInfo);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                FriendProfileActivity.this.startActivity(intent);
            }

            @Override
            public void onDeleteFriendClick(String id) {

                //删除好友
                List<String> idList = new ArrayList<>();
                idList.add(id);


                EjuImController.getInstance().deleteFriends(idList, DelFriendTypeEnmumer.TIM_FRIEND_DEL_BOTH, new ImCallBack() {
                    @Override
                    public void onError(int var1, String var2) {
                        finish();
                    }

                    @Override
                    public void onSuccess(Object var1) {
                        finish();
                    }
                });


            }
        });
    }

}
