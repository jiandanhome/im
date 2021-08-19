package com.eju.cy.audiovideo.controller;

import androidx.annotation.NonNull;

import android.text.TextUtils;

import com.eju.cy.audiovideo.tags.AppConfig;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @ Name: Caochen
 * @ Date: 2020-05-07
 * @ Time: 14:36
 * @ Description：  修改用户信息
 */
public class UserInfoController {


    private static UserInfoController instance;


    public static UserInfoController getInstance() {
        if (instance == null) {
            synchronized (UserInfoController.class) {
                if (instance == null) {
                    instance = new UserInfoController();
                }
            }
        }
        return instance;
    }

    /**
     * 修改用户昵称
     *
     * @param userName
     * @param imCallBack
     */
    public void setUserName(String userName, final ImCallBack imCallBack) {

        HashMap<String, Object> hashMap = new HashMap<>();


        if (!TextUtils.isEmpty(userName)) {
            hashMap.put(TIMUserProfile.TIM_PROFILE_TYPE_KEY_NICK, userName);
        } else {
            imCallBack.onError(8888, "setUserName userName  null");
            return;
        }

        TIMFriendshipManager.getInstance().modifySelfProfile(hashMap, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess() {
                imCallBack.onSuccess("Success");
            }
        });

    }

    /**
     * 设置用户名字和头像
     *
     * @param userName
     * @param url
     * @param imCallBack
     */
    public void setUserNameAndUserPortraitUrl(String userName, String url, final ImCallBack imCallBack) {

        HashMap<String, Object> hashMap = new HashMap<>();
        if (!TextUtils.isEmpty(userName)) {
            hashMap.put(TIMUserProfile.TIM_PROFILE_TYPE_KEY_NICK, userName);

        }

        if (!TextUtils.isEmpty(url)) {
            hashMap.put(TIMUserProfile.TIM_PROFILE_TYPE_KEY_FACEURL, url);
            AppConfig.userPortraitUrl = url;
        }

        if (hashMap.size() == 0) {
            imCallBack.onError(8888, "setUserNameAndUserPortraitUrl userName  url  null");
            return;
        }

        TIMFriendshipManager.getInstance().modifySelfProfile(hashMap, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess() {
                imCallBack.onSuccess("Success");
            }
        });


    }


    /**
     * 修改用户头像
     *
     * @param url
     * @param imCallBack
     */
    public void setUserFaceUrl(String url, final ImCallBack imCallBack) {
        HashMap<String, Object> hashMap = new HashMap<>();


        if (!TextUtils.isEmpty(url)) {
            hashMap.put(TIMUserProfile.TIM_PROFILE_TYPE_KEY_FACEURL, url);
            AppConfig.userPortraitUrl = url;
        } else {
            imCallBack.onError(8888, "setUserFaceUrl url  null");
            return;
        }

        TIMFriendshipManager.getInstance().modifySelfProfile(hashMap, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess() {
                imCallBack.onSuccess("Success");
            }
        });

    }

    /**
     * 修改个性签名
     *
     * @param signature
     * @param imCallBack
     */
    public void setSelfSignature(String signature, final ImCallBack imCallBack) {
        HashMap<String, Object> hashMap = new HashMap<>();


        if (!TextUtils.isEmpty(signature)) {
            hashMap.put(TIMUserProfile.TIM_PROFILE_TYPE_KEY_SELFSIGNATURE, signature);
        } else {
            imCallBack.onError(8888, "setSelfSignature signature  null");
            return;
        }

        TIMFriendshipManager.getInstance().modifySelfProfile(hashMap, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess() {
                imCallBack.onSuccess("Success");
            }
        });

    }

    /**
     * 设置加我为号有时验证方式
     *
     * @param type       TIMFriendAllowType.TIM_FRIEND_ALLOW_ANY   //允许
     *                   TIMFriendAllowType.TIM_FRIEND_DENY_ANY   // 拒绝
     *                   TIMFriendAllowType.TIM_FRIEND_NEED_CONFIRM  // 验证
     * @param imCallBack
     */
    public void setAllowType(String type, final ImCallBack imCallBack) {


        HashMap<String, Object> hashMap = new HashMap<>();


        if (!TextUtils.isEmpty(type)) {
            hashMap.put(TIMUserProfile.TIM_PROFILE_TYPE_KEY_ALLOWTYPE, type);
        }

        TIMFriendshipManager.getInstance().modifySelfProfile(hashMap, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess() {
                imCallBack.onSuccess("Success");
            }
        });
    }

    /**
     * 获取用户信息
     *
     * @param identifiers 用户ID列表
     * @param forceUpdate 是否强制从服务器取
     * @param imCallBack  回调
     */
    public void getUsersProfile(@NonNull String identifiers, boolean forceUpdate, final ImCallBack imCallBack) {
        List<String> stringList = new ArrayList<>();
        stringList.add(identifiers);


        TIMFriendshipManager.getInstance().getUsersProfile(stringList, forceUpdate, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                imCallBack.onSuccess(timUserProfiles);
            }
        });
    }


    /**
     * 获取用户信息
     *
     * @param identifiers 用户ID列表
     * @param forceUpdate 是否强制从服务器取
     * @param imCallBack  回调
     */
    public void getUsersProfile(@NonNull List<String> identifiers, boolean forceUpdate, final ImCallBack imCallBack) {
        TIMFriendshipManager.getInstance().getUsersProfile(identifiers, forceUpdate, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                imCallBack.onSuccess(timUserProfiles);
            }
        });
    }

    /**
     * 修改用户备注
     *
     * @param identifiers 好友标识
     * @param profileMap  profileMap 修改的字段 ,
     *                    TIMFriend.TIM_FRIEND_PROFILE_TYPE_KEY_REMARK ----备注
     *                    TIMFriend.TIM_FRIEND_PROFILE_TYPE_KEY_GROUP ---分组
     *                    TIMFriend.TIM_FRIEND_PROFILE_TYPE_KEY_CUSTOM_PREFIX ---自定义字段前缀
     * @param imCallBack  回调
     */
    public void modifyFriend(String identifiers, HashMap<String, Object> profileMap, final ImCallBack imCallBack) {


        TIMFriendshipManager.getInstance().modifyFriend(identifiers, profileMap, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                imCallBack.onError(i, s);
            }

            @Override
            public void onSuccess() {
                imCallBack.onSuccess("onSuccess");
            }
        });


    }


}
