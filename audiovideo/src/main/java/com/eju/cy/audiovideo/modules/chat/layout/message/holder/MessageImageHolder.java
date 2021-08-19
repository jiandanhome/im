package com.eju.cy.audiovideo.modules.chat.layout.message.holder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.TUIKit;
import com.eju.cy.audiovideo.component.face.FaceManager;
import com.eju.cy.audiovideo.component.photoview.PhotoViewActivity;
import com.eju.cy.audiovideo.component.picture.imageEngine.impl.GlideEngine;
import com.eju.cy.audiovideo.component.video.VideoViewActivity;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.eju.cy.audiovideo.utils.TUIKitConstants;
import com.eju.cy.audiovideo.utils.TUIKitLog;
import com.eju.cy.audiovideo.utils.ToastUtil;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMFaceElem;
import com.tencent.imsdk.TIMImage;
import com.tencent.imsdk.TIMImageElem;
import com.tencent.imsdk.TIMImageType;
import com.tencent.imsdk.TIMSnapshot;
import com.tencent.imsdk.TIMVideo;
import com.tencent.imsdk.TIMVideoElem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MessageImageHolder extends MessageContentHolder {

    private static final int DEFAULT_MAX_SIZE = 540;
    private static final int DEFAULT_RADIUS = 10;
    private final List<String> downloadEles = new ArrayList<>();
    private ImageView contentImage;
    private ImageView videoPlayBtn;
    private TextView videoDurationText;
    private boolean mClicking;

    public MessageImageHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getVariableLayout() {
        return R.layout.message_adapter_content_image;
    }

    @Override
    public void initVariableViews() {
        contentImage = rootView.findViewById(R.id.content_image_iv);
        videoPlayBtn = rootView.findViewById(R.id.video_play_btn);
        videoDurationText = rootView.findViewById(R.id.video_duration_tv);
    }

    @Override
    public void layoutVariableViews(MessageInfo msg, int position) {
        msgContentFrame.setBackground(null);
        switch (msg.getMsgType()) {
            case MessageInfo.MSG_TYPE_CUSTOM_FACE:
            case MessageInfo.MSG_TYPE_CUSTOM_FACE + 1:
                performCustomFace(msg, position);
                break;
            case MessageInfo.MSG_TYPE_IMAGE:
            case MessageInfo.MSG_TYPE_IMAGE + 1:
                performImage(msg, position);
                break;
            case MessageInfo.MSG_TYPE_VIDEO:
            case MessageInfo.MSG_TYPE_VIDEO + 1:
                performVideo(msg, position);
                break;
        }
    }

    private void performCustomFace(final MessageInfo msg, final int position) {
        videoPlayBtn.setVisibility(View.GONE);
        videoDurationText.setVisibility(View.GONE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        contentImage.setLayoutParams(params);
        TIMElem elem = msg.getElement();
        if (!(elem instanceof TIMFaceElem)) {
            return;
        }
        TIMFaceElem faceEle = (TIMFaceElem) elem;
        String filter = new String(faceEle.getData());
        if (!filter.contains("@2x")) {
            filter += "@2x";
        }
        Bitmap bitmap = FaceManager.getCustomBitmap(faceEle.getIndex(), filter);
        if (bitmap == null) {
            // 自定义表情没有找到，用emoji再试一次
            bitmap = FaceManager.getEmoji(new String(faceEle.getData()));
            if (bitmap == null) {
                // TODO 临时找的一个图片用来表明自定义表情加载失败
                contentImage.setImageDrawable(rootView.getContext().getResources().getDrawable(R.drawable.face_delete));
            } else {
                contentImage.setImageBitmap(bitmap);
            }
        } else {
            contentImage.setImageBitmap(bitmap);
        }
    }

    private ViewGroup.LayoutParams getImageParams(ViewGroup.LayoutParams params, final MessageInfo msg) {
        if (msg.getImgWidth() == 0 || msg.getImgHeight() == 0) {
            return params;
        }
        if (msg.getImgWidth() > msg.getImgHeight()) {
            params.width = DEFAULT_MAX_SIZE;
            params.height = DEFAULT_MAX_SIZE * msg.getImgHeight() / msg.getImgWidth();
        } else {
            params.width = DEFAULT_MAX_SIZE * msg.getImgWidth() / msg.getImgHeight();
            params.height = DEFAULT_MAX_SIZE;
        }
        return params;
    }

    private void resetParentLayout() {
        ((FrameLayout) contentImage.getParent().getParent()).setPadding(17, 0, 13, 0);
    }

    private void performImage(final MessageInfo msg, final int position) {
        contentImage.setLayoutParams(getImageParams(contentImage.getLayoutParams(), msg));
        resetParentLayout();
        videoPlayBtn.setVisibility(View.GONE);
        videoDurationText.setVisibility(View.GONE);
        TIMElem elem = msg.getElement();
        if (!(elem instanceof TIMImageElem)) {
            return;
        }
        final TIMImageElem imageEle = (TIMImageElem) elem;
        final List<TIMImage> imgs = imageEle.getImageList();
        if (!TextUtils.isEmpty(msg.getDataPath())) {
            GlideEngine.loadCornerImage(contentImage, msg.getDataPath(), null, DEFAULT_RADIUS);


        } else {
            for (int i = 0; i < imgs.size(); i++) {
                final TIMImage img = imgs.get(i);
                if (img.getType() == TIMImageType.Thumb) {
                    synchronized (downloadEles) {
                        if (downloadEles.contains(img.getUuid())) {
                            break;
                        }
                        downloadEles.add(img.getUuid());
                    }
                    final String path = TUIKitConstants.IMAGE_DOWNLOAD_DIR + img.getUuid();
                    img.getImage(path, new TIMCallBack() {
                        @Override
                        public void onError(int code, String desc) {
                            downloadEles.remove(img.getUuid());
                            TUIKitLog.e("MessageListAdapter img getImage", code + ":" + desc);
                        }

                        @Override
                        public void onSuccess() {
                            downloadEles.remove(img.getUuid());
                            msg.setDataPath(path);
                            GlideEngine.loadCornerImage(contentImage, msg.getDataPath(), null, DEFAULT_RADIUS);
                        }
                    });
                    break;
                }
            }
        }
        contentImage.setOnClickListener(new View.OnClickListener() {

            TIMImage timImage;

            @Override
            public void onClick(View v) {

                //存储图片地址
                List<String> imgMsg = new ArrayList<>();




                for (int i = 0; i < imgs.size(); i++) {
                    TIMImage img = imgs.get(i);
                    if (img.getType() == TIMImageType.Original) {
                        PhotoViewActivity.mCurrentOriginalImage = img;
                        timImage = img;
//                        //原始
//                        final String path = TUIKitConstants.IMAGE_DOWNLOAD_DIR + img.getUuid();
//
//
//                        final File file = new File(path);
//                        if (!file.exists()) {
//                            img.getImage(path, new TIMCallBack() {
//                                @Override
//                                public void onError(int code, String desc) {
//                                    senContentImageClick(msg.getDataPath(), msg.getDataPath());
//
//                                }
//
//                                @Override
//                                public void onSuccess() {
//
//                                    senContentImageClick(msg.getDataPath(), file.getPath());
//                                }
//                            });
//                        } else {
//
//                            senContentImageClick(msg.getDataPath(), file.getPath());
//
//                        }


                        break;
                    }
                }






                if (null != timImage) {
                    //原始
                    final String path = TUIKitConstants.IMAGE_DOWNLOAD_DIR + timImage.getUuid();


                    final File file = new File(path);
                    if (!file.exists()) {
                        timImage.getImage(path, new TIMCallBack() {
                            @Override
                            public void onError(int code, String desc) {
                                senContentImageClick(msg.getDataPath(), msg.getDataPath());

                            }

                            @Override
                            public void onSuccess() {

                                senContentImageClick(file.getPath(), file.getPath());
                            }
                        });
                    } else {

                        senContentImageClick(file.getPath(), file.getPath());

                    }
                   // LogUtils.w("图片路径点击的是"+timImage.getUrl());

                    //线上点击的
                    imgMsg.add(timImage.getUrl());
                } else {
                    senContentImageClick(msg.getDataPath(), msg.getDataPath());
                    //本地点击的
                    imgMsg.add(msg.getDataPath());
                   // LogUtils.w("图片路径点击的是本地"+msg.getDataPath());
                }








                //获取图片消息
                for (int j = 0; j < mAdapter.getDataSouurce().size(); j++) {
                    if (mAdapter.getDataSouurce().get(j).getMsgType() == MessageInfo.MSG_TYPE_IMAGE) {


                        TIMElem elem = mAdapter.getDataSouurce().get(j).getElement();
                        if (!(elem instanceof TIMImageElem)) {
                            return;
                        }
                        final TIMImageElem imageEle = (TIMImageElem) elem;
                        final List<TIMImage> imgs = imageEle.getImageList();


                        for (int i = 0; i < imgs.size(); i++) {
                            final TIMImage img = imgs.get(i);
                            if (img.getType() == TIMImageType.Original) {
                                final String path = img.getUrl();
                              //  LogUtils.w("图片路径" + path + "\n下标" + getAdapterPosition());
                                imgMsg.add(path);
                                break;
                            }
                        }

                        // imgMsg.add(mAdapter.getDataSouurce().get(j));
                    }
                }



               // LogUtils.w("图片大小"+imgMsg.size());
                ImActionDto imActionDto = new ImActionDto();
                imActionDto.setAction(ActionTags.ACTION_CLICK_IMAGE_LIST_MESSAGE_PATH);
                String json = JsonUtils.toJson(imgMsg);
                imActionDto.setJsonStr(json);
                EjuHomeImEventCar.getDefault().post(JsonUtils.toJson(imActionDto));

            }

        });
        contentImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onMessageLongClick(view, position, msg);
                }
                return true;
            }
        });


    }

    //发送图片点击事件
    private void senContentImageClick(String previewImagePath, String originalImagePath) {
        ImActionDto imActionDto = new ImActionDto();
        imActionDto.setAction(ActionTags.ACTION_CLICK_IMAGE_MESSAGE_PATH);
//
//        ImageMsgDto imageMsgDto = new ImageMsgDto();
//        imageMsgDto.setPreviewImagePath(previewImagePath);
//        imageMsgDto.setOriginalImagePath(originalImagePath);
//        String imageMsgDtoStr = JsonUtils.toJson(imageMsgDto);


//        imActionDto.setJsonStr(previewImagePath);
//
//        EjuHomeImEventCar.getDefault().post(JsonUtils.toJson(imActionDto));
    }


    private void performVideo(final MessageInfo msg, final int position) {
        contentImage.setLayoutParams(getImageParams(contentImage.getLayoutParams(), msg));
        resetParentLayout();

        videoPlayBtn.setVisibility(View.VISIBLE);
        videoDurationText.setVisibility(View.VISIBLE);
        TIMElem elem = msg.getElement();
        if (!(elem instanceof TIMVideoElem)) {
            return;
        }
        final TIMVideoElem videoEle = (TIMVideoElem) elem;
        final TIMVideo video = videoEle.getVideoInfo();

        if (!TextUtils.isEmpty(msg.getDataPath())) {
            GlideEngine.loadCornerImage(contentImage, msg.getDataPath(), null, DEFAULT_RADIUS);

        } else {
            final TIMSnapshot shotInfo = videoEle.getSnapshotInfo();
            synchronized (downloadEles) {
                if (!downloadEles.contains(shotInfo.getUuid())) {
                    downloadEles.add(shotInfo.getUuid());
                }
            }

            final String path = TUIKitConstants.IMAGE_DOWNLOAD_DIR + videoEle.getSnapshotInfo().getUuid();
            videoEle.getSnapshotInfo().getImage(path, new TIMCallBack() {
                @Override
                public void onError(int code, String desc) {
                    downloadEles.remove(shotInfo.getUuid());
                    TUIKitLog.e("MessageListAdapter video getImage", code + ":" + desc);
                }

                @Override
                public void onSuccess() {
                    downloadEles.remove(shotInfo.getUuid());
                    msg.setDataPath(path);
                    GlideEngine.loadCornerImage(contentImage, msg.getDataPath(), null, DEFAULT_RADIUS);

                }
            });
        }

        String durations = "00:" + video.getDuaration();
        if (video.getDuaration() < 10) {
            durations = "00:0" + video.getDuaration();
        }
        videoDurationText.setText(durations);

        final String videoPath = TUIKitConstants.VIDEO_DOWNLOAD_DIR + video.getUuid();
        final File videoFile = new File(videoPath);
        //以下代码为zanhanding修改，用于fix视频消息发送失败后不显示红色感叹号的问题
        if (msg.getStatus() == MessageInfo.MSG_STATUS_SEND_SUCCESS) {
            //若发送成功，则不显示红色感叹号和发送中动画
            statusImage.setVisibility(View.GONE);
            sendingProgress.setVisibility(View.GONE);
        } else if (videoFile.exists() && msg.getStatus() == MessageInfo.MSG_STATUS_SENDING) {
            //若存在正在发送中的视频文件（消息），则显示发送中动画（隐藏红色感叹号）
            statusImage.setVisibility(View.GONE);
            sendingProgress.setVisibility(View.VISIBLE);
        } else if (msg.getStatus() == MessageInfo.MSG_STATUS_SEND_FAIL) {
            //若发送失败，则显示红色感叹号（不显示发送中动画）
            statusImage.setVisibility(View.VISIBLE);
            sendingProgress.setVisibility(View.GONE);

        }
        //以上代码为zanhanding修改，用于fix视频消息发送失败后不显示红色感叹号的问题
        msgContentFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClicking) {
                    return;
                }
                sendingProgress.setVisibility(View.VISIBLE);
                mClicking = true;
                //以下代码为zanhanding修改，用于fix点击发送失败视频后无法播放，并且红色感叹号消失的问题
                final File videoFile = new File(videoPath);
                if (videoFile.exists()) {//若存在本地文件则优先获取本地文件
                    mAdapter.notifyItemChanged(position);
                    mClicking = false;
                    play(msg);
                    // 有可能播放的Activity还没有显示，这里延迟200ms，拦截压力测试的快速点击
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mClicking = false;
                        }
                    }, 200);
                } else {
                    getVideo(video, videoPath, msg, true, position);
                }
                //以上代码为zanhanding修改，用于fix点击发送失败视频后无法播放，并且红色感叹号消失的问题
            }
        });
    }

    private void getVideo(TIMVideo video, String videoPath, final MessageInfo msg, final boolean autoPlay, final int position) {
        video.getVideo(videoPath, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                ToastUtil.toastLongMessage("下载视频失败:" + code + "=" + desc);
                msg.setStatus(MessageInfo.MSG_STATUS_DOWNLOADED);
                sendingProgress.setVisibility(View.GONE);
                statusImage.setVisibility(View.VISIBLE);
                mAdapter.notifyItemChanged(position);
                mClicking = false;
            }

            @Override
            public void onSuccess() {
                mAdapter.notifyItemChanged(position);
                if (autoPlay) {
                    play(msg);
                }
                // 有可能播放的Activity还没有显示，这里延迟200ms，拦截压力测试的快速点击
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mClicking = false;
                    }
                }, 200);
            }
        });
    }

    private void play(final MessageInfo msg) {
        statusImage.setVisibility(View.GONE);
        sendingProgress.setVisibility(View.GONE);
        Intent intent = new Intent(TUIKit.getAppContext(), VideoViewActivity.class);
        intent.putExtra(TUIKitConstants.CAMERA_IMAGE_PATH, msg.getDataPath());
        intent.putExtra(TUIKitConstants.CAMERA_VIDEO_PATH, msg.getDataUri());
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        TUIKit.getAppContext().startActivity(intent);
    }

}
