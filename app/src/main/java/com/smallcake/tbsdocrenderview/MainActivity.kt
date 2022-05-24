package com.smallcake.tbsdocrenderview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.smallcake.tbs.TbsDocRenderView

class MainActivity : AppCompatActivity() {
    private val mUrl = "http://www.cztouch.com/upfiles/soft/testpdf.pdf"
    private var tbsView:TbsDocRenderView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tbsView = findViewById<TbsDocRenderView>(R.id.tbs_view)
        tbsView?.showFile(mUrl)
    }

    override fun onDestroy() {
        super.onDestroy()
        tbsView?.onStop()
    }

}