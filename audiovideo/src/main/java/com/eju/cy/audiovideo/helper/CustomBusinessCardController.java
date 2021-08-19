package com.eju.cy.audiovideo.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.component.picture.imageEngine.impl.GlideEngine;
import com.eju.cy.audiovideo.dto.CustomContentDto;
import com.eju.cy.audiovideo.dto.CustomMessage;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.dto.ParameterDto;
import com.eju.cy.audiovideo.modules.chat.layout.message.holder.ICustomMessageViewGroup;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.utils.JsonUtils;

/**
 * 自定义消息
 * 名片
 */
public class CustomBusinessCardController {

    private static final String TAG = CustomBusinessCardController.class.getSimpleName();

    public static void onDraw(ICustomMessageViewGroup parent, final CustomMessage data ,Context context) {

        // 把自定义消息view添加到TUIKit内部的父容器里
        View view = LayoutInflater.from(context).inflate(R.layout.im_business_card_layout, null, false);
        parent.addMessageContentView(view);


        TextView tv_name = view.findViewById(R.id.tv_name);
        TextView tv_phone = view.findViewById(R.id.tv_phone);


        ImageView iv_portrait = view.findViewById(R.id.iv_portrait);


        TextView im_tv_agent_address = view.findViewById(R.id.im_tv_agent_address);

        TextView tv_slogan = view.findViewById(R.id.tv_slogan);


        TextView   tv_name_and_phone = view.findViewById(R.id.tv_name_and_phone);

        if (data == null) {
            tv_name.setText("未知");
            tv_phone.setText("未知");


            im_tv_agent_address.setText("未知");
            tv_slogan.setText("未知");


        } else {


            if (null != data.getContent()) {
                CustomContentDto businessCardDto = JsonUtils.fromJson(data.getContent(), CustomContentDto.class);
                tv_name.setText(businessCardDto.getCardName());
                tv_phone.setText(businessCardDto.getPhoneNumber());

                im_tv_agent_address.setText(businessCardDto.getShopName() + " " + businessCardDto.getShopAddress());
                tv_slogan.setText(businessCardDto.getSlogan());

                tv_name_and_phone.setText(businessCardDto.getCardName()+"|"+businessCardDto.getPhoneNumber());

                GlideEngine.loadImageCircular(iv_portrait,businessCardDto.getPortraitUrl());



                view.setClickable(true);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        ParameterDto parameterDto = new ParameterDto();
//                        parameterDto.setMsgTag(data.getCallback());
//                        String parameterDtoStr = JsonUtils.toJson(parameterDto);

                        ImActionDto imActionDto = new ImActionDto();
                        imActionDto.setAction(ActionTags.ACTION_CLICK_BUSINESS_CARD);
                        imActionDto.setJsonStr(data.getCallback());
                        String cardStr = JsonUtils.toJson(imActionDto);
                        EjuHomeImEventCar.getDefault().post(cardStr);


                    }
                });

            }


        }

    }
}
