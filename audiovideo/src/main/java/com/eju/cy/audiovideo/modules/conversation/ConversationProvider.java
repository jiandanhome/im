package com.eju.cy.audiovideo.modules.conversation;


import com.eju.cy.audiovideo.controller.C2CController;
import com.eju.cy.audiovideo.controller.ImCallBack;
import com.eju.cy.audiovideo.modules.conversation.base.ConversationInfo;
import com.eju.cy.audiovideo.modules.conversation.interfaces.IConversationAdapter;
import com.eju.cy.audiovideo.modules.conversation.interfaces.IConversationProvider;
import com.eju.cy.audiovideo.tags.AppConfig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ConversationProvider implements IConversationProvider {

    private ArrayList<ConversationInfo> mDataSource = new ArrayList();
    private ConversationListAdapter mAdapter;


    @Override
    public List<ConversationInfo> getDataSource() {

        if (null == mDataSource) {
            return new ArrayList<ConversationInfo>();
        }
        return mDataSource;
    }


    /**
     * 设置会话数据源
     *
     * @param dataSource
     */
    public void setDataSource(final List<ConversationInfo> dataSource) {


        this.mDataSource.clear();


        final List<ConversationInfo> mCardList = C2CController.getInstance().getCustomCard();


        if (null != dataSource && dataSource.size() > 0) {
            Iterator<ConversationInfo> it = dataSource.iterator();

            while (it.hasNext()) {
                ConversationInfo conversationInfo = it.next();

                if (ConversationInfo.TYPE_APP_CUSTOM == conversationInfo.getType()) {
                    it.remove();
                }


            }
            //添加 自定义卡片类型数据
        }


        //操作跟新美房客服卡片数据
        if (null != mCardList && mCardList.size() > 0) {
            C2CController.getInstance().getConversation(C2CController.getInstance().getServiceID(), new ImCallBack() {
                @Override
                public void onError(int var1, String var2) {

                    //好友（人脉）不添加自定义卡片
                    if (AppConfig.appType == 0 && ConversationManagerKit.getInstance().getIsfriends()) {
                        getDataSource().addAll(dataSource);
                        updateAdapter();

                    } else {
                        //非好友（消息）添加自定义卡片
                        getDataSource().addAll(mCardList);
                        getDataSource().addAll(dataSource);
                        updateAdapter();
                    }


                }

                @Override
                public void onSuccess(Object var1) {

                    ConversationInfo conversationInfo = (ConversationInfo) var1;
                    if (null != conversationInfo) {
                        if (null != mCardList && mCardList.size() > 0) {

                            for (int i = 0; i < mCardList.size(); i++) {
                                //是美房
                                if (mCardList.get(i).getId().equals(conversationInfo.getId())) {


                                    ConversationInfo customerService = new ConversationInfo();
                                    customerService.setTitle(mCardList.get(i).getTitle());
                                    customerService.setId(mCardList.get(i).getId());

                                    customerService.setLastMessage(conversationInfo.getLastMessage());
                                    customerService.setLastMessageTime(conversationInfo.getLastMessageTime());
                                    customerService.setType(mCardList.get(i).getType());
                                    customerService.setIconUrlList(mCardList.get(i).getIconUrlList());
                                    customerService.setUnRead(conversationInfo.getUnRead());
                                    mCardList.set(i, customerService);

                                    // C2CController.getInstance().setCustomCard(mCardList);


                                }

                            }
                            //好友（人脉）不添加自定义卡片
                            if (AppConfig.appType == 0 && ConversationManagerKit.getInstance().getIsfriends()) {

                                getDataSource().addAll(dataSource);
                                updateAdapter();
                            } else {
                                //非好友（消息）添加自定义卡片
                                getDataSource().addAll(mCardList);
                                getDataSource().addAll(dataSource);
                                updateAdapter();
                            }


                        }


                    }


                }
            });
            //没有小美
        } else {
            this.mDataSource.addAll(dataSource);
            updateAdapter();
        }


        //


        // updateAdapter();
    }


    /**
     * 批量添加会话数据
     *
     * @param conversations 会话数据集合
     * @return
     */
    @Override
    public boolean addConversations(List<ConversationInfo> conversations) {
        if (conversations.size() == 1) {
            ConversationInfo conversation = conversations.get(0);
            for (int i = 0; i < mDataSource.size(); i++) {
                if (mDataSource.get(i).getId().equals(conversation.getId()))
                    return true;
            }
        }
        boolean flag = mDataSource.addAll(conversations);
        if (flag) {
            updateAdapter();
        }
        return flag;
    }

    /**
     * 批量删除会话数据
     *
     * @param conversations 会话数据集合
     * @return
     */
    @Override
    public boolean deleteConversations(List<ConversationInfo> conversations) {
        List<Integer> removeIndexs = new ArrayList();
        for (int i = 0; i < mDataSource.size(); i++) {
            for (int j = 0; j < conversations.size(); j++) {
                if (mDataSource.get(i).getId().equals(conversations.get(j).getId())) {
                    removeIndexs.add(i);
                    conversations.remove(j);
                    break;
                }
            }

        }
        if (removeIndexs.size() > 0) {
            for (int i = 0; i < removeIndexs.size(); i++) {
                mDataSource.remove(removeIndexs.get(i));
            }
            updateAdapter();
            return true;
        }
        return false;
    }

    /**
     * 删除单个会话数据
     *
     * @param index 会话在数据源集合的索引
     * @return
     */
    public void deleteConversation(int index) {
        if (mDataSource.remove(index) != null) {
            updateAdapter();
        }

    }

    /**
     * 删除单个会话数据
     *
     * @param id 会话ID
     * @return
     */
    public void deleteConversation(String id) {
        for (int i = 0; i < mDataSource.size(); i++) {
            if (mDataSource.get(i).getId().equals(id)) {
                if (mDataSource.remove(i) != null) {
                    updateAdapter();
                }
                return;
            }
        }
    }

    /**
     * 批量更新会话
     *
     * @param conversations 会话数据集合
     * @return
     */
    @Override
    public boolean updateConversations(List<ConversationInfo> conversations) {
        boolean flag = false;
        for (int i = 0; i < mDataSource.size(); i++) {
            for (int j = 0; j < conversations.size(); j++) {
                ConversationInfo update = conversations.get(j);
                if (mDataSource.get(i).getId().equals(update.getId())) {
                    mDataSource.remove(i);
                    mDataSource.add(i, update);
                    conversations.remove(j);
                    flag = true;
                    break;
                }
            }

        }
        if (flag) {
            updateAdapter();
            return true;
        } else {
            return false;
        }

    }

    /**
     * 清空会话
     */
    public void clear() {
        mDataSource.clear();
        updateAdapter();
        mAdapter = null;
    }

    /**
     * 会话会话列界面，在数据源更新的地方调用
     */
    public void updateAdapter() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }


    /**
     * 会话列表适配器绑定数据源是的回调
     *
     * @param adapter 会话UI显示适配器
     */
    @Override
    public void attachAdapter(IConversationAdapter adapter) {
        this.mAdapter = (ConversationListAdapter) adapter;
    }
}
