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
                "  \"Action12Text\" : \"@?????????\",\n" +
                "  \"idAndName\" : \"{\\n  \\\"10983\\\" : \\\"?????????\\\"\\n}\"\n" +
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






//        //????????????json?????????
//        String jsonString = "{'name':'?????????','age':24,'Position':'Mid'}";
//
//        //??????json???????????????????????????????????????JSONObject??????
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
//                LogUtils.w("101904???????????????--"+timUserProfiles.get(0).toString());
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

        //?????????ChatActivity?????????????????????
        // EjuImController.getInstance().setChatAtRightTitle("C2C");
        EjuImController.getInstance().setChatAtGroupRightTitle("??????");
        EjuImController.getInstance().setChatAtRightIcon(R.drawable.ic_back);


        LogUtils.w("loadC2CUnreadCount--" + C2CController.getInstance().loadC2cAndFriendsUnreadCount());


//        GroupController.getInstance().setNotification("888888", "?????????????????????????????????" + "????????????????????????????????????" + "????????????????????????????????????" + "?????????????????????????????????", new ImCallBack() {
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


        //  GroupController.getInstance().setGroupNotification("33333", "????????????????????????????????????????????????1???TextView??????????????????????????????????????????TextView????????????????????????????????????????????????????????????????????????TextView??????????????????????????????");


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
        FileUtil.initPath(); // ???application??????????????????????????????????????????app????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

        // ?????????????????????
        ConversationManagerKit.getInstance().addUnreadWatcher(this);
        GroupChatManagerKit.getInstance();
        if (mLastTab == null) {
            mLastTab = findViewById(R.id.conversation_btn_group);
        } else {
            // ???????????????????????????tab?????????????????????
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

                //????????????
                case ActionTags.ACTION_SEND_BUSINESS_CARD:
//                    CustomContentDto contentDto = new CustomContentDto();
//                    contentDto.setCardName("?????????");
//                    contentDto.setPhoneNumber("15601891066");
//                    contentDto.setPosition("????????????");
//                    contentDto.setPortraitUrl("");
//                    contentDto.setShopAddress("???????????????");
//                    contentDto.setShopName("????????????");
//
//                    contentDto.setPortraitUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=15880&di=ac74ee30d71eadfcb6b1985dd2ca322b&imgtype=0&src=http%3A%2F%2Fbpic.588ku.com%2Felement_origin_min_pic%2F00%2F10%2F74%2F3856a4ae2679e6c.jpg");
//                    contentDto.setSlogan("???????????????\n???????????????");
//
//
//                    CustomMsgController.getInstance().sendBusinessCard(contentDto, "1");


                    CustomContentDto contentDto = new CustomContentDto();
                    contentDto.setTitle("??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
                   // contentDto.setContentUrl("");
                     contentDto.setContentUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1588764928820&di=ac74ee30d71eadfcb6b1985dd2ca322b&imgtype=0&src=http%3A%2F%2Fbpic.588ku.com%2Felement_origin_min_pic%2F00%2F10%2F74%2F3856a4ae2679e6c.jpg");
                    CustomMsgController.getInstance().sendArticle("10211425", TIMConversationType.C2C, contentDto, "????????????????????????????????????????????????????????????", new ImCallBack() {
                        @Override
                        public void onError(int var1, String var2) {

                        }

                        @Override
                        public void onSuccess(Object var1) {

                        }
                    });


                    break;
                //????????????
                case ActionTags.ACTION_SEND_HOUSING:
                    CustomContentDto houingDto = new CustomContentDto();
                    houingDto.setTitle("??????????????? 2???2??? 990??? ????????????");
                    houingDto.setHouseUrl("https://img-test.jiandanhome.com/webget/1906/21/75bd6e6893e011e98b09fa163edde52c.jpg");


                    CustomMsgController.getInstance().sendHousing(houingDto, "2");
                    break;

                //?????????????????????????????????????????????
                case ActionTags.CHAT_C2C_RIGHT:
                    LogUtils.w("?????????????????????????????????????????????" + imActionDto.getJsonStr());//??????imActionDto.getJsonStr()?????????ID?????????ID???

                    ChatInfo chatInfo = JsonUtils.fromJson(imActionDto.getJsonStr(), ChatInfo.class);
                    ConversationInfo conversationInfo = JsonUtils.fromJson(chatInfo.getConversationInfoStr(), ConversationInfo.class);


                    LogUtils.w("setConversationTop---" + conversationInfo.isTop());


                    if (conversationInfo.isTop()) {
                        C2CController.getInstance().setConversationTop(chatInfo.getId(), false);
                    } else {
                        C2CController.getInstance().setConversationTop(chatInfo.getId(), true);
                    }


                    break;
                //?????????????????????????????????????????????
                case ActionTags.CHAT_GUOUP_RIGHT:
                    LogUtils.w("?????????????????????????????????????????????" + imActionDto.getJsonStr());//??????imActionDto.getJsonStr()?????????ID
                    break;
                //??????????????????
                case ActionTags.GROUP_VOICE_CALL:
                    LogUtils.w("??????????????????");
                    break;
                //?????????????????????
                case ActionTags.C2C_VOICE_CALL:
                    LogUtils.w("??????????????????");
                    break;
                //????????????????????????
                case ActionTags.ON_FORCE_OFF_LIN:
                    LogUtils.w("????????????????????????");
                    break;

                //???????????????
                case ActionTags.ON_NEW_GROUP_MSG:
                    LogUtils.w("???????????????");

                    //??????????????????
                case ActionTags.ACTION_CLICK_GROUP_TO_DO:


                    LogUtils.w("??????????????????" + imActionDto.getJsonStr());
                    break;

            }


        }

    }
}
