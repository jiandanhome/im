package com.eju.cy.audiovideo.trtcs.manager;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.TUIKit;
import com.eju.cy.audiovideo.component.AudioPlayer;
import com.eju.cy.audiovideo.utils.TUIKitConstants;
import com.eju.cy.audiovideo.utils.TUIKitLog;
import com.eju.cy.audiovideo.utils.ToastUtil;

/**
 * 接收通话播放背景声
 */
public class AudioPlayerManager {

    private static final String TAG = AudioPlayer.class.getSimpleName();
    private static AudioPlayerManager sInstance = new AudioPlayerManager();
    private static String CURRENT_RECORD_FILE = TUIKitConstants.RECORD_DIR + "auto_";
    private static int MAGIC_NUMBER = 500;
    private static int MIN_RECORD_DURATION = 1000;
    private AudioPlayer.Callback mRecordCallback;
    private AudioPlayer.Callback mPlayCallback;

    private String mAudioRecordPath;
    private MediaPlayer mPlayer;
    private MediaRecorder mRecorder;
    private Handler mHandler;

    private AudioPlayerManager() {
        mHandler = new Handler();
    }

    public static AudioPlayerManager getInstance() {
        return sInstance;
    }


    public void startRecord(AudioPlayer.Callback callback) {
        mRecordCallback = callback;
        try {
            mAudioRecordPath = CURRENT_RECORD_FILE + System.currentTimeMillis() + ".m4a";
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 使用mp4容器并且后缀改为.m4a，来兼容小程序的播放
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setOutputFile(mAudioRecordPath);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.prepare();
            mRecorder.start();
            // 最大录制时间之后需要停止录制
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopInternalRecord();
                    onRecordCompleted(true);
                    mRecordCallback = null;
                    ToastUtil.toastShortMessage("已达到最大语音长度");
                }
            }, TUIKit.getConfigs().getGeneralConfig().getAudioRecordMaxTime() * 1000);
        } catch (Exception e) {
            TUIKitLog.w(TAG, "startRecord failed", e);
            stopInternalRecord();
            onRecordCompleted(false);
        }
    }

    public void stopRecord() {
        stopInternalRecord();
        onRecordCompleted(true);
        mRecordCallback = null;
    }

    private void stopInternalRecord() {
        mHandler.removeCallbacksAndMessages(null);
        if (mRecorder == null) {
            return;
        }
        mRecorder.release();
        mRecorder = null;
    }

    public void startPlay(String filePath, AudioPlayer.Callback callback) {
        mAudioRecordPath = filePath;
        mPlayCallback = callback;
        try {
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(filePath);
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopInternalPlay();
                    onPlayCompleted(true);
                }
            });
            mPlayer.prepare();
            mPlayer.start();
        } catch (Exception e) {
            TUIKitLog.w(TAG, "startPlay failed", e);
            ToastUtil.toastLongMessage("语音文件已损坏或不存在");
            stopInternalPlay();
            onPlayCompleted(false);
        }
    }

    public void startPlay(AssetFileDescriptor assetFileDescriptor, boolean isLooping) {


        try {

            mPlayer = new MediaPlayer();

            mPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());

            mPlayer.setLooping(isLooping);

            mPlayer.prepare();
            mPlayer.start();
            LogUtils.w("播放--11111111");
        } catch (Exception e) {
            LogUtils.w("播放--2222222");
            TUIKitLog.w(TAG, "startPlay failed", e);
            ToastUtil.toastLongMessage("语音文件已损坏或不存在");
            stopInternalPlay();
            onPlayCompleted(false);
        }

    }

    public void startPlay(Context context, int resid, AudioPlayer.Callback callback, boolean isLooping) {
        mPlayCallback = callback;
        try {
            mPlayer = MediaPlayer.create(context, resid);

            mPlayer.setLooping(isLooping);
            mPlayer.start();
        } catch (Exception e) {
            TUIKitLog.w(TAG, "startPlay failed", e);
            ToastUtil.toastLongMessage("语音文件已损坏或不存在");
            stopInternalPlay();
            onPlayCompleted(false);
        }
    }

    public void stopMyPlay() {
        if (null != mPlayer) {
            mPlayer.stop();
        }


    }

    public void stopReleasePlay() {
        if (mPlayer == null) {
            return;
        }

        mPlayer.release();
        mPlayer = null;

    }


    public void stopPlay() {
        LogUtils.w("停止播放-----222222");
        stopInternalPlay();
        onPlayCompleted(false);
        mPlayCallback = null;
    }

    private void stopInternalPlay() {
        if (mPlayer == null) {
            return;
        }
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
    }

    public boolean isPlaying() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            return true;
        }
        return false;
    }

    private void onPlayCompleted(boolean success) {
        if (mPlayCallback != null) {
            mPlayCallback.onCompletion(success);
        }
        mPlayer = null;
    }

    private void onRecordCompleted(boolean success) {
        if (mRecordCallback != null) {
            mRecordCallback.onCompletion(success);
        }
        mRecorder = null;
    }

    public String getPath() {
        return mAudioRecordPath;
    }

    public int getDuration() {
        if (TextUtils.isEmpty(mAudioRecordPath)) {
            return 0;
        }
        int duration = 0;
        // 通过初始化播放器的方式来获取真实的音频长度
        try {
            MediaPlayer mp = new MediaPlayer();
            mp.setDataSource(mAudioRecordPath);
            mp.prepare();
            duration = mp.getDuration();
            // 语音长度如果是59s多，因为外部会/1000取整，会一直显示59'，所以这里对长度进行处理，达到四舍五入的效果
            if (duration < MIN_RECORD_DURATION) {
                duration = 0;
            } else {
                duration = duration + MAGIC_NUMBER;
            }
        } catch (Exception e) {
            TUIKitLog.w(TAG, "getDuration failed", e);
        }
        if (duration < 0) {
            duration = 0;
        }
        return duration;
    }

    public interface Callback {
        void onCompletion(Boolean success);
    }

}