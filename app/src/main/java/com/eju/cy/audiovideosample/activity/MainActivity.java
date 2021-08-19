package com.eju.cy.audiovideosample.activity;

import android.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.base.BaseActivity;
import com.eju.cy.audiovideo.controller.C2CController;
import com.eju.cy.audiovideo.controller.CustomMsgController;
import com.eju.cy.audiovideo.controller.EjuImController;
import com.eju.cy.audiovideo.controller.ImCallBack;
import com.eju.cy.audiovideo.dto.CustomContentDto;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.helper.CustomAVCallUIController;
import com.eju.cy.audiovideo.helper.CustomGroupAVCallUIController;
import com.eju.cy.audiovideo.helper.CustomVideoCallUIController;
import com.eju.cy.audiovideo.modules.chat.GroupChatManagerKit;
import com.eju.cy.audiovideo.modules.chat.base.ChatInfo;
import com.eju.cy.audiovideo.modules.conversation.ConversationManagerKit;
import com.eju.cy.audiovideo.modules.conversation.base.ConversationInfo;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.observer.EjuHomeImObserver;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.utils.FileUtil;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.eju.cy.audiovideosample.R;
import com.eju.cy.audiovideosample.fragment.ContactFragment;
import com.eju.cy.audiovideosample.fragment.ConversationFragment;
import com.eju.cy.audiovideosample.fragment.TestFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.imsdk.TIMConversationType;

import java.util.Map;

public class MainActivity extends BaseActivity implements ConversationManagerKit.MessageUnreadWatcher, EjuHomeImObserver {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView mConversationBtn;
    private TextView mContactBtn;
    private TextView mProfileSelfBtn;
    private TextView mMsgUnread;
    private View mLastTab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        EjuHomeImEventCar.getDefault().register(this);


        String json = "{\n" +
                "  \"Action12Text\" : \"@刘哈哈\",\n" +
                "  \"idAndName\" : \"{\\n  \\\"10983\\\" : \\\"刘哈哈\\\"\\n}\"\n" +
                "}";


        CustomContentDto customContentDto = JsonUtils.fromJson(json, CustomContentDto.class);


        Map<String, String> map = new Gson().fromJson(customContentDto.getIdAndName(), new TypeToken<Map<String, String>>() {
        }.getType());


