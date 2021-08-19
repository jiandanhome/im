package com.eju.cy.audiovideo.activity.chat;

import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.base.BaseFragment;
import com.eju.cy.audiovideo.component.AudioPlayer;
import com.eju.cy.audiovideo.component.TitleBarLayout;
import com.eju.cy.audiovideo.controller.ConversationController;
import com.eju.cy.audiovideo.controller.EjuImController;
import com.eju.cy.audiovideo.controller.GroupController;
import com.eju.cy.audiovideo.controller.ImCallBack;
import com.eju.cy.audiovideo.dto.CustomMessage;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.dto.MsgContentDto;
import com.eju.cy.audiovideo.dto.ReplaceMsgDto;
import com.eju.cy.audiovideo.helper.ChatLayoutHelper;
import com.eju.cy.audiovideo.modules.chat.ChatLayout;
import com.eju.cy.audiovideo.modules.chat.base.ChatInfo;
import com.eju.cy.audiovideo.modules.chat.layout.message.MessageLayout;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.eju.cy.audiovideo.modules.message.MessageInfoUtil;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.observer.EjuHomeImObserver;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.tags.Constants;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.google.gson.Gson;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessageManager;

import java.util.ArrayList;
import java.util.List;

import static com.eju.cy.audiovideo.tags.ActionTags.ACTION_CLICK_DELETE_CARD;

/**
 * 聊天列表
 */
public class ChatFragment extends BaseFragment implements EjuHomeImObserver {

    private View mBaseView;

    public ChatLayout getmChatLayout() {
        return mChatLayout;
    }

    private ChatLayout mChatLayout;
    private TitleBarLayout mTitleBar;
    private ChatInfo mChatInfo;

    private String mTitle = "";

