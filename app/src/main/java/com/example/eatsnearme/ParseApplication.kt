package com.example.eatsnearme

import android.app.Application
import com.parse.Parse

class ParseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId("1ClJnFcXZQGaE3RXAWASMJaf6ksAFfgBrDZHrvq7")
                .clientKey("fEYuZtMAi6meL2nP8heu0d4HIfACWxxxdLhiPYnK")
                .server("https://parseapi.back4app.com")
                .build()
        )
    }
}