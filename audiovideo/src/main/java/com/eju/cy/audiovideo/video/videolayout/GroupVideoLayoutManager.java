package com.eju.cy.audiovideo.video.videolayout;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Module:   TRTCVideoViewLayout
 * <p>
 * Function: {@link TXCloudVideoView} 的管理类
 * <p>
 * 1.在多人通话中，您的布局可能会比较复杂，Demo 也是如此，因此需要统一的管理类进行管理，这样子有利于写出高可维护的代码
 * <p>
 * 2.Demo 中提供堆叠布局、宫格布局两种展示方式；若您的项目也有相关的 UI 交互，您可以参考实现代码，能够快速集成。
 * <p>
 * 3.堆叠布局：{@link GroupVideoLayoutManager#makeFloatLayout()} 思路是初始化一系列的 x、y、padding、margin 组合 LayoutParams 直接对 View 进行定位
 * <p>
 * 4.宫格布局：{@link GroupVideoLayoutManager#makeGirdLayout(boolean)} 思路与堆叠布局一致，也是初始化一些列的 LayoutParams 直接对 View 进行定位
 * <p>
 * 5.如何实现管理：
 * A. 使用{@link TRTCLayoutEntity} 实体类，保存 {@link TRTCVideoLayout} 的分配信息，能够与对应的用户绑定起来，方便管理与更新UI
 * B. {@link TRTCVideoLayout} 专注实现业务 UI 相关的，控制逻辑放在此类中
 * <p>
 * 6.布局切换，见 {@link GroupVideoLayoutManager#switchMode()}
 * <p>
 * 7.堆叠布局与宫格布局参数，见{@link Utils} 工具类
 */
public class GroupVideoLayoutManager extends ViewGroup {
    public static final int MODE_FLOAT = 1;  // 前后堆叠模式
    public static final int MODE_GRID = 2;  // 九宫格模式
    public static final int MAX_USER = 9;
    private final static String TAG = GroupVideoLayoutManager.class.getSimpleName();

    private LinkedList<TRTCLayoutEntity> mLayoutEntityList;//存放显示流以及无流layout
    private ArrayList<LayoutParams> mFloatParamList;
    private ArrayList<LayoutParams> mGrid4ParamList;
    private ArrayList<LayoutParams> mGrid9ParamList;
    private int mCount = 0;
    private int mMode;
    private String mSelfUserId;//当前用户ID
    private Context mContext;

    /**
     * ===============================View相关===============================
     */
    public GroupVideoLayoutManager(Context context) {
        this(context, null);
    }


    public GroupVideoLayoutManager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }


    private void initView(Context context) {
        Log.i(TAG, "initView: ");

        mLayoutEntityList = new LinkedList<>();
        // 默认为堆叠模式
        mMode = MODE_GRID;
        this.post(new Runnable() {
            @Override
            public void run() {
                makeGirdLayout(true);
            }
        });
    }

    public void setMySelfUserId(String userId) {
        mSelfUserId = userId;
    }

    /**
     * 宫格布局与悬浮布局切换
     *
     * @return
     */
    public int switchMode() {


        if (mMode == MODE_FLOAT) {
            mMode = MODE_GRID;
            makeGirdLayout(true);
        } else {
            mMode = MODE_FLOAT;
            makeFloatLayout();
        }
        return mMode;
    }

    /**
     * 根据 userId 找到已经分配的 View
     *
     * @param userId
     * @return
     */
    public TRTCVideoLayout findCloudViewView(String userId) {
        if (userId == null) return null;
        for (TRTCLayoutEntity layoutEntity : mLayoutEntityList) {
            if (layoutEntity.userId.equals(userId)) {
                return layoutEntity.layout;
            }
        }
        return null;
    }

    /**
     * 根据 userId 分配对应的 view
     *
     * @param userId
     * @return
     */
    public TRTCVideoLayout allocCloudVideoView(String userId) {
        if (userId == null) return null;

        TRTCLayoutEntity layoutEntity = new TRTCLayoutEntity();
        layoutEntity.userId = userId;
        layoutEntity.layout = new TRTCVideoLayout(mContext);
        layoutEntity.layout.setVisibility(VISIBLE);
        initGestureListener(layoutEntity.layout);
        mLayoutEntityList.add(layoutEntity);
        addView(layoutEntity.layout);
        mCount++;
        switchModeInternal();
        return layoutEntity.layout;
    }

    //九宫格
    private void switchModeInternal() {
        mMode = MODE_GRID;
        makeGirdLayout(true);
        return;
    }


    private void initGestureListener(final TRTCVideoLayout layout) {

    }


    /**
     * 根据 userId 和 视频类型，回收对应的 view
     *
     * @param userId
     */
    public void recyclerCloudViewView(String userId) {
        if (userId == null) return;
        if (mMode == MODE_FLOAT) {
            TRTCLayoutEntity entity = mLayoutEntityList.get(mLayoutEntityList.size() - 1);
            // 当前离开的是处于0号位的人，那么需要将我换到这个位置
            if (userId.equals(entity.userId)) {
                makeFullVideoView(mSelfUserId);
            }
        } else {
        }
        Iterator iterator = mLayoutEntityList.iterator();
        while (iterator.hasNext()) {
            TRTCLayoutEntity item = (TRTCLayoutEntity) iterator.next();
            if (item.userId.equals(userId)) {
                removeView(item.layout);
                iterator.remove();
                mCount--;
                break;
            }
        }
        switchModeInternal();
    }

    /**
     * 隐藏所有音量的进度条
     */
    public void hideAllAudioVolumeProgressBar() {
        for (TRTCLayoutEntity entity : mLayoutEntityList) {
            entity.layout.setAudioVolumeProgressBarVisibility(View.GONE);
        }
    }

    /**
     * 显示所有音量的进度条
     */
    public void showAllAudioVolumeProgressBar() {
        for (TRTCLayoutEntity entity : mLayoutEntityList) {
            entity.layout.setAudioVolumeProgressBarVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置当前音量
     *
     * @param userId
     * @param audioVolume
     */
    public void updateAudioVolume(String userId, int audioVolume) {
        if (userId == null) return;
        for (TRTCLayoutEntity entity : mLayoutEntityList) {
            if (entity.layout.getVisibility() == VISIBLE) {
                if (userId.equals(entity.userId)) {
                    entity.layout.setAudioVolumeProgress(audioVolume);
                }
            }
        }
    }

    private TRTCLayoutEntity findEntity(TRTCVideoLayout layout) {
        for (TRTCLayoutEntity entity : mLayoutEntityList) {
            if (entity.layout == layout) return entity;
        }
        return null;
    }

    private TRTCLayoutEntity findEntity(String userId) {
        for (TRTCLayoutEntity entity : mLayoutEntityList) {
            if (entity.userId.equals(userId)) return entity;
        }
        return null;
    }

    /**
     * 切换到九宫格布局
     *
     * @param needUpdate 是否需要更新布局
     */
    private void makeGirdLayout(boolean needUpdate) {

    }


    /**
     * ===============================九宫格布局相关===============================
     */

    /**
     * 切换到堆叠布局：
     * 1. 如果堆叠布局参数未初始化先进行初始化：大画面+左右各三个画面
     * 2. 修改布局参数
     */
    private void makeFloatLayout() {

    }

    /**
     * ===============================堆叠布局相关===============================
     */

    /**
     * 对堆叠布局情况下的 View 添加监听器
     * <p>
     * 用于点击切换两个 View 的位置
     */
    private void addFloatViewClickListener(final TRTCLayoutEntity entity) {
        final String userId = entity.userId;
        entity.layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(userId)) {
                    makeFullVideoView(userId);
                }
            }
        });
    }

    /**
     * 堆叠模式下，将 userId 的 view 换到 0 号位，全屏化渲染
     *
     * @param userId
     */
    private void makeFullVideoView(String userId) {
        Log.i(TAG, "makeFullVideoView: from = " + userId);
        TRTCLayoutEntity entity = findEntity(userId);
        mLayoutEntityList.remove(entity);
        mLayoutEntityList.addLast(entity);
        makeFloatLayout();
    }

    private static class TRTCLayoutEntity {
        public TRTCVideoLayout layout;
        public String userId = "";
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        //控件总宽度
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = 0;
        int size = mLayoutEntityList.size();



        for (int i = 0; i < size; i++) {
            height += width/2;
        }





    }
}
