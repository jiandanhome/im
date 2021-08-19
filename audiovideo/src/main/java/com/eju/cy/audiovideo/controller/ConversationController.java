package com.eju.cy.audiovideo.controller;


import android.content.Context;

import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.dto.UserLevelDto;
import com.eju.cy.audiovideo.net.AppNetInterface;
import com.eju.cy.audiovideo.net.RetrofitManager;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.eju.cy.audiovideo.utils.ParameterUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * IM 会话列表控制类
 *
 * @ Name: Caochen
 * @ Date: 2020-03-20
 * @ Time: 10:16
 * @ Description：
 */
public class ConversationController {

    private static final String TAG = ConversationController.class.getSimpleName();
    private static ConversationController mEjuImController;

    //存放需要设置角标的用户 im id为建，等级为值
    private Map<String, Integer> userLevel = new HashMap<>();
    //存放需要是好友的消息列表
    private List<String> friendsList = new ArrayList<>();


    //单例
    public static ConversationController getInstance() {

        if (mEjuImController == null) {
            synchronized (ConversationController.class) {
                if (mEjuImController == null) {
                    mEjuImController = new ConversationController();
                }
            }

        }
        return mEjuImController;
    }


    /**
     * 角标
     * 设置用户等级
     *
     * @param level id为建，等级为值
     */
    public void setUserLevel(Map<String, Integer> level) {

        this.userLevel.clear();
        this.userLevel.putAll(level);
    }

    /**
     * 获取设置了用户等级的用户
     *
     * @return
     */
    public Map<String, Integer> getUserLevel() {

        if (null != this.userLevel) {
            return this.userLevel;
        }
        return new HashMap<String, Integer>();
    }


    /**
     * 设置好友List(用于加载人脉中的会话列表)
     *
     * @param friendsList
     */
    public void setFriendsList(List<String> friendsList) {

        this.friendsList.clear();
        this.friendsList.addAll(friendsList);


    }

    /**
     * 获取设置的好友列表
     *
     * @return
     */
    public List<String> getFriendsList() {

        if (null != this.friendsList) {
            return this.friendsList;
        }
        return new ArrayList<String>();


    }


    /**
     * 删除好友
     *
     * @param friends 好友IM  ID
     */
    public void deleteFriends(String friends) {

        if (null != this.friendsList && this.friendsList.size() > 0) {

            friendsList.remove(friends);


            ImActionDto imActionDto = new ImActionDto();
            imActionDto.setAction(ActionTags.UPLOAD_C2C_AND_FRIENDS_CONVERSATION);
            imActionDto.setJsonStr("重新拉取C2C 好友关系列会话列表");

            String str = JsonUtils.toJson(imActionDto);
            EjuHomeImEventCar.getDefault().post(str);


        }

    }


    public void deleteMsg(Context context, String from_user, String to_user, String repeal_msg_key, String push_time, String push_body, final ImCallBack imCallBack) {


        final AppNetInterface httpInterface = RetrofitManager.getDefault().provideClientApi(context);
        httpInterface.replaceMsg(ParameterUtils.prepareFormData(from_user),
                ParameterUtils.prepareFormData(to_user),
                ParameterUtils.prepareFormData(repeal_msg_key),
                ParameterUtils.prepareFormData(push_time),
                ParameterUtils.prepareFormData(push_body))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserLevelDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UserLevelDto userLevelDto) {


                        if (null != userLevelDto && "10000".equals(userLevelDto.getCode())) {

                            imCallBack.onSuccess("");
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


}
