package com.eju.cy.audiovideosample.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.activity.chat.ChatActivity;
import com.eju.cy.audiovideo.base.BaseFragment;
import com.eju.cy.audiovideo.base.IUIKitCallBack;
import com.eju.cy.audiovideo.component.action.PopActionClickListener;
import com.eju.cy.audiovideo.component.action.PopDialogAdapter;
import com.eju.cy.audiovideo.component.action.PopMenuAction;
import com.eju.cy.audiovideo.controller.C2CController;
import com.eju.cy.audiovideo.controller.ConversationController;
import com.eju.cy.audiovideo.modules.chat.base.ChatInfo;
import com.eju.cy.audiovideo.modules.conversation.ConversationLayout;
import com.eju.cy.audiovideo.modules.conversation.ConversationListLayout;
import com.eju.cy.audiovideo.modules.conversation.ConversationManagerKit;
import com.eju.cy.audiovideo.modules.conversation.ConversationProvider;
import com.eju.cy.audiovideo.modules.conversation.base.ConversationInfo;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.eju.cy.audiovideo.tags.Constants;
import com.eju.cy.audiovideo.utils.PopWindowUtil;
import com.eju.cy.audiovideo.utils.ToastUtil;
import com.eju.cy.audiovideosample.IApplication;
import com.eju.cy.audiovideosample.R;
import com.eju.cy.audiovideosample.menu.Menu;
import com.tencent.imsdk.TIMConversationType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会话列表
 */
public class TestFragment extends BaseFragment {

