package com.eju.cy.audiovideosample.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.eju.cy.audiovideo.ait.AitManager;
import com.eju.cy.audiovideo.ait.AitTextChangeListener;
import com.eju.cy.audiovideosample.R;

public class TestActivity extends Activity {
    private EditText ed_text;
    private AitManager aitManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        ed_text = findViewById(R.id.ed_text);

        initData();
    }

    private void initData() {

        aitManager = new AitManager(this, "05a34352efff4a889767dbb58227dc6c", true);

        ed_text.addTextChangedListener(new TextWatcher() {
            private int start;
            private int count;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                this.start = start;
                this.count = count;
                if (aitManager != null) {
                    aitManager.onTextChanged(s, start, before, count);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (aitManager != null) {
                    aitManager.beforeTextChanged(s, start, count, after);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {


                if (aitManager != null) {
                    aitManager.afterTextChanged(s);
                }

            }
        });


        aitManager.setTextChangeListener(new AitTextChangeListener() {
            @Override
            public void onTextAdd(String content, int start, int length) {
                ed_text.getEditableText().insert(start, content);
            }

            @Override
            public void onTextDelete(int start, int length) {
                int end = start + length - 1;
                ed_text.getEditableText().replace(start, end, "");
            }
        });


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         aitManager.onActivityResult(requestCode, resultCode, data);


    }


}
