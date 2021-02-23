package com.fxj.NeptuneHostApp

import android.app.Application
import org.qiyi.pluginlibrary.BuildConfig
import org.qiyi.pluginlibrary.Neptune
import org.qiyi.pluginlibrary.NeptuneConfig

class BaseApplication :Application(){
    companion object{
        val TAG=BaseApplication::class.java.simpleName
    }

    override fun onCreate() {
        super.onCreate()

        val config: NeptuneConfig = NeptuneConfig.Builder()
            .configSdkMode(NeptuneConfig.INSTRUMENTATION_MODE)
            .enableDebug(BuildConfig.DEBUG)
            .build()
        Neptune.init(this, config)
    }

}