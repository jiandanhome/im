package com.eju.cy.audiovideo.modules.message;

import android.net.Uri;
import android.text.TextUtils;

import com.eju.cy.audiovideo.controller.ImCallBack;
import com.eju.cy.audiovideo.controller.UserInfoController;
import com.eju.cy.audiovideo.dto.CustomContentDto;
import com.eju.cy.audiovideo.dto.CustomMessage;
import com.eju.cy.audiovideo.utils.DateTimeUtil;
import com.eju.cy.audiovideo.utils.FileUtil;
import com.eju.cy.audiovideo.utils.ImageUtil;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.eju.cy.audiovideo.utils.TUIKitConstants;
import com.eju.cy.audiovideo.utils.TUIKitLog;
import com.google.gson.Gson;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMFaceElem;
import com.tencent.imsdk.TIMFileElem;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMGroupTipsElem;
import com.tencent.imsdk.TIMGroupTipsElemGroupInfo;
import com.tencent.imsdk.TIMGroupTipsElemMemberInfo;
import com.tencent.imsdk.TIMGroupTipsGroupInfoType;
import com.tencent.imsdk.TIMGroupTipsType;
import com.tencent.imsdk.TIMImage;
import com.tencent.imsdk.TIMImageElem;
import com.tencent.imsdk.TIMImageType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageStatus;
import com.tencent.imsdk.TIMSnapshot;
import com.tencent.imsdk.TIMSoundElem;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMVideo;
import com.tencent.imsdk.TIMVideoElem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MessageInfoUtil {

    public static final String GROUP_CREATE = "group_create";
    public static final String GROUP_DELETE = "group_delete";
    public static final String GROUP_UPLOAD_FILE = "group_upload_file";
    private static final String TAG = MessageInfoUtil.class.getSimpleName();


    /**
     * 创建一条文本消息
     *
     * @param message 消息内容
     * @return
     */
    public static MessageInfo buildTextMessage(String message) {
        MessageInfo info = new MessageInfo();
        TIMMessage TIMMsg = new TIMMessage();
        TIMTextElem ele = new TIMTextElem();
        ele.setText(message);
        TIMMsg.addElement(ele);
        info.setExtra(message);
        info.setMsgTime(System.currentTimeMillis() / 1000);
        info.setElement(ele);
        info.setSelf(true);
        info.setTIMMessage(TIMMsg);
        info.setFromUser(TIMManager.getInstance().getLoginUser());
        info.setMsgType(MessageInfo.MSG_TYPE_TEXT);
        return info;
    }

    /**
     * 创建一条自定义表情的消息
     *
     * @param groupId  自定义表情所在的表情组id
     * @param faceName 表情的名称
     * @return
     */
    public static MessageInfo buildCustomFaceMessage(int groupId, String faceName) {
        MessageInfo info = new MessageInfo();
        TIMMessage TIMMsg = new TIMMessage();
        TIMFaceElem ele = new TIMFaceElem();
        ele.setIndex(groupId);
        ele.setData(faceName.getBytes());
        TIMMsg.addElement(ele);
        info.setExtra("[自定义表情]");
        info.setMsgTime(System.currentTimeMillis() / 1000);
        info.setElement(ele);
        info.setSelf(true);
        info.setTIMMessage(TIMMsg);
        info.setFromUser(TIMManager.getInstance().getLoginUser());
        info.setMsgType(MessageInfo.MSG_TYPE_CUSTOM_FACE);
        return info;
    }

    /**
     * 创建一条图片消息
     *
     * @param uri        图片URI
     * @param compressed 是否压缩
     * @return
     */
    public static MessageInfo buildImageMessage(final Uri uri, boolean compressed) {
        final MessageInfo info = new MessageInfo();
        final TIMImageElem ele = new TIMImageElem();
        info.setDataUri(uri);
        int size[] = ImageUtil.getImageSize(uri);
        String path = FileUtil.getPathFromUri(uri);
        ele.setPath(path);
        info.setDataPath(path);
        info.setImgWidth(size[0]);
        info.setImgHeight(size[1]);

        TIMMessage TIMMsg = new TIMMessage();
        TIMMsg.setSender(TIMManager.getInstance().getLoginUser());
        TIMMsg.setTimestamp(System.currentTimeMillis());
        if (!compressed) {
            ele.setLevel(0);
        }
        TIMMsg.addElement(ele);
        info.setSelf(true);
        info.setTIMMessage(TIMMsg);
        info.setExtra("[图片]");
        info.setMsgTime(System.currentTimeMillis() / 1000);
        info.setElement(ele);
        info.setFromUser(TIMManager.getInstance().getLoginUser());
        info.setMsgType(MessageInfo.MSG_TYPE_IMAGE);
        return info;
    }

    /**
     * 创建一条视频消息
     *
     * @param imgPath   视频缩略图路径
     * @param videoPath 视频路径
     * @param width     视频的宽
     * @param height    视频的高
     * @param duration  视频的时长
     * @return
     */
    public static MessageInfo buildVideoMessage(String imgPath, String videoPath, int width, int height, long duration) {
        MessageInfo info = new MessageInfo();
        TIMMessage TIMMsg = new TIMMessage();
        TIMVideoElem ele = new TIMVideoElem();

        TIMVideo video = new TIMVideo();
        video.setDuaration(duration / 1000);
        video.setType("mp4");
        TIMSnapshot snapshot = new TIMSnapshot();

        snapshot.setWidth(width);
        snapshot.setHeight(height);
        ele.setSnapshot(snapshot);
        ele.setVideo(video);
        ele.setSnapshotPath(imgPath);
        ele.setVideoPath(videoPath);

        TIMMsg.addElement(ele);
        Uri videoUri = Uri.fromFile(new File(videoPath));
        info.setSelf(true);
        info.setImgWidth(width);
        info.setImgHeight(height);
        info.setDataPath(imgPath);
        info.setDataUri(videoUri);
        info.setTIMMessage(TIMMsg);
        info.setExtra("[视频]");
        info.setMsgTime(System.currentTimeMillis() / 1000);
        info.setElement(ele);
        info.setFromUser(TIMManager.getInstance().getLoginUser());
        info.setMsgType(MessageInfo.MSG_TYPE_VIDEO);
        return info;
    }

    /**
     * 创建一条音频消息
     *
     * @param recordPath 音频路径
     * @param duration   音频的时长
     * @return
     */
    public static MessageInfo buildAudioMessage(String recordPath, int duration) {
        MessageInfo info = new MessageInfo();
        info.setDataPath(recordPath);
        TIMMessage TIMMsg = new TIMMessage();
        TIMMsg.setSender(TIMManager.getInstance().getLoginUser());
        TIMMsg.setTimestamp(System.currentTimeMillis() / 1000);
        TIMSoundElem ele = new TIMSoundElem();
        ele.setDuration(duration / 1000);
        ele.setPath(recordPath);
        TIMMsg.addElement(ele);
        info.setSelf(true);
        info.setTIMMessage(TIMMsg);
        info.setExtra("[语音]");
        info.setMsgTime(System.currentTimeMillis() / 1000);
        info.setElement(ele);
        info.setFromUser(TIMManager.getInstance().getLoginUser());
        info.setMsgType(MessageInfo.MSG_TYPE_AUDIO);
        return info;
    }

    /**
     * 创建一条文件消息
     *
     * @param fileUri 文件路径
     * @return
     */
    public static MessageInfo buildFileMessage(Uri fileUri) {
        String filePath = FileUtil.getPathFromUri(fileUri);
        File file = new File(filePath);
        if (file.exists()) {
            MessageInfo info = new MessageInfo();
            info.setDataPath(filePath);
            TIMMessage TIMMsg = new TIMMessage();
            TIMFileElem ele = new TIMFileElem();
            TIMMsg.setSender(TIMManager.getInstance().getLoginUser());
            TIMMsg.setTimestamp(System.currentTimeMillis() / 1000);
            ele.setPath(filePath);
            ele.setFileName(file.getName());
            TIMMsg.addElement(ele);
            info.setSelf(true);
            info.setTIMMessage(TIMMsg);
            info.setExtra("[文件]");
            info.setMsgTime(System.currentTimeMillis() / 1000);
            info.setElement(ele);
            info.setFromUser(TIMManager.getInstance().getLoginUser());
            info.setMsgType(MessageInfo.MSG_TYPE_FILE);
            return info;
        }
        return null;
    }

    /**
     * 创建一条自定义消息
     *
     * @param data 自定义消息内容，可以是任何内容
     * @return
     */
    public static MessageInfo buildCustomMessage(String data) {
        MessageInfo info = new MessageInfo();
        TIMMessage TIMMsg = new TIMMessage();
        TIMCustomElem ele = new TIMCustomElem();
        ele.setData(data.getBytes());
        TIMMsg.addElement(ele);
        info.setSelf(true);
        info.setTIMMessage(TIMMsg);
        info.setMsgTime(System.currentTimeMillis() / 1000);
        info.setElement(ele);
        info.setMsgType(MessageInfo.MSG_TYPE_CUSTOM);
        info.setFromUser(TIMManager.getInstance().getLoginUser());
        return info;
    }

    /**
     * 创建一条群消息自定义内容
     *
     * @param action  群消息类型，比如建群等
     * @param message 消息内容
     * @return
     */
    public static TIMMessage buildGroupCustomMessage(String action, String message) {
        TIMMessage TIMMsg = new TIMMessage();
        TIMCustomElem ele = new TIMCustomElem();
        ele.setData(action.getBytes());
        ele.setExt(message.getBytes());
        TIMMsg.addElement(ele);
        return TIMMsg;
    }



    /**
     * 把SDK的消息bean列表转化为TUIKit的消息bean列表
     *
     * @param timMessages SDK的群消息bean列表
     * @param isGroup     是否是群消息
     * @return
     */
    public static List<MessageInfo> TIMMessages2MessageInfos(List<TIMMessage> timMessages, boolean isGroup) {
        if (timMessages == null) {
            return null;
        }
        List<MessageInfo> messageInfos = new ArrayList<>();
        for (int i = 0; i < timMessages.size(); i++) {
            TIMMessage timMessage = timMessages.get(i);
            List<MessageInfo> info = TIMMessage2MessageInfo(timMessage, isGroup);
            if (info != null) {
                messageInfos.addAll(info);
            }
        }
        return messageInfos;
    }

    /**
     * 把SDK的消息bean转化为TUIKit的消息bean
     *
     * @param timMessage SDK的群消息bean
     * @param isGroup    是否是群消息
     * @return
     */
    public static List<MessageInfo> TIMMessage2MessageInfo(TIMMessage timMessage, boolean isGroup) {
        if (timMessage == null || timMessage.status() == TIMMessageStatus.HasDeleted || timMessage.getElementCount() == 0) {
            return null;
        }
        List<MessageInfo> list = new ArrayList<>();
        for (int i = 0; i < timMessage.getElementCount(); i++) {
            final MessageInfo msgInfo = new MessageInfo();
            if (ele2MessageInfo(msgInfo, timMessage, timMessage.getElement(i), isGroup) != null) {
                list.add(msgInfo);
            }
        }
        return list;
    }

    public static boolean isTyping(TIMMessage timMessage) {
        // 如果有任意一个element是正在输入，则认为这条消息是正在输入。除非测试，正常不可能发这种消息。
        for (int i = 0; i < timMessage.getElementCount(); i++) {
            if (timMessage.getElement(i).getType() == TIMElemType.Custom) {
                TIMCustomElem customElem = (TIMCustomElem) timMessage.getElement(i);
                if (isTyping(customElem.getData())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isTyping(byte[] data) {
        try {
            String str = new String(data, "UTF-8");
            MessageTyping typing = new Gson().fromJson(str, MessageTyping.class);
            if (typing != null
                    && typing.userAction == MessageTyping.TYPE_TYPING
                    && TextUtils.equals(typing.actionParam, MessageTyping.EDIT_START)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            TUIKitLog.e(TAG, "parse json error");
        }
        return false;
    }

    public static MessageInfo ele2MessageInfo(final MessageInfo msgInfo, TIMMessage timMessage, final TIMElem ele, boolean isGroup) {


        if (msgInfo == null
                || timMessage == null
                || timMessage.status() == TIMMessageStatus.HasDeleted
                || timMessage.getElementCount() == 0
                || ele == null
                || ele.getType() == TIMElemType.Invalid) {
            TUIKitLog.e(TAG, "ele2MessageInfo parameters error");
            return null;
        }

        String sender = timMessage.getSender();
        msgInfo.setTIMMessage(timMessage);
        msgInfo.setElement(ele);
        msgInfo.setGroup(isGroup);
        msgInfo.setId(timMessage.getMsgId());
        msgInfo.setUniqueId(timMessage.getMsgUniqueId());
        msgInfo.setPeerRead(timMessage.isPeerReaded());
        msgInfo.setRead(timMessage.isRead());
        msgInfo.setFromUser(sender);
        if (isGroup) {
            TIMGroupMemberInfo memberInfo = timMessage.getSenderGroupMemberProfile();
            if (memberInfo != null && !TextUtils.isEmpty(memberInfo.getNameCard())) {
                msgInfo.setGroupNameCard(memberInfo.getNameCard());
            }
        }
        msgInfo.setMsgTime(timMessage.timestamp());
        msgInfo.setSelf(sender.equals(TIMManager.getInstance().getLoginUser()));

        TIMElemType type = ele.getType();
        if (type == TIMElemType.Custom) {
            TIMCustomElem customElem = (TIMCustomElem) ele;
            String data = new String(customElem.getData());
            if (data.equals(GROUP_CREATE)) {
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_CREATE);
                msgInfo.setExtra(new String(customElem.getExt()));

                //获取名字


//                UserInfoController.getInstance().getUsersProfile(msgInfo.getFromUser(), true, new ImCallBack() {
//                    @Override
//                    public void onError(int var1, String var2) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(Object var1) {
//
//                        List<TIMUserProfile> timUserProfiles = (List<TIMUserProfile>) var1;
//
//                        if (null != timUserProfiles && timUserProfiles.size() > 0) {
//                            String message = TUIKitConstants.covert2HTMLString(
//                                    TextUtils.isEmpty(msgInfo.getGroupNameCard())
//                                            ? timUserProfiles.get(0).getNickName()
//                                            : msgInfo.getGroupNameCard()) + "创建圈子";
//                            msgInfo.setExtra(message);
//
//                        }
//
//                    }
//                });


            } else if (data.equals(GROUP_DELETE)) {
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_DELETE);
                msgInfo.setExtra(new String(customElem.getExt()));
            } else if (data.equals(GROUP_UPLOAD_FILE)) {
                //文件
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_UPLOAD_FILE);
                msgInfo.setExtra(new String(customElem.getExt()));


//                UserInfoController.getInstance().getUsersProfile(msgInfo.getFromUser(), true, new ImCallBack() {
//                    @Override
//                    public void onError(int var1, String var2) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(Object var1) {
//
//                        List<TIMUserProfile> timUserProfiles = (List<TIMUserProfile>) var1;
//
//                        if (null != timUserProfiles && timUserProfiles.size() > 0) {
//                            String message = TUIKitConstants.covert2HTMLString(
//                                    TextUtils.isEmpty(msgInfo.getGroupNameCard())
//                                            ? timUserProfiles.get(0).getNickName()
//                                            : msgInfo.getGroupNameCard()) + "上传文件";
//                            msgInfo.setExtra(message);
//
//                        }
//
//                    }
//                });
//


            } else {
                if (isTyping(customElem.getData())) {
                    // 忽略正在输入，它不能作为真正的消息展示
                    return null;
                }
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_CUSTOM);
                // msgInfo.setExtra("[自定义消息-----]");

                int count = timMessage.getElementCount();
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < count; ++i) {
                    TIMElem elem = timMessage.getElement(i);
                    if (elem != null) {
                        //  builder.append("\n\t\t{").append("Type:").append(elem.getType());
                        if (elem.getType() == TIMElemType.Text) {
                            TIMTextElem textElem = (TIMTextElem) elem;
                            //  builder.append(", Content:").append(textElem.getText());
                        } else if (elem.getType() == TIMElemType.Custom) {

                            TIMCustomElem timCustomElem = (TIMCustomElem) elem;
                            builder.append(new String(timCustomElem.getData()));
                            String msgData = builder.toString();

                            //设置自定义消息显示
                            CustomMessage customMessage = JsonUtils.fromJson(msgData, CustomMessage.class);
                            if (null != customMessage) {

                                if (customMessage.getVersion() == 4) {


                                    msgInfo.setExtra("[视频通话]");


                                } else {
                                    switch (customMessage.getAction()) {
                                        case CustomMessage.CUSTOM_BUSINESS_CARD:
                                            msgInfo.setExtra("[名片]");
                                            break;

                                        case CustomMessage.CUSTOM_HOUSING:
                                            msgInfo.setExtra("[房源]");
                                            break;
                                        case CustomMessage.VIDEO_CALL_ACTION_DIALING:
                                        case CustomMessage.VIDEO_CALL_ACTION_SPONSOR_CANCEL:
                                        case CustomMessage.VIDEO_CALL_ACTION_REJECT:

                                        case CustomMessage.VIDEO_CALL_ACTION_SPONSOR_TIMEOUT:
                                        case CustomMessage.VIDEO_CALL_ACTION_ACCEPTED:
                                        case CustomMessage.VIDEO_CALL_ACTION_HANGUP:
                                        case CustomMessage.VIDEO_CALL_ACTION_LINE_BUSY:
                                        case CustomMessage.SWITCH_AUDIO:

                                            msgInfo.setExtra("[音频通话]");

                                            break;


                                        case CustomMessage.CUSTOM_AIT:
                                            //@圈选消息
                                            String str = customMessage.getContent();
                                            if (null != str && str.length() > 0) {
                                                CustomContentDto customContentDto = JsonUtils.fromJson(customMessage.getContent(), CustomContentDto.class);
                                                if (null != customContentDto && null != customContentDto.getAction12Text()) {
                                                    msgInfo.setExtra("" + customContentDto.getAction12Text());
                                                } else {
                                                    msgInfo.setExtra("");
                                                }


                                            }
                                            break;

                                        case CustomMessage.CUSTOM_ARTICLE:
                                            msgInfo.setExtra("[文章]");

                                        case CustomMessage.CUSTOM_CARD_REVIEW:
                                            msgInfo.setExtra("[小区报告]");
                                        case CustomMessage.CUSTOM_CARD_SEND_REVIEW:
                                            msgInfo.setExtra("[小区报告]");
                                        case CustomMessage.CUSTOM_CARD_REVIEW_READ:
                                            msgInfo.setExtra("[小区报告]");
                                            break;
                                    }

                                }


                            }


                        }


                    }
                }

//                LogUtils.w("customElem.getData()-----\n--------" + timMessage.toString());
//                LogUtils.w("builder.toString(-----\n-----" + builder.toString() + "\n---" + timMessage.toString());


            }
        } else if (type == TIMElemType.GroupTips) {

            TIMGroupTipsElem groupTips = (TIMGroupTipsElem) ele;
            TIMGroupTipsType tipsType = groupTips.getTipsType();
            String user = "";
            if (groupTips.getChangedGroupMemberInfo().size() > 0) {
                Object ids[] = groupTips.getChangedGroupMemberInfo().keySet().toArray();
                for (int i = 0; i < ids.length; i++) {
                    user = user + ids[i].toString();
                    if (i != 0)
                        user = "，" + user;
                    if (i == 2 && ids.length > 3) {
                        user = user + "等";
                        break;
                    }
                }

            } else {
                user = groupTips.getOpUserInfo().getIdentifier();
            }
            String message = TUIKitConstants.covert2HTMLString(user);
            if (tipsType == TIMGroupTipsType.Join) {
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_JOIN);
                message = message + "加入圈子";
            }
            if (tipsType == TIMGroupTipsType.Quit) {
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_QUITE);
                message = message + "退出圈子";
            }
            if (tipsType == TIMGroupTipsType.Kick) {
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_KICK);
                message = message + "被踢出圈子";
            }
            if (tipsType == TIMGroupTipsType.SetAdmin) {
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                message = message + "被设置管理员";
            }
            if (tipsType == TIMGroupTipsType.CancelAdmin) {
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                message = message + "被取消管理员";
            }
            if (tipsType == TIMGroupTipsType.ModifyGroupInfo) {
                List<TIMGroupTipsElemGroupInfo> modifyList = groupTips.getGroupInfoList();
                for (int i = 0; i < modifyList.size(); i++) {
                    TIMGroupTipsElemGroupInfo modifyInfo = modifyList.get(i);
                    TIMGroupTipsGroupInfoType modifyType = modifyInfo.getType();
                    if (modifyType == TIMGroupTipsGroupInfoType.ModifyName) {
                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NAME);
                        message = message + "修改圈名称为\"" + modifyInfo.getContent() + "\"";
                    } else if (modifyType == TIMGroupTipsGroupInfoType.ModifyNotification) {
                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                        message = message + "修改圈公告为\"" + modifyInfo.getContent() + "\"";
                    } else if (modifyType == TIMGroupTipsGroupInfoType.ModifyOwner) {
                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                        message = message + "转让圈主给\"" + modifyInfo.getContent() + "\"";
                    } else if (modifyType == TIMGroupTipsGroupInfoType.ModifyFaceUrl) {
                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                        message = message + "修改了圈头像";
                    } else if (modifyType == TIMGroupTipsGroupInfoType.ModifyIntroduction) {
                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                        message = message + "修改圈介绍为\"" + modifyInfo.getContent() + "\"";
                    }
                    if (i < modifyList.size() - 1) {
                        message = message + "、";
                    }
                }
            }
            if (tipsType == TIMGroupTipsType.ModifyMemberInfo) {
                List<TIMGroupTipsElemMemberInfo> modifyList = groupTips.getMemberInfoList();
                if (modifyList.size() > 0) {
                    long shutupTime = modifyList.get(0).getShutupTime();
                    if (shutupTime > 0) {
                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                        message = message + "被禁言\"" + DateTimeUtil.formatSeconds(shutupTime) + "\"";
                    } else {
                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                        message = message + "被取消禁言";
                    }
                }
            }
            if (TextUtils.isEmpty(message)) {
                return null;
            }
            msgInfo.setExtra(message);


