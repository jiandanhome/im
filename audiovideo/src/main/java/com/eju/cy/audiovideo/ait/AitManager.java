package com.eju.cy.audiovideo.ait;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.blankj.utilcode.util.LogUtils;
import com.eju.cy.audiovideo.ait.activity.AitContactSelectorActivity;
import com.eju.cy.audiovideo.dto.GuoupMemberDto;
import com.eju.cy.audiovideo.utils.JsonUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ Name: Caochen
 * @ Date: 2020-08-05
 * @ Time: 13:37
 * @ Description： @管理类
 */

public class AitManager implements TextWatcher {

    private Context context;

    private String tid;

    private boolean robot;

    private AitContactsModel aitContactsModel;

    private int curPos;

    private boolean ignoreTextChange = false;

    private AitTextChangeListener listener;

    public AitManager(Context context, String tid, boolean robot) {
        this.context = context;
        this.tid = tid;
        this.robot = robot;
        aitContactsModel = new AitContactsModel();
    }

    public void setTextChangeListener(AitTextChangeListener listener) {
        this.listener = listener;
    }

    public List<String> getAitTeamMember() {
        return aitContactsModel.getAitTeamMember();
    }

    public String getAitRobot() {
        return aitContactsModel.getFirstAitRobot();
    }

    List<GuoupMemberDto.DataBean> aitLit = new ArrayList<>();

    public String removeRobotAitString(String text, String robotAccount) {
        AitBlock block = aitContactsModel.getAitBlock(robotAccount);
        if (block != null) {
            return text.replaceAll(block.text, "");
        } else {
            return text;
        }
    }

    public void reset() {
        aitLit.clear();
        aitContactsModel.reset();
        ignoreTextChange = false;
        curPos = 0;
    }

    /**
     * ------------------------------ 增加@成员 --------------------------------------
     */

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AitContactSelectorActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            int type = data.getIntExtra(AitContactSelectorActivity.RESULT_TYPE, -1);
            String userImId = "";
            String name = "";
            if (null != data) {


                //@单个人
                if (data.getSerializableExtra(AitContactSelectorActivity.RESULT_DATA) instanceof GuoupMemberDto.DataBean) {
                    GuoupMemberDto.DataBean member = (GuoupMemberDto.DataBean) data.getSerializableExtra(AitContactSelectorActivity.RESULT_DATA);
                    userImId = member.getIm_user();
                    name = member.getNick_name();
                    insertAitMemberInner(userImId, name, type, curPos, false);
                } else {
                    //@所有人
                    Type gsonType = new TypeToken<List<GuoupMemberDto.DataBean>>() {
                    }.getType();
                    List<GuoupMemberDto.DataBean> aitLit = (List<GuoupMemberDto.DataBean>) JsonUtils.fromJson(data.getStringExtra(AitContactSelectorActivity.RESULT_DATA), gsonType);

                    if (null != aitLit && aitLit.size() > 0) {
                        insertAitMemberInner("8888", "All", type, curPos, false);
                        setAitList(aitLit);
                    }

                    // insertAitMemberInner("8888", "All", type, curPos, false);
                    LogUtils.w("全部人大小" + aitLit.size());
                }
            }


        }
    }

    // 群昵称 > 用户昵称 > 账号
//    private static String getAitTeamMemberName(TeamMember member) {
//        if (member == null) {
//            return "";
//        }
//        String memberNick = member.getTeamNick();
//        if (!TextUtils.isEmpty(memberNick)) {
//            return memberNick;
//        }
//        return UserInfoHelper.getUserName(member.getAccount());
//    }

    public void insertAitRobot(String account, String name, int start) {
        insertAitMemberInner(account, name, AitContactType.ROBOT, start, true);
    }

    private void insertAitMemberInner(String account, String name, int type, int start, boolean needInsertAitInText) {
        name = name + " ";
        String content = needInsertAitInText ? "@" + name : name;
        if (listener != null) {
            // 关闭监听
            ignoreTextChange = true;
            // insert 文本到editText
            listener.onTextAdd(content, start, content.length());
            // 开启监听
            ignoreTextChange = false;
        }

        // update 已有的 aitBlock
        aitContactsModel.onInsertText(start, content);

        int index = needInsertAitInText ? start : start - 1;
        // 添加当前到 aitBlock
        aitContactsModel.addAitMember(account, name, type, index);


        LogUtils.w("添加完了----" + aitContactsModel.getAitBlocks());
    }


    public void setAitList(List<GuoupMemberDto.DataBean> list) {

        aitLit = list;
    }

    public   List<GuoupMemberDto.DataBean> getAitLit(){

        return  aitLit;
    }
    public Map<String, AitBlock> getAitBlocks() {


        return aitContactsModel.getAitBlocks();
    }

    /**
     * ------------------------------ editText 监听 --------------------------------------
     */

    // 当删除尾部空格时，删除一整个segment,包含界面上也删除
    private boolean deleteSegment(int start, int count) {
        if (count != 1) {
            return false;
        }
        boolean result = false;
        AitBlock.AitSegment segment = aitContactsModel.findAitSegmentByEndPos(start);
        if (segment != null) {
            int length = start - segment.start;
            if (listener != null) {
                ignoreTextChange = true;
                listener.onTextDelete(segment.start, length);
                ignoreTextChange = false;
            }
            aitContactsModel.onDeleteText(start, length);
            result = true;
        }
        return result;
    }

    /**
     * @param editable 变化后的Editable
     * @param start    text 变化区块的起始index
     * @param count    text 变化区块的大小
     * @param delete   是否是删除
     */
    private void afterTextChanged(Editable editable, int start, int count, boolean delete) {


        LogUtils.w("afterTextChanged", "检测是否有@");
        curPos = delete ? start : count + start;
        if (ignoreTextChange) {
            return;
        }
        if (delete) {
            int before = start + count;
            if (deleteSegment(before, count)) {
                return;
            }
            aitContactsModel.onDeleteText(before, count);

        } else {
            if (count <= 0 || editable.length() < start + count) {
                return;
            }
            CharSequence s = editable.subSequence(start, start + count);
            if (s == null) {
                return;
            }
            if (s.toString().equals("@")) {
                // 启动@联系人界面
                if (!TextUtils.isEmpty(tid)) {
                    AitContactSelectorActivity.start(context, tid, true);
                }
            }
            aitContactsModel.onInsertText(start, s.toString());
        }
    }

    private int editTextStart;
    private int editTextCount;
    private int editTextBefore;
    private boolean delete;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        LogUtils.w("beforeTextChanged-------", "\ncount-----" + count + "\nafter----" + after);
        delete = count > after;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        this.editTextStart = start;
        this.editTextCount = count;
        this.editTextBefore = before;
    }

    @Override
    public void afterTextChanged(Editable s) {
        afterTextChanged(s, editTextStart, delete ? editTextBefore : editTextCount, delete);
    }
}
