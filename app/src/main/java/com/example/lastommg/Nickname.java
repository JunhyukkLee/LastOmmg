package com.example.lastommg;

import android.app.Application;

public class Nickname extends Application {

    private String nickname;

    @Override
    public void onCreate() {
        nickname="";
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        //프로세스 소멸 시 호출
        super.onTerminate();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname=nickname;
    }
}