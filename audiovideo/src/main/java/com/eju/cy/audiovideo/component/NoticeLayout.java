package com.eju.cy.audiovideo.component;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.base.INoticeLayout;

public class NoticeLayout extends RelativeLayout implements INoticeLayout {

    private TextView mContentText;
    private TextView mContentExtraText, tv_notification;
    private boolean mAwaysShow;


    private LinearLayout ll_to_do, ll_notification;

    public NoticeLayout(Context context) {
        super(context);
        init();
    }

    public NoticeLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoticeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.chat_notice_layout, this);
        mContentText = findViewById(R.id.notice_content);
        mContentExtraText = findViewById(R.id.notice_content_extra);

        ll_to_do = findViewById(R.id.ll_to_do);
        ll_notification = findViewById(R.id.ll_notification);
        tv_notification = findViewById(R.id.tv_notification);
    }


    //显示代办
    public void showToDo() {
        ll_to_do.setVisibility(VISIBLE);
        ll_notification.setVisibility(GONE);


    }

    //显示群通知
    public void showNotification(String notifaication) {
        LogUtils.w("showNotification------"+notifaication);


        if (!"".equals(notifaication)) {
            ll_to_do.setVisibility(GONE);
            ll_notification.setVisibility(VISIBLE);

            tv_notification.setText(notifaication);
            tv_notification.setSelected(true);
        }


    }

    @Override
    public TextView getContent() {
        return mContentText;
    }

    @Override
    public TextView getContentExtra() {
        return mContentExtraText;
    }

    @Override
    public void setOnNoticeClickListener(OnClickListener l) {
        setOnClickListener(l);
    }

    @Override
    public void setVisibility(int visibility) {
        if (mAwaysShow) {
            super.setVisibility(VISIBLE);
        } else {
            super.setVisibility(visibility);
        }
    }

    @Override
    public void alwaysShow(boolean show) {
        mAwaysShow = show;
        if (mAwaysShow) {
            super.setVisibility(VISIBLE);
        }
    }

}
