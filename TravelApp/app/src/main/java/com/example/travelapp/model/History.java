package com.example.travelapp.model;

import java.io.Serializable;

public class History implements Serializable {


    public String region;
    public String strDate;
    public String endDate;
    public String firstDay;
    public String secondDay;
    public String thirdDay;
    public String fourthDay;
    public String fifthDay;

    public int id;
    public int option;

    public History(int id,int option){
        this.id = id;
        this.option = option;

    }

    public History(String firstDay,String secondDay, String thirdDay, String fourthDay, String fifthDay){
        this.firstDay = firstDay;
        this.secondDay = secondDay;
        this.thirdDay = thirdDay;
        this.fourthDay = fourthDay;
        this.fifthDay = fifthDay;
    }



    public History(String region, String strDate, String endDate) {
        this.region = region;
        this.strDate = strDate;
        this.endDate = endDate;
    }
}
