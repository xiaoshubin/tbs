package com.smallcake.tbs

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.tencent.smtt.sdk.TbsReaderView
import me.jessyan.progressmanager.body.ProgressInfo
import org.litepal.LitePal
import java.io.File

/**
 * 封装tbs显示 doc、docx、ppt、pptx、xls、xlsx、pdf、txt、epub等文件
 * 官方文档：https://x5.tencent.com/docs/index.html
 * 参考：https://github.com/ZhongXiaoHong/superFileView
 *
 * 注意：在页面关闭或pop关闭时需要调用onStop方法
 * 注意：onViewInitFinished返回false需要重新开启下载TbsDownloader.startDownload(application)
 */
@SuppressLint("UsingALog")
class TbsDocRenderView: FrameLayout {
    private  var mTbsReaderView: TbsReaderView? = null
    companion object {
        private const val TAG = "SuperFileView"
    }

    private fun getTbsReaderView(context: Context): TbsReaderView {
        return TbsReaderView(context, null)
    }

    constructor(@NonNull context: Context,):super(context)
    constructor(@NonNull context: Context,@Nullable attrs: AttributeSet?):super(context,attrs)
    constructor(@NonNull context: Context,@Nullable attrs: AttributeSet?, defStyleAttr: Int = 0):super(context,attrs,defStyleAttr)

    /**
     * 显示网络doc,pdf,xsl文件
     * @param url String?
     */
    fun showFile(url: String?) {
        downloadFile(url)
    }
    private fun downloadFile(url:String?){
        if (TextUtils.isEmpty(url))return
        //如果已经有此记录了就直接加载
        val docBeanLocal = LitePal.findAll(DocBeanLocal::class.java).find { it.url==url }
        if (docBeanLocal!=null){
            showFile(File(docBeanLocal.downloadPath))
            return
        }
        DownloadUtils.download(url!!, object : DownloadUtils.OnDownloadListener {
            override fun onDownloadSuccess(downloadPath: String) {
                val downDoc = DocBeanLocal(url,downloadPath)
                downDoc.saveOrUpdate("url = ?",url)
                Log.e("TAG", "下载成功的路径：$downloadPath")
                showFile(File(downloadPath))
            }
            override fun onDownloading(progress: ProgressInfo?) {
                Log.e("TAG", "进度：" + progress?.percent + "%")
            }
            override fun onDownloadFailed() {
            }
        })
    }

    /**
     * 加载文件
     * @param mFile File?
     */
    fun showFile(mFile: File?) {
        mTbsReaderView = TbsReaderView(context, null)
        this.addView(mTbsReaderView, LinearLayout.LayoutParams(-1, -1))
        if (mFile != null && !TextUtils.isEmpty(mFile.toString())) {
            //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
            val bsReaderTemp = "/storage/emulated/0/TbsReaderTemp"
            val bsReaderTempFile = File(bsReaderTemp)
            if (!bsReaderTempFile.exists()) {
                Log.d(TAG,"准备创建/storage/emulated/0/TbsReaderTemp！！")
                val mkdir = bsReaderTempFile.mkdir()
                if (!mkdir) {
                    Log.e(TAG,"创建/storage/emulated/0/TbsReaderTemp失败！！！！！")
                }
            }
            //加载文件
            val localBundle = Bundle()
            Log.d(TAG,mFile.toString())
            localBundle.putString("filePath", mFile.toString())
            localBundle.putString("tempPath",Environment.getExternalStorageDirectory().toString() + "/" + "TbsReaderTemp")
            if (mTbsReaderView == null) mTbsReaderView = getTbsReaderView(context)
            val bool = try {
                mTbsReaderView?.preOpen(getFileType(mFile.toString()), false)
            } catch (e: Exception) {
                //同一页面多次调用会导致崩溃
                e.printStackTrace()
            }
            if (bool==true) {
                //延迟加载，避免白屏
                Handler().postDelayed({ mTbsReaderView?.openFile(localBundle)},300)
            }
        } else {
           Log.e(TAG,"文件路径无效！")
        }
    }

    /**
     * 停止显示，在页面关闭或pop隐藏时调用
     */
    fun onStop() {
        mTbsReaderView?.onStop()
        mTbsReaderView?.removeAllViews()
        this.removeView(mTbsReaderView)
        mTbsReaderView==null

    }

    /***
     * 获取文件类型
     */
    private fun getFileType(paramString: String): String {
        var str = ""
        if (TextUtils.isEmpty(paramString)) return str
        val i = paramString.lastIndexOf('.')
        if (i <= -1)return str
        str = paramString.substring(i + 1)
//        Log.d(TAG,"文件类型:$str")
        return str
    }
}