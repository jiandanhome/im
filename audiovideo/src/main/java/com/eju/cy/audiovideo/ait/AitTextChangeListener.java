package com.eju.cy.audiovideo.ait;

/**
 * @ Name: Caochen
 * @ Date: 2020-08-05
 * @ Time: 13:37
 * @ Description：  @监听
 */

public interface AitTextChangeListener {

    void onTextAdd(String content, int start, int length);

    void onTextDelete(int start, int length);
}
