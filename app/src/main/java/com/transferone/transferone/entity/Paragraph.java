package com.transferone.transferone.entity;

/**
 * Created by 99517 on 2017/6/19.
 */
public class Paragraph{
    private String articleid;//文章id
    private String content;//原段落内容
    private String translate;//译文内容
    private String image;//图片

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public String getArticleid() {
        return articleid;
    }

    public void setArticleid(String articleid) {
        this.articleid = articleid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
