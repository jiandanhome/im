package com.eju.cy.audiovideo.controller;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eju.cy.audiovideo.dto.CustomContentDto;
import com.eju.cy.audiovideo.dto.CustomMessage;
import com.eju.cy.audiovideo.modules.chat.ChatLayout;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.eju.cy.audiovideo.modules.message.MessageInfoUtil;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMValueCallBack;

/**
 * @ Name: Caochen
 * @ Date: 2020-05-07
 * @ Time: 10:57
 * @ Description：  发送自定义消息--名片--房源
 */
public class CustomMsgController {

    private static CustomMsgController instance;
    private ChatLayout chatLayout;

    public static CustomMsgController getInstance() {
        if (instance == null) {
            synchronized (CustomMsgController.class) {
                if (instance == null) {
                    instance = new CustomMsgController();
                }
            }
        }
        return instance;
    }


    public void setChatLayout(ChatLayout chatLayout) {
        this.chatLayout = chatLayout;

    }


    /**
     * 发送名片
     *
     * @param contentDto 名片所需字段
     * @param callback   业务方自定义消息ID
     */
    public void sendBusinessCard(CustomContentDto contentDto, String callback) {

        if (null != contentDto) {
            CustomMessage customMessage = new CustomMessage();
            String contentStr = JsonUtils.toJson(contentDto);
            customMessage.setAction(CustomMessage.CUSTOM_BUSINESS_CARD);

            customMessage.setCallback(callback);
            customMessage.setContent(contentStr);
            String data = JsonUtils.toJson(customMessage);


            LogUtils.d("名片----------" + data);
            MessageInfo info = MessageInfoUtil.buildCustomMessage(data);
            this.chatLayout.sendMessage(info, false);

        } else {

            ToastUtils.showLong("CustomContentDto 为空，请检查");
        }
    }


    /**
     * 发送房源
     *
     * @param contentDto 房源所需字段
     * @param callback   业务方自定义消息ID
     */
    public void sendHousing(CustomContentDto contentDto, String callback) {


        if (null != contentDto) {
            CustomMessage customMessage = new CustomMessage();
            String contentStr = JsonUtils.toJson(contentDto);
            customMessage.setAction(CustomMessage.CUSTOM_HOUSING);

            customMessage.setCallback(callback);
            customMessage.setContent(contentStr);
            String data = JsonUtils.toJson(customMessage);

            LogUtils.d("房源----------" + data);
            MessageInfo info = MessageInfoUtil.buildCustomMessage(data);
            this.chatLayout.sendMessage(info, false);
        } else {
            ToastUtils.showLong("CustomContentDto 为空，请检查");


        }

    }

    /**
     * 发送房源
     *
     * @param conversationId 对方会话ID
     * @param type           会话类型
     * @param contentDto     房源所需字段
     * @param callback       业务方自定义消息ID
     * @param imCallBack     回调
     */
    public void sendHousing(String conversationId, TIMConversationType type, CustomContentDto contentDto, String callback, final ImCallBack imCallBack) {


        if (null != contentDto) {
            CustomMessage customMessage = new CustomMessage();
            String contentStr = JsonUtils.toJson(contentDto);
            customMessage.setAction(CustomMessage.CUSTOM_HOUSING);

            customMessage.setCallback(callback);
            customMessage.setContent(contentStr);
            String data = JsonUtils.toJson(customMessage);

            LogUtils.d("房源----------" + data);
            MessageInfo info = MessageInfoUtil.buildCustomMessage(data);

            TIMConversation con = TIMManager.getInstance().getConversation(type, conversationId);
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


        } else {
            ToastUtils.showLong("CustomContentDto 为空，请检查");
        }

    }

    /**
     * 发送圈选消息（@消息）
     *
     * @param conversationId 对方会话ID
     * @param type           会话类型
     * @param contentDto     所需字段
     */
    public void sentAit(String conversationId, TIMConversationType type, CustomContentDto contentDto) {

        if (null != contentDto) {

            String contentStr = JsonUtils.toJson(contentDto);


            CustomMessage customMessage = new CustomMessage();
            customMessage.setAction(CustomMessage.CUSTOM_AIT);
            customMessage.setContent(contentStr);


            String data = JsonUtils.toJson(customMessage);
            MessageInfo info = MessageInfoUtil.buildCustomMessage(data);
            LogUtils.w("发送圈选消息----------------" + contentStr.toString());

            this.chatLayout.sendMessage(info, false);

        }
    }


    /**
     * 发送分享的文章
     *
     * @param conversationId 对方会话ID
     * @param type 会话类型
     * @param contentDto 所需字段
     */
    public void sendArticle(String conversationId, TIMConversationType type, CustomContentDto contentDto,String callback ,final ImCallBack imCallBack) {


        if (null != contentDto) {
            String contentStr = JsonUtils.toJson(contentDto);
            CustomMessage customMessage = new CustomMessage();
            customMessage.setAction(CustomMessage.CUSTOM_ARTICLE);

            customMessage.setCallback(callback);
            customMessage.setContent(contentStr);
            String data = JsonUtils.toJson(customMessage);

            LogUtils.d("分享文章----------" + data);
            MessageInfo info = MessageInfoUtil.buildCustomMessage(data);

            TIMConversation con = TIMManager.getInstance().getConversation(type, conversationId);
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


        } else {
            ToastUtils.showLong("分享文章CustomContentDto 为空，请检查");
        }


        /**
         *
         * 示例
         *
         *                     CustomContentDto contentDto = new CustomContentDto();
         *                     contentDto.setTitle("经纪圈如何营销。");
         *                     contentDto.setContentUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1588764928820&di=ac74ee30d71eadfcb6b1985dd2ca322b&imgtype=0&src=http%3A%2F%2Fbpic.588ku.com%2Felement_origin_min_pic%2F00%2F10%2F74%2F3856a4ae2679e6c.jpg");
         *                     CustomMsgController.getInstance().sendArticle("10211425", TIMConversationType.C2C, contentDto, "文章链接地址点击的时候原封不动的返回给你", new ImCallBack() {
         *                         @Override
         *                         public void onError(int var1, String var2) {
         *
         *                         }
         *
         *                         @Override
         *                         public void onSuccess(Object var1) {
         *
         *                         }
         *                     });
         *
         *
         *
         *
         *
         */
    }


}
