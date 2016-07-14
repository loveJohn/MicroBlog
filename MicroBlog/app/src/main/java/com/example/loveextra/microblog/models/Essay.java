package com.example.loveextra.microblog.models;

import java.util.ArrayList;

/**
 * Created by LoveExtra on 2016/7/8.
 */
public class Essay {

    private String id;          //文章id
    private String typeName;        //文章类型
    private String title;           //标题
    private String contentImg;      //配图
    private String userLogo;        //公众号logo
    private String userName;        //公众号
    private String date;            //时间
    private String typeId;          //类型号
    private String url;             //文章url
    private String weixinNum;       //微信号
    private String userLogo_code;

    public Essay(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentImg() {
        return contentImg;
    }

    public void setContentImg(String contentImg) {
        this.contentImg = contentImg;
    }

    public String getUserLogo() {
        return userLogo;
    }

    public void setUserLogo(String userLogo) {
        this.userLogo = userLogo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWeixinNum() {
        return weixinNum;
    }

    public void setWeixinNum(String weixinNum) {
        this.weixinNum = weixinNum;
    }

    public String getUserLogo_code() {
        return userLogo_code;
    }

    public void setUserLogo_code(String userLogo_code) {
        this.userLogo_code = userLogo_code;
    }

    @Override
    public String toString() {
        return "Essay{" +
                "id='" + id + '\'' +
                ", typeName='" + typeName + '\'' +
                ", title='" + title + '\'' +
                ", contentImg='" + contentImg + '\'' +
                ", userLogo='" + userLogo + '\'' +
                ", userName='" + userName + '\'' +
                ", date='" + date + '\'' +
                ", typeId='" + typeId + '\'' +
                ", url='" + url + '\'' +
                ", weixinNum='" + weixinNum + '\'' +
                ", userLogo_code='" + userLogo_code + '\'' +
                '}';
    }
}
