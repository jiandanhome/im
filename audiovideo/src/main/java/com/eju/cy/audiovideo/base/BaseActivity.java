package com.eju.cy.audiovideo.base;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.TUIKit;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.tags.Constants;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;


/**
 * 登录状态的Activity都要集成该类，来完成被踢下线等监听处理。
 */
public class BaseActivity extends FragmentActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    // 监听做成静态可以让每个子类重写时都注册相同的一份。
    private static IMEventListener mIMEventListener = new IMEventListener() {
        @Override
        public void onForceOffline() {
            // ToastUtil.toastLongMessage("您的帐号已在其它终端登录");

            ImActionDto imActionDto = new ImActionDto();
            imActionDto.setAction(ActionTags.ON_FORCE_OFF_LIN);
            String actionStr = JsonUtils.toJson(imActionDto);
            EjuHomeImEventCar.getDefault().post(actionStr);


        }
    };

    public static void logout(Context context, boolean autoLogin) {
        LogUtils.i(TAG, "logout");
        SharedPreferences shareInfo = context.getSharedPreferences(Constants.USERINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shareInfo.edit();
        editor.putBoolean(Constants.AUTO_LOGIN, autoLogin);
        editor.commit();


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.navigation_bar_color));
            int vis = getWindow().getDecorView().getSystemUiVisibility();
            vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            vis |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            getWindow().getDecorView().setSystemUiVisibility(vis);
        }

        TUIKit.addIMEventListener(mIMEventListener);
    }

    @Override
    protected void onStart() {
        LogUtils.i(TAG, "onStart");
        super.onStart();
        SharedPreferences shareInfo = getSharedPreferences(Constants.USERINFO, Context.MODE_PRIVATE);
        boolean login = shareInfo.getBoolean(Constants.AUTO_LOGIN, false);
        if (!login) {
            // BaseActivity.logout(DemoApplication.instance(), false);
        }
    }

    @Override
    protected void onResume() {
        LogUtils.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        LogUtils.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        LogUtils.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LogUtils.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        LogUtils.i(TAG, "onNewIntent");
        super.onNewIntent(intent);
    }
}
