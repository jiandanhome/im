package com.eju.cy.audiovideo.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.component.picture.imageEngine.impl.GlideEngine;
import com.eju.cy.audiovideo.dto.UserStatusDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @ Name: Caochen
 * @ Date: 2020-06-04
 * @ Time: 15:24
 * @ Description：  被邀请参加音视频的人数
 */
public class InviterViewAdapter extends RecyclerView.Adapter<InviterViewAdapter.InviterHolder> {


    protected List<UserStatusDto> mUsers = new ArrayList<>();
    protected LayoutInflater mInflater;

    public InviterViewAdapter(Activity activity, List<UserStatusDto> mUsers) {
        this.mUsers = mUsers;
        mInflater = ((Activity) activity).getLayoutInflater();
    }


    @NonNull
    @Override
    public InviterHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_inviter_layout, viewGroup, false);
        return new InviterHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull InviterHolder audioHolder, int i) {

        GlideEngine.loadImage(audioHolder.iv_user, mUsers.get(i).getPortraitUrl());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class InviterHolder extends RecyclerView.ViewHolder {


        private ImageView iv_user;

        public InviterHolder(@NonNull View itemView) {
            super(itemView);


            iv_user = (ImageView) itemView.findViewById(R.id.iv_user);
        }
    }
}
