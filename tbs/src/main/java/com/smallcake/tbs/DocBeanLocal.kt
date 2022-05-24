package com.smallcake.tbs

import org.litepal.crud.LitePalSupport

/**
 * 保存到本地的文件
 * @property url String?           文件的网络路径
 * @property downloadPath String?  下载到本地的路径
 */
data class DocBeanLocal(
    val url: String?,
    val downloadPath: String?,
): LitePalSupport()
