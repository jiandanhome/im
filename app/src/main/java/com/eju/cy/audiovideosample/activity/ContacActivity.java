package com.eju.cy.audiovideosample.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideosample.R;
import com.eju.cy.audiovideo.modules.contact.ContactLayout;

/**
 * @ Name: Caochen
 * @ Date: 2020-05-11
 * @ Time: 15:09
 * @ Description：  通讯录面板
 */
public class ContacActivity extends AppCompatActivity {
    private ContactLayout mContactLayout;
    private static final String TAG = ContacActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contac);

        initView();
    }

    private void initView() {

        mContactLayout = findViewById(R.id.contact_layout);
    }

    private void refreshData() {
        // 通讯录面板的默认UI和交互初始化
        mContactLayout.initDefault();
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.i(TAG, "onResume");
        refreshData();
    }
}
