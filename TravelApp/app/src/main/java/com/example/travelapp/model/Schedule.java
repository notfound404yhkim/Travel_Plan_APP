package com.example.travelapp.model;

import java.io.Serializable;

public class Schedule  {
    public int id;
    public int userId;

    public int[] placeId;

    public String region;
    public String strDate;
    public String endDate;

    public String content;
    public String createdAt;
    public String imgUrl;


    public Schedule(String region, String strDate, String endDate,String content) {
        this.region = region;
        this.strDate = strDate;
        this.endDate = endDate;
        this.content = content;
    }

    public Schedule(String region, String strDate, String endDate,String content,int[] placeId) {
        this.region = region;
        this.strDate = strDate;
        this.endDate = endDate;
        this.content = content;
        this.placeId = placeId;
    }

    public Schedule(int id, int userId, String region, String strDate, String endDate, String content, String createdAt, String imgUrl) {
        this.id = id;
        this.userId = userId;
        this.region = region;
        this.strDate = strDate;
        this.endDate = endDate;
        this.content = content;
        this.createdAt = createdAt;
        this.imgUrl = imgUrl;
    }

    public Schedule(int id, int userId, String region, String strDate, String endDate, String content, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.region = region;
        this.strDate = strDate;
        this.endDate = endDate;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Getter 메서드를 추가하여 "T00:00:00" 이후의 부분을 제거한 날짜를 반환합니다.
    public String getFormattedStartDate() {
        if (strDate != null && strDate.contains("T")) {
            return strDate.split("T")[0];
        }
        return strDate;
    }

    // Getter 메서드를 추가하여 "T00:00:00" 이후의 부분을 제거한 날짜를 반환합니다.
    public String getFormattedEndDate() {
        if (endDate != null && endDate.contains("T")) {
            return endDate.split("T")[0];
        }
        return endDate;
    }

    // Setter 메서드를 추가하여 날짜를 설정할 수 있습니다.
    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    // Setter 메서드를 추가하여 날짜를 설정할 수 있습니다.
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}