package com.eju.cy.audiovideo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.helper.CustomAVCallUIController;
import com.eju.cy.audiovideo.helper.CustomVideoCallUIController;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.utils.JsonUtils;

public class SelectCallDialog implements View.OnClickListener {

    private Context mContext;
    protected Dialog dialog;
    private RelativeLayout rl_video, rl_audio;
    private TextView tv_close;
    private RelativeLayout mBackgroundLayout;
    private Display mDisplay;


    /**
     * dialog  宽度
     */
    private float dialogWidth = 1f;

    public SelectCallDialog(Context context) {
        this.mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = windowManager.getDefaultDisplay();
    }


    public SelectCallDialog builder() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_select_call_layout, null);

        mBackgroundLayout = (RelativeLayout) view.findViewById(R.id.rl_background);
        rl_video = (RelativeLayout) view.findViewById(R.id.rl_video);
        rl_audio = (RelativeLayout) view.findViewById(R.id.rl_audio);
        tv_close = (TextView) view.findViewById(R.id.tv_close);
        mBackgroundLayout = (RelativeLayout) view.findViewById(R.id.rl_background);
        rl_video.setOnClickListener(this);
        rl_audio.setOnClickListener(this);
        tv_close.setOnClickListener(this);

        dialog = new Dialog(mContext, R.style.sdk_AlertDialogStyle);
        dialog.setContentView(view);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        mBackgroundLayout.setLayoutParams(new RelativeLayout.LayoutParams((int) (mDisplay.getWidth() * dialogWidth), WindowManager.LayoutParams.MATCH_PARENT));

        return this;
    }


    public SelectCallDialog setTvValue(String msg) {

        return this;
    }


    /***
     * 设置取消
     * @param listener
     * @return
     */
    public SelectCallDialog setNegativeButton(
            final View.OnClickListener listener) {


        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }


    /***
     * 是否点击返回能够取消
     * @param cancel
     * @return
     */
    public SelectCallDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }


    /**
     * 设置是否可以取消
     *
     * @param isCancelOutside
     * @return
     */
    public SelectCallDialog setCancelOutside(boolean isCancelOutside) {
        dialog.setCanceledOnTouchOutside(isCancelOutside);
        return this;
    }


    /***
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     * @param dpValue
     * @return
     */
    public int dp2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int px2dip(float pxValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public int px2sp(float pxValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public int sp2px(float spValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 设置dialog  宽度
     *
     * @param dialogWidth
     * @return
     */
    public SelectCallDialog setDialogWidth(float dialogWidth) {
        if (mBackgroundLayout != null) {
            mBackgroundLayout.setLayoutParams(new FrameLayout.LayoutParams((int) (mDisplay.getWidth() * dialogWidth), LinearLayout.LayoutParams.WRAP_CONTENT));
        }
        this.dialogWidth = dialogWidth;
        return this;
    }


    public void show() {

        dialog.show();
    }


    public void dismiss() {
        dialog.dismiss();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.tv_close) {
            dismiss();
        } else if (v.getId() == R.id.rl_video) {


            ImActionDto imActionDto = new ImActionDto();
            imActionDto.setAction(ActionTags.C2C_VOICE_CALL);
            String voiceCall = JsonUtils.toJson(imActionDto);
            EjuHomeImEventCar.getDefault().post(voiceCall);
            CustomVideoCallUIController.getInstance().createC2cVideoCall();
            dismiss();



        } else if (v.getId() == R.id.rl_audio) {

            ImActionDto imActionDto = new ImActionDto();
            imActionDto.setAction(ActionTags.C2C_VOICE_CALL);
            String voiceCall = JsonUtils.toJson(imActionDto);
            EjuHomeImEventCar.getDefault().post(voiceCall);
            CustomAVCallUIController.getInstance().createC2cVideoCall();
            dismiss();
        }


    }
}