    private boolean isdelete;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        EjuHomeImEventCar.getDefault().register(this);
        mBaseView = inflater.inflate(R.layout.chat_fragment, container, false);
        return mBaseView;
    }



    private void initView() {
        //从布局文件中获取聊天面板组件
        mChatLayout = mBaseView.findViewById(R.id.chat_layout);

        //单聊组件的默认UI和交互初始化
        mChatLayout.initDefault();

        /*
         * 需要聊天的基本信息
         */
        mChatLayout.setChatInfo(mChatInfo);

        //获取单聊面板的标题栏
        mTitleBar = mChatLayout.getTitleBar();

        //单聊面板标记栏返回按钮点击事件，这里需要开发者自行控制
        mTitleBar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMsg();
            }
        });


        //单聊
        if (mChatInfo.getType() == TIMConversationType.C2C) {


            if (null != EjuImController.getInstance().getChatAtRightTitle() && EjuImController.getInstance().getChatAtRightTitle().length() > 0) {
                mTitleBar.getRightTitle().setVisibility(View.VISIBLE);
                mTitleBar.getRightIcon().setVisibility(View.GONE);
                mTitleBar.getRightTitle().setText(EjuImController.getInstance().getChatAtRightTitle());
            }


            if (EjuImController.getInstance().getChatAtRightIcon() > 0) {
                mTitleBar.getRightIcon().setVisibility(View.VISIBLE);
                mTitleBar.setRightIcon(EjuImController.getInstance().getChatAtRightIcon());
                mTitleBar.getRightTitle().setVisibility(View.GONE);
            }


            mTitleBar.setOnRightClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // ToastUtils.showLong("业务方控制--用户信息");
                    ImActionDto onRightClick = new ImActionDto();

                    String str = JsonUtils.toJson(mChatInfo);
                    onRightClick.setAction(ActionTags.CHAT_C2C_RIGHT);


                    onRightClick.setJsonStr(str);
                    String cardStr = JsonUtils.toJson(onRightClick);
                    EjuHomeImEventCar.getDefault().post(cardStr);

                }
            });
        } else if (mChatInfo.getType() == TIMConversationType.Group) {


            if (null != EjuImController.getInstance().getChatAtGroupRightTitle() && EjuImController.getInstance().getChatAtGroupRightTitle().length() > 0) {
                mTitleBar.getRightTitle().setVisibility(View.VISIBLE);
                mTitleBar.getRightIcon().setVisibility(View.GONE);
                mTitleBar.getRightTitle().setText(EjuImController.getInstance().getChatAtGroupRightTitle());
            }


            if (EjuImController.getInstance().getmChatAtGroupRightIcon() > 0) {
                mTitleBar.getRightIcon().setVisibility(View.VISIBLE);
                mTitleBar.setRightIcon(EjuImController.getInstance().getmChatAtGroupRightIcon());
                mTitleBar.getRightTitle().setVisibility(View.GONE);
            }


//            mTitleBar.getRightTitle().setText(EjuImController.getInstance().getChatAtGroupRightTitle());
//            mTitleBar.getRightTitle().setVisibility(View.VISIBLE);
//            mTitleBar.getRightIcon().setVisibility(View.GONE);
            mTitleBar.setOnRightClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // ToastUtils.showLong("业务方控制-群组信息");

                    //发送圈子聊天界面右边点击事件（用于控制圈子详情界面）
                    ImActionDto onRightClick = new ImActionDto();
                    String str = JsonUtils.toJson(mChatInfo);
                    onRightClick.setAction(ActionTags.CHAT_GUOUP_RIGHT);
                    onRightClick.setJsonStr(str);
                    String cardStr = JsonUtils.toJson(onRightClick);
                    EjuHomeImEventCar.getDefault().post(cardStr);


//                    Intent intent = new Intent(getActivity(), GroupInfoActivity.class);
//                    intent.putExtra(TUIKitConstants.Group.GROUP_ID, mChatInfo.getId());
//                    getActivity().startActivity(intent);


                }
            });


        }

        // mChatLayout.getInputLayout().setActionShow();


        mChatLayout.getMessageLayout().setOnItemClickListener(new MessageLayout.OnItemClickListener() {
            @Override
            public void onMessageLongClick(View view, int position, MessageInfo messageInfo) {
                //因为adapter中第一条为加载条目，位置需减1

                mChatLayout.getMessageLayout().showItemPopMenu(position - 1, messageInfo, view);
            }

            @Override
            public void onUserIconClick(View view, int position, MessageInfo messageInfo) {
                if (null == messageInfo) {
                    return;
                }

                LogUtils.w("用户头像点击--用户信息" + messageInfo.getFromUser());
                ImActionDto onUserAvatarClick = new ImActionDto();
                onUserAvatarClick.setAction(ActionTags.ACTION_CLICK_USER_AVATAR_APPLY);
                onUserAvatarClick.setJsonStr(messageInfo.getFromUser());
                String cardStr = JsonUtils.toJson(onUserAvatarClick);
                EjuHomeImEventCar.getDefault().post(cardStr);


                //ToastUtils.showLong("用户头像点击--用户信息");

//                ChatInfo info = new ChatInfo();
//                info.setId(messageInfo.getFromUser());
//                Intent intent = new Intent(DemoApplication.instance(), FriendProfileActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra(TUIKitConstants.ProfileType.CONTENT, info);
//                DemoApplication.instance().startActivity(intent);
            }


        });
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle bundle = getArguments();
        mChatInfo = (ChatInfo) bundle.getSerializable(Constants.CHAT_INFO);
        if (mChatInfo == null) {
            return;
        }
        initView();


        ChatLayoutHelper helper = new ChatLayoutHelper(getActivity());
        helper.customizeChatLayout(mChatLayout);

        LogUtils.w("onResume------" + mChatInfo.getId() + "-----" + mChatInfo.getChatName());


        if (!"".equals(mTitle) && mTitle.length() > 0) {
            mChatLayout.getTitleBar().getMiddleTitle().setText(mTitle);
        }

        if (null != mChatLayout && null != mChatInfo && mChatInfo.getType().equals(TIMConversationType.Group)) {
            mChatLayout.loadApplyList(false);
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        AudioPlayer.getInstance().stopPlay();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatLayout != null) {
            EjuHomeImEventCar.getDefault().unregister(this);
            mChatLayout.exitChat();
        }
        EjuImController.getInstance().clearVmap();

    }


    @Override
    public void action(Object obj) {
        if (null != obj) {

            String srt = (String) obj;
            final ImActionDto imActionDto = JsonUtils.fromJson(srt, ImActionDto.class);

            if (null != imActionDto)
                switch (imActionDto.getAction()) {

                    case ActionTags.UPDATE_GUOUP_NAME:
                        mTitle = imActionDto.getJsonStr();
                        break;

                    //删除消息
                    case ActionTags.ACTION_CLICK_DELETE_CARD:
                        MessageInfo messageInfo = JsonUtils.fromJson(imActionDto.getJsonStr(), MessageInfo.class);


                        if (null != mChatLayout.getMyAdapter().getDataSouurce()) {
                            TIMMessage timMessage = new TIMMessage();
                            int pos = -1;
                            String toUserId = "";
                            String msgId = "";
                            long msgTime = 0;
                            MessageInfo deleInfo = null;
                            for (int i = 0; i < mChatLayout.getMyAdapter().getDataSouurce().size(); i++) {
                                if (mChatLayout.getMyAdapter().getDataSouurce().get(i).getId().equals(messageInfo.getId())) {
                                    toUserId = mChatLayout.getMyAdapter().getDataSouurce().get(i).getTIMMessage().getConversation().getPeer();
                                    deleInfo = mChatLayout.getMyAdapter().getDataSouurce().get(i);

                                    msgId = mChatLayout.getMyAdapter().getDataSouurce().get(i).getId();
                                    msgTime = mChatLayout.getMyAdapter().getDataSouurce().get(i).getMsgTime();

                                    //   LogUtils.w("数据是----" + mChatLayout.getMyAdapter().getDataSouurce().get(i).getTIMMessage().getConversation().getPeer());
                                    pos = i;
                                    timMessage = mChatLayout.getMyAdapter().getDataSouurce().get(i).getTIMMessage();
                                    break;
                                }
                            }
                            //找出位置


                            if (-1 != pos) {

                                //   LogUtils.w("对方ID" + toUserId);
                                //删除
                                // mChatLayout.deleteMsg(pos, deleInfo);

                                // mChatLayout.getMyAdapter().getDataSouurce().get(pos).getTIMMessage().

                                //自定义消息体
                                CustomMessage customMessage = new CustomMessage();
                                customMessage.setAction(16);

                                String msgData = JsonUtils.toJson(customMessage);


                                //组装
                                MsgContentDto msgContentDto = new MsgContentDto();
                                msgContentDto.setData(msgData);
                                msgContentDto.setDesc("notification");
                                // String msgContentStr = JsonUtils.toJson(msgContentDto);

                                ReplaceMsgDto replaceMsgDto = new ReplaceMsgDto();
                                replaceMsgDto.setMsgType("TIMCustomElem");
                                replaceMsgDto.setMsgContent(msgContentDto);


                                String replaceMsgStr = JsonUtils.toJson(replaceMsgDto);


                                final int finalPos = pos;
                                final TIMMessage finalTimMessage = timMessage;
                                final MessageInfo finalDeleInfo = deleInfo;
                                ConversationController.getInstance().deleteMsg(getActivity(), deleInfo.getFromUser(), toUserId, msgId, msgTime + "", replaceMsgStr, new ImCallBack() {
                                    @Override
                                    public void onError(int var1, String var2) {
                                        /*
                                         * 改变聊天内容
                                         */
                                        isdelete = true;
                                        TIMCustomElem elem = (TIMCustomElem) mChatLayout.getMyAdapter().getDataSouurce().get(finalPos).getElement();
                                        CustomMessage msg = JsonUtils.fromJson(new String(elem.getData()), CustomMessage.class);

                                        LogUtils.w("原Action是-------" + msg.getAction());

                                        msg.setAction(CustomMessage.CUSTOM_CARD_REVIEW_READ);
                                        String data = JsonUtils.toJson(msg);
                                        elem.setData(data.getBytes());
                                        mChatLayout.getMyAdapter().getDataSouurce().get(finalPos).setElement(elem);
                                        mChatLayout.getMyAdapter().getDataSouurce().get(finalPos).setDelete(true);
                                        mChatLayout.getMyAdapter().notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onSuccess(Object var1) {

                                        /*
                                         * 改变聊天内容
                                         */
                                        isdelete = true;
                                        TIMCustomElem elem = (TIMCustomElem) mChatLayout.getMyAdapter().getDataSouurce().get(finalPos).getElement();
                                        CustomMessage msg = JsonUtils.fromJson(new String(elem.getData()), CustomMessage.class);

                                        LogUtils.w("原Action是-------" + msg.getAction());

                                        msg.setAction(CustomMessage.CUSTOM_CARD_REVIEW_READ);
                                        String data = JsonUtils.toJson(msg);
                                        elem.setData(data.getBytes());
                                        mChatLayout.getMyAdapter().getDataSouurce().get(finalPos).setElement(elem);
                                        mChatLayout.getMyAdapter().getDataSouurce().get(finalPos).setDelete(true);
                                        mChatLayout.getMyAdapter().notifyDataSetChanged();

                                    }
                                });


                            }
                        }


                        break;

                }


        }
    }


    public ChatLayout getChatLayout() {


        return mChatLayout;


    }


    /**
     * 删除消息
     */
    public void deleteMsg() {



        if (isdelete) {


            for (int j = 0; j < mChatLayout.getMyAdapter().getDataSouurce().size(); j++) {


                if (mChatLayout.getMyAdapter().getDataSouurce().get(j).isDelete() == true) {
                    LogUtils.w("删除卡片的下标" + j);
                    mChatLayout.deleteMsg(j, mChatLayout.getMyAdapter().getDataSouurce().get(j));
                        j--;

                }


            }
        }


        getActivity().finish();

    }


}
