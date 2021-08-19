//package com.eju.cy.audiovideo.adapter;
//
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.blankj.utilcode.util.LogUtils;
//import com.eju.cy.audiovideo.R;
//import com.eju.cy.audiovideo.component.picture.imageEngine.impl.GlideEngine;
//import com.eju.cy.audiovideo.dto.GroupAvCallDto;
//import com.tencent.rtmp.ui.TXCloudVideoView;
//import com.tencent.trtc.TRTCCloud;
//
//import java.util.List;
//
//public class GroupVideoCallAdapter extends RecyclerView.Adapter<GroupVideoCallAdapter.Holder> implements View.OnClickListener {
//
//
//    private List<GroupAvCallDto> mData;
//
//    public GroupVideoCallAdapter(List<GroupAvCallDto> mData, TRTCCloud trtcCloud) {
//        this.mData = mData;
//        this.trtcCloud = trtcCloud;
//    }
//
//    private TRTCCloud trtcCloud;
//
//    public GroupVideoCallAdapter(List<GroupAvCallDto> mData) {
//        this.mData = mData;
//    }
//
//
//
//
//    @NonNull
//    @Override
//    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//
//        View view = View.inflate(viewGroup.getContext(), R.layout.item_group_video_layout, null);
//        view.setOnClickListener(this);
//
//        Holder holder = new Holder(view);
//
//
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull Holder holder, int i) {
//
//        GroupAvCallDto groupAvCallDto = this.mData.get(i);
//
//
//        holder.tv_user_name.setText("" + groupAvCallDto.getUserNakeName());
//
//        //渲染流---代做
//
//        if (groupAvCallDto.isOpenVideoPermissions() && groupAvCallDto.isOpenVideo()) {
//            //渲染
//            LogUtils.w("渲染渲染渲染渲染");
//
//        } else {
//            LogUtils.w("停止接收渲染停止接收渲染");
//            //停止接收渲染
//          //  trtcCloud.stopRemoteView(groupAvCallDto.getImId());
//            GlideEngine.loadImage(holder.iv_user_portrait, groupAvCallDto.getPortraitUrl());
//        }
//
//        if (groupAvCallDto.isAnswer()) {
//            holder.tv_answer.setVisibility(View.GONE);
//        } else {
//            holder.tv_answer.setText("暂未接听");
//            holder.tv_answer.setVisibility(View.VISIBLE);
//        }
//
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return this.mData.size();
//    }
//
//    @Override
//    public void onClick(View v) {
//
//    }
//
//    public class Holder extends RecyclerView.ViewHolder {
//
//        private ImageView iv_user_portrait;
//        private TextView tv_user_name, tv_answer;
//
//
//        public Holder(View itemView) {
//            super(itemView);
//
//
//            iv_user_portrait = itemView.findViewById(R.id.iv_user_portrait);
//            tv_user_name = itemView.findViewById(R.id.tv_user_name);
//            tv_answer = itemView.findViewById(R.id.tv_answer);
//
//        }
//    }
//}
