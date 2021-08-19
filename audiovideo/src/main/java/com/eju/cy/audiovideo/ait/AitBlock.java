package com.eju.cy.audiovideo.ait;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
* @ Name: Caochen
* @ Date: 2020-08-05
* @ Time: 13:36
* @ Description：  表示一个ait块
*/

public class AitBlock {

    /**
     * text = "@" + name
     */
    public String text;

    /**
     * 类型，群成员/机器人
     */
    public int aitType;


    public  String imId;

    @Override
    public String toString() {
        return "AitBlock{" +
                "text='" + text + '\'' +
                ", aitType=" + aitType +
                ", imId='" + imId + '\'' +
                ", segments=" + segments +
                '}';
    }

    /**
     * 在文本中的位置
     */
    public List<AitSegment> segments = new ArrayList<>();

    public AitBlock(String name, int aitType,String imId) {
        this.text = "@" + name;
        this.aitType = aitType;
        this.imId = imId;
    }

    // 新增 segment
    public AitSegment addSegment(int start) {
        int end = start + text.length() - 1;
        AitSegment segment = new AitSegment(start, end);
        segments.add(segment);
        return segment;
    }

    /**
     * @param start      起始光标位置
     * @param changeText 插入文本
     */
    public void moveRight(int start, String changeText) {
        if (changeText == null) {
            return;
        }
        int length = changeText.length();
        Iterator<AitSegment> iterator = segments.iterator();
        while (iterator.hasNext()) {
            AitSegment segment = iterator.next();
            // 从已有的一个@块中插入
            if (start > segment.start && start <= segment.end) {
                segment.end += length;
                segment.broken = true;
            } else if (start <= segment.start) {
                segment.start += length;
                segment.end += length;
            }
        }
    }

    /**
     * @param start  删除前光标位置
     * @param length 删除块的长度
     */
    public void moveLeft(int start, int length) {
        int after = start - length;
        Iterator<AitSegment> iterator = segments.iterator();

        while (iterator.hasNext()) {
            AitSegment segment = iterator.next();
            // 从已有@块中删除
            if (start > segment.start) {
                // @被删除掉
                if (after <= segment.start) {
                    iterator.remove();
                } else if (after <= segment.end) {
                    segment.broken = true;
                    segment.end -= length;
                }
            } else if (start <= segment.start) {
                segment.start -= length;
                segment.end -= length;
            }
        }
    }

    // 获取该账号所有有效的@块最靠前的start
    public int getFirstSegmentStart() {
        int start = -1;
        for (AitSegment segment : segments) {
            if (segment.broken) {
                continue;
            }
            if (start == -1 || segment.start < start) {
                start = segment.start;
            }
        }
        return start;
    }

    public AitSegment findLastSegmentByEnd(int end) {
        int pos = end - 1;
        for (AitSegment segment : segments) {
            if (!segment.broken && segment.end == pos) {
                return segment;
            }
        }
        return null;
    }

    public boolean valid() {
        if (segments.size() == 0) {
            return false;
        }
        for (AitSegment segment : segments) {
            if (!segment.broken) {
                return true;
            }
        }
        return false;
    }

    public static class AitSegment {
        /**
         * 位于文本起始位置(include)
         */
        public int start;

        /**
         * 位于文本结束位置(include)
         */
        public int end;

        /**
         * 是否坏掉
         */
        public boolean broken = false;

        public AitSegment(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}
