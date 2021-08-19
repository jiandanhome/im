package com.eju.cy.audiovideo.helper;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.dto.CustomContentDto;
import com.eju.cy.audiovideo.dto.CustomMessage;
import com.eju.cy.audiovideo.dto.StartEndDto;
import com.eju.cy.audiovideo.modules.chat.layout.message.holder.ICustomMessageViewGroup;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.eju.cy.audiovideo.utils.JsonUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 自定义消息
 *
 * @人 圈选
 */
public class CustomAitController {

    private static final String TAG = CustomAitController.class.getSimpleName();


    public static void onDraw(ICustomMessageViewGroup parent, final CustomMessage data, Context context, MessageInfo info) {

        // 把自定义消息view添加到TUIKit内部的父容器里

        View view = LayoutInflater.from(context).inflate(R.layout.im_ait_msg_layout, null, false);
        parent.addMessageContentView(view);


        TextView tv_msg = view.findViewById(R.id.msg_body_tv);


        if (data == null) {


        } else {


            if (null != data.getContent()) {
                CustomContentDto businessCardDto = JsonUtils.fromJson(data.getContent(), CustomContentDto.class);


                if (null != businessCardDto) {

                    //  LogUtils.w("消息是----" + businessCardDto.getAction12Text());
                    //自己发送的字体颜色
                    if (info.isSelf()) {
                        tv_msg.setTextColor(tv_msg.getResources().getColor(R.color.color_FFFFFF));
                        tv_msg.setText(businessCardDto.getAction12Text());
                    } else {
                        //别人@我且消息未读
                        //颜色分段

                        tv_msg.setTextColor(tv_msg.getResources().getColor(R.color.color_23242A));
                        //   if (info.isRead()) {

                        //存储每个被@的人在字符串中的起止位置
                        List<StartEndDto> staEndList = new ArrayList<>();
                        //存放被@的人
                        List<String> aitNameList = new ArrayList<>();


                        if (null != businessCardDto.getAitMap()) {
                            //丢带存放被@的人
                            Map<String, String> aitMap = businessCardDto.getAitMap();

                            Iterator<Map.Entry<String, String>> entries = aitMap.entrySet().iterator();
                            while (entries.hasNext()) {
                                Map.Entry<String, String> entry = entries.next();
                                String aitName = entry.getValue().replace(" ", "");
                                aitNameList.add("@" + aitName);


                            }

                            String str = businessCardDto.getAction12Text();

                            if (str.startsWith("@All")
                                    ||str.startsWith("@所有人")
                                    ||str.startsWith("@ALL")
                                    ||str.startsWith("@ALl")
                                    ||str.startsWith("@AlL")
                                    ||str.startsWith("@all")
                                    ||str.startsWith("@aLl")
                                    ||str.startsWith("@aLL")
                                    ||str.startsWith("@alL")) {

                                SpannableString spannableString = new SpannableString(str);
                                int sta = str.indexOf(str);
                                int end = sta + str.length();
                                 //   LogUtils.w("设置字体颜色" + startEndDto.getStartIndenx() + "\n" + startEndDto.getEndIndenx() + "\n" + businessCardDto.getAction12Text());
                                    spannableString.setSpan(new ForegroundColorSpan(tv_msg.getResources().getColor(R.color.color_366EE3)), sta,end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);


                                tv_msg.setText(spannableString);

                            } else {
                            //遍历被@的人找出在字符串中名字出现的起止位置
                            for (String s : aitNameList) {
                                int sta = str.indexOf(s);
                                int end = sta + s.length();
                                StartEndDto startEndDto = new StartEndDto();

                                startEndDto.setStartIndenx(sta);
                                startEndDto.setEndIndenx(end);
                                staEndList.add(startEndDto);
                            }


                            //设置字体颜色
                            SpannableString spannableString = new SpannableString(str);
//                            for (StartEndDto startEndDto : staEndList) {
//                                LogUtils.w("设置字体颜色" + startEndDto.getStartIndenx() + "\n" + startEndDto.getEndIndenx() + "\n" + businessCardDto.getAction12Text());
//                                spannableString.setSpan(new ForegroundColorSpan(tv_msg.getResources().getColor(R.color.color_366EE3)), startEndDto.getStartIndenx(), startEndDto.getEndIndenx(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//                            }
                                spannableString.setSpan(new ForegroundColorSpan(tv_msg.getResources().getColor(R.color.color_366EE3)),str.indexOf(str), str.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            tv_msg.setText(spannableString);
                        }

                        } else {
                            tv_msg.setText(businessCardDto.getAction12Text());
                        }
//                    } else {
//                        //已读
//                        tv_msg.setText(businessCardDto.getAction12Text());
//                    }

                    }

                }
            }


        }

    }
}
