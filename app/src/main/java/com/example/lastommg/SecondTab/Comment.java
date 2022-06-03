package com.example.lastommg.SecondTab;

public class Comment {

    String comment;
    String name;
    String profile_img;

    public Comment(String comment, String name, String profile_img) {
        this.comment = comment;
        this.name = name;
        this.profile_img=profile_img;
    }

    public Comment() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setId(String name) {
        this.name=name;
    }
    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img=profile_img;
    }
}

