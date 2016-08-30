把The movie db 的api Key添加到gradle.properties文件即可（需要新建此文件）
添加下面这一行代码，xxxxxxxxxxxx换成你的api key即可

`MyApiKey="xxxxxxxxxxxx"`

然后app/build.gradle文件添加下面这一段代码
```
buildTypes.each {
        it.buildConfigField 'String', 'TheMovieDb_Key', MyApiKey
    }
```
    
@Walker
