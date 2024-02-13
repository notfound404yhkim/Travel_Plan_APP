package com.example.travelapp.model;

import java.io.Serializable;

public class User implements Serializable {

    public String name;
    public String email;
    public String phone;
    public String password;
    public String profileImg;
    // 구글 로그인 구분 위한 변수
    public int type;

    public User(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // 구글 로그인을 이용한 회원가입을 위한 생성자
    public User(String name, String email, int type) {
        this.name = name;
        this.email = email;
        this.type = type;
    }
}