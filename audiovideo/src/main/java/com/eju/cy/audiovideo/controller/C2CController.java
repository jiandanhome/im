package com.eju.cy.audiovideo.controller;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.TUIKit;
import com.eju.cy.audiovideo.activity.chat.ChatActivity;
import com.eju.cy.audiovideo.dto.UserLevelDto;
import com.eju.cy.audiovideo.modules.chat.base.ChatInfo;
import com.eju.cy.audiovideo.modules.conversation.ConversationManagerKit;
import com.eju.cy.audiovideo.modules.conversation.base.ConversationInfo;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.eju.cy.audiovideo.modules.message.MessageInfoUtil;
import com.eju.cy.audiovideo.net.AppNetInterface;
import com.eju.cy.audiovideo.net.RetrofitManager;
import com.eju.cy.audiovideo.tags.AppConfig;
import com.eju.cy.audiovideo.tags.Constants;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.eju.cy.audiovideo.utils.ParameterUtils;
import com.eju.cy.audiovideo.utils.SharedPreferenceUtils;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMGroupSystemElem;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @ Name: Caochen
 * @ Date: 2020-05-07
 * @ Time: 16:02
 * @ Description： C2C会话相关
 */
public class C2CController {

    private final static String TAG = C2CController.class.getSimpleName();
    //存放自定义卡片
    private List<ConversationInfo> customCadList = new ArrayList<>();

    private static C2CController instance;

    private Application application;


    public static C2CController getInstance() {
        if (instance == null) {
            synchronized (C2CController.class) {
                if (instance == null) {
                    instance = new C2CController();
                }
            }
        }
        return instance;
    }


    public void init(Application application) {
        this.application = application;
    }


    /**
     * 获取自定义卡片
     *
     * @return
     */
    public List<ConversationInfo> getCustomCard() {
        return this.customCadList;
    }

    /**
     * 设置自定义卡片
     *
     * @param conversationInfos
     */
    public void setCustomCard(List<ConversationInfo> conversationInfos) {
        this.customCadList.clear();
        this.customCadList.addAll(conversationInfos);
    }


