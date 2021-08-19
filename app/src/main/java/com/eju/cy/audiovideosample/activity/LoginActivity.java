package com.eju.cy.audiovideosample.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.TUIKit;
import com.eju.cy.audiovideo.controller.EjuImController;
import com.eju.cy.audiovideo.controller.ImCallBack;
import com.eju.cy.audiovideo.enumer.AppChannelEnmumer;
import com.eju.cy.audiovideo.enumer.AppTypeEnmumer;
import com.eju.cy.audiovideo.utils.ToastUtil;
import com.eju.cy.audiovideosample.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @ Name: Caochen
 * @ Date: 2020-05-11
 * @ Time: 15:53
 * @ Description： 登录
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    private TextView tv_login1, tv_login2;
    private static final int REQ_PERMISSION_CODE = 0x100;

    String userId9027 = "9027";
    String userToken9027 = "dc20b97073803348fb63aee070364e8e03bbf5c5";
    String user9027Url = "http://img.zcool.cn/community/01e0e457b32f640000018c1b402bf2.png@1280w_1l_2o_100sh.png";

    /**
     * cs
     */
//    String userId9027 = "1367";
//    String userToken9027 = "7b30e6d5e7130514d9706696b5ece35c97c13799";
//    String user9027Url = "http://img.zcool.cn/community/01e0e457b32f640000018c1b402bf2.png@1280w_1l_2o_100sh.png";


//    String userId9027 = "1367";
//    String userToken9027 = "7b30e6d5e7130514d9706696b5ece35c97c13799";
//    String user9027Url = "http://img.zcool.cn/community/01e0e457b32f640000018c1b402bf2.png@1280w_1l_2o_100sh.png";


    String userId211425 = "211425";
    String userToken211425 = "6d3d9b1e230c46aff449b46ab3e0b7bf5fdc1651";
    String user211425Url = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimgsa.baidu.com%2Fexp%2Fw%3D500%2Fsign%3D58dd5d885c3d26972ed3085d65fab24f%2Fc8ea15ce36d3d5394e316f813787e950352ab060.jpg&refer=http%3A%2F%2Fimgsa.baidu.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1625714388&t=9a3138db1774a56b3fffd15c3cd4a929";


    //权限检查
    public boolean checkPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TUIKit.getAppContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TUIKit.getAppContext(), Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TUIKit.getAppContext(), Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TUIKit.getAppContext(), Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(TUIKit.getAppContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (permissions.size() != 0) {
                String[] permissionsArray = permissions.toArray(new String[1]);
                ActivityCompat.requestPermissions(activity,
                        permissionsArray,
                        REQ_PERMISSION_CODE);
                return false;
            }
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();


    }

    private void initView() {

        checkPermission(this);
        tv_login1 = findViewById(R.id.tv_login1);
        tv_login2 = findViewById(R.id.tv_login2);

        tv_login1.setOnClickListener(this);
        tv_login2.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_login1:
                login(userId9027, userToken9027, user9027Url, "帅哥", AppTypeEnmumer.JDM, AppChannelEnmumer.JDMF_AND_YILOU);

                break;
            case R.id.tv_login2:
                login(userId211425, userToken211425, user211425Url, "美女", AppTypeEnmumer.JDM, AppChannelEnmumer.JDMF_AND_YILOU);

                break;

        }

    }

    /**
     * @param userId            用户ID
     * @param userToken         用户token
     * @param appTypeEnmumer    用户类型
     * @param appChannelEnmumer 应用通道类型
     */
    private void login(final String userId, final String userToken, String url, String userName, AppTypeEnmumer appTypeEnmumer, AppChannelEnmumer appChannelEnmumer) {
        EjuImController.getInstance().autoLogin(userId, userToken, url, userName, appTypeEnmumer, appChannelEnmumer, new ImCallBack() {
            @Override
            public void onError(int var1, String var2) {

                LogUtils.w("onError---------" + var2);
            }

            @Override
            public void onSuccess(Object data) {
                //此处返回的是IM 登录成功的IM id
                LogUtils.w("IM-ID" + data.toString());


                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }


        });
    }


    /**
     * 系统请求权限回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_PERMISSION_CODE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtil.toastLongMessage("未全部授权，部分功能可能无法使用！");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return true;
    }
}
