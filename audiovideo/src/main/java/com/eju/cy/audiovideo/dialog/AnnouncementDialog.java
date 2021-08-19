package com.eju.cy.audiovideo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eju.cy.audiovideo.R;

public class AnnouncementDialog {

    private Context mContext;
    protected Dialog dialog;
    private RelativeLayout mBackgroundLayout;

    private ImageView iv_close;
    private TextView tv_value;

    private Display mDisplay;

    /**
     * dialog  宽度
     */
    private float dialogWidth = 1f;


    public AnnouncementDialog(Context context) {
        this.mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = windowManager.getDefaultDisplay();
    }


    public AnnouncementDialog builder() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.im_announcement_dialog_layout, null);

        mBackgroundLayout = (RelativeLayout) view.findViewById(R.id.rl_background);


        iv_close = (ImageView) view.findViewById(R.id.iv_close);
        tv_value = (TextView) view.findViewById(R.id.tv_value);

        dialog = new Dialog(mContext, R.style.sdk_AlertDialogStyle);
        dialog.setContentView(view);
        mBackgroundLayout.setLayoutParams(new RelativeLayout.LayoutParams((int) (mDisplay.getWidth() * dialogWidth), WindowManager.LayoutParams.MATCH_PARENT));

        return this;
    }


    public AnnouncementDialog setTvValue(String msg) {
        tv_value.setText(msg);

        return this;
    }


    /***
     * 设置取消
     * @param listener
     * @return
     */
    public AnnouncementDialog setNegativeButton(
            final View.OnClickListener listener) {


        iv_close.setOnClickListener(new View.OnClickListener() {
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
    public AnnouncementDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }


    /**
     * 设置是否可以取消
     *
     * @param isCancelOutside
     * @return
     */
    public AnnouncementDialog setCancelOutside(boolean isCancelOutside) {
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
    public AnnouncementDialog setDialogWidth(float dialogWidth) {
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
}