    /**
     * 打开一个C2C会话
     *
     * @param context  上下文
     * @param Id       对方IM  ID
     * @param title    名字
     * @param userRole 角色-----好友-经济人  等等
     */
    public void openChatActivity(final Context context, final String Id, final String title, final String userRole) {

        if (TextUtils.isEmpty(title) || "".equals(title)) {


            UserInfoController.getInstance().getUsersProfile(Id, true, new ImCallBack() {
                @Override
                public void onError(int var1, String var2) {

                    isSetTop(Id, new ImCallBack() {
                        @Override
                        public void onError(int var1, String var2) {

                        }

                        @Override
                        public void onSuccess(Object data) {

                            ConversationInfo conversationInfo = (ConversationInfo) data;

                            if (null != conversationInfo) {
                                ChatInfo chatInfo = new ChatInfo();
                                chatInfo.setId(Id);
                                chatInfo.setType(TIMConversationType.C2C);
                                chatInfo.setUserRole(userRole);
                                chatInfo.setChatName("与用户" + Id + "会话");

                                chatInfo.setTopChat(conversationInfo.isTop());
                                chatInfo.setConversationInfoStr(JsonUtils.toJson(conversationInfo));
                                Intent intent = new Intent(context, ChatActivity.class);
                                intent.putExtra(Constants.CHAT_INFO, chatInfo);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        }
                    });


                }

                @Override
                public void onSuccess(final Object var1) {


                    isSetTop(Id, new ImCallBack() {
                        @Override
                        public void onError(int var1, String var2) {

                        }

                        @Override
                        public void onSuccess(Object data) {


                            ConversationInfo conversationInfo = (ConversationInfo) data;

                            if (null != conversationInfo) {

                                List<TIMUserProfile> timUserProfileList = (List<TIMUserProfile>) var1;


                                ChatInfo chatInfo = new ChatInfo();
                                chatInfo.setId(Id);
                                chatInfo.setType(TIMConversationType.C2C);
                                chatInfo.setUserRole(userRole);
                                if (null != timUserProfileList && timUserProfileList.size() > 0 && timUserProfileList.get(0).getNickName().length() > 0) {
                                    chatInfo.setChatName(timUserProfileList.get(0).getNickName());
                                } else {
                                    chatInfo.setChatName("与用户" + Id + "会话");
                                }

                                chatInfo.setTopChat(conversationInfo.isTop());
                                chatInfo.setConversationInfoStr(JsonUtils.toJson(conversationInfo));

                                Intent intent = new Intent(context, ChatActivity.class);
                                intent.putExtra(Constants.CHAT_INFO, chatInfo);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }

                        }
                    });

                }
            });


        } else {


            isSetTop(Id, new ImCallBack() {
                @Override
                public void onError(int var1, String var2) {

                }

                @Override
                public void onSuccess(Object data) {


                    ConversationInfo conversationInfo = (ConversationInfo) data;

                    if (null != conversationInfo) {


                        ChatInfo chatInfo = new ChatInfo();
                        chatInfo.setId(Id);
                        chatInfo.setType(TIMConversationType.C2C);
                        chatInfo.setUserRole(userRole);
                        chatInfo.setChatName(title);
                        chatInfo.setTopChat(conversationInfo.isTop());
                        chatInfo.setConversationInfoStr(JsonUtils.toJson(conversationInfo));

                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra(Constants.CHAT_INFO, chatInfo);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }

                }
            });

        }

    }


    //CarryV

    /**
     * 打开一个C2C会话且带黄V
     *
     * @param context  上下文
     * @param Id       对方IM  ID
     * @param title    名字
     * @param userRole 角色-----好友-经济人  等等
     */
    public void openChatActivityCarryV(final Context context, final String Id, final String title, final String userRole) {

        if (TextUtils.isEmpty(title) || "".equals(title)) {


            UserInfoController.getInstance().getUsersProfile(Id, true, new ImCallBack() {
                @Override
                public void onError(int var1, String var2) {

                    isSetTop(Id, new ImCallBack() {
                        @Override
                        public void onError(int var1, String var2) {

                        }

                        @Override
                        public void onSuccess(Object data) {

                            final ConversationInfo conversationInfo = (ConversationInfo) data;

                            if (null != conversationInfo) {
//                                ChatInfo chatInfo = new ChatInfo();
//                                chatInfo.setId(Id);
//                                chatInfo.setType(TIMConversationType.C2C);
//                                chatInfo.setUserRole(userRole);
//                                chatInfo.setChatName("与用户" + Id + "会话");
//
//                                chatInfo.setTopChat(conversationInfo.isTop());
//                                chatInfo.setConversationInfoStr(JsonUtils.toJson(conversationInfo));
//                                Intent intent = new Intent(context, ChatActivity.class);
//                                intent.putExtra(Constants.CHAT_INFO, chatInfo);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                context.startActivity(intent);


                                getUserGrade(Id, new ImCallBack() {
                                    @Override
                                    public void onError(int var1, String var2) {
                                        startChatAt(Id, null, userRole, conversationInfo, context);
                                    }

                                    @Override
                                    public void onSuccess(Object var1) {


                                        startChatAt(Id, null, userRole, conversationInfo, context);

                                    }
                                });


                            }
                        }
                    });


                }

                @Override
                public void onSuccess(final Object var1) {


                    isSetTop(Id, new ImCallBack() {
                        @Override
                        public void onError(int var1, String var2) {

                        }

                        @Override
                        public void onSuccess(Object data) {


                            final ConversationInfo conversationInfo = (ConversationInfo) data;

                            if (null != conversationInfo) {

                                final List<TIMUserProfile> timUserProfileList = (List<TIMUserProfile>) var1;
//
//
//                                ChatInfo chatInfo = new ChatInfo();
//                                chatInfo.setId(Id);
//                                chatInfo.setType(TIMConversationType.C2C);
//                                chatInfo.setUserRole(userRole);
//                                if (null != timUserProfileList && timUserProfileList.size() > 0 && timUserProfileList.get(0).getNickName().length() > 0) {
//                                    chatInfo.setChatName(timUserProfileList.get(0).getNickName());
//                                } else {
//                                    chatInfo.setChatName("与用户" + Id + "会话");
//                                }
//
//                                chatInfo.setTopChat(conversationInfo.isTop());
//                                chatInfo.setConversationInfoStr(JsonUtils.toJson(conversationInfo));
//
//                                Intent intent = new Intent(context, ChatActivity.class);
//                                intent.putExtra(Constants.CHAT_INFO, chatInfo);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                context.startActivity(intent);

                                getUserGrade(Id, new ImCallBack() {
                                    @Override
                                    public void onError(int var1, String var2) {


                                        if (null != timUserProfileList && timUserProfileList.size() > 0 && timUserProfileList.get(0).getNickName().length() > 0) {

                                            startChatAt(Id, timUserProfileList.get(0).getNickName(), userRole, conversationInfo, context);
                                        } else {
                                            startChatAt(Id, null, userRole, conversationInfo, context);
                                        }


                                    }

                                    @Override
                                    public void onSuccess(Object var1) {


                                        if (null != timUserProfileList && timUserProfileList.size() > 0 && timUserProfileList.get(0).getNickName().length() > 0) {

                                            startChatAt(Id, timUserProfileList.get(0).getNickName(), userRole, conversationInfo, context);
                                        } else {
                                            startChatAt(Id, null, userRole, conversationInfo, context);
                                        }


                                    }
                                });

                            }

                        }
                    });

                }
            });


        } else {


            isSetTop(Id, new ImCallBack() {
                @Override
                public void onError(int var1, String var2) {

                }

                @Override
                public void onSuccess(Object data) {


                    final ConversationInfo conversationInfo = (ConversationInfo) data;

                    if (null != conversationInfo) {


//                        ChatInfo chatInfo = new ChatInfo();
//                        chatInfo.setId(Id);
//                        chatInfo.setType(TIMConversationType.C2C);
//                        chatInfo.setUserRole(userRole);
//                        chatInfo.setChatName(title);
//                        chatInfo.setTopChat(conversationInfo.isTop());
//                        chatInfo.setConversationInfoStr(JsonUtils.toJson(conversationInfo));
//
//                        Intent intent = new Intent(context, ChatActivity.class);
//                        intent.putExtra(Constants.CHAT_INFO, chatInfo);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);


                        getUserGrade(Id, new ImCallBack() {
                            @Override
                            public void onError(int var1, String var2) {
                                startChatAt(Id, title, userRole, conversationInfo, context);
                            }

                            @Override
                            public void onSuccess(Object var1) {


                                startChatAt(Id, title, userRole, conversationInfo, context);

                            }
                        });


                    }

                }
            });

        }

    }


    private void startChatAt(String Id, String title, String userRole, ConversationInfo conversationInfo, Context context) {
        if (null != conversationInfo) {

            ChatInfo chatInfo = new ChatInfo();
            chatInfo.setId(Id);
            chatInfo.setType(TIMConversationType.C2C);
            chatInfo.setUserRole(userRole);

            if (null == title) {
                chatInfo.setChatName("与用户" + Id + "会话");
            } else {
                chatInfo.setChatName(title);
            }

            chatInfo.setTopChat(conversationInfo.isTop());
            chatInfo.setConversationInfoStr(JsonUtils.toJson(conversationInfo));
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra(Constants.CHAT_INFO, chatInfo);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }


    }


    /**
     * 获取单个聊天信息
     *
     * @param conversationId
     * @param imCallBack
     */
    public void getConversation(String conversationId, ImCallBack imCallBack) {
        TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, conversationId);

        if (null != conversationId && null != conversation) {
            ConversationInfo conversationInfo = TIMConversation2ConversationInfo(conversation);
            if (null != conversationInfo) {

                imCallBack.onSuccess(conversationInfo);
            } else {
                imCallBack.onError(8888, "getConversation  conversationId  null");
            }


        } else {
            imCallBack.onError(8888, "getConversation  conversationId  null");
        }


    }


