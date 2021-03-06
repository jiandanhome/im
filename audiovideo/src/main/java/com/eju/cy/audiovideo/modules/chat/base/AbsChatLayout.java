package com.eju.cy.audiovideo.modules.chat.base;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.base.IUIKitCallBack;
import com.eju.cy.audiovideo.component.AudioPlayer;
import com.eju.cy.audiovideo.controller.GroupController;
import com.eju.cy.audiovideo.controller.ImCallBack;
import com.eju.cy.audiovideo.dto.CustomContentDto;
import com.eju.cy.audiovideo.dto.CustomMessage;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.modules.chat.interfaces.IChatLayout;
import com.eju.cy.audiovideo.modules.chat.interfaces.IChatProvider;
import com.eju.cy.audiovideo.modules.chat.layout.input.InputLayout;
import com.eju.cy.audiovideo.modules.chat.layout.message.MessageLayout;
import com.eju.cy.audiovideo.modules.chat.layout.message.MessageListAdapter;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.eju.cy.audiovideo.modules.message.MessageInfoUtil;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.observer.EjuHomeImObserver;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.tags.AppConfig;
import com.eju.cy.audiovideo.utils.BackgroundTasks;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.eju.cy.audiovideo.utils.NetWorkUtils;
import com.eju.cy.audiovideo.utils.TUIKitLog;
import com.eju.cy.audiovideo.utils.ToastUtil;
import com.google.gson.Gson;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;

import java.util.Map;


public abstract class AbsChatLayout extends ChatLayoutUI implements IChatLayout {

