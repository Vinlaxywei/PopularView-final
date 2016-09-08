把The movie db 的api Key添加到gradle.properties文件即可（如果没有则需要新建此文件）
添加下面这一行代码，xxxxxxxxxxxx换成你的api key即可

`MyOpenMovieDbApiKey="xxxxxxxxxxxx"`

然后app/build.gradle文件添加下面这一段代码
```
buildTypes.each {
        it.buildConfigField 'String', 'THE_OPEN_MOVIE_DB_API_KEY', MyOpenMovieDbApiKey
    }
```
    
PopularMovie 查看最新热播电影
=====================

一点基础应用信息
--------
- targetSdkVersion 24
- minSdkVersion 15
- Android Build Tools v24.0.1
- Android Support Repository v23.3.0

扩展库
---
- compile 'com.squareup.picasso:picasso:2.5.2'
- compile 'com.android.support:cardview-v7:21.0.+'

应用演示
----
- Google+ Community: https://plus.google.com/communities/105153134372062985968
- Stack Overflow: http://stackoverflow.com/questions/tagged/android


