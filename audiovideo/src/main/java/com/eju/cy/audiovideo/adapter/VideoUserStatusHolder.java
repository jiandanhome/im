package com.eju.cy.audiovideo.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eju.cy.audiovideo.R;

public class VideoUserStatusHolder extends RecyclerView.ViewHolder {

    public RelativeLayout user_control_mask;


    public ImageView iv_user_portrait;
    public TextView tv_answer;
    public TextView tv_user_name;

    public VideoUserStatusHolder(@NonNull View v) {
        super(v);

        user_control_mask = (RelativeLayout) v.findViewById(R.id.user_control_mask);


        iv_user_portrait = (ImageView) v.findViewById(R.id.iv_user_portrait);
        tv_answer = (TextView) v.findViewById(R.id.tv_answer);
        tv_user_name = (TextView) v.findViewById(R.id.tv_user_name);
    }
}