    private MessageListAdapter mAdapter;
    private boolean isOneLoad = true;
    private AnimationDrawable mVolumeAnim;
    private Runnable mTypingRunnable = null;
    private ChatProvider.TypingListener mTypingListener = new ChatProvider.TypingListener() {
        @Override
        public void onTyping() {
            final String oldTitle = getTitleBar().getMiddleTitle().getText().toString();
            getTitleBar().getMiddleTitle().setText(R.string.typing);
            if (mTypingRunnable == null) {
                mTypingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        getTitleBar().getMiddleTitle().setText(oldTitle);
                    }
                };
            }
            getTitleBar().getMiddleTitle().removeCallbacks(mTypingRunnable);
            getTitleBar().getMiddleTitle().postDelayed(mTypingRunnable, 3000);
        }
    };

    public AbsChatLayout(Context context) {
        super(context);
    }

    public AbsChatLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AbsChatLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initListener() {
        getMessageLayout().setPopActionClickListener(new MessageLayout.OnPopActionClickListener() {
            @Override
            public void onCopyClick(int position, MessageInfo msg) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard == null || msg == null) {
                    return;
                }
                if (msg.getElement() instanceof TIMTextElem) {
                    TIMTextElem textElem = (TIMTextElem) msg.getElement();
                    ClipData clip = ClipData.newPlainText("message", textElem.getText());
                    clipboard.setPrimaryClip(clip);
                }
            }

            @Override
            public void onSendMessageClick(MessageInfo msg, boolean retry) {


                Log.w("-------------????????????", msg.toString());
                sendMessage(msg, retry);
            }

            @Override
            public void onDeleteMessageClick(int position, MessageInfo msg) {
                deleteMessage(position, msg);
            }

            @Override
            public void onRevokeMessageClick(int position, MessageInfo msg) {
                revokeMessage(position, msg);
            }
        });
        getMessageLayout().setLoadMoreMessageHandler(new MessageLayout.OnLoadMoreHandler() {
            @Override
            public void loadMore() {
                loadMessages();
            }
        });
        getMessageLayout().setEmptySpaceClickListener(new MessageLayout.OnEmptySpaceClickListener() {
            @Override
            public void onClick() {
                getInputLayout().hideSoftInput();
            }
        });

        /**
         * ???????????????????????????????????????
         */
        getMessageLayout().addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_UP) {
                    View child = rv.findChildViewUnder(e.getX(), e.getY());
                    if (child == null) {
                        getInputLayout().hideSoftInput();
                    } else if (child instanceof ViewGroup) {
                        ViewGroup group = (ViewGroup) child;
                        final int count = group.getChildCount();
                        float x = e.getRawX();
                        float y = e.getRawY();
                        View touchChild = null;
                        for (int i = count - 1; i >= 0; i--) {
                            final View innerChild = group.getChildAt(i);
                            int position[] = new int[2];
                            innerChild.getLocationOnScreen(position);
                            if (x >= position[0]
                                    && x <= position[0] + innerChild.getMeasuredWidth()
                                    && y >= position[1]
                                    && y <= position[1] + innerChild.getMeasuredHeight()) {
                                touchChild = innerChild;
                                break;
                            }
                        }
                        if (touchChild == null)
                            getInputLayout().hideSoftInput();
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        getInputLayout().setChatInputHandler(new InputLayout.ChatInputHandler() {
            @Override
            public void onInputAreaClick() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        scrollToEnd();
                    }
                });
            }

            @Override
            public void onRecordStatusChanged(int status) {
                switch (status) {
                    case RECORD_START:
                        startRecording();
                        break;
                    case RECORD_STOP:
                        stopRecording();
                        break;
                    case RECORD_CANCEL:
                        cancelRecording();
                        break;
                    case RECORD_TOO_SHORT:
                    case RECORD_FAILED:
                        stopAbnormally(status);
                        break;
                    default:
                        break;
                }
            }

            private void startRecording() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        AudioPlayer.getInstance().stopPlay();
                        mRecordingGroup.setVisibility(VISIBLE);
                        mRecordingIcon.setImageResource(R.drawable.recording_volume);
                        mVolumeAnim = (AnimationDrawable) mRecordingIcon.getDrawable();
                        mVolumeAnim.start();
                        mRecordingTips.setTextColor(Color.WHITE);
                        mRecordingTips.setText("???????????????????????????");
                    }
                });
            }

            private void stopRecording() {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mVolumeAnim.stop();
                        mRecordingGroup.setVisibility(GONE);
                    }
                }, 500);
            }

            private void stopAbnormally(final int status) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        mVolumeAnim.stop();
                        mRecordingIcon.setImageResource(R.drawable.ic_volume_dialog_length_short);
                        mRecordingTips.setTextColor(Color.WHITE);
                        if (status == RECORD_TOO_SHORT) {
                            mRecordingTips.setText("??????????????????");
                        } else {
                            mRecordingTips.setText("????????????");
                        }
                    }
                });
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecordingGroup.setVisibility(GONE);
                    }
                }, 1000);
            }

            private void cancelRecording() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        mRecordingIcon.setImageResource(R.drawable.ic_volume_dialog_cancel);
                        mRecordingTips.setText("???????????????????????????");
                    }
                });
            }
        });
    }

    @Override
    public void initDefault() {
        getTitleBar().getLeftGroup().setVisibility(VISIBLE);
        getTitleBar().setOnLeftClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() instanceof Activity) {
                    ((Activity) getContext()).finish();
                }
            }
        });
        getInputLayout().setMessageHandler(new InputLayout.MessageHandler() {
            @Override
            public void sendMessage(MessageInfo msg) {
                Log.w("-------------????????????", msg.toString());
                AbsChatLayout.this.sendMessage(msg, false);
            }
        });
        getInputLayout().clearCustomActionList();
        if (getMessageLayout().getAdapter() == null) {
            mAdapter = new MessageListAdapter();
            getMessageLayout().setAdapter(mAdapter);
        }
        initListener();
    }

    @Override
    public void setParentLayout(Object parentContainer) {

    }

    public void scrollToEnd() {
        getMessageLayout().scrollToEnd();
    }

    public void setDataProvider(IChatProvider provider) {


        if (provider != null) {
            ((ChatProvider) provider).setTypingListener(mTypingListener);
        }
        if (mAdapter != null) {
            mAdapter.setDataSource(provider);
        }


        //@??????  //@??????  //@??????  //@??????  //@??????  //@??????
        //@??????  //@??????  //@??????  //@??????  //@??????  //@??????
        //@??????  //@??????  //@??????  //@??????  //@??????  //@??????
        //@??????  //@??????  //@??????  //@??????  //@??????  //@??????
        if (null != provider && null != provider.getDataSource() && provider.getDataSource().size() > 0) {


            int read = -1;

            for (int i = 0; i < provider.getDataSource().size(); i++) {
                // ???????????????????????????json??????
                if ((provider.getDataSource().get(i).getElement() instanceof TIMCustomElem)) {


                    TIMCustomElem elem = (TIMCustomElem) provider.getDataSource().get(i).getElement();
                    // ????????????json????????????????????????bean??????
                    CustomMessage data = null;

                    try {
                        data = new Gson().fromJson(new String(elem.getData()), CustomMessage.class);


                    } catch (Exception e) {
                        LogUtils.w("@invalid json: " + new String(elem.getData()) + " " + e.getMessage());
                    }

                    //??????????????????????????????@?????????????????????????????????????????????????????????????????????@???Map???
                    if (null != data && data.getAction() == CustomMessage.CUSTOM_AIT && !provider.getDataSource().get(i).isRead() && !provider.getDataSource().get(i).isSelf()) {

                        CustomContentDto businessCardDto = JsonUtils.fromJson(data.getContent(), CustomContentDto.class);

                        if (null != businessCardDto && null != businessCardDto.getAitMap()) {
                            Map<String, String> aitMap = businessCardDto.getAitMap();
                            if (null != aitMap.get(AppConfig.appImId) && !"".equals(aitMap.get(AppConfig.appImId))) {
                                read = i;
                            }
                        }


                        //  LogUtils.w("@??????------" + provider.getDataSource().get(i).toString() + "\n??????------" + read);

                    }
                }
                //????????????@??????
                if (read >= 0) {
                    getAitTextView().setVisibility(VISIBLE);
                    final int finalRead = read;
                    getAitTextView().setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // LogUtils.w("scrollToPosition"+finalRead);
                            getMessageLayout().scrollToPosition(finalRead + 1);
                            getAitTextView().setVisibility(GONE);
                        }
                    });
                } else {
                    getAitTextView().setVisibility(GONE);

                }
            }


            //????????????????????????
            if (null != provider.getDataSource() && provider.getDataSource().size() > 0 && !provider.getDataSource().get(0).isGroup() && isOneLoad) {
                isOneLoad = false;
                TIMMessage msg = provider.getDataSource().get(provider.getDataSource().size() - 1).getTIMMessage();

                TIMElem elem = msg.getElement(0);
                if (elem != null) {
                    if (elem.getType() == TIMElemType.Text) {
                        TIMTextElem textElem = (TIMTextElem) elem;
                        LogUtils.w("??????????????????---" + textElem.getText());

                        if (textElem.getText().startsWith("[????????????]")) {

                            //??????????????????????????????
                            TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, msg.getConversation().getPeer());
                            TIMMessage timMessage = new TIMMessage();
                            TIMCustomElem ele = new TIMCustomElem();
                            ele.setData(MessageInfoUtil.GROUP_CREATE.getBytes());
                            ele.setExt("???????????????????????????".getBytes());
                            timMessage.addElement(ele);
                            conversation.saveMessage(timMessage, msg.getConversation().getPeer(), true);

                            //provider.getDataSource().add(createTips);
                            getChatManager().addMessage(conversation, timMessage);

                        }

                    }
                }


            }
        }


        //??????????????????
        if (null !=

                getChatInfo()) {
            TIMConversation mCurrentConversation = TIMManager.getInstance().getConversation(getChatInfo().getType(), getChatInfo().getId());
            mCurrentConversation.setReadMessage(null, new TIMCallBack() {
                @Override
                public void onError(int code, String desc) {
                    LogUtils.v("@??????????????????", " setReadMessage failed, code = " + code + ", desc = " + desc);
                }

                @Override
                public void onSuccess() {
                    TUIKitLog.v("@??????????????????", " setReadMessage success");
                }
            });


        }


    }

    public abstract ChatManagerKit getChatManager();

    @Override
    public void loadMessages() {


        loadChatMessages(mAdapter.getItemCount() > 0 ? mAdapter.getItem(1) : null);

    }

    public void loadChatMessages(final MessageInfo lastMessage) {


        if (getChatInfo().getType() == TIMConversationType.Group) {
            //?????????

            GroupController.getInstance().setNowOpenGroupRole(getChatInfo().getId(), new ImCallBack() {
                @Override
                public void onError(int var1, String var2) {
                    loadChatMessages(1, lastMessage);
                }

                @Override
                public void onSuccess(Object var1) {
                    loadChatMessages(1, lastMessage);
                }
            });

        } else {

            loadChatMessages(1, lastMessage);
        }


    }

    private void loadChatMessages(int i, final MessageInfo lastMessage) {


        if (NetWorkUtils.sIMSDKConnected) {
            getChatManager().loadChatMessages(lastMessage, new IUIKitCallBack() {
                @Override
                public void onSuccess(Object data) {
                    if (lastMessage == null && data != null) {

                        setDataProvider((ChatProvider) data);


                    }
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    //ToastUtil.toastLongMessage(errMsg);
                    ToastUtils.showShort("??????????????????????????????????????????APP?????????????????????");
                    if (lastMessage == null) {
                        setDataProvider(null);


                    }
                }
            });
        } else {
            getChatManager().loadLocalChatMessages(lastMessage, new IUIKitCallBack() {
                @Override
                public void onSuccess(Object data) {
                    if (lastMessage == null && data != null) {
                        setDataProvider((ChatProvider) data);
                    }
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    // ToastUtil.toastLongMessage(errMsg);
                    ToastUtils.showShort("??????????????????????????????????????????APP?????????????????????");
                    if (lastMessage == null) {
                        setDataProvider(null);
                    }
                }
            });
        }
    }


    public void addMsg(TIMConversation conversation, TIMMessage msg) {
        getChatManager().addMessage(conversation, msg);

    }

    protected void deleteMessage(int position, MessageInfo msg) {
        getChatManager().deleteMessage(position, msg);
    }

    public void deleteMsg(int position, MessageInfo msg) {
        getChatManager().deleteMessage(position, msg);
    }

    protected void revokeMessage(int position, MessageInfo msg) {
        getChatManager().revokeMessage(position, msg);
    }

    @Override
    public void sendMessage(MessageInfo msg, boolean retry) {
        Log.w("-------------????????????", msg.getMsgTime() + "\n" + msg.isGroup());
        getChatManager().sendMessage(msg, retry, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scrollToEnd();
                    }
                });
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ToastUtil.toastLongMessage(errMsg);
            }
        });
    }


    @Override
    public void exitChat() {
        getTitleBar().getMiddleTitle().removeCallbacks(mTypingRunnable);
        AudioPlayer.getInstance().stopRecord();
        AudioPlayer.getInstance().stopPlay();
        if (getChatManager() != null) {
            getChatManager().destroyChat();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        exitChat();
    }


    public MessageListAdapter getMyAdapter() {

        return mAdapter;
    }


}
