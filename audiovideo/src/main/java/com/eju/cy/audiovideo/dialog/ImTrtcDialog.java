package com.eju.cy.audiovideo.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.view.WindowManager;

import com.blankj.utilcode.util.ToastUtils;
import com.eju.cy.audiovideo.trtcs.manager.AudioPlayerManager;


public class ImTrtcDialog extends ImDoalog {

    private Context mContext;

    public ImTrtcDialog(Context context, String url, String userName) {
        super(context);
        mContext = context;

        builder();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        dialog.getWindow().setAttributes(lp);

        setCancelable(false);
        setCancelOutside(false);
        setDialogWidth(1f);
        setPortrait(url);
        setUserName(userName);

    }

    public boolean showSystemDialog() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(mContext)) {
                AudioPlayerManager.getInstance().stopMyPlay();
                ToastUtils.showLong("请打开设置“允许显示在其他应用的上层”选项");
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                return false;
            } else {
                // Android6.0以上
                if (!dialog.isShowing()) {
                    super.show();
                    return true;
                }
            }
        } else {
            // Android6.0以下，不用动态声明权限
            if (!dialog.isShowing()) {
                super.show();
                return true;
            }
        }
        return false;
    }
}
