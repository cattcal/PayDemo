# Android微信支付集成，准备工作

### 1. 通过 微信开放平台 注册，获取APP ID等信息

   应用签名的获取:

### 2. Android端集成

  - 在app build.gradle下添加以下依赖：
  
        implementation 'com.tencent.mm.opensdk:wechat-sdk-android-with-mta:+'

  - 接收微信回调我们需要一个WXPayEntryActivity，目录如下图，推荐直接拷贝项目demo中的类，
    这里需要注意WXPayEntryActivity 路径必须为：绑定的商户应用包名 + wxapi + WXPayEntryActivity，
    比如：我在微信开发平台注册包名为："cn.hujw.paydemo"

    [点击查看WXPayEntryActivity.](https://github.com/cattcal/PayDemo/blob/master/app/src/main/java/cn/hujw/paydemo/wxapi/WXPayEntryActivity.java)
    
  - 权限声明，我们需要在AndroidManifest.xml中添加权限
  
  ```
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
  ```
  - 声明WXPayEntryActivity，我们需要在AndroidManifest.xml中添加如下:

  ```
   <activity
       android:name=".wxapi.WXPayEntryActivity"
       android:exported="true"
       android:launchMode="singleTop"/>
  ```
  
  - 在我们调用API之前，我们需要向微信注册您的APPID，注册微信支付App Id，有两种方式分别如下：
    
    1. 在AndroidManifest.xml注册
    
   ```
    <activity
          android:name=".wxapi.WXPayEntryActivity"
          android:exported="true"
          android:launchMode="singleTop">

          <intent-filter>
              <action android:name="android.intent.action.VIEW" />

              <category android:name="android.intent.category.DEFAULT" />

               !-- 第一种方式 注册 这里填写你申请的app id
              第二种方式在应用入口Application，app id注册到微信*-->
              <data android:scheme="app id" />
          </intent-filter>
        </activity>
   ```
   2. 在应用Application，APP ID注册到微信
   
   ```
       public class MyApplication extends Application {
    
        private static Context mContext;
    
        @Override
        public void onCreate() {
            super.onCreate();
            mContext = getApplicationContext();
    
            //第二种方式注册
            initSdk();
        }
    
        private void initSdk() {
            //注册微信
            IWXAPI msgApi = WXAPIFactory.createWXAPI(mContext, Constant.WX_PAY_APP_ID);
            // 将该app id 注册到微信   AppID: 申请到的AppID
            msgApi.registerApp(Constant.WX_PAY_APP_ID);
    
        }
    
        public static Context getContext() {
            return mContext;
        }
    
    }
   ```
   
# Android 微信支付流程

  - Android 与 服务端 交互流程如下图所示：
  
  - 我们的Android APP调起微信所需要的参数如下图所示（参数大多是服务端订单接口返回的）：
  
    当调用接口成功，服务端返回数据之后，调启微信支付的代码如下所示:
    
   ```
   /**
     * 微信支付
     * <p>
     * 注意： 每次调用微信支付的时候都会校验 appid 、包名 和 应用签名的。 这三个必须保持一致才能够成功调起微信
     *
     * @param wxPayBean
     */
    public void wechatPay(WxPayBean wxPayBean) {

        //这里的appid，替换成自己的即可
        IWXAPI api = WXAPIFactory.createWXAPI(activity, Constant.WX_PAY_APP_ID);
        api.registerApp(Constant.WX_PAY_APP_ID);

        //这里的bean，是服务器返回的json生成的bean
        PayReq payRequest = new PayReq();
        payRequest.appId = Constant.WX_PAY_APP_ID;
        payRequest.partnerId = wxPayBean.getPartnerid();
        payRequest.prepayId = wxPayBean.getPrepayid();
        payRequest.packageValue = "Sign=WXPay";//固定值
        payRequest.nonceStr = wxPayBean.getNoncestr();
        payRequest.timeStamp = wxPayBean.getTimestamp();
        payRequest.sign = wxPayBean.getSign();

        //发起请求，调起微信前去支付
        api.sendReq(payRequest);
    }
   ```
   - 编写WxPayBean实体类(请求参数的实体类, [点击查看WxPayBean.](https://github.com/cattcal/PayDemo/blob/master/app/src/main/java/cn/hujw/paydemo/bean/WxPayBean.java))如下所示:
   
   ```
   public class WxPayBean {
    private String appid; //微信开放平台审核通过的应用APPID
    private String partnerid; //微信支付分配的商户号
    private String prepayid; //微信返回的支付交易会话ID
    //        private String package;// 扩展字段 暂填写固定值Sign=WXPay
    private String noncestr; //随机字符串,随机字符串，不长于32位。推荐随机数生成算法
    private String sign; //签名，详见签名生成算法注意：签名方式一定要与统一下单接口使用的一致
    private String timestamp; //时间戳，请见接口规则-参数规定
   
    ...
    
}
   ```
   
   - 微信接口回调在 {@link WXPayEntryActivity#onResp(BaseResp)} 中，具体请查看[点击查看WXPayEntryActivity.](https://github.com/cattcal/PayDemo/blob/master/app/src/main/java/cn/hujw/paydemo/wxapi/WXPayEntryActivity.java)
   
   ```
     /**
     * 处理结果的回调
     *
     * @param resp
     */
    @Override
    public void onResp(BaseResp resp) {
        Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

            switch (resp.errCode) {
                case 0://支付成功
                    Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResp: resp.errCode = 0   支付成功");
                    break;
                case -1://错误，可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等
                    Toast.makeText(this, "支付错误" + resp.errCode, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResp: resp.errCode = -1  支付错误");
                    break;
                case -2://用户取消，无需处理。发生场景：用户不支付了，点击取消，返回APP。
                    Log.d(TAG, "onResp: resp.errCode = -2  用户取消");
                    Toast.makeText(this, "用户取消" + resp.errCode, Toast.LENGTH_SHORT).show();
                    break;

            }

            finish();//这里需要关闭该页面
        }
    }
   ```
   
   # 注意
   
   1. 支付不回调问题
      
      如果获取不到支付回调，请检查一下WXPayEntryActivity放置的路径；微信支付必须要求该类的路径为：包名.wxapi.WXPayEntryActivity，不然不能回调。
  
   2. 申请App Id，应用签名的获取
     
      如果接入有问题，请使用排查一下是否申请app id的时候，应用签名是否有误，推荐使用微信应用签名获取apk 来校验，填入自己应用包名即可查看应用签名。
   
   3. 在微信后台申请完app以后，注意监测是否获得微信支付的能力，需要单独申请
   
      微信支付中，如果报appid和mech_id(微信支付分配的商户号)的错，应该就是要支持支付功能的App的appid与商户平台的商户id不对应造成的。
      
      如图：
      
  检测是否安装微信App代码如下：
   
   ```
     /**
     * 判断微信是否安装
     * @param context
     * @return true 已安装   false 未安装
     */
    public  static boolean isWxAppInstalled(Context context) {
        IWXAPI wxApi = WXAPIFactory.createWXAPI(context, null);
        wxApi.registerApp(Constant.WX_PAY_APP_ID);
        boolean bIsWXAppInstalled = false;
        bIsWXAppInstalled = wxApi.isWXAppInstalled();
        return bIsWXAppInstalled;
    }
   ```   