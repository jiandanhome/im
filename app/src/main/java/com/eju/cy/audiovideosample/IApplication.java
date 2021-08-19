package com.eju.cy.audiovideosample;

import android.app.Application;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.controller.EjuImController;


public class IApplication extends Application {

    private static final String TAG = IApplication.class.getSimpleName();

    private static IApplication instance;

    public static IApplication instance() {
        return instance;
    }

    @Override
    public void onCreate() {


        LogUtils.i(TAG, "onCreate-----------");
        super.onCreate();
        instance = this;

        //初始化SDK------正式 1400324570  测试 1400391871
       // EjuImController.getInstance().initSDK(this, 1400329280);
        EjuImController.getInstance().initSDK(this, 1400324570,0);


    }


}
