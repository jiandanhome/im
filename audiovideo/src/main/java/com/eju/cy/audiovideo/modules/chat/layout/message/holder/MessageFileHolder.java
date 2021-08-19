package com.eju.cy.audiovideo.modules.chat.layout.message.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.R;
import com.eju.cy.audiovideo.dto.ImActionDto;
import com.eju.cy.audiovideo.modules.message.MessageInfo;
import com.eju.cy.audiovideo.observer.EjuHomeImEventCar;
import com.eju.cy.audiovideo.tags.ActionTags;
import com.eju.cy.audiovideo.utils.FileUtil;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.eju.cy.audiovideo.utils.ToastUtil;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMFileElem;

public class MessageFileHolder extends MessageContentHolder {

    private TextView fileNameText;
    private TextView fileSizeText;
    private TextView fileStatusText;
    private ImageView fileIconImage;
    private LinearLayout ll_bg;

    public MessageFileHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getVariableLayout() {
        return R.layout.message_adapter_content_file;
    }

    @Override
    public void initVariableViews() {
        fileNameText = rootView.findViewById(R.id.file_name_tv);
        fileSizeText = rootView.findViewById(R.id.file_size_tv);
        fileStatusText = rootView.findViewById(R.id.file_status_tv);
        fileIconImage = rootView.findViewById(R.id.file_icon_iv);

        ll_bg = rootView.findViewById(R.id.ll_bg);
    }

    @Override
    public void layoutVariableViews(final MessageInfo msg, final int position) {
        TIMElem elem = msg.getElement();
        if (!(elem instanceof TIMFileElem)) {
            return;
        }
        final TIMFileElem fileElem = (TIMFileElem) elem;
        final String path = msg.getDataPath();
        fileNameText.setText(fileElem.getFileName());
        String size = FileUtil.FormetFileSize(fileElem.getFileSize());
        fileSizeText.setText(size);
        msgContentFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastUtil.toastLongMessage("文件路径:" + path);
                LogUtils.w("文件路径:" + path);
                sendFilePath(path);
            }
        });



        if (msg.isSelf()) {
            //自己
            ll_bg.setBackground(rootView.getResources().getDrawable(R.drawable.icon_mf_my_message));
        } else {
            //非自己
            ll_bg.setBackground(rootView.getResources().getDrawable(R.drawable.icon_mf_other_message));
        }



        if (msg.getStatus() == MessageInfo.MSG_STATUS_SEND_SUCCESS || msg.getStatus() == MessageInfo.MSG_STATUS_NORMAL) {
            fileStatusText.setText(R.string.sended);
        } else if (msg.getStatus() == MessageInfo.MSG_STATUS_DOWNLOADING) {
            fileStatusText.setText(R.string.downloading);
        } else if (msg.getStatus() == MessageInfo.MSG_STATUS_DOWNLOADED) {
            fileStatusText.setText(R.string.downloaded);
        } else if (msg.getStatus() == MessageInfo.MSG_STATUS_UN_DOWNLOAD) {
            fileStatusText.setText(R.string.un_download);
            msgContentFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    msg.setStatus(MessageInfo.MSG_STATUS_DOWNLOADING);
                    sendingProgress.setVisibility(View.VISIBLE);
                    fileStatusText.setText(R.string.downloading);
                    fileElem.getToFile(path, new TIMCallBack() {
                        @Override
                        public void onError(int code, String desc) {
                            ToastUtil.toastLongMessage("getToFile fail:" + code + "=" + desc);
                            fileStatusText.setText(R.string.un_download);
                            sendingProgress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onSuccess() {
                            msg.setDataPath(path);
                            fileStatusText.setText(R.string.downloaded);
                            msg.setStatus(MessageInfo.MSG_STATUS_DOWNLOADED);
                            sendingProgress.setVisibility(View.GONE);
                            msgContentFrame.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // ToastUtil.toastLongMessage("文件路径:" + path);
                                    LogUtils.w("文件路径:" + path);
                                    sendFilePath(path);

                                }
                            });
                        }
                    });
                }
            });
        }
    }

    /**
     * 发送文件被点击地址
     *
     * @param path
     */
    private void sendFilePath(String path) {

        ImActionDto imActionDto = new ImActionDto();
        imActionDto.setAction(ActionTags.ACTION_CLICK_FILE_MESSAGE_PATH);
        imActionDto.setJsonStr(path);
        EjuHomeImEventCar.getDefault().post(JsonUtils.toJson(imActionDto));

    }

}