    /**
     * TIMConversation转换为ConversationInfo
     *
     * @param conversation
     * @return
     */
    private ConversationInfo TIMConversation2ConversationInfo(final TIMConversation conversation) {
        if (conversation == null) {
            return null;
        }
        LogUtils.i("TIMConversation2ConversationInfo id:" + conversation.getPeer()
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
                    //groupSystMsgHandle(groupSysEle);
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
            //fillConversationWithGroupInfo(conversation, info);
        } else {
            //fillConversationWithUserProfile(conversation, info);
        }
        info.setId(conversation.getPeer());
        info.setGroup(conversation.getType() == TIMConversationType.Group);
        info.setUnRead((int) conversation.getUnreadMessageNum());
        return info;
    }


    /**
     * 获取美房客服ID
     *
     * @return
     */
    public String getServiceID() {

        if (AppConfig.sdkAppId == 1400324570) {
            return "1053816";
        }

        return "10954";
    }

    /**
     * 是否是设置置顶的聊天（C2C）
     *
     * @param conversationId 会话ID
     * @param imCallBack     回调
     */
    public void isSetTop(String conversationId, ImCallBack imCallBack) {
        String SP_NAME = "top_conversion_list";
        String TOP_LIST = "top_list";
        SharedPreferences mConversationPreferences = TUIKit.getAppContext().getSharedPreferences(TIMManager.getInstance().getLoginUser() + SP_NAME, Context.MODE_PRIVATE);
        LinkedList<ConversationInfo> mTopLinkedList = SharedPreferenceUtils.getListData(mConversationPreferences, TOP_LIST, ConversationInfo.class);
        TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, conversationId);


