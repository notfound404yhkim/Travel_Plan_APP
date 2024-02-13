package com.example.travelapp.model;

import java.io.Serializable;

public class Posting implements Serializable {

    public int id;
    public int userId;
    public String imgUrl;
    public String title;
    public String content;
    public String createdAt;
    public String updatedAt;
    public int isLike;
    public int likeCnt;

    public int postingId;
    public String name;
    public int bookmarkCnt;
    public int isBookmark;

    public Posting(int id, int userId, String imgUrl, String title, String content, String createdAt, int isLike, int likeCnt) {
        this.id = id;
        this.userId = userId;
        this.imgUrl = imgUrl;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isLike = isLike;
        this.likeCnt = likeCnt;
    }
}