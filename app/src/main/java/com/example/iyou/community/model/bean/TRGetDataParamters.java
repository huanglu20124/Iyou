package com.example.iyou.community.model.bean;

/**
 * Created by cyhaha on 2017/1/15.
 */

//请求参数类
public class TRGetDataParamters {
    //服务器判断请求类型，不用改
    private String type = "GetComment";

    //必填
    private String noteId;                  //帖子ID

    //可选
    private Integer limit = 20;             //单页返回的记录条数，默认20
    private Integer sinceId =  null;                //返回比sinceId大的数据

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getSinceId() {
        return sinceId;
    }

    public void setSinceId(Integer sinceId) {
        this.sinceId = sinceId;
    }

    public String getType() {
        return type;
    }
}