        ConversationInfo conversationInfo = TIMConversation2ConversationInfo(conversation);
        if (null != conversationInfo) {

            if (null != mTopLinkedList && mTopLinkedList.size() > 0) {

                for (ConversationInfo info : mTopLinkedList) {
                    if (TextUtils.equals(info.getId(), conversationId)) {
                        conversationInfo.setTop(true);
                        break;
                    }
                }
                imCallBack.onSuccess(conversationInfo);
            } else {
                imCallBack.onSuccess(conversationInfo);
            }
            //无数据
        } else {
            imCallBack.onSuccess(new ConversationInfo());
        }


    }


    /**
     * C2C设置消息置顶以及取消置顶
     *
     * @param id   会话ID
     * @param flag 是否置顶
     */
    public void setConversationTop(String id, boolean flag) {
        ConversationManagerKit.getInstance().setConversationTop(id, flag);
    }


    /**
     * C2C发送一条图片消息
     *
     * @param path           图片本地地址
     * @param conversationId 对方会话ID
     * @param imCallBack     回调
     */
    public void sendImageMessage(Uri path, String conversationId, final ImCallBack imCallBack) {

        MessageInfo info = MessageInfoUtil.buildImageMessage(path, true);

        TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.C2C, conversationId);
        con.sendMessage(info.getTIMMessage(), new TIMValueCallBack<TIMMessage>() {

            @Override
            public void onError(int code, String desc) {
                LogUtils.w("sendMessage fail:" + code + "=" + desc);
                imCallBack.onError(code, desc);

            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                LogUtils.w("sendMessage onSuccess");
                imCallBack.onSuccess(timMessage);
            }
        });


    }


    /**
     * C2C发送一条文本消息
     *
     * @param msg            文本消息内容
     * @param conversationId 对方会话ID
     * @param imCallBack     回调
     */
    public void sendTextMessage(String msg, String conversationId, final ImCallBack imCallBack) {

        MessageInfo info = MessageInfoUtil.buildTextMessage(msg);

        TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.C2C, conversationId);
        con.sendMessage(info.getTIMMessage(), new TIMValueCallBack<TIMMessage>() {

            @Override
            public void onError(int code, String desc) {
                LogUtils.w("sendMessage fail:" + code + "=" + desc);
                imCallBack.onError(code, desc);

            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                LogUtils.w("sendMessage onSuccess");
                imCallBack.onSuccess(timMessage);
            }
        });


    }


    /**
     * C2C发送一条文件消息
     *
     * @param fileUri        文件本地地址
     * @param conversationId 对方会话ID
     * @param imCallBack     回调
     */
    public void sendFileMessage(Uri fileUri, String conversationId, final ImCallBack imCallBack) {

        MessageInfo info = MessageInfoUtil.buildFileMessage(fileUri);

        TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.C2C, conversationId);
        con.sendMessage(info.getTIMMessage(), new TIMValueCallBack<TIMMessage>() {

            @Override
            public void onError(int code, String desc) {
                LogUtils.w("sendMessage fail:" + code + "=" + desc);
                imCallBack.onError(code, desc);

            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                LogUtils.w("sendMessage onSuccess");
                imCallBack.onSuccess(timMessage);
            }
        });


    }


    /**
     * 获取C2C会话未读消息数量(易楼使用)
     *
     * @return
     */
    public int loadC2CUnreadCount() {
        return ConversationManagerKit.getInstance().loadC2CUnreadCount();
    }

    /**
     * 获取C2C会话好友（人脉）未读消息数量
     *
     * @return
     */
    public int loadC2cAndFriendsUnreadCount() {
        return ConversationManagerKit.getInstance().loadC2cAndFriendsUnreadCount();
    }

    /**
     * 获取C2C会话非好友（消息）未读消息数量
     *
     * @return
     */
    public int loadC2cAndNonFriendsUnreadCount() {
        return ConversationManagerKit.getInstance().loadC2cAndNonFriendsUnreadCount();
    }


    /**
     * 查询用户是否为黄V
     *
     * @param otherImDd  对方ID
     * @param imCallBack
     */
    private void getUserGrade(final String otherImDd, final ImCallBack imCallBack) {


        String ids = otherImDd + "," + AppConfig.appImId;

        final AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(application);
        httpInterface.getUserGrade(ParameterUtils.prepareFormData(ids))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserLevelDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UserLevelDto userLevelDto) {

                        if (null != userLevelDto && "10000".equals(userLevelDto.getCode())) {
                            Map<String, String> gradeMap = new HashMap<>();

                            if (null != userLevelDto.getData() && userLevelDto.getData().size() > 0) {
                                gradeMap.putAll(userLevelDto.getData());
                                imCallBack.onSuccess(gradeMap);
                                EjuImController.getInstance().setvMap(gradeMap);
                            } else {
                                imCallBack.onError(888888, "" + "非10000");
                            }


                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        imCallBack.onError(888888, "" + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


}