        LogUtils.w("mapmapmap---" + map.size());


//        GroupController.getInstance().sendTipsMessage("6ee6694ae43a448389852d1bc2ae492c", new ImCallBack() {
//            @Override
//            public void onError(int var1, String var2) {
//
//            }
//
//            @Override
//            public void onSuccess(Object var1) {
//
//            }
//        });






//        //待解析的json字符串
//        String jsonString = "{'name':'卢本伟','age':24,'Position':'Mid'}";
//
//        //因为json字符串是大括号包围，所以用JSONObject解析
//        try {
//            JSONObject json = new JSONObject(jsonString);
//
//
//
//            LogUtils.w("---------"+json.get("name"));
//
//
//
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


//        UserInfoController.getInstance().getUsersProfile("101904", true, new ImCallBack() {
//            @Override
//            public void onError(int var1, String var2) {
//
//            }
//
//            @Override
//            public void onSuccess(Object var1) {
//
//                List<TIMUserProfile> timUserProfiles = (List<TIMUserProfile>) var1;
//
//                LogUtils.w("101904的用户信息--"+timUserProfiles.get(0).toString());
//
//            }
//        });


//
//        GroupController.getInstance().modifyGroupOwner("2d495c73e75a4cbeaf79b52ff4bb2695", "10211425", new ImCallBack() {
//            @Override
//            public void onError(int var1, String var2) {
//
//            }
//
//            @Override
//            public void onSuccess(Object var1) {
//
//            }
//        });

//
//        GroupController.getInstance().modifyGroupOwner("00aaf4f858404513af18f8c4b43e5227", "109027", new ImCallBack() {
//            @Override
//            public void onError(int var1, String var2) {
//
//            }
//
//            @Override
//            public void onSuccess(Object var1) {
//
//            }
//        });


//
//        GroupController.getInstance().modifyMemberInfo("00aaf4f858404513af18f8c4b43e5227", "10211425", GroupMemberRoleTypeEnmunmer.ROLE_TYPE_NORMAL, new ImCallBack() {
//            @Override
//            public void onError(int var1, String var2) {
//
//            }
//
//            @Override
//            public void onSuccess(Object var1) {
//
//            }
//        });


    }


    private void initView() {

        CustomAVCallUIController.getInstance().setActivityContext(MainActivity.this);
        CustomVideoCallUIController.getInstance().setActivityContext(MainActivity.this);
        CustomGroupAVCallUIController.getInstance().setActivityContext(MainActivity.this);

        //初始化ChatActivity中右侧显示按钮
        // EjuImController.getInstance().setChatAtRightTitle("C2C");
        EjuImController.getInstance().setChatAtGroupRightTitle("详情");
        EjuImController.getInstance().setChatAtRightIcon(R.drawable.ic_back);


        LogUtils.w("loadC2CUnreadCount--" + C2CController.getInstance().loadC2cAndFriendsUnreadCount());


//        GroupController.getInstance().setNotification("888888", "春种一粒粟，秋收万颗子" + "四海无闲田，农夫犹饿死。" + "锄禾日当午，汗滴禾下土。" + "谁知盘中餐，粒粒皆辛苦", new ImCallBack() {
//            @Override
//            public void onError(int var1, String var2) {
//
//            }
//
//            @Override
//            public void onSuccess(Object var1) {
//                LogUtils.w("setNotification------");
//            }
//        });


        //  GroupController.getInstance().setGroupNotification("33333", "但是，上述方式只适用于界面中只有1个TextView的情况，当一个界面之中有多个TextView设置了相同属性，有且只有一个控件会滚动显示，其他TextView控件则不会滚动显示。");


//        GroupController.getInstance().getGroupStatic("@TGS#2CRZSXOGR","0",new ImCallBack(){
//            @Override
//            public void onError(int var1, String var2) {
//                LogUtils.w("groupsInfoListBean-----onError"+var2.toString());
//            }
//
//            @Override
//            public void onSuccess(Object var1) {
//
//                GuoupStaticDto guoupStaticDto = (GuoupStaticDto) var1;
//                LogUtils.w("guoupStaticDto siz-----"+guoupStaticDto.getData().getGroups_info_list().size());
//                for (GuoupStaticDto.DataBean.GroupsInfoListBean  groupsInfoListBean :guoupStaticDto.getData().getGroups_info_list()){
//
//                    LogUtils.w("groupsInfoListBean-----"+groupsInfoListBean.toString());
//
//
//                }
//
//            }
//        });


        setContentView(R.layout.activity_main);
        mConversationBtn = findViewById(R.id.conversation);
        mContactBtn = findViewById(R.id.contact);
        mProfileSelfBtn = findViewById(R.id.mine);
        mMsgUnread = findViewById(R.id.msg_total_unread);
        getFragmentManager().beginTransaction().replace(R.id.empty_view, new ConversationFragment()).commitAllowingStateLoss();
        FileUtil.initPath(); // 从application移入到这里，原因在于首次装上app，需要获取一系列权限，如创建文件夹，图片下载需要指定创建好的文件目录，否则会下载本地失败，聊天页面从而获取不到图片、表情

        // 未读消息监视器
        ConversationManagerKit.getInstance().addUnreadWatcher(this);
        GroupChatManagerKit.getInstance();
        if (mLastTab == null) {
            mLastTab = findViewById(R.id.conversation_btn_group);
        } else {
            // 初始化时，强制切换tab到上一次的位置
            View tmp = mLastTab;
            mLastTab = null;
            tabClick(tmp);
            mLastTab = tmp;
        }
    }


    public void tabClick(View view) {
        LogUtils.i(TAG, "tabClick last: " + mLastTab + " current: " + view);
        if (mLastTab != null && mLastTab.getId() == view.getId()) {
            return;
        }
        mLastTab = view;
        resetMenuState();
        Fragment current = null;
        switch (view.getId()) {
            case R.id.conversation_btn_group:
                current = new ConversationFragment();
                mConversationBtn.setTextColor(getResources().getColor(R.color.tab_text_selected_color));
                mConversationBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.conversation_selected), null, null);
                break;
            case R.id.contact_btn_group:
                current = new ContactFragment();
                mContactBtn.setTextColor(getResources().getColor(R.color.tab_text_selected_color));
                mContactBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.contact_selected), null, null);


                break;


            case R.id.myself_btn_group:
                current = new TestFragment();
                mProfileSelfBtn.setTextColor(getResources().getColor(R.color.tab_text_selected_color));
                mProfileSelfBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.myself_selected), null, null);


            default:
                break;
        }

        if (current != null && !current.isAdded()) {
            getFragmentManager().beginTransaction().replace(R.id.empty_view, current).commitAllowingStateLoss();
            getFragmentManager().executePendingTransactions();
        } else {
            LogUtils.w(TAG, "fragment added!");
        }
    }

    private void resetMenuState() {
        mConversationBtn.setTextColor(getResources().getColor(R.color.tab_text_normal_color));
        mConversationBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.conversation_normal), null, null);
        mContactBtn.setTextColor(getResources().getColor(R.color.tab_text_normal_color));
        mContactBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.contact_normal), null, null);
        mProfileSelfBtn.setTextColor(getResources().getColor(R.color.tab_text_normal_color));
        mProfileSelfBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.myself_normal), null, null);
    }


    @Override
    public void updateUnread(int count) {

        LogUtils.w("updateUnread----" + count);
        if (count > 0) {
            mMsgUnread.setVisibility(View.VISIBLE);
        } else {
            mMsgUnread.setVisibility(View.GONE);
        }
        String unreadStr = "" + count;
        if (count > 100) {
            unreadStr = "99+";
        }
        mMsgUnread.setText(unreadStr);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return true;

    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onStart() {
        LogUtils.i(TAG, "onStart");
        super.onStart();
        initView();
    }

    @Override
    protected void onResume() {
        LogUtils.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        LogUtils.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        LogUtils.i(TAG, "onStop");
        ConversationManagerKit.getInstance().destroyConversation();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LogUtils.i(TAG, "onDestroy");
        mLastTab = null;
        EjuHomeImEventCar.getDefault().unregister(this);
        super.onDestroy();
    }


    @Override
    public void action(Object obj) {

        if (null != obj) {

            String srt = (String) obj;

            ImActionDto imActionDto = JsonUtils.fromJson(srt, ImActionDto.class);

            LogUtils.w("ACTIVON--------" + imActionDto.getAction() + "\n" + imActionDto.getJsonStr());


            switch (imActionDto.getAction()) {

                //发送名片
                case ActionTags.ACTION_SEND_BUSINESS_CARD:
//                    CustomContentDto contentDto = new CustomContentDto();
//                    contentDto.setCardName("郭富城");
//                    contentDto.setPhoneNumber("15601891066");
//                    contentDto.setPosition("房产顾问");
//                    contentDto.setPortraitUrl("");
//                    contentDto.setShopAddress("延长路门店");
//                    contentDto.setShopName("易居房友");
//
//                    contentDto.setPortraitUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=15880&di=ac74ee30d71eadfcb6b1985dd2ca322b&imgtype=0&src=http%3A%2F%2Fbpic.588ku.com%2Felement_origin_min_pic%2F00%2F10%2F74%2F3856a4ae2679e6c.jpg");
//                    contentDto.setSlogan("您买的放心\n我就会开心");
//
//
//                    CustomMsgController.getInstance().sendBusinessCard(contentDto, "1");


                    CustomContentDto contentDto = new CustomContentDto();
                    contentDto.setTitle("经纪圈如何营销经纪圈如何营销经纪圈如何营销经纪圈如何营销经纪圈如何营销经纪圈如何营销经纪圈如何营销。");
                   // contentDto.setContentUrl("");
                     contentDto.setContentUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1588764928820&di=ac74ee30d71eadfcb6b1985dd2ca322b&imgtype=0&src=http%3A%2F%2Fbpic.588ku.com%2Felement_origin_min_pic%2F00%2F10%2F74%2F3856a4ae2679e6c.jpg");
                    CustomMsgController.getInstance().sendArticle("10211425", TIMConversationType.C2C, contentDto, "文章链接地址点击的时候原封不动的返回给你", new ImCallBack() {
                        @Override
                        public void onError(int var1, String var2) {

                        }

                        @Override
                        public void onSuccess(Object var1) {

                        }
                    });


                    break;
                //发送房源
                case ActionTags.ACTION_SEND_HOUSING:
                    CustomContentDto houingDto = new CustomContentDto();
                    houingDto.setTitle("宝华现代城 2室2厅 990万 超高性价");
                    houingDto.setHouseUrl("https://img-test.jiandanhome.com/webget/1906/21/75bd6e6893e011e98b09fa163edde52c.jpg");


                    CustomMsgController.getInstance().sendHousing(houingDto, "2");
                    break;

                //处理单聊右侧按钮点击事件后逻辑
                case ActionTags.CHAT_C2C_RIGHT:
                    LogUtils.w("处理单聊右侧按钮点击事件后逻辑" + imActionDto.getJsonStr());//此时imActionDto.getJsonStr()为会话ID（对方ID）

                    ChatInfo chatInfo = JsonUtils.fromJson(imActionDto.getJsonStr(), ChatInfo.class);
                    ConversationInfo conversationInfo = JsonUtils.fromJson(chatInfo.getConversationInfoStr(), ConversationInfo.class);


                    LogUtils.w("setConversationTop---" + conversationInfo.isTop());


                    if (conversationInfo.isTop()) {
                        C2CController.getInstance().setConversationTop(chatInfo.getId(), false);
                    } else {
                        C2CController.getInstance().setConversationTop(chatInfo.getId(), true);
                    }


                    break;
                //处理群聊右侧按钮点击事件后逻辑
                case ActionTags.CHAT_GUOUP_RIGHT:
                    LogUtils.w("处理群聊右侧按钮点击事件后逻辑" + imActionDto.getJsonStr());//此时imActionDto.getJsonStr()为圈子ID
                    break;
                //群聊语音通话
                case ActionTags.GROUP_VOICE_CALL:
                    LogUtils.w("群聊语音通话");
                    break;
                //单聊聊语音通话
                case ActionTags.C2C_VOICE_CALL:
                    LogUtils.w("群聊语音通话");
                    break;
                //账号在其他端登陆
                case ActionTags.ON_FORCE_OFF_LIN:
                    LogUtils.w("账号在其他端登陆");
                    break;

                //群组新消息
                case ActionTags.ON_NEW_GROUP_MSG:
                    LogUtils.w("群组新消息");

                    //入圈信息通知
                case ActionTags.ACTION_CLICK_GROUP_TO_DO:


                    LogUtils.w("入圈信息通知" + imActionDto.getJsonStr());
                    break;

            }


        }

    }
}
