package com.eju.cy.audiovideo.net;


import com.eju.cy.audiovideo.dto.CallRecordsDto;
import com.eju.cy.audiovideo.dto.CsationStatusDto;
import com.eju.cy.audiovideo.dto.GroupListWithKeyDto;
import com.eju.cy.audiovideo.dto.GuoupMemberDto;
import com.eju.cy.audiovideo.dto.GuoupStaticDto;
import com.eju.cy.audiovideo.dto.InviteGroupMemberDto;
import com.eju.cy.audiovideo.dto.RoomDto;
import com.eju.cy.audiovideo.dto.SetGuoupStaticDto;
import com.eju.cy.audiovideo.dto.SigDto;
import com.eju.cy.audiovideo.dto.UpdateStatusDto;
import com.eju.cy.audiovideo.dto.UserLevelDto;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface AppNetInterface {


    /**
     * @ Name: Caochen
     * @ Date: 2020-03-11
     * @ Time: 16:51
     * @ Description：  生成房间号
     */

    @Multipart
    @POST("av/rtc/gen_room/")
    Observable<RoomDto> getRoom(@Part("appChannel") RequestBody appChannel,
                                @Part("calling") RequestBody calling,
                                @Part("listening") RequestBody listening,
                                @Part("platform") RequestBody platform);


    /**
     * @ Name: Caochen
     * @ Date: 2020-03-12
     * @ Time: 06:56
     * @ Description： 查询状态
     */

    @GET("av/rtc/by_call/")
    Observable<CallRecordsDto> byCall();

    /**
     * @ Name: Caochen
     * @ Date: 2020-03-13
     * @ Time: 12:10
     * @ Description： 签名
     */
    @Multipart
    @POST("av/usersig/gen_user_sig/")
    Observable<SigDto> genUserSig(@Part("appChannel") RequestBody appChannel);


    /**
     * @ Name: Caochen
     * @ Date: 2020-03-13
     * @ Time: 12:10
     * @ Description：  更改通话状态  av/rtc/update_talk_status/
     */


    @Multipart
    @POST("av/rtc/update_talk_status/")
    Observable<UpdateStatusDto> updateTalkStatus(@Part("id") RequestBody id, @Part("talk_status") RequestBody talk_status);


    /**
     * @ Name: Caochen
     * @ Date: 2020-03-13
     * @ Time: 12:10
     * @ Description：  更改房间状态  av/rtc/update_talk_status/
     */


    @Multipart
    @POST("av/rtc/update_room_status/")
    Observable<UpdateStatusDto> updateRoomStatus(@Part("id") RequestBody id, @Part("room_status") RequestBody room_status);


    /**
     * 设置圈子是否公开
     *
     * @param group_id
     * @return
     */
    @Multipart
    @POST("av/im/set_group_public/")
    Observable<SetGuoupStaticDto> setGuoupState(@Part("group_id") RequestBody group_id, @Part("public") RequestBody isPublic);


    /**
     * 获取群详情
     *
     * @param Groups
     * @param IsFilter
     * @param ImUser
     * @return
     */
    @Multipart
    @POST("av/im/get_groups_info/")
    Observable<GuoupStaticDto> getGuoupState(@Part("Groups") RequestBody Groups, @Part("IsFilter") RequestBody IsFilter, @Part("ImUser") RequestBody ImUser);


    /**
     * 设置免打扰
     *
     * @param conversation_id 会话 ID。会话ID组成方式：C2C+userID（单聊）GROUP+groupID（群聊） @TIM#SYSTEM（系统通知会话)
     * @param imuser_id       IM 用户ID
     * @param disturb         消息免打扰 0：否 1：是
     * @return
     */
    @Multipart
    @POST("/av/csation/disturb/")
    Observable<SetGuoupStaticDto> setDisturb(@Part("conversation_id") RequestBody conversation_id, @Part("imuser_id") RequestBody imuser_id, @Part("disturb") RequestBody disturb);


    /**
     * 会话置顶
     *
     * @param conversation_id 会话 ID。会话ID组成方式：C2C+userID（单聊）GROUP+groupID（群聊） @TIM#SYSTEM（系统通知会话)
     * @param imuser_id       IM 用户ID
     * @param stick           会话置顶 0：否 1：是
     * @return
     */
    @Multipart
    @POST("/av/csation/stick/")
    Observable<SetGuoupStaticDto> setStick(@Part("conversation_id") RequestBody conversation_id, @Part("imuser_id") RequestBody imuser_id, @Part("stick") RequestBody stick);


    /**
     * 获取会话状态（是否置顶  支付免打扰）
     *
     * @param conversation_id
     * @param imuser_id
     * @return
     */
    @Multipart
    @POST("/av/csation/get_csation_status/")
    Observable<CsationStatusDto> getCsationStatus(@Part("conversations_id") RequestBody conversation_id, @Part("imuser_id") RequestBody imuser_id);


    /**
     * 邀请用户进群
     *
     * @param group_id    IM通讯群组ID
     * @param im_users_id IM通讯用户Id，用，隔开,例如 “101,1017,1023”
     * @return
     */
    @Multipart
    @POST("/av/imadmin/add_member_to_group/")
    Observable<InviteGroupMemberDto> inviteGroupMember(@Part("group_id") RequestBody group_id, @Part("im_users_id") RequestBody im_users_id);


    /**
     * 存储IM用户信息到服务器
     *
     * @param ImUser IM通讯用户的IM ID
     * @return
     */
    @Multipart
    @POST("/av/im/store_im_user/")
    Observable<CsationStatusDto> storeImUser(@Part("ImUser") RequestBody ImUser, @Part("app_type") RequestBody app_type);


    /**
     * 设置用户是否免打扰---APP  前台 后台调用
     *
     * @param im_user IM通讯用户的IM ID
     * @return disturb 是否设为免打扰 0：否 1：是
     */
    @Multipart
    @POST("/av/im/set_imuser_disturb/")
    Observable<SetGuoupStaticDto> setUserDisturb(@Part("im_user") RequestBody im_user, @Part("disturb") RequestBody disturb);


    /**
     * @param im_user
     * @param group_id
     * @param role     更改后的群组中的角色 （#Admin/Member/Owner）
     * @return
     */
    @Multipart
    @POST("/av/im/set_member_role/")
    Observable<SetGuoupStaticDto> setMemberRole(@Part("im_user") RequestBody im_user, @Part("group_id") RequestBody group_id, @Part("role") RequestBody role);


    /**
     * 获取圈成员列表
     *
     * @param group_id
     * @return
     */
    @Multipart
    @POST("/av/im/get_groups_member/")
    Observable<GuoupMemberDto> getGuoupMember(@Part("group_id") RequestBody group_id);


    /**
     * 根据关键字获取圈子列表（不包含未公开的，不包含用户已经加入的）
     *
     * @param im_user  IM用户ID（结果不包含用户已经加入的圈子）
     * @param name_key 圈子名称包含的关键词
     * @return
     */
    @Multipart
    @POST("/av/im/get_group_list_with_key/")
    Observable<GroupListWithKeyDto> getGroupListWithKey(@Part("im_user") RequestBody im_user, @Part("name_key") RequestBody name_key);


    /**
     * 获取个人用户等级
     *
     * @param im_user IM用户 ID，多个用，隔开
     * @return
     */
    @Multipart
    @POST("/av/im/get_broker_grade/")
    Observable<UserLevelDto> getUserGrade(@Part("im_user") RequestBody im_user);


    /**
     * 获取群成员等级
     *
     * @param group_id 群组ID
     * @return
     */
    @Multipart
    @POST("/av/im/get_group_broker_grade/")
    Observable<UserLevelDto> getGroupMembersGrade(@Part("group_id") RequestBody group_id);


    /**
     * 智能小区15卡片点击
     *
     * @param url
     * @param report
     * @param community_id
     * @param client_id
     * @param broker_id
     * @return
     */
    @Multipart
    @POST()
    Observable<UserLevelDto> sendReport(@Url String url, @Part("report") RequestBody report,
                                        @Part("community_id") RequestBody community_id,
                                        @Part("client_id") RequestBody client_id,
                                        @Part("broker_id") RequestBody broker_id,
                                        @Part("mf_id") RequestBody mf_id,
                                        @Part("community_name") RequestBody community_name,
                                        @Part("lj_city_id") RequestBody lj_city_id);


    /**
     * 消息替换
     *
     * @param from_user
     * @param to_user
     * @param repeal_msg_key
     * @param push_time
     * @param push_body
     * @return
     */
    @Multipart
    @POST("/av/imadmin/replace_msg/")
    Observable<UserLevelDto> replaceMsg(@Part("from_user") RequestBody from_user,
                                        @Part("to_user") RequestBody to_user,
                                        @Part("repeal_msg_key") RequestBody repeal_msg_key,
                                        @Part("push_time") RequestBody push_time,
                                        @Part("push_body") RequestBody push_body);


}
