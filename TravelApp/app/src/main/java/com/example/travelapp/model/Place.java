package com.example.travelapp.model;

import java.io.Serializable;

public class Place implements Serializable {

    public int option;
    public int id;
    public int userId;
    public String region;
    public String placeName;

    public String name;

    public String content;
    public String imgUrl;
    public String strDate;
    public String endDate;


    public Place(int id, String imgUrl) {
        this.id = id;
        this.imgUrl = imgUrl;
    }

    public Place(int option, String region, String placeName, String content, String imgUrl) {
        this.option = option;
        this.region = region;
        this.placeName = placeName;
        this.content = content;
        this.imgUrl = imgUrl;
    }


    public Place(int id, String name, String imgUrl) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
    }
}
