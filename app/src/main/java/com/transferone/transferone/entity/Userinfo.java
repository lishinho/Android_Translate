package com.transferone.transferone.entity;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 99517 on 2017/6/30.
 */
@AVClassName("Userinfo")
public class Userinfo extends AVObject{
    private String username;
    private String userhead;
    private String sex;
    private String interest;
    private String whatsup;
    private String profession;
    private String location;
    private List follow;
    private List collect;
    private int comment;
    private int translate;
    private int score;//积分

    public String getUsername() {
        return getString("username");
    }

    public void setUsername(String value) {
        put("username",value);
    }
    public String getUserhead() {
        return getString("userhead");
    }

    public void setUserhead(String value) {
        put("userhead",value);
    }
    public String getSex() {
        return getString("sex");
    }

    public void setSex(String value) {
        put("sex",value);
    }
    public String getInterest() {
        return getString("interest");
    }

    public void setInterest(String value) {
        put("interest",value);
    }
    public String getWhatsup() {
        return getString("whatsup");
    }

    public void setWhatsup(String value) {
        put("whatsup",value);
    }
    public String getProfession() {
        return getString("profession");
    }

    public void setProfession(String value) {
        put("profession",value);
    }
    public String getLocation() {
        return getString("location");
    }

    public void setLocation(String value) {
        put("location",value);
    }
    public List getFollow() {
        return getList("follow");
    }

    public void setFollow(List value) {
        put("follow",value);
    }
    public List getCollect() {
        return getList("collect");
    }

    public void setCollect(List value) {
        put("collect",value);
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public int getTranslate() {
        return translate;
    }

    public void setTranslate(int translate) {
        this.translate = translate;
    }

    public int getScore() {
        return getInt("score");
    }

    public void setScore(int score) {
        put("score",score);
    }
}
