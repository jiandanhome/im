package com.eju.cy.audiovideo.ait.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.component.picture.imageEngine.impl.GlideEngine;
import com.eju.cy.audiovideo.dto.GuoupMemberDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @ Name: Caochen
 * @ Date: 2020-07-30
 * @ Time: 10:14
 * @ Description： 圈选
 */
public class AitUsersAdapter extends RecyclerView.Adapter<AitUsersAdapter.AitUsersHolder> {
    private List<GuoupMemberDto.DataBean> list = new ArrayList<>();
    private Activity activity;
    private OnItemClickListener onItemClickListener = null;

    public AitUsersAdapter(List<GuoupMemberDto.DataBean> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AitUsersAdapter.AitUsersHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = activity.getLayoutInflater().inflate(R.layout.item_ait_layout, viewGroup, false);
        return new AitUsersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AitUsersAdapter.AitUsersHolder aitUsersHolder, final int position) {


        if (position == 0 || !list.get(position - 1).getZi_mu().equals(list.get(position).getZi_mu())) {
            aitUsersHolder.tv_index.setVisibility(View.VISIBLE);
            aitUsersHolder.tv_index.setText("" + list.get(position).getZi_mu());
        } else {
            aitUsersHolder.tv_index.setVisibility(View.GONE);
        }


        GlideEngine.loadCornerImage(aitUsersHolder.iv_portrait, list.get(position).getFace_url(), null, 6);

        aitUsersHolder.tv_name.setText(list.get(position).getNick_name());


        aitUsersHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v,  aitUsersHolder.getAdapterPosition());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class AitUsersHolder extends RecyclerView.ViewHolder {

        private ImageView iv_portrait;
        private TextView tv_name, tv_index;

        public AitUsersHolder(@NonNull View itemView) {
            super(itemView);

            iv_portrait = itemView.findViewById(R.id.iv_portrait);
            tv_index = itemView.findViewById(R.id.tv_index);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
}
