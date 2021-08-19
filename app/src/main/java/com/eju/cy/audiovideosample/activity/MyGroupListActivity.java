package com.eju.cy.audiovideosample.activity;

import android.app.Activity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideosample.R;
import com.eju.cy.audiovideosample.adapter.MyGroupAdapter;
import com.eju.cy.audiovideosample.adapter.RecyclerViewListener;
import com.eju.cy.audiovideo.controller.GroupController;
import com.eju.cy.audiovideo.controller.ImCallBack;
import com.eju.cy.audiovideo.dto.MyGroupDto;

import java.util.ArrayList;
import java.util.List;

public class MyGroupListActivity extends Activity {


    private RecyclerView rl_list;
    private List<MyGroupDto> myGroupDtoList = new ArrayList<>();
    private MyGroupAdapter myGroupAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group_list);


        initView();

        initData();


    }

    private void initData() {
        myGroupDtoList.clear();
        GroupController.getInstance().getMyGroup(new ImCallBack() {
            @Override
            public void onError(int var1, String var2) {

            }

            @Override
            public void onSuccess(Object var1) {

                if (null != var1) {
                    List<MyGroupDto> groupDtos = (List<MyGroupDto>) var1;
                    myGroupDtoList.addAll(groupDtos);

                    myGroupAdapter.notifyDataSetChanged();


                    LogUtils.w("myGroupDtoList-----"+myGroupDtoList.size());

                }


            }
        });


    }

    private void initView() {


        myGroupAdapter = new MyGroupAdapter(myGroupDtoList, new RecyclerViewListener() {
            @Override
            public void onRecyclerItemClick(int p) {

                GroupController.getInstance().openChatActivityCarryV(MyGroupListActivity.this, myGroupDtoList.get(p).getGroupId(), myGroupDtoList.get(p).getGroupName());


            }
        });

        rl_list = findViewById(R.id.rl_list);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

        rl_list.setLayoutManager(mLayoutManager);
        rl_list.setAdapter(myGroupAdapter);

    }
}