//            final TIMGroupTipsElem groupTips = (TIMGroupTipsElem) ele;
//            TIMGroupTipsType tipsType = groupTips.getTipsType();
//            //存放名字
//            List<String> imIsd = new ArrayList<>();
//            String user = "";
//            if (groupTips.getChangedGroupMemberInfo().size() > 0) {
//                final Object ids[] = groupTips.getChangedGroupMemberInfo().keySet().toArray();
//                for (int i = 0; i < ids.length; i++) {
//                    imIsd.add(ids[i].toString());
//
//
//                    user = user + ids[i].toString();
//                    if (i != 0)
//                        user = "，" + user;
//                    if (i == 2 && ids.length > 3) {
//                        user = user + "等";
//                        break;
//                    }
//
//
//                }
//
//
//                UserInfoController.getInstance().getUsersProfile(imIsd, true, new ImCallBack() {
//                    @Override
//                    public void onError(int var1, String var2) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(Object var1) {
//
//
//                        String userName = "";
//                        if (null != var1) {
//
//                            List<TIMUserProfile> timUserProfiles = (List<TIMUserProfile>) var1;
//
//                            if (null != timUserProfiles && timUserProfiles.size() > 0) {
//                                Map<String, String> userNameMap = new HashMap<>();
//                                for (int i = 0; i < timUserProfiles.size(); i++) {
//                                    if ("".equals(timUserProfiles.get(i).getNickName())) {
//                                        userName = "用户: " + timUserProfiles.get(i).getIdentifier();
//                                    } else {
//                                        userName = timUserProfiles.get(i).getNickName();
//                                    }
//
//                                    userNameMap.put(timUserProfiles.get(i).getIdentifier(), timUserProfiles.get(i).getNickName());
//
//                                    if (i != 0) {
//                                        userName = "，" + userName;
//                                    }
//                                    if (i == 2 && timUserProfiles.size() > 3) {
//                                        userName = userName + "等";
//                                        break;
//                                    }
//
//
//                                }
//                                setGuoupMsgStyle(msgInfo, ele, userName, userNameMap);
//
//                            }
//
//
//                        }
//
//                    }
//                });
//
//
//                //一个人
//            } else {
//                if ("".equals(groupTips.getOpUserInfo().getNickName())) {
//                    Map<String, String> userNameMap = new HashMap<>();
//                    String userId = groupTips.getOpUserInfo().getIdentifier();
//                    setGuoupMsgStyle(msgInfo, ele, userId, userNameMap);
//
//
//                    GroupController.getInstance().getGroupMemberList(groupTips.getGroupId(), V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ALL, 0, new ImCallBack() {
//                        @Override
//                        public void onError(int var1, String var2) {
//                            Map<String, String> userNameMap = new HashMap<>();
//                            String userId = groupTips.getOpUserInfo().getIdentifier();
//                            setGuoupMsgStyle(msgInfo, ele, userId, userNameMap);
//
//                        }
//
//                        @Override
//                        public void onSuccess(Object var1) {
//
//                            Map<String, String> userNameMap = new HashMap<>();
//                            V2TIMGroupMemberInfoResult v2TIMGroupMemberInfoResult = (V2TIMGroupMemberInfoResult) var1;
//
//                            if (null != v2TIMGroupMemberInfoResult && null != v2TIMGroupMemberInfoResult.getMemberInfoList() && v2TIMGroupMemberInfoResult.getMemberInfoList().size() > 0) {
//
//                                for (V2TIMGroupMemberFullInfo v2TIMGroupMemberFullInfo : v2TIMGroupMemberInfoResult.getMemberInfoList()) {
//                                    userNameMap.put(v2TIMGroupMemberFullInfo.getUserID(), v2TIMGroupMemberFullInfo.getNickName());
//                                }
//
//                                String userId = groupTips.getOpUserInfo().getIdentifier();
//                                setGuoupMsgStyle(msgInfo, ele, userId, userNameMap);
//
//                            } else {
//
//                                String userId = groupTips.getOpUserInfo().getIdentifier();
//                                setGuoupMsgStyle(msgInfo, ele, userId, userNameMap);
//
//                            }
//
//
//                        }
//                    });
//
//
//                } else {
//
//
//                    GroupController.getInstance().getGroupMemberList(groupTips.getGroupId(), V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ALL, 0, new ImCallBack() {
//                        @Override
//                        public void onError(int var1, String var2) {
//
//                            Map<String, String> userNameMap = new HashMap<>();
//                            String userName = groupTips.getOpUserInfo().getNickName();
//
//
//                            setGuoupMsgStyle(msgInfo, ele, userName, userNameMap);
//                        }
//
//                        @Override
//                        public void onSuccess(Object var1) {
//
//                            Map<String, String> userNameMap = new HashMap<>();
//                            V2TIMGroupMemberInfoResult v2TIMGroupMemberInfoResult = (V2TIMGroupMemberInfoResult) var1;
//
//                            if (null != v2TIMGroupMemberInfoResult && null != v2TIMGroupMemberInfoResult.getMemberInfoList() && v2TIMGroupMemberInfoResult.getMemberInfoList().size() > 0) {
//
//                                for (V2TIMGroupMemberFullInfo v2TIMGroupMemberFullInfo : v2TIMGroupMemberInfoResult.getMemberInfoList()) {
//                                    userNameMap.put(v2TIMGroupMemberFullInfo.getUserID(), v2TIMGroupMemberFullInfo.getNickName());
//                                }
//
//
//                                String userName = groupTips.getOpUserInfo().getNickName();
//
//
//                                setGuoupMsgStyle(msgInfo, ele, userName, userNameMap);
//
//                            } else {
//
//                                String userName = groupTips.getOpUserInfo().getNickName();
//
//
//                                setGuoupMsgStyle(msgInfo, ele, userName, userNameMap);
//                            }
//
//
//                        }
//                    });
//
////
////                    Map<String, String> userNameMap = new HashMap<>();
////                    String userName = groupTips.getOpUserInfo().getNickName();
////
////
////                    setGuoupMsgStyle(msgInfo, ele, userName, userNameMap);
////                    LogUtils.w("userNameMapuserNameMap22222222222"+groupTips.getGroupId());
//                }
//
//            }

