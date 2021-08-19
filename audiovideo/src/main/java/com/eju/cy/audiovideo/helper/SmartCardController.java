package com.eju.cy.audiovideo.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.component.picture.imageEngine.impl.GlideEngine;
import com.eju.cy.audiovideo.controller.EjuImController;
import com.eju.cy.audiovideo.dto.CustomContentDto;
import com.eju.cy.audiovideo.dto.CustomMessage;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.dto.SendReportDto;
import com.eju.cy.audiovideo.dto.UserLevelDto;
import com.eju.cy.audiovideo.modules.chat.layout.message.holder.ICustomMessageViewGroup;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.eju.cy.audiovideo.net.AppNetInterface;
import com.eju.cy.audiovideo.net.RetrofitManager;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.utils.DateUtil;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.eju.cy.audiovideo.utils.ParameterUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 自定义消息
 * 小区评测报告
 */
public class SmartCardController {

    private static final String TAG = SmartCardController.class.getSimpleName();

    public static void onDraw(ICustomMessageViewGroup parent, final CustomMessage data, Context context) {

        // 把自定义消息view添加到TUIKit内部的父容器里
        View view = LayoutInflater.from(context).inflate(R.layout.card_smart_layout, null, false);
        parent.addMessageContentView(view);


        TextView tv_name = view.findViewById(R.id.tv_card_title);
        TextView tv_stop_date = view.findViewById(R.id.tv_stop_date);


        if (data == null) {
            tv_name.setText("未知");
            tv_stop_date.setText("未知");


        } else {


            if (null != data.getContent()) {

                CustomContentDto businessCardDto = JsonUtils.fromJson(data.getContent(), CustomContentDto.class);

                if (null != businessCardDto) {
                    tv_name.setText(businessCardDto.getTitle());


                    DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                    long time = Long.parseLong(businessCardDto.getStopActionDate());

                    String timeStr = TimeUtils.millis2String(time, fmt);
                    tv_stop_date.setText("截至统计时间" + DateUtil.timeStamp2Date(businessCardDto.getStopActionDate(), null));

                    view.setClickable(true);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            ImActionDto imActionDto = new ImActionDto();
                            imActionDto.setAction(ActionTags.ACTION_CLICK_RVIEW_CARD);
                            imActionDto.setJsonStr(data.getCallback());
                            String cardStr = JsonUtils.toJson(imActionDto);
                            EjuHomeImEventCar.getDefault().post(cardStr);


                        }
                    });
                }

            }


        }

    }


    public static void onReViewDraw(ICustomMessageViewGroup parent, final CustomMessage data, final Context context, final MessageInfo messageInfo) {

        // 把自定义消息view添加到TUIKit内部的父容器里
        final View view = LayoutInflater.from(context).inflate(R.layout.card_smart_review_layout, null, false);
        parent.addMessageContentView(view);

        // LogUtils.w("消息---"+messageInfo.toString());
        TextView tv_name = view.findViewById(R.id.tv_card_title);
        TextView tv_stop_date = view.findViewById(R.id.tv_stop_date);
        final RelativeLayout rl_body = view.findViewById(R.id.rl_body);

        if (data == null) {
            tv_name.setText("未知");
            tv_stop_date.setText("未知");


        } else {


            if (null != data.getContent()) {
                CustomContentDto businessCardDto = JsonUtils.fromJson(data.getContent(), CustomContentDto.class);
                tv_name.setText("我已申请查看本小区的评测报告");
                tv_stop_date.setText("发送报告");


                rl_body.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rl_body.setClickable(false);
                        SendReportDto sendReportDto = JsonUtils.fromJson(data.getCallback(), SendReportDto.class);

                        //https://card.jiandanhome.com/api/v1/lj/message/send_report/

                        //  https:card-tst.jiandanhome.com/api/v1/lj/message/send_report/

                            String url = "";

                        if (EjuImController.getInstance().getIsdebug()) {
                            url = "https://card-tst.jiandanhome.com/api/v1/lj/message/send_report/";
                        } else {
                            url = "https://card.jiandanhome.com/api/v1/lj/message/send_report/";
                        }

                        final AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(context);
                        httpInterface.sendReport(url, ParameterUtils.prepareFormData(sendReportDto.getReport()),
                                ParameterUtils.prepareFormData(sendReportDto.getCommunity_id()),
                                ParameterUtils.prepareFormData(sendReportDto.getClient_id()),
                                ParameterUtils.prepareFormData(sendReportDto.getBroker_id())

                                , ParameterUtils.prepareFormData(sendReportDto.getMf_id()),
                                ParameterUtils.prepareFormData(sendReportDto.getCommunity_name()),
                                ParameterUtils.prepareFormData(sendReportDto.getLj_city_id()))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<UserLevelDto>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(UserLevelDto userLevelDto) {


                                        if ("10000".equals(userLevelDto.getCode())) {


                                            //删除这条消息


                                            ImActionDto imActionDto = new ImActionDto();
                                            imActionDto.setAction(ActionTags.ACTION_CLICK_DELETE_CARD);
                                            imActionDto.setJsonStr(JsonUtils.toJson(messageInfo));
                                            String cardStr = JsonUtils.toJson(imActionDto);
                                            EjuHomeImEventCar.getDefault().post(cardStr);


                                        } else {
                                            rl_body.setClickable(true);
                                        }


                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });


                    }
                });

            }


        }

    }


    public static void onReViewDrawComplete(ICustomMessageViewGroup parent, final CustomMessage data, Context context) {

        // 把自定义消息view添加到TUIKit内部的父容器里
        View view = LayoutInflater.from(context).inflate(R.layout.card_smart_review_complete_layout, null, false);
        parent.addMessageContentView(view);


        TextView tv_name = view.findViewById(R.id.tv_card_title);
        TextView tv_stop_date = view.findViewById(R.id.tv_stop_date);

        tv_stop_date.setText("已发送");
        //  tv_name.setText("已发送");


    }
}
