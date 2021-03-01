package com.fxj.NeptuneHostApp

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.RemoteException
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.qiyi.pluginlibrary.Neptune
import org.qiyi.pluginlibrary.install.IInstallCallBack
import org.qiyi.pluginlibrary.install.IUninstallCallBack
import org.qiyi.pluginlibrary.pm.PluginLiteInfo
import org.qiyi.pluginlibrary.utils.FileUtils
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object{
        val TAG=MainActivity::class.java.simpleName
        const val PLUGIN_PKG="com.fxj.NeptunePluginApp"
    }

    var tvPluginState: TextView? = null
    var btn01:Button?=null
    var btn02:Button?=null
    var btn03:Button?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.tvPluginState=findViewById(R.id.tv_plugin_state)
        this.btn01=findViewById(R.id.btn01)
        this.btn02=findViewById(R.id.btn02)
        this.btn03=findViewById(R.id.btn03)

        this.btn01?.setOnClickListener(this)
        this.btn02?.setOnClickListener(this)
        this.btn03?.setOnClickListener(this)

        updatePluginState()
    }

    private fun updatePluginState() {
        val installed = Neptune.isPackageInstalled(this, MainActivity.PLUGIN_PKG)
        tvPluginState!!.text = getString(R.string.plugin_state, if (installed) "已安装" else "未安装")
    }


    fun installPlugin() {
        if (Environment.MEDIA_MOUNTED != Environment.getExternalStorageState()) {
            Toast.makeText(this, "sdcard was NOT MOUNTED!", Toast.LENGTH_SHORT).show()
            return
        }
        val apkName = PLUGIN_PKG+".apk"
        Log.i(TAG, "install plugin from asset")
        val targetFile = File(filesDir, apkName)
        try {
            val `is` = assets.open("pluginapp/$apkName")
            FileUtils.copyToFile(`is`, targetFile)
            installPluginInternal(targetFile.absolutePath)
            return
        } catch (e: IOException) {
            Toast.makeText(this, "sample plugin not found in asset", Toast.LENGTH_SHORT).show()
        }
        Log.i(TAG, "install sample plugin from sdcard")
        val externalPluginApk = File(Environment.getExternalStorageDirectory(), apkName)
        if (externalPluginApk.exists()) {
            installPluginInternal(externalPluginApk.absolutePath)
        }
    }
    private fun installPluginInternal(apkPath: String) {
        Neptune.install(this, apkPath, object : IInstallCallBack.Stub() {
            @Throws(RemoteException::class)
            override fun onPackageInstalled(info: PluginLiteInfo) {
                val msg = "位于${apkPath}的插件安装成功!"
                Log.d(TAG, msg)
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                updatePluginState()
            }

            @Throws(RemoteException::class)
            override fun onPackageInstallFail(info: PluginLiteInfo, failReason: Int) {
                val msg = "位于${apkPath}的插件安装失败!"
                Log.d(TAG, msg)
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                updatePluginState()
            }
        })
    }

    private fun uninstallPlugin(pkgName: String){
        Neptune.uninstall(this, pkgName, object : IUninstallCallBack.Stub() {
            @Throws(RemoteException::class)
            override fun onPackageUninstalled(info: PluginLiteInfo, resultCode: Int) {
                var msg = "插件${pkgName}卸载成功"
                Log.d(TAG, msg)
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                updatePluginState()
            }
        })
    }
    /**
     * 启动插件
     * @param pkg 插件包名
     * @param cls 目标插件Activity，默认值为空则启动默认入口
     * */
    fun launchPlugin(pkg:String, cls:String="") {
        val intent = Intent()
        val cn = ComponentName(pkg, cls) // 启动默认入口Activity
        intent.component = cn
        if (Neptune.isPackageInstalled(this, pkg)) {
            Neptune.launchPlugin(this, intent)
        } else {
            var msg="插件${pkg}没有安装"
            Log.d(TAG,msg)
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }
    override fun onClick(v: View?) {
        var id=v?.id
        when(id){
            R.id.btn01 -> {
                Log.d(TAG, "安装插件按钮被点击了!")
                installPlugin()
            }
            R.id.btn02 -> {
                Log.d(TAG, "卸载插件按钮被点击了!")
                uninstallPlugin(PLUGIN_PKG)
            }
            R.id.btn03 -> {
                Log.d(TAG, "启动插件按钮被点击了!")
                launchPlugin(PLUGIN_PKG,"com.fxj.NeptunePluginApp.MainActivity")
            }
        }
    }
}