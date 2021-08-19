package com.eju.cy.audiovideo.component;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.base.ITitleBarLayout;
import com.eju.cy.audiovideo.utils.ScreenUtil;
import com.noober.background.view.BLTextView;


public class TitleBarLayout extends LinearLayout implements ITitleBarLayout {

    private LinearLayout mLeftGroup;
    private LinearLayout mRightGroup;
    private TextView mLeftTitle;
    private TextView mCenterTitle;
    private TextView mRightTitle;
    private ImageView mLeftIcon;
    private ImageView mRightIcon;
    private RelativeLayout mTitleLayout;
    private BLTextView tv_user_role;
    private TextView tv_group_sum;

    public TitleBarLayout(Context context) {
        super(context);
        init();
    }

    public TitleBarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TitleBarLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.title_bar_layout, this);
        mTitleLayout = findViewById(R.id.page_title_layout);
        mLeftGroup = findViewById(R.id.page_title_left_group);
        mRightGroup = findViewById(R.id.page_title_right_group);
        mLeftTitle = findViewById(R.id.page_title_left_text);
        mRightTitle = findViewById(R.id.page_title_right_text);

        tv_group_sum = findViewById(R.id.tv_group_sum);
        mCenterTitle = findViewById(R.id.page_title);
        tv_user_role = findViewById(R.id.tv_user_role);
        mLeftIcon = findViewById(R.id.page_title_left_icon);
        mRightIcon = findViewById(R.id.page_title_right_icon);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mTitleLayout.getLayoutParams();
        params.height = ScreenUtil.getPxByDp(50);
        mTitleLayout.setLayoutParams(params);

    }

    @Override
    public void setOnLeftClickListener(OnClickListener listener) {
        mLeftGroup.setOnClickListener(listener);
    }

    @Override
    public void setOnRightClickListener(OnClickListener listener) {
        mRightGroup.setOnClickListener(listener);
    }

    @Override
    public void setTitle(String title, POSITION position) {
        switch (position) {
            case LEFT:
                mLeftTitle.setText(title);
                break;
            case RIGHT:
                mRightTitle.setText(title);
                break;
            case MIDDLE:
                mCenterTitle.setText(title);
                break;
        }
    }

    @Override
    public LinearLayout getLeftGroup() {
        return mLeftGroup;
    }

    @Override
    public LinearLayout getRightGroup() {
        return mRightGroup;
    }

    @Override
    public ImageView getLeftIcon() {
        return mLeftIcon;
    }

    @Override
    public void setLeftIcon(int resId) {
        mLeftIcon.setImageResource(resId);
    }

    @Override
    public ImageView getRightIcon() {
        return mRightIcon;
    }

    @Override
    public void setRightIcon(int resId) {
        mRightIcon.setImageResource(resId);
    }

    @Override
    public TextView getLeftTitle() {
        return mLeftTitle;
    }

    @Override
    public TextView getMiddleTitle() {
        return mCenterTitle;
    }

    @Override
    public TextView getRightTitle() {
        return mRightTitle;
    }

    /**
     * 设置用户 角色
     *
     * @param userRole
     */
    public void setUserRole(String userRole) {


        if (!TextUtils.isEmpty(userRole) && !"".equals(userRole)) {
            tv_user_role.setText(userRole);
            tv_user_role.setVisibility(VISIBLE);
        } else {

            tv_user_role.setVisibility(GONE);
        }

    }

    public RelativeLayout getTitleLayout() {
        return mTitleLayout;
    }

    public void setTitleBarLayoutBg(Drawable drawable) {
        //setBackgroundColor(getResources().getColor(R.color.status_bar_color));
        mTitleLayout.setBackground(drawable);
    }


    public void setGroupPeopleSum(String peopleSum) {

        if (!TextUtils.isEmpty(peopleSum) && !"".equals(peopleSum)) {
            tv_group_sum.setText(peopleSum);
            tv_group_sum.setVisibility(VISIBLE);
        } else {

            tv_user_role.setVisibility(GONE);
        }

    }
}
