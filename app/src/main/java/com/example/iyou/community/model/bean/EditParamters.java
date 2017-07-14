package com.example.iyou.community.model.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cyhaha on 2017/2/6.
 */


//足迹评论上传参数类
public class EditParamters {

    private String type = "addComment";     //服务器识别请求类型
    private String account;                 //上传用户id
    private String timestamp;               //时间戳   (自己生成)
    private String commentType;             //帖子类型
    private String content;                 //评论内容
    private String noteId;                  //帖子id
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

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCommentType() {
        return commentType;
    }

    public void setCommentType(String commentType) {
        this.commentType = commentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
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
