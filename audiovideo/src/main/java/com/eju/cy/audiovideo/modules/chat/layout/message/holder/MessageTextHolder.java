package com.eju.cy.audiovideo.modules.chat.layout.message.holder;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.component.face.FaceManager;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.eju.cy.audiovideo.video.videolayout.ClickMovementMethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageTextHolder extends MessageContentHolder {

    private TextView msgBodyText;

    public MessageTextHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getVariableLayout() {
        return R.layout.message_adapter_content_text;
    }

    @Override
    public void initVariableViews() {
        msgBodyText = rootView.findViewById(R.id.msg_body_tv);
    }

    @Override
    public void layoutVariableViews(final MessageInfo msg, int position) {
        msgBodyText.setVisibility(View.VISIBLE);


        if (null != msg.getExtra()) {
            FaceManager.handlerEmojiText(msgBodyText, msg.getExtra().toString(), false);
        }

        if (properties.getChatContextFontSize() != 0) {
            msgBodyText.setTextSize(properties.getChatContextFontSize());
        }
        if (msg.isSelf()) {
            if (properties.getRightChatContentFontColor() != 0) {
                msgBodyText.setTextColor(properties.getRightChatContentFontColor());
            }
        } else {
            if (properties.getLeftChatContentFontColor() != 0) {
                msgBodyText.setTextColor(properties.getLeftChatContentFontColor());
            }
        }






//        msgBodyText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ImActionDto imActionDto = new ImActionDto();
//                imActionDto.setAction(ActionTags.ACTION_CLICK_TEXT_MESSAGE_VALUE);
//
//
//                imActionDto.setJsonStr(msg.getExtra().toString());
//
//                if (isHttpUrl(msg.getExtra().toString())) {
//                    EjuHomeImEventCar.getDefault().post(JsonUtils.toJson(imActionDto));
//                }
//            }
//        });

//        msgBodyText.setOnTouchListener(ClickMovementMethod.getInstance());
    }

    /**
     * 是否是网址地址
     * @param urls
     * @return
     */
    public boolean isHttpUrl(String urls) {
        boolean isurl = false;
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式

        Pattern pat = Pattern.compile(regex.trim());//对比
        Matcher mat = pat.matcher(urls.trim());
        isurl = mat.matches();//判断是否匹配
        if (isurl) {
            isurl = true;
        }
        return isurl;
    }

}
