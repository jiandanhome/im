package com.eju.cy.audiovideo.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.component.picture.imageEngine.impl.GlideEngine;
import com.eju.cy.audiovideo.component.picture.imageEngine.impl.PicassoEngine;
import com.eju.cy.audiovideo.dto.CustomContentDto;
import com.eju.cy.audiovideo.dto.CustomMessage;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.modules.chat.layout.message.holder.ICustomMessageViewGroup;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.utils.JsonUtils;

/**
 * 自定义消息
 * 房源
 */
public class CustomHousingController {

    private static final String TAG = CustomHousingController.class.getSimpleName();

    public static void onDraw(ICustomMessageViewGroup parent, final CustomMessage data, Context context) {

        // 把自定义消息view添加到TUIKit内部的父容器里

        View view = LayoutInflater.from(context).inflate(R.layout.im_housing_layout, null, false);
        parent.addMessageContentView(view);


        TextView tv_title = view.findViewById(R.id.tv_title);
        ImageView iv_housing = view.findViewById(R.id.iv_housing);


        if (data == null) {


        } else {


            if (null != data.getContent()) {
                CustomContentDto businessCardDto = JsonUtils.fromJson(data.getContent(), CustomContentDto.class);

                tv_title.setText(businessCardDto.getTitle());
                GlideEngine.loadImage(iv_housing, businessCardDto.getHouseUrl());
               // PicassoEngine.loadImage(iv_housing, businessCardDto.getHouseUrl());

                view.setClickable(true);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


//                        ParameterDto parameterDto = new ParameterDto();
//                        parameterDto.setMsgTag(data.getCallback());
//                        String parameterDtoStr = JsonUtils.toJson(parameterDto);

                        ImActionDto imActionDto = new ImActionDto();
                        imActionDto.setAction(ActionTags.ACTION_CLICK_HOUSING);
                        imActionDto.setJsonStr(data.getCallback());
                        String housingStr = JsonUtils.toJson(imActionDto);
                        EjuHomeImEventCar.getDefault().post(housingStr);


                    }
                });

            }


        }

    }
}
