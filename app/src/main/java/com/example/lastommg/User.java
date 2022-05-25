package com.example.lastommg;


import android.net.Uri;

public class User {


     String id;
    // Uri pro_uri;
    String username;
    String nickname;


    public User(String id, String username, String nickname) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
       // this.pro_uri=pro_uri;

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

    public void setNickname(String nickname) {
        this.nickname=nickname;
    }

    /*public Uri getPro_uri() {
        return pro_uri;
    }

    public void setPro_uri(Uri pro_uri) {
        this.pro_uri=pro_uri;
    }*/



}


