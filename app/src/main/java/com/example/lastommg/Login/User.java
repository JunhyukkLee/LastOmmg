package com.example.lastommg.Login;


import android.net.Uri;

public class User {

    private String id;
    private String username;
    private String nickname;
    private String pro_img;
    private String intro;

    public User(String id, String intro,String username, String nickname,String pro_img) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.pro_img=pro_img;
        this.intro=intro;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname() {
        this.nickname=nickname;
    }

    public String getPro_img() {
        return pro_img;
    }

    public void setPro_img() {
        this.pro_img=pro_img;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro() {
        this.intro=intro;
    }




}


