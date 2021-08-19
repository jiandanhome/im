package com.eju.cy.audiovideo.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.component.picture.imageEngine.impl.GlideEngine;
import com.eju.cy.audiovideo.dto.CustomContentDto;
import com.eju.cy.audiovideo.dto.CustomMessage;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.modules.chat.layout.message.holder.ICustomMessageViewGroup;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.utils.JsonUtils;

/**
 * 自定义消息
 * 分享文章
 */
public class CustomArticleController {

    private static final String TAG = CustomArticleController.class.getSimpleName();

    public static void onDraw(ICustomMessageViewGroup parent, final CustomMessage data, Context context, MessageInfo info) {

        // 把自定义消息view添加到TUIKit内部的父容器里

        View view = LayoutInflater.from(context).inflate(R.layout.im_article_layout, null, false);
        parent.addMessageContentView(view);


        TextView tv_title = view.findViewById(R.id.tv_title);
        ImageView iv_article_url = view.findViewById(R.id.iv_article_url);
        ImageView iv_article_icon = view.findViewById(R.id.iv_article_icon);
        RelativeLayout rl_body = view.findViewById(R.id.rl_body);


        if (data == null) {


        } else {


            if (null != data.getContent()) {
                CustomContentDto businessCardDto = JsonUtils.fromJson(data.getContent(), CustomContentDto.class);

                tv_title.setText(businessCardDto.getTitle());

                if (null != businessCardDto.getContentUrl() && !"".equals(businessCardDto.getContentUrl()) && businessCardDto.getContentUrl().length() > 0) {
                    iv_article_icon.setVisibility(View.GONE);
                    iv_article_url.setVisibility(View.VISIBLE);
                    GlideEngine.loadCornerImage(iv_article_url, businessCardDto.getContentUrl(), null, 8);
                } else {
                    GlideEngine.loadImage(iv_article_url, R.drawable.icon_im_article_link);
                    iv_article_icon.setVisibility(View.VISIBLE);
                    iv_article_url.setVisibility(View.GONE);
                }


                if (null != info) {
                    if (info.isSelf()) {
                        //自己
                        rl_body.setBackground(context.getResources().getDrawable(R.drawable.bg_article_my));
                    } else {
                        //非自己
                        rl_body.setBackground(context.getResources().getDrawable(R.drawable.bg_article_other));
                    }
                }


                view.setClickable(true);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImActionDto imActionDto = new ImActionDto();
                        imActionDto.setAction(ActionTags.ACTION_CLICK_ARTICLE_MESSAGE_VALUE);
                        imActionDto.setJsonStr(data.getCallback());
                        String housingStr = JsonUtils.toJson(imActionDto);
                        EjuHomeImEventCar.getDefault().post(housingStr);


                    }
                });

            }


        }

    }
}
