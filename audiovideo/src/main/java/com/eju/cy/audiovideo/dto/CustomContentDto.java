package com.eju.cy.audiovideo.dto;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送自定义消息所需字段
 */
public class CustomContentDto {


    /*--------------名片-----------------*/
    private String cardName = "";  //经纪人名字
    private String phoneNumber = "";//经纪人手机号码
    private String portraitUrl = "";//经纪人头像地址


    private String shopName = ""; //门面名称
    private String shopAddress = "";//门面地址
    private String slogan = ""; //名片口号
    private String position = "";//职位






    /*--------------房源-----------------*/

    private String title="";//标题
    private String houseUrl; //房源显示的图片


    /*--------------@功能-----------------*/
    private String Action12Text = "";//@功能信息文字内容
    private String idAndName = "";// map  String /被@的人  id   name
    private Map<String, String> aitMap = new HashMap<>();//被@的人  id   name


    /*--------------分享文章---------------*/
    //title
    private String contentUrl = "";//文章地址


    /*--------------评测报告---------------*/
    private String stopActionDate;//截至统计时间





    public String getStopActionDate() {
        return stopActionDate;
    }

    public void setStopActionDate(String stopActionDate) {
        this.stopActionDate = stopActionDate;
    }

    @Override
    public String toString() {
        return "CustomContentDto{" +
                "cardName='" + cardName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", portraitUrl='" + portraitUrl + '\'' +
                ", shopName='" + shopName + '\'' +
                ", shopAddress='" + shopAddress + '\'' +
                ", slogan='" + slogan + '\'' +
                ", position='" + position + '\'' +
                ", title='" + title + '\'' +
                ", houseUrl='" + houseUrl + '\'' +
                ", Action12Text='" + Action12Text + '\'' +
                ", aitMap=" + aitMap +
                '}';
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }


    public String getIdAndName() {
        return idAndName;
    }

    public void setIdAndName(String idAndName) {
        this.idAndName = idAndName;
    }

    public String getAction12Text() {
        return Action12Text;
    }

    public void setAction12Text(String action12Text) {
        Action12Text = action12Text;
    }

    public Map<String, String> getAitMap() {

        if (!"".equals(idAndName)) {
            aitMap = new Gson().fromJson(idAndName, new TypeToken<Map<String, String>>() {
            }.getType());
        }
        return aitMap;
    }

    public void setAitMap(Map<String, String> aitMap) {
        this.aitMap = aitMap;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPortraitUrl() {
        return portraitUrl;
    }

    public void setPortraitUrl(String portraitUrl) {
        this.portraitUrl = portraitUrl;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHouseUrl() {
        return houseUrl;
    }

    public void setHouseUrl(String houseUrl) {
        this.houseUrl = houseUrl;
    }
}
