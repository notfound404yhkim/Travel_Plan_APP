package com.example.travelapp.model;

import java.io.Serializable;

public class User implements Serializable {

    public String name;
    public String email;
    public String phone;
    public String password;

    public String profileImg;

    public User(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public User(String name, String email, String phone, String password, String profileImg) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.profileImg = profileImg;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }



}