package com.transferone.transferone.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 99517 on 2017/5/2.
 */

public class DayTranslateCard implements Parcelable {
    private String title;//标题
    private String username;//用户名
    private String context;//正文
    private String share;//分享
    private String commit;//评论
    private String like;//点赞
    private String headurl;//用户头像url
    private String Paragraph_ID ;//对应段落的objectID
    private String translate;  //原文
    private String paragraph_translateID; //当前翻译段落ID
    private String isLike;  //是否被当前用户点赞
    private String original; //译文
    private Date  date;  // 段落创建的时间
    private Date  update;  // 段落更新的时间
    private String count;  //数据对应的记数值
    private String userid;//用户id
    @Override
    public int describeContents() {

        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dest.writeString(title);
        dest.writeString(username);
        dest.writeString(context);
        dest.writeString(share);
        dest.writeString(commit);
        dest.writeString(headurl);
        dest.writeString(Paragraph_ID);
        dest.writeString(like);
        dest.writeString(translate);
        dest.writeString(paragraph_translateID);
        dest.writeString(isLike);
        dest.writeString(sdf.format(date));
        dest.writeString(original);
        dest.writeString(sdf.format(update));
        dest.writeString(count);
        dest.writeString(userid);
    }

    public static final Parcelable.Creator<DayTranslateCard> CREATOR = new Parcelable.Creator<DayTranslateCard>() {

        @Override
        public DayTranslateCard createFromParcel(Parcel source) {
            DayTranslateCard d = new DayTranslateCard();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            d.title = source.readString();
            d.username = source.readString();
            d.context = source.readString();
            d.share = source.readString();
            d.commit = source.readString();
            d.headurl = source.readString();
            d.Paragraph_ID = source.readString();
            d.like = source.readString();
            d.translate=source.readString();
            d.paragraph_translateID=source.readString();
            d.isLike = source.readString();
            try {
                d.date = sdf.parse(source.readString());
            }catch (java.text.ParseException e){
                d.date = null;
            }
            d.original=source.readString();
            try {
                d.update = sdf.parse(source.readString());
            }catch (java.text.ParseException e){
                d.update = null;
            }
            d.count=source.readString();
            d.userid = source.readString();
            return d;

        }

        @Override
        public DayTranslateCard[] newArray(int size) {
            return null;
        }
    };

    public String getParagraph_translateID(){
        return paragraph_translateID;
    }

    public void setParagraph_translateID(String paragraph_translateID){
        this.paragraph_translateID=paragraph_translateID;
    }
    public int getCount(){
        return Integer.parseInt(count);
    }

    public void setCount(int count){
        this.count=count+"";
    }

    public int getIsLike(){
        return Integer.parseInt(isLike);
    }

    public void setIsLike(int isLike){
        this.isLike=isLike+"";
    }

    public String getTranslate(){
        return translate;
    }

    public void setTranslate(String translate){
        this.translate=translate;
    }

    public String getOriginal(){
        return original;
    }

    public void setOriginal(String original){
        this.original=original;
    }
    public String getParagraph_ID(){
        return Paragraph_ID;
    }

    public void setParagraph_ID(String Paragraph_ID){
        this.Paragraph_ID=Paragraph_ID;
    }

    public Date getUpdate(){
        return update;
    }

    public void setUpdate(Date update){
        this.update=update;
    }

    public Date getDate(){
        return date;
    }

    public void setDate(Date date){
        this.date=date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public String getHeadurl() {
        return headurl;
    }

    public void setHeadurl(String headurl) {
        this.headurl = headurl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getShare() {
        return Integer.parseInt(share);
    }

    public void setShare(int share) {
        this.share = share+"";
    }

    public int getLike() {
        return Integer.parseInt(like);
    }

    public void setLike(int like) {
        this.like = like+"";
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getComment() {
        return Integer.parseInt(commit);


    }

    public void setComment(int commit) {
        this.commit = commit+"";
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
