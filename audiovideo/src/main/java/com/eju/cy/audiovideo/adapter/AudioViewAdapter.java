package com.eju.cy.audiovideo.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.component.picture.imageEngine.impl.GlideEngine;
import com.eju.cy.audiovideo.dto.UserStatusDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @ Name: Caochen
 * @ Date: 2020-06-04
 * @ Time: 15:24
 * @ Description： 没视频权限用户
 */
public class AudioViewAdapter extends RecyclerView.Adapter<AudioViewAdapter.AudioHolder> {


    protected List<UserStatusDto> mUsers = new ArrayList<>();
    protected LayoutInflater mInflater;

    public AudioViewAdapter(Activity activity, List<UserStatusDto> mUsers) {
        this.mUsers = mUsers;
        mInflater = ((Activity) activity).getLayoutInflater();
    }


    @NonNull
    @Override
    public AudioHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_audio_layout, viewGroup, false);
        return new AudioHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull AudioHolder audioHolder, int i) {
        audioHolder.tv_user_name.setText("" + mUsers.get(i).getUserNakeName());
        GlideEngine.loadImage(audioHolder.iv_user_portrait, mUsers.get(i).getPortraitUrl());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class AudioHolder extends RecyclerView.ViewHolder {

        private TextView tv_user_name;
        private ImageView iv_user_portrait;

        public AudioHolder(@NonNull View itemView) {
            super(itemView);

            tv_user_name = (TextView) itemView.findViewById(R.id.tv_user_name);
            iv_user_portrait = (ImageView) itemView.findViewById(R.id.iv_user_portrait);
        }
    }
}