//
//            String message = TUIKitConstants.covert2HTMLString(user);
//            if (tipsType == TIMGroupTipsType.Join) {
//                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_JOIN);
//
//                message = message + "加入圈子";
//            }
//            if (tipsType == TIMGroupTipsType.Quit) {
//                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_QUITE);
//                message = message + "退出圈子";
//            }
//            if (tipsType == TIMGroupTipsType.Kick) {
//                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_KICK);
//                message = message + "被踢出圈子";
//            }
//            if (tipsType == TIMGroupTipsType.SetAdmin) {
//                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
//                message = message + "被设置圈秘书";
//            }
//            if (tipsType == TIMGroupTipsType.CancelAdmin) {
//                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
//                message = message + "被取消管理员";
//            }
//            if (tipsType == TIMGroupTipsType.ModifyGroupInfo) {
//                List<TIMGroupTipsElemGroupInfo> modifyList = groupTips.getGroupInfoList();
//                for (int i = 0; i < modifyList.size(); i++) {
//                    TIMGroupTipsElemGroupInfo modifyInfo = modifyList.get(i);
//                    TIMGroupTipsGroupInfoType modifyType = modifyInfo.getType();
//                    if (modifyType == TIMGroupTipsGroupInfoType.ModifyName) {
//                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NAME);
//                        message = message + "修改圈名称为\"" + modifyInfo.getContent() + "\"";
//                    } else if (modifyType == TIMGroupTipsGroupInfoType.ModifyNotification) {
//                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
//                        message = message + "修改圈公告为\"" + modifyInfo.getContent() + "\"";
//                    } else if (modifyType == TIMGroupTipsGroupInfoType.ModifyOwner) {
//                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
//                        message = message + "转让圈主给\"" + modifyInfo.getContent() + "\"";
//                    } else if (modifyType == TIMGroupTipsGroupInfoType.ModifyFaceUrl) {
//                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
//                        message = message + "修改了圈头像";
//                    } else if (modifyType == TIMGroupTipsGroupInfoType.ModifyIntroduction) {
//                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
//                        message = message + "修改圈介绍为\"" + modifyInfo.getContent() + "\"";
//                    }
//                    if (i < modifyList.size() - 1) {
//                        message = message + "、";
//                    }
//                }
//            }
//            if (tipsType == TIMGroupTipsType.ModifyMemberInfo) {
//                List<TIMGroupTipsElemMemberInfo> modifyList = groupTips.getMemberInfoList();
//                if (modifyList.size() > 0) {
//                    long shutupTime = modifyList.get(0).getShutupTime();
//                    if (shutupTime > 0) {
//                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
//                        message = message + "被禁言\"" + DateTimeUtil.formatSeconds(shutupTime) + "\"";
//                    } else {
//                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
//                        message = message + "被取消禁言";
//                    }
//                }
//            }
//            if (TextUtils.isEmpty(message)) {
//                return null;
//            }
//            msgInfo.setExtra(message);
        } else {
            if (type == TIMElemType.Text) {
                TIMTextElem txtEle = (TIMTextElem) ele;
                msgInfo.setExtra(txtEle.getText());
            } else if (type == TIMElemType.Face) {
                TIMFaceElem txtEle = (TIMFaceElem) ele;
                if (txtEle.getIndex() < 1 || txtEle.getData() == null) {
                    TUIKitLog.e("MessageInfoUtil", "txtEle data is null or index<1");
                    return null;
                }
                msgInfo.setExtra("[自定义表情]");


            } else if (type == TIMElemType.Sound) {
                TIMSoundElem soundElemEle = (TIMSoundElem) ele;
                if (msgInfo.isSelf()) {
                    msgInfo.setDataPath(soundElemEle.getPath());
                } else {
                    final String path = TUIKitConstants.RECORD_DOWNLOAD_DIR + soundElemEle.getUuid();
                    File file = new File(path);
                    if (!file.exists()) {
                        soundElemEle.getSoundToFile(path, new TIMCallBack() {
                            @Override
                            public void onError(int code, String desc) {
                                TUIKitLog.e("MessageInfoUtil getSoundToFile", code + ":" + desc);
                            }

                            @Override
                            public void onSuccess() {
                                msgInfo.setDataPath(path);
                            }
                        });
                    } else {
                        msgInfo.setDataPath(path);
                    }
                }
                msgInfo.setExtra("[语音]");
            } else if (type == TIMElemType.Image) {
                TIMImageElem imageEle = (TIMImageElem) ele;
                String localPath = imageEle.getPath();
                if (msgInfo.isSelf() && !TextUtils.isEmpty(localPath)) {
                    msgInfo.setDataPath(localPath);
                    int size[] = ImageUtil.getImageSize(localPath);
                    msgInfo.setImgWidth(size[0]);
                    msgInfo.setImgHeight(size[1]);
                }
                //本地路径为空，可能为更换手机或者是接收的消息
                else {
                    List<TIMImage> imgs = imageEle.getImageList();
                    for (int i = 0; i < imgs.size(); i++) {
                        TIMImage img = imgs.get(i);
                        if (img.getType() == TIMImageType.Thumb) {
                            final String path = TUIKitConstants.IMAGE_DOWNLOAD_DIR + img.getUuid();
                            msgInfo.setImgWidth((int) img.getWidth());
                            msgInfo.setImgHeight((int) img.getHeight());
                            File file = new File(path);
                            if (file.exists()) {
                                msgInfo.setDataPath(path);
                            }
                        }
                    }
                }

                msgInfo.setExtra("[图片]");
            } else if (type == TIMElemType.Video) {
                TIMVideoElem videoEle = (TIMVideoElem) ele;
                if (msgInfo.isSelf() && !TextUtils.isEmpty(videoEle.getSnapshotPath())) {
                    int size[] = ImageUtil.getImageSize(videoEle.getSnapshotPath());
                    msgInfo.setImgWidth(size[0]);
                    msgInfo.setImgHeight(size[1]);
                    msgInfo.setDataPath(videoEle.getSnapshotPath());
                    msgInfo.setDataUri(FileUtil.getUriFromPath(videoEle.getVideoPath()));
                } else {
                    TIMVideo video = videoEle.getVideoInfo();
                    final String videoPath = TUIKitConstants.VIDEO_DOWNLOAD_DIR + video.getUuid();
                    Uri uri = Uri.parse(videoPath);
                    msgInfo.setDataUri(uri);
                    TIMSnapshot snapshot = videoEle.getSnapshotInfo();
                    msgInfo.setImgWidth((int) snapshot.getWidth());
                    msgInfo.setImgHeight((int) snapshot.getHeight());
                    final String snapPath = TUIKitConstants.IMAGE_DOWNLOAD_DIR + snapshot.getUuid();
                    //判断快照是否存在,不存在自动下载
                    if (new File(snapPath).exists()) {
                        msgInfo.setDataPath(snapPath);
                    }
                }

                msgInfo.setExtra("[视频]");
            } else if (type == TIMElemType.File) {
                TIMFileElem fileElem = (TIMFileElem) ele;
                String filename = fileElem.getUuid();
                if (TextUtils.isEmpty(filename)) {
                    filename = System.currentTimeMillis() + fileElem.getFileName();
                }
                final String path = TUIKitConstants.FILE_DOWNLOAD_DIR + filename;
                File file = new File(path);
                if (file.exists()) {
                    if (msgInfo.isSelf()) {
                        msgInfo.setStatus(MessageInfo.MSG_STATUS_SEND_SUCCESS);
                    } else {
                        msgInfo.setStatus(MessageInfo.MSG_STATUS_DOWNLOADED);
                    }
                    msgInfo.setDataPath(path);
                } else {
                    if (msgInfo.isSelf()) {
                        if (TextUtils.isEmpty(fileElem.getPath())) {
                            msgInfo.setStatus(MessageInfo.MSG_STATUS_UN_DOWNLOAD);
                            msgInfo.setDataPath(path);
                        } else {
                            file = new File(fileElem.getPath());
                            if (file.exists()) {
                                msgInfo.setStatus(MessageInfo.MSG_STATUS_SEND_SUCCESS);
                                msgInfo.setDataPath(fileElem.getPath());
                            } else {
                                msgInfo.setStatus(MessageInfo.MSG_STATUS_UN_DOWNLOAD);
                                msgInfo.setDataPath(path);
                            }
                        }
                    } else {
                        msgInfo.setStatus(MessageInfo.MSG_STATUS_UN_DOWNLOAD);
                        msgInfo.setDataPath(path);
                    }
                }
                msgInfo.setExtra("[文件]");
            }
            msgInfo.setMsgType(TIMElemType2MessageInfoType(type));
        }

        if (timMessage.status() == TIMMessageStatus.HasRevoked) {
            msgInfo.setStatus(MessageInfo.MSG_STATUS_REVOKE);
            msgInfo.setMsgType(MessageInfo.MSG_STATUS_REVOKE);
            if (msgInfo.isSelf()) {
                msgInfo.setExtra("您撤回了一条消息");
            } else if (msgInfo.isGroup()) {
                String message = TUIKitConstants.covert2HTMLString(msgInfo.getFromUser());
                msgInfo.setExtra(message + "撤回了一条消息");
            } else {
                msgInfo.setExtra("对方撤回了一条消息");
            }
        } else {
            if (msgInfo.isSelf()) {
                if (timMessage.status() == TIMMessageStatus.SendFail) {
                    msgInfo.setStatus(MessageInfo.MSG_STATUS_SEND_FAIL);
                } else if (timMessage.status() == TIMMessageStatus.SendSucc) {
                    msgInfo.setStatus(MessageInfo.MSG_STATUS_SEND_SUCCESS);
                } else if (timMessage.status() == TIMMessageStatus.Sending) {
                    msgInfo.setStatus(MessageInfo.MSG_STATUS_SENDING);
                }
            }
        }
        return msgInfo;
    }

    private static int TIMElemType2MessageInfoType(TIMElemType type) {
        switch (type) {
            case Text:
                return MessageInfo.MSG_TYPE_TEXT;
            case Image:
                return MessageInfo.MSG_TYPE_IMAGE;
            case Sound:
                return MessageInfo.MSG_TYPE_AUDIO;
            case Video:
                return MessageInfo.MSG_TYPE_VIDEO;
            case File:
                return MessageInfo.MSG_TYPE_FILE;
            case Location:
                return MessageInfo.MSG_TYPE_LOCATION;
            case Face:
                return MessageInfo.MSG_TYPE_CUSTOM_FACE;
            case GroupTips:
                return MessageInfo.MSG_TYPE_TIPS;
        }

        return -1;
    }


