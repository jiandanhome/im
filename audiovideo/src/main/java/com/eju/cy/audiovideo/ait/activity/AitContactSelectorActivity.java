package com.eju.cy.audiovideo.ait.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.ait.adapter.AitUsersAdapter;
import com.eju.cy.audiovideo.base.BaseActivity;
import com.eju.cy.audiovideo.base.ITitleBarLayout;
import com.eju.cy.audiovideo.component.TitleBarLayout;
import com.eju.cy.audiovideo.controller.GroupController;
import com.eju.cy.audiovideo.controller.ImCallBack;
import com.eju.cy.audiovideo.dto.GuoupMemberDto;
import com.eju.cy.audiovideo.tags.AppConfig;
import com.eju.cy.audiovideo.tags.Constants;
import com.eju.cy.audiovideo.utils.ChineseCharacterUtil;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.gjiazhe.wavesidebar.WaveSideBar;
import com.gyf.barlibrary.ImmersionBar;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.v2.V2TIMGroupMemberFullInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberInfoResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @ Name: Caochen
 * @ Date: 2020-07-30
 * @ Time: 10:34
 * @ Description： 圈选
 */
public class AitContactSelectorActivity extends BaseActivity implements AitUsersAdapter.OnItemClickListener, TextWatcher {
    private List<GuoupMemberDto.DataBean> list = new ArrayList<>();//原始

    private List<GuoupMemberDto.DataBean> aitLit = new ArrayList<>();//最终

    private List<GuoupMemberDto.DataBean> mListSearch = new ArrayList<>();//搜索


    private static final String EXTRA_ID = "EXTRA_ID";
    private static final String EXTRA_ROBOT = "EXTRA_ROBOT";

    public static final int REQUEST_CODE = 0x10;
    public static final String RESULT_TYPE = "type";
    public static final String RESULT_DATA = "data";

    private AitUsersAdapter adapter;
    private WaveSideBar sideBar;
    private TitleBarLayout mTitleBar;
    private RelativeLayout rl_search;

    private EditText tv_search;
    private TextView tv_all_user;


    private long nextSeq = 0;
    private long nextSeqnextSeq = 0;

    public static void start(Context context, String tid, boolean addRobot) {
        Intent intent = new Intent();
        if (tid != null) {
            intent.putExtra(Constants.GROUP_ID, tid);
        }

        intent.setClass(context, AitContactSelectorActivity.class);

        ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ait_contact_selector);

