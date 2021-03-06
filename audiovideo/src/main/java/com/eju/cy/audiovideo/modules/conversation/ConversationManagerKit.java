package com.eju.cy.audiovideo.modules.conversation;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.TUIKit;
import com.eju.cy.audiovideo.base.IUIKitCallBack;
import com.eju.cy.audiovideo.controller.ConversationController;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.modules.conversation.base.ConversationInfo;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.eju.cy.audiovideo.modules.message.MessageInfoUtil;
import com.eju.cy.audiovideo.modules.message.MessageRevokedManager;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.tags.AppConfig;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.eju.cy.audiovideo.utils.SharedPreferenceUtils;
import com.eju.cy.audiovideo.utils.TUIKitLog;
import com.eju.cy.audiovideo.utils.ToastUtil;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMGroupSystemElem;
import com.tencent.imsdk.TIMGroupSystemElemType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMRefreshListener;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.group.TIMGroupDetailInfo;
import com.tencent.imsdk.ext.group.TIMGroupDetailInfoResult;
import com.tencent.imsdk.ext.group.TIMGroupMemberRoleFilter;
import com.tencent.imsdk.ext.group.TIMGroupMemberSucc;
import com.tencent.imsdk.ext.message.TIMMessageLocator;
import com.tencent.imsdk.friendship.TIMFriend;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ConversationManagerKit implements TIMRefreshListener, MessageRevokedManager.MessageRevokeHandler {

    private final static String TAG = ConversationManagerKit.class.getSimpleName();
    private final static String SP_NAME = "top_conversion_list";
    private final static String TOP_LIST = "top_list";
    private final String SP_IMAGE = "conversation_group_face";

    private static ConversationManagerKit instance = new ConversationManagerKit();

    private ConversationProvider mProvider;
    private List<MessageUnreadWatcher> mUnreadWatchers = new ArrayList<>();
    private SharedPreferences mConversationPreferences;
    private LinkedList<ConversationInfo> mTopLinkedList = new LinkedList<>();

    private boolean isfriends = true;//????????????????????????????????????????????????
    private int mUnreadTotal;

    private String appServiceId = "1053816";//??????????????? ??????

    private ConversationManagerKit() {
        init();
    }

    public static ConversationManagerKit getInstance() {
        return instance;
    }

    private void init() {
        TUIKitLog.i(TAG, "init");
        MessageRevokedManager.getInstance().addHandler(this);
        if (AppConfig.sdkAppId == 1400324570) {
            appServiceId = "1053816";
        } else {
            appServiceId = "10954";
        }
    }

    /**
     * ??????????????????
     *
     * @param callBack
     */
    public void loadConversation(IUIKitCallBack callBack) {
        TUIKitLog.i(TAG, "loadConversation callBack:" + callBack);
        mConversationPreferences = TUIKit.getAppContext().getSharedPreferences(TIMManager.getInstance().getLoginUser() + SP_NAME, Context.MODE_PRIVATE);
        mTopLinkedList = SharedPreferenceUtils.getListData(mConversationPreferences, TOP_LIST, ConversationInfo.class);
        mUnreadTotal = 0;
        //mProvider???????????????null,???????????????????????????????????????????????????????????????
        if (mProvider == null) {
            mProvider = new ConversationProvider();
        }
        List<TIMConversation> TIMConversations = TIMManager.getInstance().getConversationList();

        ArrayList<ConversationInfo> infos = new ArrayList<>();
        for (int i = 0; i < TIMConversations.size(); i++) {
            TIMConversation conversation = TIMConversations.get(i);
            //???imsdk TIMConversation?????????UIKit ConversationInfo
            ConversationInfo conversationInfo = TIMConversation2ConversationInfo(conversation);
            if (conversationInfo != null) {
                mUnreadTotal = mUnreadTotal + conversationInfo.getUnRead();
                conversationInfo.setType(ConversationInfo.TYPE_COMMON); //
                infos.add(conversationInfo);
            }
        }
        //?????????imsdk???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????imsdk????????????????????????
        mProvider.setDataSource(sortConversations(infos, "1"));
        SharedPreferenceUtils.putListData(mConversationPreferences, TOP_LIST, mTopLinkedList);
        //????????????????????????
        updateUnreadTotal(mUnreadTotal);
        if (callBack != null) {
            callBack.onSuccess(mProvider);
        }
    }

    /**
     * ??????C2C????????????
     *
     * @param callBack
     */
    public void loadC2CConversation(IUIKitCallBack callBack) {

        TUIKitLog.i(TAG, "loadConversation callBack:" + callBack);
        mConversationPreferences = TUIKit.getAppContext().getSharedPreferences(TIMManager.getInstance().getLoginUser() + SP_NAME, Context.MODE_PRIVATE);
        mTopLinkedList = SharedPreferenceUtils.getListData(mConversationPreferences, TOP_LIST, ConversationInfo.class);
        mUnreadTotal = 0;
        //mProvider???????????????null,???????????????????????????????????????????????????????????????
        if (mProvider == null) {
            mProvider = new ConversationProvider();
        }
        List<TIMConversation> TIMConversations = TIMManager.getInstance().getConversationList();

        ArrayList<ConversationInfo> infos = new ArrayList<>();
        for (int i = 0; i < TIMConversations.size(); i++) {


            TIMConversation conversation = TIMConversations.get(i);
            // LogUtils.w("??????ID---" + conversation.getPeer());
            //??????????????????????????????????????????
            if (conversation.getType() == TIMConversationType.C2C && !conversation.getPeer().equals(appServiceId)) {


                //???imsdk TIMConversation?????????UIKit ConversationInfo
                ConversationInfo conversationInfo = TIMConversation2ConversationInfo(conversation);
                if (conversationInfo != null) {
                    mUnreadTotal = mUnreadTotal + conversationInfo.getUnRead();
                    conversationInfo.setType(ConversationInfo.TYPE_COMMON); //


                    infos.add(conversationInfo);
                }
            }
        }

        LogUtils.w("infos----" + infos.size());
        //?????????imsdk???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????imsdk????????????????????????
        mProvider.setDataSource(sortConversations(infos, "2"));
        SharedPreferenceUtils.putListData(mConversationPreferences, TOP_LIST, mTopLinkedList);
        //????????????????????????
        updateUnreadTotal(mUnreadTotal);
        if (callBack != null) {
            callBack.onSuccess(mProvider);
        }
    }


    /**
     * ???????????????????????????????????????
     *
     * @param callBack
     */
    public void loadC2cAndFriendsConversation(IUIKitCallBack callBack) {

        this.isfriends = true;
        TUIKitLog.i(TAG, "loadConversation callBack:" + callBack);
        mConversationPreferences = TUIKit.getAppContext().getSharedPreferences(TIMManager.getInstance().getLoginUser() + SP_NAME, Context.MODE_PRIVATE);
        mTopLinkedList = SharedPreferenceUtils.getListData(mConversationPreferences, TOP_LIST, ConversationInfo.class);
        mUnreadTotal = 0;
        //mProvider???????????????null,???????????????????????????????????????????????????????????????
        if (mProvider == null) {
            mProvider = new ConversationProvider();
        }
        List<TIMConversation> TIMConversations = TIMManager.getInstance().getConversationList();

        ArrayList<ConversationInfo> infos = new ArrayList<>();
        for (int i = 0; i < TIMConversations.size(); i++) {


            TIMConversation conversation = TIMConversations.get(i);
            // LogUtils.w("??????ID---" + conversation.getPeer());
            //??????????????????????????????????????????
            if (conversation.getType() == TIMConversationType.C2C && !conversation.getPeer().equals(appServiceId)) {


                //???imsdk TIMConversation?????????UIKit ConversationInfo
                ConversationInfo conversationInfo = TIMConversation2ConversationInfo(conversation);
                if (conversationInfo != null) {


                    List<String> friendsList = ConversationController.getInstance().getFriendsList();

                    if (null != friendsList && friendsList.size() > 0) {
                        for (String friendId : friendsList) {

                            if (conversationInfo.getId().equals(friendId)) {
                                mUnreadTotal = mUnreadTotal + conversationInfo.getUnRead();
                                conversationInfo.setType(ConversationInfo.TYPE_COMMON);
                                infos.add(conversationInfo);
                            }
                        }
                    }

                }
            }
        }

        //?????????imsdk???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????imsdk????????????????????????


        LogUtils.w("infos----" + infos.size() + "\nmUnreadTotal---" +
                mUnreadTotal);
        mProvider.setDataSource(sortConversations(infos, "3"));
        SharedPreferenceUtils.putListData(mConversationPreferences, TOP_LIST, mTopLinkedList);

        //????????????????????????
        ImActionDto imActionDto = new ImActionDto();
        imActionDto.setAction(ActionTags.PUST_FRIENDS_UNREAD_COUNT);
        imActionDto.setJsonStr("" + mUnreadTotal);

        String str = JsonUtils.toJson(imActionDto);
        EjuHomeImEventCar.getDefault().post(str);
        if (callBack != null) {
            callBack.onSuccess(mProvider);
        }


    }


    /**
     * ??????????????????????????????????????????
     *
     * @param callBack
     */
    public void loadC2cAndNonFriendsConversation(IUIKitCallBack callBack) {


        this.isfriends = false;

        TUIKitLog.i(TAG, "loadConversation callBack:" + callBack);
        mConversationPreferences = TUIKit.getAppContext().getSharedPreferences(TIMManager.getInstance().getLoginUser() + SP_NAME, Context.MODE_PRIVATE);
        mTopLinkedList = SharedPreferenceUtils.getListData(mConversationPreferences, TOP_LIST, ConversationInfo.class);
        mUnreadTotal = 0;
        //mProvider???????????????null,???????????????????????????????????????????????????????????????
        if (mProvider == null) {
            mProvider = new ConversationProvider();
        }
        List<TIMConversation> TIMConversations = TIMManager.getInstance().getConversationList();

        ArrayList<ConversationInfo> infos = new ArrayList<>();
        for (int i = 0; i < TIMConversations.size(); i++) {


            TIMConversation conversation = TIMConversations.get(i);
            // LogUtils.w("??????ID---" + conversation.getPeer());
            //??????????????????????????????????????????
            if (conversation.getType() == TIMConversationType.C2C && !conversation.getPeer().equals(appServiceId)) {


                //???imsdk TIMConversation?????????UIKit ConversationInfo
                ConversationInfo conversationInfo = TIMConversation2ConversationInfo(conversation);
                if (conversationInfo != null) {

                    mUnreadTotal = mUnreadTotal + conversationInfo.getUnRead();
                    conversationInfo.setType(ConversationInfo.TYPE_COMMON); //


                    infos.add(conversationInfo);


                }
            }
        }


        //??????
        //????????????
        List<String> friendsList = ConversationController.getInstance().getFriendsList();
        Iterator<ConversationInfo> it = infos.iterator();

        while (it.hasNext()) {

            ConversationInfo conversationInfo = it.next();

            if (null != friendsList && friendsList.size() > 0) {
                if (friendsList.contains(conversationInfo.getId())) {
                    it.remove();

                    mUnreadTotal = mUnreadTotal - conversationInfo.getUnRead();

                }

            }
        }


        LogUtils.w("infos----" + infos.size() + "\nmUnreadTotal---" +
                mUnreadTotal);
        //?????????imsdk???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????imsdk????????????????????????
        mProvider.setDataSource(sortConversations(infos, "4"));
        SharedPreferenceUtils.putListData(mConversationPreferences, TOP_LIST, mTopLinkedList);


        //????????????????????????
        ImActionDto imActionDto = new ImActionDto();
        imActionDto.setAction(ActionTags.PUST_NON_FRIENDS_UNREAD_COUNT);
        imActionDto.setJsonStr("" + mUnreadTotal);

        String str = JsonUtils.toJson(imActionDto);
        EjuHomeImEventCar.getDefault().post(str);


        if (callBack != null) {
            callBack.onSuccess(mProvider);
        }


    }


    /**
     * ??????????????????????????????????????????
     *
     * @return
     */
    public int loadC2cAndFriendsUnreadCount() {


        mConversationPreferences = TUIKit.getAppContext().getSharedPreferences(TIMManager.getInstance().getLoginUser() + SP_NAME, Context.MODE_PRIVATE);
        mTopLinkedList = SharedPreferenceUtils.getListData(mConversationPreferences, TOP_LIST, ConversationInfo.class);
        mUnreadTotal = 0;
        //mProvider???????????????null,???????????????????????????????????????????????????????????????
        if (mProvider == null) {
            mProvider = new ConversationProvider();
        }
        List<TIMConversation> TIMConversations = TIMManager.getInstance().getConversationList();

        ArrayList<ConversationInfo> infos = new ArrayList<>();
        for (int i = 0; i < TIMConversations.size(); i++) {


            TIMConversation conversation = TIMConversations.get(i);
            // LogUtils.w("??????ID---" + conversation.getPeer());
            //??????????????????????????????????????????
            if (conversation.getType() == TIMConversationType.C2C && !conversation.getPeer().equals(appServiceId)) {


                //???imsdk TIMConversation?????????UIKit ConversationInfo
                ConversationInfo conversationInfo = TIMConversation2ConversationInfo(conversation);
                if (conversationInfo != null) {


                    List<String> friendsList = ConversationController.getInstance().getFriendsList();

                    if (null != friendsList && friendsList.size() > 0) {
                        for (String friendId : friendsList) {

                            if (conversationInfo.getId().equals(friendId)) {
                                mUnreadTotal = mUnreadTotal + conversationInfo.getUnRead();
                                conversationInfo.setType(ConversationInfo.TYPE_COMMON);
                                infos.add(conversationInfo);
                            }
                        }
                    }

                }
            }
        }


        //????????????????????????
        ImActionDto imActionDto = new ImActionDto();
        imActionDto.setAction(ActionTags.PUST_FRIENDS_UNREAD_COUNT);
        imActionDto.setJsonStr("" + mUnreadTotal);

        String str = JsonUtils.toJson(imActionDto);
        EjuHomeImEventCar.getDefault().post(str);

        return mUnreadTotal;

    }


    /**
     * ?????????????????????????????????????????????
     *
     * @return
     */
    public int loadC2cAndNonFriendsUnreadCount() {


        mConversationPreferences = TUIKit.getAppContext().getSharedPreferences(TIMManager.getInstance().getLoginUser() + SP_NAME, Context.MODE_PRIVATE);
        mTopLinkedList = SharedPreferenceUtils.getListData(mConversationPreferences, TOP_LIST, ConversationInfo.class);
        mUnreadTotal = 0;
        //mProvider???????????????null,???????????????????????????????????????????????????????????????
        if (mProvider == null) {
            mProvider = new ConversationProvider();
        }
        List<TIMConversation> TIMConversations = TIMManager.getInstance().getConversationList();

        ArrayList<ConversationInfo> infos = new ArrayList<>();
        for (int i = 0; i < TIMConversations.size(); i++) {


            TIMConversation conversation = TIMConversations.get(i);
            // LogUtils.w("??????ID---" + conversation.getPeer());
            //??????????????????????????????????????????
            if (conversation.getType() == TIMConversationType.C2C && !conversation.getPeer().equals(appServiceId)) {


                //???imsdk TIMConversation?????????UIKit ConversationInfo
                ConversationInfo conversationInfo = TIMConversation2ConversationInfo(conversation);
                if (conversationInfo != null) {

                    mUnreadTotal = mUnreadTotal + conversationInfo.getUnRead();
                    conversationInfo.setType(ConversationInfo.TYPE_COMMON);
                    infos.add(conversationInfo);

                }
            }
        }


        //??????
        //????????????
        List<String> friendsList = ConversationController.getInstance().getFriendsList();
        Iterator<ConversationInfo> it = infos.iterator();
        //????????????
        while (it.hasNext()) {

            ConversationInfo conversationInfo = it.next();

            if (null != friendsList && friendsList.size() > 0) {
                if (friendsList.contains(conversationInfo.getId())) {
                    it.remove();

                    mUnreadTotal = mUnreadTotal - conversationInfo.getUnRead();

                }

            }
        }


        //????????????????????????
        ImActionDto imActionDto = new ImActionDto();
        imActionDto.setAction(ActionTags.PUST_NON_FRIENDS_UNREAD_COUNT);
        imActionDto.setJsonStr("" + mUnreadTotal);

        String str = JsonUtils.toJson(imActionDto);
        EjuHomeImEventCar.getDefault().post(str);
        return mUnreadTotal;

    }


    /**
     * ????????????????????????
     *
     * @return
     */
    public int loadC2CUnreadCount() {


        mConversationPreferences = TUIKit.getAppContext().getSharedPreferences(TIMManager.getInstance().getLoginUser() + SP_NAME, Context.MODE_PRIVATE);
        mTopLinkedList = SharedPreferenceUtils.getListData(mConversationPreferences, TOP_LIST, ConversationInfo.class);
        mUnreadTotal = 0;
        //mProvider???????????????null,???????????????????????????????????????????????????????????????
        if (mProvider == null) {
            mProvider = new ConversationProvider();
        }
        List<TIMConversation> TIMConversations = TIMManager.getInstance().getConversationList();

        ArrayList<ConversationInfo> infos = new ArrayList<>();
        for (int i = 0; i < TIMConversations.size(); i++) {


            TIMConversation conversation = TIMConversations.get(i);
            // LogUtils.w("??????ID---" + conversation.getPeer());
            //??????????????????????????????????????????
            if (conversation.getType() == TIMConversationType.C2C && !conversation.getPeer().equals(appServiceId)) {


                //???imsdk TIMConversation?????????UIKit ConversationInfo
                ConversationInfo conversationInfo = TIMConversation2ConversationInfo(conversation);
                if (conversationInfo != null) {
                    mUnreadTotal = mUnreadTotal + conversationInfo.getUnRead();
                    conversationInfo.setType(ConversationInfo.TYPE_COMMON); //


                    infos.add(conversationInfo);
                }
            }
        }


        //?????????imsdk???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????imsdk????????????????????????
//        mProvider.setDataSource(sortConversations(infos));
//        SharedPreferenceUtils.putListData(mConversationPreferences, TOP_LIST, mTopLinkedList);
        //????????????????????????
        //  updateUnreadTotal(mUnreadTotal);


        return mUnreadTotal;

    }


    public boolean getIsfriends() {

        return this.isfriends;

    }

    /**
     * ???????????????????????????????????????
     *
     * @param isfriends
     */
    public void setIsfriends(boolean isfriends) {
        this.isfriends = isfriends;
    }


    /**
     * ???????????????????????????????????????????????????????????????
     */
    @Override
    public void onRefresh() {

    }


    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param conversations ???????????????????????????
     */
    @Override
    public void onRefreshConversation(List<TIMConversation> conversations) {


        LogUtils.w("onRefreshConversation conversations:" + conversations);
        if (mProvider == null) {
            return;
        }
        ArrayList<ConversationInfo> infos = new ArrayList<>();
        for (int i = 0; i < conversations.size(); i++) {
            TIMConversation conversation = conversations.get(i);
            TUIKitLog.v(TAG, "onRefreshConversation TIMConversation " + conversation.toString());
            ConversationInfo conversationInfo = TIMConversation2ConversationInfo(conversation);
            if (conversation.getType() == TIMConversationType.System) {
                TIMMessage message = conversation.getLastMsg();
                if (message.getElementCount() > 0) {
                    TIMElem ele = message.getElement(0);
                    TIMElemType eleType = ele.getType();
                    if (eleType == TIMElemType.GroupSystem) {
                        TIMGroupSystemElem groupSysEle = (TIMGroupSystemElem) ele;
                        if (groupSysEle.getSubtype() == TIMGroupSystemElemType.TIM_GROUP_SYSTEM_INVITED_TO_GROUP_TYPE) {
                            String group = conversation.getGroupName();
                            if (TextUtils.isEmpty(group)) {
                                group = groupSysEle.getGroupId();
                            }
                            ToastUtil.toastLongMessage("????????????????????????????????????????????????????????????");



                        }
                    }
                }
            }
            if (conversationInfo != null) {

                //?????????????????????????????????
                //?????????????????????????????????
                //?????????????????????????????????
                if (conversation.getType() == TIMConversationType.C2C && !conversation.getPeer().equals(appServiceId)) {

                    conversationInfo.setType(ConversationInfo.TYPE_COMMON);
                    infos.add(conversationInfo);


//                    //???????????????????????????
//                    if (AppConfig.appType == 0) {
//
//                        List<String> friendsList = ConversationController.getInstance().getFriendsList();
//
//                        if (null != friendsList && friendsList.size() > 0) {
//                            for (String friends : friendsList) {
//                                //???????????????????????????????????????????????????
//                                if (this.isfriends) {
//                                    if (conversation.getPeer().equals(friends)) {
//                                        conversationInfo.setType(ConversationInfo.TYPE_COMMON);
//                                        infos.add(conversationInfo);
//                                    }
//                                } else {
//
//                                    //?????????
//                                    if (!conversation.getPeer().equals(friends)) {
//                                        if (null != infos && infos.size() > 0) {
//
//                                            if (!infos.contains(conversationInfo)) {
//                                                conversationInfo.setType(ConversationInfo.TYPE_COMMON);
//                                                infos.add(conversationInfo);
//                                            }
//
//
//                                        } else {
//                                            conversationInfo.setType(ConversationInfo.TYPE_COMMON);
//                                            infos.add(conversationInfo);
//                                        }
//
//
//
//                                    }
//                                }
//
//
//                            }
//                        }
//
//
//                    } else {
//                        infos.add(conversationInfo);
//                    }


                }
            }
        }

        //?????????????????????
        if (AppConfig.appType == 0) {

            List<String> friendsList = ConversationController.getInstance().getFriendsList();
            Iterator<ConversationInfo> it = infos.iterator();
            //??????
            if (this.isfriends) {

                while (it.hasNext()) {
                    ConversationInfo conversationInfo = it.next();
                    if (null != friendsList && friendsList.size() > 0) {
                        if (!friendsList.contains(conversationInfo.getId())) {
                            it.remove();
                        }

                    }
                }


            } else {
                //?????????
                //??????
                //????????????

                //????????????
                while (it.hasNext()) {
                    ConversationInfo conversationInfo = it.next();
                    if (null != friendsList && friendsList.size() > 0) {
                        if (friendsList.contains(conversationInfo.getId())) {
                            it.remove();
                        }

                    }
                }


            }


        }


        if (infos.size() == 0) {
            return;
        }

        /*-------------------------------------*/
        List<ConversationInfo> dataSource = mProvider.getDataSource();
        ArrayList exists = new ArrayList();
        for (int j = 0; j < infos.size(); j++) {
            ConversationInfo update = infos.get(j);
            boolean exist = false;

            if (null != dataSource && dataSource.size() > 0) {
                for (int i = 0; i < dataSource.size(); i++) {
                    ConversationInfo cacheInfo = dataSource.get(i);
                    //???????????????????????????????????????????????????????????????????????????????????????????????????????????????id?????????id??????
                    if (cacheInfo.getId().equals(update.getId()) && cacheInfo.isGroup() == update.isGroup()) {


                        //?????????tag?????????
                        update.setUserRoleList(dataSource.get(i).getUserRoleList());

                        dataSource.remove(i);
                        dataSource.add(i, update);
                        exists.add(update);
                        //infos.remove(j);
                        //???????????????????????????
                        mUnreadTotal = mUnreadTotal - cacheInfo.getUnRead() + update.getUnRead();
                        TUIKitLog.v(TAG, "onRefreshConversation after mUnreadTotal = " + mUnreadTotal);
                        exist = true;
                        break;

                    }
                }
            }
            if (!exist) {
                mUnreadTotal += update.getUnRead();
                TUIKitLog.i(TAG, "onRefreshConversation exist = " + exist + ", mUnreadTotal = " + mUnreadTotal);
            }
        }

        //??????????????????
        updateUnreadTotal(mUnreadTotal);

        //??????????????????
        if (AppConfig.appType == 0) {
            loadC2cAndFriendsUnreadCount();
            loadC2cAndNonFriendsUnreadCount();
        }


        infos.removeAll(exists);
        if (infos.size() > 0) {
            dataSource.addAll(infos);
        }
        mProvider.setDataSource(sortConversations(dataSource, "5"));
        SharedPreferenceUtils.putListData(mConversationPreferences, TOP_LIST, mTopLinkedList);
    }

    /**
     * TIMConversation?????????ConversationInfo
     *
     * @param conversation
     * @return
     */
    private ConversationInfo TIMConversation2ConversationInfo(final TIMConversation conversation) {
        if (conversation == null) {
            return null;
        }
        TUIKitLog.i(TAG, "TIMConversation2ConversationInfo id:" + conversation.getPeer()
                + "|name:" + conversation.getGroupName()
                + "|unreadNum:" + conversation.getUnreadMessageNum());
        TIMMessage message = conversation.getLastMsg();
        if (message == null) {
            return null;
        }
        final ConversationInfo info = new ConversationInfo();
        TIMConversationType type = conversation.getType();
        if (type == TIMConversationType.System) {
            if (message.getElementCount() > 0) {
                TIMElem ele = message.getElement(0);
                TIMElemType eleType = ele.getType();
                if (eleType == TIMElemType.GroupSystem) {
                    TIMGroupSystemElem groupSysEle = (TIMGroupSystemElem) ele;
                    groupSystMsgHandle(groupSysEle);
                }
            }
            return null;
        }

        boolean isGroup = type == TIMConversationType.Group;
        info.setLastMessageTime(message.timestamp());
        List<MessageInfo> list = MessageInfoUtil.TIMMessage2MessageInfo(message, isGroup);
        if (list != null && list.size() > 0) {
            info.setLastMessage(list.get(list.size() - 1));
        }
        if (isGroup) {
            fillConversationWithGroupInfo(conversation, info);
        } else {
            fillConversationWithUserProfile(conversation, info);
        }
        info.setId(conversation.getPeer());
        info.setGroup(conversation.getType() == TIMConversationType.Group);
        info.setUnRead((int) conversation.getUnreadMessageNum());
        return info;
    }

    private void fillConversationWithUserProfile(final TIMConversation conversation, final ConversationInfo info) {
        String title = conversation.getPeer();
        final ArrayList<String> ids = new ArrayList<>();
        ids.add(conversation.getPeer());
        TIMUserProfile profile = TIMFriendshipManager.getInstance().queryUserProfile(conversation.getPeer());
        if (profile == null) {
            TIMFriendshipManager.getInstance().getUsersProfile(ids, false, new TIMValueCallBack<List<TIMUserProfile>>() {
                @Override
                public void onError(int code, String desc) {
                    TUIKitLog.e(TAG, "getUsersProfile failed! code: " + code + " desc: " + desc);
                }

                @Override
                public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                    if (timUserProfiles == null || timUserProfiles.size() != 1) {
                        TUIKitLog.i(TAG, "No TIMUserProfile");
                        return;
                    }
                    TIMUserProfile profile = timUserProfiles.get(0);
                    List<Object> face = new ArrayList<>();
                    if (profile != null && !TextUtils.isEmpty(profile.getFaceUrl())) {
                        face.add(profile.getFaceUrl());
                    } else {
                        face.add(R.drawable.default_head);
                    }
                    String title = conversation.getPeer();
                    if (profile != null && !TextUtils.isEmpty(profile.getNickName())) {
                        title = profile.getNickName();
                    }
                    info.setTitle(title);
                    info.setIconUrlList(face);
                    mProvider.updateAdapter();
                }
            });
        } else {
            List<Object> face = new ArrayList<>();
            if (!TextUtils.isEmpty(profile.getNickName())) {
                title = profile.getNickName();
            }
            if (TextUtils.isEmpty(profile.getFaceUrl())) {
                face.add(R.drawable.default_head);
            } else {
                face.add(profile.getFaceUrl());
            }
            info.setTitle(title);
            info.setIconUrlList(face);
        }

        TIMFriend friend = TIMFriendshipManager.getInstance().queryFriend(conversation.getPeer());
        if (friend == null) {
            TIMFriendshipManager.getInstance().getFriendList(new TIMValueCallBack<List<TIMFriend>>() {
                @Override
                public void onError(int code, String desc) {
                    TUIKitLog.e(TAG, "getFriendList failed! code: " + code + " desc: " + desc);
                }

                @Override
                public void onSuccess(List<TIMFriend> timFriends) {
                    if (timFriends == null || timFriends.size() == 0) {
                        TUIKitLog.i(TAG, "No Friends");
                        return;
                    }
                    for (TIMFriend friend : timFriends) {
                        if (!TextUtils.equals(conversation.getPeer(), friend.getIdentifier())) {
                            continue;
                        }
                        if (TextUtils.isEmpty(friend.getRemark())) {
                            continue;
                        }
                        info.setTitle(friend.getRemark());
                        mProvider.updateAdapter();
                        return;
                    }
                    TUIKitLog.i(TAG, conversation.getPeer() + " is not my friend");
                }
            });
        } else {
            if (!TextUtils.isEmpty(friend.getRemark())) {
                title = friend.getRemark();
                info.setTitle(title);
            }
        }
    }

    private void fillConversationWithGroupInfo(final TIMConversation conversation, final ConversationInfo info) {
        SharedPreferences sp = TUIKit.getAppContext().getSharedPreferences(
                TIMManager.getInstance().getLoginUser() + SP_IMAGE, Context.MODE_PRIVATE);
        final String savedIcon = sp.getString(conversation.getPeer(), "");
        if (!TextUtils.isEmpty(savedIcon) && new File(savedIcon).isFile() && new File(savedIcon).exists()) {
            List<Object> list = new ArrayList<>();
            list.add(savedIcon);
            info.setIconUrlList(list);
        }
        TIMGroupDetailInfo groupDetailInfo = TIMGroupManager.getInstance().queryGroupInfo(conversation.getPeer());
        if (groupDetailInfo == null) {
            if (TextUtils.isEmpty(conversation.getGroupName())) {
                info.setTitle(conversation.getPeer());
            } else {
                info.setTitle(conversation.getGroupName());
            }
            final ArrayList<String> ids = new ArrayList<>();
            ids.add(conversation.getPeer());
            TIMGroupManager.getInstance().getGroupInfo(ids, new TIMValueCallBack<List<TIMGroupDetailInfoResult>>() {
                @Override
                public void onError(int code, String desc) {
                    TUIKitLog.e(TAG, "getGroupInfo failed! code: " + code + " desc: " + desc);
                }

                @Override
                public void onSuccess(List<TIMGroupDetailInfoResult> timGroupDetailInfoResults) {
                    if (timGroupDetailInfoResults == null || timGroupDetailInfoResults.size() != 1) {
                        TUIKitLog.i(TAG, "No GroupInfo");
                        return;
                    }
                    TIMGroupDetailInfoResult result = timGroupDetailInfoResults.get(0);
                    if (TextUtils.isEmpty(result.getGroupName())) {
                        info.setTitle(result.getGroupId());
                    } else {
                        info.setTitle(result.getGroupName());
                    }
                    if (TextUtils.isEmpty(result.getFaceUrl())) {
                        fillFaceUrlList(conversation.getPeer(), info);
                    } else {
                        List<Object> list = new ArrayList<>();
                        list.add(result.getFaceUrl());
                        info.setIconUrlList(list);
                    }
                    mProvider.updateAdapter();
                }
            });
        } else {
            if (TextUtils.isEmpty(groupDetailInfo.getFaceUrl())) {
                fillFaceUrlList(conversation.getPeer(), info);
            } else {
                List<Object> list = new ArrayList<>();
                list.add(groupDetailInfo.getFaceUrl());
                info.setIconUrlList(list);
            }
            if (TextUtils.isEmpty(groupDetailInfo.getGroupName())) {
                info.setTitle(groupDetailInfo.getGroupId());
            } else {
                info.setTitle(groupDetailInfo.getGroupName());
            }
        }
    }

    private void fillFaceUrlList(final String groupID, final ConversationInfo info) {
        TIMGroupManager.getInstance().getGroupMembersByFilter(groupID,
                TIMGroupManager.TIM_GET_GROUP_MEM_INFO_FLAG_NAME_CARD,
                TIMGroupMemberRoleFilter.All,
                null,
                0,
                new TIMValueCallBack<TIMGroupMemberSucc>() {
                    @Override
                    public void onError(int code, String desc) {
                        TUIKitLog.e(TAG, "getGroupMembersByFilter failed! code: " + code + " desc: " + desc);
                    }

                    @Override
                    public void onSuccess(TIMGroupMemberSucc timGroupMemberSucc) {
                        List<TIMGroupMemberInfo> timGroupMemberInfos = timGroupMemberSucc.getMemberInfoList();
                        int faceSize = timGroupMemberInfos.size() > 9 ? 9 : timGroupMemberInfos.size();
                        final List<Object> urlList = new ArrayList<>();
                        List<String> needGetFromNetworkList = new ArrayList<>();
                        for (int i = 0; i < faceSize; i++) {
                            TIMUserProfile profile = TIMFriendshipManager.getInstance().queryUserProfile(timGroupMemberInfos.get(i).getUser());
                            if (profile == null) {
                                needGetFromNetworkList.add(timGroupMemberInfos.get(i).getUser());
                            } else {
                                if (TextUtils.isEmpty(profile.getFaceUrl())) {
                                    urlList.add(R.drawable.default_head);
                                } else {
                                    urlList.add(profile.getFaceUrl());
                                }
                            }
                        }
                        if (urlList.size() == faceSize) {
                            info.setIconUrlList(urlList);
                            mProvider.updateAdapter();
                            return;
                        }
                        TIMFriendshipManager.getInstance().getUsersProfile(needGetFromNetworkList, false, new TIMValueCallBack<List<TIMUserProfile>>() {
                            @Override
                            public void onError(int code, String desc) {
                                TUIKitLog.e(TAG, "getUsersProfile failed! code: " + code + " desc: " + desc);
                            }

                            @Override
                            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                                if (timUserProfiles == null || timUserProfiles.size() == 0) {
                                    return;
                                }
                                for (TIMUserProfile profile : timUserProfiles) {
                                    if (TextUtils.isEmpty(profile.getFaceUrl())) {
                                        urlList.add(R.drawable.default_head);
                                    } else {
                                        urlList.add(profile.getFaceUrl());
                                    }
                                }
                                info.setIconUrlList(urlList);
                                mProvider.updateAdapter();
                            }
                        });
                    }
                });
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param groupSysEle
     */
    private void groupSystMsgHandle(TIMGroupSystemElem groupSysEle) {
        TIMGroupSystemElemType type = groupSysEle.getSubtype();
        //?????????????????????????????????
        if (type == TIMGroupSystemElemType.TIM_GROUP_SYSTEM_KICK_OFF_FROM_GROUP_TYPE
                || type == TIMGroupSystemElemType.TIM_GROUP_SYSTEM_DELETE_GROUP_TYPE) {
            //imsdk????????????????????????????????????????????????????????????????????????????????????
            deleteConversation(groupSysEle.getGroupId(), true);
        }
    }


    /**
     * ?????????????????????
     *
     * @param index
     * @param conversation
     */
    public void setConversationTop(int index, ConversationInfo conversation) {
        TUIKitLog.i(TAG, "setConversationTop index:" + index + "|conversation:" + conversation);
        if (!conversation.isTop()) {
            mTopLinkedList.remove(conversation);
            mTopLinkedList.addFirst(conversation);
            conversation.setTop(true);
            LogUtils.w("setConversationTop11111");
        } else {
            conversation.setTop(false);
            mTopLinkedList.remove(conversation);
            LogUtils.w("setConversationTop22222");
        }
        mProvider.setDataSource(sortConversations(mProvider.getDataSource(), "6"));
        SharedPreferenceUtils.putListData(mConversationPreferences, TOP_LIST, mTopLinkedList);
    }

    /**
     * ??????????????????
     *
     * @param id   ??????ID
     * @param flag ????????????
     */
    public void setConversationTop(String id, boolean flag) {
        TUIKitLog.i(TAG, "setConversationTop id:" + id + "|flag:" + flag);
        handleTopData(id, flag);
        mProvider.setDataSource(sortConversations(mProvider.getDataSource(), "7"));
        SharedPreferenceUtils.putListData(mConversationPreferences, TOP_LIST, mTopLinkedList);
    }

    private boolean isTop(String id) {
        if (mTopLinkedList == null || mTopLinkedList.size() == 0) {
            return false;
        }
        for (ConversationInfo info : mTopLinkedList) {
            if (TextUtils.equals(info.getId(), id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ?????????????????????????????????????????????SharePreferences????????????????????????
     *
     * @param id
     * @param flag
     */
    private void handleTopData(String id, boolean flag) {
        ConversationInfo conversation = null;

        if (null != mProvider && null != mProvider.getDataSource()) {
            List<ConversationInfo> conversationInfos = mProvider.getDataSource();

            if (null != conversationInfos && conversationInfos.size() > 0) {
                for (int i = 0; i < conversationInfos.size(); i++) {
                    ConversationInfo info = conversationInfos.get(i);
                    if (info.getId().equals(id)) {
                        conversation = info;
                        break;
                    }
                }
            }

            if (conversation == null) {
                return;
            }
            if (flag) {
                if (!isTop(conversation.getId())) {
                    mTopLinkedList.remove(conversation);
                    mTopLinkedList.addFirst(conversation);
                    conversation.setTop(true);
                } else {
                    return;
                }
            } else {
                if (isTop(conversation.getId())) {
                    conversation.setTop(false);
                    mTopLinkedList.remove(conversation);
                } else {
                    return;
                }
            }
            SharedPreferenceUtils.putListData(mConversationPreferences, TOP_LIST, mTopLinkedList);
        }
    }

    /**
     * ??????????????????????????????????????????imsdk?????????
     *
     * @param index        ????????????????????????
     * @param conversation ????????????
     */
    public void deleteConversation(int index, ConversationInfo conversation) {
        TUIKitLog.i(TAG, "deleteConversation index:" + index + "|conversation:" + conversation);
        boolean status = TIMManager.getInstance().deleteConversation(conversation.isGroup() ? TIMConversationType.Group : TIMConversationType.C2C, conversation.getId());
        if (status) {

            if (null != mProvider) {
                handleTopData(conversation.getId(), false);
                mProvider.deleteConversation(index);
                updateUnreadTotal(mUnreadTotal - conversation.getUnRead());
            }

        }
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param id ??????id
     */
    public void deleteConversation(String id, boolean isGroup) {
        if (null != mProvider) {
            TUIKitLog.i(TAG, "deleteConversation id:" + id + "|isGroup:" + isGroup);
            handleTopData(id, false);
            List<ConversationInfo> conversationInfos = mProvider.getDataSource();

            if (null != conversationInfos && conversationInfos.size() > 0) {
                for (int i = 0; i < conversationInfos.size(); i++) {
                    ConversationInfo info = conversationInfos.get(i);
                    if (info.getId().equals(id)) {
                        updateUnreadTotal(mUnreadTotal - info.getUnRead());
                        break;
                    }
                }
            }
            if (mProvider != null) {
                mProvider.deleteConversation(id);
            }
            TIMManager.getInstance().deleteConversation(isGroup ? TIMConversationType.Group : TIMConversationType.C2C, id);
        }
    }

    /**
     * ????????????
     *
     * @param conversationInfo
     * @return
     */
    public boolean addConversation(ConversationInfo conversationInfo) {
        List<ConversationInfo> conversationInfos = new ArrayList<>();
        conversationInfos.add(conversationInfo);
        return mProvider.addConversations(conversationInfos);
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param sources
     * @return
     */
    private List<ConversationInfo> sortConversations(List<ConversationInfo> sources, String type) {

        LogUtils.w("sortConversations" + sources.size() + "\ntype" + type);
        ArrayList<ConversationInfo> conversationInfos = new ArrayList<>();
        List<ConversationInfo> normalConversations = new ArrayList<>();
        List<ConversationInfo> topConversations = new ArrayList<>();

        for (int i = 0; i <= sources.size() - 1; i++) {
            ConversationInfo conversation = sources.get(i);
            if (isTop(conversation.getId())) {
                conversation.setTop(true);
                topConversations.add(conversation);
            } else {
                normalConversations.add(conversation);
            }
        }

        mTopLinkedList.clear();
        mTopLinkedList.addAll(topConversations);
        Collections.sort(topConversations); // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        conversationInfos.addAll(topConversations);
        Collections.sort(normalConversations); // ?????????????????????????????????????????????????????????????????????
        conversationInfos.addAll(normalConversations);
        return conversationInfos;
    }

    /**
     * ????????????????????????
     *
     * @param unreadTotal
     */
    public void updateUnreadTotal(int unreadTotal) {
        TUIKitLog.i(TAG, "updateUnreadTotal:" + unreadTotal);
        mUnreadTotal = unreadTotal;
        for (int i = 0; i < mUnreadWatchers.size(); i++) {
            mUnreadWatchers.get(i).updateUnread(mUnreadTotal);
        }
    }

    public boolean isTopConversation(String groupId) {
        TUIKitLog.i(TAG, "isTopConversation:" + groupId);
        return isTop(groupId);
    }

    /**
     * ??????????????????
     *
     * @param locator
     */
    @Override
    public void handleInvoke(TIMMessageLocator locator) {
        TUIKitLog.i(TAG, "handleInvoke:" + locator);
        if (mProvider != null) {
            // loadConversation(null);
            loadC2CConversation(null);

            loadC2cAndNonFriendsConversation(null);
            loadC2cAndFriendsConversation(null);

        }
    }


    /**
     * ???????????????????????????
     *
     * @param messageUnreadWatcher
     */
    public void addUnreadWatcher(MessageUnreadWatcher messageUnreadWatcher) {
        TUIKitLog.i(TAG, "addUnreadWatcher:" + messageUnreadWatcher);
        if (!mUnreadWatchers.contains(messageUnreadWatcher)) {
            mUnreadWatchers.add(messageUnreadWatcher);
        }
    }


    /**
     * ???UI????????????????????????????????????
     */
    public void destroyConversation() {
        TUIKitLog.i(TAG, "destroyConversation");
        if (mProvider != null) {
            mProvider.attachAdapter(null);
        }
        if (mUnreadWatchers != null) {
            mUnreadWatchers.clear();
        }
        mUnreadTotal = 0;
    }


    /**
     * ?????????????????????????????????
     */
    public interface MessageUnreadWatcher {
        void updateUnread(int count);
    }

}