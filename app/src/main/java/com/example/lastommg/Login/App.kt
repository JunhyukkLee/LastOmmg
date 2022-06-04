package com.example.lastommg.Login

import android.app.Application
import com.kakao.auth.KakaoSDK
class App : Application() {

    private var nickname: String? = null
    private var pro_img: String?=null
    private var email: String?=null
    private var intro: String?=null
    private var uid: String?=null
    private var name: String?=null

    companion object{
        var instance : App? = null

    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        if(KakaoSDK.getAdapter() == null){
            KakaoSDK.init(KakaoSDKAdapter(getAppContext()))
        }

    }
    override fun onTerminate()
    { super.onTerminate()
        instance = null
    }
    fun getAppContext() : App {
        checkNotNull(instance){
            "This Application does not inherit com.example.App"
        }
        return instance!!
    }
    fun getNickname(): String? {
        return nickname
    }

    fun setNickname(nickname: String?) {
        this.nickname=nickname;

    }
    fun getUid(): String? {
        return uid
    }

    fun setUid(uid: String?) {
        this.uid=uid

    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name=name
    }

    fun getPro_img(): String? {
        return pro_img;
    }

    fun setPro_img(pro_img: String?) {
        this.pro_img=pro_img;

    }
    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?) {
        this.email=email;

    }
    fun getIntro(): String? {
        return intro
    }

    fun setIntro(intro: String?) {
        this.intro=intro;

    }


}

