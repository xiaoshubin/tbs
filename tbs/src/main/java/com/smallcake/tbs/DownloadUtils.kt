package com.smallcake.tbs

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import me.jessyan.progressmanager.ProgressListener
import me.jessyan.progressmanager.ProgressManager
import me.jessyan.progressmanager.body.ProgressInfo
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * 下载工具类
 */
@SuppressLint("UsingALog")
object DownloadUtils {

    private var okHttpClient: OkHttpClient? = null
    private var mHandler: Handler? = null          //所有监听器在 Handler 中被执行,所以可以保证所有监听器在主线程中被执行

    init {
        mHandler = Handler(Looper.getMainLooper())
        val okhttpBuilder = OkHttpClient().newBuilder()
        okhttpBuilder.connectTimeout(60,TimeUnit.SECONDS)
        okhttpBuilder.readTimeout(60,TimeUnit.SECONDS)
        okhttpBuilder.writeTimeout(60,TimeUnit.SECONDS)
        okHttpClient = ProgressManager.getInstance().with(okhttpBuilder).build()

    }


    interface OnDownloadListener {
        /**
         * 下载成功
         */
        fun onDownloadSuccess(downloadPath:String)

        /**
         * @param progress 下载进度
         */
        fun onDownloading(progress: ProgressInfo?)

        /**
         * 下载失败
         */
        fun onDownloadFailed()
    }

    fun download(url: String,listener: OnDownloadListener){
       val fileName =  getFileName(url)
        val saveDir = TbsRenderClient.appContext?.externalCacheDir?.path
        download(url,saveDir,fileName,listener)
    }
    /**
     * 获取文件名称
     * http://testing.cloudjoytech.com.cn:50011/upload/2021-09/04f700dcb0634d6e959887f02e10789d.pdf
     * 返回04f700dcb0634d6e959887f02e10789d.pdf
     * @param fileName String
     */
    fun getFileName(fileName: String):String{
        return fileName.substring(fileName.lastIndexOf("/") + 1)
    }
    /**
     * @param url       下载连接
     * @param saveDir   储存下载文件的SDCard目录
     * @param saveName  储存下载文件的名称
     * @param listener  下载监听
     */
    fun download(url: String, saveDir: String?, saveName: String?, listener: OnDownloadListener) {
        val request: Request = Request.Builder().url(url).build()
        okHttpClient?.newCall(request)?.enqueue(object : Callback {


            override fun onFailure(call: Call, e: IOException) {
                Log.e("下载失败", e.message?:"--")
                mHandler?.post { listener.onDownloadFailed() }
            }


            override fun onResponse(call: Call, response: Response) {
                // Okhttp/Retofit 下载监听
                var inputStream: InputStream? = null
                val buf = ByteArray(2048)
                var len = 0
                var fos: FileOutputStream? = null
                // 储存下载文件的目录
                try {
                    inputStream = response.body?.byteStream()
                    val file = File(saveDir, saveName)
                    if (!file.parentFile.exists()) file.parentFile.mkdirs()
                    fos = FileOutputStream(file)
                    while (inputStream?.read(buf).also { len = it!! } != -1) {
                        fos.write(buf, 0, len)
                    }
                    fos.flush()
                    mHandler?.post {
                        listener.onDownloadSuccess(saveDir+File.separator+saveName)
                    }
                } catch (e: Exception) {
                    Log.e("下载异常", e.message?:"--")
                    // 下载失败
                    mHandler?.post { listener.onDownloadFailed() }
                } finally {
                    try {
                        inputStream?.close()
                        fos?.close()
                    } catch (e: IOException) {
                    }
                }
            }


        })
        ProgressManager.getInstance().addResponseListener(url, object : ProgressListener {
            override fun onProgress(progressInfo: ProgressInfo?) {
                listener.onDownloading(progressInfo)
            }

            override fun onError(l: Long, e: Exception?) {
                listener.onDownloadFailed()
            }
        })
    }
}


