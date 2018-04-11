package com.transferone.transferone.entity;

/**
 * Created by 99517 on 2017/6/29.
 */

public class RankItem {
    public String username;
    public String userhead;
    public String share;
    public String like;
    public String comment;
    public String paragraph_translateID;

    public void setParagraph_translateID(String paragraph_translateID) {
        this.paragraph_translateID = paragraph_translateID;
    }

    public String getParagraph_translateID() {
        return paragraph_translateID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserhead() {
        return userhead;
    }

    public void setUserhead(String userhead) {
        this.userhead = userhead;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