        ImmersionBar.with(this)
                .statusBarColor(AppConfig.appType == 0 ? R.color.white : R.color.status_bar_color)
                .statusBarDarkFont(true, 0.2f)
                .fitsSystemWindows(true)
                .keyboardEnable(true)
                .init();
        initViews();
    }


    private void initViews() {
        RecyclerView recyclerView = findViewById(R.id.member_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTitleBar = findViewById(R.id.chat_title_bar);

        mTitleBar.setUserRole("");
        mTitleBar.setTitleBarLayoutBg(getResources().getDrawable(R.color.white));
        mTitleBar.setLeftIcon(R.drawable.icon_ait_back);
        mTitleBar.setTitle(getResources().getString(R.string.select_ait_people), ITitleBarLayout.POSITION.MIDDLE);
        mTitleBar.getRightGroup().setVisibility(View.GONE);
        mTitleBar.getLeftGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sideBar = (WaveSideBar) findViewById(R.id.side_bar);


        rl_search = findViewById(R.id.rl_search);
        tv_search = findViewById(R.id.tv_search);

        tv_search.addTextChangedListener(this);

        tv_all_user = findViewById(R.id.tv_all_user);
        tv_all_user.setVisibility(View.GONE);
        tv_all_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != aitLit && aitLit.size() > 0) {

                    Intent intent = new Intent();

                    intent.putExtra(RESULT_DATA, JsonUtils.toJson(aitLit));
                    setResult(RESULT_OK, intent);

                    finish();

                }
            }
        });
        initAdapter(recyclerView);


    }


    private void initAdapter(final RecyclerView recyclerView) {
        adapter = new AitUsersAdapter(aitLit, AitContactSelectorActivity.this);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        loadData();

        sideBar.setOnSelectIndexItemListener(new WaveSideBar.OnSelectIndexItemListener() {
            @Override
            public void onSelectIndexItem(String index) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getZi_mu().equals(index)) {
                        ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(i, 0);
                        return;
                    }
                }
            }
        });


    }


    private void loadData() {


        GroupController.getInstance().getGroupMemberList(getIntent().getStringExtra(Constants.GROUP_ID), V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ALL, nextSeq, new ImCallBack() {
            @Override
            public void onError(int var1, String var2) {

            }

            @Override
            public void onSuccess(Object var1) {

                V2TIMGroupMemberInfoResult v2TIMGroupMemberInfoResult = (V2TIMGroupMemberInfoResult) var1;

                List<V2TIMGroupMemberFullInfo> infos = v2TIMGroupMemberInfoResult.getMemberInfoList();

                nextSeq = v2TIMGroupMemberInfoResult.getNextSeq();
                if (null != infos && infos.size() > 0) {


                    for (V2TIMGroupMemberFullInfo info : infos) {

                        if (info.getRole() == 300 || info.getRole() == 400) {
                            if (info.getUserID().equals(AppConfig.appImId)) {
                                tv_all_user.setVisibility(View.VISIBLE);
                            }

                        }

                        GuoupMemberDto.DataBean dataBean = new GuoupMemberDto.DataBean();
                        dataBean.setNick_name(info.getNickName());
                        dataBean.setIm_user(info.getUserID());
                        dataBean.setFace_url(info.getFaceUrl());
                        if (!TextUtils.isEmpty(info.getNickName())) {


                            dataBean.setZi_mu(ChineseCharacterUtil.getUpperCase(info.getNickName(), false).substring(0, 1));
                        } else {
                            dataBean.setZi_mu(ChineseCharacterUtil.getUpperCase(info.getUserID(), false).substring(0, 1));
                        }
                        if (!dataBean.getIm_user().equals(AppConfig.appImId)) {
                            list.add(dataBean);
                        }
                    }

                    aitLit.clear();
                    aitLit.addAll(list);
                    rl_search.setVisibility(View.VISIBLE);
                    // adapter.notifyDataSetChanged();

                }


                if (nextSeq != 0) {
                    loadData();
                } else {
                    adapter.notifyDataSetChanged();
                }


            }
        });


    }


    private void test(Long i) {
        LogUtils.w("测试啊啊啊啊啊 啊" + i);

        if (i > 0) {
            nextSeqnextSeq++;
            if (i == 5) {
                nextSeqnextSeq = 0;

            }
            test(nextSeqnextSeq);
        }


    }


    @Override
    public void onItemClick(View v, int position) {

        Intent intent = new Intent();
        intent.putExtra(RESULT_DATA, list.get(position));
        setResult(RESULT_OK, intent);

        finish();


    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {


        if (!TextUtils.isEmpty(s)) {
            String text = s.toString().trim();


            if (!TextUtils.isEmpty(text)) {
                if (mListSearch != null && mListSearch.size() > 0) {
                    // mListSearch 模糊搜索结果集合
                    mListSearch.clear();
                }


                for (GuoupMemberDto.DataBean bean : list) {
                    // 判断javabean中是否包含搜索字段
                    if (bean.getNick_name().contains(text)) {
                        // 若包含，添加
                        mListSearch.add(bean);
                    }
                }
                aitLit.clear();
                aitLit.addAll(mListSearch);
                adapter.notifyDataSetChanged();


            }


        } else {

            s.clear();
            aitLit.clear();
            aitLit.addAll(list);
            adapter.notifyDataSetChanged();

        }


    }
}
