package com.fxj.NeptunePluginApp

import android.app.Application
import android.util.Log

class BaseApplication:Application() {
    companion object{
        val TAG=BaseApplication::class.java.simpleName
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"##onCreate##")
    }
}