//    private static void setGuoupMsgStyle(final MessageInfo msgInfo, final TIMElem ele, String user, Map<String, String> userNameMap) {
//
//        TIMGroupTipsElem groupTips = (TIMGroupTipsElem) ele;
//        TIMGroupTipsType tipsType = groupTips.getTipsType();
//
//
//        String message = TUIKitConstants.covert2HTMLString(user);
//        if (tipsType == TIMGroupTipsType.Join) {
//            msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_JOIN);
//
//            message = message + "加入圈子";
//
//        }
//        if (tipsType == TIMGroupTipsType.Quit) {
//            msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_QUITE);
//            message = message + "退出圈子";
//        }
//        if (tipsType == TIMGroupTipsType.Kick) {
//            msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_KICK);
//            message = message + "被踢出圈子";
//            msgInfo.setOperationId(message);
//        }
//        if (tipsType == TIMGroupTipsType.SetAdmin) {
//            msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
//            message = message + "被设置圈秘书";
//        }
//        if (tipsType == TIMGroupTipsType.CancelAdmin) {
//            msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
//            message = message + "被取消管理员";
//        }
//        if (tipsType == TIMGroupTipsType.ModifyGroupInfo) {
//            List<TIMGroupTipsElemGroupInfo> modifyList = groupTips.getGroupInfoList();
//            for (int i = 0; i < modifyList.size(); i++) {
//                final TIMGroupTipsElemGroupInfo modifyInfo = modifyList.get(i);
//                TIMGroupTipsGroupInfoType modifyType = modifyInfo.getType();
//                if (modifyType == TIMGroupTipsGroupInfoType.ModifyName) {
//                    msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NAME);
//                    message = message + "修改圈名称为\"" + modifyInfo.getContent() + "\"";
//                } else if (modifyType == TIMGroupTipsGroupInfoType.ModifyNotification) {
//                    msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
//                    message = message + "修改圈公告为\"" + modifyInfo.getContent() + "\"";
//                } else if (modifyType == TIMGroupTipsGroupInfoType.ModifyOwner) {
//                    String userName = "";
//                    msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
//                    //   message = message + "转让圈主给\"" + modifyInfo.getContent() + "\"";
//
//                    LogUtils.w("转让圈主给---" + userNameMap.size());
//                    if (null != userNameMap && userNameMap.size() > 0) {
//
//                        userName = userNameMap.get(modifyInfo.getContent());
//                        if (userName.length() > 0) {
//                            message = message + "转让圈主给\"" + userName + "\"";
//                        } else {
//                            message = message + "转让圈主给\"" + modifyInfo.getContent() + "\"";
//                        }
//
//                    } else {
//
//                        message = message + "转让圈主给\"" + modifyInfo.getContent() + "\"";
//                    }
//
//
//                } else if (modifyType == TIMGroupTipsGroupInfoType.ModifyFaceUrl) {
//                    msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
//                    message = message + "修改了圈头像";
//                } else if (modifyType == TIMGroupTipsGroupInfoType.ModifyIntroduction) {
//                    msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
//                    message = message + "修改圈介绍为\"" + modifyInfo.getContent() + "\"";
//                }
//                if (i < modifyList.size() - 1) {
//                    message = message + "、";
//                }
//            }
//        }
//        if (tipsType == TIMGroupTipsType.ModifyMemberInfo) {
//            List<TIMGroupTipsElemMemberInfo> modifyList = groupTips.getMemberInfoList();
//            if (modifyList.size() > 0) {
//                long shutupTime = modifyList.get(0).getShutupTime();
//                if (shutupTime > 0) {
//                    msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
//                    message = message + "被禁言\"" + DateTimeUtil.formatSeconds(shutupTime) + "\"";
//                } else {
//                    msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
//                    message = message + "被取消禁言";
//                }
//            }
//        }
//        if (!TextUtils.isEmpty(message)) {
//            msgInfo.setExtra(message);
//
//        }
//
//
//    }
}
