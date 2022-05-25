package com.example.lastommg;


public class User {

    private String id;
    private String username;
    private String nickname;
    private String phonenumber;

    public User(String id, String username, String nickname,String phonenumber) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.phonenumber=phonenumber;
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

    public String getPhonenumber(String phonenumber) {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }


}


