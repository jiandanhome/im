package com.eju.cy.audiovideo.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.controller.CustomMsgController;
import com.eju.cy.audiovideo.controller.EjuImController;
import com.eju.cy.audiovideo.dto.CustomMessage;
import com.eju.cy.audiovideo.dto.GroupAvCallDto;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.modules.chat.ChatLayout;
import com.eju.cy.audiovideo.modules.chat.layout.input.InputLayout;
import com.eju.cy.audiovideo.modules.chat.layout.inputmore.InputMoreActionUnit;
import com.eju.cy.audiovideo.modules.chat.layout.message.MessageLayout;
import com.eju.cy.audiovideo.modules.chat.layout.message.holder.ICustomMessageViewGroup;
import com.eju.cy.audiovideo.modules.chat.layout.message.holder.IOnCustomMessageDrawListener;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.tags.AppConfig;
import com.eju.cy.audiovideo.trtcs.model.TRTCVideoCallImpl;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.google.gson.Gson;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;

import java.util.ArrayList;
import java.util.List;

import static com.eju.cy.audiovideo.dto.CustomMessage.JSON_VERSION_1_HELLOTIM;


public class ChatLayoutHelper {

    private static final String TAG = ChatLayoutHelper.class.getSimpleName();

    private Context mContext;
    private MessageLayout messageLayout;
    private ChatLayout layout;

    public ChatLayoutHelper(Context context) {
        mContext = context;
    }

