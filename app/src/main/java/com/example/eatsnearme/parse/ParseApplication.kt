package com.example.eatsnearme.parse

import android.app.Application
import com.parse.Parse
import com.parse.ParseObject

class ParseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        ParseObject.registerSubclass(SavedRestaurants::class.java)

        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId("1ClJnFcXZQGaE3RXAWASMJaf6ksAFfgBrDZHrvq7")
                .clientKey("fEYuZtMAi6meL2nP8heu0d4HIfACWxxxdLhiPYnK")
                .server("https://parseapi.back4app.com")
                .build()
        )
    }
}