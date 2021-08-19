package com.eju.cy.audiovideo.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.component.picture.imageEngine.impl.GlideEngine;
import com.eju.cy.audiovideo.dto.UserStatusDto;
import com.eju.cy.audiovideo.tags.AppConfig;
import com.eju.cy.audiovideo.trtcs.model.ITRTCVideoCall;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected LayoutInflater mInflater;
    protected Context mContext;

    protected List<UserStatusDto> mUsers = new ArrayList<>();

    protected String mLocalUid;


    //Item 宽高
    protected int mItemWidth;
    protected int mItemHeight;


    private ITRTCVideoCall trtcCloud;
    private Map<String, Integer> paly = new HashMap<>();


    public VideoViewAdapter(Activity activity, String localUid, List<UserStatusDto> mData, ITRTCVideoCall trtcCloud) {
        mInflater = ((Activity) activity).getLayoutInflater();
        mContext = ((Activity) activity).getApplicationContext();

        mLocalUid = localUid;
        mUsers = mData;
        this.trtcCloud = trtcCloud;

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewtype) {

        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        View view;

        if (viewtype == 0) {
            //视频开启
            view = mInflater.inflate(R.layout.item_video_view_container, parent, false);
            return new VideoUserStatusHolder(view);
        } else {
            //视频没未开启
            view = mInflater.inflate(R.layout.item_group_video_layout, parent, false);
            return new VideoUserStatusHolderHead(view);
        }

    }


    @Override
    public int getItemViewType(int position) {
        if (mUsers.get(position).isOpenVideoPermissions() && mUsers.get(position).isAnswer() && mUsers.get(position).isOpenVideo()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof VideoUserStatusHolder) {
            ((VideoUserStatusHolder) viewHolder).setData();
        } else {
            ((VideoUserStatusHolderHead) viewHolder).setData();
        }


    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    /**
     * @param view
     */
    public void stripView(TXCloudVideoView view) {
        LogUtils.w("清除数据");
        if (null != view) {
            ViewParent parent = view.getParent();
            if (parent != null) {
                LogUtils.w("开始清除");
                ((FrameLayout) parent).removeView(view);
            }
        }
    }


    public class VideoUserStatusHolderHead extends RecyclerView.ViewHolder {

        public RelativeLayout user_control_mask;


        public ImageView iv_user_portrait;
        public TextView tv_answer;
        public TextView tv_user_name;

        public VideoUserStatusHolderHead(@NonNull View v) {
            super(v);

            user_control_mask = (RelativeLayout) v.findViewById(R.id.user_control_mask);
            iv_user_portrait = (ImageView) v.findViewById(R.id.iv_user_portrait);
            tv_answer = (TextView) v.findViewById(R.id.tv_answer);
            tv_user_name = (TextView) v.findViewById(R.id.tv_user_name);
            user_control_mask.setVisibility(View.VISIBLE);
        }

        public void setData() {
            UserStatusDto user = mUsers.get(getAdapterPosition());


            //是否接听
            if (user.isAnswer()) {
                tv_answer.setVisibility(View.GONE);

            } else {
                tv_answer.setText("未接听");
                tv_answer.setVisibility(View.VISIBLE);

            }


            tv_user_name.setText(user.getUserNakeName());
            GlideEngine.loadImage(iv_user_portrait, user.getPortraitUrl());
        }
    }

    public class VideoUserStatusHolder extends RecyclerView.ViewHolder {


        public TextView tv_user_name;
        FrameLayout holderView;
        View v;

        public VideoUserStatusHolder(@NonNull View v) {
            super(v);
            this.v = v;
            tv_user_name = (TextView) v.findViewById(R.id.tv_user_name);
            tv_user_name.setVisibility(View.VISIBLE);
            holderView = v.findViewById(R.id.fl_v);
        }

        public void setData() {
            UserStatusDto user = mUsers.get(getAdapterPosition());
            TXCloudVideoView target = user.getTxCloudVideoView();
            target.setTag(getAdapterPosition());
            stripView(target);
            //动态添加
            if (null != target) {
                if (user.isOpenVideoPermissions() && user.isOpenVideo() && user.isAnswer()) {
                    if (getAdapterPosition() == ((int) target.getTag())) {
                        if (!paly.containsKey(getAdapterPosition() + "")) {
                            if (user.getImId().equals(AppConfig.appImId)) {
                                trtcCloud.openCamera(true, target);
                                LogUtils.w("加载本地用户流");
                            } else {
                                LogUtils.w("加载其他用户流");
                                trtcCloud.startRemoteView(user.getImId(), target);
                            }
                            paly.put(getAdapterPosition() + "", getAdapterPosition());
                        }

                        holderView.addView(target, 0, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                }
            }
            tv_user_name.setText(user.getUserNakeName());
        }
    }
}