    public void customizeChatLayout(final ChatLayout layout) {
        this.layout = layout;

        CustomAVCallUIController.getInstance().setUISender(layout);
        CustomVideoCallUIController.getInstance().setUISender(layout);
        CustomGroupAVCallUIController.getInstance().setUISender(layout);

        CustomMsgController.getInstance().setChatLayout(layout);
        EjuImController.getInstance().setUISender(layout);

//        //====== NoticeLayout使用范例 ======//
//        NoticeLayout noticeLayout = layout.getNoticeLayout();
//        noticeLayout.alwaysShow(true);
//        noticeLayout.getContent().setText("现在插播一条广告");
//        noticeLayout.getContentExtra().setText("参看有奖");
//        noticeLayout.setOnNoticeClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ToastUtil.toastShortMessage("赏白银五千两");
//            }
//        });
// //  this.layout.getTitleBar().setBackgroundColor(layout.getResources().getColor(R.color.btn_negative_hover));
        //====== MessageLayout使用范例 ======//
        this.messageLayout = layout.getMessageLayout();
        this.layout = layout;

        //根据应用设置聊天组件相关
        //聊天消息背景
        Drawable messageLayoutBg = layout.getContext().getResources().getDrawable(R.color.color_F4F4F4);
        //聊天气泡右
        Drawable rightBubble = layout.getContext().getResources().getDrawable(R.drawable.my_message_mf_bg);
        //聊天气泡左
        Drawable leftBubble = layout.getContext().getResources().getDrawable(R.drawable.other_message_mf_bg);

        //titlebg
        Drawable titleBg = layout.getContext().getResources().getDrawable(R.color.white);

        //inputBg
        Drawable inputBg = layout.getContext().getResources().getDrawable(R.color.white);

        int rightChatContentFontColor = layout.getResources().getColor(R.color.color_FFFFFF);
        int leftChatContentFontColor = layout.getResources().getColor(R.color.color_23242A);

        int textSize = 15;
        //美房
        if (AppConfig.appType == 0) {
            messageLayoutBg = layout.getContext().getResources().getDrawable(R.color.color_F4F4F4);
            leftBubble = layout.getContext().getResources().getDrawable(R.drawable.icon_mf_other_message);
            rightBubble = layout.getContext().getResources().getDrawable(R.drawable.icon_mf_my_message);
            titleBg = layout.getContext().getResources().getDrawable(R.color.white);
            inputBg = layout.getContext().getResources().getDrawable(R.color.white);
        } else {
            //易楼
            messageLayoutBg = layout.getContext().getResources().getDrawable(R.color.color_F4F4F4);
            leftBubble = layout.getContext().getResources().getDrawable(R.drawable.other_message_yl_bg);
            rightBubble = layout.getContext().getResources().getDrawable(R.drawable.my_message_yl_bg);
            titleBg = layout.getContext().getResources().getDrawable(R.color.status_bar_color);
            inputBg = layout.getContext().getResources().getDrawable(R.color.status_bar_color);
        }


        ////// 设置聊天背景 //////
        messageLayout.setBackground(messageLayoutBg);
        messageLayout.setRightBubble(rightBubble);
        messageLayout.setLeftBubble(leftBubble);


        messageLayout.setChatContextFontSize(textSize);
        // 设置自己聊天内容字体颜色
        messageLayout.setRightChatContentFontColor(rightChatContentFontColor);
        // 设置朋友聊天内容字体颜色
        messageLayout.setLeftChatContentFontColor(leftChatContentFontColor);

        //设置聊天title  背景
        layout.getTitleBar().setTitleBarLayoutBg(titleBg);
        //设置聊天inputLayout  背景
        layout.getInputLayout().setInputLayoutBg(inputBg);


        //设置头像圆角
        messageLayout.setAvatarRadius(50);

        /*---------------------------------------------------*/
        /*-------------------ChatActivity  Title 设置-------------------------*/
        /*---------------------------------------------------*/
        if (EjuImController.getInstance().getChatAtRightIcon() > 0) {
            this.layout.getTitleBar().setRightIcon(EjuImController.getInstance().getChatAtRightIcon());
        }

        if (EjuImController.getInstance().getChatAtLiftIcon() > 0) {
            this.layout.getTitleBar().setLeftIcon(EjuImController.getInstance().getChatAtLiftIcon());
        }


        // this.layout.getTitleBar().getRightTitle().setText(EjuImController.getInstance().getChatAtRightTitle());


//

//        ////// 设置头像 //////
//        // 设置默认头像，默认与朋友与自己的头像相同
        //messageLayout.setAvatar(R.drawable.ic_more_file);

//        // 设置头像圆角
//        messageLayout.setAvatarRadius(50);
//        // 设置头像大小
//        messageLayout.setAvatarSize(new int[]{48, 48});
//
//        ////// 设置昵称样式（对方与自己的样式保持一致）//////
//        messageLayout.setNameFontSize(12);
//        messageLayout.setNameFontColor(0xFF8B5A2B);
//
//        ////// 设置气泡 ///////
//        // 设置自己聊天气泡的背景
//        messageLayout.setRightBubble(new ColorDrawable(0xFFCCE4FC));
//        // 设置朋友聊天气泡的背景
//        messageLayout.setLeftBubble(new ColorDrawable(0xFFE4E7EB));
//
//        ////// 设置聊天内容 //////
//        // 设置聊天内容字体字体大小，朋友和自己用一种字体大小
//        messageLayout.setChatContextFontSize(15);
//         // 设置自己聊天内容字体颜色
//        messageLayout.setRightChatContentFontColor(layout.getResources().getColor(R.color.color_FFFFFF));
//        // 设置朋友聊天内容字体颜色
//        messageLayout.setLeftChatContentFontColor(layout.getResources().getColor(R.color.color_23242A));
////
//        ////// 设置聊天时间 //////
//        // 设置聊天时间线的背景
//        messageLayout.setChatTimeBubble(new ColorDrawable(0xFFE4E7EB));
//        // 设置聊天时间的字体大小
//        messageLayout.setChatTimeFontSize(12);
//        // 设置聊天时间的字体颜色
//        messageLayout.setChatTimeFontColor(0xFF7E848C);
//
//        ////// 设置聊天的提示信息 //////
//        // 设置提示的背景
//        messageLayout.setTipsMessageBubble(new ColorDrawable(0xFFE4E7EB));
//        // 设置提示的字体大小
//        messageLayout.setTipsMessageFontSize(12);
//        // 设置提示的字体颜色
//        messageLayout.setTipsMessageFontColor(0xFF7E848C);
//
        // 设置自定义的消息渲染时的回调
        messageLayout.setOnCustomMessageDrawListener(new CustomMessageDraw());
//
//        // 新增一个PopMenuAction
//        PopMenuAction action = new PopMenuAction();
//        action.setActionName("test");
//        action.setActionClickListener(new PopActionClickListener() {
//            @Override
//            public void onActionClick(int position, Object data) {
//                ToastUtil.toastShortMessage("新增一个pop action");
//            }
//        });
//        messageLayout.addPopAction(action);
//
//        final MessageLayout.OnItemClickListener l = messageLayout.getOnItemClickListener();
//        messageLayout.setOnItemClickListener(new MessageLayout.OnItemClickListener() {
//            @Override
//            public void onMessageLongClick(View view, int position, MessageInfo messageInfo) {
//                l.onMessageLongClick(view, position, messageInfo);
//                ToastUtil.toastShortMessage("demo中自定义长按item");
//            }
//
//            @Override
//            public void onUserIconClick(View view, int position, MessageInfo messageInfo) {
//                l.onUserIconClick(view, position, messageInfo);
//                ToastUtil.toastShortMessage("demo中自定义点击头像");
//            }
//        });


        //====== InputLayout使用范例 ======//
        InputLayout inputLayout = layout.getInputLayout();

//        // TODO 隐藏音频输入的入口，可以打开下面代码测试
        //inputLayout.disableAudioInput(true);
//        // TODO 隐藏表情输入的入口，可以打开下面代码测试
//        inputLayout.disableEmojiInput(true);
//        // TODO 隐藏更多功能的入口，可以打开下面代码测试
//        inputLayout.disableMoreInput(true);
//        // TODO 可以用自定义的事件来替换更多功能的入口，可以打开下面代码测试
//        inputLayout.replaceMoreInput(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ToastUtil.toastShortMessage("自定义的更多功能按钮事件");
//                MessageInfo info = MessageInfoUtil.buildTextMessage("自定义的消息");
//                layout.sendMessage(info, false);
//            }
//        });
//        // TODO 可以用自定义的fragment来替换更多功能，可以打开下面代码测试
//        inputLayout.replaceMoreInput(new CustomInputFragment().setChatLayout(layout));
//
//        // TODO 可以disable更多面板上的各个功能，可以打开下面代码测试
//        inputLayout.disableCaptureAction(true);
        //  inputLayout.disableSendFileAction(true);
//        inputLayout.disableSendPhotoAction(true);
//        inputLayout.disableVideoRecordAction(true);
        // TODO 可以自己增加一些功能，可以打开下面代码测试


//        layout.getMessageLayout().setTipsMessageBubble();




        /*------------------------------------这里增加一个视频通话---------------------------------*/
        InputMoreActionUnit videoCall = new InputMoreActionUnit();
        if (AppConfig.appType == 0) {
            videoCall.setIconResId(R.drawable.icon_yy_chat);
        } else {
            videoCall.setIconResId(com.eju.cy.audiovideo.R.drawable.ic_more_video);
        }

        videoCall.setTitleId(R.string.audio_call);
        videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ImActionDto actionDto = new ImActionDto();
                actionDto.setAction(ActionTags.ACTION_CLICK_C2C_VOICEL);
                actionDto.setJsonStr(layout.getChatInfo().getType() + "");
                String actionDtoStr = JsonUtils.toJson(actionDto);
                EjuHomeImEventCar.getDefault().post(actionDtoStr);


                //单聊语音通话
                if (layout.getChatInfo().getType() == TIMConversationType.C2C) {


                    //群聊语音通话
                } else if (layout.getChatInfo().getType() == TIMConversationType.Group) {


                    //-------群聊视频通话参数--------
                    List<GroupAvCallDto> groupAvCallDtoList = new ArrayList<>();


                    GroupAvCallDto groupAvCallDto1 = new GroupAvCallDto();
                    groupAvCallDto1.setImId("109027");
                    groupAvCallDto1.setUserNakeName("李四");
                    groupAvCallDto1.setPortraitUrl("http://img.zcool.cn/community/01e0e457b32f640000018c1b402bf2.png@1280w_1l_2o_100sh.png");
                    groupAvCallDto1.setOpenVideoPermissions(true);
                    groupAvCallDtoList.add(groupAvCallDto1);

                    GroupAvCallDto groupAvCallDto = new GroupAvCallDto();
                    groupAvCallDto.setImId("1021145");
                    groupAvCallDto.setUserNakeName("张三");
                    groupAvCallDto.setPortraitUrl("http://img.zcool.cn/community/01e0e457b32f640000018c1b402bf2.png@1280w_1l_2o_100sh.png");
                    groupAvCallDto.setOpenVideoPermissions(true);
                    groupAvCallDtoList.add(groupAvCallDto);

                    //  CustomGroupAVCallUIController.getInstance().createGroupCall(groupAvCallDtoList);


                    List<String> suoyouList = new ArrayList<>();

                    // suoyouList.add("10211425");
                    suoyouList.add("109027");
                    suoyouList.add("10211425");

                    suoyouList.add("1234");
                    suoyouList.add("1235");

                    List<String> quanxianList = new ArrayList<>();
                    quanxianList.add("109027");
                    quanxianList.add("10211425");
                    //  quanxianList.add("1012458");

                    // GroupController.getInstance().groupVideoCall(mContext, suoyouList, quanxianList, layout.getChatInfo().getId());


                }

                //获取双方ID


            }
        });
        inputLayout.addAction(videoCall);



        /*------------------------------------这里增加一个名片---------------------------------*/
        InputMoreActionUnit card = new InputMoreActionUnit();

        if (AppConfig.appType == 0) {
            card.setIconResId(R.drawable.icon_mf_mp);
        } else {
            card.setIconResId(R.drawable.icon_card);
        }

        card.setTitleId(R.string.business_card);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImActionDto imActionDto = new ImActionDto();
                imActionDto.setAction(ActionTags.ACTION_SEND_BUSINESS_CARD);
                String cardStr = JsonUtils.toJson(imActionDto);
                EjuHomeImEventCar.getDefault().post(cardStr);

            }
        });
        inputLayout.addAction(card);

        /*------------------------------------这里增加一个房源---------------------------------*/
        InputMoreActionUnit housing = new InputMoreActionUnit();
        if (AppConfig.appType == 0) {
            housing.setIconResId(R.drawable.icon_mf_fy);
        } else {
            housing.setIconResId(R.drawable.icon_housing);
        }


        housing.setTitleId(R.string.housing);
        housing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ImActionDto imActionDto = new ImActionDto();
                imActionDto.setAction(ActionTags.ACTION_SEND_HOUSING);
                String housingStr = JsonUtils.toJson(imActionDto);
                EjuHomeImEventCar.getDefault().post(housingStr);
            }
        });
        inputLayout.addAction(housing);



        /*----------------------------------------------*/
        /*--控制聊天界面更多按钮点击弹出菜单按钮列表--*/
        /*----------------------------------------------*/
        /*----------------------------------------------*/
        /*--控制聊天界面更多按钮点击弹出菜单按钮列表--*/
        /*----------------------------------------------*/
        //  inputLayout.setActionShow(EjuImController.getInstance().getC2CInputMoreCustomActionList());


    }

    /*------------------------------------渲染自定义消息UI---------------------------------*/
    public class CustomMessageDraw implements IOnCustomMessageDrawListener {

        /**
         * 自定义消息渲染时，会调用该方法，本方法实现了自定义消息的创建，以及交互逻辑
         *
         * @param parent 自定义消息显示的父View，需要把创建的自定义消息view添加到parent里
         * @param info   消息的具体信息
         */
        @Override
        public void onDraw(ICustomMessageViewGroup parent, MessageInfo info) {





            // 获取到自定义消息的json数据
            if (!(info.getElement() instanceof TIMCustomElem)) {
                return;
            }
            TIMCustomElem elem = (TIMCustomElem) info.getElement();
            // 自定义的json数据，需要解析成bean实例
            CustomMessage data = null;
            TRTCVideoCallImpl.CallModel callModel = null;
            try {
                data = new Gson().fromJson(new String(elem.getData()), CustomMessage.class);
                callModel = new Gson().fromJson(new String(elem.getData()), TRTCVideoCallImpl.CallModel.class);
            } catch (Exception e) {
                LogUtils.e(TAG, "invalid json: " + new String(elem.getData()) + " " + e.getMessage());
            }


            if (data == null) {
                LogUtils.e(TAG, "No Custom Data: " + new String(elem.getData()));
            } else if (data.getAction() == CustomMessage.CUSTOM_BUSINESS_CARD) {
                //名片
                CustomBusinessCardController.onDraw(parent, data, mContext);
            } else if (data.getAction() == CustomMessage.CUSTOM_HOUSING) {
                //房源
                CustomHousingController.onDraw(parent, data, mContext);
            } else if (data.getAction() == CustomMessage.CUSTOM_CARD_REVIEW) {
                //评测报告
                SmartCardController.onDraw(parent, data, mContext);
            }
            else if (data.getAction() == CustomMessage.CUSTOM_CARD_SEND_REVIEW) {
                //发送
                SmartCardController.onReViewDraw(parent, data, mContext,info);
            }
            else if (data.getAction() == CustomMessage.CUSTOM_CARD_REVIEW_READ) {
                //以发送
                SmartCardController.onReViewDrawComplete(parent, data, mContext);
            }

            else if (data.getAction() == CustomMessage.CUSTOM_AIT) {
                //@人
                CustomAitController.onDraw(parent, data, mContext, info);
                //分享文章
            } else if (data.getAction() == CustomMessage.CUSTOM_ARTICLE) {
                CustomArticleController.onDraw(parent, data, mContext, info);
            } else if (data.getVersion() == JSON_VERSION_1_HELLOTIM) {
                //CustomHelloTIMUIController.onDraw(parent, data);
            } else if (data.isAudioCall() && data.getVersion() == 3) {
                LogUtils.w("onDraw语音-----");
                CustomAVCallUIController.getInstance().onDraw(parent, data);
            } else if (!data.isAudioCall() && data.getVersion() == 3) {
                LogUtils.w("onDraw视频-----");
                CustomVideoCallUIController.getInstance().onDraw(parent, data);
            } else {
                LogUtils.w(TAG, "unsupported version: " + data.getVersion());


            }


            //视频
            if (null != callModel) {
                // LogUtils.w("视频视频-----" + callModel.toString());


                //视频聊天
                if (callModel.version == 4 && callModel.callType == 2) {
                    CustomGroupAVCallUIController.getInstance().onDraw(parent, callModel);
                }

            }


        }
    }

}
