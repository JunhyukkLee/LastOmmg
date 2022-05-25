package com.example.lastommg;

public class Comment {

    String comment;
    String name;

    public Comment(String comment, String name) {
        this.comment = comment;
        this.name = name;
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
}

