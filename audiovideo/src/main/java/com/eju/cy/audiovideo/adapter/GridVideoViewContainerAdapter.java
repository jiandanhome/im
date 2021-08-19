//package com.eju.cy.audiovideo.adapter;
//
//import android.app.Activity;
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.util.DisplayMetrics;
//import android.view.SurfaceView;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//
//import com.eju.cy.audiovideo.dto.GroupAvCallDto;
//import com.eju.cy.audiovideo.dto.VideoInfoData;
//import com.tencent.rtmp.ui.TXCloudVideoView;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//public class GridVideoViewContainerAdapter extends VideoViewAdapter {
//
//
//    public GridVideoViewContainerAdapter(Activity activity, String localUid, HashMap<String, TXCloudVideoView> uids) {
//        super(activity, localUid, uids);
//    }
//
//    //组件初始化
//    @Override
//    protected void customizedInit(HashMap<String, TXCloudVideoView> uids, boolean force) {
//
//        if (force || mItemWidth == 0 || mItemHeight == 0) {
//
//            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
//            DisplayMetrics outMetrics = new DisplayMetrics();
//            windowManager.getDefaultDisplay().getMetrics(outMetrics);
//            int width = outMetrics.widthPixels;
//            int height = outMetrics.heightPixels;
//
//            //设置item  宽高
//            mItemWidth = width / 2;
//            mItemHeight = mItemWidth;
//        }
//
//
//    }
//
//    @Override
//    public void notifyUiChanged(HashMap<String, TXCloudVideoView> uids, String uidExtra, HashMap<Integer, Integer> status, HashMap<Integer, Integer> volume) {
//
//        setLocalUid(uidExtra);
//
//        notifyDataSetChanged();
//
//    }
//
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return super.onCreateViewHolder(parent, viewType);
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        super.onBindViewHolder(holder, position);
//    }
//
//    @Override
//    public int getItemCount() {
//        return mUsers.size();
//    }
//
//    public GroupAvCallDto getItem(int position) {
//        return mUsers.get(position);
//    }
//
//
//    /**
//     * 显示视频
//     */
//    private void composeDataItem1(final ArrayList<GroupAvCallDto> users, HashMap<String, TXCloudVideoView> uids, int localUid) {
//
//
//        for (HashMap.Entry<String, TXCloudVideoView> entry : uids.entrySet()) {
//            TXCloudVideoView surfaceView = entry.getValue();
//
//
//        }
//
//    }
//
//
//    private void searchUidsAndAppend(ArrayList<GroupAvCallDto> users, HashMap.Entry<String, SurfaceView> entry,
//                                     String localUid, Integer status, int volume, VideoInfoData i) {
//
//
//        if (null != entry.getKey() && entry.getKey().equals("0") || localUid.equals(entry.getKey())) {
//            boolean found = false;
//            for (GroupAvCallDto user : users) {
//                if (user.getImId().equals(entry.getKey()) || entry.getKey().equals(localUid)) {
//                    user.setImId(localUid);
//                }else {
//
//
//                }
//
//            }
//
//            if (!found) {
//               // users.add(0, new GroupAvCallDto(localUid, entry.getValue(), status, volume, i));
//            }
//
//
//        } else {
//
//
//        }
//
//
//    }
//
//
//}
