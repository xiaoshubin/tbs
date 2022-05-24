# 腾讯TBS文件查看器

- 应用内展示word、excel、pdf、ppt等文件

TBS Android SDK
大小：566K
版本：44181
更新日期：2022-4-12
说明：优化内核下载流程
[TBS SDK 近期热点问题汇总](https://docs.qq.com/doc/DYW9QdXJNWFZnbVdz)
------



### 引入

Step1 仓库地址新增

```
allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
            maven { url 'https://maven.aliyun.com/repository/jcenter'}
        }
    }
```

Step2 项目build.gradle加入
```
implementation 'com.github.xiaoshubin:tbs:1.0.0'
```

### 初始化

在项目的MyApplication中初始化

```
TbsRenderClient.init(this)
```

### 使用

xml中

```
<com.smallcake.tbs.TbsDocRenderView  
     android:id="@+id/tbs_view"  
     android:layout_width="match_parent"  
     android:layout_height="match_parent"/>
```

页面中

```
tbsView?.showFile(mUrl)
```

注意：在页面关闭时调用`onStop()`

```
override fun onDestroy() {
    super.onDestroy()
    tbsView?.onStop()
}
```
