package com.smallcake.tbsdocrenderview

import android.app.Application
import com.smallcake.tbs.TbsRenderClient

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
       TbsRenderClient.init(this)
    }
}