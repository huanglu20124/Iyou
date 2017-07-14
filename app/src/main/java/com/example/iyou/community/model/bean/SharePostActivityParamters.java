package com.example.iyou.community.model.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cyhaha on 2017/2/16.
 */

public class SharePostActivityParamters {

    private String account;
    private String timestamp;
    private String title;
    private String NoteType;
    private String content;
    private byte[] imageArr = null;                //图片数据
    private String imageUrl = null;                //图片储存位置

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getTimestamp() {
        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timestamp = sdf.format(new Date());
        return timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNoteType() {
        return NoteType;
    }

    public void setNoteType(String noteType) {
        NoteType = noteType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getImageArr() {
        return imageArr;
    }

    public void setImageArr(byte[] imageArr) {
        this.imageArr = imageArr;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
