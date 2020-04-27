# Android微信支付集成，准备工作

### 1.微信开放平台 注册，获取APP ID等信息

   应用签名的获取:

### 2.Android端集成

  - 在app build.gradle下添加以下依赖：
  
  ```
    implementation 'com.tencent.mm.opensdk:wechat-sdk-android-with-mta:+'
  ```

  - 接收微信回调需要一个WXPayEntryActivity，目录如下图，推荐直接拷贝项目demo中的类，
    这里需要注意WXPayEntryActivity 路径必须为：绑定的商户应用包名 + wxapi + WXPayEntryActivity，
    比如：我在微信开发平台注册包名为：com.example.wx ，

    点击查看WXPayEntryActivity

