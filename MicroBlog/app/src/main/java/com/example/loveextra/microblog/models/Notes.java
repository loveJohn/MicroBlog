package com.example.loveextra.microblog.models;

/**
 * Created by LoveExtra on 2016/6/22.
 */
public class Notes {
    private int noId;
    private String noContent;
    private String noDateStamp;

    public Notes() {
    }

    public Notes(int noId, String noContent, String noDateStamp) {
        this.noId = noId;
        this.noContent = noContent;
        this.noDateStamp = noDateStamp;
    }


    public int getNoId() {
        return noId;
    }

    public void setNoId(int noId) {
        this.noId = noId;
    }

    public String getNoContent() {
        return noContent;
    }

    public void setNoContent(String noContent) {
        this.noContent = noContent;
    }

    public String getNoDateStamp() {
        return noDateStamp;
    }

    public void setNoDateStamp(String noDateStamp) {
        this.noDateStamp = noDateStamp;
    }
}
