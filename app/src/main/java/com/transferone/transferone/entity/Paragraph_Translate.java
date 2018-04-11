package com.transferone.transferone.entity;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

/**
 * Created by 99517 on 2017/5/3.
 */
@AVClassName("Paragraph_Translate")
public class Paragraph_Translate extends AVObject{
    private String paragraph;//段落内容(可不需要)
    private String paragraphid;//段落id
    private String translate;//译文
    private String commit;//译文
    private String like;//点赞
    private String share;//分享
    private String title;//文章标题

    public String getParagraph(){
        return getString("paragraph");
    }
    public void setParagraph(String value) {
        put("paragraph", value);
    }
    public String getTranslate(){
        return getString("translate");
    }
    public void setTranslate(String value) {
        put("translate", value);
    }
    public String getParagraphid() {
        return getString("paragraphid");
    }
    public void setParagraphid(String value) {
        put("paragraphid",value);
    }
    public String getCommit() {
        return getString("commit");
    }
    public void setCommit(String value) {
        put("commit",value);
    }
    public String getLike() {
        return getString("like");
    }
    public void setLike(String value) {
        put("like",value);
    }
    public String getShare() {
        return getString("share");
    }
    public void setShare(String value) {
        put("share",value);
    }
    public String getTitle() {
        return getString("title");
    }
    public void setTitle(String value) {
        put("title",value);
    }
}
