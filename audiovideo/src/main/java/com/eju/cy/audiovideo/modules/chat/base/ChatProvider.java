package com.eju.cy.audiovideo.modules.chat.base;

import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.controller.EjuImController;
import com.eju.cy.audiovideo.modules.chat.interfaces.IChatProvider;
import com.eju.cy.audiovideo.modules.chat.layout.message.MessageLayout;
import com.eju.cy.audiovideo.modules.chat.layout.message.MessageListAdapter;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.tencent.imsdk.ext.message.TIMMessageLocator;
import com.tencent.imsdk.ext.message.TIMMessageReceipt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatProvider implements IChatProvider {

    private ArrayList<MessageInfo> mDataSource = new ArrayList();

    private MessageListAdapter mAdapter;
    private TypingListener mTypingListener;

    private Map<String, String> vMap = new HashMap<>();

    @Override
    public List<MessageInfo> getDataSource() {

        setUserGrader();
        return mDataSource;
    }

    /**
     * 设置黄V用户
     */
    private void setUserGrader() {
        vMap = EjuImController.getInstance().getvMap();
        if (null != vMap && vMap.size() > 0) {
            if (null != mDataSource && mDataSource.size() > 0) {


                for (MessageInfo msg : mDataSource) {

                    if (null != msg) {

                        //   if (msg.isGroup()) {
                        //群聊的


                        //  } else {
                        //单聊
                        if (!msg.isSelf()) {

                            if (null != vMap.get(msg.getFromUser()) && "1".equals(vMap.get(msg.getFromUser()))) {
                                msg.setGrade(true);
                            } else {
                                msg.setGrade(false);

                            }

                        }


                        //}
                    }


                }


            }

        }


    }

    @Override
    public boolean addMessageList(List<MessageInfo> msgs, boolean front) {
        List<MessageInfo> list = new ArrayList<>();
        for (MessageInfo info : msgs) {
            if (checkExist(info)) {
                continue;
            }
            list.add(info);
        }
        boolean flag;
        if (front) {
            flag = mDataSource.addAll(0, list);
            updateAdapter(MessageLayout.DATA_CHANGE_TYPE_ADD_FRONT, list.size());

        } else {
            flag = mDataSource.addAll(list);
            updateAdapter(MessageLayout.DATA_CHANGE_TYPE_ADD_BACK, list.size());
            LogUtils.w("更新消息---222222222222 ");
        }
        return flag;
    }

    private boolean checkExist(MessageInfo msg) {
        if (null != msg) {
            String msgId = msg.getId();
            for (int i = mDataSource.size() - 1; i >= 0; i--) {
                if (mDataSource.get(i).getId().equals(msgId)
                        && mDataSource.get(i).getUniqueId() == msg.getUniqueId()) {
                    if (null != mDataSource.get(i).getExtra() && null != msg.getExtra() && TextUtils.equals(mDataSource.get(i).getExtra().toString() + "", msg.getExtra().toString())) {
                        return true;
                    } else {
                        return false;
                    }

                }
            }
        }
        return false;
    }

    @Override
    public boolean deleteMessageList(List<MessageInfo> messages) {
        for (int i = 0; i < mDataSource.size(); i++) {
            for (int j = 0; j < messages.size(); j++) {
                if (mDataSource.get(i).getId().equals(messages.get(j).getId())) {
                    mDataSource.remove(i);
                    updateAdapter(MessageLayout.DATA_CHANGE_TYPE_DELETE, i);
                    break;
                }
            }
        }
        return false;
    }

    @Override
    public boolean updateMessageList(List<MessageInfo> messages) {
        return false;
    }

    public boolean addMessageInfoList(List<MessageInfo> msg) {
        if (msg == null || msg.size() == 0) {
            updateAdapter(MessageLayout.DATA_CHANGE_TYPE_LOAD, 0);
            return true;
        }
        List<MessageInfo> list = new ArrayList<>();
        for (MessageInfo info : msg) {
            if (checkExist(info)) {
                continue;
            }
            list.add(info);
        }
        boolean flag = mDataSource.addAll(list);
        setUserGrader();
        updateAdapter(MessageLayout.DATA_CHANGE_TYPE_ADD_BACK, list.size());
        LogUtils.w("更新消息---333333333333 ");
        return flag;

    }

    public boolean addMessageInfo(MessageInfo msg) {
        if (msg == null) {
            updateAdapter(MessageLayout.DATA_CHANGE_TYPE_LOAD, 0);
            return true;
        }
        if (checkExist(msg)) {
            return true;
        }
        boolean flag = mDataSource.add(msg);

        LogUtils.w("更新消息---1111111111 ");

        updateAdapter(MessageLayout.DATA_CHANGE_TYPE_ADD_BACK, 1);
        return flag;
    }

    public boolean deleteMessageInfo(MessageInfo msg) {
        for (int i = 0; i < mDataSource.size(); i++) {
            if (mDataSource.get(i).getId().equals(msg.getId())) {
                mDataSource.remove(i);
                updateAdapter(MessageLayout.DATA_CHANGE_TYPE_DELETE, -1);
                return true;
            }
        }
        return false;
    }

    public boolean resendMessageInfo(MessageInfo message) {
        boolean found = false;
        for (int i = 0; i < mDataSource.size(); i++) {
            if (mDataSource.get(i).getId().equals(message.getId())) {
                mDataSource.remove(i);
                found = true;
                break;
            }
        }
        if (!found) {
            return false;
        }
        return addMessageInfo(message);
    }

    public boolean updateMessageInfo(MessageInfo message) {
        for (int i = 0; i < mDataSource.size(); i++) {
            if (mDataSource.get(i).getId().equals(message.getId())) {
                mDataSource.remove(i);
                mDataSource.add(i, message);
                updateAdapter(MessageLayout.DATA_CHANGE_TYPE_UPDATE, i);
                return true;
            }
        }
        return false;
    }

    public boolean updateMessageRevoked(TIMMessageLocator locator) {
        for (int i = 0; i < mDataSource.size(); i++) {
            MessageInfo messageInfo = mDataSource.get(i);
            // 一条包含多条元素的消息，撤回时，会把所有元素都撤回，所以下面的判断即使满足条件也不能return
            if (messageInfo.checkEquals(locator)) {
                messageInfo.setMsgType(MessageInfo.MSG_STATUS_REVOKE);
                messageInfo.setStatus(MessageInfo.MSG_STATUS_REVOKE);
                updateAdapter(MessageLayout.DATA_CHANGE_TYPE_UPDATE, i);
            }
        }
        return false;
    }

    public boolean updateMessageRevoked(String msgId) {
        for (int i = 0; i < mDataSource.size(); i++) {
            MessageInfo messageInfo = mDataSource.get(i);
            // 一条包含多条元素的消息，撤回时，会把所有元素都撤回，所以下面的判断即使满足条件也不能return
            if (messageInfo.getId().equals(msgId)) {
                messageInfo.setMsgType(MessageInfo.MSG_STATUS_REVOKE);
                messageInfo.setStatus(MessageInfo.MSG_STATUS_REVOKE);
                updateAdapter(MessageLayout.DATA_CHANGE_TYPE_UPDATE, i);
            }
        }
        return false;
    }

    public void updateReadMessage(TIMMessageReceipt max) {
        for (int i = 0; i < mDataSource.size(); i++) {
            MessageInfo messageInfo = mDataSource.get(i);
            if (messageInfo.getMsgTime() > max.getTimestamp()) {
                messageInfo.setPeerRead(false);
            } else {
                messageInfo.setPeerRead(true);
                updateAdapter(MessageLayout.DATA_CHANGE_TYPE_UPDATE, i);
            }
        }
    }

    public void notifyTyping() {
        if (mTypingListener != null) {
            mTypingListener.onTyping();
        }
    }

    public void setTypingListener(TypingListener l) {
        mTypingListener = l;
    }

    public void remove(int index) {
        mDataSource.remove(index);
        updateAdapter(MessageLayout.DATA_CHANGE_TYPE_DELETE, index);
    }

    public void clear() {
        mDataSource.clear();
        updateAdapter(MessageLayout.DATA_CHANGE_TYPE_LOAD, 0);
    }

    private void updateAdapter(int type, int data) {
        if (mAdapter != null) {

            LogUtils.w("更新消息---" + type);
            mAdapter.notifyDataSourceChanged(type, data);
        }
    }

    public void setAdapter(MessageListAdapter adapter) {
        this.mAdapter = adapter;
    }

    public interface TypingListener {
        void onTyping();
    }
}