    private View mBaseView;
    private ConversationLayout mConversationLayout;
    private ListView mConversationPopList;
    private PopDialogAdapter mConversationPopAdapter;
    private PopupWindow mConversationPopWindow;
    private List<PopMenuAction> mConversationPopActions = new ArrayList<>();
    private Menu mMenu;
    private ImageView iv_add_function;
    private RelativeLayout rl_title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.conversation_fragment, container, false);
        initView();
        return mBaseView;
    }

    private void initView() {
        // 从布局文件中获取会话列表面板
        iv_add_function = mBaseView.findViewById(R.id.iv_add_function);
        rl_title = mBaseView.findViewById(R.id.rl_title);

        mConversationLayout = mBaseView.findViewById(R.id.conversation_layout);
        mMenu = new Menu(getActivity(), rl_title, Menu.MENU_TYPE_CONVERSATION);
        // 会话列表面板的默认UI和交互初始化
        mConversationLayout.initDefault();

        //设置自定义卡片数据--数据来源于客户端服务器，以下为演示
        C2CController.getInstance().setCustomCard(getCustomCad());
        //获取会话列表
        initData();


        //点击
        mConversationLayout.getConversationList().setOnItemClickListener(new ConversationListLayout.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, ConversationInfo conversationInfo) {

                if (conversationInfo.getTitle().equals("简单美房客服小美")) {
                    startChatActivity(conversationInfo);

                } else {
                    C2CController.getInstance().openChatActivity(getActivity(), conversationInfo.getId(), conversationInfo.getTitle(), "角色");
                }

            }
        });
        //长按
        mConversationLayout.getConversationList().setOnItemLongClickListener(new ConversationListLayout.OnItemLongClickListener() {
            @Override
            public void OnItemLongClick(View view, int position, ConversationInfo conversationInfo) {
                startPopShow(view, position, conversationInfo);
                //是否需要长按功能 按需使用
            }
        });
        initTitleAction();
        initPopMenuAction();
    }

    private void initTitleAction() {

        iv_add_function.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMenu.isShowing()) {
                    mMenu.hide();
                } else {
                    mMenu.show();
                }
            }
        });


    }

    private void initPopMenuAction() {

        // 设置长按conversation显示PopAction
        List<PopMenuAction> conversationPopActions = new ArrayList<PopMenuAction>();
        PopMenuAction action = new PopMenuAction();
        action.setActionName(getResources().getString(R.string.chat_top));
        action.setActionClickListener(new PopActionClickListener() {
            @Override
            public void onActionClick(int position, Object data) {
                mConversationLayout.setConversationTop(position, (ConversationInfo) data);
            }
        });
        conversationPopActions.add(action);
        action = new PopMenuAction();
        action.setActionClickListener(new PopActionClickListener() {
            @Override
            public void onActionClick(int position, Object data) {
                mConversationLayout.deleteConversation(position, (ConversationInfo) data);
            }
        });
        action.setActionName(getResources().getString(R.string.chat_delete));
        conversationPopActions.add(action);
        mConversationPopActions.clear();
        mConversationPopActions.addAll(conversationPopActions);
    }

    /**
     * 长按会话item弹框
     *
     * @param index            会话序列号
     * @param conversationInfo 会话数据对象
     * @param locationX        长按时X坐标
     * @param locationY        长按时Y坐标
     */
    private void showItemPopMenu(final int index, final ConversationInfo conversationInfo, float locationX, float locationY) {
        if (mConversationPopActions == null || mConversationPopActions.size() == 0)
            return;
        View itemPop = LayoutInflater.from(getActivity()).inflate(R.layout.pop_menu_layout, null);
        mConversationPopList = itemPop.findViewById(R.id.pop_menu_list);
        mConversationPopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PopMenuAction action = mConversationPopActions.get(position);
                if (action.getActionClickListener() != null) {
                    action.getActionClickListener().onActionClick(index, conversationInfo);
                }
                mConversationPopWindow.dismiss();
            }
        });

        for (int i = 0; i < mConversationPopActions.size(); i++) {
            PopMenuAction action = mConversationPopActions.get(i);
            if (conversationInfo.isTop()) {
                if (action.getActionName().equals(getResources().getString(R.string.chat_top))) {
                    action.setActionName(getResources().getString(R.string.quit_chat_top));
                }
            } else {
                if (action.getActionName().equals(getResources().getString(R.string.quit_chat_top))) {
                    action.setActionName(getResources().getString(R.string.chat_top));
                }

            }
        }
        mConversationPopAdapter = new PopDialogAdapter();
        mConversationPopList.setAdapter(mConversationPopAdapter);
        mConversationPopAdapter.setDataSource(mConversationPopActions);
        mConversationPopWindow = PopWindowUtil.popupWindow(itemPop, mBaseView, (int) locationX, (int) locationY);
        mBaseView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mConversationPopWindow.dismiss();
            }
        }, 10000); // 10s后无操作自动消失
    }

    private void startPopShow(View view, int position, ConversationInfo info) {
        showItemPopMenu(position, info, view.getX(), view.getY() + view.getHeight() / 2);
    }

    /**
     * 打开某个会话
     *
     * @param conversationInfo
     */
    private void startChatActivity(ConversationInfo conversationInfo) {
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setType(conversationInfo.isGroup() ? TIMConversationType.Group : TIMConversationType.C2C);
        chatInfo.setId(conversationInfo.getId());
        chatInfo.setUserRole("你好啊");
        chatInfo.setChatName(conversationInfo.getTitle());
        Intent intent = new Intent(IApplication.instance(), ChatActivity.class);
        intent.putExtra(Constants.CHAT_INFO, chatInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        IApplication.instance().startActivity(intent);

        LogUtils.w("conversationInfo.getId()-----" + conversationInfo.getId());

    }

    /**
     * 获取用户会话列表
     */
    private void initData() {



        LogUtils.w("非好友未读消息"+C2CController.getInstance().loadC2cAndNonFriendsUnreadCount());
        LogUtils.w("好友未读消息"+C2CController.getInstance().loadC2cAndFriendsUnreadCount());


        ConversationManagerKit.getInstance().loadC2cAndNonFriendsConversation(new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {

//                //拿到数据后设置下setUserType,用于区分用户类型
                ConversationProvider mData = (ConversationProvider) data;

                //设置用户角色
                List<String> strList = new ArrayList<>();
                strList.add("易楼用户");
                strList.add("好友a ");

                //设置用户角标
                Map<String, Integer> userLevel = new HashMap<>();


                for (int i = 0; i < mData.getDataSource().size(); i++) {
                    mData.getDataSource().get(i).setUserRoleList(strList);

                    if (i == 1 || i == 3 || i == 5) {
                        userLevel.put(mData.getDataSource().get(i).getId(), 1);
                    }

                }


                //设置角标
                ConversationController.getInstance().setUserLevel(userLevel);


                //设置会话列表数据
                mConversationLayout.setDataSource(mData);


                //3.7  不需要标签 如下设置
                //  mConversationLayout.setDataSource(mData);

            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ToastUtil.toastLongMessage("加载消息失败");
            }
        });


    }

    /**
     * 设置自定义卡片
     */
    private List<ConversationInfo> getCustomCad() {

        List<ConversationInfo> adapterList = new ArrayList<>();


        //客服
        ConversationInfo customerService = new ConversationInfo();
        //title
        customerService.setTitle("简单美房客服小美");
        //此处是IM聊天即为美房客服IM id


        customerService.setId(C2CController.getInstance().getServiceID());
        //value
        MessageInfo customerServiceMessageInfo = new MessageInfo();
        customerServiceMessageInfo.setExtra("我是小美啊");
        customerServiceMessageInfo.setMsgTime(1590385982);
        customerServiceMessageInfo.setGroup(false);
        //设置消息类型为文本
        customerServiceMessageInfo.setMsgType(MessageInfo.MSG_TYPE_TEXT);
        customerService.setLastMessage(customerServiceMessageInfo);
        //设置卡片类型为自定义类型
        //设置卡片类型为自定义类型
        customerService.setType(ConversationInfo.TYPE_APP_CUSTOM);


        List<Object> iconUrl = new ArrayList<>();
        iconUrl.add(com.eju.cy.audiovideo.R.drawable.ic_av_guaduan);
        customerService.setIconUrlList(iconUrl);


        //追踪
        ConversationInfo tracking = new ConversationInfo();
        //title
        tracking.setTitle("客户追踪");
        //此处是IM聊天即为美房客服IM id
        tracking.setId("888888");
        //value
        MessageInfo trackingMessageInfo = new MessageInfo();
        trackingMessageInfo.setExtra("客户追踪");
        trackingMessageInfo.setMsgTime(1590385982);
        trackingMessageInfo.setGroup(false);
        //设置消息类型为文本
        trackingMessageInfo.setMsgType(MessageInfo.MSG_TYPE_TEXT);
        tracking.setLastMessage(customerServiceMessageInfo);
        //设置卡片类型为自定义类型
        //设置卡片类型为自定义类型
        tracking.setType(ConversationInfo.TYPE_APP_CUSTOM);


        List<Object> trackingIconUrl = new ArrayList<>();
        trackingIconUrl.add(com.eju.cy.audiovideo.R.drawable.ic_av_jieting);
        tracking.setIconUrlList(trackingIconUrl);


        //动态
        ConversationInfo dynamic = new ConversationInfo();
        //title
        dynamic.setTitle("美房动态");
        //此处是IM聊天即为美房客服IM id
        dynamic.setId("8888889");
        //value
        MessageInfo dynamicMessageInfo = new MessageInfo();
        dynamicMessageInfo.setExtra("美房动态");
        dynamicMessageInfo.setMsgTime(1590385982);
        dynamicMessageInfo.setGroup(false);
        //设置消息类型为文本
        dynamicMessageInfo.setMsgType(MessageInfo.MSG_TYPE_TEXT);
        dynamic.setLastMessage(customerServiceMessageInfo);
        //设置卡片类型为自定义类型
        //设置卡片类型为自定义类型
        dynamic.setType(ConversationInfo.TYPE_APP_CUSTOM);


        List<Object> dynamicIconUrl = new ArrayList<>();
        dynamicIconUrl.add(com.eju.cy.audiovideo.R.drawable.ic_av_jingyinweikaiqi);
        dynamic.setIconUrlList(dynamicIconUrl);


        adapterList.add(customerService);
        adapterList.add(tracking);
        adapterList.add(dynamic);


        return adapterList;

    }


}