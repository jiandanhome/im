package com.eju.cy.audiovideosample.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eju.cy.audiovideo.component.picture.imageEngine.impl.GlideEngine;
import com.eju.cy.audiovideo.dto.MyGroupDto;
import com.eju.cy.audiovideosample.R;

import java.util.List;

public class MyGroupAdapter extends RecyclerView.Adapter<MyGroupAdapter.ViewHoder> {

    private List<MyGroupDto> myGroupDtoList;
    private RecyclerViewListener recyclerViewListener;

    public MyGroupAdapter(List<MyGroupDto> myGroupDtoList, RecyclerViewListener recyclerViewListener) {
        this.myGroupDtoList = myGroupDtoList;
        this.recyclerViewListener = recyclerViewListener;
    }

    @NonNull
    @Override
    public ViewHoder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_my_group_list, viewGroup, false);
        ViewHoder viewHolder = new ViewHoder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoder holder, int position) {


        MyGroupDto myGroupDto = myGroupDtoList.get(position);
        holder.tv_msg.setText(myGroupDto.getLastMsgAuthor() + " :" + myGroupDto.getLastMsg());
        holder.tv_guoup_name.setText(myGroupDto.getGroupName() + myGroupDto.getMemberNum());

        holder.tv_time.setText(myGroupDto.getLastMsgTime() + "");


        if (myGroupDto.isAitMsg()) {

            holder.tv_msg.setTextColor(holder.itemView.getResources().getColor(R.color.btn_negative_hover));
        } else {
            holder.tv_msg.setTextColor(holder.itemView.getResources().getColor(R.color.black));
        }


        GlideEngine.loadImage(holder.im_portrait, myGroupDto.getGroupFaceUrl());
    }

    @Override
    public int getItemCount() {
        return myGroupDtoList.size();
    }

    public class ViewHoder extends RecyclerView.ViewHolder {
        TextView tv_guoup_name, tv_msg, tv_time;
        ImageView im_portrait;


        public ViewHoder(View itemView) {
            super(itemView);

            tv_guoup_name = itemView.findViewById(R.id.tv_guoup_name);
            tv_msg = itemView.findViewById(R.id.tv_msg);
            tv_time = itemView.findViewById(R.id.tv_time);


            im_portrait = itemView.findViewById(R.id.im_portrait);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    recyclerViewListener.onRecyclerItemClick(getAdapterPosition());
                }
            });

        }
    }
}
