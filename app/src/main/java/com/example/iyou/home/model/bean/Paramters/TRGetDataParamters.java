package com.example.iyou.home.model.bean.Paramters;

/**
 * Created by cyhaha on 2017/1/12.
 */

public class TRGetDataParamters {
    private String type = "queryScenery";
    private String keyWord;                 //搜索关键字
    private String keyWordType;             //关键字类型

    //可选
    private Integer limit = 20;             //单页返回的记录条数，默认20
    private Integer sinceId;                //返回比sinceId大的数据


    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getKeyWordType() {
        return keyWordType;
    }

    public void setKeyWordType(String keyWordType) {
        this.keyWordType = keyWordType;
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

    public void setType(String type) {
        this.type = type;
    }
}
