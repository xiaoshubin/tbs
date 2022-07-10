package com.smallcake.tbs

import android.app.Application
import android.util.Log
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.TbsDownloader
import com.tencent.smtt.sdk.TbsListener
import org.litepal.LitePal

object TbsRenderClient {
    var appContext:Application?=null

    fun init(application: Application){
        appContext = application
       // 腾讯tbs优化:在调用TBS初始化、创建WebView之前进行如下配置
        val map = HashMap<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)
        QbSdk.initX5Environment(application,object : QbSdk.PreInitCallback{
            override fun onCoreInitFinished() {
                Log.d("TabClient","Tbs黑心初始化完成")
            }
            override fun onViewInitFinished(b: Boolean) {
                Log.d("TabClient","视图初始化完成:$b")
                if (!b) TbsDownloader.startDownload(application)
            }
        })
        QbSdk.setTbsListener(object : TbsListener {
            override fun onDownloadFinish(i: Int) {
                //tbs内核下载完成回调
                Log.d("TabClient","tbs内核下载完成回调：$i")
            }

            override fun onInstallFinish(i: Int) {
                //内核安装完成回调，
                Log.d("TabClient","tbs内核安装完成回调:$i")
            }

            override fun onDownloadProgress(i: Int) {
                //下载进度监听
                Log.d("TabClient","tbs下载进度监听:$i")
            }
        })
        //数据库初始化
        LitePal.initialize(application)
    }
}