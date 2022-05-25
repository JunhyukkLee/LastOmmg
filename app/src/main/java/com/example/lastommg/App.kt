package com.example.lastommg

import android.app.Application
import com.kakao.auth.KakaoSDK
class App : Application() {
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
    fun getAppContext() : App{
        checkNotNull(instance){
            "This Application does not inherit com.example.App"
        }
        return instance!!
    }
}

