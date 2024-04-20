package com.example.prashantadvaitdemo

import android.app.Application
import com.unsplash.pickerandroid.photopicker.UnsplashPhotoPicker

class MainApplication : Application(){
    private val UNSPLASH_SECRET_KEY = "saMXv6bapnhs9brLklu1ugze0PRvs2C_Tg-sugaXPuo"
    private val UNSPLASH_ACCESS_KEY ="ETvwFT7sqWw_S9Nb-NQDur3rItWfENgiapuoe50rSrE"

    override fun onCreate() {
        super.onCreate()
        UnsplashPhotoPicker.init(
            this,
            UNSPLASH_ACCESS_KEY,
            UNSPLASH_SECRET_KEY
        )
    }
}