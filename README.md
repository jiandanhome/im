 
# 音视频IMSDK

## Step1

-**引入**:

```
主工程需要以下jar支持，请添加，如已添加请忽略
 iimplementation('com.tencent.liteav:LiteAVSDK_TRTC:latest.release') {
          exclude module: 'gson'
          exclude module: 'okhttp'
          exclude module: 'okio'
          exclude module: 'retrofit'
          exclude module: 'glide'
      }
  
      implementation('com.tencent.imsdk:tuikit:4.6.101') {
          exclude module: 'gson'
          exclude module: 'okhttp'
          exclude module: 'okio'
          exclude module: 'retrofit'
          exclude module: 'glide'
      }
 
 //引入aar
 implementation 'com.eju.cy.audiovideo:audiovideo:2.1.4'
```

## Step2 
-**配置混淆**
```
-keep class com.eju.cy.audiovideo.** { *; }
-keep class com.tencent.** { *; }
```
## Step3

-**调用方式如下**

```
 1:在项目Application中onCreate加入
 //初始化SDK------正式环境 1400324570  测试 1400329280
                EjuImController.getInstance().initSDK(this);
                
 2:在MainActity中  onCreate加入
   CustomAVCallUIController.getInstance().setActivityContext(MainActivity.this); 
   
   
 3:在需要接受SDK相关操作回调的地方实现 EjuHomeImObserver  
   相关操作回调结果如下
   
       @Override
       public void action(Object obj) {
   
           if (null != obj) {
   
               String srt = (String) obj;
   
               ImActionDto imActionDto = JsonUtils.fromJson(srt, ImActionDto.class);
   
               LogUtils.w("ACTIVON--------" + imActionDto.getAction() + "\n" + imActionDto.getJsonStr());
   
   
               switch (imActionDto.getAction()) {
   
                   //发送名片
                   case ActionTags.ACTION_SEND_BUSINESS_CARD:
                       CustomContentDto contentDto = new CustomContentDto();
                       contentDto.setCardName("郭富城");
                       contentDto.setPhoneNumber("15601891066");
                       contentDto.setPosition("房产顾问");
                       contentDto.setPortraitUrl("");
                       contentDto.setShopAddress("延长路门店");
                       contentDto.setShopName("易居房友");
   
                       contentDto.setPortraitUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1588764928820&di=ac74ee30d71eadfcb6b1985dd2ca322b&imgtype=0&src=http%3A%2F%2Fbpic.588ku.com%2Felement_origin_min_pic%2F00%2F10%2F74%2F3856a4ae2679e6c.jpg");
   
   
                       contentDto.setSlogan("从业房产八十年从业房产八十年从业房产八十");
                       CustomMsgController.getInstance().sendBusinessCard(contentDto, "1");
   
                       break;
                   //发送房源
                   case ActionTags.ACTION_SEND_HOUSING:
                       CustomContentDto houingDto = new CustomContentDto();
                       houingDto.setTitle("宝华现代城 2室2厅 990万 超高性价");
                       houingDto.setHouseUrl("http://img.ziroom.com/pic/house_images/g2m3/M00/E7/F8/ChAZE16lfiWAKQmJAAKkQ2VfuPI273.jpg_C_640_480_Q100.jpg");
                       CustomMsgController.getInstance().sendHousing(houingDto, "2");
                       break;
   
                   //处理单聊右侧按钮点击事件后逻辑
                   case ActionTags.CHAT_C2C_RIGHT:
                       LogUtils.w("处理单聊右侧按钮点击事件后逻辑");
                       break;
                   //处理群聊右侧按钮点击事件后逻辑
                   case ActionTags.CHAT_GUOUP_RIGHT:
                       LogUtils.w("处理群聊右侧按钮点击事件后逻辑" + imActionDto.getJsonStr());//此时imActionDto.getJsonStr()为圈子ID
                       break;
                   //群聊语音通话
                   case ActionTags.GROUP_VOICE_CALL:
                       LogUtils.w("群聊语音通话");
                       break;
                   //单聊聊语音通话
                   case ActionTags.C2C_VOICE_CALL:
                       LogUtils.w("群聊语音通话");
                       break;
                   //账号在其他端登陆
                   case ActionTags.ON_FORCE_OFF_LIN:
                       LogUtils.w("账号在其他端登陆");
                       break;
   
               }
   
   
           }
   
       }       
       
 4:  基础功能控制器:EjuImController.getInstance().XXX,详细使用请查阅 Simple   
        其中包含
        初始化initSDK()
        登录 autoLogin()
        登出 logInOut()
        添加好友addFriend()
        删除好友deleteFriends()
 
 5:  圈子（群聊）相关控制器:GroupController.getInstance().XXX,详细使用请查阅 Simple 
        其中包含 
        创建群聊createGroup()
        设置群聊头像setGroupFaceUrl()
        设置群聊名字setGroupName()
        设置群聊公告setNotification()
        设置群介绍setGuoupIntroduce()
        邀请人加入群聊inviteGroupMember()
        申请加入群聊applyJoinGroup()
        退出群聊quitGroup()
        删除群成员deleteGroupMember()
        获取群成员列表getGroupMembers（）
        获取已经加入的群聊列表getGroupList()
        解散群聊getGroupList()
        转让圈子modifyGroupOwner()
        获取指定类型群成员getGroupMembersByFilter()
        获取某个圈子详情getGroupInfo（）
        打开某个圈聊天界面openChatActivity（）
        同意入圈acceptGuoupApply（）
        拒绝入圈refuseGroupApply（）
        发送圈通知setGroupNotification（）
        设置圈聊天是否置顶setStick（）
        圈聊天发送一条文件消息sendFileMessage（）
        发送图片消息sendImageMessage（）
        ....
        
   
 
 6:  用户信息相关控制器:UserInfoController.getInstance().XXX,详细使用请查阅 Simple   
        修改用户昵称setUserName（）
        设置用户名以及头像setUserNameAndUserPortraitUrl（）
        修改头像setUserFaceUrl（）
        设置加我时验证方式setAllowType（）
        获取用户信息getUsersProfile（）
        修改用户备注modifyFriend（）
        ....
 
 
 
 
 

```
## Step4
-**退出登陆**
 EjuImController.getInstance().logInOut(new EjuImSdkCallBack());


